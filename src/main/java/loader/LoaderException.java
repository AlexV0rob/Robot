package loader;

/**
 * Исключение при загрузке классов
 */
public class LoaderException extends Exception {
	/**
	 * Уровень критичности ошибки
	 */
	private final int criticalLevel;
	
	/**
	 * Создать исключение при загрузке классов
	 */
	public LoaderException(String message, int criticalLevel) {
		super(message);
		this.criticalLevel = criticalLevel;
	}
	
	/**
	 * Получить уровень критичности ошибки
	 */
	public int getCriticalLevel() {
		return criticalLevel;
	}
}
