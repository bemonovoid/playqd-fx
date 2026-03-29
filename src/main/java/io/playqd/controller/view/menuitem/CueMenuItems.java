package io.playqd.controller.view.menuitem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.Track;
import io.playqd.platform.PlatformApi;
import io.playqd.utils.PlayqdApis;
import javafx.scene.control.MenuItem;

import java.util.Collections;
import java.util.List;

public final class CueMenuItems implements MenuItemsBuilder {

    private List<Track> selectedTracks = Collections.emptyList();

    @Override
    public List<MenuItem> build() {
        var showCueMenuItem = new MenuItem("Open cue file", new FontAwesomeIconView(FontAwesomeIcon.EXTERNAL_LINK));
        showCueMenuItem.setOnAction(_ -> {
            selectedTracks.stream()
                    .map(t -> PlayqdApis.trackCueFile(t.id()))
                    .distinct()
                    .forEach(PlatformApi::open);
        });
        return List.of(showCueMenuItem);
    }

    public CueMenuItems setSelectedTracks(List<Track> selectedTracks) {
        if (selectedTracks != null) {
            this.selectedTracks = selectedTracks;
        }
        return this;
    }
}
