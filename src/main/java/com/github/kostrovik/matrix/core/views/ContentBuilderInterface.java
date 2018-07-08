package com.github.kostrovik.matrix.core.views;

import javafx.scene.layout.Pane;

import java.util.EventObject;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    04/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
public interface ContentBuilderInterface {
    ContentViewInterface build(EventObject event, Pane content);
}
