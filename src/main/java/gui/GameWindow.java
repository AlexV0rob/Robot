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
 * Окно с визуальным отображением игры
 */
public class GameWindow extends JInternalFrame implements StateSaveable, PropertyChangeListener {
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
     * Создать окно с игрой
     */
    public GameWindow(WindowStateHandler windowStateHandler, GameVisualizer gameVisualizer) {
        super("", true, true, true, true);
        setTitle(localizator.getString("game.name"));
        stateHandler = windowStateHandler;
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        localizator.addPropertyChangeListener(this);
    }
    
    @Override
   	public void propertyChange(PropertyChangeEvent evt) {
    	if (evt.getPropertyName().equals(LANGUAGE_CHANGE)) {
			setTitle(localizator.getString("game.name"));
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
		return "game";
	}
}
