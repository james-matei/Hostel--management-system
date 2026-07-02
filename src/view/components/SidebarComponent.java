package view.components;

import dao.RoomDao;
import dao.StudentDao;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Room;

import java.util.List;
import java.util.function.Consumer;

public class SidebarComponent {

    private static final String[][] NAV_ITEMS = {
        {"📊 Dashboard", "Dashboard"},
        {"👥 Students",  "Students"},
        {"🏠 Rooms",     "Rooms"},
        {"💰 Payments",  "Payments"},
        {"📋 Reports",   "Reports"}
    };

    private final Consumer<String> onNavigate;

    public SidebarComponent(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
    }

    public VBox getSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setStyle("-fx-background-color: #34495e;");
        sidebar.setPrefWidth(220);

        for (String[] item : NAV_ITEMS) {
            sidebar.getChildren().add(buildNavButton(item[0], item[1]));
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);
        sidebar.getChildren().add(buildQuickStats());
        return sidebar;
    }

    private Button buildNavButton(String text, String action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        String normal = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;";
        String hover  = "-fx-background-color: #3d566e;    -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;";

        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e  -> btn.setStyle(normal));
        btn.setOnAction(e -> onNavigate.accept(action));
        return btn;
    }

    private VBox buildQuickStats() {
        VBox statsBox = new VBox(8);
        statsBox.setPadding(new Insets(15, 10, 10, 10));
        statsBox.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 5;");

        Label statsTitle = new Label("QUICK STATS");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        statsTitle.setTextFill(Color.web("#95a5a6"));

        int totalStudents  = 0;
        int availableRooms = 0;
        int occupancyPct   = 0;

        try {
            totalStudents = new StudentDao().getAllStudents().size();
            List<Room> rooms = new RoomDao().getAllRooms();
            int total     = rooms.size();
            int available = (int) rooms.stream().filter(r -> !r.isFull()).count();
            availableRooms = available;
            occupancyPct   = total > 0 ? (int) ((total - available) * 100.0 / total) : 0;
        } catch (Exception ignored) {}

        statsBox.getChildren().addAll(
            statsTitle,
            statLabel("👥 Students: "  + totalStudents),
            statLabel("🏠 Available: " + availableRooms + " rooms"),
            statLabel("📊 Occupancy: " + occupancyPct + "%")
        );
        return statsBox;
    }

    private Label statLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.LIGHTGRAY);
        l.setFont(Font.font("Arial", 12));
        return l;
    }
}