package gui;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import state.StateHandleHelper;
import state.StateSaveable;
import state.WindowState;

import javax.swing.*;
import java.awt.*;

public class LogWindow extends JInternalFrame implements LogChangeListener, StateSaveable
{
    private LogWindowSource logSource;
    private TextArea logContent;
    
    /**
     * Помощник для сохранения состояния
     */
    private final StateHandleHelper stateHelper;

    public LogWindow(LogWindowSource logSource, StateHandleHelper stateHandleHelper)
    {
        super("Протокол работы", true, true, true, true);
        this.logSource = logSource;
        this.logSource.registerListener(this);
        this.logContent = new TextArea("");
        this.logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
        
        stateHelper = stateHandleHelper;
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        logContent.setText(content.toString());
        logContent.invalidate();
    }
    
    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
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
