package io.playqd.mini.controller.factories;

import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.NavigableItems;
import io.playqd.mini.events.NavigationEvent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.function.Function;

public class HyperLinkTableCellFactory implements DescriptionTableCellFactory {

    private final Function<LibraryItemRow, NavigableItems> navItemsProducer;

    public HyperLinkTableCellFactory(Function<LibraryItemRow, NavigableItems> navItemsProducer) {
        this.navItemsProducer = navItemsProducer;
    }

    @Override
    public TableCell<LibraryItemRow, String> call(TableColumn<LibraryItemRow, String> param) {
        return new TextFieldTableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    return;
                }
                setText(null);
                var itemRow = getTableRow().getItem();

                var hyperLink = new Hyperlink(itemRow.getDescription());

                hyperLink.getStyleClass().add("hyper-link-as-label");
                hyperLink.setOnAction(_ -> {
                    hyperLink.fireEvent(new NavigationEvent(navItemsProducer.apply(itemRow)));
                });

                setGraphic(hyperLink);
            }
        };
    }
}
