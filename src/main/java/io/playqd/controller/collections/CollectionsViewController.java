package io.playqd.controller.collections;

import io.playqd.data.MediaCollection;
import io.playqd.data.PlaylistWithTrackIds;
import io.playqd.service.MusicLibrary;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class CollectionsViewController {

    private static final Logger LOG = LoggerFactory.getLogger(CollectionsViewController.class);

    @FXML
    private ListView<MediaCollection> listView;

    @FXML
    private CollectionItemsViewController collectionItemsViewController;

    @FXML
    private void initialize() {
        listView.setCellFactory(new CollectionsListViewCellFactory());
        initCollections();
        listView.getSelectionModel().selectedItemProperty().addListener((_, _, selectedCollection) -> {
            if (selectedCollection == null) {
                return;
            }
            collectionItemsViewController.showItems(selectedCollection);
        });
    }

    private void initCollections() {
        MusicLibrary.onCollectionsModified((collections) -> {
            listView.setDisable(false);
            listView.getItems().clear();
            listView.getItems().addAll(collections);
        });
        MusicLibrary.getCollections();
    }

    private void setTracksVisibleColumns() {

    }

    private void initTracksTableViewEventHandlers() {

    }

    @FXML
    private void showCreateCollectionDialog() {
        var dialog = new CollectionDialog();
        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                MusicLibrary.createCollection(name, Collections.emptyList());
            }
        });
    }

    @FXML
    private void showDeleteEmptyCollections() {

    }

    private void initCollectionsInfoLabelListener() {
//        listView.getItems().addListener((ListChangeListener<PlaylistWithTrackIds>) changed -> {
//            if (changed.getList() == null || changed.getList().isEmpty()) {
//                playlistsInfoLabel.setText("");
//            } else {
//                var itemsText = changed.getList().size() > 1 ? "playlists" : "playlist";
//                playlistsInfoLabel.setText(Numbers.format(changed.getList().size()) + " " + itemsText);
//            }
//        });
    }

    private void updateTrackViewHeader(PlaylistWithTrackIds playlist) {
//        tracksView.tracksTableHeader().setTitle("Playlist: " + playlist.name());
//        var lmdLabel = new Label("Last modified: ");
//        var lmdLabelValue = new Label(playlist.lastModifiedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
//        var hBox = new HBox(lmdLabel, lmdLabelValue);
//        hBox.setAlignment(Pos.CENTER_LEFT);
//        hBox.setSpacing(5);
//        hBox.setDisable(true);
//        tracksView.tracksTableHeader().setDetails(hBox);
    }

//    public PlaylistWithTrackIds getSelectedPlaylist() {
//        return listView.getSelectionModel().getSelectedItem();
//    }
//
//    public int getSelectedPlaylistIndex() {
//        return listView.getSelectionModel().getSelectedIndex();
//    }
//
//    public void refreshPlaylistAtIndex(int listViewItemIndex) {
//        listView.getSelectionModel().select(listViewItemIndex);
//    }

}
