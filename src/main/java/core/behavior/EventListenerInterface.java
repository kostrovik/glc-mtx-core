package core.behavior;

import java.util.EventObject;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    03/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 *
 * Обозначает сущности системы которые должны реагировать на возникающие события.
 */
public interface EventListenerInterface<E extends EventObject> {
    /**
     * Вызывается как реакция на событие.
     * @param event
     */
    void handleEvent(E event);
}
