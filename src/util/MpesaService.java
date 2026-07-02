package util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.json.JSONObject;

public class MpesaService {

    // ── Step 1: Get Access Token ──────────────────────────────────────────────

    public String getAccessToken() throws Exception {
        String credentials = MpesaConfig.CONSUMER_KEY + ":" + MpesaConfig.CONSUMER_SECRET;
        String encoded     = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        URL url = new URL(MpesaConfig.AUTH_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Basic " + encoded);
        conn.setRequestProperty("Content-Type", "application/json");

        int responseCode = conn.getResponseCode();
        String response  = readResponse(conn);

        if (responseCode != 200)
            throw new Exception("Failed to get access token. Response: " + response);

        JSONObject json = new JSONObject(response);
        return json.getString("access_token");
    }

    // ── Step 2: Initiate STK Push ─────────────────────────────────────────────

    public String initiateSTKPush(String phoneNumber, double amount, String studentId) throws Exception {
        String accessToken = getAccessToken();
        String timestamp   = generateTimestamp();
        String password    = generatePassword(timestamp);

        JSONObject body = new JSONObject();
        body.put("BusinessShortCode", MpesaConfig.SHORTCODE);
        body.put("Password",          password);
        body.put("Timestamp",         timestamp);
        body.put("TransactionType",   "CustomerPayBillOnline");
        body.put("Amount",            (int) amount);
        body.put("PartyA",            phoneNumber);
        body.put("PartyB",            MpesaConfig.SHORTCODE);
        body.put("PhoneNumber",       phoneNumber);
        body.put("CallBackURL",       MpesaConfig.CALLBACK_URL);
        body.put("AccountReference",  studentId);
        body.put("TransactionDesc",   MpesaConfig.TRANSACTION_DESC);

        URL url = new URL(MpesaConfig.STK_PUSH_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        String response  = readResponse(conn);

        System.out.println("STK Push Response (" + responseCode + "): " + response);

        if (responseCode != 200)
            throw new Exception("STK Push failed: " + response);

        JSONObject json = new JSONObject(response);

        if (!"0".equals(json.optString("ResponseCode")))
            throw new Exception("STK Push rejected: " + json.optString("ResponseDescription"));

        String checkoutRequestId = json.getString("CheckoutRequestID");

        // ── Store CheckoutRequestID → studentId mapping ───────────────────────
        // Sandbox doesn't always return AccountReference in callback,
        // so we keep our own map to look up the student when callback arrives.
        MpesaCallbackServer.pendingPayments.put(checkoutRequestId, studentId);
        System.out.println("Stored pending payment: " + checkoutRequestId + " → studentId: " + studentId);

        return checkoutRequestId;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String generateTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private String generatePassword(String timestamp) {
        String raw = MpesaConfig.SHORTCODE + MpesaConfig.PASSKEY + timestamp;
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        InputStream stream;
        try {
            stream = conn.getInputStream();
        } catch (IOException e) {
            stream = conn.getErrorStream();
        }

        if (stream == null) return "";

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
}