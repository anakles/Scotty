package Utils;


public class Particle {
	public int x;
	public int y;
	
	//ToDo: Add rotation (0°, 90°, 180° or 270°)
	public int rotation = 0;
	
	
	public double poss;

	public Particle(int x, int y, double poss) {
		this.x = x;
		this.y = y;
		
		this.poss = poss;
		
	}
	
	
	public void changeRotation(int rotateTimes) {
		rotation = (rotation + rotateTimes * 90) % 360;
	}
	
	
}



