package view.students;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import model.Student;
import util.QRCodeGenerator;

import java.io.File;

public class QRCodeDialog {

    private final Student student;

    public QRCodeDialog(Student student) {
        this.student = student;
    }

    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("QR Code — " + student.getName());
        stage.setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        // ── Title ─────────────────────────────────────────────────────────────
        Label title = new Label("Student QR Code");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        // ── Student info ──────────────────────────────────────────────────────
        String content = QRCodeGenerator.buildQRContent(
            student.getName(),
            student.getRegNumber(),
            student.getRoomId(),
            student.getCourse()
        );

        Label infoLabel = new Label(content);
        infoLabel.setFont(Font.font("Arial", 13));
        infoLabel.setTextFill(Color.web("#2c3e50"));
        infoLabel.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 5;");

        // ── QR Image ──────────────────────────────────────────────────────────
        ImageView qrView = new ImageView();
        qrView.setFitWidth(250);
        qrView.setFitHeight(250);
        qrView.setPreserveRatio(true);

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(320);

        try {
            Image qrImage = QRCodeGenerator.generateQRImage(content);
            qrView.setImage(qrImage);
        } catch (Exception e) {
            statusLabel.setText("❌ Failed to generate QR code: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }

        // ── Buttons ───────────────────────────────────────────────────────────
        Button saveBtn = new Button("💾 Save as PNG");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                         "-fx-font-size: 13; -fx-cursor: hand; -fx-padding: 8 20;");
        saveBtn.setOnAction(e -> saveQRCode(content, statusLabel));

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                          "-fx-font-size: 13; -fx-cursor: hand; -fx-padding: 8 20;");
        closeBtn.setOnAction(e -> stage.close());

        HBox btnRow = new HBox(10, saveBtn, closeBtn);
        btnRow.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, infoLabel, qrView, btnRow, statusLabel);

        Scene scene = new Scene(root, 370, 560);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void saveQRCode(String content, Label statusLabel) {
        try {
            // Save to src/qrcodes/<regNumber>_<name>.png
            String regNo   = student.getRegNumber() != null ? student.getRegNumber() : student.getId();
            String safeName = student.getName().replaceAll("[^a-zA-Z0-9]", "_");
            String fileName = regNo + "_" + safeName + ".png";
            String filePath = "src/qrcodes/" + fileName;

            File saved = QRCodeGenerator.saveQRCode(content, filePath);
            statusLabel.setTextFill(Color.web("#27ae60"));
            statusLabel.setText("✅ Saved to: " + saved.getAbsolutePath());

        } catch (Exception e) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("❌ Failed to save: " + e.getMessage());
        }
    }
}
