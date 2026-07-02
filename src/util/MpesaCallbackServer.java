package util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dao.PaymentDao;
import model.Payment;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MpesaCallbackServer {

    private static final int PORT = 8080;
    private static HttpServer server;

    /**
     * Maps CheckoutRequestID → studentId.
     * Populated by MpesaService when STK push is initiated.
     * Used here to identify which student made the payment since
     * sandbox doesn't always return AccountReference in the callback.
     */
    public static final Map<String, String> pendingPayments = new ConcurrentHashMap<>();

    public static void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/mpesa/callback", new CallbackHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("✅ M-Pesa callback server started on port " + PORT);
        } catch (IOException e) {
            System.out.println("❌ Failed to start callback server: " + e.getMessage());
        }
    }

    public static void stop() {
        if (server != null) server.stop(0);
    }

    // ── Callback Handler ──────────────────────────────────────────────────────

    static class CallbackHandler implements HttpHandler {

        private final PaymentDao paymentDao = new PaymentDao();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );
            System.out.println("📩 M-Pesa Callback received: " + body);

            // Always respond 200 immediately — Daraja expects a quick response
            String response = "{\"ResultCode\":\"00000\",\"ResultDesc\":\"Success\"}";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }

            // Process in background so we don't block the HTTP response
            new Thread(() -> processCallback(body)).start();
        }

        private void processCallback(String body) {
            try {
                JSONObject root        = new JSONObject(body);
                JSONObject stkCallback = root
                    .getJSONObject("Body")
                    .getJSONObject("stkCallback");

                int resultCode = stkCallback.getInt("ResultCode");

                if (resultCode != 0) {
                    System.out.println("❌ Payment failed/cancelled. ResultCode: " + resultCode
                        + " - " + stkCallback.getString("ResultDesc"));
                    return;
                }

                // ── Extract payment details ───────────────────────────────────
                JSONObject metadata = stkCallback.getJSONObject("CallbackMetadata");
                var items = metadata.getJSONArray("Item");

                double amount        = 0;
                String receiptNumber = "";
                String phoneNumber   = "";

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    if (!item.has("Value")) continue; // skip items with no value e.g. Balance

                    switch (item.getString("Name")) {
                        case "Amount"             -> amount        = item.getDouble("Value");
                        case "MpesaReceiptNumber" -> receiptNumber = item.getString("Value");
                        case "PhoneNumber"        -> phoneNumber   = String.valueOf(item.get("Value")); // handle Long or String
                    }
                }

                // ── Look up student from our pending map ──────────────────────
                String checkoutRequestId = stkCallback.optString("CheckoutRequestID", "");
                String studentId         = pendingPayments.getOrDefault(checkoutRequestId, "");
                pendingPayments.remove(checkoutRequestId); // clean up

                System.out.println("✅ Payment confirmed!");
                System.out.println("   Amount    : KSh " + amount);
                System.out.println("   Receipt   : " + receiptNumber);
                System.out.println("   Phone     : " + phoneNumber);
                System.out.println("   Student ID: " + studentId);

                if (studentId.isEmpty()) {
                    System.out.println("⚠  Could not identify student — payment not saved.");
                    return;
                }

                // ── Save payment to DB ────────────────────────────────────────
                Payment payment = new Payment(
                    "MPE-" + receiptNumber,
                    studentId,
                    amount,
                    LocalDate.now(),
                    "Paid",
                    "M-Pesa"
                );

                boolean saved = paymentDao.addPayment(payment);
                if (saved) {
                    System.out.println("✅ Payment saved to DB. Student QR pass will now show ALLOWED.");
                } else {
                    System.out.println("❌ Payment confirmed but failed to save to DB.");
                }

            } catch (Exception e) {
                System.out.println("❌ Error processing callback: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}