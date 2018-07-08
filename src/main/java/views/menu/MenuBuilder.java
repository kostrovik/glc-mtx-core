package views.menu;

import interfaces.ModuleConfiguratorInterface;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    07/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
public class MenuBuilder implements MenuBuilderInterface {
    private static Logger logger = LogManager.getLogger(MenuBuilder.class);
    private ModuleConfiguratorInterface configurator;

    public MenuBuilder(ModuleConfiguratorInterface configurator) {
        this.configurator = configurator;
    }

    @Override
    public List<MenuItem> getMenu() {
        Map<String, Object> config = configurator.getConfig();
        Map<String, Map> menuConfig = (Map<String, Map>) configurator.getModuleMenu().get("items");
        List<MenuItem> menuItems = new ArrayList<>();

        for (String titleKey : menuConfig.keySet()) {
            MenuItem item = new MenuItem((String) menuConfig.get(titleKey).get("title"));
            item.setId(String.format("%s_menu_item_%s", config.get("moduleName"), titleKey));

            item.setOnAction(prepareAction((String) menuConfig.get(titleKey).get("action")));

            menuItems.add(item);
        }

        return menuItems;
    }

    private EventHandler<ActionEvent> prepareAction(String actionClassName) {
        EventHandler<ActionEvent> action = null;

        Class<?> actionClass;
        try {
            actionClass = Class.forName(actionClassName);
            Constructor<?> constructor = actionClass.getDeclaredConstructor();
            action = (EventHandler<ActionEvent>) constructor.newInstance();
        } catch (ClassNotFoundException e) {
            logger.error(String.format("Для пункта меню не найден класс action %s.", actionClassName), e);
        } catch (NoSuchMethodException e) {
            logger.error("Не задан конструктор для action с необходимымой сигнатурой getDeclaredConstructor().", e);
        } catch (IllegalAccessException e) {
            logger.error("Конструктор для action не доступен.", e);
        } catch (InstantiationException | InvocationTargetException e) {
            logger.error(String.format("Не возможно создать объект action %s.", actionClassName), e);
        }

        return action;
    }
}
