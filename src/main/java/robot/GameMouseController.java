package robot;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import localization.Localizator;
import log.Logger;

/**
 * Контроллер для управления игрой
 */
public class GameMouseController implements RobotController {
	/**
	 * Экземпляр игры
	 */
	private final GameManager manager;
	
	/**
	 * Локализатор
	 */
	private final Localizator localizator = Localizator.getInstance();
	
	/**
	 * Создать контроллер
	 */
	public GameMouseController(GameManager gameManager) {
		manager = gameManager;
	}

	@Override
	public void changeModelState(InputEvent evt) {
		if (evt instanceof MouseEvent mouseEvent) {
			int newTargetX = mouseEvent.getX();
			int newTargetY = mouseEvent.getY();
			GameData gameData = manager.getModel().getCurrentState();
			GameData newGameData = new GameData(newTargetX, newTargetY, 
					gameData.robotPositionX(), gameData.robotPositionY(), 
					gameData.robotDirection(), 0);
			manager.changeModelState(newGameData);
			Logger.info(localizator.getString("log.message.target_location", 
					gameData.targetPositionX(), gameData.targetPositionY()));
		}
	}
}
