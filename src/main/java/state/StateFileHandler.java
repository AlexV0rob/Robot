package state;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Файловые запись и чтение состояния
 */
public class StateFileHandler {
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
					"Couldn't write state to file: " + e.getMessage());
		}
	}

	/**
	 * Прочитать состояния из файла
	 */
	public Map<String, Map<String, String>> readStates(String fileName) 
			throws StateHandleException {
		File file;
		Map<String, Map<String, String>> statesMap = new HashMap<>();
		if (fileName.isEmpty()) {
			file = new File(System.getProperty("user.home"), "vorobyov/state.cfg");
		} else {
			file = new File(fileName);
		}
		try {
			if (file.exists()) {
				ObjectMapper mapper = new ObjectMapper();
				List<Map<String, String>> windowsStates = 
						mapper.readValue(file, new TypeReference<>() {});
				for (Map<String, String> state : windowsStates) {
					String name = state.get("name");
					if (name != null) {
						statesMap.put(name, state);
					}
				}
			}
		} catch (IOException e) {
			throw new StateHandleException(
					"Couldn't read state from file: " + e.getMessage());
		}
		return statesMap;
	}
}
