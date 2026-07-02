package view.rooms;

import dao.RoomDao;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Room;

import java.util.List;

public class RoomsView {

    private final RoomDao roomDao = new RoomDao();
    private ObservableList<Room> allRooms = FXCollections.observableArrayList();

    // Filter state
    private String currentFilter = "All";
    private String currentSearch = "";
    private FlowPane roomGrid;
    private Label summaryLabel;

    public VBox getView() {
        loadFromDatabase();

        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        // ── Title ─────────────────────────────────────────────────────────────
        Label title = new Label("Room Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        // ── Summary bar ───────────────────────────────────────────────────────
        summaryLabel = new Label();
        summaryLabel.setFont(Font.font("Arial", 13));
        summaryLabel.setTextFill(Color.web("#7f8c8d"));
        updateSummaryLabel();

        // ── Legend ────────────────────────────────────────────────────────────
        HBox legend = buildLegend();

        // ── Controls bar ──────────────────────────────────────────────────────
        HBox controls = buildControls();

        // ── Room grid ─────────────────────────────────────────────────────────
        roomGrid = new FlowPane();
        roomGrid.setHgap(12);
        roomGrid.setVgap(12);
        roomGrid.setPadding(new Insets(10, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(roomGrid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        renderRooms();

        view.getChildren().addAll(title, summaryLabel, legend, controls, scroll);
        return view;
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    private void loadFromDatabase() {
        allRooms.setAll(roomDao.getAllRooms());
    }

    private void updateSummaryLabel() {
        long total   = allRooms.size();
        long empty   = allRooms.stream().filter(Room::isEmpty).count();
        long partial = allRooms.stream().filter(r -> !r.isEmpty() && !r.isFull()).count();
        long full    = allRooms.stream().filter(Room::isFull).count();

        summaryLabel.setText(
            "Total: " + total + "  |  " +
            "Empty: " + empty + "  |  " +
            "Partially Occupied: " + partial + "  |  " +
            "Full: " + full
        );
    }

    // ── Legend ────────────────────────────────────────────────────────────────

    private HBox buildLegend() {
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.getChildren().addAll(
            legendItem("#27ae60", "Empty"),
            legendItem("#f39c12", "Partially Occupied (1/2)"),
            legendItem("#e74c3c", "Full (2/2)")
        );
        return legend;
    }

    private HBox legendItem(String color, String label) {
        HBox item = new HBox(6);
        item.setAlignment(Pos.CENTER_LEFT);

        Pane dot = new Pane();
        dot.setPrefSize(14, 14);
        dot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 7;");

        Label l = new Label(label);
        l.setFont(Font.font("Arial", 12));

        item.getChildren().addAll(dot, l);
        return item;
    }

    // ── Controls bar ─────────────────────────────────────────────────────────

    private HBox buildControls() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);

        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search room number...");
        searchField.setPrefWidth(200);
        searchField.setPrefHeight(35);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            currentSearch = newVal.trim().toLowerCase();
            renderRooms();
        });

        // Filter buttons
        ToggleGroup group = new ToggleGroup();
        ToggleButton allBtn     = filterBtn("All",     group);
        ToggleButton emptyBtn   = filterBtn("Empty",   group);
        ToggleButton partialBtn = filterBtn("Partial", group);
        ToggleButton fullBtn    = filterBtn("Full",    group);
        allBtn.setSelected(true);

        group.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentFilter = ((ToggleButton) newVal).getText();
                renderRooms();
            }
        });

        // Refresh button
        Button refreshBtn = new Button("↻ Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        refreshBtn.setPrefHeight(35);
        refreshBtn.setOnAction(e -> {
            loadFromDatabase();
            updateSummaryLabel();
            renderRooms();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bar.getChildren().addAll(searchField, allBtn, emptyBtn, partialBtn, fullBtn, spacer, refreshBtn);
        return bar;
    }

    private ToggleButton filterBtn(String text, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(group);
        btn.setPrefHeight(35);
        btn.setStyle("-fx-cursor: hand;");
        return btn;
    }

    // ── Room grid rendering ───────────────────────────────────────────────────

    private void renderRooms() {
        roomGrid.getChildren().clear();

        for (Room room : allRooms) {
            // Apply search filter
            if (!currentSearch.isEmpty() && !room.getRoomId().contains(currentSearch)) continue;

            // Apply status filter
            if (!currentFilter.equals("All")) {
                boolean matches = switch (currentFilter) {
                    case "Empty"   -> room.isEmpty();
                    case "Partial" -> !room.isEmpty() && !room.isFull();
                    case "Full"    -> room.isFull();
                    default        -> true;
                };
                if (!matches) continue;
            }

            roomGrid.getChildren().add(buildRoomCard(room));
        }

        if (roomGrid.getChildren().isEmpty()) {
            Label none = new Label("No rooms match your filter.");
            none.setFont(Font.font("Arial", 13));
            none.setTextFill(Color.GRAY);
            roomGrid.getChildren().add(none);
        }
    }

    // ── Room card ─────────────────────────────────────────────────────────────

    private VBox buildRoomCard(Room room) {
        String color = room.isEmpty()  ? "#27ae60"
                     : room.isFull()   ? "#e74c3c"
                     :                   "#f39c12";

        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setPrefSize(130, 110);
        card.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 2);"
        );

        Label roomNum = new Label("Room " + room.getRoomId());
        roomNum.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        roomNum.setTextFill(Color.WHITE);

        Label status = new Label(room.getStatus());
        status.setFont(Font.font("Arial", 11));
        status.setTextFill(Color.WHITE);

        Label beds = new Label(room.getOccupied() + " / " + room.getCapacity() + " beds");
        beds.setFont(Font.font("Arial", 11));
        beds.setTextFill(Color.web("#ecf0f1"));

        Label floor = new Label("Floor " + room.getFloor());
        floor.setFont(Font.font("Arial", 10));
        floor.setTextFill(Color.web("#ecf0f1"));

        card.getChildren().addAll(roomNum, status, beds, floor);
        card.setOnMouseClicked(e -> showRoomDetails(room));

        // Hover effect
        String hoverStyle = "-fx-background-color: derive(" + color + ", -15%); " +
                            "-fx-background-radius: 8; -fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 3);";
        String normalStyle = card.getStyle();
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e  -> card.setStyle(normalStyle));

        return card;
    }

    // ── Room details popup ────────────────────────────────────────────────────

    private void showRoomDetails(Room room) {
        // Fetch current occupants from DB
        List<String> occupants = roomDao.getOccupantNames(room.getRoomId());

        StringBuilder content = new StringBuilder();
        content.append("Type     : ").append(room.getType()     != null ? room.getType()   : "Double").append("\n");
        content.append("Floor    : ").append(room.getFloor()).append("\n");
        content.append("Capacity : ").append(room.getCapacity()).append(" students\n");
        content.append("Occupied : ").append(room.getOccupied()).append(" / ").append(room.getCapacity()).append("\n");
        content.append("Price    : KSh ").append(String.format("%,.0f", room.getPrice())).append(" / month\n");
        content.append("Status   : ").append(room.getStatus()).append("\n\n");

        if (occupants.isEmpty()) {
            content.append("Occupants: None");
        } else {
            content.append("Occupants:\n");
            for (int i = 0; i < occupants.size(); i++) {
                content.append("  ").append(i + 1).append(". ").append(occupants.get(i)).append("\n");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Room Details");
        alert.setHeaderText("Room " + room.getRoomId());
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
}