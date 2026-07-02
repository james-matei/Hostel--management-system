package view.students;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
//import javafx.scene.StackPane;
import model.Student;

public class MyRoomView {

    private Student student;

    public MyRoomView(Student student) {
        this.student = student;
    }

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("My Room Information");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        view.getChildren().addAll(title, buildRoomCard());
        return view;
    }

    private VBox buildRoomCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                      "-fx-border-color: #ddd; -fx-border-radius: 10; " +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");

        card.getChildren().addAll(
            buildRoomHeader(),
            buildDetailsGrid(),
            facilitiesTitle(),
            buildFacilitiesPane()
        );

        if (hasRoommate()) {
            card.getChildren().addAll(roommateTitle(), buildRoommateBox());
        }

        return card;
    }

    private HBox buildRoomHeader() {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("🏠");
        icon.setFont(Font.font(32));

        Label roomNumber = new Label("Room " + (student.getRoomId() != null ? student.getRoomId() : "Not Assigned"));
        roomNumber.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        header.getChildren().addAll(icon, roomNumber);
        return header;
    }

    private GridPane buildDetailsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 0, 0, 0));

        addRow(grid, "Room Status:", getRoomStatus(), 0);
        addRow(grid, "Room Type:",   "Shared Double Room", 1);
        addRow(grid, "Capacity:",    "2 persons", 2);
        addRow(grid, "Occupants:",   hasRoommate() ? "2/2" : "1/2", 3);
        addRow(grid, "Floor:",       getFloorNumber(), 4);
        addRow(grid, "Block:",       getHostelBlock(), 5);

        return grid;
    }

    private Label facilitiesTitle() {
        Label l = new Label("Facilities");
        l.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        return l;
    }

    private FlowPane buildFacilitiesPane() {
        FlowPane pane = new FlowPane();
        pane.setHgap(10);
        pane.setVgap(10);

        for (String f : new String[]{"🛏️ Bed", "🪑 Desk", "🪑 Chair", "👕 Wardrobe", "❄️ AC", "🚿 Attached Bathroom"}) {
            Label l = new Label(f);
            l.setPadding(new Insets(8, 15, 8, 15));
            l.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 20;");
            pane.getChildren().add(l);
        }
        return pane;
    }

    private Label roommateTitle() {
        Label l = new Label("Roommate Information");
        l.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        return l;
    }

    private HBox buildRoommateBox() {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5;");

        Label icon = new Label("👤");
        icon.setFont(Font.font(24));

        VBox info = new VBox(5);
        Label name = new Label("John Doe");
        name.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label course = new Label("Computer Science, Year 2");
        course.setFont(Font.font("Arial", 12));
        course.setTextFill(Color.GRAY);
        info.getChildren().addAll(name, course);

        box.getChildren().addAll(icon, info);
        return box;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void addRow(GridPane grid, String label, String value, int row) {
        Label l = new Label(label);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        l.setTextFill(Color.GRAY);
        grid.add(l, 0, row);
        grid.add(new Label(value), 1, row);
    }

    private String getRoomStatus()  { return student.getRoomId() != null ? "Occupied" : "Not Assigned"; }
    private String getFloorNumber() {
        String r = student.getRoomId();
        return (r != null) ? String.valueOf(r.charAt(0)) : "N/A";
    }
    private String getHostelBlock() {
        String r = student.getRoomId();
        if (r == null) return "Not Assigned";
        if (r.startsWith("1")) return "Block A";
        if (r.startsWith("2")) return "Block B";
        if (r.startsWith("3")) return "Block C";
        return "Block A";
    }
    private boolean hasRoommate() {
        String r = student.getRoomId();
        return r != null && (r.equals("101") || r.equals("102"));
    }
}