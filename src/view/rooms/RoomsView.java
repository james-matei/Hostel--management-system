package view.rooms;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RoomsView {

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("Room Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        FlowPane roomGrid = new FlowPane();
        roomGrid.setHgap(10);
        roomGrid.setVgap(10);

        for (int i = 101; i <= 110; i++) {
            roomGrid.getChildren().add(buildRoomCard(i));
        }

        view.getChildren().addAll(title, roomGrid);
        return view;
    }

    private VBox buildRoomCard(int roomNumber) {
        boolean available = roomNumber % 2 == 0;
        String color = available ? "#27ae60" : "#e74c3c";

        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5; -fx-cursor: hand;");
        card.setPrefSize(120, 100);

        Label roomNum = new Label("Room " + roomNumber);
        roomNum.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        roomNum.setTextFill(Color.WHITE);

        Label status = new Label(available ? "Available" : "Occupied");
        status.setTextFill(Color.WHITE);

        Label student = new Label(available ? "" : "John Doe");
        student.setTextFill(Color.WHITE);
        student.setFont(Font.font("Arial", 10));

        card.getChildren().addAll(roomNum, status, student);
        card.setOnMouseClicked(e -> showRoomDetails(roomNumber));

        return card;
    }

    private void showRoomDetails(int roomNumber) {
        boolean available = roomNumber % 2 == 0;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Room Details");
        alert.setHeaderText("Room " + roomNumber);
        alert.setContentText(
            "Status: " + (available ? "Available" : "Occupied by John Doe") +
            "\nCapacity: 2\nFacilities: Bed, Desk, Chair, Wardrobe"
        );
        alert.showAndWait();
    }
}