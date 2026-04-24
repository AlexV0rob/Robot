package gui;

import robot.GameData;
import robot.GameManager;
import robot.RobotController;
import robot.RobotView;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

/**
 * Визуализатор игрового состояния
 */
public class GameVisualizer extends JPanel implements RobotView {
	/**
	 * Игровые данные
	 */
	private final static String GAME_DATA = "game_data";
	/**
	 * Обновление правил рисования
	 */
	private final static String DRAWER_UPDATE = "drawer_update";
	
	/**
	 * Контроллер для управления положением цели
	 */
	private final RobotController controller;
	
	/**
	 * Текущее состояние игры
	 */
	private GameData gameData;
	
	/**
	 * Правила отрисовки робота
	 */
	private RobotDrawer drawer;
    
	/**
	 * Создать визуализатор игры
	 */
    public GameVisualizer(RobotController gameController, GameManager gameManager) {
        gameManager.addPropertyChangeListener(this);
        drawer = gameManager.getDrawer();
    	controller = gameController;
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                controller.changeModelState(e);
            }
        });
        setDoubleBuffered(true);
        gameManager.updateModel();
    }

    @Override
	public void propertyChange(PropertyChangeEvent evt) {
    	String eventString = evt.getPropertyName();
    	Object dataRaw = evt.getNewValue();
    	switch (eventString) {
    	case GAME_DATA -> {
        	if (dataRaw instanceof GameData newGameData) {
        		gameData = newGameData;
        	}
    	}
    	case DRAWER_UPDATE -> {
    		if (dataRaw instanceof RobotDrawer newRobotDrawer) {
    			drawer = newRobotDrawer;
    		}
    	}
    	}
        EventQueue.invokeLater(this::repaint);
	}
    
    private static int round(double value)
    {
        return (int)(value + 0.5);
    }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g; 
        if (gameData != null) {
        	drawer.drawRobot(g2d, round(gameData.robotPositionX()),
        			round(gameData.robotPositionY()),
        			gameData.robotDirection());
        	drawer.drawTarget(g2d, gameData.targetPositionX(), gameData.targetPositionY());
        }
    }
    
	@Override
	public void changeState(GameData gameData) {
		this.gameData = gameData;
	}
}
