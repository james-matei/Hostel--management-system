package view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.function.Consumer;

public class SidebarComponent {

    private static final String[] NAV_ITEMS = {
        "📊 Dashboard", "👥 Students", "🏠 Rooms",
        "💰 Payments", "📋 Reports", "⚙ Settings", "❓ Help"
    };

    private Consumer<String> onNavigate;

    public SidebarComponent(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
    }

    public VBox getSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setStyle("-fx-background-color: #34495e;");
        sidebar.setPrefWidth(220);

        for (String item : NAV_ITEMS) {
            sidebar.getChildren().add(buildNavButton(item));
        }

        sidebar.getChildren().add(buildQuickStats());
        return sidebar;
    }

    private Button buildNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        String normal = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;";
        String hover  = "-fx-background-color: #3d566e;    -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;";

        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e  -> btn.setStyle(normal));
        btn.setOnAction(e -> onNavigate.accept(text.replaceAll("[^a-zA-Z]", "")));
        return btn;
    }

    private VBox buildQuickStats() {
        VBox statsBox = new VBox(10);
        statsBox.setPadding(new Insets(20, 10, 10, 10));
        statsBox.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 5;");

        Label statsTitle = new Label("QUICK STATS");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        statsTitle.setTextFill(Color.WHITE);

        statsBox.getChildren().addAll(
            statsTitle,
            statLabel("👥 Total Students: 156"),
            statLabel("🏠 Available Rooms: 23"),
            statLabel("📊 Occupancy: 87%")
        );
        return statsBox;
    }

    private Label statLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.LIGHTGRAY);
        return l;
    }
}