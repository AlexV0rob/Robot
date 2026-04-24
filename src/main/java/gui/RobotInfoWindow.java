package gui;

import javax.swing.*;

import localization.Localizator;
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
	 * Изменение языка
	 */
	private final static String LANGUAGE_CHANGE = "language_change";    
    /**
     * Помощник для сохранения состояния
     */
    private final WindowStateHandler stateHandler;
    
    /**
     * Локализатор
     */
    private final Localizator localizator = Localizator.getInstance();

    /**
     * Создать окно с информацией об игре
     */
    public RobotInfoWindow(WindowStateHandler windowStateHandler, 
    		RobotInfoVisualizer robotVisualizer) {
        super("", true, true, true, true);
        setTitle(localizator.getString("info.name"));
        stateHandler = windowStateHandler;
        robotVisualizer.setSize(300, 200);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(robotVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        localizator.addPropertyChangeListener(this);
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(LANGUAGE_CHANGE)) {
			setTitle(localizator.getString("info.name"));
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
