package Utils;


public class Particle {
	public int x;
	public int y;
	
	//ToDo: Add rotation (0°, 90°, 180° or 270°)
	public int rotation = 0;
	
	
	public double weight;
	

	public Particle(int x, int y, double weight) {
		this.x = x;
		this.y = y;
		
		this.weight = weight;
		
	}
	
	public Particle(Particle part) {
		this.x = part.x;
		this.y = part.y;
		
		this.weight = part.weight;
		this.rotation = part.rotation;
		
	}
	
	
	
	public void changeRotation(int rotateTimes) {
		rotation = (rotation + rotateTimes * 90) % 360;
	}
	
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
	
	public String toString() {
		return "Particle[X:"+x+" Y:"+y+" -> Weighted: "+weight+"]";
	}
	
}



