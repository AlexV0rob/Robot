package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);
        
        
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
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
}
