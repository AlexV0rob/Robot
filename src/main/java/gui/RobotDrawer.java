package gui;

import java.awt.Graphics2D;
/**
 * Список правил рисования робота
 */
public interface RobotDrawer {
	/**
	 * Нарисовать робота
	 */
	void drawRobot(Graphics2D g, int x, int y, double direction);
	
	/**
	 * Нарисовать цель
	 */
	void drawTarget(Graphics2D g, int x, int y);
}
