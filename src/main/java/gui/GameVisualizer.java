package gui;

import javax.swing.*;

import robot.GameController;
import robot.GameData;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Визуализатор игрового состояния
 */
public class GameVisualizer extends JPanel implements PropertyChangeListener {
	/**
	 * Текущее состояние игры
	 */
	private GameData gameData = null;
	
	/**
	 * Контроллер для управления положением цели
	 */
	private final GameController controller;
    
	/**
	 * Создать визуализатор игры
	 */
    public GameVisualizer(GameController gameController) 
    {
    	controller = gameController;
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                controller.setTargetPosition(e.getPoint().x, e.getPoint().y);
            }
        });
        setDoubleBuffered(true);
    }

    @Override
	public void propertyChange(PropertyChangeEvent evt) {
    	Object gameDataRaw = evt.getNewValue();
    	if (gameDataRaw instanceof GameData newGameData) {
    		gameData = newGameData;
            EventQueue.invokeLater(this::repaint);
    	}
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
        	drawRobot(g2d, round(gameData.robotPositionX()),
        			round(gameData.robotPositionY()),
        			gameData.robotDirection());
        	drawTarget(g2d, gameData.targetPositionX(), gameData.targetPositionY());
        }
    }
    
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        int robotCenterX = x; 
        int robotCenterY = y;
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY); 
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
    }
    
    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0); 
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
