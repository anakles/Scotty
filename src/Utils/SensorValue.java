package Utils;

public class SensorValue {
	public static final String DIR_FRONT = "front";
	public static final String DIR_LEFT = "left";
	public static final String DIR_RIGHT = "right";
	
	
	private double colorValue;
	private double sonicValue;
	private String mesuredDirection;
	
	public SensorValue(double sonic, double color, String dir) {
		this.colorValue = color;
		this.sonicValue = sonic;
		this.mesuredDirection = dir;
	}

	public double getColorValue() {
		return colorValue;
	}

	public double getSonicValue() {
		return sonicValue;
	}
	
	public String getMesuredDirection() {
		return mesuredDirection;
	}

	public String toString() {
		return "SensorValue[Sonic: "+sonicValue+" | Color: "+colorValue+"] in direction: "+mesuredDirection;
	}
}
