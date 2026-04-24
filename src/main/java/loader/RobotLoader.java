package loader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import gui.RobotDrawer;
import localization.Localizator;
import robot.RobotModel;

/**
 * Загрузчик классов робота
 */
public class RobotLoader {
	/**
	 * Расширение файлов классов
	 */
	private final static String CLASS = ".class";
	
	/**
	 * Локализатор
	 */
	private final Localizator localizator = Localizator.getInstance();
	
	/**
	 * URL выбранного JAR файла робота
	 */
	private URLClassLoader loader = null;
	
	/**
	 * Найденный класс модели робота
	 */
	private Class<?> robotModel;
	
	/**
	 * Найденный класс правил рисования робота
	 */
	private Class<?> robotDrawer;
	
	/**
	 * Загрузить файл с классами робота
	 */
	public void loadRobotJar(File robotJar) throws LoaderException {
		if (checkJarFile(robotJar)) {
			try {
				robotModel = null;
				robotDrawer = null;
				if (loader != null) {
					loader.close();
				}
				loader = new URLClassLoader(new URL[] {robotJar.toURI().toURL()});
				loadClasses(robotJar);
			} catch (MalformedURLException e) {
				throw new LoaderException(
						localizator.getString("loader.error.filename"), 2);
			} catch (IOException e) {
				throw new LoaderException(
						localizator.getString("loader.error.access", e.getMessage()), 2);
			}
		} else {
			throw new LoaderException(
					localizator.getString("loader.error.jar"), 2);
		}
	}
	
	/**
	 * Получить модель робота
	 */
	public RobotModel getRobotModelClass() throws LoaderException {
		try {
			return (RobotModel) getClass(robotModel, RobotModel.class.getName());
		} catch (ClassCastException e) {
			throw new LoaderException(localizator.getString("loader.error.cast", 
					robotModel.getName(), RobotModel.class.getName()), 1);
		}
	}
	
	/**
	 * Получить правила рисования робота
	 */
	public RobotDrawer getRobotDrawerClass() throws LoaderException {
		try {
			return (RobotDrawer) getClass(robotDrawer, RobotDrawer.class.getName());
		} catch (ClassCastException e) {
			throw new LoaderException(localizator.getString("loader.error.cast", 
					robotDrawer.getName(), RobotDrawer.class.getName()), 1);
		}
	}
	
	/**
	 * Получить экземпляр требуемого класса
	 */
	private Object getClass(Class<?> wantedClass, String interfaceName) throws LoaderException {
		try {
			if (wantedClass != null) {
				return wantedClass.getDeclaredConstructor().newInstance();
			} else {
				throw new LoaderException(
						localizator.getString("loader.error.class", interfaceName), 0);
			}
		} catch (NoSuchMethodException e) {
			throw new LoaderException(localizator.getString(
					"loader.error.constructor", wantedClass.getName()), 1);
		} catch (InstantiationException | IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException e) {
			throw new LoaderException(localizator.getString("loader.error.instance", 
							wantedClass.getName(), e.getMessage()), 1);
		}
	}
	
	/**
	 * Загрузить модель робота и правила рисования робота
	 */
	private void loadClasses(File file) throws LoaderException {
		try (JarFile jarFile = new JarFile(file)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()
					&& (robotModel == null || robotDrawer == null)) {
				JarEntry jarEntry = entries.nextElement();
				if (!jarEntry.isDirectory() && jarEntry.getName().endsWith(CLASS)) {
					String className = jarEntry.getName()
							.substring(0, jarEntry.getName().length() - CLASS.length())
							.replace('/', '.');
					try {
						Class<?> currentClass = loader.loadClass(className);
						if (robotModel == null 
								&& RobotModel.class.isAssignableFrom(currentClass)) {
							robotModel = currentClass;
						}
						if (robotDrawer == null 
								&& RobotDrawer.class.isAssignableFrom(currentClass)) {
							robotDrawer = currentClass;
						}
					} catch (ClassNotFoundException e) {
						//just ignore
					}
				}
			}
		} catch (IOException e) {
			throw new LoaderException(
					localizator.getString("loader.error.reading", e.getMessage()), 2);
		}
	}
	
	/**
	 * Проверить, что файл является JAR файлом
	 */
	private boolean checkJarFile(File file) {
		if (!file.exists()) {
			return false;
		}
		try (JarFile jarFile = new JarFile(file)) {
			return jarFile.getManifest() != null;
		} catch (IOException e) {
			return false;
		}
	}
}
