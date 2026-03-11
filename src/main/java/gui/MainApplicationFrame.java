package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import state.*;

public class MainApplicationFrame extends JFrame implements StateSaveable
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    
    /**
     * Список окон с сохраняемым состоянием
     */
    private final List<StateSaveable> saveableWindows;
    
    /**
     * Список ключей для сохранения/восстановления состояний окон
     */
    private final List<String> windowsKeys;
    
    /**
     * Файловый обработчик состояний окон 
     */
    private final StateFileHandler stateFileHandler = new StateFileHandler();
    
    /**
     * Помощник для сохранения сосоятояния
     */
    private final StateHandleHelper stateHandleHelper = new StateHandleHelper();
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setContentPane(desktopPane);
        
        saveableWindows = new ArrayList<>();
        windowsKeys = new ArrayList<>();
        
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow(stateHandleHelper);
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        saveableWindows.add(this);
        windowsKeys.add("main");
        saveableWindows.add(logWindow);
        windowsKeys.add("log");
        saveableWindows.add(gameWindow);
        windowsKeys.add("game");
        
        recoverWindowsStates();
        
        addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		createClosingApprovalWindow();
        	}
        });
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(
        		Logger.getDefaultLogSource(), stateHandleHelper);
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Создать элемент меню
     */
    private JMenuItem createMenuItem(String name, int mnemonic, 
    		ActionListener actionListener) {
    	JMenuItem menuItem = new JMenuItem(name, mnemonic);
        menuItem.addActionListener(actionListener);
        return menuItem;
    }
    
    /**
     * Сгенерировать меню с настройками игры
     */
    private JMenu generateGameMenu() {
    	JMenu menu = new JMenu("Игра");
        menu.setMnemonic(KeyEvent.VK_G);
        menu.getAccessibleContext().setAccessibleDescription(
                "Настройки игры");
        
        menu.add(createMenuItem(
        		"Выйти",
        		KeyEvent.VK_E,
        		(event) -> {
        			Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
        			        new WindowEvent(
        			        		MainApplicationFrame.this, 
        			        		WindowEvent.WINDOW_CLOSING));
        		}));
        return menu;
    }
    
    /**
     * Сгенерировать меню с настройками отображения
     */
    private JMenu generateViewMenu() {
    	JMenu menu = new JMenu("Режим отображения");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");
        
        menu.add(createMenuItem(
        		"Системная схема",
        		KeyEvent.VK_S,
        		(event) -> {
        			setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        			this.invalidate();
        		}));

        menu.add(createMenuItem(
        		"Универсальная схема",
        		KeyEvent.VK_S,
        		(event) -> {
        			setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        			this.invalidate();
        		}));
        return menu;
    }
    
    /**
     * Сгенерировать меню с тестами
     */
    private JMenu generateTestsMenu() {
    	JMenu menu = new JMenu("Тесты");
        menu.setMnemonic(KeyEvent.VK_T);
        menu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");
        
        menu.add(createMenuItem(
        		"Сообщение в лог",
        		KeyEvent.VK_S,
        		(event) -> {
        			Logger.debug("Новая строка");
        		}));
        return menu;
    }
    
    /**
     * Создать строку меню
     */
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(generateGameMenu());
        menuBar.add(generateViewMenu());
        menuBar.add(generateTestsMenu());
        return menuBar;
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
    
    /**
     * Сохранить состояния окон
     */
    private void saveWindowsStates() {
    	Map<String, WindowState> windowsStates = new HashMap<>();
    	for (int i = 0; i < saveableWindows.size(); ++i) {
    		windowsStates.put(windowsKeys.get(i), saveableWindows.get(i).saveState());
    	}
    	try {
    		stateFileHandler.writeStates(windowsStates, windowsKeys, "");
    	} catch (StateHandleException e) {
    		JOptionPane.showConfirmDialog(MainApplicationFrame.this, 
    				e.getMessage(), "Ошибка!", 
    				JOptionPane.CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    /**
     * Восстановить состояния окон
     */
    private void recoverWindowsStates() {
    	Map<String, WindowState> windowsStates;
    	try {
    		windowsStates = stateFileHandler.readStates(windowsKeys, "");
    		for (int i = 0; i < windowsKeys.size(); ++i) {
    			WindowState windowState = windowsStates.get(windowsKeys.get(i));
    			if (windowState != null) {
    				saveableWindows.get(i).recoverState(windowState);
    			}
        	}
    	} catch (StateHandleException e) {
    		JOptionPane.showConfirmDialog(MainApplicationFrame.this, 
    				e.getMessage(), "Ошибка!", 
    				JOptionPane.CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    /**
     * Показать окно подтверждения выхода
     */
    private void createClosingApprovalWindow() {
    	String[] buttons_names = {"Да", "Нет"};
		int result = JOptionPane.showOptionDialog(MainApplicationFrame.this, 
                "Вы действительно хотите выйти?", "Внимание!",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, buttons_names, buttons_names[1]);
		if (result == JOptionPane.YES_OPTION) {
			saveWindowsStates();
			System.exit(0);
		}
    }

	@Override
	public WindowState saveState() {
		return stateHandleHelper.saveJFrameState(this);
	}

	@Override
	public void recoverState(WindowState windowState) {
		stateHandleHelper.recoverJFrameState(this, windowState);
	}
}
