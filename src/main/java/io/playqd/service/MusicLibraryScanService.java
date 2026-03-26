package io.playqd.service;

import io.playqd.client.ClientException;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.ScanData;
import io.playqd.data.ScanRef;
import io.playqd.data.ScanStatus;
import io.playqd.data.WatchFolder;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class MusicLibraryScanService extends Service<ScanData> {

    private static final Logger LOG = LoggerFactory.getLogger(MusicLibraryScanService.class);

    private static final int TASK_TIMEOUT_MINUTES = 10;
    private static final int POLL_RESULT_INITIAL_DELAY_SECONDS = 5;
    private static final int POLL_RESULT_AT_FIXED_RATE_SECONDS = 10;

    private static final String POLL_THREAD_NAME_PREFIX = "virtual-poll-scan-result-";

    private final WatchFolder watchFolder;

    private final AtomicReference<ScheduledFuture<?>> pollerScheduledFuture;
    private final CompletableFuture<ScanData> resultFuture;
    private final ScheduledExecutorService executor;

    public MusicLibraryScanService(WatchFolder watchFolder) {
        this.watchFolder = watchFolder;
        this.resultFuture = new CompletableFuture<>();
        this.pollerScheduledFuture = new AtomicReference<>();
        var threadFactory = Thread.ofVirtual().name(POLL_THREAD_NAME_PREFIX + watchFolder.name(), 0).factory();
        executor = Executors.newScheduledThreadPool(1, threadFactory);
    }

    @Override
    public boolean cancel() {
        resultFuture.cancel(false);
        pollerScheduledFuture.get().cancel(false);
        executor.shutdown();
        return super.cancel();
    }

    @Override
    protected Task<ScanData> createTask() {

        return new Task<>() {

            @Override
            protected ScanData call() {
                try {
                    var scanRef = PlayqdClientProvider.get().scan(watchFolder.uuid());
                    return pollForScanResult(scanRef).get(TASK_TIMEOUT_MINUTES + 1, TimeUnit.MINUTES);
                } catch (Exception e) {
                    LOG.error("Task failed. {}", e.getMessage());
                    setException(e);
                    return null;
                }
            }
        };
    }

    private CompletableFuture<ScanData> pollForScanResult(ScanRef scanRef) {
        var pollScanData = (Runnable) () -> {
            try {
                var scanData = PlayqdClientProvider.get().getScanData(scanRef.id());
                LOG.info("Polled scan id: {},  status: {}", scanRef.id(), scanData.status());
                // Task has completed, clean up the resources
                if (ScanStatus.READY != scanData.status() && ScanStatus.RUNNING != scanData.status()) {
                    resultFuture.complete(scanData);
                    pollerScheduledFuture.get().cancel(false);
                    executor.shutdown();
                }
            } catch (ClientException e) {
                LOG.error("Failed to poll scan status for scan id: {}. {}", scanRef.id(), e.getMessage());
                resultFuture.completeExceptionally(e);
                pollerScheduledFuture.get().cancel(false);
                executor.shutdown();
            }
        };

        var future = executor.scheduleAtFixedRate(
                pollScanData,
                POLL_RESULT_INITIAL_DELAY_SECONDS,
                POLL_RESULT_AT_FIXED_RATE_SECONDS,
                TimeUnit.SECONDS);

        pollerScheduledFuture.set(future);

        executor.schedule(() -> {
            if (!future.isDone()) {
                LOG.error("{} minutes timeout reached. Forcing stop.", TASK_TIMEOUT_MINUTES);
                resultFuture.completeExceptionally(new TimeoutException
                        (String.format("Polling timed out after %s minutes", TASK_TIMEOUT_MINUTES)));
                future.cancel(true);
                executor.shutdown();
            }
        }, TASK_TIMEOUT_MINUTES, TimeUnit.MINUTES);

        return resultFuture;
    }
}
