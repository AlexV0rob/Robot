package state;

/**
 * Умеет сохранять и восстанавливать своё состояние
 */
public interface StateSaveable {
	/**
	 * Сохранить состояние
	 */
	WindowState saveState();
	
	/**
	 * Восстановить состояние
	 */
	void recoverState(WindowState windowState);
}
