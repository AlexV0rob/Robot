package gui;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import log.Logger;
import state.WindowStateHandler;
import state.StateSaveable;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import localization.Localizator;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * Окно с логами
 */
public class LogWindow extends JInternalFrame 
implements LogChangeListener, StateSaveable, PropertyChangeListener {
	/**
	 * Изменение языка
	 */
	private final static String LANGUAGE_CHANGE = "language_change";
	
	/**
	 * Хранитель логов
	 */
    private LogWindowSource logSource;
    
    /**
     * Текстовое поле с логами
     */
    private TextArea logContent;
    
    /**
     * Помощник для сохранения состояния
     */
    private final WindowStateHandler stateHandler;
    
    /**
     * Локализатор
     */
    private final Localizator localizator = Localizator.getInstance();

    public LogWindow(LogWindowSource logSource, WindowStateHandler windowStateHandler)
    {
        super("", true, true, true, true);
        setTitle(localizator.getString("log.name"));
        this.logSource = logSource;
        this.logSource.registerListener(this);
        this.logContent = new TextArea("");
        this.logContent.setSize(200, 500);
        this.logContent.addKeyListener(new KeyAdapter() {
        	public void keyPressed(KeyEvent e) {
            	e.consume();
                if (e.getKeyCode() == KeyEvent.VK_T) {
                	Logger.debug(localizator.getString("log.message.test"));
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
        
        stateHandler = windowStateHandler;
        
        addInternalFrameListener(new InternalFrameAdapter() {
        	public void internalFrameClosing(InternalFrameEvent e) {
        		if (logSource != null) {
        			logSource.clearListeners();
        		}
        	}
        });
        localizator.addPropertyChangeListener(this);
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
   	public void propertyChange(PropertyChangeEvent evt) {
    	if (evt.getPropertyName().equals(LANGUAGE_CHANGE)) {
            setTitle(localizator.getString("log.name"));
            updateLogContent();
		}
   	}
    
    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
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
		return "log";
	}
}
