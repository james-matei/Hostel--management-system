package view.students;

import controller.StudentDashboardController;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Payment;
import model.Student;

import java.util.function.Function;

public class MyPaymentsView {

    private final Student                      student;
    private final StudentDashboardController   controller;
    private final ObservableList<Payment>      payments;

    public MyPaymentsView(Student student, StudentDashboardController controller, ObservableList<Payment> payments) {
        this.student    = student;
        this.controller = controller;
        this.payments   = FXCollections.observableArrayList(
            controller.getStudentPayments(student.getId()) // always fresh from DB
        );
    }

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("My Payment History");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // ── Summary cards from real data ──────────────────────────────────────
        double totalPaid    = payments.stream().filter(p -> "Paid".equals(p.getStatus())).mapToDouble(Payment::getAmount).sum();
        double totalPending = payments.stream().filter(p -> "Pending".equals(p.getStatus())).mapToDouble(Payment::getAmount).sum();
        long   paidCount    = payments.stream().filter(p -> "Paid".equals(p.getStatus())).count();

        HBox summaryBox = new HBox(20);
        summaryBox.getChildren().addAll(
            summaryCard("Total Paid",     "KSh " + String.format("%,.0f", totalPaid),    "#27ae60"),
            summaryCard("Pending",        "KSh " + String.format("%,.0f", totalPending), "#e74c3c"),
            summaryCard("Paid Payments",  paidCount + " payment(s)",                      "#3498db")
        );

        // ── Pay button ────────────────────────────────────────────────────────
        Button payBtn = new Button("💳  Pay via M-Pesa");
        payBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                        "-fx-font-size: 13; -fx-cursor: hand; -fx-padding: 8 20; -fx-background-radius: 6;");
        payBtn.setOnAction(e -> new MpesaPaymentDialog(student).show());

        // ── Table ─────────────────────────────────────────────────────────────
        TableView<Payment> table = buildTable();

        // ── Search bar ────────────────────────────────────────────────────────
        HBox searchBar = buildSearchBar(table);

        view.getChildren().addAll(title, summaryBox, payBtn, searchBar, table);
        return view;
    }

    @SuppressWarnings("unchecked")
    private TableView<Payment> buildTable() {
        TableView<Payment> table = new TableView<>();
        table.setItems(payments);
        table.setPrefHeight(350);
        table.setPlaceholder(new Label("No payments found."));

        table.getColumns().addAll(
            makeCol("Date",           p -> p.getPaymentDate().toString()),
            makeCol("Amount (KSh)",   p -> String.format("%,.0f", p.getAmount())),
            makeCol("Status",         p -> p.getStatus()),
            makeCol("Method",         p -> p.getPaymentMethod() != null ? p.getPaymentMethod() : "—"),
            makeCol("Due Date",       p -> p.getDueDate() != null ? p.getDueDate().toString() : "—")
        );

        // Color code status column
        TableColumn<Payment, String> statusCol = (TableColumn<Payment, String>) table.getColumns().get(2);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setTextFill(switch (item) {
                    case "Paid"    -> Color.web("#27ae60");
                    case "Pending" -> Color.web("#f39c12");
                    case "Overdue" -> Color.web("#e74c3c");
                    default        -> Color.BLACK;
                });
            }
        });

        return table;
    }

    private TableColumn<Payment, String> makeCol(String title, Function<Payment, String> extractor) {
        TableColumn<Payment, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(extractor.apply(cd.getValue())));
        return col;
    }

    private HBox buildSearchBar(TableView<Payment> table) {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(5, 0, 5, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("Search by date or amount...");
        searchField.setPrefHeight(35);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All", "Paid", "Pending", "Overdue");
        filterCombo.setValue("All");
        filterCombo.setPrefWidth(120);

        Button searchBtn = new Button("🔍 Search");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        searchBtn.setOnAction(e -> {
            String text   = searchField.getText().toLowerCase();
            String filter = filterCombo.getValue();

            ObservableList<Payment> filtered = FXCollections.observableArrayList();
            for (Payment p : payments) {
                boolean matchSearch = text.isEmpty()
                    || p.getPaymentDate().toString().contains(text)
                    || String.valueOf(p.getAmount()).contains(text);
                boolean matchFilter = filter.equals("All") || p.getStatus().equals(filter);
                if (matchSearch && matchFilter) filtered.add(p);
            }
            table.setItems(filtered);
        });

        // Clear filter
        searchField.textProperty().addListener((obs, o, n) -> {
            if (n.isEmpty()) table.setItems(payments);
        });

        bar.getChildren().addAll(searchField, filterCombo, searchBtn);
        return bar;
    }

    private VBox summaryCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5;");
        card.setPrefWidth(180);

        Label t = new Label(title); t.setTextFill(Color.WHITE); t.setFont(Font.font("Arial", 12));
        Label v = new Label(value); v.setTextFill(Color.WHITE); v.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        card.getChildren().addAll(t, v);
        return card;
    }
}