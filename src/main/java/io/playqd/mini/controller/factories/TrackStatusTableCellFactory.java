package io.playqd.mini.controller.factories;

import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import io.playqd.data.MediaItemType;
import io.playqd.data.Reaction;
import io.playqd.data.Track;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.TrackItemRow;
import io.playqd.service.MusicLibrary;

public final class TrackStatusTableCellFactory implements StatusTableCellFactory {

    @Override
    public TableCell<LibraryItemRow, String> call(TableColumn<LibraryItemRow, String> param) {

        return new TextFieldTableCell<>() {

            private final HBox statusHBox = new HBox();

            {
                statusHBox.setSpacing(5);
                statusHBox.setAlignment(Pos.CENTER);
                statusHBox.setOnMouseEntered(e -> {
                    if (getTableRow().getItem() != null) {
                        var track = (Track) getTableRow().getItem().getSource();
                        if (Reaction.THUMB_UP != track.reaction()) {
                            statusHBox.getChildren().addFirst(createLikeBtn(FontIcon.of(FontAwesomeRegular.HEART)));
                        }
                    }
                    e.consume();
                });
                statusHBox.setOnMouseExited(e -> {
                    if (getTableRow().getItem() != null) {
                        var track = (Track) getTableRow().getItem().getSource();
                        if (Reaction.THUMB_UP != track.reaction()) {
                            statusHBox.getChildren().removeFirst();
                        }
                        e.consume();
                    }
                });
            }

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);
                statusHBox.getChildren().clear();
                if (item == null || empty || getTableRow() == null) {
                    setGraphic(null);
                } else {
                    setStatusIcons();
                }
            }

            private void setStatusIcons() {
                var itemRow = getTableRow().getItem();
                if (itemRow instanceof TrackItemRow trackItemRow) {
                    var track = trackItemRow.getSource();
                    Platform.runLater(() -> {
                        if (Reaction.THUMB_UP == track.reaction()) {
                            statusHBox.getChildren().add(
                                    createLikeBtn(FontIcon.of(FontAwesomeSolid.HEART, Color.web("#ff6688"))));
                        }
                        var inPlaylists = MusicLibrary.getPlaylists().stream()
                                .filter(p -> p.tracks().stream().anyMatch(pt -> pt.id() == track.id()))
                                .toList();
                        if (!inPlaylists.isEmpty()) {
                            statusHBox.getChildren().add(FontIcon.of(FontAwesomeSolid.HEADPHONES_ALT));
                        }
                        var inCollections = MusicLibrary.getCollections().stream()
                                .filter(c -> c.items().stream()
                                        .anyMatch(cItem ->
                                                MediaItemType.TRACK == cItem.itemType() &&
                                                        Long.parseLong(cItem.refId()) == track.id()))
                                .toList();
                        if (!inCollections.isEmpty()) {
                            statusHBox.getChildren().add(FontIcon.of(FontAwesomeRegular.CLONE));
                        }
                        setGraphic(statusHBox);
                    });
                }
            }

            private Button createLikeBtn(FontIcon icon) {
                var btn = new Button();
                btn.getStyleClass().setAll("icon-button");
                btn.setGraphic(icon);
                btn.setOnAction(_ -> {
                    var track = (Track) getTableRow().getItem().getSource();
                    var reaction = Reaction.THUMB_UP == track.reaction() ? Reaction.NONE : Reaction.THUMB_UP;
                    MusicLibrary.updateReaction(List.of(track.id()), reaction);
                });
                return btn;
            }
        };
    }
}
