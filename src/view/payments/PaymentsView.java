package view.payments;

import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PaymentsView {

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("Payment Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        HBox summaryBox = new HBox(20);
        summaryBox.getChildren().addAll(
            buildSummaryBox("Total Collected",   "$124,500", "#3498db"),
            buildSummaryBox("Pending Payments",  "$12,300",  "#e74c3c")
        );

        Label tableLabel = new Label("Recent Transactions");
        tableLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        view.getChildren().addAll(title, summaryBox, tableLabel, buildPaymentTable());
        return view;
    }

    private VBox buildSummaryBox(String labelText, String amountText, String color) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5;");
        box.setPrefWidth(200);

        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);

        Label amount = new Label(amountText);
        amount.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        amount.setTextFill(Color.WHITE);

        box.getChildren().addAll(label, amount);
        return box;
    }

    @SuppressWarnings("unchecked")
    private TableView<ObservableList<String>> buildPaymentTable() {
        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(200);

        table.getColumns().addAll(
            makeCol("Date",    0, 100),
            makeCol("Student", 1, 150),
            makeCol("Amount",  2, 100),
            makeCol("Status",  3, 100)
        );

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        data.add(FXCollections.observableArrayList("2024-01-15", "John Smith",    "$450", "Paid"));
        data.add(FXCollections.observableArrayList("2024-01-14", "Emma Watson",   "$450", "Paid"));
        data.add(FXCollections.observableArrayList("2024-01-13", "Michael Brown", "$450", "Pending"));
        data.add(FXCollections.observableArrayList("2024-01-12", "Sarah Davis",   "$450", "Paid"));
        data.add(FXCollections.observableArrayList("2024-01-11", "James Wilson",  "$450", "Overdue"));
        table.setItems(data);

        return table;
    }

    private TableColumn<ObservableList<String>, String> makeCol(String title, int index, int width) {
        TableColumn<ObservableList<String>, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(cd.getValue().get(index)));
        return col;
    }
}
