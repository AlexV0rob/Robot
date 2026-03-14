package state;

import java.util.Map;

/**
 * Умеет сохранять и восстанавливать своё состояние
 */
public interface StateSaveable {
	/**
	 * Получить имя объекта
	 */
	String sayMyName();
	
	/**
	 * Сохранить состояние
	 */
	Map<String, String> saveState();
	
	/**
	 * Восстановить состояние
	 */
	void recoverState(Map<String, String> windowState);
}
