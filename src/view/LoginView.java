package view;

import controller.AuthController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView extends Application {

    private AuthController authController;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage   = primaryStage;
        this.authController = new AuthController();

        primaryStage.setTitle("Hostel Management System - Login");
        primaryStage.setResizable(false);

        // ── Two-panel layout ──────────────────────────────────────────────────
        HBox root = new HBox();

        root.getChildren().addAll(createLeftPanel(), createRightPanel());

        Scene scene = new Scene(root, 750, 480);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ── Left decorative panel ─────────────────────────────────────────────────

    private VBox createLeftPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(300);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(40));
        panel.setStyle("-fx-background-color: #2c3e50;");

        Label icon = new Label("🏨");
        icon.setFont(Font.font("Arial", 60));

        Label name = new Label("HOSTEL\nMANAGEMENT\nSYSTEM");
        name.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        name.setTextFill(Color.WHITE);
        name.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label tagline = new Label("Manage smarter.\nLive better.");
        tagline.setFont(Font.font("Arial", 13));
        tagline.setTextFill(Color.web("#95a5a6"));
        tagline.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Separator sep = new Separator();
        sep.setMaxWidth(80);
        sep.setStyle("-fx-background-color: #3498db;");

        panel.getChildren().addAll(icon, name, sep, tagline);
        return panel;
    }

    // ── Right login form ──────────────────────────────────────────────────────

    private VBox createRightPanel() {
        VBox panel = new VBox(18);
        panel.setPrefWidth(450);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(50, 60, 50, 60));
        panel.setStyle("-fx-background-color: #f5f6fa;");

        Label titleLabel = new Label("Welcome back 👋");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        Label subtitleLabel = new Label("Sign in to your account");
        subtitleLabel.setFont(Font.font("Arial", 13));
        subtitleLabel.setTextFill(Color.GRAY);

        // Username
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefHeight(42);
        usernameField.setStyle(fieldStyle());

        // Password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(42);
        passwordField.setStyle(fieldStyle());

        // Status label
        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setMinHeight(18);

        // Login button
        Button loginButton = new Button("Sign In");
        loginButton.setPrefHeight(42);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;"));
        loginButton.setOnMouseExited(e  -> loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;"));

        // Allow Enter key to trigger login
        passwordField.setOnAction(e -> loginButton.fire());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        // Login action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty()) {
                setStatus(statusLabel, "❌ Please enter your username.", true);
                return;
            }
            if (password.isEmpty()) {
                setStatus(statusLabel, "❌ Please enter your password.", true);
                return;
            }

            Object user = authController.login(username, password);

            if (user != null) {
                setStatus(statusLabel, "✅ Login successful! Redirecting...", false);
                AuthController.setCurrentUser(user);
                primaryStage.close();

                if (authController.isAdmin(user)) {
                    System.out.println("Redirecting to Admin Dashboard");
                    openDashboard(user);
                } else if (authController.isStudent(user)) {
                    System.out.println("Redirecting to Student Dashboard");
                    openStudentView(user);
                }
            } else {
                setStatus(statusLabel, "❌ Invalid username or password.", true);
                passwordField.clear();
                passwordField.requestFocus();
            }
        });

        // Footer
        Label footer = new Label("© 2024 Hostel Management System v1.0");
        footer.setFont(Font.font("Arial", 11));
        footer.setTextFill(Color.LIGHTGRAY);

        panel.getChildren().addAll(
            titleLabel, subtitleLabel,
            usernameField, passwordField,
            statusLabel, loginButton,
            footer
        );
        return panel;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String fieldStyle() {
        return "-fx-padding: 10; -fx-background-radius: 6; " +
               "-fx-border-radius: 6; -fx-border-color: #dfe6e9; " +
               "-fx-background-color: white; -fx-font-size: 13;";
    }

    private void setStatus(Label label, String message, boolean isError) {
        label.setText(message);
        label.setTextFill(isError ? Color.RED : Color.web("#27ae60"));
    }

    private void openDashboard(Object user) {
        try {
            DashboardView dashboard = new DashboardView();
            dashboard.setCurrentUser(user);
            dashboard.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open dashboard: " + e.getMessage());
        }
    }

    private void openStudentView(Object user) {
        try {
            StudentDashboardView studentDashboard = new StudentDashboardView();
            studentDashboard.setCurrentUser(user);
            studentDashboard.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open student dashboard: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}