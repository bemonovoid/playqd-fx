package io.playqd.dialog.settings;

import io.playqd.client.PlayqdClientProvider;
import io.playqd.config.AppConfig;
import io.playqd.data.WatchFolder;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.ToggleSwitch;

public class LibraryConfigView extends VBox implements ConfigView {

    @FXML
    private ToggleSwitch scanOnStartToggle;

    @FXML
    private ListView<WatchFolder> foldersListView;

    public LibraryConfigView() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.DIALOG_SETTINGS_LIBRARY);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, LibraryConfigView.class);
    }

    @FXML
    private void initialize() {
        scanOnStartToggle.setSelected(AppConfig.getProperties().library().rescanOnStartUp().get());
        AppConfig.getProperties().library().rescanOnStartUp().bind(scanOnStartToggle.selectedProperty());
        foldersListView.setCellFactory(new CustomCellFactory());
        foldersListView.getItems().addAll(PlayqdClientProvider.get().watchFolders().getAll());
    }

    @FXML
    private void refreshStatus() {

    }

    @FXML
    private void scan() {

    }

    @Override
    public void applyUpdates() {
        AppConfig.saveProperties();
    }

    private static final class CustomCellFactory
            implements Callback<ListView<WatchFolder>, ListCell<WatchFolder>> {

        @Override
        public ListCell<WatchFolder> call(ListView<WatchFolder> watchFolderListView) {
            return new ListCell<>() {

                @Override
                protected void updateItem(WatchFolder item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        var vBox = new VBox();
                        vBox.setSpacing(5);
                        vBox.setAlignment(Pos.CENTER_LEFT);

                        var name = new Label(item.name());
                        name.setStyle("-fx-font-size: 14");

                        var path = new Label(item.location());
                        path.setStyle("-fx-font-size: 11");
                        path.setDisable(true);

                        vBox.getChildren().addAll(name, path);

                        setGraphic(vBox);
                    }
                }
            };
        }
    }
}
