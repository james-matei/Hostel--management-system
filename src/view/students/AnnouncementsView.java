package view.students;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AnnouncementsView {

    private ObservableList<String> announcements;

    public AnnouncementsView(ObservableList<String> announcements) {
        this.announcements = announcements;
    }

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("Hostel Announcements");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        ListView<String> list = new ListView<>(announcements);
        list.setPrefHeight(400);
        list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }

                HBox cell = new HBox(10);
                cell.setPadding(new Insets(10));
                cell.setAlignment(Pos.CENTER_LEFT);

                Label icon = new Label("📢");
                icon.setFont(Font.font(16));

                VBox textBox = new VBox(3);
                String[] parts = item.split("\\|", 2);
                if (parts.length == 2) {
                    Label t = new Label(parts[0].trim());
                    t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    Label d = new Label(parts[1].trim());
                    d.setTextFill(Color.GRAY);
                    textBox.getChildren().addAll(t, d);
                } else {
                    textBox.getChildren().add(new Label(item));
                }

                cell.getChildren().addAll(icon, textBox);
                setGraphic(cell);
            }
        });

        view.getChildren().addAll(title, list);
        return view;
    }
}