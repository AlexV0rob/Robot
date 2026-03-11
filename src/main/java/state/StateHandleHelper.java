package state;

import java.beans.PropertyVetoException;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;

/**
 * Помощник обработки состояний, содержащий методы для сохранения и восстановления состояния
 */
public class StateHandleHelper {
	/**
	 * Сохранить состояние окна, унаследованного от JInternalFrame
	 */
	public WindowState saveJInternalFrameState(JInternalFrame frame) {
		return new WindowState(
				frame.getWidth(),
				frame.getHeight(),
				frame.getX(),
				frame.getY(),
				frame.isIcon());
	}
	
	/**
	 * Восстановить состояние окна, унаследованного от JInternalFrame
	 */
	public void recoverJInternalFrameState(JInternalFrame frame, WindowState windowState) {
		int width = windowState.width() >= 0 ? windowState.width() : frame.getWidth();
		int height = windowState.height() >= 0 ? windowState.height() : frame.getHeight();
		int x = windowState.x() >= 0 ? windowState.x() : frame.getX();
		int y = windowState.y() >= 0 ? windowState.y() : frame.getY();
		frame.setSize(width, height);
		frame.setLocation(x, y);
		try {
			frame.setIcon(windowState.isIconified());
		} catch (PropertyVetoException e) {
			//just ignore
		}
	}
	
	/**
	 * Сохранить состояние окна, унаследованного от JFrame
	 */
	public WindowState saveJFrameState(JFrame frame) {
		return new WindowState(
				frame.getWidth(),
				frame.getHeight(),
				frame.getX(),
				frame.getY(),
				(frame.getExtendedState() & JFrame.ICONIFIED) == JFrame.ICONIFIED);
	}
	
	/**
	 * Восстановить состояние окна, унаследованного от JFrame
	 */
	public void recoverJFrameState(JFrame frame, WindowState windowState) {
		int width = windowState.width() >= 0 ? windowState.width() : frame.getWidth();
		int height = windowState.height() >= 0 ? windowState.height() : frame.getHeight();
		int x = windowState.x() >= 0 ? windowState.x() : frame.getX();
		int y = windowState.y() >= 0 ? windowState.y() : frame.getY();
		frame.setSize(width, height);
		frame.setLocation(x, y);
		if (windowState.isIconified()) {
			frame.setExtendedState(JFrame.ICONIFIED);
		} else {
			frame.setExtendedState(JFrame.NORMAL);			
		}
	}
}
