package view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class StatusBarComponent {

    private Label statusLabel;

    public HBox getStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5, 15, 5, 15));
        statusBar.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        statusBar.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label("Ready");
        statusLabel.setFont(Font.font("Arial", 11));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label versionLabel = new Label("v1.0.0");
        versionLabel.setFont(Font.font("Arial", 11));
        versionLabel.setTextFill(Color.GRAY);

        statusBar.getChildren().addAll(statusLabel, spacer, versionLabel);
        return statusBar;
    }

    public void setStatus(String message) {
        if (statusLabel != null) statusLabel.setText(message);
    }
}