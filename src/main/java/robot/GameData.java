package robot;

/**
 * POJO объект для передачи данных о состоянии игры
 * @param targetPositionX
 * @param targetPositionY
 * @param robotPositionX
 * @param robotPositionY
 * @param robotDirection
 * @param angleToTarget
 */
public record GameData(int targetPositionX, int targetPositionY,
		double robotPositionX, double robotPositionY, 
		double robotDirection, double angleToTarget) {}
