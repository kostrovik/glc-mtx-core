package containers;

import com.sun.javafx.scene.control.skin.ScrollBarSkin;
import core.Configurator;
import core.behavior.EventListenerInterface;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import views.ContentBuilderInterface;
import views.ContentViewInterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;
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
    private Configurator configurator;

    public SceneFactory(Stage mainWindow, Configurator configurator) {
        this.configurator = configurator;

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

    public void initScene(String viewName, EventObject event) {
        Scene scene = getSceneTemplate();
        Pane content = (Pane) scene.lookup("#scene-content");

        ContentViewInterface contentView;
        if (storage.containsKey(viewName)) {
            contentView = storage.get(viewName);
            contentView.initView(event);
        } else {
            contentView = createView(viewName, event, content);
        }


        if (contentView != null && contentView.getView() != null) {
            content.getChildren().setAll(contentView.getView());
        } else {
            content.getChildren().setAll(errorCreateScene(content));
        }

        mainWindow.setScene(scene);
    }

    private ContentBuilderInterface prepareBuilder(String viewName, EventObject event, Pane content) {

        ContentBuilderInterface contentBuilder = null;

        Map<String, String> views = configurator.getViews();

        String builderClassName = views.get("main");
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

    private ContentViewInterface createView(String viewName, EventObject event, Pane content) {
        ContentBuilderInterface contentBuilder = prepareBuilder(viewName, event, content);
        return contentBuilder != null ? contentBuilder.build(event, content) : null;
    }

    private Scene getSceneTemplate() {
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);

        Pane content = new Pane();
        content.setId("scene-content");
        vbox.getChildren().addAll(content);

        setBackground(content);

        content.prefWidthProperty().bind(vbox.widthProperty());
        content.prefHeightProperty().bind(vbox.heightProperty());

        return scene;
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

        textHolder.prefWidthProperty().bind(parent.widthProperty().subtract(ScrollBarSkin.DEFAULT_WIDTH));
        textHolder.prefHeightProperty().bind(parent.heightProperty().subtract(ScrollBarSkin.DEFAULT_WIDTH));

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
