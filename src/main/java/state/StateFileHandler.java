package state;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import localization.Localizator;

/**
 * Файловые запись и чтение состояния
 */
public class StateFileHandler {
	/**
	 * Локализатор
	 */
	private final Localizator localizator = Localizator.getInstance();
	
	/**
	 * Записать состояния в файл
	 */
	public void writeStates(List<Map<String, String>> windowsStates, String fileName)
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
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(file, windowsStates);
		} catch (IOException e) {
			throw new StateHandleException(
					localizator.getString("state.error.write", e.getMessage()));
		}
	}

	/**
	 * Прочитать состояния из файла
	 */
	public List<Map<String, String>> readStates(String fileName) 
			throws StateHandleException {
		File file;
		List<Map<String, String>> windowsStates = List.of();
		if (fileName.isEmpty()) {
			file = new File(System.getProperty("user.home"), "vorobyov/state.cfg");
		} else {
			file = new File(fileName);
		}
		try {
			if (file.exists()) {
				ObjectMapper mapper = new ObjectMapper();
				windowsStates = mapper.readValue(file, new TypeReference<>() {});
			}
		} catch (IOException e) {
			throw new StateHandleException(
					localizator.getString("state.error.read", e.getMessage()));
		}
		return windowsStates;
	}
}
