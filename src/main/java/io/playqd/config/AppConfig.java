package io.playqd.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AppConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    private static final String APP_WORK_DIR = ".playqd";
    private static final String PROPERTIES_FILE_NAME = "properties.json";

    private static ApplicationProperties PROPERTIES;

    public static ApplicationProperties getProperties() {
        if (PROPERTIES != null) {
            return PROPERTIES;
        }
        try {
            var workingDir = getOrCreateDefaultWorkingDir(getWorkingDir());
            var propertiesFile = getOrCreatePropertiesFile(workingDir);
            if (Files.size(propertiesFile) == 0) {
                commitPropertiesToFile(new ApplicationProperties());
            }
            var objectMapper = new ObjectMapper();
            return PROPERTIES =
                    objectMapper.readValue(Files.readAllBytes(propertiesFile), ApplicationProperties.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void commitPropertiesToFile() {
       commitPropertiesToFile(PROPERTIES);
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
                var appHomeDir = getWorkingDir();
                var appPropertiesInWorkDir = appHomeDir.resolve(PROPERTIES_FILE_NAME);
                Files.copy(is, appPropertiesInWorkDir, StandardCopyOption.REPLACE_EXISTING);
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

    public static Path getUserHomeDir() {
        return Paths.get(System.getProperty("user.home"));
    }

    public static Path getWorkingDir() {
        return getUserHomeDir().resolve(APP_WORK_DIR);
    }

}
