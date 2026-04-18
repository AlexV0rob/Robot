package gui;

import javax.swing.*;

import localization.Localizator;
import robot.GameData;
import robot.RobotGame;
import state.WindowStateHandler;
import state.StateSaveable;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * Отображение информации о роботе
 */
public class RobotInfoWindow extends JInternalFrame implements StateSaveable, PropertyChangeListener {    
    /**
     * Логика робота
     */
    private final RobotGame robot;
    
    /**
     * Помощник для сохранения состояния
     */
    private final WindowStateHandler stateHandler;
    
    /**
     * Текстовое поле с информацией о роботе
     */
    private TextArea infoContent;
    
	/**
	 * Текущее состояние игры
	 */
	private GameData gameData = null;
    
    /**
     * Локализатор
     */
    private final Localizator localizator = Localizator.getInstance();

    /**
     * Создать окно с информацией об игре
     */
    public RobotInfoWindow(WindowStateHandler windowStateHandler, RobotGame robotGame) {
        super("", true, true, true, true);
        setTitle(localizator.getString("info.name"));
        robot = robotGame;
        stateHandler = windowStateHandler;
        this.infoContent = new TextArea("");
        this.infoContent.setSize(300, 200);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(infoContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        robot.addPropertyChangeListener(this);
        localizator.addPropertyChangeListener(this);
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("GameData")) {
			robotPropertyChange(evt);
		} else if (evt.getPropertyName().equals("LanguageChange")) {
			languagePropertyChange(evt);
		}
	}
	
	/**
	 * Изменить язык
	 */
	private void languagePropertyChange(PropertyChangeEvent evt) {
		setTitle(localizator.getString("info.name"));
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
        	infoContent.setText(content.toString());
        	infoContent.invalidate();
		}
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
		return "info";
	}
}
