package io.playqd.player;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TrackInfoContainer extends VBox {

    public TrackInfoContainer() {
        this.setPadding(new Insets(15));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #f4f4f4;"); // Light grey background

        // --- Header ---
        Label header = new Label("TRACK DETAILS");
        header.setFont(Font.font("System", FontWeight.BOLD, 14));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(8);

        // Setting column widths: labels take what they need, values grow
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(100);
        ColumnConstraints valueCol = new ColumnConstraints();
        valueCol.setPercentWidth(70);
        grid.getColumnConstraints().addAll(labelCol, valueCol);

        // --- Section 1: Metadata ---
        addInfoRow(grid, 0, "Track Name:", "N/A");
        addInfoRow(grid, 1, "Artist:", "N/A");
        addInfoRow(grid, 2, "Album:", "N/A");
        addInfoRow(grid, 3, "Genre:", "N/A");

        // Visual Break
        Separator sep = new Separator();
        sep.setPadding(new Insets(10, 0, 10, 0));

        // --- Section 2: Technical & Location ---
        GridPane techGrid = new GridPane();
        techGrid.setHgap(20);
        techGrid.setVgap(8);
        techGrid.getColumnConstraints().addAll(labelCol, valueCol);

        addInfoRow(techGrid, 0, "Location:", "C:/Music/...");
        addInfoRow(techGrid, 1, "Format:", "FLAC");
        addInfoRow(techGrid, 2, "Bitrate:", "1411 kbps");
        addInfoRow(techGrid, 3, "Sample Bits:", "16-bit");

        this.getChildren().addAll(header, grid, sep, techGrid);
    }

    private void addInfoRow(GridPane grid, int row, String labelText, String valueText) {
        var lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: #666666; -fx-font-weight: bold;");

        var value = new Label(valueText);
        value.setStyle("-fx-text-fill: #222222;");
        value.setWrapText(true); // Useful for long file paths

        grid.add(lbl, 0, row);
        grid.add(value, 1, row);
    }


}
