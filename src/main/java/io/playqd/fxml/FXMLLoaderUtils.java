package io.playqd.fxml;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class FXMLLoaderUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FXMLLoaderUtils.class);

    public static final String APPLICATION_PATH = "/fxml/application/application.fxml";

    public static Parent loadParent() {
        return load(APPLICATION_PATH);
    }

    public static Node load(FXMLResource fxmlResource) {
        return load(fxmlResource.path());
    }

    public static FXMLLoader resourceLoader(FXMLResource fxmlResource) {
        return new FXMLLoader(FXMLLoaderUtils.class.getResource(fxmlResource.path()));
    }

    public static Parent loadAsParent(FXMLResource fxmlResource) {
        return load(fxmlResource.path());
    }

    public static void load(FXMLLoader fxmlLoader) {
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed loading %s.", fxmlLoader.getRoot()), e);
        }
    }

    public static Node loadNode(FXMLLoader fxmlLoader) {
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed loading %s.", fxmlLoader.getRoot()), e);
        }
    }

    public static <T> T loadObject(FXMLLoader fxmlLoader, Class<T> type) {
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed loading %s.", fxmlLoader.getRoot()), e);
        }
    }

    public static <T> T loadObject(FXMLResource fxmlResource) {
        return load(fxmlResource.path());
    }

    public static <T> T getController(FXMLLoader fxmlLoader) {
        return fxmlLoader.getController();
    }

    public static <T> T getController(FXMLLoader fxmlLoader, Class<T> controller) {
        return fxmlLoader.getController();
    }

    private static <T> T load(String resourcePath) {
        try {
            var url = FXMLLoaderUtils.class.getResource(resourcePath);
            if (url == null) {
                throw new IllegalArgumentException(
                        String.format("%s was not found in class path resources.", resourcePath));
            }
            return FXMLLoader.load(url);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed loading %s.", resourcePath), e);
        }
    }


}
