package view.students;

import controller.StudentDashboardController;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Payment;
import model.Student;

public class MyPaymentsView {

    private Student student;
    private StudentDashboardController controller;
    private ObservableList<Payment> payments;

    public MyPaymentsView(Student student, StudentDashboardController controller, ObservableList<Payment> payments) {
        this.student    = student;
        this.controller = controller;
        this.payments   = payments;
    }

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("My Payment History");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        HBox summaryBox = new HBox(20);
        summaryBox.getChildren().addAll(
            summaryCard("Total Paid", "$2,250",       "#27ae60"),
            summaryCard("Pending",    "$450",          "#e74c3c"),
            summaryCard("Next Due",   "Mar 15, 2024",  "#f39c12")
        );

        TableView<Payment> table = buildTable();
        HBox searchBar = buildSearchBar(table);
        wireSearch(searchBar, table);

        view.getChildren().addAll(title, summaryBox, searchBar, table);
        return view;
    }

    @SuppressWarnings("unchecked")
    private TableView<Payment> buildTable() {
        TableView<Payment> table = new TableView<>();
        table.setItems(payments);
        table.setPrefHeight(300);

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

        table.getColumns().addAll(dateCol, amountCol, statusCol, methodCol);
        return table;
    }

    private HBox buildSearchBar(TableView<Payment> table) {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(10, 0, 10, 0));

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

        bar.getChildren().addAll(searchField, filterCombo, searchBtn);
        return bar;
    }

    private void wireSearch(HBox searchBar, TableView<Payment> table) {
        TextField searchField = (TextField) searchBar.getChildren().get(0);
        ComboBox<String> filterCombo = (ComboBox<String>) searchBar.getChildren().get(1);
        Button searchBtn = (Button) searchBar.getChildren().get(2);

        searchBtn.setOnAction(e -> {
            String text   = searchField.getText().toLowerCase();
            String filter = filterCombo.getValue();

            ObservableList<Payment> filtered = FXCollections.observableArrayList();
            for (Payment p : controller.getStudentPayments(student.getId())) {
                boolean matchSearch = text.isEmpty()
                    || p.getPaymentDate().toString().contains(text)
                    || String.valueOf(p.getAmount()).contains(text);
                boolean matchFilter = filter.equals("All") || p.getStatus().equals(filter);
                if (matchSearch && matchFilter) filtered.add(p);
            }
            table.setItems(filtered);
        });
    }

    private VBox summaryCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5;");
        card.setPrefWidth(150);

        Label t = new Label(title); t.setTextFill(Color.WHITE); t.setFont(Font.font("Arial", 12));
        Label v = new Label(value); v.setTextFill(Color.WHITE); v.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        card.getChildren().addAll(t, v);
        return card;
    }
}
