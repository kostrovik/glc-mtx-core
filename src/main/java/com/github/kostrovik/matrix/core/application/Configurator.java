package com.github.kostrovik.matrix.core.application;

import com.github.kostrovik.configurator.core.ModuleConfigurator;

import java.util.Map;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    06/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
public class Configurator extends ModuleConfigurator {

    public Map<String, String> getViews() {
        Map<String, Object> config = getConfig();

        return (Map<String, String>) config.get("views");
    }

    public Map<String, Object> getModuleMenu() {
        Map<String, Object> config = getConfig();

        return (Map<String, Object>) config.get("menu");
    }
}
