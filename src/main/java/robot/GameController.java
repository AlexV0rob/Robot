package robot;

import log.Logger;

/**
 * Контроллер для управления игрой
 */
public class GameController {
	/**
	 * Экземпляр игры
	 */
	private final RobotGame game;
	
	/**
	 * Создать контроллер
	 */
	public GameController(RobotGame robotGame) {
		game = robotGame;
	}
	
	/**
	 * Поменять положение точки
	 */
	public void setTargetPosition(int targetPositionX, int targetPositionY) {
		game.changeTargetPosition(targetPositionX, targetPositionY);
		Logger.info(String.format(
				"Новое положение цели: x=%d, y=%d", 
				targetPositionX, targetPositionY));
	}
}
