package view.students;

import dao.StudentDao;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Student;
import java.util.function.Function;

public class StudentsView {

    private final ObservableList<Student> students;
    private final StudentDao dao = new StudentDao();

    public StudentsView(ObservableList<Student> students) {
        this.students = students;
        loadFromDatabase();
    }

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("Student Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        TableView<Student> table = buildTable();
        HBox searchBar = buildSearchBar(table);

        view.getChildren().addAll(title, searchBar, table);
        return view;
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    private void loadFromDatabase() {
        students.setAll(dao.getAllStudents());
    }

    // ── Search Bar ────────────────────────────────────────────────────────────

    private HBox buildSearchBar(TableView<Student> table) {
        HBox bar = new HBox(10);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or ID...");
        searchField.setPrefHeight(35);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) table.setItems(students);
        });

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        searchBtn.setOnAction(e -> filterTable(table, searchField.getText()));

        Button addBtn = new Button("+ Add Student");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
        addBtn.setOnAction(e -> handleAdd(table));

        bar.getChildren().addAll(searchField, searchBtn, addBtn);
        return bar;
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private TableView<Student> buildTable() {
        TableView<Student> table = new TableView<>();
        table.setPrefHeight(450);
        table.setItems(students);
        table.setPlaceholder(new Label("No students found."));

        table.getColumns().addAll(
            makeCol("ID",       s -> s.getId()),
            makeCol("Name",     s -> s.getName()),
            makeCol("Course",   s -> s.getCourse()   != null ? s.getCourse()   : "-"),
            makeCol("Room",     s -> s.getRoomId()   != null ? s.getRoomId()   : "Unassigned"),
            makeCol("Username", s -> s.getUsername() != null ? s.getUsername() : "-"),
            makeCol("Status",   s -> s.getStatus()   != null ? s.getStatus()   : "-"),
            buildActionColumn(table)
        );

        return table;
    }

    private TableColumn<Student, String> makeCol(String title, Function<Student, String> extractor) {
        TableColumn<Student, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(extractor.apply(cd.getValue())));
        return col;
    }

    private TableColumn<Student, Void> buildActionColumn(TableView<Student> table) {
        TableColumn<Student, Void> col = new TableColumn<>("Actions");
        col.setPrefWidth(200);
        col.setCellFactory(c -> new TableCell<>() {
            private final Button viewBtn   = styledBtn("View",   "#3498db");
            private final Button editBtn   = styledBtn("Edit",   "#f39c12");
            private final Button deleteBtn = styledBtn("Delete", "#e74c3c");
            private final HBox pane        = new HBox(5, viewBtn, editBtn, deleteBtn);

            {
                viewBtn.setOnAction(e ->
                    handleView(getTableView().getItems().get(getIndex())));
                editBtn.setOnAction(e ->
                    handleEdit(getTableView().getItems().get(getIndex()), table));
                deleteBtn.setOnAction(e ->
                    handleDelete(getTableView().getItems().get(getIndex()), table));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
        return col;
    }

    // ── CRUD Handlers ─────────────────────────────────────────────────────────

    private void handleAdd(TableView<Student> table) {
        Student result = new StudentFormDialog(null).show();
        if (result == null) return;

        Student saved = dao.addStudent(result);
        if (saved != null) {
            students.add(saved);
            table.refresh();
            showSuccess("Student '" + saved.getName() + "' added.\n" +
                        "ID: " + saved.getId() + "\n" +
                        "Username: " + saved.getUsername());
        } else {
            showError("Failed to add student. Please try again.");
        }
    }

    private void handleEdit(Student student, TableView<Student> table) {
        Student result = new StudentFormDialog(student).show();
        if (result == null) return;

        result.setId(student.getId()); // preserve original ID

        boolean success = dao.updateStudent(result);
        if (success) {
            int index = students.indexOf(student);
            if (index >= 0) students.set(index, result);
            table.refresh();
            showSuccess("Student updated successfully.");
        } else {
            showError("Failed to update student. Please try again.");
        }
    }

    private void handleDelete(Student student, TableView<Student> table) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Student");
        confirm.setHeaderText("Delete " + student.getName() + "?");
        confirm.setContentText("This cannot be undone.");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean success = dao.deleteStudent(student.getId());
                if (success) {
                    students.remove(student);
                    table.refresh();
                    showSuccess("Student deleted successfully.");
                } else {
                    showError("Failed to delete student. Please try again.");
                }
            }
        });
    }

    private void handleView(Student s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Details");
        alert.setHeaderText(s.getName() + "  |  ID: " + s.getId());
        alert.setContentText(
            "Course   : " + (s.getCourse()   != null ? s.getCourse()   : "—") + "\n" +
            "Room     : " + (s.getRoomId()   != null ? s.getRoomId()   : "Unassigned") + "\n" +
            "Status   : " + (s.getStatus()   != null ? s.getStatus()   : "—") + "\n" +
            "Username : " + (s.getUsername() != null ? s.getUsername() : "—") + "\n" +
            "Email    : " + (s.getEmail()    != null ? s.getEmail()    : "—") + "\n" +
            "Phone    : " + (s.getPhone()    != null ? s.getPhone()    : "—")
        );
        alert.showAndWait();
    }

    // ── Filter ────────────────────────────────────────────────────────────────

    private void filterTable(TableView<Student> table, String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            table.setItems(students);
            return;
        }
        String lower = searchText.toLowerCase();
        ObservableList<Student> filtered = FXCollections.observableArrayList();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(lower) ||
                s.getId().toLowerCase().contains(lower))
                filtered.add(s);
        }
        table.setItems(filtered);
    }

    // ── UI Helpers ────────────────────────────────────────────────────────────

    private Button styledBtn(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-cursor: hand;");
        return btn;
    }

    private void showSuccess(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Success");
        a.setHeaderText(null);
        a.setContentText("✅ " + message);
        a.showAndWait();
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText("❌ " + message);
        a.showAndWait();
    }
}