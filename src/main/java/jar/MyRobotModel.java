package jar;

import robot.GameData;
import robot.RobotModel;

/**
 * Модель робота для демонстрации загрузки классов: другие начальные координаты,
 * выше тикрейт, выше скорость и угловая скорость
 */
public class MyRobotModel implements RobotModel {
    private double robotPositionX = 300;
    private double robotPositionY = 500; 
    private double robotDirection = 0; 

    private int targetPositionX = 150;
    private int targetPositionY = 100;

    private static final double MAX_VELOCITY = 0.5;
    private static final double MAX_ANGULAR_VELOCITY = 0.05;
    
    /**
     * Начальное значение игровой скорости
     */
    private static final double START_ANGULAR_VELOCITY = 0.005;
    
    /**
     * Значение, на которое увеличивается угловая скорость
     */
    private static final double ANGULAR_INCREMENT = 0.00001;
    
    /**
     * Коэффициент увеличения угловой скорости
     */
    private double angularCoefficient = 0;
    
    @Override
	public void changeState(GameData gameData) {
		robotPositionX = gameData.robotPositionX();
		robotPositionY = gameData.robotPositionY();
		robotDirection = gameData.robotDirection();
		targetPositionX = gameData.targetPositionX();
		targetPositionY = gameData.targetPositionY();
	}
    
	private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }
    
    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);
        double newX = robotPositionX + velocity / angularVelocity * 
        	(Math.sin(robotDirection  + angularVelocity * duration) -
           	Math.sin(robotDirection));
        if (!Double.isFinite(newX)) {
        	newX = robotPositionX + velocity * duration * Math.cos(robotDirection);
        }
        double newY = robotPositionY - velocity / angularVelocity * 
        	(Math.cos(robotDirection  + angularVelocity * duration) -
           	Math.cos(robotDirection));
        if (!Double.isFinite(newY)) {
        	newY = robotPositionY + velocity * duration * Math.sin(robotDirection);
        }
        robotPositionX = newX;
        robotPositionY = newY;
        robotDirection = asNormalizedRadians(robotDirection + angularVelocity * duration);
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
    
    @Override
    public void updateModel()
    {
    	if (distance(targetPositionX, targetPositionY, robotPositionX, robotPositionY) > 1) {
        	double angleToTarget = angleTo(robotPositionX, robotPositionY,
                	targetPositionX, targetPositionY);
    		angularCoefficient += 1;
        	if (Math.abs(angleToTarget - robotDirection) < 1e-3) {
        		angularCoefficient = 0;
        	}
    		double velocity = MAX_VELOCITY;
        	double angularVelocityAbsolute = START_ANGULAR_VELOCITY
        			+ angularCoefficient * ANGULAR_INCREMENT;
        	double angularVelocity = (flashOnTheRight(angleToTarget) ? -1 : 1)
        			* angularVelocityAbsolute;
        	moveRobot(velocity, angularVelocity, 5);
    	}
    }

    /**
     * Проверить, что цель находится справа от робота
     */
    private boolean flashOnTheRight(double angleToTarget) {
    	if (robotDirection >= 0 && robotDirection < Math.PI) {
    		return angleToTarget < robotDirection || angleToTarget > Math.PI + robotDirection;
    	} else {
    		return angleToTarget < robotDirection && angleToTarget > robotDirection - Math.PI;
    	}
    }

	@Override
	public GameData getCurrentState() {
		return new GameData(targetPositionX, targetPositionY, 
				robotPositionX, robotPositionY, 
				robotDirection, angleTo(robotPositionX, robotPositionY,
						targetPositionX, targetPositionY));
	}
}
