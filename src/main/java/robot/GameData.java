package robot;

/**
 * POJO объект для передачи данных о состоянии игры
 * В параметрах сначала идут значения для цели, потом для робота
 */
public record GameData(int targetPositionX, int targetPositionY,
		double robotPositionX, double robotPositionY, 
		double robotDirection, double angleToTarget) {}
