package Code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Graphics;
import java.io.IOException;

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
	
	public static final double STREET_COLOR_ID = 2.0;
//	MonteCarloFrame monteCarloFrame = new MonteCarloFrame();
	
	double bestProb = 0;
	double bestDist = 0;
	
	boolean onStreet = false;
	
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
	
	/**Checks the last sensor values if the bot is currently on the street */
	private static boolean isOnStreet(ArrayList<SensorValue> sensorHistory) {
		if(sensorHistory.get(sensorHistory.size()-1).getColorValue() == STREET_COLOR_ID)
			return true;
		else 
			return false;
	}
	
	public void findStreetCommands() throws NullPointerException{
		BotMove lastMove = ServerMain.moveHistory.get(ServerMain.moveHistory.size()-1);
		BotMove lastForwardMove = null;
		SensorValue lastFront = null;
		
		//Filter
		for(int i = ServerMain.sensorHistory.size()-1; i > 0; i--) {
			if(ServerMain.sensorHistory.get(i).getMesuredDirection().equals(SensorValue.DIR_FRONT)) {
				lastFront = ServerMain.sensorHistory.get(i);
				break;
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
			 * 10° right + drive 10 cm
			 * sensors
			 * Redo last move
			 * 20° left + drive 10 cm
			 * sensors
			 * if still not on street, ERROR?!
			 */
			//SensorValue tempSensor = ServerMain.sensorHistory.get(ServerMain.sensorHistory.size()-1);
			
			//redo last forward move:
			if(lastMove.getDirection().equals(FORWARD)) {
				lastForwardMove = lastMove;
				addCommand(BACKWARD+"_"+lastMove.getValue());
			}
			else {
				for(int i = ServerMain.moveHistory.size()-1; i > 0; i--) {
					if(ServerMain.moveHistory.get(i).getDirection().equals(FORWARD)) {
						lastForwardMove = ServerMain.moveHistory.get(i);
						addCommand(BACKWARD+"_"+lastForwardMove.getValue());
						break;
					}
				}
			}
			//addCommand(GIVE_SENSORS);
			
			
			try {
				ServerMain.runCommandsOnClient();
				Thread.sleep(500);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
	
			commands.add(ROTATE_RIGHT+"_"+(10));
			commands.add(FORWARD+"_"+100);
			commands.add(GIVE_SENSORS);
				
			try {
				ServerMain.runCommandsOnClient();
				Thread.sleep(500);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
				
			if(isOnStreet(ServerMain.sensorHistory)) {
				System.out.println("Scotty found the street again \\(*o*)/");
				onStreet = true;
				return;
			}
				
			commands.add(BACKWARD+"_"+100);
			commands.add(ROTATE_LEFT+"_"+(20));
			commands.add(FORWARD+"_"+100);
			commands.add(GIVE_SENSORS);
				
			try {
				ServerMain.runCommandsOnClient();
				Thread.sleep(500);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
				
			if(isOnStreet(ServerMain.sensorHistory)) {
				System.out.println("Scotty found the street again \\(*o*)/");
				onStreet = true;
				return;
			}
				
			commands.add(BACKWARD+"_"+100);
			commands.add(ROTATE_RIGHT+"_"+(10));

			try {
				ServerMain.runCommandsOnClient();
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			return;
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
				MonteCarloFrame.panel.currentBest = old_particles.get(i);
			}

		}
		
		return new_particles;
	}
	
	
	
	/**Distance is the value of the sonic sensor, movement the distance the bot moved in the last action  */
	public void monteCarlo()
	{
		BotMove latestMove, lastNotNull = null;
		SensorValue latestSensor;
		
		if(!isOnStreet(ServerMain.sensorHistory)) {
			System.out.println("ERROR >>> The bot was placed off the road");
			return;
		}
		onStreet = true;
		
		latestMove = ServerMain.moveHistory.get(ServerMain.moveHistory.size()-1);
		latestSensor = ServerMain.sensorHistory.get(ServerMain.sensorHistory.size()-1);
		
		if(latestMove.getDirection().equals(FORWARD) && latestMove.getValue() == 0){
			addCommand(FORWARD+"_"+100);
			try {
				ServerMain.runCommandsOnClient();
			}
			catch(Exception e) {
				e.printStackTrace();
				return;
			}
		}
		
		double[] probs = new double[NUMBER_OF_PARTICLES];
		
		
		//Are we still on the street?
		if(!isOnStreet(ServerMain.sensorHistory)) {	
			onStreet = false;
			
			while(!onStreet) {
				System.out.println("~~~~~   ? ~~~~~");
				System.out.println("~~~~~ O=O ~~~~~");
				System.out.println("~~~~~ [ ] ~~~~~");
				System.out.println("~~~~~ . . ~~~~~");
				findStreetCommands();
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
		
		for(int i = ServerMain.moveHistory.size()-1; i > 0; i--) {
			BotMove temp = ServerMain.moveHistory.get(i);
			if(temp.getDirection().equals(FORWARD) && temp.getValue() > 0) {
				lastNotNull = temp;
				break;
			}
		}
		
		for (int i = 0; i < particles.size(); i++) 
		{
			Particle currParticle = particles.get(i);
		
			double prob = currParticle.weight;
			particles.get(i).changeRotation(rotationTimes); 
			
			switch(currParticle.rotation) {
				case 0:
					break;
					
				case 90:
					particles.get(i).x += (lastNotNull != null) ? (lastNotNull.getValue()/10):(latestMove.getValue()/10); //TODO: Fill with actual robot movement
					break;
					
				case 180:
					break;
					
				case 270:
					particles.get(i).x -= (lastNotNull != null) ? (lastNotNull.getValue()/10):(latestMove.getValue()/10);
					break;
			}
			
//			particles.get(i).y += 1; //TODO: anpassen to relativ Y-position of the robot (+someting or -something)

			
			//check if particles are out of bounds
			if (particles.get(i).x >= 580 || particles.get(i).x <= 10){
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

			//Gewichtungsfunktion der Partikel
			double diff = 0;

			
			//Particle-Direction right; Looking left
			if (latestSensor.getMesuredDirection().equals(SensorValue.DIR_LEFT) && particles.get(i).rotation == 90){
				for (MapLine line : MonteCarloFrame.panel.lines){
					if (particles.get(i).y >= line.y1 && line.y1 == line.y2 && particles.get(i).x >= line.x1 && particles.get(i).x <= line.x2){
						diff = 1 - Math.abs(((particles.get(i).y - line.y1) - ( 100 * latestSensor.getSonicValue())));
					}					
				}
			}
				
			
			//Particle-Direction right; Looking right
			else if (latestSensor.getMesuredDirection().equals(SensorValue.DIR_RIGHT) && particles.get(i).rotation == 90){
				for (MapLine line : MonteCarloFrame.panel.lines){
					if (particles.get(i).y <= line.y1 && line.y1 == line.y2 && particles.get(i).x >= line.x1 && particles.get(i).x <= line.x2){
						diff = 1 - Math.abs(((line.y1 - particles.get(i).y) - ( 100 * latestSensor.getSonicValue())));
					}					
				}
			}
			
			
			//Particle-Direction left; Looking left
			else if (latestSensor.getMesuredDirection().equals(SensorValue.DIR_LEFT) && particles.get(i).rotation == 270){
				for (MapLine line : MonteCarloFrame.panel.lines){
					if (particles.get(i).y <= line.y1 && line.y1 == line.y2 && particles.get(i).x >= line.x1 && particles.get(i).x <= line.x2){
						diff = 1 - Math.abs(((line.y1 - particles.get(i).y) - ( 100 * latestSensor.getSonicValue())));
					}					
				}
			}
			
			
			//Particle-Direction left; Looking right
			else if (latestSensor.getMesuredDirection().equals(SensorValue.DIR_RIGHT) && particles.get(i).rotation == 270){
				for (MapLine line : MonteCarloFrame.panel.lines){
					if (particles.get(i).y >= line.y1 && line.y1 == line.y2 && particles.get(i).x >= line.x1 && particles.get(i).x <= line.x2)	{
						diff = 1 - Math.abs(((particles.get(i).y - line.y1) - ( 100 * latestSensor.getSonicValue())));
					}					
				}
			}
			
			
			prob = diff;

			probs[i] = prob;
		}

		normalize(probs);

		for (int i = 0; i < particles.size(); i++) {
			Particle currParticle = particles.get(i);
			currParticle.weight = probs[i];
		}
	
		particles = calculateBelieve(particles);
		for(Particle p : particles)
			System.out.println(p.toString());
		
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
