package view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Admin;
import model.Student;

public class HeaderComponent {

    private Label userLabel;
    private Label dateTimeLabel;
    private Runnable onLogout;

    public HeaderComponent(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    public HBox getHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #2c3e50;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("🏨 HOSTEL MANAGEMENT SYSTEM");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);

        userLabel = new Label("Loading...");
        userLabel.setFont(Font.font("Arial", 14));
        userLabel.setTextFill(Color.LIGHTGRAY);

        dateTimeLabel = new Label();
        dateTimeLabel.setFont(Font.font("Arial", 14));
        dateTimeLabel.setTextFill(Color.LIGHTGRAY);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> onLogout.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleLabel, spacer, userLabel, dateTimeLabel, logoutBtn);
        return header;
    }

    public Label getDateTimeLabel() { return dateTimeLabel; }

    public void updateUser(Object currentUser) {
        if (userLabel == null) return;
        if (currentUser instanceof Admin admin) {
            userLabel.setText("Admin: " + admin.getName() + " (" + admin.getRole() + ")");
        } else if (currentUser instanceof Student student) {
            userLabel.setText("Student: " + student.getName());
        } else {
            userLabel.setText("Not logged in");
        }
    }
}