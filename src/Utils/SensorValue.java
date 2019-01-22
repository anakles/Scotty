package Utils;

public class SensorValue {
	
	private double colorValue;
	private double sonicValue;
	
	public SensorValue(double sonic, double color) {
		this.colorValue = color;
		this.sonicValue = sonic;
		
	}

	public double getColorValue() {
		return colorValue;
	}

	public void setColorValue(double colorValue) {
		this.colorValue = colorValue;
	}

	public double getSonicValue() {
		return sonicValue;
	}

	public void setSonicValue(double sonicValue) {
		this.sonicValue = sonicValue;
	}
}
