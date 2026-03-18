package robot;

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
	}
}
