package state;

/**
 * Исключение при обработке состояний
 */
public class StateHandleException extends Exception {
	/**
	 * Создать исключение, возникшее при обработке состояний
	 */
	public StateHandleException(String message) {
		super(message);
	}
}
