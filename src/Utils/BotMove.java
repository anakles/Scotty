package Utils;

public class BotMove {
	
	private String direction;
	private double value;
	
	public BotMove(String dir, double val) {
		this.direction = dir;
		this.value = val;
	}

	public String getDirection() {
		return direction;
	}

	public double getValue() {
		return value;
	}
	
	public String toString() {
		return "BotMove[Direction: "+direction+" | Value: "+value+"]";
	}
}
