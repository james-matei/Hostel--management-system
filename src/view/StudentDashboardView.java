package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import controller.AuthController;
import controller.StudentDashboardController;
import model.Student;
import model.Payment;

public class StudentDashboardView extends Application {

    
    
    private Stage primaryStage;
    private BorderPane mainLayout;
    private Label statusLabel;
    private Label userLabel;
    private Label dateTimeLabel;
    private Student currentStudent;
    private StudentDashboardController controller;
    
    // Data collections
    private ObservableList<Payment> payments = FXCollections.observableArrayList();
    private ObservableList<String> announcements = FXCollections.observableArrayList();
    public void setCurrentUser(Object user) {
    if (user instanceof Student) {
        this.currentStudent = (Student) user;
        
        // Update UI if it's already initialized
        if (userLabel != null) {
            userLabel.setText("Student: " + currentStudent.getName());
        }
        
        System.out.println("Student set in dashboard: " + currentStudent.getName());
    }
}

    @Override
    public void start(Stage primaryStage) {
        this.controller = new StudentDashboardController();
        
        // Get current user from AuthController
        Object user = AuthController.getCurrentUser();
        
        // Check if user is a Student
        if (!(user instanceof Student)) {
            showErrorAndRedirect("Access Denied", "Student access required.");
            return;
        }
        
        this.currentStudent = (Student) user;
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Hostel Management System - Student Dashboard");
        
        // Load student data
        loadStudentData();
        
        // Create main layout
        mainLayout = new BorderPane();
        
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
        
        // Start time updater
        startTimeUpdater();
        
        System.out.println("StudentDashboardView started for: " + currentStudent.getName());
    }
    
    private void loadStudentData() {
        // Load payments from controller
        List<Payment> studentPayments = controller.getStudentPayments(currentStudent.getId());
        payments.setAll(studentPayments);
        
        // Load announcements
        announcements.addAll(controller.getAnnouncements());
    }
    
    private void showErrorAndRedirect(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
        
        // Redirect to login
        try {
            LoginView login = new LoginView();
            login.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        
        // User info
        userLabel = new Label("Student: " + currentStudent.getName());
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
        
        // Navigation buttons for students
        String[][] navItems = {
            {"📊 Dashboard", "Dashboard"},
            {"🏠 My Room", "MyRoom"},
            {"💰 My Payments", "MyPayments"},
            {"📄 My Details", "MyDetails"},
            {"📢 Announcements", "Announcements"},
            {"⚙ Settings", "Settings"}
        };
        
        for (String[] item : navItems) {
            Button navButton = createNavButton(item[0], item[1]);
            sidebar.getChildren().add(navButton);
        }
        
        // Add logout button at bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);
        
        Button logoutBtn = createNavButton("🚪 Logout", "Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;");
        sidebar.getChildren().add(logoutBtn);
        
        return sidebar;
    }
    
    private Button createNavButton(String text, String action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
        btn.setUserData(action);
        
        // Hover effect
        btn.setOnMouseEntered(e -> 
            btn.setStyle("-fx-background-color: #3d566e; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> 
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;"));
        
        // Action based on button
        btn.setOnAction(e -> handleNavigation(btn.getUserData().toString()));
        
        return btn;
    }
    
    private void handleNavigation(String page) {
        System.out.println("Navigating to: " + page);
        statusLabel.setText("Loading " + page + "...");
        
        switch(page) {
            case "Dashboard":
                mainLayout.setCenter(createDashboardContent());
                break;
            case "MyRoom":
                mainLayout.setCenter(createMyRoomView());
                break;
            case "MyPayments":
                mainLayout.setCenter(createMyPaymentsView());
                break;
            case "MyDetails":
                mainLayout.setCenter(createMyDetailsView());
                break;
            case "Announcements":
                mainLayout.setCenter(createAnnouncementsView());
                break;
            case "Settings":
                mainLayout.setCenter(createSettingsView());
                break;
            case "Logout":
                logout();
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
        
        // Welcome card
        VBox welcomeCard = createCard("Welcome back, " + currentStudent.getName() + "!", 
            "Here's your hostel information for today.", 
            "#3498db");
        
        // Student info cards
        VBox roomCard = createStatsCard("Room Number", 
            currentStudent.getRoomId() != null ? currentStudent.getRoomId() : "Not Assigned", 
            getRoomStatus(), "#27ae60");
        
        VBox paymentCard = createStatsCard("Payment Status", 
            getPaymentStatus(), 
            "Next: " + getNextPaymentDate(), "#e67e22");
        
        VBox blockCard = createStatsCard("Hostel Block", 
            getHostelBlock(), 
            "Floor: " + getFloorNumber(), "#9b59b6");
        
        VBox dueCard = createStatsCard("Next Payment", 
            getNextPaymentAmount(), 
            "Due: " + getNextPaymentDate(), "#e74c3c");
        
        // Recent announcements
        VBox announcementsCard = createAnnouncementsPreview();
        
        // Recent payments
        VBox recentPaymentsCard = createRecentPaymentsPreview();
        
        // Add to grid
        grid.add(welcomeCard, 0, 0, 4, 1);
        grid.add(roomCard, 0, 1);
        grid.add(paymentCard, 1, 1);
        grid.add(blockCard, 2, 1);
        grid.add(dueCard, 3, 1);
        grid.add(announcementsCard, 0, 2, 2, 1);
        grid.add(recentPaymentsCard, 2, 2, 2, 1);
        
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
    
    private VBox createMyRoomView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        
        Label title = new Label("My Room Information");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Room details card
        VBox roomCard = new VBox(15);
        roomCard.setPadding(new Insets(25));
        roomCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");
        
        // Room header
        HBox roomHeader = new HBox(10);
        roomHeader.setAlignment(Pos.CENTER_LEFT);
        Label roomIcon = new Label("🏠");
        roomIcon.setFont(Font.font(32));
        Label roomNumber = new Label("Room " + (currentStudent.getRoomId() != null ? currentStudent.getRoomId() : "Not Assigned"));
        roomNumber.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        roomHeader.getChildren().addAll(roomIcon, roomNumber);
        
        // Room details grid
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(20);
        detailsGrid.setVgap(15);
        detailsGrid.setPadding(new Insets(10, 0, 0, 0));
        
        // Room details
        addDetailRow(detailsGrid, "Room Status:", getRoomStatus(), 0);
        addDetailRow(detailsGrid, "Room Type:", getRoomType(), 1);
        addDetailRow(detailsGrid, "Capacity:", getRoomCapacity(), 2);
        addDetailRow(detailsGrid, "Occupants:", getRoomOccupants(), 3);
        addDetailRow(detailsGrid, "Floor:", getFloorNumber(), 4);
        addDetailRow(detailsGrid, "Block:", getHostelBlock(), 5);
        
        // Facilities section
        Label facilitiesTitle = new Label("Facilities");
        facilitiesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        FlowPane facilitiesPane = new FlowPane();
        facilitiesPane.setHgap(10);
        facilitiesPane.setVgap(10);
        
        String[] facilities = {"🛏️ Bed", "🪑 Desk", "🪑 Chair", "👕 Wardrobe", "❄️ AC", "🚿 Attached Bathroom"};
        for (String facility : facilities) {
            Label facilityLabel = new Label(facility);
            facilityLabel.setPadding(new Insets(8, 15, 8, 15));
            facilityLabel.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 20;");
            facilitiesPane.getChildren().add(facilityLabel);
        }
        
        // Roommate section
        if (hasRoommate()) {
            Label roommateTitle = new Label("Roommate Information");
            roommateTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            
            HBox roommateBox = new HBox(15);
            roommateBox.setAlignment(Pos.CENTER_LEFT);
            roommateBox.setPadding(new Insets(10));
            roommateBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5;");
            
            Label roommateIcon = new Label("👤");
            roommateIcon.setFont(Font.font(24));
            
            VBox roommateInfo = new VBox(5);
            Label roommateName = new Label("John Doe");
            roommateName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            Label roommateCourse = new Label("Computer Science, Year 2");
            roommateCourse.setFont(Font.font("Arial", 12));
            roommateCourse.setTextFill(Color.GRAY);
            
            roommateInfo.getChildren().addAll(roommateName, roommateCourse);
            roommateBox.getChildren().addAll(roommateIcon, roommateInfo);
            
            roomCard.getChildren().addAll(roomHeader, detailsGrid, facilitiesTitle, facilitiesPane, roommateTitle, roommateBox);
        } else {
            roomCard.getChildren().addAll(roomHeader, detailsGrid, facilitiesTitle, facilitiesPane);
        }
        
        view.getChildren().addAll(title, roomCard);
        
        return view;
    }
    
    private VBox createMyPaymentsView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        
        Label title = new Label("My Payment History");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Payment summary cards
        HBox summaryBox = new HBox(20);
        
        VBox totalPaidBox = createSummaryCard("Total Paid", "$2,250", "#27ae60");
        VBox pendingBox = createSummaryCard("Pending", "$450", "#e74c3c");
        VBox nextDueBox = createSummaryCard("Next Due", "Mar 15, 2024", "#f39c12");
        
        summaryBox.getChildren().addAll(totalPaidBox, pendingBox, nextDueBox);
        
        // Search and filter
        HBox searchBar = new HBox(10);
        searchBar.setPadding(new Insets(10, 0, 10, 0));
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search payments by date or amount...");
        searchField.setPrefHeight(35);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All", "Paid", "Pending", "Overdue");
        filterCombo.setValue("All");
        filterCombo.setPrefWidth(120);
        
        Button searchBtn = new Button("🔍 Search");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        searchBar.getChildren().addAll(searchField, filterCombo, searchBtn);
        
        // Payments table
        TableView<Payment> table = new TableView<>();
        table.setItems(payments);
        
        TableColumn<Payment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        dateCol.setPrefWidth(120);
        
        TableColumn<Payment, Double> amountCol = new TableColumn<>("Amount ($)");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(100);
        
        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<Payment, String> methodCol = new TableColumn<>("Payment Method");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        methodCol.setPrefWidth(150);
        
        //table.getColumns().addAll(dateCol, amountCol, statusCol, methodCol);
        table.setPrefHeight(300);
        
        // Search functionality
        searchBtn.setOnAction(e -> {
            String searchText = searchField.getText().toLowerCase();
            String filter = filterCombo.getValue();
            
            ObservableList<Payment> filtered = FXCollections.observableArrayList();
            for (Payment p : controller.getStudentPayments(currentStudent.getId())) {
                boolean matchesSearch = searchText.isEmpty() || 
                    p.getPaymentDate().toString().contains(searchText) ||
                    String.valueOf(p.getAmount()).contains(searchText);
                
                boolean matchesFilter = filter.equals("All") || p.getStatus().equals(filter);
                
                if (matchesSearch && matchesFilter) {
                    filtered.add(p);
                }
            }
            table.setItems(filtered);
        });
        
        view.getChildren().addAll(title, summaryBox, searchBar, table);
        
        return view;
    }
    
    private VBox createMyDetailsView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        
        Label title = new Label("My Personal Details");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Student photo and basic info card
        HBox profileCard = new HBox(30);
        profileCard.setPadding(new Insets(25));
        profileCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");
        
        // Photo placeholder
        StackPane photoPlaceholder = new StackPane();
        photoPlaceholder.setPrefSize(120, 120);
        photoPlaceholder.setStyle("-fx-background-color: #3498db; -fx-background-radius: 60;");
        
        Label photoIcon = new Label("👤");
        photoIcon.setFont(Font.font(48));
        photoIcon.setTextFill(Color.WHITE);
        photoPlaceholder.getChildren().add(photoIcon);
        
        // Student info
        VBox infoBox = new VBox(10);
        
        Label nameLabel = new Label(currentStudent.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Details grid
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(20);
        detailsGrid.setVgap(10);
        detailsGrid.setPadding(new Insets(10, 0, 0, 0));
        
        addDetailRow(detailsGrid, "Student ID:", currentStudent.getId(), 0);
        addDetailRow(detailsGrid, "Programme:", currentStudent.getCourse(), 1);
        addDetailRow(detailsGrid, "Email:", currentStudent.getEmail() != null ? currentStudent.getEmail() : "Not provided", 2);
        addDetailRow(detailsGrid, "Phone:", currentStudent.getPhone() != null ? currentStudent.getPhone() : "Not provided", 3);
        addDetailRow(detailsGrid, "Hostel Block:", getHostelBlock(), 4);
        addDetailRow(detailsGrid, "Room Number:", currentStudent.getRoomId() != null ? currentStudent.getRoomId() : "Not assigned", 5);
        addDetailRow(detailsGrid, "Enrollment Date:", "2024-01-15", 6);
        addDetailRow(detailsGrid, "Status:", currentStudent.getStatus(), 7);
        
        infoBox.getChildren().addAll(nameLabel, detailsGrid);
        profileCard.getChildren().addAll(photoPlaceholder, infoBox);
        
        view.getChildren().addAll(title, profileCard);
        
        return view;
    }
    
    private VBox createAnnouncementsView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Hostel Announcements");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Announcements list
        ListView<String> announcementsList = new ListView<>(announcements);
        announcementsList.setPrefHeight(400);
        
        // Style the list cells
        announcementsList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox cellBox = new HBox(10);
                    cellBox.setPadding(new Insets(10));
                    cellBox.setAlignment(Pos.CENTER_LEFT);
                    
                    Label iconLabel = new Label("📢");
                    iconLabel.setFont(Font.font(16));
                    
                    VBox textBox = new VBox(3);
                    String[] parts = item.split("\\|", 2);
                    if (parts.length == 2) {
                        Label titleLabel = new Label(parts[0].trim());
                        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                        Label descLabel = new Label(parts[1].trim());
                        descLabel.setTextFill(Color.GRAY);
                        textBox.getChildren().addAll(titleLabel, descLabel);
                    } else {
                        textBox.getChildren().add(new Label(item));
                    }
                    
                    cellBox.getChildren().addAll(iconLabel, textBox);
                    setGraphic(cellBox);
                }
            }
        });
        
        view.getChildren().addAll(title, announcementsList);
        
        return view;
    }
    
    private VBox createSettingsView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Settings options
        VBox settingsBox = new VBox(10);
        settingsBox.setPadding(new Insets(20));
        settingsBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10;");
        
        String[][] settings = {
            {"🔔 Notifications", "Manage your notification preferences"},
            {"🔐 Change Password", "Update your account password"},
            {"📱 Contact Preferences", "Set how we contact you"},
            {"🌙 Dark Mode", "Toggle dark/light theme"},
            {"📧 Email Settings", "Configure email notifications"}
        };
        
        for (String[] setting : settings) {
            HBox settingRow = new HBox(15);
            settingRow.setAlignment(Pos.CENTER_LEFT);
            settingRow.setPadding(new Insets(10));
            settingRow.setStyle("-fx-border-color: transparent transparent #ecf0f1 transparent; -fx-border-width: 0 0 1 0;");
            
            Label iconLabel = new Label(setting[0].split(" ")[0]);
            iconLabel.setFont(Font.font(18));
            
            VBox textBox = new VBox(3);
            Label titleLabel = new Label(setting[0].substring(2));
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            Label descLabel = new Label(setting[1]);
            descLabel.setFont(Font.font("Arial", 11));
            descLabel.setTextFill(Color.GRAY);
            textBox.getChildren().addAll(titleLabel, descLabel);
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Button editBtn = new Button("Edit");
            editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
            
            settingRow.getChildren().addAll(iconLabel, textBox, spacer, editBtn);
            settingsBox.getChildren().add(settingRow);
        }
        
        view.getChildren().addAll(title, settingsBox);
        
        return view;
    }
    
    private VBox createAnnouncementsPreview() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        
        Label title = new Label("📢 Latest Announcements");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        ListView<String> previewList = new ListView<>();
        ObservableList<String> previewAnnouncements = FXCollections.observableArrayList();
        
        if (announcements.size() > 3) {
            previewAnnouncements.addAll(announcements.subList(0, 3));
        } else {
            previewAnnouncements.addAll(announcements);
        }
        
        previewList.setItems(previewAnnouncements);
        previewList.setPrefHeight(120);
        
        Button viewAllBtn = new Button("View All Announcements");
        viewAllBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        viewAllBtn.setMaxWidth(Double.MAX_VALUE);
        viewAllBtn.setOnAction(e -> handleNavigation("Announcements"));
        
        card.getChildren().addAll(title, previewList, viewAllBtn);
        
        return card;
    }
    
    private VBox createRecentPaymentsPreview() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        
        Label title = new Label("💰 Recent Payments");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Simple table for preview
        GridPane previewGrid = new GridPane();
        previewGrid.setHgap(10);
        previewGrid.setVgap(5);
        
        // Headers
        Label dateHeader = new Label("Date");
        dateHeader.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        Label amountHeader = new Label("Amount");
        amountHeader.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        Label statusHeader = new Label("Status");
        statusHeader.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        
        previewGrid.add(dateHeader, 0, 0);
        previewGrid.add(amountHeader, 1, 0);
        previewGrid.add(statusHeader, 2, 0);
        
        // Sample data
        int row = 1;
        for (int i = 0; i < Math.min(3, payments.size()); i++) {
            Payment p = payments.get(i);
            previewGrid.add(new Label(p.getPaymentDate().toString()), 0, row);
            previewGrid.add(new Label("$" + p.getAmount()), 1, row);
            
            Label statusLabel = new Label(p.getStatus());
            if (p.getStatus().equals("Paid")) {
                statusLabel.setTextFill(Color.GREEN);
            } else if (p.getStatus().equals("Pending")) {
                statusLabel.setTextFill(Color.ORANGE);
            } else {
                statusLabel.setTextFill(Color.RED);
            }
            
            previewGrid.add(statusLabel, 2, row);
            row++;
        }
        
        Button viewAllBtn = new Button("View All Payments");
        viewAllBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        viewAllBtn.setMaxWidth(Double.MAX_VALUE);
        viewAllBtn.setOnAction(e -> handleNavigation("MyPayments"));
        
        card.getChildren().addAll(title, previewGrid, viewAllBtn);
        
        return card;
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
    
    private VBox createSummaryCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5;");
        card.setPrefWidth(150);
        
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Arial", 12));
        
        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        card.getChildren().addAll(titleLabel, valueLabel);
        
        return card;
    }
    
    private void addDetailRow(GridPane grid, String label, String value, int row) {
        Label labelCol = new Label(label);
        labelCol.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        labelCol.setTextFill(Color.GRAY);
        
        Label valueCol = new Label(value);
        valueCol.setFont(Font.font("Arial", 12));
        
        grid.add(labelCol, 0, row);
        grid.add(valueCol, 1, row);
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
        
        Label versionLabel = new Label("v1.0.0 - Student Portal");
        versionLabel.setFont(Font.font("Arial", 11));
        versionLabel.setTextFill(Color.GRAY);
        
        statusBar.getChildren().addAll(statusLabel, spacer, versionLabel);
        
        return statusBar;
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
            AuthController.logout();
            primaryStage.close();
            
            try {
                LoginView login = new LoginView();
                Stage loginStage = new Stage();
                login.start(loginStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // Helper methods for student data
    private String getRoomStatus() {
        return currentStudent.getRoomId() != null ? "Occupied" : "Not Assigned";
    }
    
    private String getPaymentStatus() {
        if (payments.isEmpty()) return "No payments";
        Payment latest = payments.get(0);
        return latest.getStatus();
    }
    
    private String getNextPaymentDate() {
        return "Mar 15, 2024";
    }
    
    private String getNextPaymentAmount() {
        return "$450.00";
    }
    
    private String getHostelBlock() {
        String roomId = currentStudent.getRoomId();
        if (roomId == null) return "Not Assigned";
        
        if (roomId.startsWith("1")) return "Block A";
        if (roomId.startsWith("2")) return "Block B";
        if (roomId.startsWith("3")) return "Block C";
        return "Block A";
    }
    
    private String getFloorNumber() {
        String roomId = currentStudent.getRoomId();
        if (roomId == null) return "N/A";
        
        return String.valueOf(roomId.charAt(0));
    }
    
    private String getRoomType() {
        return "Shared Double Room";
    }
    
    private String getRoomCapacity() {
        return "2 persons";
    }
    
    private String getRoomOccupants() {
        return hasRoommate() ? "2/2" : "1/2";
    }
    
    private boolean hasRoommate() {
        return currentStudent.getRoomId() != null && 
               (currentStudent.getRoomId().equals("101") || 
                currentStudent.getRoomId().equals("102"));
    }
    
    @Override
    public void stop() {
        System.out.println("StudentDashboardView shutting down at " + LocalDateTime.now());
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}