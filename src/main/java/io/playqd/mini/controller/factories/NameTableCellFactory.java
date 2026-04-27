package io.playqd.mini.controller.factories;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import io.playqd.data.Track;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.configurer.TracksViewConfigurer;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.events.NavigationEvent;
import io.playqd.player.Player;
import io.playqd.player.PlayerTrackStatus;

public final class NameTableCellFactory implements
        Callback<TableColumn<LibraryItemRow, String>, TableCell<LibraryItemRow, String>> {

    private final Object configurer;
    private final Function<LibraryItemRow, String> title;
    private final Function<LibraryItemRow, String> subTitle;

    public NameTableCellFactory(Object configurer, Function<LibraryItemRow, String> subTitle) {
        this(configurer, null, subTitle);
    }

    public NameTableCellFactory(Object configurer, Function<LibraryItemRow, String> title,
                                Function<LibraryItemRow, String> subTitle) {
        this.configurer = configurer;
        this.title = title;
        this.subTitle = subTitle;
    }

    @Override
    public TableCell<LibraryItemRow, String> call(TableColumn<LibraryItemRow, String> param) {

        return new TableCell<>() {

            private final HBox container = new HBox();
            private final Label titleLabel = new Label();
            private final Label playerTrackStatusLabel = new Label();
            private final Consumer<String> subtitleSetter;
            private Supplier<LibraryItemRow> subtitleActionRow;

            {
                Node subtitleLabel;
                if (configurer instanceof TracksViewConfigurer) {
                    var hyperLink = new Hyperlink();
                    hyperLink.setOnAction(_ -> {
                        if (subtitleActionRow != null) {
                            var navItems = NavigableItemsResolver.resolveArtistAlbums(subtitleActionRow.get());
                            hyperLink.fireEvent(new NavigationEvent(navItems));
                        }
                    });
                    subtitleLabel = hyperLink;
                    subtitleLabel.getStyleClass().addAll("hyper-link-as-label");
                    subtitleSetter = hyperLink::setText;
                } else {
                    var label = new Label();
                    subtitleLabel = label;
                    subtitleSetter = label::setText;
                }
                titleLabel.setStyle("-fx-font-weight: 500; -fx-font-size: 13; -fx-font-smoothing-type: lcd");
                subtitleLabel.setStyle("-fx-font-weight: 100; -fx-font-size: 11; -fx-font-smoothing-type: lcd; -fx-text-fill: #a2a2a2");

                var titlesContainer = new VBox();
                titlesContainer.setAlignment(Pos.CENTER_LEFT);
                titlesContainer.getChildren().addAll(titleLabel, subtitleLabel);

                var region = new Region();
                HBox.setHgrow(region, Priority.ALWAYS);
                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().addAll(titlesContainer, region);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    var libraryItemRow = getTableRow().getItem();
                    if (title == null) {
                        titleLabel.setText(libraryItemRow.getName());
                    } else {
                        titleLabel.setText(title.apply(libraryItemRow));
                    }
                    subtitleSetter.accept(subTitle.apply(libraryItemRow));
                    subtitleActionRow = () -> libraryItemRow;

                    if (libraryItemRow.getSource() instanceof Track track) {
                        Player.playerTrack()
                                .filter(playingTrack -> playingTrack.track().id() == track.id())
                                .ifPresentOrElse(playerTrack -> {
                                            if (!(container.getChildren().getLast() instanceof Label)) {
                                                container.getChildren().add(playerTrackStatusToLabel(playerTrack.status().get()));
                                            }
                                        },
                                        () -> {
                                            if (container.getChildren().getLast() instanceof Label) {
                                                container.getChildren().removeLast();
                                            }
                                        });
                    }
                    setGraphic(container);
                }
            }

            private Label playerTrackStatusToLabel(PlayerTrackStatus status) {
                var nowPlayingGlow = new DropShadow();
                nowPlayingGlow.setRadius(15);
                nowPlayingGlow.setSpread(0.5);
                switch (status) {
                    case PAUSED -> {
                        nowPlayingGlow.setColor(Color.YELLOW);
                        playerTrackStatusLabel.setText("paused");
                    }
                    case PLAYING -> {
                        nowPlayingGlow.setColor(Color.SPRINGGREEN);
                        playerTrackStatusLabel.setText("is playing");
                    }
                    case READY -> {
                        nowPlayingGlow.setColor(Color.LIGHTSKYBLUE);
                        playerTrackStatusLabel.setText("in player");
                    }
                }

                playerTrackStatusLabel.setEffect(nowPlayingGlow);
                playerTrackStatusLabel.setStyle("-fx-font-size: 0.8em; -fx-font-weight: 100;-fx-font-style: italic");
                return playerTrackStatusLabel;
            }
        };
    }
}
