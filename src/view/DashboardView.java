package view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import controller.AuthController;
import model.Admin;
import model.Student;
import view.components.HeaderComponent;
import view.components.SidebarComponent;
import view.components.StatusBarComponent;
import view.students.StudentsView;
import view.students.StudentFormDialog;
import view.rooms.RoomsView;
import view.payments.PaymentsView;

public class DashboardView extends Application {

    private Stage primaryStage;
    private BorderPane mainLayout;
    private Object currentUser;
    private AuthController authController;

    // Components
    private HeaderComponent header;
    private StatusBarComponent statusBar;

    // Dashboard-only data
    private final ObservableList<String> recentActivities = FXCollections.observableArrayList();
    private final ObservableList<Student> students = FXCollections.observableArrayList();

    public void setCurrentUser(Object user) {
        this.currentUser = user;
        if (header != null) header.updateUser(user);
    }

    @Override
    public void start(Stage primaryStage) {
        this.authController = new AuthController();
        if (this.currentUser == null) this.currentUser = AuthController.getCurrentUser();

        if (currentUser == null || !authController.isAdmin(currentUser)) {
            showAlert(Alert.AlertType.ERROR, "Unauthorized", null, "Admin access required.");
            redirectToLogin();
            primaryStage.close();
            return;
        }

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Hostel Management System - Dashboard");

        initializeSampleData();

        header    = new HeaderComponent(this::logout);
        statusBar = new StatusBarComponent();

        mainLayout = new BorderPane();
        mainLayout.setTop(header.getHeader());
        mainLayout.setLeft(new SidebarComponent(this::handleNavigation).getSidebar());
        mainLayout.setCenter(createDashboardContent());
        mainLayout.setBottom(statusBar.getStatusBar());

        Scene scene = new Scene(mainLayout, 1200, 700);
        try {
            scene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("No CSS file found, using default styles");
        }

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        Platform.runLater(() -> header.updateUser(currentUser));
        startClock();
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void handleNavigation(String page) {
        statusBar.setStatus("Loading " + page + "...");
        switch (page) {
            case "Dashboard" -> mainLayout.setCenter(createDashboardContent());
            case "Students"  -> mainLayout.setCenter(new StudentsView(students).getView());
            case "Rooms"     -> mainLayout.setCenter(new RoomsView().getView());
            case "Payments"  -> mainLayout.setCenter(new PaymentsView().getView());
            default          -> {
                showAlert(Alert.AlertType.INFORMATION, "Not Implemented", null, page + " view is coming soon!");
                statusBar.setStatus(page + " view not yet implemented");
            }
        }
    }

    // ── Dashboard Content ─────────────────────────────────────────────────────

    private GridPane createDashboardContent() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(20);

        String name = (currentUser instanceof Admin a) ? a.getName() : "Back";
        grid.add(createCard("Welcome back, " + name + "!", "Here's what's happening in your hostel today.", "#3498db"), 0, 0, 4, 1);
        grid.add(createStatsCard("Total Students",  "156",     "+12 this month",       "#27ae60"), 0, 1);
        grid.add(createStatsCard("Available Rooms", "23",      "Out of 180 total",     "#e67e22"), 1, 1);
        grid.add(createStatsCard("Monthly Revenue", "$45,600", "+15% vs last month",   "#9b59b6"), 2, 1);
        grid.add(createStatsCard("Occupancy Rate",  "87%",     "156/180 rooms filled", "#e74c3c"), 3, 1);
        grid.add(createActivitiesCard(), 0, 2, 2, 1);
        grid.add(createChartCard(),      2, 2, 2, 1);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            grid.getColumnConstraints().add(cc);
        }
        return grid;
    }

    private VBox createCard(String title, String content, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5;");
        card.setPrefHeight(100);

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        t.setTextFill(Color.WHITE);

        Label c = new Label(content);
        c.setTextFill(Color.WHITE);
        c.setWrapText(true);

        card.getChildren().addAll(t, c);
        return card;
    }

    private VBox createStatsCard(String title, String value, String change, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        card.setPrefHeight(120);

        Label t = new Label(title); t.setFont(Font.font("Arial", 12)); t.setTextFill(Color.GRAY);
        Label v = new Label(value); v.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Label ch = new Label(change); ch.setFont(Font.font("Arial", 11)); ch.setTextFill(Color.web(color));
        Rectangle accent = new Rectangle(50, 3); accent.setFill(Color.web(color));

        card.getChildren().addAll(t, v, ch, accent);
        return card;
    }

    private VBox createActivitiesCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label title = new Label("Recent Activities");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        ListView<String> list = new ListView<>(recentActivities);
        list.setPrefHeight(150);

        card.getChildren().addAll(title, list);
        return card;
    }

    private VBox createChartCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label title = new Label("Monthly Occupancy");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis(0, 200, 25));
        chart.setTitle("Room Occupancy (Last 6 Months)");
        chart.setLegendVisible(false);
        chart.setPrefHeight(200);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun"};
        int[] values    = {145,  148,  152,  150,  156,  155};
        for (int i = 0; i < months.length; i++)
            series.getData().add(new XYChart.Data<>(months[i], values[i]));

        chart.getData().add(series);
        card.getChildren().addAll(title, chart);
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void initializeSampleData() {
        recentActivities.addAll(
            "10:30 AM - Student John Doe checked in",
            "09:45 AM - Room 205 payment received",
            "Yesterday - New student registered",
            "Yesterday - Maintenance request completed",
            "2 days ago - Room 101 cleaning done"
        );
        
        students.addAll(
                new Student("S001", "John Smith",    20, "Computer Science"),
                new Student("S002", "Emma Watson",   21, "Business"),
                new Student("S003", "Michael Brown", 19, "Engineering"),
                new Student("S004", "Sarah Davis",   22, "Medicine"),
                new Student("S005", "James Wilson",  20, "Law")
            );

            students.get(0).setRoomId("101"); students.get(0).setStatus("Active");
            students.get(1).setRoomId("102"); students.get(1).setStatus("Active");
            students.get(2).setRoomId("103"); students.get(2).setStatus("Suspended");
            students.get(3).setRoomId("104"); students.get(3).setStatus("Active");
            students.get(4).setRoomId("105"); students.get(4).setStatus("Active");
    }

    
    /** Replaces Thread-based timer with JavaFX Timeline (recommended for UI updates) */
    private void startClock() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e ->
            header.getDateTimeLabel().setText(LocalDateTime.now().format(fmt))
        ));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setContentText("Are you sure you want to logout?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            AuthController.logout();
            primaryStage.close();
            redirectToLogin();
        }
    }

    private void redirectToLogin() {
        try {
            new LoginView().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        System.out.println("DashboardView shutting down at " + LocalDateTime.now());
    }

    public static void main(String[] args) { launch(args); }
}