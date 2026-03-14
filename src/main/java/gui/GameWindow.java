package gui;

import javax.swing.*;

import state.WindowStateHandler;
import state.StateSaveable;

import java.awt.*;
import java.util.Map;

public class GameWindow extends JInternalFrame implements StateSaveable
{
    private final GameVisualizer gameVisualizer;
    
    /**
     * Помощник для сохранения состояния
     */
    private final WindowStateHandler stateHandler;

    public GameWindow(WindowStateHandler windowStateHandler) 
    {
        super("Игровое поле", true, true, true, true);
        gameVisualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        
        stateHandler = windowStateHandler;
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
