package robot;

import java.util.Timer;
import java.util.TimerTask;

import localization.Localizator;
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
	 * Локализатор
	 */
	private final Localizator localizator = Localizator.getInstance();
    
	/**
	 * Таймер для управления игрой
	 */
    private final Timer timer = initTimer();
	
	/**
	 * Создать контроллер
	 */
	public GameController(RobotGame robotGame) {
		game = robotGame;
	}
	
	/**
	 * Создать таймер
	 */
	private static Timer initTimer() 
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }
	
	/**
	 * Начать игру
	 */
	public void startGame() {
		timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                game.updateModel();
            }
        }, 0, 10);
	}
	
	/**
	 * Поменять положение точки
	 */
	public void setTargetPosition(int targetPositionX, int targetPositionY) {
		game.changeTargetPosition(targetPositionX, targetPositionY);
		Logger.info(localizator.getString("log.message.target_location", 
				targetPositionX, targetPositionY));
	}
}
