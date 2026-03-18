package io.playqd;

import io.playqd.core.ApplicationCloseHandler;
import io.playqd.core.ApplicationTitleUpdateListener;
import io.playqd.core.ApplicationUncaughtExceptionHandler;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application extends javafx.application.Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static final String TITLE = "playqd-fx";

    static {
//        System.setProperty("slf4j.internal.verbosity", "WARN");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        startInStage(primaryStage);
    }

    private static void startInStage(Stage stage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(new ApplicationUncaughtExceptionHandler());

        var fxmlLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.APPLICATION);

        var scene = new Scene(fxmlLoader.load());

        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setMaximized(true);
        scene.getStylesheets().addAll("css/glyphs.css", "css/tables.css", "css/music-library.css");

        ApplicationCloseHandler.register(stage);
        ApplicationTitleUpdateListener.register(stage);

        stage.setOnShown(_ -> {

        });

        stage.show();
    }

}
