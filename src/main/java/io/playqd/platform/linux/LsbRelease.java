package io.playqd.platform.linux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public record LsbRelease(String distributorId, String description, String release, String codename) {

    private static final Logger LOG = LoggerFactory.getLogger(LsbRelease.class);

    public boolean isMint() {
        return distributorId.toLowerCase().contains("mint") || description.toLowerCase().contains("mint");
    }

    public static Optional<LsbRelease> get() {
        if (!System.getProperty("os.name").toLowerCase().contains("linux")) {
            LOG.error("'lsb_release -a' is not supported on this OS.");
            return Optional.empty();
        }
        try {
            var process = Runtime.getRuntime().exec(new String[]{"lsb_release", "-a"});
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                var distributorId = "";
                var description = "";
                var release = "";
                var codename = "";
                while ((line = reader.readLine()) != null) {
                    var parts = line.split(":");
                    if (parts[0].toLowerCase().contains("distributor")) {
                        distributorId =  parts[1].trim();
                    } else if (parts[0].toLowerCase().contains("description")) {
                        description = parts[1].trim();
                    } else if (parts[0].toLowerCase().contains("release")) {
                        release = parts[1].trim();
                    } else if (parts[0].toLowerCase().contains("codename")) {
                        codename = parts[1].trim();
                    }
                }
                return Optional.of(new LsbRelease(distributorId, description, release, codename));
            }
        } catch (IOException e) {
            LOG.error("Failed to execute 'lsb_release -a'", e);
            return Optional.empty();
        }
    }
}
