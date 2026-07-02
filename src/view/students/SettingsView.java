package view.students;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SettingsView {

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        view.getChildren().addAll(title, buildSettingsBox());
        return view;
    }

    private VBox buildSettingsBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10;");

        String[][] settings = {
        
            {"🔐 Change Password",      "Update your account password"},
            
        };

        for (String[] s : settings) {
            box.getChildren().add(buildSettingRow(s[0], s[1]));
        }
        return box;
    }

    private HBox buildSettingRow(String setting, String description) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-border-color: transparent transparent #ecf0f1 transparent; -fx-border-width: 0 0 1 0;");

        Label icon = new Label(setting.split(" ")[0]);
        icon.setFont(Font.font(18));

        VBox text = new VBox(3);
        Label name = new Label(setting.substring(2));
        name.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label desc = new Label(description);
        desc.setFont(Font.font("Arial", 11));
        desc.setTextFill(Color.GRAY);
        text.getChildren().addAll(name, desc);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");

        row.getChildren().addAll(icon, text, spacer, editBtn);
        return row;
    }
}
