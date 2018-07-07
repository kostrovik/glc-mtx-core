package core;

import java.util.Map;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    06/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
public class Configurator extends ModuleConfigurator {
    public Map<String, String> getViews() {
        Map<String, Object> config = getModuleConfig();

        return (Map<String, String>) config.get("views");
    }

    private Map<String, Object> getModuleConfig() {
        return (Map<String, Object>) ((Map) getConfig().get("module")).get("core");
    }
}
