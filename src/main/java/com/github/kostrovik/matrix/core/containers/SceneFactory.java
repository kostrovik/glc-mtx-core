package com.github.kostrovik.matrix.core.containers;

import com.github.kostrovik.configurator.interfaces.ModuleConfiguratorInterface;
import com.github.kostrovik.matrix.core.application.ApplicationModulesConfigurator;
import com.github.kostrovik.matrix.core.application.behavior.EventListenerInterface;
import com.github.kostrovik.matrix.core.views.ContentBuilderInterface;
import com.github.kostrovik.matrix.core.views.ContentViewInterface;
import com.github.kostrovik.matrix.core.views.menu.MenuBuilderInterface;
import com.sun.javafx.scene.control.Properties;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    03/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
public final class SceneFactory implements EventListenerInterface<EventObject> {
    private static Logger logger = LogManager.getLogger(SceneFactory.class);
    private static Stage mainWindow;
    private static Map<String, ContentViewInterface> storage;
    private ApplicationModulesConfigurator modulesConfigurator;
    private Map<String, ModuleConfiguratorInterface> configs = new ConcurrentHashMap<>();

    public SceneFactory(Stage mainWindow, ApplicationModulesConfigurator modulesConfigurator) {
        this.modulesConfigurator = modulesConfigurator;

        if (SceneFactory.mainWindow == null) {
            SceneFactory.mainWindow = mainWindow;
        }
        if (SceneFactory.storage == null) {
            SceneFactory.storage = new ConcurrentHashMap<>();
        }
    }

    @Override
    public void handleEvent(EventObject event) {

    }

    public void initScene(String moduleName, String viewName, EventObject event) {
        Scene scene = getSceneTemplate(moduleName);
        Pane content = (Pane) scene.lookup("#scene-content");

        ContentViewInterface contentView;
        if (storage.containsKey(moduleName + "_" + viewName)) {
            contentView = storage.get(moduleName + "_" + viewName);
            contentView.initView(event);
        } else {
            contentView = createView(moduleName, viewName, event, content);
        }


        if (contentView != null && contentView.getView() != null) {
            content.getChildren().setAll(contentView.getView());
        } else {
            content.getChildren().setAll(errorCreateScene(content));
        }

        mainWindow.setScene(scene);
    }

    private ModuleConfiguratorInterface buildConfigs(String moduleName) {
        if (configs.containsKey(moduleName)) {
            return configs.get(moduleName);
        }

        ModuleConfiguratorInterface moduleConfig = null;

        String configClassName = (String) modulesConfigurator.getConfig().get(moduleName);

        Class<?> configClass;
        try {
            configClass = Class.forName(configClassName);
            Constructor<?> constructor = configClass.getDeclaredConstructor();
            moduleConfig = (ModuleConfiguratorInterface) constructor.newInstance();
        } catch (ClassNotFoundException e) {
            logger.error(String.format("Для view не найден класс builder %s.", configClassName), e);
        } catch (NoSuchMethodException e) {
            logger.error("Не задан конструктор для builder с необходимымой сигнатурой getDeclaredConstructor().", e);
        } catch (IllegalAccessException e) {
            logger.error("Конструктор для builder не доступен.", e);
        } catch (InstantiationException | InvocationTargetException e) {
            logger.error(String.format("Не возможно создать объект builder %s.", configClassName), e);
        }

        configs.put(moduleName, moduleConfig);

        return moduleConfig;
    }

    private ContentBuilderInterface prepareViewBuilder(String moduleName, String viewName, EventObject event, Pane content) {
        ContentBuilderInterface contentBuilder = null;

        Map<String, String> views = buildConfigs(moduleName).getViews();

        String builderClassName = views.get(viewName);
        Class<?> builderClass;
        try {
            builderClass = Class.forName(builderClassName);
            Constructor<?> constructor = builderClass.getDeclaredConstructor(EventObject.class, Pane.class);
            contentBuilder = (ContentBuilderInterface) constructor.newInstance(event, content);
        } catch (ClassNotFoundException e) {
            logger.error(String.format("Для view не найден класс builder %s.", builderClassName), e);
        } catch (NoSuchMethodException e) {
            logger.error(String.format("Не задан конструктор для builder с необходимымой сигнатурой getDeclaredConstructor(%s, %s).", EventObject.class.getName(), Pane.class.getName()), e);
        } catch (IllegalAccessException e) {
            logger.error("Конструктор для builder не доступен.", e);
        } catch (InstantiationException | InvocationTargetException e) {
            logger.error(String.format("Не возможно создать объект builder %s.", builderClassName), e);
        }

        return contentBuilder;
    }

    private ContentViewInterface createView(String moduleName, String viewName, EventObject event, Pane content) {
        ContentBuilderInterface contentBuilder = prepareViewBuilder(moduleName, viewName, event, content);
        return contentBuilder != null ? contentBuilder.build(event, content) : null;
    }

    private Scene getSceneTemplate(String moduleName) {
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);

        Pane content = new Pane();
        content.setId("scene-content");
        vbox.getChildren().addAll(getSceneMenu(), content);

        setBackground(content);

        content.prefWidthProperty().bind(vbox.widthProperty());
        content.prefHeightProperty().bind(vbox.heightProperty());

        return scene;
    }

    private MenuBar getSceneMenu() {
        MenuBar menuBar = new MenuBar();
        menuBar.setPadding(new Insets(0, 0, 0, 0));

        for (String moduleName : modulesConfigurator.getConfig().keySet()) {
            MenuBuilderInterface menu = prepareMenuBuilder(moduleName);
            List<MenuItem> menuItems = menu.getMenu();

            Menu addDataMenu = new Menu(moduleName);
            addDataMenu.getItems().addAll(menuItems);

            menuBar.getMenus().add(addDataMenu);
        }

        return menuBar;
    }

    private MenuBuilderInterface prepareMenuBuilder(String moduleName) {
        MenuBuilderInterface menuBuilder = null;

        Map<String, Object> menu = buildConfigs(moduleName).getModuleMenu();

        String builderClassName = (String) menu.get("builder");
        Class<?> builderClass;
        try {
            builderClass = Class.forName(builderClassName);
            Constructor<?> constructor = builderClass.getDeclaredConstructor(ModuleConfiguratorInterface.class);
            menuBuilder = (MenuBuilderInterface) constructor.newInstance(buildConfigs(moduleName));
        } catch (ClassNotFoundException e) {
            logger.error(String.format("Для меню не найден класс builder %s.", builderClassName), e);
        } catch (NoSuchMethodException e) {
            logger.error(String.format("Не задан конструктор для builder с необходимымой сигнатурой getDeclaredConstructor(%s).", ModuleConfiguratorInterface.class.getName()), e);
        } catch (IllegalAccessException e) {
            logger.error("Конструктор для builder не доступен.", e);
        } catch (InstantiationException | InvocationTargetException e) {
            logger.error(String.format("Не возможно создать объект builder %s.", builderClassName), e);
        }

        return menuBuilder;
    }

    private Region errorCreateScene(Pane parent) {
        ScrollPane view = new ScrollPane();
        setBackground(view);

        view.prefWidthProperty().bind(parent.widthProperty());
        view.prefHeightProperty().bind(parent.heightProperty());

        Text value = new Text();
        value.setFill(Color.ORANGE);
        value.setText("Не возможно создать страницу.");
        value.setFont(Font.font(18));

        StackPane textHolder = new StackPane(value);

        textHolder.prefWidthProperty().bind(parent.widthProperty().subtract(Properties.DEFAULT_WIDTH));
        textHolder.prefHeightProperty().bind(parent.heightProperty().subtract(Properties.DEFAULT_WIDTH));

        view.setContent(textHolder);

        view.viewportBoundsProperty().addListener((arg0, arg1, arg2) -> {
            Node content = view.getContent();
            view.setFitToWidth(content.prefWidth(-1) < arg2.getWidth());
            view.setFitToHeight(content.prefHeight(-1) < arg2.getHeight());
        });

        return view;
    }

    private void setBackground(Region container) {
        container.setBackground(Background.EMPTY);
    }
}
