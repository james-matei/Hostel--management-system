package view.payments;

import dao.PaymentDao;
import dao.StudentDao;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Payment;
import model.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class PaymentsView {

    private final PaymentDao paymentDao = new PaymentDao();
    private final StudentDao studentDao = new StudentDao();
    private ObservableList<Payment> payments = FXCollections.observableArrayList();

    public VBox getView() {
        loadFromDatabase();

        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("Payment Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        double totalCollected = payments.stream().filter(p -> "Paid".equals(p.getStatus())).mapToDouble(Payment::getAmount).sum();
        double totalPending   = payments.stream().filter(p -> "Pending".equals(p.getStatus())).mapToDouble(Payment::getAmount).sum();

        HBox summaryBox = new HBox(20);
        summaryBox.getChildren().addAll(
            buildSummaryCard("Total Collected", "KSh " + String.format("%,.0f", totalCollected), "#27ae60"),
            buildSummaryCard("Pending",         "KSh " + String.format("%,.0f", totalPending),   "#e74c3c"),
            buildSummaryCard("Total Records",   payments.size() + " payments",                    "#3498db")
        );

        TableView<Payment> table = buildTable();
        HBox controls = buildControls(table);

        view.getChildren().addAll(title, summaryBox, controls, table);
        return view;
    }

    private void loadFromDatabase() {
        payments.setAll(paymentDao.getAllPayments());
    }

    private HBox buildControls(TableView<Payment> table) {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by student ID or payment ID...");
        searchField.setPrefHeight(35);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All", "Paid", "Pending", "Overdue");
        filterCombo.setValue("All");
        filterCombo.setPrefWidth(120);

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        searchBtn.setOnAction(e -> filterTable(table, searchField.getText(), filterCombo.getValue()));

        searchField.textProperty().addListener((obs, o, n) -> {
            if (n.isEmpty()) table.setItems(payments);
        });

        Button recordBtn = new Button("+ Record Payment");
        recordBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
        recordBtn.setOnAction(e -> showRecordPaymentDialog(table));

        Button refreshBtn = new Button("↻ Refresh");
        refreshBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> { loadFromDatabase(); table.setItems(payments); });

        bar.getChildren().addAll(searchField, filterCombo, searchBtn, recordBtn, refreshBtn);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private TableView<Payment> buildTable() {
        TableView<Payment> table = new TableView<>();
        table.setItems(payments);
        table.setPrefHeight(420);
        table.setPlaceholder(new Label("No payments recorded yet."));

        table.getColumns().addAll(
            makeCol("Payment ID",   p -> p.getPaymentId()),
            makeCol("Student ID",   p -> p.getStudentId()),
            makeCol("Date",         p -> p.getPaymentDate().toString()),
            makeCol("Amount (KSh)", p -> String.format("%,.0f", p.getAmount())),
            makeCol("Method",       p -> p.getPaymentMethod() != null ? p.getPaymentMethod() : "—"),
            makeCol("Status",       p -> p.getStatus()),
            makeCol("Due Date",     p -> p.getDueDate() != null ? p.getDueDate().toString() : "—")
        );

        // Color code status
        TableColumn<Payment, String> statusCol = (TableColumn<Payment, String>) table.getColumns().get(5);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
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

    private void showRecordPaymentDialog(TableView<Payment> table) {
        List<Student> allStudents = studentDao.getAllStudents();
        if (allStudents.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Students", "No students found in the system.");
            return;
        }

        Dialog<Payment> dialog = new Dialog<>();
        dialog.setTitle("Record Payment");
        dialog.setHeaderText("Record a manual payment for a student");

        ButtonType saveBtn   = new ButtonType("Save",   ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        ComboBox<Student> studentCombo = new ComboBox<>();
        studentCombo.getItems().addAll(allStudents);
        studentCombo.setPromptText("Select student");
        studentCombo.setPrefWidth(250);
        studentCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getName() + "  (ID: " + s.getId() + ")");
            }
        });
        studentCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getName() + "  (ID: " + s.getId() + ")");
            }
        });

        TextField amountField = new TextField();
        amountField.setPromptText("e.g. 10000");

        ComboBox<String> methodCombo = new ComboBox<>();
        methodCombo.getItems().addAll("Cash", "Bank Transfer", "M-Pesa", "Cheque");
        methodCombo.setValue("Cash");

        DatePicker datePicker    = new DatePicker(LocalDate.now());
        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Optional");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Paid", "Pending");
        statusCombo.setValue("Paid");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 11));

        int row = 0;
        addRow(grid, "Student *",        studentCombo,  row++);
        addRow(grid, "Amount (KSh) *",   amountField,   row++);
        addRow(grid, "Payment Method *", methodCombo,   row++);
        addRow(grid, "Payment Date *",   datePicker,    row++);
        addRow(grid, "Due Date",         dueDatePicker, row++);
        addRow(grid, "Status *",         statusCombo,   row++);
        grid.add(errorLabel, 0, row, 2, 1);

        dialog.getDialogPane().lookupButton(saveBtn).addEventFilter(
            javafx.event.ActionEvent.ACTION, e -> {
                errorLabel.setText("");
                if (studentCombo.getValue() == null) {
                    errorLabel.setText("❌ Please select a student."); e.consume(); return;
                }
                if (amountField.getText().trim().isEmpty()) {
                    errorLabel.setText("❌ Amount is required."); e.consume(); return;
                }
                try {
                    double amt = Double.parseDouble(amountField.getText().trim());
                    if (amt <= 0) { errorLabel.setText("❌ Amount must be greater than 0."); e.consume(); }
                } catch (NumberFormatException ex) {
                    errorLabel.setText("❌ Invalid amount — enter a number."); e.consume();
                }
            }
        );

        dialog.setResultConverter(btn -> {
            if (btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                Payment p = new Payment(
                    "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    studentCombo.getValue().getId(),
                    Double.parseDouble(amountField.getText().trim()),
                    datePicker.getValue(),
                    statusCombo.getValue(),
                    methodCombo.getValue()
                );
                if (dueDatePicker.getValue() != null) p.setDueDate(dueDatePicker.getValue());
                return p;
            }
            return null;
        });

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(450);

        dialog.showAndWait().ifPresent(payment -> {
            boolean saved = paymentDao.addPayment(payment);
            if (saved) {
                payments.add(0, payment);
                table.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Payment recorded.\nPayment ID: " + payment.getPaymentId());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save payment. Please try again.");
            }
        });
    }

    private void filterTable(TableView<Payment> table, String search, String filter) {
        String lower = search.toLowerCase();
        ObservableList<Payment> filtered = FXCollections.observableArrayList();
        for (Payment p : payments) {
            boolean matchSearch = lower.isEmpty()
                || p.getStudentId().toLowerCase().contains(lower)
                || p.getPaymentId().toLowerCase().contains(lower)
                || p.getPaymentDate().toString().contains(lower);
            boolean matchFilter = filter.equals("All") || p.getStatus().equals(filter);
            if (matchSearch && matchFilter) filtered.add(p);
        }
        table.setItems(filtered);
    }

    private VBox buildSummaryCard(String title, String value, String color) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5;");
        box.setPrefWidth(200);
        Label t = new Label(title); t.setTextFill(Color.WHITE); t.setFont(Font.font("Arial", 12));
        Label v = new Label(value); v.setTextFill(Color.WHITE); v.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        box.getChildren().addAll(t, v);
        return box;
    }

    private void addRow(GridPane grid, String labelText, Control field, int row) {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        label.setMinWidth(130);
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(content);
        a.showAndWait();
    }
}