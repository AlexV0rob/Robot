package gui;

import java.awt.EventQueue;
import java.awt.TextArea;
import java.beans.PropertyChangeEvent;

import localization.Localizator;
import robot.GameData;
import robot.GameManager;
import robot.RobotView;

/**
 * Визуализатор информации о роботе в виде текста
 */
public class RobotInfoVisualizer extends TextArea implements RobotView {
	/**
	 * Игровые данные
	 */
	private final static String GAME_DATA = "game_data";
	/**
	 * Смена языка
	 */
	private final static String LANGUAGE_CHANGE = "language_change";
	
	/**
	 * Текущее состояние игры
	 */
	private GameData gameData;
    
    /**
     * Локализатор
     */
    private final Localizator localizator = Localizator.getInstance();
    
    /**
     * Создать визуализатор информации о роботе
     */
    public RobotInfoVisualizer(GameManager gameManager) {
        gameManager.addPropertyChangeListener(this);
        localizator.addPropertyChangeListener(this);
        gameManager.updateModel();
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(GAME_DATA)) {
			robotPropertyChange(evt);
		} else if (evt.getPropertyName().equals(LANGUAGE_CHANGE)) {
			languagePropertyChange(evt);
		}
	}

	@Override
	public void changeState(GameData gameData) {
		this.gameData = gameData;
	}

	/**
	 * Изменить язык
	 */
	private void languagePropertyChange(PropertyChangeEvent evt) {
		EventQueue.invokeLater(this::redraw);
	}
	
	/**
	 * Изменить информацию о роботе
	 */
	private void robotPropertyChange(PropertyChangeEvent evt) {
		Object gameDataRaw = evt.getNewValue();
    	if (gameDataRaw instanceof GameData newGameData) {
    		gameData = newGameData;
    		EventQueue.invokeLater(this::redraw);
    	}
	}
	
	/**
	 * Нарисовать состояние
	 */
	private void redraw() {
		if (gameData != null) {
			StringBuilder content = new StringBuilder();
    		content.append(localizator.getString(
    				"info.robot_x", gameData.robotPositionX())).append("\n");
    		content.append(localizator.getString(
    				"info.robot_y", gameData.robotPositionY())).append("\n");
    		content.append(localizator.getString(
    				"info.robot_direction", gameData.robotDirection())).append("\n");
    		content.append(localizator.getString(
    				"info.angle_to_target", gameData.angleToTarget())).append("\n");
        	setText(content.toString());
        	invalidate();
		}
	}
}
