package robot;

import java.awt.event.InputEvent;

/**
 * Контроллер робота
 */
public interface RobotController {
	/**
	 * Обновить состояние модели по событию ввода
	 */
	void changeModelState(InputEvent evt);	
}
