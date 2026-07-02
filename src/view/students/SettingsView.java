package view.students;

import controller.AuthController;
import controller.StudentController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Student;

public class SettingsView {

    private final StudentController studentController = new StudentController();

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        view.getChildren().addAll(title, buildSettingsBox());
        return view;
    }

    private VBox buildSettingsBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #ddd;" +
            "-fx-border-radius: 10;"
        );
        box.getChildren().add(
            buildSettingRow("🔐 Change Password", "Update your account password", this::showChangePasswordDialog)
        );
        return box;
    }

    private HBox buildSettingRow(String setting, String description, Runnable onEdit) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-border-color: transparent transparent #ecf0f1 transparent; -fx-border-width: 0 0 1 0;");

        Label icon = new Label(setting.split(" ")[0]);
        icon.setFont(Font.font(18));

        VBox text = new VBox(3);
        Label name = new Label(setting.substring(2));
        name.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label desc = new Label(description);
        desc.setFont(Font.font("Arial", 11));
        desc.setTextFill(Color.GRAY);
        text.getChildren().addAll(name, desc);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        editBtn.setOnAction(e -> onEdit.run());

        row.getChildren().addAll(icon, text, spacer, editBtn);
        return row;
    }

    // ── Change Password Dialog ────────────────────────────────────────────────

    private void showChangePasswordDialog() {
        Object user = AuthController.getCurrentUser();
        if (!(user instanceof Student student)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not identify current user.");
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Change Password");
        stage.setResizable(false);

        VBox root = new VBox(12);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: white;");
        root.setPrefWidth(380);

        Label title = new Label("🔐 Change Password");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2c3e50"));

        Label subtitle = new Label("Enter your current password and choose a new one.");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(Color.GRAY);
        subtitle.setWrapText(true);

        PasswordField currentField = new PasswordField();
        currentField.setPromptText("Current password");
        currentField.setPrefHeight(40);
        currentField.setStyle(fieldStyle());

        PasswordField newField = new PasswordField();
        newField.setPromptText("New password (min 6 characters)");
        newField.setPrefHeight(40);
        newField.setStyle(fieldStyle());

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm new password");
        confirmField.setPrefHeight(40);
        confirmField.setStyle(fieldStyle());

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(320);
        statusLabel.setMinHeight(18);

        Button saveBtn = new Button("Update Password");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setPrefHeight(42);
        saveBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        saveBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setPrefHeight(38);
        cancelBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> stage.close());

        saveBtn.setOnAction(e -> {
            String current = currentField.getText();
            String newPass  = newField.getText();
            String confirm  = confirmField.getText();

            if (current.isEmpty()) {
                setStatus(statusLabel, "❌ Please enter your current password.", true); return;
            }
            if (newPass.isEmpty()) {
                setStatus(statusLabel, "❌ Please enter a new password.", true); return;
            }
            if (newPass.length() < 6) {
                setStatus(statusLabel, "❌ New password must be at least 6 characters.", true); return;
            }
            if (!newPass.equals(confirm)) {
                setStatus(statusLabel, "❌ Passwords do not match.", true); return;
            }
            if (newPass.equals(current)) {
                setStatus(statusLabel, "❌ New password must be different from current.", true); return;
            }

            boolean success = studentController.changePassword(student.getUsername(), current, newPass);
            if (success) {
                setStatus(statusLabel, "✅ Password changed successfully!", false);
                saveBtn.setDisable(true);
                currentField.clear(); newField.clear(); confirmField.clear();
            } else {
                setStatus(statusLabel, "❌ Current password is incorrect. Please try again.", true);
                currentField.clear();
                currentField.requestFocus();
            }
        });

        root.getChildren().addAll(
            title, subtitle,
            labelFor("Current Password"), currentField,
            labelFor("New Password"),     newField,
            labelFor("Confirm Password"), confirmField,
            statusLabel, saveBtn, cancelBtn
        );

        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Label labelFor(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#2c3e50"));
        return l;
    }

    private String fieldStyle() {
        return "-fx-padding: 8; -fx-background-radius: 6; -fx-border-radius: 6; " +
               "-fx-border-color: #dfe6e9; -fx-background-color: white; -fx-font-size: 13;";
    }

    private void setStatus(Label label, String message, boolean isError) {
        label.setText(message);
        label.setTextFill(isError ? Color.RED : Color.web("#27ae60"));
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(content);
        a.showAndWait();
    }
}