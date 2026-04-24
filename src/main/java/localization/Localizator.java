package localization;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Локализатор, обёртка над с ResourceBundle, реализован как синглтон
 */
public class Localizator {
	/**
	 * Изменение языка
	 */
	private final static String LANGUAGE_CHANGE = "language_change";
	
	/**
	 * Обработка изменения языка
	 */
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	/**
	 * Кэш для хранения шаблонов
	 */
	private final Map<Locale, Map<String, MessageFormat>> cache = new HashMap<>();
	
	/**
	 * Базовое имя набора ресурсов
	 */
	private final static String RES_NAME = "text";
	
	/**
	 * Набор ресурсов для локализации
	 */
	private ResourceBundle resources;
	
	/**
	 * Текущая локаль
	 */
	private Locale currentLocale;
	
	/**
	 * Экземпляр локализатора
	 */
	private final static Localizator INSTANCE = new Localizator();
	
	/**
	 * Создать локализатор
	 */
	private Localizator() {
		resources = ResourceBundle.getBundle(RES_NAME);
		currentLocale = Locale.of("");
	}
	
	/**
     * Добавить слушателя свойства
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    	changeSupport.addPropertyChangeListener(listener);
    }
    
    /**
     * Удалить слушателя свойства
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    	changeSupport.removePropertyChangeListener(listener);
    }
	
	/**
	 * Получить экземпляр локализатора
	 */
	public static Localizator getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Поменять локаль
	 */
	public void changeLocale(Locale locale) {
		Locale oldLocale = currentLocale;
		currentLocale = locale;
		resources = ResourceBundle.getBundle(RES_NAME, currentLocale);
		fireNewData(oldLocale);
	}
	
	/**
	 * Получить строку по ключу
	 */
	public String getString(String key) {
		return getMessageFormat(key).toPattern();
	}
	
	/**
	 * Получить строку по ключу с выбранными числовыми параметрами
	 */
	public String getString(String key, Number... numbers) {
		return getMessageFormat(key).format(numbers);
	}
	
	/**
	 * Получить строку по ключу с выбранными строковыми параметрами
	 */
	public String getString(String key, String... numbers) {
		return getMessageFormat(key).format(numbers);
	}
	
	/**
	 * Получить MessageFormat из кеша
	 */
	private MessageFormat getMessageFormat(String key) {
		Map<String, MessageFormat> localeCache = cache.get(currentLocale);
		if (localeCache == null) {
			localeCache = new HashMap<String, MessageFormat>();
			cache.put(currentLocale, localeCache);
		}
		MessageFormat format = localeCache.get(key);
		if (format == null) {
			format = new MessageFormat(resources.getString(key));
			localeCache.put(key, format);
		}
		return format;
	}
	
	/**
	 * Послать новые данные
	 */
    private void fireNewData(Locale oldLocale) {
		changeSupport.firePropertyChange(LANGUAGE_CHANGE, 
				oldLocale, currentLocale);
    }
    
    /**
     * Получить текущую локаль в виде строки
     */
    public String getCurrentLocale() {
    	return currentLocale.toString();
    }
}
