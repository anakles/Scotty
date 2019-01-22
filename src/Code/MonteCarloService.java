package Code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Graphics;

import Utils.*;

public class MonteCarloService {	
	
	private static final int NUMBER_OF_PARTICLES = 100;
	
	public static final String FORWARD = "vor";
	public static final String BACKWARD = "zurueck";
	public static final String ROTATE_RIGHT = "rechts";
	public static final String ROTATE_LEFT = "links";
	public static final String SENSORS = "sensor";
	public static final String GIVE_SENSORS = "sensor_0";
	public static final String END = "end";
//	MonteCarloFrame monteCarloFrame = new MonteCarloFrame();
	
	double bestProb = 0;
	double bestDist = 0;
	
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
	
	public void findStreetCommands() throws NullPointerException{
		BotMove lastMove = ServerMain.moveHistory.get(ServerMain.moveHistory.size()-1);
		SensorValue lastFront = null;
		
		//Filter
		for(int i = ServerMain.sensorHistory.size()-1; i > 0; i--) {
			if(ServerMain.sensorHistory.get(i).getMesuredDirection().equals(SensorValue.DIR_FRONT)) {
				lastFront = ServerMain.sensorHistory.get(i);
				continue;
			}	
		}
		
		//Case: We reached the end of the street
		if(lastFront.getSonicValue() < 0.4) {
			commands.clear();
			commands.add(BACKWARD+"_"+lastMove.getValue());
			commands.add(ROTATE_RIGHT+"_"+"180");
			commands.add(GIVE_SENSORS);
			
			
		}
		//Case: Scotty the schrotty botty did not drive straight
		else {
			/* Redo last move (drive backward)
			 * 2° right + drive 10 cm
			 * sensors
			 * Redo last move
			 * 4° left + drive 10 cm
			 * sensors
			 * if still not on street, ERROR?!
			 */
			
			
			
			
			
			
		}
		
		
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
			double init_weight = 1 / NUMBER_OF_PARTICLES;
			//double init_weight = Math.random();
			
			do {
				temp_x = (int) (Math.random() * max_x);
			}
			while(temp_x <= min_x);
			
			do {
				temp_y = (int) (Math.random() * max_y);
			}
			while(temp_y <= min_y);
			
			
			Particle particle = new Particle(temp_x,temp_y, init_weight );
			//Initial particles have only 2 possible directions to look at: 90° or 270°:
			particle.changeRotation((Math.random() > 0.5) ? 1 : 3);
			particles.add(particle);
			
		}
		
		
	}
	
	public static ArrayList<Particle> getParticles()
	{
		initParticles();
		return particles;
	}
	
	private ArrayList<Particle> calculateBelieve(ArrayList<Particle> old_particles) 
	{
		int N = old_particles.size();
		ArrayList<Particle> new_particles = new ArrayList<Particle>();
		double incr = 0;
		bestProb = 0;

		int index = 0;

		for (int i = 0; i < N; i++) {
			incr += old_particles.get(i).weight;
		}

		incr = incr / 2.0 / N;
		double beta = incr;
		
		for (int i = 0; i < N; i++) 
		{
			while (beta > old_particles.get(i).weight) 
			{
				beta -= old_particles.get(i).weight;
				index = (index + 1) % N;
			}

			beta += incr;
			//removed clone()
			//new_particles.add((Particle) old_particles.get(i));
			new_particles.add(new Particle(old_particles.get(i)));
			
			if (old_particles.get(i).weight > bestProb) 
			{
				bestProb = old_particles.get(i).weight;
			}

		}
		
		return new_particles;
	}
	
	
	
	/**Distance is the value of the sonic sensor, movement the distance the bot moved in the last action  */
	public void monteCarlo()
	{
		//TODO: set to 0 later again
		BotMove latestMove = ServerMain.moveHistory.get(ServerMain.moveHistory.size()-1);
		SensorValue latestSensor = ServerMain.sensorHistory.get(ServerMain.sensorHistory.size()-1);
		
		double[] probs = new double[NUMBER_OF_PARTICLES];
		double streetColorId = 7.0;
		boolean onStreet = true;
		
		//Are we still on the street?
		if(!(latestSensor.getColorValue() == streetColorId)) {
			onStreet = false;
			
			while(!onStreet) {
				
				
			}
		}
			
		
		//times 90° rotation
		int rotationTimes = 0;
		if(latestMove.getDirection().equals(MonteCarloService.ROTATE_LEFT)){
			rotationTimes = (int) ((0 - (latestMove.getValue() / 90)) * -1);
		}
		else if(latestMove.getDirection().equals(MonteCarloService.ROTATE_RIGHT)) {
			rotationTimes = (int) ( latestMove.getValue() / 90);		
		}
		
		
		//TODO: Check if still on street:
		//If not, move bot until we find it.
		
		
		
		
		
		
		
		
		
		for (int i = 0; i < particles.size(); i++) 
		{
			Particle currParticle = particles.get(i);
			
			double prob = currParticle.weight;
			particles.get(i).changeRotation(rotationTimes); 
			particles.get(i).x += latestMove.getValue(); //TODO: Fill with actual robot movement
//			particles.get(i).y += 1; //TODO: anpassen to relativ Y-position of the robot (+someting or -something)

			
			//check if particles are out of bounds
			if (particles.get(i).x >= 580 || particles.get(i).x <= 10)
			{
				int temp_x;
				int temp_y;				
				do {
					temp_x = (int) (Math.random() * 580);
				}
				while(temp_x <= 10);
				
				do {
					temp_y = (int) (Math.random() * 80);
				}
				while(temp_y <= 70);
				
				Particle p = new Particle(temp_x, temp_y, prob);
				p.changeRotation((Math.random() < 0.5) ? 1 : 3);
				
				particles.set(i, p);
			}
			
			
			//TODO: Gewichtungsfunktion der Partikel

			prob = Math.random();

			probs[i] = prob;
		}

		normalize(probs);

		for (int i = 0; i < particles.size(); i++) {
			Particle currParticle = particles.get(i);
			currParticle.weight = probs[i];
		}
	
		particles = calculateBelieve(particles);
		
		MonteCarloFrame.panel.particles = particles;
		MonteCarloFrame.panel.repaint(); //TODO: test, if this repaint()-method is the right one
		MonteCarloFrame.panel.revalidate();
}


	
	public final static void normalize(double[] doubles) {

		double sum = 0;
		for (int i = 0; i < doubles.length; i++) {
			sum += doubles[i];
		}
		normalize(doubles, sum);
	}
	
	public final static void normalize(double[] doubles, double sum) {

		if (Double.isNaN(sum)) {
			throw new IllegalArgumentException("Can't normalize array. Sum is NaN.");
		}
		if (sum != 0) {
			for (int i = 0; i < doubles.length; i++) {
				doubles[i] /= sum;
			}
		}

	}

}
