package view;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.collections.*;
import javafx.scene.chart.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import controller.AuthController;
import model.Admin;
import model.Student;

public class DashboardView extends Application {
    
    private Stage primaryStage;
    private BorderPane mainLayout;
    private Label statusLabel;
    private Label userLabel;
    private Label dateTimeLabel;
    private Object currentUser;
    private AuthController authController;
    
    // Sample data collections
    private ObservableList<String> recentActivities = FXCollections.observableArrayList();
    private ObservableList<StudentData> students = FXCollections.observableArrayList();
    
    // Inner class for student data
    public static class StudentData {
        private String id;
        private String name;
        private String room;
        private String status;
        
        public StudentData(String id, String name, String room, String status) {
            this.id = id;
            this.name = name;
            this.room = room;
            this.status = status;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getRoom() { return room; }
        public String getStatus() { return status; }
    }
    
    // FIXED: Add null check in setCurrentUser
    public void setCurrentUser(Object user) {
        this.currentUser = user;
        // Only update if UI is initialized
        if (userLabel != null) {
            updateUserLabel();
        } else {
            System.out.println("User set, will update when UI is ready");
        }
    }
    
    // FIXED: Add null check in updateUserLabel
    private void updateUserLabel() {
        if (userLabel == null) {
            System.out.println("UserLabel not initialized yet");
            return;
        }
        
        if (currentUser != null) {
            if (currentUser instanceof Admin) {
                Admin admin = (Admin) currentUser;
                userLabel.setText("Admin: " + admin.getName() + " (" + admin.getRole() + ")");
            } else if (currentUser instanceof Student) {
                Student student = (Student) currentUser;
                userLabel.setText("Student: " + student.getName());
            }
        } else {
            userLabel.setText("Not logged in");
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.authController = new AuthController();
        
        // Try to get user from AuthController first
        if (this.currentUser == null) {
            this.currentUser = AuthController.getCurrentUser();
        }
        
        // Check if user is authenticated and is admin
        if (currentUser == null || !authController.isAdmin(currentUser)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unauthorized");
            alert.setContentText("Admin access required.");
            alert.showAndWait();
            
            // Redirect to login
            try {
                LoginView login = new LoginView();
                Stage loginStage = new Stage();
                login.start(loginStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            primaryStage.close();
            return;
        }
        
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Hostel Management System - Dashboard");
        
        // Create main layout
        mainLayout = new BorderPane();
        
        // Initialize sample data
        initializeSampleData();
        
        // Setup UI sections
        mainLayout.setTop(createHeader());
        mainLayout.setLeft(createSidebar());
        mainLayout.setCenter(createDashboardContent());
        mainLayout.setBottom(createStatusBar());
        
        // Create scene
        Scene scene = new Scene(mainLayout, 1200, 700);
        
        // Try to load CSS if exists
        try {
            scene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("No CSS file found, using default styles");
        }
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
        
        // FIXED: Use Platform.runLater to ensure UI is ready
        Platform.runLater(() -> {
            updateUserLabel();
        });
        
        // Start time updater
        startTimeUpdater();
        
        System.out.println("DashboardView started successfully at " + LocalDateTime.now());
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #2c3e50;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        
        // Logo/Title
        Label titleLabel = new Label("🏨 HOSTEL MANAGEMENT SYSTEM");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        // User info - initialize userLabel here
        userLabel = new Label("Loading...");
        userLabel.setFont(Font.font("Arial", 14));
        userLabel.setTextFill(Color.LIGHTGRAY);
        
        // Date and time
        dateTimeLabel = new Label();
        dateTimeLabel.setFont(Font.font("Arial", 14));
        dateTimeLabel.setTextFill(Color.LIGHTGRAY);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Logout button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> logout());
        
        header.getChildren().addAll(titleLabel, spacer, userLabel, dateTimeLabel, logoutBtn);
        
        return header;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setStyle("-fx-background-color: #34495e;");
        sidebar.setPrefWidth(220);
        sidebar.setSpacing(10);
        
        // Navigation buttons
        String[] navItems = {
            "📊 Dashboard", "👥 Students", "🏠 Rooms", 
            "💰 Payments", "📋 Reports", "⚙ Settings", "❓ Help"
        };
        
        for (String item : navItems) {
            Button navButton = createNavButton(item);
            sidebar.getChildren().add(navButton);
        }
        
        // Quick stats
        VBox statsBox = new VBox(10);
        statsBox.setPadding(new Insets(20, 10, 10, 10));
        statsBox.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 5;");
        
        Label statsTitle = new Label("QUICK STATS");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        statsTitle.setTextFill(Color.WHITE);
        
        Label totalStudents = new Label("👥 Total Students: 156");
        totalStudents.setTextFill(Color.LIGHTGRAY);
        
        Label availableRooms = new Label("🏠 Available Rooms: 23");
        availableRooms.setTextFill(Color.LIGHTGRAY);
        
        Label occupancyRate = new Label("📊 Occupancy: 87%");
        occupancyRate.setTextFill(Color.LIGHTGRAY);
        
        statsBox.getChildren().addAll(statsTitle, totalStudents, availableRooms, occupancyRate);
        
        sidebar.getChildren().add(statsBox);
        
        return sidebar;
    }
    
    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
        
        // Hover effect
        btn.setOnMouseEntered(e -> 
            btn.setStyle("-fx-background-color: #3d566e; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> 
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;"));
        
        // Action based on button text
        btn.setOnAction(e -> handleNavigation(text.replaceAll("[^a-zA-Z]", "")));
        
        return btn;
    }
    
    private void handleNavigation(String page) {
        System.out.println("Navigating to: " + page);
        statusLabel.setText("Loading " + page + "...");
        
        switch(page) {
            case "Dashboard":
                mainLayout.setCenter(createDashboardContent());
                break;
            case "Students":
                mainLayout.setCenter(createStudentsView());
                break;
            case "Rooms":
                mainLayout.setCenter(createRoomsView());
                break;
            case "Payments":
                mainLayout.setCenter(createPaymentsView());
                break;
            default:
                showNotImplemented(page);
        }
    }
    
    private GridPane createDashboardContent() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(20);
        
        // Personalized welcome message
        String welcomeMessage = "Welcome Back";
        if (currentUser instanceof Admin) {
            welcomeMessage = "Welcome back, " + ((Admin)currentUser).getName();
        }
        
        VBox welcomeCard = createCard(welcomeMessage + "!", 
            "Here's what's happening in your hostel today.", 
            "#3498db");
        
        // Stats cards
        VBox studentsCard = createStatsCard("Total Students", "156", "+12 this month", "#27ae60");
        VBox roomsCard = createStatsCard("Available Rooms", "23", "Out of 180 total", "#e67e22");
        VBox paymentsCard = createStatsCard("Monthly Revenue", "$45,600", "+15% vs last month", "#9b59b6");
        VBox occupancyCard = createStatsCard("Occupancy Rate", "87%", "156/180 rooms filled", "#e74c3c");
        
        // Recent activities
        VBox activitiesCard = createActivitiesCard();
        
        // Occupancy chart
        VBox chartCard = createChartCard();
        
        // Add to grid
        grid.add(welcomeCard, 0, 0, 4, 1);
        grid.add(studentsCard, 0, 1);
        grid.add(roomsCard, 1, 1);
        grid.add(paymentsCard, 2, 1);
        grid.add(occupancyCard, 3, 1);
        grid.add(activitiesCard, 0, 2, 2, 1);
        grid.add(chartCard, 2, 2, 2, 1);
        
        // Column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);
        
        return grid;
    }
    
    @SuppressWarnings({"unchecked"})
    private VBox createStudentsView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        
        // Search bar
        HBox searchBar = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search students by name or ID...");
        searchField.setPrefHeight(35);
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        Button addBtn = new Button("+ Add Student");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
        
        searchBar.getChildren().addAll(searchField, searchBtn, addBtn);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        // Students table
        TableView<StudentData> table = new TableView<>();
        
        TableColumn<StudentData, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        
        TableColumn<StudentData, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        
        TableColumn<StudentData, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRoom()));
        
        TableColumn<StudentData, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        
        TableColumn<StudentData, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<StudentData, Void>() {
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            private final HBox pane = new HBox(5, viewBtn, editBtn);
            
            {
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand;");
                
                viewBtn.setOnAction(e -> {
                    StudentData student = getTableView().getItems().get(getIndex());
                    showStudentDetails(student);
                });
                
                editBtn.setOnAction(e -> {
                    StudentData student = getTableView().getItems().get(getIndex());
                    editStudent(student);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
        
        table.getColumns().addAll(idCol, nameCol, roomCol, statusCol, actionCol);
        table.setItems(students);
        table.setPrefHeight(400);
        
        // Search functionality
        searchBtn.setOnAction(e -> {
            String searchText = searchField.getText().toLowerCase();
            if (searchText.isEmpty()) {
                table.setItems(students);
            } else {
                ObservableList<StudentData> filtered = FXCollections.observableArrayList();
                for (StudentData student : students) {
                    if (student.getName().toLowerCase().contains(searchText) ||
                        student.getId().toLowerCase().contains(searchText)) {
                        filtered.add(student);
                    }
                }
                table.setItems(filtered);
            }
        });
        
        view.getChildren().addAll(searchBar, table);
        
        return view;
    }
    
    private VBox createRoomsView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Room Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Room grid
        FlowPane roomGrid = new FlowPane();
        roomGrid.setHgap(10);
        roomGrid.setVgap(10);
        
        for (int i = 101; i <= 110; i++) {
            VBox roomCard = new VBox(5);
            roomCard.setPadding(new Insets(15));
            String color = (i % 2 == 0) ? "#27ae60" : "#e74c3c";
            roomCard.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5; -fx-cursor: hand;");
            roomCard.setPrefSize(120, 100);
            
            Label roomNum = new Label("Room " + i);
            roomNum.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            roomNum.setTextFill(Color.WHITE);
            
            Label status = new Label(i % 2 == 0 ? "Available" : "Occupied");
            status.setTextFill(Color.WHITE);
            
            Label student = new Label(i % 2 == 0 ? "" : "John Doe");
            student.setTextFill(Color.WHITE);
            student.setFont(Font.font("Arial", 10));
            
            roomCard.getChildren().addAll(roomNum, status, student);
            
            // Fix for lambda variable - create final copy
            final int roomNumber = i;
            roomCard.setOnMouseClicked(e -> showRoomDetails(roomNumber));
            
            roomGrid.getChildren().add(roomCard);
        }
        
        view.getChildren().addAll(title, roomGrid);
        
        return view;
    }
    
    private VBox createPaymentsView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Payment Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Payment summary
        HBox summaryBox = new HBox(20);
        
        VBox totalBox = new VBox(5);
        totalBox.setPadding(new Insets(15));
        totalBox.setStyle("-fx-background-color: #3498db; -fx-background-radius: 5;");
        totalBox.setPrefWidth(200);
        
        Label totalLabel = new Label("Total Collected");
        totalLabel.setTextFill(Color.WHITE);
        
        Label totalAmount = new Label("$124,500");
        totalAmount.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        totalAmount.setTextFill(Color.WHITE);
        
        totalBox.getChildren().addAll(totalLabel, totalAmount);
        
        VBox pendingBox = new VBox(5);
        pendingBox.setPadding(new Insets(15));
        pendingBox.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 5;");
        pendingBox.setPrefWidth(200);
        
        Label pendingLabel = new Label("Pending Payments");
        pendingLabel.setTextFill(Color.WHITE);
        
        Label pendingAmount = new Label("$12,300");
        pendingAmount.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        pendingAmount.setTextFill(Color.WHITE);
        
        pendingBox.getChildren().addAll(pendingLabel, pendingAmount);
        
        summaryBox.getChildren().addAll(totalBox, pendingBox);
        
        // Payment table
        Label tableLabel = new Label("Recent Transactions");
        tableLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        TableView<ObservableList<String>> table = createPaymentTable();
        
        view.getChildren().addAll(title, summaryBox, tableLabel, table);
        
        return view;
    }
    
    @SuppressWarnings({"unchecked"})
    private TableView<ObservableList<String>> createPaymentTable() {
        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(200);
        
        TableColumn<ObservableList<String>, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(100);
        dateCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get(0)));
        
        TableColumn<ObservableList<String>, String> studentCol = new TableColumn<>("Student");
        studentCol.setPrefWidth(150);
        studentCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get(1)));
        
        TableColumn<ObservableList<String>, String> amountCol = new TableColumn<>("Amount");
        amountCol.setPrefWidth(100);
        amountCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get(2)));
        
        TableColumn<ObservableList<String>, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get(3)));
        
        table.getColumns().addAll(dateCol, studentCol, amountCol, statusCol);
        
        // Add sample data
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        data.add(FXCollections.observableArrayList("2024-01-15", "John Smith", "$450", "Paid"));
        data.add(FXCollections.observableArrayList("2024-01-14", "Emma Watson", "$450", "Paid"));
        data.add(FXCollections.observableArrayList("2024-01-13", "Michael Brown", "$450", "Pending"));
        data.add(FXCollections.observableArrayList("2024-01-12", "Sarah Davis", "$450", "Paid"));
        data.add(FXCollections.observableArrayList("2024-01-11", "James Wilson", "$450", "Overdue"));
        table.setItems(data);
        
        return table;
    }
    
    private VBox createCard(String title, String content, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5;");
        card.setPrefHeight(100);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);
        
        Label contentLabel = new Label(content);
        contentLabel.setTextFill(Color.WHITE);
        contentLabel.setWrapText(true);
        
        card.getChildren().addAll(titleLabel, contentLabel);
        
        return card;
    }
    
    private VBox createStatsCard(String title, String value, String change, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        card.setPrefHeight(120);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 12));
        titleLabel.setTextFill(Color.GRAY);
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label changeLabel = new Label(change);
        changeLabel.setFont(Font.font("Arial", 11));
        changeLabel.setTextFill(Color.web(color));
        
        // Colored accent bar
        Rectangle accent = new Rectangle(50, 3);
        accent.setFill(Color.web(color));
        
        card.getChildren().addAll(titleLabel, valueLabel, changeLabel, accent);
        
        return card;
    }
    
    private VBox createActivitiesCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        
        Label title = new Label("Recent Activities");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        ListView<String> activitiesList = new ListView<>(recentActivities);
        activitiesList.setPrefHeight(150);
        
        card.getChildren().addAll(title, activitiesList);
        
        return card;
    }
    
    private VBox createChartCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        
        Label title = new Label("Monthly Occupancy");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Create a simple bar chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 200, 25);
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Room Occupancy (Last 6 Months)");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Occupied Rooms");
        
        series.getData().add(new XYChart.Data<>("Jan", 145));
        series.getData().add(new XYChart.Data<>("Feb", 148));
        series.getData().add(new XYChart.Data<>("Mar", 152));
        series.getData().add(new XYChart.Data<>("Apr", 150));
        series.getData().add(new XYChart.Data<>("May", 156));
        series.getData().add(new XYChart.Data<>("Jun", 155));
        
        barChart.getData().add(series);
        barChart.setPrefHeight(200);
        barChart.setLegendVisible(false);
        
        card.getChildren().addAll(title, barChart);
        
        return card;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5, 15, 5, 15));
        statusBar.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        
        statusLabel = new Label("Ready");
        statusLabel.setFont(Font.font("Arial", 11));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label versionLabel = new Label("v1.0.0");
        versionLabel.setFont(Font.font("Arial", 11));
        versionLabel.setTextFill(Color.GRAY);
        
        statusBar.getChildren().addAll(statusLabel, spacer, versionLabel);
        
        return statusBar;
    }
    
    private void initializeSampleData() {
        // Recent activities
        recentActivities.addAll(
            "10:30 AM - Student John Doe checked in",
            "09:45 AM - Room 205 payment received",
            "Yesterday - New student registered",
            "Yesterday - Maintenance request completed",
            "2 days ago - Room 101 cleaning done"
        );
        
        // Students data
        students.addAll(
            new StudentData("S001", "John Smith", "101", "Active"),
            new StudentData("S002", "Emma Watson", "102", "Active"),
            new StudentData("S003", "Michael Brown", "103", "Late Payment"),
            new StudentData("S004", "Sarah Davis", "104", "Active"),
            new StudentData("S005", "James Wilson", "105", "Inactive")
        );
    }
    
    private void startTimeUpdater() {
        Thread timeThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        dateTimeLabel.setText(now.format(formatter));
                    });
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        timeThread.setDaemon(true);
        timeThread.start();
    }
    
    private void showStudentDetails(StudentData student) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Details");
        alert.setHeaderText(student.getName());
        alert.setContentText("ID: " + student.getId() + "\nRoom: " + student.getRoom() + "\nStatus: " + student.getStatus());
        alert.showAndWait();
    }
    
    private void editStudent(StudentData student) {
        // This would open an edit form
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Edit Student");
        alert.setHeaderText("Edit " + student.getName());
        alert.setContentText("Edit form would open here");
        alert.showAndWait();
    }
    
    private void showRoomDetails(int roomNumber) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Room Details");
        alert.setHeaderText("Room " + roomNumber);
        alert.setContentText("Status: " + (roomNumber % 2 == 0 ? "Available" : "Occupied by John Doe") +
                           "\nCapacity: 2\nFacilities: Bed, Desk, Chair, Wardrobe");
        alert.showAndWait();
    }
    
    private void showNotImplemented(String page) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Not Implemented");
        alert.setHeaderText(null);
        alert.setContentText(page + " view is coming soon!");
        alert.showAndWait();
        
        statusLabel.setText(page + " view not yet implemented");
    }
    
    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to logout?");
        
        if (confirm.showAndWait().get() == ButtonType.OK) {
            // Clear logged-in user
            AuthController.logout();
            
            // Close dashboard
            primaryStage.close();
            
            System.out.println("Logged out");
            
            // Open login screen again
            try {
                LoginView login = new LoginView();
                Stage loginStage = new Stage();
                login.start(loginStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void stop() {
        System.out.println("DashboardView shutting down at " + LocalDateTime.now());
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}