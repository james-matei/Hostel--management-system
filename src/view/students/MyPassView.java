package view.students;

import controller.StudentDashboardController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Student;
import util.QRCodeGenerator;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MyPassView {

    private final Student student;
    private final StudentDashboardController controller;

    public MyPassView(Student student, StudentDashboardController controller) {
        this.student    = student;
        this.controller = controller;
    }

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(30));
        view.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("🪪 My Hostel Pass");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Label subtitle = new Label("Present this pass at the hostel entrance for scanning");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#7f8c8d"));

        boolean isPaid     = controller.hasPaidCurrentSemester(student.getId());
        String  accessText = isPaid ? "ALLOWED" : "NOT ALLOWED";
        String  semester   = getCurrentSemesterLabel();

        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.setMaxWidth(420);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + (isPaid ? "#27ae60" : "#e74c3c") + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0, 0, 4);"
        );

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 8;");
        Label hostelName = new Label("🏨  HOSTEL MANAGEMENT SYSTEM");
        hostelName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        hostelName.setTextFill(Color.WHITE);
        header.getChildren().add(hostelName);

        HBox statusBanner = new HBox();
        statusBanner.setAlignment(Pos.CENTER);
        statusBanner.setPadding(new Insets(8));
        statusBanner.setStyle("-fx-background-color: " + (isPaid ? "#27ae60" : "#e74c3c") + "; -fx-background-radius: 6;");
        Label statusLabel = new Label(isPaid ? "✅  ACCESS ALLOWED" : "❌  ACCESS NOT ALLOWED");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        statusLabel.setTextFill(Color.WHITE);
        statusBanner.getChildren().add(statusLabel);

        ImageView qrView = new ImageView();
        qrView.setFitWidth(190);
        qrView.setFitHeight(190);
        qrView.setPreserveRatio(true);
        if (!isPaid) qrView.setOpacity(0.45);

        Label qrErrorLabel = new Label();
        qrErrorLabel.setFont(Font.font("Arial", 11));
        qrErrorLabel.setTextFill(Color.RED);

        String qrContent = buildQRContent(accessText, semester);
        try {
            Image qrImage = QRCodeGenerator.generateQRImage(qrContent);
            qrView.setImage(qrImage);
        } catch (Exception e) {
            qrErrorLabel.setText("Could not generate QR: " + e.getMessage());
        }

        GridPane details = new GridPane();
        details.setHgap(15);
        details.setVgap(6);
        details.setAlignment(Pos.CENTER_LEFT);
        addDetailRow(details, "Name",     student.getName(),                                             0);
        addDetailRow(details, "Reg No",   student.getRegNumber() != null ? student.getRegNumber() : "—", 1);
        addDetailRow(details, "Course",   student.getCourse()    != null ? student.getCourse()    : "—", 2);
        addDetailRow(details, "Room",     student.getRoomId()    != null ? student.getRoomId()    : "Unassigned", 3);
        addDetailRow(details, "Semester", semester,                                                       4);

        Separator sep = new Separator();
        sep.setMaxWidth(340);

        VBox notPaidBox = new VBox(10);
        notPaidBox.setAlignment(Pos.CENTER);
        if (!isPaid) {
            Label msg = new Label("⚠  Your hostel fee for " + semester + " has not been paid.");
            msg.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            msg.setTextFill(Color.web("#e74c3c"));
            msg.setWrapText(true);
            msg.setMaxWidth(340);

            Button payBtn = new Button("💳  Pay via M-Pesa");
            payBtn.setMaxWidth(Double.MAX_VALUE);
            payBtn.setPrefHeight(40);
            payBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            payBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
            payBtn.setOnAction(e -> new MpesaPaymentDialog(student).show());
            notPaidBox.getChildren().addAll(msg, payBtn);
        }

        Label saveStatus = new Label();
        saveStatus.setFont(Font.font("Arial", 11));
        saveStatus.setWrapText(true);
        saveStatus.setMaxWidth(340);

        Button saveBtn = new Button("💾  Save Pass as PNG");
        saveBtn.setStyle(
            "-fx-background-color: " + (isPaid ? "#2c3e50" : "#bdc3c7") + ";" +
            "-fx-text-fill: white; -fx-font-size: 13;" +
            "-fx-cursor: " + (isPaid ? "hand" : "default") + ";" +
            "-fx-padding: 10 25; -fx-background-radius: 6;"
        );
        saveBtn.setDisable(!isPaid);
        if (isPaid) saveBtn.setOnAction(e -> savePass(qrContent, saveStatus));

        card.getChildren().addAll(header, statusBanner, qrView, qrErrorLabel, sep, details, notPaidBox, saveBtn, saveStatus);
        view.getChildren().addAll(title, subtitle, card);
        return view;
    }

    private String buildQRContent(String accessText, String semester) {
        return QRCodeGenerator.buildQRContent(student.getName(), student.getRegNumber(), student.getRoomId(), student.getCourse())
             + "\nSemester: " + semester
             + "\nACCESS: "   + accessText
             + "\nGenerated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    private String getCurrentSemesterLabel() {
        int month = LocalDate.now().getMonthValue();
        int year  = LocalDate.now().getYear();
        return (month <= 6) ? "Semester 1 - " + year : "Semester 2 - " + year;
    }

    private void addDetailRow(GridPane grid, String label, String value, int row) {
        Label l = new Label(label + ":"); l.setFont(Font.font("Arial", FontWeight.BOLD, 12)); l.setTextFill(Color.web("#7f8c8d")); l.setMinWidth(80);
        Label v = new Label(value);       v.setFont(Font.font("Arial", 12));                  v.setTextFill(Color.web("#2c3e50"));
        grid.add(l, 0, row); grid.add(v, 1, row);
    }

    private void savePass(String qrContent, Label statusLabel) {
        try {
            String regNo    = student.getRegNumber() != null ? student.getRegNumber() : student.getId();
            String safeName = student.getName().replaceAll("[^a-zA-Z0-9]", "_");
            File saved = QRCodeGenerator.saveQRCode(qrContent, "src/qrcodes/" + regNo + "_" + safeName + "_pass.png");
            statusLabel.setTextFill(Color.web("#27ae60"));
            statusLabel.setText("✅ Pass saved to: " + saved.getAbsolutePath());
        } catch (Exception e) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("❌ Failed to save: " + e.getMessage());
        }
    }
}
