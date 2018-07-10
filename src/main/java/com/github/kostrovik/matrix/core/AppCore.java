package com.github.kostrovik.matrix.core;

import com.github.kostrovik.matrix.core.application.ApplicationModulesConfigurator;
import com.github.kostrovik.matrix.core.containers.SceneFactory;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    01/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
public class AppCore extends Application {
    private static Logger logger = LogManager.getLogger(AppCore.class);
    private static ApplicationModulesConfigurator modulesConfigurator;

    public static void main(String[] args) {
        Map<String, List<String>> inputParams = parseInputParams(args);

        if (inputParams.containsKey("-modules")) {
            modulesConfigurator = new ApplicationModulesConfigurator(inputParams.get("-modules").get(0));
        } else {
            modulesConfigurator = new ApplicationModulesConfigurator();
        }

        launch(args);
    }

    public void start(Stage mainWindow) {
        logger.info("Запуск приложения.");

        SceneFactory factory = new SceneFactory(mainWindow, modulesConfigurator);
        factory.initScene("core", "main", new EventObject(new Object()));
        mainWindow.setTitle("Glance Matrix");

        setStageSize(mainWindow);

        mainWindow.show();

        // Завершение приложения при закрытии окна.
        mainWindow.setOnHidden(event -> System.exit(0));
    }

    /**
     * Устанавливает размеры главного окна на всю ширину и высоту экрана
     *
     * @param stage Объект главного окна приложения
     */
    private void setStageSize(Stage stage) {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());
    }

    private static Map<String, List<String>> parseInputParams(String... args) {
        Map<String, List<String>> inputParams = new ConcurrentHashMap<>();
        String cachedParam = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (!inputParams.containsKey(args[i])) {
                    inputParams.put(args[i], new ArrayList<>());
                }
                cachedParam = args[i];
            } else {
                if (!cachedParam.isEmpty()) {
                    inputParams.get(cachedParam).add(args[i]);
                }
            }
        }
        return inputParams;
    }
}