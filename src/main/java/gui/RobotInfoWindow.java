package gui;

import javax.swing.*;

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
     * Создать окно с информацией об игре
     */
    public RobotInfoWindow(WindowStateHandler windowStateHandler, RobotGame robotGame) 
    {
        super("Информация о роботе", true, true, true, true);
        robot = robotGame;
        stateHandler = windowStateHandler;
        this.infoContent = new TextArea("");
        this.infoContent.setSize(300, 200);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(infoContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        robot.addPropertyChangeListener(this);      
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object gameDataRaw = evt.getNewValue();
    	if (gameDataRaw instanceof GameData newGameData) {
    		StringBuilder content = new StringBuilder();
    		content.append("Robot X: ").append(newGameData.robotPositionX()).append("\n");
    		content.append("Robot Y: ").append(newGameData.robotPositionY()).append("\n");
    		content.append("Robot direction: ").append(newGameData.robotDirection()).append("\n");
        	infoContent.setText(content.toString());
        	infoContent.invalidate();
    	}
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
		return "info";
	}
}
