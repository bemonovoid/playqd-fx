package io.playqd.service;

import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.Result;
import io.playqd.data.ScanData;
import io.playqd.utils.TimeUtils;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MusicLibraryScanServiceManager {

    private static final Logger LOG = LoggerFactory.getLogger(MusicLibraryScanServiceManager.class);

    private static final SimpleObjectProperty<Result<ScanData>> SCAN_RESULT_PROPERTY =
            new SimpleObjectProperty<>();

    // watch folder uuid -> Service
    private static final Map<String, Service<ScanData>> ACTIVE_SCANS = Collections.synchronizedMap(new HashMap<>());

    public static ReadOnlyObjectProperty<Result<ScanData>> scanResultProperty() {
        return SCAN_RESULT_PROPERTY;
    }

    public static boolean isRunning() {
        if (ACTIVE_SCANS.isEmpty()) {
            return false;
        }
        return ACTIVE_SCANS.values().stream().anyMatch(Service::isRunning);
    }

    public static void stop() {
        ACTIVE_SCANS.values().forEach(Service::cancel);
    }

    public static void submitScan() {
        var watchFolders = PlayqdClientProvider.get().getWatchFolders();

        watchFolders.forEach(watchFolder -> {

            var scanService = ACTIVE_SCANS.computeIfAbsent(
                    watchFolder.uuid(), _ -> new MusicLibraryScanService(watchFolder));

            if (scanService.isRunning()) {
                LOG.warn("Can submit scan for {} at {}. Previous scan is still in progress.",
                        watchFolder.name(), watchFolder.path());
                return;
            }

            scanService.setOnRunning(_ -> LOG.info("Scanning {} at {}.", watchFolder.name(), watchFolder.path()));

            scanService.setOnSucceeded(_ -> {
                var scanData = scanService.getValue();
                if (scanData != null) {
                    LOG.info("Scan completed: {}", scanData);
                    NotificationService.showSuccess("Music library", buildScanDataMessage(scanData));
                } else {
                    LOG.info("Scan completed but result was not available.");
                }
                SCAN_RESULT_PROPERTY.set(Result.success(scanData));
                ACTIVE_SCANS.remove(watchFolder.uuid());
            });

            scanService.setOnCancelled(_ -> {
                ACTIVE_SCANS.remove(watchFolder.uuid());
                LOG.info("Scan {} at {} was cancelled.", watchFolder.name(), watchFolder.path());
            });

            scanService.setOnFailed(_ -> {
                var error = scanService.getException();
                if (error != null) {
                    var errMessage = String.format("Scan failed. %s", error.getMessage());
                    LOG.warn(errMessage, error);
                    SCAN_RESULT_PROPERTY.set(Result.error(errMessage, error));
                } else {
                    var errMessage = "Scan failed";
                    LOG.warn(errMessage);
                    Result.error(errMessage);
                }
                ACTIVE_SCANS.remove(watchFolder.uuid());
            });

            scanService.start();
        });
    }

    private static String buildScanDataMessage(ScanData scanData) {
        var sb = new StringBuilder()
                .append("Scan completed in: ")
                .append(TimeUtils.durationToTimeFormat(Duration.ofMillis(scanData.durationInMillis())));
        if (scanData.added() > 0) {
            sb.append("\n\t").append("Added: ").append(scanData.added());
        }
        if (scanData.updated() > 0) {
            sb.append("\n\t").append("Updated: ").append(scanData.updated());
        }
        if (scanData.deleted() > 0) {
            sb.append("\n\t").append("Deleted: ").append(scanData.deleted());
        }
        return sb.toString();

    }
}
