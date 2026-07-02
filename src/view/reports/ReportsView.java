package view.reports;

import dao.PaymentDao;
import dao.RoomDao;
import dao.StudentDao;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Payment;
import model.Room;
import model.Student;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsView {

    private final StudentDao studentDao = new StudentDao();
    private final PaymentDao paymentDao = new PaymentDao();
    private final RoomDao    roomDao    = new RoomDao();

    public VBox getView() {
        // Load data
        List<Student> students = studentDao.getAllStudents();
        List<Payment> payments = paymentDao.getAllPayments();
        List<Room>    rooms    = roomDao.getAllRooms();

        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        // ── Title ─────────────────────────────────────────────────────────────
        Label title = new Label("📋 Reports & Analytics");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Label generated = new Label("Generated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        generated.setFont(Font.font("Arial", 12));
        generated.setTextFill(Color.GRAY);

        // ── Summary cards ─────────────────────────────────────────────────────
        HBox summaryRow = buildSummaryRow(students, payments, rooms);

        // ── Charts row ────────────────────────────────────────────────────────
        HBox chartsRow = new HBox(20);
        chartsRow.getChildren().addAll(
            buildPaymentStatusChart(payments),
            buildRoomOccupancyChart(rooms)
        );

        // ── Student status breakdown ──────────────────────────────────────────
        VBox studentBreakdown = buildStudentBreakdown(students);

        // ── Recent payments table ─────────────────────────────────────────────
        VBox recentPayments = buildRecentPaymentsTable(payments);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox content = new VBox(20, title, generated, summaryRow, chartsRow, studentBreakdown, recentPayments);
        content.setPadding(new Insets(0, 0, 20, 0));
        scroll.setContent(content);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        view.getChildren().add(scroll);
        return view;
    }

    // ── Summary Cards ─────────────────────────────────────────────────────────

    private HBox buildSummaryRow(List<Student> students, List<Payment> payments, List<Room> rooms) {
        long   totalStudents   = students.size();
        long   activeStudents  = students.stream().filter(s -> "Active".equals(s.getStatus())).count();
        double totalRevenue    = payments.stream().filter(p -> "Paid".equals(p.getStatus())).mapToDouble(Payment::getAmount).sum();
        double pendingRevenue  = payments.stream().filter(p -> "Pending".equals(p.getStatus())).mapToDouble(Payment::getAmount).sum();
        long   fullRooms       = rooms.stream().filter(Room::isFull).count();
        long   emptyRooms      = rooms.stream().filter(Room::isEmpty).count();
        int    occupancyPct    = rooms.isEmpty() ? 0 : (int) (fullRooms * 100 / rooms.size());

        HBox row = new HBox(15);
        row.getChildren().addAll(
            summaryCard("Total Students",   String.valueOf(totalStudents),                    "#3498db"),
            summaryCard("Active Students",  String.valueOf(activeStudents),                   "#27ae60"),
            summaryCard("Total Revenue",    "KSh " + String.format("%,.0f", totalRevenue),   "#9b59b6"),
            summaryCard("Pending Revenue",  "KSh " + String.format("%,.0f", pendingRevenue), "#e74c3c"),
            summaryCard("Occupancy Rate",   occupancyPct + "%",                               "#e67e22"),
            summaryCard("Empty Rooms",      String.valueOf(emptyRooms),                       "#1abc9c")
        );
        return row;
    }

    private VBox summaryCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setPrefWidth(160);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 8;");

        Label t = new Label(title); t.setTextFill(Color.WHITE); t.setFont(Font.font("Arial", 11));
        Label v = new Label(value); v.setTextFill(Color.WHITE); v.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        card.getChildren().addAll(t, v);
        return card;
    }

    // ── Payment Status Pie Chart ──────────────────────────────────────────────

    private VBox buildPaymentStatusChart(List<Payment> payments) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(350);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8;");

        Label title = new Label("Payment Status Breakdown");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        long paid    = payments.stream().filter(p -> "Paid".equals(p.getStatus())).count();
        long pending = payments.stream().filter(p -> "Pending".equals(p.getStatus())).count();
        long overdue = payments.stream().filter(p -> "Overdue".equals(p.getStatus())).count();

        PieChart chart = new PieChart();
        chart.setPrefHeight(200);
        chart.setLegendVisible(true);

        if (paid > 0)    chart.getData().add(new PieChart.Data("Paid ("    + paid    + ")", paid));
        if (pending > 0) chart.getData().add(new PieChart.Data("Pending (" + pending + ")", pending));
        if (overdue > 0) chart.getData().add(new PieChart.Data("Overdue (" + overdue + ")", overdue));

        if (chart.getData().isEmpty()) {
            Label none = new Label("No payment data yet.");
            none.setTextFill(Color.GRAY);
            card.getChildren().addAll(title, none);
        } else {
            card.getChildren().addAll(title, chart);
        }
        return card;
    }

    // ── Room Occupancy Bar Chart ──────────────────────────────────────────────

    private VBox buildRoomOccupancyChart(List<Room> rooms) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(350);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8;");

        Label title = new Label("Room Occupancy by Floor");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        // Group rooms by floor
        Map<Integer, List<Room>> byFloor = rooms.stream().collect(Collectors.groupingBy(Room::getFloor));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Floor");
        yAxis.setLabel("Rooms");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(true);
        chart.setPrefHeight(200);

        XYChart.Series<String, Number> fullSeries  = new XYChart.Series<>(); fullSeries.setName("Full");
        XYChart.Series<String, Number> partSeries  = new XYChart.Series<>(); partSeries.setName("Partial");
        XYChart.Series<String, Number> emptySeries = new XYChart.Series<>(); emptySeries.setName("Empty");

        byFloor.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            String floor = "Floor " + entry.getKey();
            List<Room> floorRooms = entry.getValue();
            fullSeries.getData().add(new XYChart.Data<>(floor, floorRooms.stream().filter(Room::isFull).count()));
            partSeries.getData().add(new XYChart.Data<>(floor, floorRooms.stream().filter(r -> !r.isEmpty() && !r.isFull()).count()));
            emptySeries.getData().add(new XYChart.Data<>(floor, floorRooms.stream().filter(Room::isEmpty).count()));
        });

        chart.getData().addAll(fullSeries, partSeries, emptySeries);
        card.getChildren().addAll(title, chart);
        return card;
    }

    // ── Student Status Breakdown ──────────────────────────────────────────────

    private VBox buildStudentBreakdown(List<Student> students) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8;");

        Label title = new Label("Student Status Breakdown");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Map<String, Long> byStatus = students.stream()
            .collect(Collectors.groupingBy(s -> s.getStatus() != null ? s.getStatus() : "Unknown", Collectors.counting()));

        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        byStatus.forEach((status, count) -> {
            String color = switch (status) {
                case "Active"    -> "#27ae60";
                case "Inactive"  -> "#95a5a6";
                case "Suspended" -> "#e74c3c";
                case "Graduated" -> "#3498db";
                default          -> "#7f8c8d";
            };
            VBox chip = new VBox(3);
            chip.setPadding(new Insets(10, 15, 10, 15));
            chip.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 6;");
            chip.setAlignment(Pos.CENTER);
            Label s = new Label(status);   s.setTextFill(Color.WHITE); s.setFont(Font.font("Arial", 11));
            Label c = new Label(String.valueOf(count)); c.setTextFill(Color.WHITE); c.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            chip.getChildren().addAll(c, s);
            row.getChildren().add(chip);
        });

        if (row.getChildren().isEmpty()) {
            row.getChildren().add(new Label("No student data yet."));
        }

        card.getChildren().addAll(title, row);
        return card;
    }

    // ── Recent Payments Table ─────────────────────────────────────────────────

    private VBox buildRecentPaymentsTable(List<Payment> payments) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8;");

        Label title = new Label("Recent Payments (Last 10)");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        TableView<Payment> table = new TableView<>();
        table.setPrefHeight(220);
        table.setPlaceholder(new Label("No payments yet."));

        ObservableList<Payment> recent = FXCollections.observableArrayList(
            payments.stream().limit(10).collect(Collectors.toList())
        );
        table.setItems(recent);

        table.getColumns().addAll(
            makeCol("Date",         p -> p.getPaymentDate().toString()),
            makeCol("Student ID",   p -> p.getStudentId()),
            makeCol("Amount (KSh)", p -> String.format("%,.0f", p.getAmount())),
            makeCol("Method",       p -> p.getPaymentMethod() != null ? p.getPaymentMethod() : "—"),
            makeCol("Status",       p -> p.getStatus())
        );

        card.getChildren().addAll(title, table);
        return card;
    }

    private TableColumn<Payment, String> makeCol(String title, java.util.function.Function<Payment, String> extractor) {
        TableColumn<Payment, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(extractor.apply(cd.getValue())));
        return col;
    }
}