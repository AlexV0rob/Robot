package robot;

import java.beans.PropertyChangeListener;

/**
 * Представление робота
 */
public interface RobotView extends PropertyChangeListener {
	/**
	 * Поменять состояние представления
	 */
	void changeState(GameData gameData);
}
