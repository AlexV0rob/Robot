package gui;

import javax.swing.*;

import robot.GameController;
import robot.RobotGame;
import state.WindowStateHandler;
import state.StateSaveable;

import java.awt.*;
import java.util.Map;

public class GameWindow extends JInternalFrame implements StateSaveable {
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
     * Создать окно с игрой
     */
    public GameWindow(WindowStateHandler windowStateHandler, RobotGame robotGame) {
        super("Игровое поле", true, true, true, true);
        robot = robotGame;
        controller = new GameController(robot);
        stateHandler = windowStateHandler;
        gameVisualizer = new GameVisualizer(controller);
        robot.addPropertyChangeListener(gameVisualizer);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

	@Override
	public Map<String, String> saveState() {
		return stateHandler.saveJInternalFrameState(this);
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
