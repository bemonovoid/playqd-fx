package io.playqd.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.playqd.platform.PlatformApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class AppConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    private static final String APP_WORK_DIR = ".playqd";
    private static final String PROPERTIES_FILE_NAME = "properties.json";
    private static final ObjectMapper OBJECT_MAPPER;
    private static final Path WORKING_DIR;

    private static ApplicationProperties PROPERTIES;

    static {
        var customWorkDir = System.getProperty("playqd.workdir");
        if (customWorkDir != null) {
            WORKING_DIR = getOrCreateDefaultWorkingDir(Paths.get(customWorkDir));
        } else {
            WORKING_DIR = getOrCreateDefaultWorkingDir(PlatformApi.getUserHomeDir().resolve(APP_WORK_DIR));
        }
        OBJECT_MAPPER = new ObjectMapper();
    }

    public static ApplicationProperties getProperties() {
        if (PROPERTIES != null) {
            return PROPERTIES;
        }
        try {
            var propertiesFile = getOrCreatePropertiesFile(getWorkingDir());
            if (Files.size(propertiesFile) == 0) {
                commitPropertiesToFile(new ApplicationProperties());
            }
            return PROPERTIES =
                    OBJECT_MAPPER.readValue(Files.readAllBytes(propertiesFile), ApplicationProperties.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveProperties() {
        try {
            var propertiesFile = getWorkingDir().resolve(PROPERTIES_FILE_NAME);
            var old = OBJECT_MAPPER.readValue(
                    Files.readAllBytes(propertiesFile), new TypeReference<Map<String, Object>>() {});
            if (!old.equals(OBJECT_MAPPER.convertValue(PROPERTIES, Map.class))) {
                commitPropertiesToFile(PROPERTIES);
                LOG.info("Properties were successfully saved to {}", propertiesFile);
            } else {
                LOG.info("Properties are up to date.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save properties", e);
        }
    }

    private static void commitPropertiesToFile(ApplicationProperties properties) {
        try {
            var objectMapper = new ObjectMapper();
            var data = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(properties);
            commitPropertiesToFile(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void commitPropertiesToFile(byte[] data) {
        try {
            try (var is = new ByteArrayInputStream(data)) {
                var propertiesFile = getWorkingDir().resolve(PROPERTIES_FILE_NAME);
                Files.copy(is, propertiesFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getOrCreateDefaultWorkingDir(Path workDirPath) {
        if (Files.exists(workDirPath)) {
            LOG.info("Working dir already exists: {}", workDirPath);
        } else {
            if (workDirPath.toFile().mkdirs()) {
                LOG.info("Working dir was successfully created: {}", workDirPath);
            } else {
                throw new IllegalStateException(String.format("Working dir was not created: %s", workDirPath));
            }
        }
        return workDirPath;
    }

    private static Path getOrCreatePropertiesFile(Path workingDir) throws Exception {
        var propertiesFile = workingDir.resolve(PROPERTIES_FILE_NAME);
        if (Files.exists(propertiesFile)) {
            LOG.info("Using exising properties file: {}", propertiesFile);
            return propertiesFile;
        } else {
            return Files.createFile(propertiesFile);
        }
    }

    public static Path getWorkingDir() {
        return WORKING_DIR;
    }

}
