package view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import java.util.List;

import controller.AuthController;
import controller.StudentDashboardController;
import model.Payment;
import model.Student;
import view.students.MyRoomView;
import view.students.MyPaymentsView;
import view.students.MyDetailsView;
import view.students.MyPassView;
import view.students.AnnouncementsView;
import view.students.SettingsView;

public class StudentDashboardView extends Application {

    private Stage primaryStage;
    private BorderPane mainLayout;
    private Label statusLabel;
    private Label userLabel;
    private Label dateTimeLabel;
    private Student currentStudent;
    private StudentDashboardController controller;

    private ObservableList<Payment> payments      = FXCollections.observableArrayList();
    private ObservableList<String>  announcements = FXCollections.observableArrayList();

    public void setCurrentUser(Object user) {
        if (user instanceof Student s) {
            this.currentStudent = s;
            if (userLabel != null) userLabel.setText("Student: " + s.getName());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.controller = new StudentDashboardController();

        if (currentStudent == null) {
            Object user = AuthController.getCurrentUser();
            if (!(user instanceof Student)) {
                showErrorAndRedirect("Access Denied", "Student access required.");
                return;
            }
            this.currentStudent = (Student) user;
        }

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Hostel Management System - Student Dashboard");

        loadStudentData();

        mainLayout = new BorderPane();
        mainLayout.setTop(createHeader());
        mainLayout.setLeft(createSidebar());
        mainLayout.setCenter(createDashboardContent());
        mainLayout.setBottom(createStatusBar());

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

        startClock();
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    private void loadStudentData() {
        List<Payment> p = controller.getStudentPayments(currentStudent.getId());
        payments.setAll(p);
        announcements.addAll(controller.getAnnouncements());
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #2c3e50;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🏨 HOSTEL MANAGEMENT SYSTEM");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        userLabel = new Label("Student: " + currentStudent.getName());
        userLabel.setFont(Font.font("Arial", 14));
        userLabel.setTextFill(Color.LIGHTGRAY);

        dateTimeLabel = new Label();
        dateTimeLabel.setFont(Font.font("Arial", 14));
        dateTimeLabel.setTextFill(Color.LIGHTGRAY);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> logout());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, userLabel, dateTimeLabel, logoutBtn);
        return header;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setStyle("-fx-background-color: #34495e;");
        sidebar.setPrefWidth(220);

        String[][] navItems = {
            {"📊 Dashboard",    "Dashboard"},
            {"🏠 My Room",       "MyRoom"},
            {"💰 My Payments",   "MyPayments"},
            {"📄 My Details",    "MyDetails"},
            {"🪪 My Pass",       "MyPass"},
            {"📢 Announcements", "Announcements"},
            {"⚙ Settings",      "Settings"}
        };

        for (String[] item : navItems) {
            sidebar.getChildren().add(buildNavButton(item[0], item[1]));
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        Button logoutBtn = buildNavButton("🚪 Logout", "Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;");
        sidebar.getChildren().add(logoutBtn);

        return sidebar;
    }

    private Button buildNavButton(String text, String action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setUserData(action);

        String normal = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;";
        String hover  = "-fx-background-color: #3d566e;    -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;";

        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e  -> btn.setStyle(normal));
        btn.setOnAction(e -> handleNavigation(btn.getUserData().toString()));

        return btn;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void handleNavigation(String page) {
        statusLabel.setText("Loading " + page + "...");
        switch (page) {
            case "Dashboard"     -> mainLayout.setCenter(createDashboardContent());
            case "MyRoom"        -> mainLayout.setCenter(new MyRoomView(currentStudent).getView());
            case "MyPayments"    -> {
                MyPaymentsView paymentsView = new MyPaymentsView(currentStudent, controller, payments);
                mainLayout.setCenter(paymentsView.getView());
            }
            case "MyDetails"     -> mainLayout.setCenter(new MyDetailsView(currentStudent).getView());
            case "MyPass"        -> mainLayout.setCenter(new MyPassView(currentStudent, controller).getView());
            case "Announcements" -> mainLayout.setCenter(new AnnouncementsView(announcements).getView());
            case "Settings"      -> mainLayout.setCenter(new SettingsView().getView());
            case "Logout"        -> logout();
            default              -> {
                showAlert(Alert.AlertType.INFORMATION, "Not Implemented", null, page + " coming soon!");
                statusLabel.setText(page + " not yet implemented");
            }
        }
    }

    // ── Dashboard Content ─────────────────────────────────────────────────────

    private GridPane createDashboardContent() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(20);

        grid.add(createCard("Welcome back, " + currentStudent.getName() + "!",
            "Here's your hostel information for today.", "#3498db"), 0, 0, 4, 1);

        grid.add(createStatsCard("Room Number",
            currentStudent.getRoomId() != null ? currentStudent.getRoomId() : "Not Assigned",
            getRoomStatus(), "#27ae60"), 0, 1);

        grid.add(createStatsCard("Payment Status", getPaymentStatus(),
            "Current Semester", "#e67e22"), 1, 1);

        grid.add(createStatsCard("Hostel Block", getHostelBlock(),
            "Floor: " + getFloorNumber(), "#9b59b6"), 2, 1);

        grid.add(createStatsCard("Room Price",
            "KSh 10,000", "Per semester", "#e74c3c"), 3, 1);

        grid.add(buildAnnouncementsPreview(), 0, 2, 2, 1);
        grid.add(buildRecentPaymentsPreview(), 2, 2, 2, 1);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            grid.getColumnConstraints().add(cc);
        }
        return grid;
    }

    private VBox buildAnnouncementsPreview() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label title = new Label("📢 Latest Announcements");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        ObservableList<String> preview = FXCollections.observableArrayList(
            announcements.size() > 3 ? announcements.subList(0, 3) : announcements);
        ListView<String> list = new ListView<>(preview);
        list.setPrefHeight(120);

        Button viewAll = new Button("View All Announcements");
        viewAll.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        viewAll.setMaxWidth(Double.MAX_VALUE);
        viewAll.setOnAction(e -> handleNavigation("Announcements"));

        card.getChildren().addAll(title, list, viewAll);
        return card;
    }

    private VBox buildRecentPaymentsPreview() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label title = new Label("💰 Recent Payments");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        Label[] headers = {new Label("Date"), new Label("Amount"), new Label("Status")};
        for (int i = 0; i < headers.length; i++) {
            headers[i].setFont(Font.font("Arial", FontWeight.BOLD, 11));
            grid.add(headers[i], i, 0);
        }

        if (payments.isEmpty()) {
            Label none = new Label("No payments recorded yet.");
            none.setTextFill(Color.GRAY);
            grid.add(none, 0, 1, 3, 1);
        } else {
            for (int i = 0; i < Math.min(3, payments.size()); i++) {
                Payment p = payments.get(i);
                grid.add(new Label(p.getPaymentDate().toString()), 0, i + 1);
                grid.add(new Label("KSh " + String.format("%,.0f", p.getAmount())), 1, i + 1);
                Label s = new Label(p.getStatus());
                s.setTextFill(p.getStatus().equals("Paid")    ? Color.GREEN
                            : p.getStatus().equals("Pending") ? Color.ORANGE : Color.RED);
                grid.add(s, 2, i + 1);
            }
        }

        Button viewAll = new Button("View All Payments");
        viewAll.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        viewAll.setMaxWidth(Double.MAX_VALUE);
        viewAll.setOnAction(e -> handleNavigation("MyPayments"));

        card.getChildren().addAll(title, grid, viewAll);
        return card;
    }

    // ── Card Builders ─────────────────────────────────────────────────────────

    private VBox createCard(String title, String content, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5;");
        card.setPrefHeight(100);

        Label t = new Label(title); t.setFont(Font.font("Arial", FontWeight.BOLD, 16)); t.setTextFill(Color.WHITE);
        Label c = new Label(content); c.setTextFill(Color.WHITE); c.setWrapText(true);

        card.getChildren().addAll(t, c);
        return card;
    }

    private VBox createStatsCard(String title, String value, String change, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        card.setPrefHeight(120);

        Label t  = new Label(title);  t.setFont(Font.font("Arial", 12)); t.setTextFill(Color.GRAY);
        Label v  = new Label(value);  v.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Label ch = new Label(change); ch.setFont(Font.font("Arial", 11)); ch.setTextFill(Color.web(color));
        Rectangle accent = new Rectangle(50, 3); accent.setFill(Color.web(color));

        card.getChildren().addAll(t, v, ch, accent);
        return card;
    }

    // ── Status Bar ────────────────────────────────────────────────────────────

    private HBox createStatusBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(5, 15, 5, 15));
        bar.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        bar.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label("Ready");
        statusLabel.setFont(Font.font("Arial", 11));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label version = new Label("v1.0.0 - Student Portal");
        version.setFont(Font.font("Arial", 11));
        version.setTextFill(Color.GRAY);

        bar.getChildren().addAll(statusLabel, spacer, version);
        return bar;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void startClock() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1),
            e -> dateTimeLabel.setText(LocalDateTime.now().format(fmt))));
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
            try { new LoginView().start(new Stage()); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void showErrorAndRedirect(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, null, message);
        try { new LoginView().start(new Stage()); } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert a = new Alert(type);
        a.setTitle(title); a.setHeaderText(header); a.setContentText(content);
        a.showAndWait();
    }

    private String getRoomStatus()    { return currentStudent.getRoomId() != null ? "Occupied" : "Not Assigned"; }
    private String getPaymentStatus() { return payments.isEmpty() ? "No payments" : payments.get(0).getStatus(); }
    private String getHostelBlock() {
        String r = currentStudent.getRoomId();
        if (r == null) return "Not Assigned";
        if (r.startsWith("1")) return "Block A";
        if (r.startsWith("2")) return "Block B";
        if (r.startsWith("3")) return "Block C";
        return "Block A";
    }
    private String getFloorNumber() {
        String r = currentStudent.getRoomId();
        return (r != null) ? String.valueOf(r.charAt(0)) : "N/A";
    }

    @Override
    public void stop() {
        System.out.println("StudentDashboardView shutting down at " + LocalDateTime.now());
    }

    public static void main(String[] args) { launch(args); }
}