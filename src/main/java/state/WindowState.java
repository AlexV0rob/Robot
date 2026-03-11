package state;

/**
 * DTO для передачи данных об окне
 */
public record WindowState(
		int width, int height, 
		int x, int y, 
		boolean isIconified) {
	/**
	 * Длина состояния в байтах
	 */
	public final static int STATE_LENGTH = 17;
}
