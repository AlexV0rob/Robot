package gui;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

import state.WindowStateHandler;
import state.StateSaveable;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LogWindow extends JInternalFrame implements LogChangeListener, StateSaveable
{
    private LogWindowSource logSource;
    private TextArea logContent;
    
    /**
     * Помощник для сохранения состояния
     */
    private final WindowStateHandler stateHandler;

    public LogWindow(LogWindowSource logSource, WindowStateHandler windowStateHandler)
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
        
        stateHandler = windowStateHandler;
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
	public Map<String, String> saveState() {
		return stateHandler.saveJInternalFrameState(this);
	}

	@Override
	public void recoverState(Map<String, String> windowState) {
		stateHandler.recoverJInternalFrameState(this, windowState);
	}

	@Override
	public String sayMyName() {
		return "log";
	}
}
