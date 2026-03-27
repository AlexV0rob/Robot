package state;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;

/**
 * Обработчик состояний окна, содержащий методы для сохранения и восстановления состояния
 * Используется окнами для делегирования этих задач
 */
public class WindowStateHandler {
	/**
	 * Сохранить состояние окна, унаследованного от JInternalFrame
	 */
	public Map<String, String> saveJInternalFrameState(JInternalFrame frame, String name) {
		Map<String, String> state = new HashMap<>();
		state.put("name", name);
		state.put("width", String.valueOf(frame.getWidth()));
		state.put("height", String.valueOf(frame.getHeight()));
		state.put("x", String.valueOf(frame.getX()));
		state.put("y", String.valueOf(frame.getY()));
		state.put("isIconified", String.valueOf(frame.isIcon()));
		state.put("isMaximized", String.valueOf(frame.isMaximum()));
		return state;
	}
	
	/**
	 * Восстановить состояние окна, унаследованного от JInternalFrame
	 */
	public void recoverJInternalFrameState(JInternalFrame frame, Map<String, String> windowState) {
		int width = getValueByKeyOrDefault("width", windowState, frame.getWidth());
		int height = getValueByKeyOrDefault("height", windowState, frame.getHeight());
		int x = getValueByKeyOrDefault("x", windowState, frame.getX());
		int y = getValueByKeyOrDefault("y", windowState, frame.getY());
		frame.setSize(width, height);
		frame.setLocation(x, y);
		try {
			frame.setIcon(Boolean.parseBoolean(windowState.get("isIconified")));
			frame.setMaximum(Boolean.parseBoolean(windowState.get("isMaximized")));
		} catch (PropertyVetoException e) {
			//just ignore
		}
	}
	
	/**
	 * Сохранить состояние окна, унаследованного от JFrame
	 */
	public Map<String, String> saveJFrameState(JFrame frame, String name) {
		Map<String, String> state = new HashMap<>();
		state.put("name", name);
		state.put("width", String.valueOf(frame.getWidth()));
		state.put("height", String.valueOf(frame.getHeight()));
		state.put("x", String.valueOf(frame.getX()));
		state.put("y", String.valueOf(frame.getY()));
		state.put("isIconified", String.valueOf(
				(frame.getExtendedState() & JFrame.ICONIFIED) == JFrame.ICONIFIED));
		state.put("isMaximized", String.valueOf(
				(frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH));
		return state;
	}
	
	/**
	 * Восстановить состояние окна, унаследованного от JFrame
	 */
	public void recoverJFrameState(JFrame frame, Map<String, String> windowState) {
		int width = getValueByKeyOrDefault("width", windowState, frame.getWidth());
		int height = getValueByKeyOrDefault("height", windowState, frame.getHeight());
		int x = getValueByKeyOrDefault("x", windowState, frame.getX());
		int y = getValueByKeyOrDefault("y", windowState, frame.getY());
		frame.setSize(width, height);
		frame.setLocation(x, y);
		if (Boolean.parseBoolean(windowState.get("isIconified"))) {
			frame.setExtendedState(JFrame.ICONIFIED);
		} else if (Boolean.parseBoolean(windowState.get("isMaximized"))) {
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			frame.setExtendedState(JFrame.NORMAL);
		}
	}
	
	/**
	 * Получить конвертированное в число значение по ключу или вернуть стандартное, 
	 * если возникло исключение или значение отрицательно
	 */
	private int getValueByKeyOrDefault(String key, Map<String, String> state, int defaultValue) {
		String stringValue = state.get(key);
		if (stringValue == null) {
			return defaultValue;
		}
		try {
			int value = Integer.parseInt(stringValue);
			if (value < 0) {
				return defaultValue;
			}
			return value;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
