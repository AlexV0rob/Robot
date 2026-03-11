package state;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Файловые запись и чтение состояния
 */
public class StateFileHandler {
	/**
	 * Записать состояния в файл
	 */
	public void writeStates(Map<String, WindowState> windowStates, 
			List<String> windowsKeys, String fileName)
					throws StateHandleException {
		File file;
		if (fileName.isEmpty()) {
			file = new File(System.getProperty("user.home"), "vorobyov/state.cfg");
		} else {
			file = new File(fileName);
		}
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			OutputStream out = new FileOutputStream(file);
			try {
				out = new BufferedOutputStream(out);
				for (String windowKey : windowsKeys) {
					WindowState currentState = windowStates.getOrDefault(
							windowKey,
							new WindowState(-1, -1, -1, -1, false));
					out.write(stateToBytes(currentState));
				}
				out.flush();
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new StateHandleException(
					"Couldn't write state to file: " + e.getMessage());
		}
	}

	/**
	 * Прочитать состояния из файла
	 */
	public Map<String, WindowState> readStates(List<String> windowsKeys, String fileName) 
			throws StateHandleException {
		File file;
		Map<String, WindowState> statesMap = new HashMap<>();
		if (fileName.isEmpty()) {
			file = new File(System.getProperty("user.home"), "vorobyov/state.cfg");
		} else {
			file = new File(fileName);
		}
		try {
			if (file.exists()) {
				InputStream in = new FileInputStream(file);
				try {
					in = new BufferedInputStream(in);
					for (String windowKey : windowsKeys) {
						byte[] byteState = new byte[WindowState.STATE_LENGTH];
						in.read(byteState);
						WindowState currentState = bytesToWindowState(byteState);
						statesMap.put(windowKey, currentState);
					}
				} finally {
					in.close();
				}
			}
		} catch (IOException e) {
			throw new StateHandleException(
					"Couldn't read state from file: " + e.getMessage());
		}
		return statesMap;
	}
	
	/**
	 * Конвертировать состояние окна в массив байтов
	 */
	private byte[] stateToBytes(WindowState windowState) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(WindowState.STATE_LENGTH);
		byteBuffer.putInt(windowState.width());
		byteBuffer.putInt(windowState.height());
		byteBuffer.putInt(windowState.x());
		byteBuffer.putInt(windowState.y());
		byteBuffer.put((byte) (windowState.isIconified() ? 1 : 0));
		return byteBuffer.array();
	}
	
	/**
	 * Конвертировать массив байтов в состояние окна
	 */
	private WindowState bytesToWindowState(byte[] byteArray) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		int width, height, x, y;
		boolean isIconified;
		try {
			width = byteBuffer.getInt();
			height = byteBuffer.getInt();
			x = byteBuffer.getInt();
			y = byteBuffer.getInt();
			isIconified = (byteBuffer.get() == 1);
		} catch (BufferUnderflowException e) {
			width = height = x = y = -1;
			isIconified = false;
		}
		return new WindowState(width, height, x, y, isIconified);
	}
}
