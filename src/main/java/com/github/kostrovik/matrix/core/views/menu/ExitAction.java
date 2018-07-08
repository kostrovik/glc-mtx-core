package com.github.kostrovik.matrix.core.views.menu;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    08/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
public class ExitAction implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
        System.exit(0);
    }
}
