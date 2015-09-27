package joystick.classes;

/**
 * Place program description here
 *
 * @author Mats Otten
 * @project joystick
 * @since 26-09-15
 */
public class Calculation {

	public static int startX = 150;
	public static int startY = 150;

	// part (0-6) => speed => left, right

	public static int[][][] speedTable = new int[][][] {
		{
			{1, 0},
			{1, 0},
			{1, 0},
			{1, 0},
			{1, 0},
			{1, 0}
		},
		{
			{2, 0},
			{2, 0},
			{2, 0},
			{2, 0},
			{2, 0},
			{2, 0}
		},
		{
			{3, 0},
			{3, 0},
			{3, 0},
			{3, 0},
			{3, 0},
			{3, 0}
		},
		{
			{4, 0},
			{4, 0},
			{4, 0},
			{4, 0},
			{4, 0},
			{4, 0}
		},
		{
			{5, 0},
			{5, 0},
			{5, 0},
			{5, 0},
			{5, 0},
			{5, 0}
		},
		{
			{6, 0},
			{6, 0},
			{6, 0},
			{6, 0},
			{6, 0},
			{6, 0}
		}
	};

	public static int[][][] spacingSpeedTable = new int[][][] { // spacingField (0 / 1) => speed (0 - 5)
		{
			{0, 0},
			{1, 1},
			{2, 2},
			{3, 3},
			{4, 4},
			{5, 5},
		},
		{
			{1, 0},
			{2, 0},
			{3, 0},
			{4, 0},
			{5, 0},
			{6, 0},
		},
	};

	public static int calculateValue(double x, double y) {
		int degrees = calculateDegrees(x, y);
		int distance = calculateDistance(x, y);

		int speed = distanceToSpeed(distance);
		int direction = degreesToDirection(degrees);

		int engineValue = calculateEngineValue(direction, degrees, speed);

		return engineValue;
	}

	protected static int calculateDegrees(double x, double y) {
		double deltaX = x - startX;
		double deltaY = y - startY;
		double rad = Math.atan2(deltaY, deltaX);

		rad += Math.PI / 2;

		double deg = Math.toDegrees(rad);
		if (deg < 0)
			deg += 360;

		return (int)deg;
	}

	protected static int calculateDistance(double x, double y) {
		double deltaX = x - startX;
		double deltaY = y - startY;

		double pointDelta = Math.pow(deltaX,2) + Math.pow(deltaY, 2);

		return (int)Math.sqrt(pointDelta);
	}

	protected  static int distanceToSpeed(int distance) {
		int speed = 0;

		if(distance < 15)
			speed = 1;
		else if(distance < 30)
			speed = 2;
		else if(distance < 45)
			speed = 3;
		else if(distance < 60)
			speed = 4;
		else if(distance < 75)
			speed = 5;
		else
			speed = 6;

		return speed;
	}

	protected static int degreesToDirection(int degrees) {
		if(degrees < 90 || degrees > 270)
			return 0; // forward
		return 1; // backward
	}

	protected static int calculateEngineValue(int direction, int degrees, int speed) {
		int piePart = (degrees / 90);

		int partNumber = 0;
		int spacing = 20;
		int partSize = (90 - (spacing * 2)) / 6;
		int spacingField = -1;

		for(int i = 0; i < 5; i++) {
			if((i == 0) && (((degrees - (piePart * 90)) <= spacing))) {
				spacingField = 0;
				break;
			}
			if((i == 0) && ((piePart * 90 + 90) - spacing < (degrees))) {
				spacingField = 1;
				break;
			}
			if(degrees - (piePart * 90) >= spacing + ((i * partSize)) && degrees - (piePart * 90) <= (spacing + (i * partSize) + partSize)) {
				break;
			}
			partNumber++;
		}

		int speeding[];
		if(spacingField == -1) {
			speeding = speedTable[partNumber][(speed - 1)];
		} else {
			spacingField = ((piePart == 0 && spacingField == 0) || (piePart == 1 && spacingField == 1) || (piePart == 2 && spacingField == 0) || (piePart == 3 && spacingField == 1) ? 0 : 1);
			speeding = spacingSpeedTable[spacingField][(speed - 1)];
		}

		int left = 0;
		int right = 0;

		if(piePart == 0 || piePart == 1) {
			left = speeding[0];
			right = speeding[1];
		} else if(piePart == 2 || piePart == 3) {
			left = speeding[1];
			right = speeding[0];
		}

		//System.out.println("P:   " + partNumber + " D: " + direction + "   L: " + left + "   R: " + right);

		int valueLeft = direction << 3 ^ left;
		int valueRight = direction << 3 ^ right;
		int value = valueLeft << 4 ^ valueRight;

		return value;
	}

}
