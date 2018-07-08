package com.github.kostrovik.matrix.core.views;

import javafx.scene.layout.Region;

import java.util.EventObject;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    03/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
public interface ContentViewInterface {
    void initView(EventObject event);

    Region getView();
}
