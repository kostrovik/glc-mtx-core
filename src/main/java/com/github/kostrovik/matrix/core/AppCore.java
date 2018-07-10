package com.github.kostrovik.matrix.core;

import com.github.kostrovik.configurator.interfaces.ModuleConfiguratorInterface;
import com.github.kostrovik.matrix.core.application.ApplicationModulesConfigurator;
import com.github.kostrovik.matrix.core.application.Configurator;
import com.github.kostrovik.matrix.core.containers.SceneFactory;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EventObject;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    01/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
public class AppCore extends Application {
    private static Logger logger = LogManager.getLogger(AppCore.class);
    private static ModuleConfiguratorInterface configurator;
    private static ApplicationModulesConfigurator modulesConfigurator;

    public static void main(String[] args) {
        configurator = new Configurator();
        modulesConfigurator = new ApplicationModulesConfigurator();

        launch(args);
    }

    public void start(Stage mainWindow) {
        logger.info("Запуск приложения.");

        SceneFactory factory = new SceneFactory(mainWindow, configurator, modulesConfigurator);
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
}