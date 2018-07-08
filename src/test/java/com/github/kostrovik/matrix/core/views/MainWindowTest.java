package com.github.kostrovik.matrix.core.views;

import com.github.kostrovik.matrix.core.containers.SceneFactory;
import com.github.kostrovik.matrix.core.application.Configurator;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.service.query.NodeQuery;

import java.util.EventObject;

import static org.testfx.assertions.api.Assertions.assertThat;


/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    04/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
@ExtendWith(ApplicationExtension.class)
public class MainWindowTest extends ApplicationExtension {
    private Configurator configurator;

    @Start
    void onStart(Stage mainWindow) {
        configurator = new Configurator();
        SceneFactory factory = new SceneFactory(mainWindow, configurator);
        factory.initScene("main", new EventObject(new Object()));
        mainWindow.setTitle("Glance Matrix");

        mainWindow.show();
    }

    @Test
    void mainWindowTest() {
        NodeQuery content = lookup("#page-text");

        assertThat(content.queryAs(Text.class)).hasText("Для начала работы обратитесь к основному меню.");
    }
}
