package view.students;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import model.Student;
import util.MpesaConfig;
import util.MpesaService;

public class MpesaPaymentDialog {

    private final Student      student;
    private final MpesaService mpesaService = new MpesaService();

    public MpesaPaymentDialog(Student student) {
        this.student = student;
    }

    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Pay Hostel Fee via M-Pesa");
        stage.setResizable(false);

        VBox root = new VBox(18);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");
        root.setPrefWidth(380);

        // ── Title ─────────────────────────────────────────────────────────────
        Label title = new Label("💳 M-Pesa Payment");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));

        Label infoLabel = new Label("Hostel Fee Payment — " + getCurrentSemesterLabel());
        infoLabel.setFont(Font.font("Arial", 12));
        infoLabel.setTextFill(Color.GRAY);

        // ── Amount field ──────────────────────────────────────────────────────
        Label amountLabel = new Label("Amount (KSh)");
        amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        amountLabel.setTextFill(Color.web("#2c3e50"));

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount e.g. 10000");
        amountField.setPrefHeight(40);
        amountField.setStyle(fieldStyle());

        Label amountHint = new Label("ℹ  Minimum amount to unlock access: KSh " +
                                     String.format("%,.0f", MpesaConfig.HOSTEL_FEE));
        amountHint.setFont(Font.font("Arial", 11));
        amountHint.setTextFill(Color.GRAY);

        // ── Phone number field ────────────────────────────────────────────────
        Label phoneLabel = new Label("M-Pesa Phone Number");
        phoneLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        phoneLabel.setTextFill(Color.web("#2c3e50"));

        TextField phoneField = new TextField();
        phoneField.setPromptText("e.g. 0712345678");
        phoneField.setPrefHeight(40);
        phoneField.setStyle(fieldStyle());

        // Pre-fill with student's phone if available
        if (student.getPhone() != null && !student.getPhone().isEmpty()) {
            phoneField.setText(student.getPhone());
        }

        Label phoneHint = new Label("ℹ  A payment prompt will be sent to this number");
        phoneHint.setFont(Font.font("Arial", 11));
        phoneHint.setTextFill(Color.GRAY);

        // ── Status label ──────────────────────────────────────────────────────
        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(320);
        statusLabel.setMinHeight(20);

        // ── Buttons ───────────────────────────────────────────────────────────
        Button payBtn = new Button("Send Payment Request");
        payBtn.setMaxWidth(Double.MAX_VALUE);
        payBtn.setPrefHeight(42);
        payBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        payBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setPrefHeight(38);
        cancelBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> stage.close());

        payBtn.setOnAction(e -> {
            // Validate amount
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                setStatus(statusLabel, "❌ Please enter the amount.", true);
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amountText.replaceAll(",", ""));
                if (amount <= 0) {
                    setStatus(statusLabel, "❌ Amount must be greater than 0.", true);
                    return;
                }
            } catch (NumberFormatException ex) {
                setStatus(statusLabel, "❌ Invalid amount. Enter a number e.g. 10000", true);
                return;
            }

            // Validate phone
            String phone = formatPhone(phoneField.getText().trim());
            if (phone == null) {
                setStatus(statusLabel, "❌ Invalid phone number. Use format 07XXXXXXXX", true);
                return;
            }

            // Disable button and show loading
            payBtn.setDisable(true);
            payBtn.setText("Sending request...");
            setStatus(statusLabel, "⏳ Sending KSh " + String.format("%,.0f", amount) +
                      " request to " + phone + "...", false);

            final double finalAmount = amount;

            // Run in background thread
            new Thread(() -> {
                try {
                    String checkoutId = mpesaService.initiateSTKPush(
                        phone,
                        finalAmount,
                        student.getId()
                    );

                    Platform.runLater(() -> {
                        setStatus(statusLabel,
                            "✅ Payment prompt sent to your phone!\n" +
                            "Enter your M-Pesa PIN to complete payment.\n" +
                            "Your pass will update once confirmed.", false);
                        payBtn.setText("Request Sent ✓");
                        System.out.println("CheckoutRequestID: " + checkoutId);
                    });

                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        setStatus(statusLabel, "❌ Failed: " + ex.getMessage(), true);
                        payBtn.setDisable(false);
                        payBtn.setText("Send Payment Request");
                    });
                }
            }).start();
        });

        root.getChildren().addAll(
            title, infoLabel,
            amountLabel, amountField, amountHint,
            phoneLabel, phoneField, phoneHint,
            statusLabel, payBtn, cancelBtn
        );

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String getCurrentSemesterLabel() {
        int month = java.time.LocalDate.now().getMonthValue();
        int year  = java.time.LocalDate.now().getYear();
        return (month <= 6) ? "Semester 1 - " + year : "Semester 2 - " + year;
    }

    private String fieldStyle() {
        return "-fx-padding: 8; -fx-background-radius: 6; -fx-border-radius: 6; " +
               "-fx-border-color: #dfe6e9; -fx-background-color: white; -fx-font-size: 13;";
    }

    private String formatPhone(String phone) {
        if (phone == null || phone.isEmpty()) return null;
        phone = phone.replaceAll("\\s+", "").replaceAll("\\+", "");
        if (phone.startsWith("07") && phone.length() == 10)  return "254" + phone.substring(1);
        if (phone.startsWith("01") && phone.length() == 10)  return "254" + phone.substring(1);
        if (phone.startsWith("2547") && phone.length() == 12) return phone;
        if (phone.startsWith("2541") && phone.length() == 12) return phone;
        return null;
    }

    private void setStatus(Label label, String message, boolean isError) {
        label.setText(message);
        label.setTextFill(isError ? Color.RED : Color.web("#27ae60"));
    }
}