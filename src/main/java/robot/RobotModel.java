package robot;

/**
 * Модель робота
 */
public interface RobotModel {	
	/**
	 * Поменять состояние модели
	 */
	void changeState(GameData gameData);
	
	/**
	 * Получить текущее состояние
	 */
	GameData getCurrentState();
	
	/**
	 * Обновить модель
	 */
	void updateModel();
}
