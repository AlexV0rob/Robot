package robot;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Timer;
import java.util.TimerTask;

import gui.RobotDrawer;
import gui.RobotDrawerDefault;

/**
 * Управляющий одной игровой сессией
 */
public class GameManager {
	/**
	 * Данные игры
	 */
	private final static String GAME_DATA = "game_data";
	
	/**
	 * Обновление правил рисования
	 */
	private final static String DRAWER_UPDATE = "drawer_update";
	
	/**
	 * Обновление модели
	 */
	private final static String MODEL_UPDATE = "model_update";

	/**
	 * Обработка изменения свойства
	 */
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	/**
	 * Текущая модель робота
	 */
	private RobotModel model;
	/**
	 * Правила рисования робота
	 */
	private RobotDrawer drawer;
	
	/**
	 * Таймер для управления игрой
	 */
    private Timer timer;
	
	/**
	 * Создать игровой менеджер
	 */
	public GameManager() {
		model = new RobotModelDefault();
		drawer = new RobotDrawerDefault();
	}
	
    /**
     * Добавить слушателя
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    	changeSupport.addPropertyChangeListener(listener);
    }
    
    /**
     * Удалить слушателя
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    	changeSupport.removePropertyChangeListener(listener);
    }
    
	/**
	 * Начать игру
	 */
	public void startGame() {
		timer = new Timer("events generator", true);
		timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                updateModel();
            }
        }, 0, 10);
	}
	
	public void stopGame() {
		timer.cancel();
	}
    
    /**
     * Установить новую модель робота
     */
    public void setModel(RobotModel newModel) {
    	RobotModel oldModel = model;
    	model = newModel;
    	fireModelUpdate(oldModel);
    }
    
    /**
     * Получить текущую модель
     */
    public RobotModel getModel() {
    	return model;
    }
    
    /**
     * Установить новые правила рисования робота
     */
    public void setDrawer(RobotDrawer newDrawer) {
    	RobotDrawer oldDrawer = drawer;
    	drawer = newDrawer;
    	fireDrawerUpdate(oldDrawer);
    }
    
    /**
     * Получить текущие правила рисования робота
     */
    public RobotDrawer getDrawer() {
    	return drawer;
    }
    
    /**
     * Обновить модель
     */
    public void updateModel() {
    	GameData oldGameData = model.getCurrentState();
    	model.updateModel();
    	fireGameData(oldGameData);
    }
    
    /**
     * Поменять состояние модели
     */
    public void changeModelState(GameData gameData) {
    	model.changeState(gameData);
    }
    
    /**
     * Отправить данные о состоянии модели
     */
    private void fireGameData(GameData oldData) {
    	GameData newData = model.getCurrentState();
		changeSupport.firePropertyChange(GAME_DATA, oldData, newData);
    }
    
    /**
     * Отправить новые правила рисования робота
     */
    private void fireDrawerUpdate(RobotDrawer oldDrawer) {
    	RobotDrawer newDrawer = drawer;
    	changeSupport.firePropertyChange(DRAWER_UPDATE, oldDrawer, newDrawer);
    }
    
    /**
     * Отправить новую модель робота
     */
    private void fireModelUpdate(RobotModel oldModel) {
    	RobotModel newModel = model;
    	changeSupport.firePropertyChange(MODEL_UPDATE, oldModel, newModel);
    }
}
