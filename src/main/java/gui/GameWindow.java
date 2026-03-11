package gui;

import javax.swing.*;

import state.StateHandleHelper;
import state.StateSaveable;
import state.WindowState;

import java.awt.*;

public class GameWindow extends JInternalFrame implements StateSaveable
{
    private final GameVisualizer gameVisualizer;
    
    /**
     * Помощник для сохранения состояния
     */
    private final StateHandleHelper stateHelper;

    public GameWindow(StateHandleHelper stateHandleHelper) 
    {
        super("Игровое поле", true, true, true, true);
        gameVisualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        
        stateHelper = stateHandleHelper;
    }

	@Override
	public WindowState saveState() {
		return stateHelper.saveJInternalFrameState(this);
	}

	@Override
	public void recoverState(WindowState windowState) {
		stateHelper.recoverJInternalFrameState(this, windowState);
	}
}
