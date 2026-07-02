
package view.students;

import javafx.geometry.Insets;
//import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Student;

public class MyDetailsView {

    private Student student;

    public MyDetailsView(Student student) {
        this.student = student;
    }

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("My Personal Details");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        view.getChildren().addAll(title, buildProfileCard());
        return view;
    }

    private HBox buildProfileCard() {
        HBox card = new HBox(30);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                      "-fx-border-color: #ddd; -fx-border-radius: 10; " +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");

        card.getChildren().addAll(buildPhotoPlaceholder(), buildInfoBox());
        return card;
    }

    private StackPane buildPhotoPlaceholder() {
        StackPane photo = new StackPane();
        photo.setPrefSize(120, 120);
        photo.setStyle("-fx-background-color: #3498db; -fx-background-radius: 60;");

        Label icon = new Label("👤");
        icon.setFont(Font.font(48));
        icon.setTextFill(Color.WHITE);
        photo.getChildren().add(icon);
        return photo;
    }

    private VBox buildInfoBox() {
        VBox box = new VBox(10);

        Label name = new Label(student.getName());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 0, 0, 0));

        addRow(grid, "Student ID:",      student.getId(), 0);
        addRow(grid, "Programme:",       student.getCourse(), 1);
        addRow(grid, "Email:",           student.getEmail()  != null ? student.getEmail()  : "Not provided", 2);
        addRow(grid, "Phone:",           student.getPhone()  != null ? student.getPhone()  : "Not provided", 3);
        addRow(grid, "Hostel Block:",    getHostelBlock(), 4);
        addRow(grid, "Room Number:",     student.getRoomId() != null ? student.getRoomId() : "Not assigned", 5);
        addRow(grid, "Enrollment Date:", "2024-01-15", 6);
        addRow(grid, "Status:",          student.getStatus(), 7);

        box.getChildren().addAll(name, grid);
        return box;
    }

    private void addRow(GridPane grid, String label, String value, int row) {
        Label l = new Label(label);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        l.setTextFill(Color.GRAY);
        grid.add(l, 0, row);
        grid.add(new Label(value), 1, row);
    }

    private String getHostelBlock() {
        String r = student.getRoomId();
        if (r == null) return "Not Assigned";
        if (r.startsWith("1")) return "Block A";
        if (r.startsWith("2")) return "Block B";
        if (r.startsWith("3")) return "Block C";
        return "Block A";
    }
}