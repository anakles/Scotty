package Code;

import java.util.ArrayList;
import java.util.Arrays;

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
	
	public static final double STREET_COLOR_ID = 7.0;
	private static int multiplier = 1;
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
	
	public BotMove getLastForwardMove() {
		for(int i = ServerMain.moveHistory.size()-1; i > 0; i--) {
			BotMove temp = ServerMain.moveHistory.get(i);
			if(temp.getDirection().equals(FORWARD) && temp.getValue() > 0.0) {
				return temp;
			}
		}
		return null;
	}
	
	public SensorValue getLastFrontMeasure() {
		//Filter
		for(int i = ServerMain.sensorHistory.size()-1; i > 0; i--) {
			if(ServerMain.sensorHistory.get(i).getMesuredDirection().equals(SensorValue.DIR_FRONT)) {
				return ServerMain.sensorHistory.get(i);
			}	
		}	
		
		return null;		
	}
	
	public void findStreetCommands() throws NullPointerException{
		BotMove lastMove = ServerMain.moveHistory.get(ServerMain.moveHistory.size()-1);
		BotMove lastForwardMove = null;
		SensorValue lastFront = null;
		
		lastFront = getLastFrontMeasure();
		lastForwardMove = getLastForwardMove();
		
		//Case: We reached the end of the street
		if(lastFront.getSonicValue() < 0.2) {
			System.out.println("Scotty stands in front of a wall");
			commands.clear();
			commands.add(BACKWARD+"_"+(lastForwardMove.getValue()+50));
			commands.add(ROTATE_RIGHT+"_"+"168");
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
			
			
			commands.add(ROTATE_LEFT+"_"+(5*multiplier));
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
				
			commands.add(BACKWARD+"_"+120);
//			commands.add(GIVE_SENSORS);
//			
//			try {
//				ServerMain.runCommandsOnClient();
//				Thread.sleep(500);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return;
//			}
				
//			if(isOnStreet(ServerMain.sensorHistory)) {
//				System.out.println("Scotty found the street again \\(*o*)/");
//				onStreet = true;
//				return;
//			}
//			
			commands.add(ROTATE_RIGHT+"_"+(10*multiplier));
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
				
			commands.add(BACKWARD+"_"+120);
			commands.add(ROTATE_LEFT+"_"+(5*multiplier));

			
		}
		
		try {
			ServerMain.runCommandsOnClient();
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		if(isOnStreet(ServerMain.sensorHistory)) {
			System.out.println("Scotty found the street again \\(*o*)/");
			onStreet = true;
			return;
		}
		
		return;
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
			//double init_weight = (1 / NUMBER_OF_PARTICLES);
			double init_weight = 1;
			
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
        double beta = 0;
        bestProb = 0;
        double max = 0;

        for (Particle p : old_particles )
        {
            if (p.weight >= max)
            {
                max = p.weight;
            }
        }

        //int index = (int) (Math.random() * max);
        int index = (int)Math.random() * N;

        for (int i = 0; i < N; i++) 
        {
            beta += Math.random() * 2 * max;
            while (beta > old_particles.get(index).weight) 
            {
                beta -= old_particles.get(index).weight;
                index = (index + 1) % N;
            }
           
            new_particles.add(new Particle(old_particles.get(index)));
            


            //removed clone()
            //new_particles.add((Particle) old_particles.get(i));
            //new_particles.add(new Particle(old_particles.get(i)));


//            if (old_particles.get(index).weight > bestProb) 
//            {
//                bestProb = old_particles.get(index).weight;
//                MonteCarloFrame.panel.currentBest = old_particles.get(index);
//            }

        }

        return new_particles;
	}
	
	
	
	/**Distance is the value of the sonic sensor, movement the distance the bot moved in the last action  */
	public void monteCarlo()
	{
		BotMove latestMove, lastNotNull = null;
		SensorValue latestSensor;
		
		/*
		if(!isOnStreet(ServerMain.sensorHistory)) {
			System.out.println("ERROR >>> The bot was placed off the road");
			return;
		}
		onStreet = true;*/
		
		latestMove = ServerMain.moveHistory.get(ServerMain.moveHistory.size()-1);
		latestSensor = ServerMain.sensorHistory.get(ServerMain.sensorHistory.size()-1);
		
		if(latestMove.getDirection().equals(FORWARD) && latestMove.getValue() == 0){
		//	addCommand(FORWARD+"_"+100);
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
			multiplier = 1;
			
			while(!onStreet) {
				System.out.println("~~~~~~~~~~~~~~~~~~~");
				System.out.println("~WHERE THE F AM I?~");
				System.out.println("~~~~~~~   ? ~~~~~~~");
				System.out.println("~~~~~~~ O=O ~~~~~~~");
				System.out.println("~~~~~~~ [ ] ~~~~~~~");
				System.out.println("~~~~~~~ . . ~~~~~~~");
				System.out.println("~~~~~~~~~~~~~~~~~~~\n");
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					return;
				}
				findStreetCommands();
				multiplier++;
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
		
		
		System.out.println("> Searching the last forward move ...");
		for(int i = ServerMain.moveHistory.size()-1; i > 0; i--) {
			BotMove temp = ServerMain.moveHistory.get(i);
			if(temp.getDirection().equals(FORWARD) && temp.getValue() > 0) {
				lastNotNull = temp;
				break;
			}
		}
		
		System.out.println("> Iterating over all pixels...");
		for (int i = 0; i < particles.size(); i++) 
		{
			double diff = 0;
			Particle currParticle = particles.get(i);
			double prob = currParticle.weight;
			
//			System.out.println("<"+i+"> Changing current particles rotation");
			particles.get(i).changeRotation(rotationTimes); 
			
//			System.out.println("<"+i+"> Changing current particles coordinates");
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
			


			
			//check if particles are out of bounds
//			System.out.println("<"+i+"> Checking current particles location in bounds");
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

//			System.out.println("<"+i+"> Changing current particles believe");
			ArrayList<SensorValue> lastValueList = new ArrayList<>();
			lastValueList.add(ServerMain.sensorHistory.get(ServerMain.sensorHistory.size()-2));
			lastValueList.add(ServerMain.sensorHistory.get(ServerMain.sensorHistory.size()-3));
			
//			System.out.println(Arrays.toString(probs));
			for(SensorValue s : lastValueList) {
				System.out.println(">>>>>>>>>>>>>>>EVAL<<<<<<<<<<<<<<<<<");
				System.out.println(s.toString());
				
//				//Gewichtungsfunktion der Partikel
//				double diff = particles.get(i).weight;
				
				//Particle-Direction right; Looking left
				if (s.getMesuredDirection().equals(SensorValue.DIR_LEFT) && particles.get(i).rotation == 90)
				{
					for (MapLine line : MonteCarloFrame.panel.lines)
					{
						if (particles.get(i).y >= line.y1 && line.y1 == line.y2 && particles.get(i).x >= line.x1 && particles.get(i).x <= line.x2)
						{
							diff += 100 - Math.abs(((particles.get(i).y - line.y1) - (100 * s.getSonicValue())));
							System.out.println("I'm Number " + i + ". I'm Case: 1. ");
						}					
					}
				}
				
				//Particle-Direction right; Looking right
				else if (s.getMesuredDirection().equals(SensorValue.DIR_RIGHT) && particles.get(i).rotation == 90)
				{
					for (MapLine line : MonteCarloFrame.panel.lines)
					{
						if (particles.get(i).y <= line.y1 && line.y1 == line.y2 && particles.get(i).x >= line.x1 && particles.get(i).x <= line.x2)
						{
							diff += 100 - Math.abs(((line.y1 - particles.get(i).y) - (100 * s.getSonicValue())));
							System.out.println("I'm Number " + i + ". I'm Case: 2. ");
						}					
					}
				}
				
				//Particle-Direction left; Looking left
				else if (s.getMesuredDirection().equals(SensorValue.DIR_LEFT) && particles.get(i).rotation == 270)
				{
					for (MapLine line : MonteCarloFrame.panel.lines)
					{
						if (particles.get(i).y <= line.y1 && line.y1 == line.y2 && particles.get(i).x >= line.x1 && particles.get(i).x <= line.x2)
						{
							diff += 100 - Math.abs(((line.y1 - particles.get(i).y) - (100 * s.getSonicValue())));
							System.out.println("I'm Number " + i + ". I'm Case: 3. ");
						}					
					}
				}
			
				//Particle-Direction left; Looking right
				else if (s.getMesuredDirection().equals(SensorValue.DIR_RIGHT) && particles.get(i).rotation == 270)
				{
					for (MapLine line : MonteCarloFrame.panel.lines)
					{
						if (particles.get(i).y >= line.y1 && line.y1 == line.y2 && particles.get(i).x >= line.x1 && particles.get(i).x <= line.x2)	
						{
							diff += 100 - Math.abs(((particles.get(i).y - line.y1) - (100 * s.getSonicValue())));
							System.out.println("I'm Number " + i + ". I'm Case: 4. ");
						}					
					}
				}
	
				//LVL-Up the best particles, to create a bigger difference for the resempling wheel
                if (diff >= 160 && diff <= 200)
                {
                    diff += 400;
                }
				
				probs[i] = diff;
				System.out.println(i + " aktuelle Prob: " + diff);
				
			}
		}	
		
		System.out.println("> Normalizing the particles");
		normalize(probs);
		System.out.println(Arrays.toString(probs));
		
		System.out.println("> Updating all particles believe");
		for (int i = 0; i < particles.size(); i++) {
			Particle currPart = particles.get(i);
			if(probs[i] != 0.0)
				currPart.weight = probs[i];
		}	
		
		System.out.println("> Calculating the new believes");
		particles = calculateBelieve(particles);
		
		
		
		System.out.println("> Repainting all particles");
		MonteCarloFrame.panel.particles = particles;
		MonteCarloFrame.panel.repaint();
		MonteCarloFrame.panel.revalidate();
		/*
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					 MonteCarloFrame.repaintThis();
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		System.out.println(">>>>>>>>>>>>>>> This are the new believes <<<<<<<<<<<<<<<<<");
		for(Particle p : particles) {
			System.out.println(p.toString());
		}
				
}


	
	public final static void normalize(double[] doubles) {

		double sum = 0;
		for (int i = 0; i < doubles.length; i++) {
			sum += doubles[i];
		}
		
		normalize(doubles, sum);
		
	}
	
	public final static void normalize(double[] doubles, double sum) {

		try {
			if (Double.isNaN(sum)) {
			throw new IllegalArgumentException("Can't normalize array. Sum is NaN.");
			}
			if (sum != 0) {
				for (int i = 0; i < doubles.length; i++) {
					doubles[i] /= sum;
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
