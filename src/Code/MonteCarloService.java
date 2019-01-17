package Code;

import java.util.ArrayList;
import Utils.*;

public class MonteCarloService {	
	
	private static final int NUMBER_OF_PARTICLES = 50;
	
	public static final String FORWARD = "vor";
	public static final String BACKWARD = "zurueck";
	public static final String ROTATE_RIGHT = "rechts";
	public static final String ROTATE_LEFT = "links";
	public static final String SENSORS = "sensor";
	public static final String END = "end";
	
	private static ArrayList<String> commands = new ArrayList<>();
	private static ArrayList<Particle> particles = new ArrayList<>();
	
	public ArrayList<String> getCommands(){		
		return commands;
	}
	
	public void addAufgabe3_1Commands() {
		commands.clear();
		
		commands.add("sensor_0");
		commands.add("vor_500");
		commands.add("links_90");
		commands.add("rechts_270");
		commands.add("vor_500");
		commands.add("sensor_0");
		commands.add("links_180");
		commands.add("sensor_0");
		//commands.add("end_0");
		
	}
	
	public void addCommand(String cmd) {
		commands.add(cmd);
	}
	
	public void clearCommands() {
		commands.clear();
	}
	
	
	private static void initParticles() {
		int min_x = MonteCarloFrame.panel.x1;
		int max_x = MonteCarloFrame.panel.x2;
		int min_y = MonteCarloFrame.panel.y1;
		int max_y = MonteCarloFrame.panel.y2;
		
		System.out.println("-----------------------------------------------------");
		System.out.println("Initializing the first particles...");
		
		for(int i = 0; i < NUMBER_OF_PARTICLES; i++) {
			int temp_x;
			int temp_y;
			
			do {
				temp_x = (int) (Math.random() * max_x);
			}
			while(temp_x <= min_x);
			
			do {
				temp_y = (int) (Math.random() * max_y);
			}
			while(temp_y <= min_y);
			
			
			Particle particle = new Particle(temp_x,temp_y, 0.0);
			particle.changeRotation((int) (Math.random()*1000));
			particles.add(particle);
			
		}
		
		
	}
	
	public static ArrayList<Particle> getParticles(){
		initParticles();
		return particles;
	}
	private void calculateBelieve() {
		
		
		
	}
}
