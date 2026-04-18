package gui;

import javax.swing.*;

import localization.Localizator;
import robot.GameController;
import robot.GameData;
import robot.RobotGame;
import state.WindowStateHandler;
import state.StateSaveable;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

public class GameWindow extends JInternalFrame implements StateSaveable, PropertyChangeListener {
	/**
	 * Визулизатор игрового состояния
	 */
    private final GameVisualizer gameVisualizer;
    
    /**
     * Модель робота
     */
    private final RobotGame robot;
    
    /**
     * Контроллер игры
     */
    private final GameController controller;
    
    /**
     * Помощник для сохранения состояния
     */
    private final WindowStateHandler stateHandler;
    
    /**
     * Локализатор
     */
    private final Localizator localizator = Localizator.getInstance();

    /**
     * Создать окно с игрой
     */
    public GameWindow(WindowStateHandler windowStateHandler, RobotGame robotGame) {
        super("", true, true, true, true);
        setTitle(localizator.getString("game.name"));
        robot = robotGame;
        controller = new GameController(robot);
        controller.startGame();
        stateHandler = windowStateHandler;
        gameVisualizer = new GameVisualizer(controller);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        robot.addPropertyChangeListener(gameVisualizer);
        localizator.addPropertyChangeListener(this);
    }
    
    @Override
   	public void propertyChange(PropertyChangeEvent evt) {
        setTitle(localizator.getString("game.name"));
   	}

	@Override
	public Map<String, String> saveState() {
		return stateHandler.saveJInternalFrameState(this, sayMyName());
	}

	@Override
	public void recoverState(Map<String, String> windowState) {
		stateHandler.recoverJInternalFrameState(this, windowState);
	}

	@Override
	public String sayMyName() {
		return "game";
	}
}
