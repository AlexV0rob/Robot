package gui;

import log.Logger;
import robot.GameMouseController;
import robot.GameManager;
import robot.RobotController;
import robot.RobotModelDefault;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import loader.LoaderException;
import loader.RobotLoader;
import localization.Localizator;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import state.*;

public class MainApplicationFrame extends JFrame implements StateSaveable, PropertyChangeListener {
	/**
	 * Изменение языка
	 */
	private final static String LANGUAGE_CHANGE = "language_change";
	
    private final JDesktopPane desktopPane = new JDesktopPane();
    
    /**
     * Файловый обработчик состояний окон 
     */
    private final StateFileHandler stateFileHandler = new StateFileHandler();
    
    /**
     * Локализатор
     */
    private final Localizator localizator = Localizator.getInstance();
    
    /**
     * Помощник для сохранения сосоятояния
     */
    private final WindowStateHandler stateHandler = new WindowStateHandler();
    
    /**
     * Загрузчик классов роботов
     */
    private final RobotLoader robotLoader = new RobotLoader();
    
    /**
     * Менеджер игры
     */
    private final GameManager gameManager = new GameManager();
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
    	List<Map<String, String>> windowsStates = readWindowsStates();
    	recoverLocale(windowsStates);
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setContentPane(desktopPane);
        
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);
    
        RobotController controller = new GameMouseController(gameManager);
        GameVisualizer gameVisualizer = new GameVisualizer(controller, gameManager);
        RobotInfoVisualizer infoVisualizer = new RobotInfoVisualizer(gameManager);
        
        JInternalFrame gameWindow = new GameWindow(stateHandler, gameVisualizer);
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);
        
        JInternalFrame robotInfo = new RobotInfoWindow(stateHandler, infoVisualizer);
        robotInfo.setSize(300, 200);
        addWindow(robotInfo);

        setJMenuBar(generateMenuBar());
        
        recoverWindowsStates(windowsStates);
        
        localizator.addPropertyChangeListener(this);

        gameManager.startGame();
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		createClosingApprovalWindow();
        	}
        });
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(
        		Logger.getDefaultLogSource(), stateHandler);
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(localizator.getString("log.message.working"));
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
    	JMenu menu = new JMenu(localizator.getString("menu.game.name"));
        menu.setMnemonic(KeyEvent.VK_G);
        menu.getAccessibleContext().setAccessibleDescription(
                localizator.getString("menu.game.settings.name"));
        
        menu.add(createMenuItem(
        		localizator.getString("menu.game.settings.upload"),
        		KeyEvent.VK_U,
        		(event) -> {
        			uploadRobot();
        		}));
        
        menu.add(createMenuItem(
        		localizator.getString("menu.game.settings.exit"),
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
    	JMenu menu = new JMenu(localizator.getString("menu.view.name"));
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription(
                localizator.getString("menu.view.mode_manager.name"));
        
        menu.add(createMenuItem(
        		localizator.getString("menu.view.mode_manager.system"),
        		KeyEvent.VK_S,
        		(event) -> {
        			setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        			this.invalidate();
        		}));

        menu.add(createMenuItem(
        		localizator.getString("menu.view.mode_manager.universal"),
        		KeyEvent.VK_U,
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
    	JMenu menu = new JMenu(localizator.getString("menu.test.name"));
        menu.setMnemonic(KeyEvent.VK_T);
        menu.getAccessibleContext().setAccessibleDescription(
                localizator.getString("menu.test.commands.name"));
        
        menu.add(createMenuItem(
        		localizator.getString("menu.test.commands.message"),
        		KeyEvent.VK_S,
        		(event) -> {
        			Logger.debug(localizator.getString("log.message.new_string"));
        		}));
        return menu;
    }
    
    /**
     * Сгенерировать меню с настройками языка
     */
    private JMenu generateLangMenu() {
    	JMenu menu = new JMenu(localizator.getString("menu.lang.name"));
        menu.setMnemonic(KeyEvent.VK_L);
        menu.getAccessibleContext().setAccessibleDescription(
                localizator.getString("menu.lang.manager.name"));
        
        menu.add(createMenuItem(
        		localizator.getString("menu.lang.manager.ru"),
        		KeyEvent.VK_R,
        		(event) -> {
        			setLocale("ru_RU");
        			this.invalidate();
        		}));

        menu.add(createMenuItem(
        		localizator.getString("menu.lang.manager.en"),
        		KeyEvent.VK_E,
        		(event) -> {
        			setLocale("en_US");
        			this.invalidate();
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
        menuBar.add(generateLangMenu());
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
     * Сохранить текущую локаль
     */
    private Map<String, String> saveCurrentLocale() {
    	return Map.of("locale", localizator.getCurrentLocale());
    }
    
    /**
     * Восстановить локаль
     */
    private void recoverLocale(List<Map<String, String>> states) {
    	for (Map<String, String> state : states) {
 			String locale = state.get("locale");
 			if (locale != null) {
     			setLocale(locale);
     			break;
 			}
 		}
    }
    
    /**
     * Загрузить робота
     */
    private void uploadRobot() {
    	JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
        		new FileNameExtensionFilter(
        				localizator.getString("loader.file_picker.files"), 
        				"jar"));
        fileChooser.setDialogTitle(localizator.getString("loader.file_picker.name"));
        int returnVal = fileChooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	try {
        		robotLoader.loadRobotJar(fileChooser.getSelectedFile());
        		try {
        			gameManager.setModel(robotLoader.getRobotModelClass());
        		} catch (LoaderException e) {
        			gameManager.setModel(new RobotModelDefault());
        			showNotificationDialog(e.getMessage(), 
        					getJOptionPaneType(e.getCriticalLevel()),
        					getJOptionPaneMessageKey(e.getCriticalLevel()));
        		}
        		try {
        			gameManager.setDrawer(robotLoader.getRobotDrawerClass());        	        			
        		} catch (LoaderException e) {
        			gameManager.setDrawer(new RobotDrawerDefault());
        			showNotificationDialog(e.getMessage(), 
        					getJOptionPaneType(e.getCriticalLevel()),
        					getJOptionPaneMessageKey(e.getCriticalLevel()));
        		}
        	} catch (LoaderException e) {
    			showNotificationDialog(e.getMessage(), 
    					getJOptionPaneType(e.getCriticalLevel()),
    					getJOptionPaneMessageKey(e.getCriticalLevel()));
        	}
        }
    }
    
    /**
     * Получить код типа JOptionPane по критическому уровню загрузчика классов
     */
    private int getJOptionPaneType(int criticalLevel) {
    	return switch(criticalLevel) {
		case 0 -> JOptionPane.INFORMATION_MESSAGE;
		case 1 -> JOptionPane.WARNING_MESSAGE;
		case 2 -> JOptionPane.ERROR_MESSAGE;
		default -> JOptionPane.INFORMATION_MESSAGE;
		};
    }
    
    /**
     * Получить ключ сообщения JOptionPane по критическому уровню загрузчика классов
     */
    private String getJOptionPaneMessageKey(int criticalLevel) {
    	return switch(criticalLevel) {
		case 0 -> "error.notification";
		case 1 -> "error.warning";
		case 2 -> "error.error";
		default -> "error.notification";
		};
    }
    
    /**
     * Установить локаль
     */
    private void setLocale(String locale) {
		localizator.changeLocale(Locale.of(locale));
    }
    
    /**
     * Сохранить состояния окон
     */
    private void saveWindowsStates() {
    	List<Map<String, String>> windowsStates = new ArrayList<>();
    	windowsStates.add(saveCurrentLocale());
    	Map<String, StateSaveable> saveableWindows = findAllSaveableWindows();
    	for (Map.Entry<String, StateSaveable> window : saveableWindows.entrySet()) {
    		windowsStates.add(window.getValue().saveState());
    	}
    	try {
    		stateFileHandler.writeStates(windowsStates, "");
    	} catch (StateHandleException e) {
    		showNotificationDialog(e.getMessage(), JOptionPane.ERROR_MESSAGE, "error.error");
    	}
    }
    
    /**
     * Получить состояния окон из файла
     */
    private List<Map<String, String>> readWindowsStates() {
    	try {
    		return stateFileHandler.readStates("");
    	} catch (StateHandleException e) {
    		showNotificationDialog(e.getMessage(), JOptionPane.ERROR_MESSAGE, "error.error");
    	}
    	return List.of();
    }
    
    /**
     * Восстановить состояния окон
     */
    private void recoverWindowsStates(List<Map<String, String>> windowsStates) {
        Map<String, StateSaveable> saveableWindows = findAllSaveableWindows();
    	for (Map<String, String> state : windowsStates) {
    		String name = state.get("name");
    		if (name != null) {
        		StateSaveable window = saveableWindows.get(name);
        		if (window != null) {
        			window.recoverState(state);
        		}
    		}
        }
    }
    
    /**
     * Показать окно ошибки при обработке состояний окон
     */
    private void showNotificationDialog(String message, int jOptionPaneStatus, String messageKey) {
		JOptionPane.showConfirmDialog(MainApplicationFrame.this, 
				message, localizator.getString(messageKey), 
				JOptionPane.CANCEL_OPTION, jOptionPaneStatus);    	
    }
    
    /**
     * Показать окно подтверждения выхода
     */
    private void createClosingApprovalWindow() {
    	String[] buttons_names = {
    			localizator.getString("close_window.yes"), 
    			localizator.getString("close_window.no")
    	};
		int result = JOptionPane.showOptionDialog(MainApplicationFrame.this, 
                localizator.getString("close_window.text"), 
                localizator.getString("close_window.name"),
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, buttons_names, buttons_names[1]);
		if (result == JOptionPane.YES_OPTION) {
			saveWindowsStates();
			System.exit(0);
		}
    }
    
    /**
     * Получить все сохраняемые окна
     */
    private Map<String, StateSaveable> findAllSaveableWindows() {
    	Map<String, StateSaveable> windows = new HashMap<>();
    	windows.put(sayMyName(), this);
    	Arrays.stream(this.getContentPane().getComponents()).forEach(component -> {
			if (component instanceof StateSaveable saveableComponent) {
				windows.put(saveableComponent.sayMyName(), saveableComponent);
			}
        });
    	return windows;
    }
    
    @Override
   	public void propertyChange(PropertyChangeEvent evt) {
    	if (evt.getPropertyName().equals(LANGUAGE_CHANGE)) {
        	setJMenuBar(generateMenuBar());
		}
   	}

	@Override
	public Map<String, String> saveState() {
		return stateHandler.saveJFrameState(this, sayMyName());
	}

	@Override
	public void recoverState(Map<String, String> windowState) {
		stateHandler.recoverJFrameState(this, windowState);
	}

	@Override
	public String sayMyName() {
		return "Heisenberg";
	}
}
