package robot;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Логика робота
 */
public class RobotGame {
	/**
	 * Обработка изменения свойства
	 */
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
    private volatile double robotPositionX = 100;
    private volatile double robotPositionY = 100; 
    private volatile double robotDirection = 0; 

    private volatile int targetPositionX = 150;
    private volatile int targetPositionY = 100;

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;
    
    private final Timer timer = initTimer();
    
    private static Timer initTimer() 
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }
    
    /**
     * Создать игру
     */
    public RobotGame() {
    	timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                updateModel();
            }
        }, 0, 10);
    }
    
    /**
     * Добавить слушателя свойства
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    	changeSupport.addPropertyChangeListener(listener);
    	fireNewData(new GameData(targetPositionX, targetPositionY, 
    			robotPositionX, robotPositionY, robotDirection));
    }
    
    /**
     * Удалить слушателя свойства
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    	changeSupport.removePropertyChangeListener(listener);
    }
    
    public void changeTargetPosition(int positionX, int positionY)  {
    	targetPositionX = positionX;
    	targetPositionY = positionY;
    }
    
    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }
    
    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }
    
    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }
    
    private void moveRobot(double velocity, double angularVelocity, double duration)
    {
    	GameData oldData = new GameData(targetPositionX, targetPositionY, 
    			robotPositionX, robotPositionY, robotDirection);
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);
        double newX = robotPositionX + velocity / angularVelocity * 
            (Math.sin(robotDirection  + angularVelocity * duration) -
                Math.sin(robotDirection));
        if (!Double.isFinite(newX))
        {
            newX = robotPositionX + velocity * duration * Math.cos(robotDirection);
        }
        double newY = robotPositionY - velocity / angularVelocity * 
            (Math.cos(robotDirection  + angularVelocity * duration) -
                Math.cos(robotDirection));
        if (!Double.isFinite(newY))
        {
            newY = robotPositionY + velocity * duration * Math.sin(robotDirection);
        }
        robotPositionX = newX;
        robotPositionY = newY;
        double newDirection = asNormalizedRadians(robotDirection + angularVelocity * duration); 
        robotDirection = newDirection;
        fireNewData(oldData);
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }
    
    /**
     * Обновить модель
     */
    private void updateModel()
    {
        double distance = distance(targetPositionX, targetPositionY, 
            robotPositionX, robotPositionY);
        if (distance < 0.5)
        {
            return;
        }
        double velocity = MAX_VELOCITY;
        double angleToTarget = angleTo(robotPositionX, robotPositionY,
                targetPositionX, targetPositionY);
        double angularVelocity = 0;
        if (angleToTarget > robotDirection)
        {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        }
        if (angleToTarget < robotDirection)
        {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }
        
        moveRobot(velocity, angularVelocity, 10);
    }

    /**
     * Отправить новые данные о состоянии
     */
    private void fireNewData(GameData oldData) {
    	GameData newData = new GameData(targetPositionX, targetPositionY, 
    			robotPositionX, robotPositionY, robotDirection);
		changeSupport.firePropertyChange("GameData", oldData, newData);
	}
}
