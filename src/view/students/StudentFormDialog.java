package view.students;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Student;

/**
 * Reusable dialog for both Add and Edit student.
 * - Add mode : pass null  → empty form, admin sets username + password
 * - Edit mode: pass student → pre-filled, leave password blank to keep existing
 */
public class StudentFormDialog {

    private final Dialog<Student> dialog;
    private final boolean isEditMode;

    // Form fields
    private final TextField      nameField     = new TextField();
    private final TextField      ageField      = new TextField();
    private final TextField      courseField   = new TextField();
    private final TextField      roomField     = new TextField();
    private final TextField      emailField    = new TextField();
    private final TextField      phoneField    = new TextField();
    private final TextField      usernameField = new TextField();
    private final PasswordField  passwordField = new PasswordField();
    private final PasswordField  confirmField  = new PasswordField();
    private final ComboBox<String> statusCombo = new ComboBox<>();

    private final Label errorLabel = new Label();

    public StudentFormDialog(Student student) {
        this.isEditMode = (student != null);
        this.dialog     = new Dialog<>();
        setupDialog();
        setupForm();
        if (isEditMode) prefillForm(student);
    }

    // ── Dialog setup ──────────────────────────────────────────────────────────

    private void setupDialog() {
        dialog.setTitle(isEditMode ? "Edit Student" : "Add New Student");
        dialog.setHeaderText(isEditMode ? "Update student details" : "Enter new student details");

        ButtonType saveBtn   = new ButtonType("Save",   ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        // Block save if validation fails
        dialog.getDialogPane().lookupButton(saveBtn).addEventFilter(
            javafx.event.ActionEvent.ACTION, e -> {
                if (!validateForm()) e.consume();
            }
        );

        dialog.setResultConverter(btn -> {
            if (btn.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                return buildStudentFromForm();
            return null;
        });
    }

    // ── Form layout ───────────────────────────────────────────────────────────

    private void setupForm() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        nameField.setPromptText("Full name");
        ageField.setPromptText("Age");
        courseField.setPromptText("e.g. Computer Science");
        roomField.setPromptText("e.g. 101  (leave blank if unassigned)");
        emailField.setPromptText("student@email.com");
        phoneField.setPromptText("+254 700 000 000");
        usernameField.setPromptText("Login username");
        passwordField.setPromptText(isEditMode ? "Leave blank to keep current password" : "Set login password");
        confirmField.setPromptText("Confirm password");

        statusCombo.getItems().addAll("Active", "Inactive", "Suspended", "Graduated");
        statusCombo.setValue("Active");

        int row = 0;

        if (!isEditMode) {
            Label idNote = new Label("ℹ  Student ID will be auto-assigned by the system");
            idNote.setFont(Font.font("Arial", 11));
            idNote.setTextFill(Color.web("#7f8c8d"));
            grid.add(idNote, 0, row++, 2, 1);
        }

        // Personal details section
        grid.add(sectionLabel("Personal Details"), 0, row++, 2, 1);
        addRow(grid, "Full Name *",  nameField,   row++);
        addRow(grid, "Age *",        ageField,    row++);
        addRow(grid, "Course *",     courseField, row++);
        addRow(grid, "Room ID",      roomField,   row++);
        addRow(grid, "Email",        emailField,  row++);
        addRow(grid, "Phone",        phoneField,  row++);
        addRow(grid, "Status *",     statusCombo, row++);

        // Login credentials section
        grid.add(sectionLabel("Login Credentials"), 0, row++, 2, 1);
        addRow(grid, "Username *",   usernameField, row++);
        addRow(grid, isEditMode ? "New Password"     : "Password *",         passwordField, row++);
        addRow(grid, isEditMode ? "Confirm New Password" : "Confirm Password *", confirmField,  row++);

        if (isEditMode) {
            Label hint = new Label("ℹ  Leave password fields blank to keep the current password");
            hint.setFont(Font.font("Arial", 11));
            hint.setTextFill(Color.web("#7f8c8d"));
            hint.setWrapText(true);
            grid.add(hint, 0, row++, 2, 1);
        }

        // Error label
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 11));
        errorLabel.setWrapText(true);
        grid.add(errorLabel, 0, row, 2, 1);

        nameField.setPrefWidth(250);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(480);
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        l.setTextFill(Color.web("#2c3e50"));
        l.setPadding(new Insets(8, 0, 0, 0));
        return l;
    }

    private void addRow(GridPane grid, String labelText, Control field, int row) {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        label.setMinWidth(140);
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }

    // ── Prefill (edit mode) ───────────────────────────────────────────────────

    private void prefillForm(Student s) {
        nameField.setText(s.getName());
        ageField.setText(String.valueOf(s.getAge()));
        courseField.setText(s.getCourse()    != null ? s.getCourse()    : "");
        roomField.setText(s.getRoomId()      != null ? s.getRoomId()    : "");
        emailField.setText(s.getEmail()      != null ? s.getEmail()     : "");
        phoneField.setText(s.getPhone()      != null ? s.getPhone()     : "");
        usernameField.setText(s.getUsername()!= null ? s.getUsername()  : "");
        statusCombo.setValue(s.getStatus()   != null ? s.getStatus()    : "Active");
        // Password intentionally left blank — fill only to change it
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private boolean validateForm() {
        errorLabel.setText("");

        if (nameField.getText().trim().isEmpty()) {
            errorLabel.setText("❌ Full name is required.");
            return false;
        }
        if (ageField.getText().trim().isEmpty()) {
            errorLabel.setText("❌ Age is required.");
            return false;
        }
        try {
            int age = Integer.parseInt(ageField.getText().trim());
            if (age < 16 || age > 60) {
                errorLabel.setText("❌ Age must be between 16 and 60.");
                return false;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("❌ Age must be a valid number.");
            return false;
        }
        if (courseField.getText().trim().isEmpty()) {
            errorLabel.setText("❌ Course is required.");
            return false;
        }
        if (usernameField.getText().trim().isEmpty()) {
            errorLabel.setText("❌ Username is required.");
            return false;
        }

        String password = passwordField.getText();
        String confirm  = confirmField.getText();

        // Password required when adding
        if (!isEditMode) {
            if (password.isEmpty()) {
                errorLabel.setText("❌ Password is required.");
                return false;
            }
            if (password.length() < 6) {
                errorLabel.setText("❌ Password must be at least 6 characters.");
                return false;
            }
            if (!password.equals(confirm)) {
                errorLabel.setText("❌ Passwords do not match.");
                return false;
            }
        }

        // On edit, only validate if a new password was entered
        if (isEditMode && !password.isEmpty()) {
            if (password.length() < 6) {
                errorLabel.setText("❌ New password must be at least 6 characters.");
                return false;
            }
            if (!password.equals(confirm)) {
                errorLabel.setText("❌ Passwords do not match.");
                return false;
            }
        }

        return true;
    }

    // ── Build result ──────────────────────────────────────────────────────────

    private Student buildStudentFromForm() {
        Student s = new Student(
            "",  // ID assigned by DB
            nameField.getText().trim(),
            Integer.parseInt(ageField.getText().trim()),
            courseField.getText().trim()
        );
        s.setRoomId(roomField.getText().trim().isEmpty()   ? null : roomField.getText().trim());
        s.setEmail(emailField.getText().trim().isEmpty()    ? null : emailField.getText().trim());
        s.setPhone(phoneField.getText().trim().isEmpty()    ? null : phoneField.getText().trim());
        s.setStatus(statusCombo.getValue());
        s.setUsername(usernameField.getText().trim());

        // Null password on edit = keep existing; DAO handles this
        String password = passwordField.getText();
        s.setPassword(password.isEmpty() ? null : password);

        return s;
    }

    /** Show dialog and return Student, or null if cancelled */
    public Student show() {
        return dialog.showAndWait().orElse(null);
    }
}