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
        this.primaryStage = primaryStage;
        this.authController = new AuthController();
        
        primaryStage.setTitle("Hostel Management System - Login");
        
        // Create main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #ecf0f1;");
        
        // Center login form
        VBox loginForm = createLoginForm();
        mainLayout.setCenter(loginForm);
        
        // Add footer
        mainLayout.setBottom(createFooter());
        
        Scene scene = new Scene(mainLayout, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createLoginForm() {
        VBox form = new VBox(20);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(40));
        form.setMaxWidth(350);
        form.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");
        
        // Title
        Label titleLabel = new Label("🏨 HOSTEL MANAGEMENT");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));
        
        Label subtitleLabel = new Label("Login to your account");
        subtitleLabel.setFont(Font.font("Arial", 14));
        subtitleLabel.setTextFill(Color.GRAY);
        
        // Username field
        VBox usernameBox = new VBox(5);
        Label usernameLabel = new Label("Username");
        usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter admin username or student ID");
        usernameField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #ddd;");
        usernameBox.getChildren().addAll(usernameLabel, usernameField);
        
        // Password field
        VBox passwordBox = new VBox(5);
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #ddd;");
        passwordBox.getChildren().addAll(passwordLabel, passwordField);
        
        // Login button
        Button loginButton = new Button("LOGIN");
        loginButton.setPrefHeight(40);
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        
        // Status label for messages
        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.RED);
        statusLabel.setWrapText(true);
        
        // Forgot password link
        Hyperlink forgotLink = new Hyperlink("Forgot password?");
        forgotLink.setTextFill(Color.GRAY);
        
        // Add hover effect to button
        loginButton.setOnMouseEntered(e -> 
            loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;"));
        loginButton.setOnMouseExited(e -> 
            loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;"));
        
        // Login action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty()) {
                statusLabel.setText("❌ Please enter username");
                statusLabel.setTextFill(Color.RED);
                return;
            }
            
            // Call login on the instance
            Object user = authController.login(username, password);
            
            if (user != null) {
                statusLabel.setText("✅ Login successful! Redirecting...");
                statusLabel.setTextFill(Color.GREEN);
                
                // Store user in AuthController for global access
                AuthController.setCurrentUser(user);
                
                // Close login window
                primaryStage.close();
                
                // Check role and redirect
                if (authController.isAdmin(user)) {
                    System.out.println("Redirecting to Admin Dashboard");
                    openDashboard(user);
                } else if (authController.isStudent(user)) {
                    System.out.println("Redirecting to Student Dashboard");
                    openStudentView(user);
                }
            } else {
                statusLabel.setText("❌ Invalid username or password");
                statusLabel.setTextFill(Color.RED);
            }
        });
        
        form.getChildren().addAll(titleLabel, subtitleLabel, usernameBox, passwordBox, loginButton, statusLabel, forgotLink);
        
        return form;
    }
    
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: #2c3e50;");
        
        Label copyright = new Label("© 2024 Hostel Management System v1.0");
        copyright.setFont(Font.font("Arial", 11));
        copyright.setTextFill(Color.LIGHTGRAY);
        
        footer.getChildren().add(copyright);
        
        return footer;
    }
    
    private void openDashboard(Object user) {
        try {
            // Create and show dashboard with user
            DashboardView dashboard = new DashboardView();
            dashboard.setCurrentUser(user);  // Pass the user object
            
            Stage dashboardStage = new Stage();
            dashboard.start(dashboardStage);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open dashboard: " + e.getMessage());
        }
    }
    
  private void openStudentView(Object user) {
    try {
        // Open StudentDashboardView directly
        StudentDashboardView studentDashboard = new StudentDashboardView();
        studentDashboard.setCurrentUser(user);
        
        Stage studentStage = new Stage();
        studentDashboard.start(studentStage);
        
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
    
    public static void main(String[] args) {
        launch(args);
    }
}