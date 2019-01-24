package Code;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import Utils.*;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ServerMain {
	
	private static final double THRESHOLD = 0.05;
	
	private static MainFrame mainFrame;
	private static MonteCarloService monteCarloService;
	public static MonteCarloFrame monteCarloFrame;
	private static boolean serverIsRunning = false;
	
	public static ServerSocket serverSocket = null;
	public static Socket clientSocket = null;
	
	public static ArrayList<BotMove> moveHistory = new ArrayList<>();
	public static ArrayList<SensorValue> sensorHistory = new ArrayList<>();	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(">>> LeJOS Server was started <<<");
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			System.out.println("ERROR @ Loading Look and Feel");
		}
		
		createInterface();
		createMonteCarloService();		
		
	}
	
	private static void createInterface() {
		mainFrame = new MainFrame();
		mainFrame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent ew)
		    {
		    	try {
					serverSocket.close();
					clientSocket.close();
				} catch (Exception e) {
					//who cares...
				}
		    	
		    }
		});
		
		monteCarloFrame = new MonteCarloFrame();
	}
	
	private static void createMonteCarloService() {
		monteCarloService = new MonteCarloService();
	}
	
	
	/**
	 * @throws IOException
	 */
	public static void startServer(int port) throws IOException {
		
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server is listening to port "+port);
		} catch (IOException e) {
			//e.printStackTrace();
			System.err.println("Server couldn't listen to port");
		}
		
		
        try {
        	System.out.println("Server is waiting for incoming connection... ");
            clientSocket = serverSocket.accept();
            System.out.println("Server has accepted a connection from "+clientSocket.getInetAddress());
        } catch (IOException e) {
        	//e.printStackTrace();
            System.err.println("Accept failed.");
        }
         
        
        serverIsRunning = true;

	}
	
	private static void sendMessage(String msg, PrintWriter out) {
		try{
			out.print(msg+"\r\n");
			out.flush();
		}
		catch (Exception e){
			e.printStackTrace();
			System.out.println("The client was not ready for my glory!");
		}	
	}
	
	
	private static String readMessage(BufferedReader in) {
		String response = "WRONG";
		try {
			response = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("I was not ready for clients glory!");
		}
			
		return response;
	}

	public static void sendCommandToClient(String cmd, String val) {
		int value;
		try {
			value = Integer.valueOf(val);
			String command = cmd + "_" + value;
			System.out.println("Command "+command+" added.");
			monteCarloService.addCommand(command);
		}
		catch(NumberFormatException e) {
			//e.printStackTrace();
			System.out.println("Error >> Invalid number for steering");
		}
	}
	
	public static MonteCarloService getMonteCarloService() {
		if(monteCarloService != null) return monteCarloService;
		else {
			monteCarloService = new MonteCarloService();
			return monteCarloService;
		}
	}
	
	public static void startMonteCarlo() {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("============================================================");
				
				monteCarloService.monteCarlo();			
			    
			}
		};
		
		Thread t = new Thread(r);
		t.start();
		
		
		try {
			t.join();
			monteCarloFrame.repaintThis();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			
		if(monteCarloFrame.panel.currentBest.weight < THRESHOLD) {
			monteCarloService.addCommand(MonteCarloService.FORWARD+"_"+100);
			monteCarloService.addCommand(MonteCarloService.GIVE_SENSORS);
				
			try{
				runCommandsOnClient();
			}
			catch (Exception e) {
				//e.printStackTrace();
				System.out.println(">>> ERROR: Could not send commands to client");
			}
			startMonteCarlo();
		}
		else {
			System.out.println(">>>> ~~~~~~~ <<<<");
			System.out.println(">>>> VICTORY <<<<");
			System.out.println(">>>> ~~~~~~~ <<<<");
			monteCarloFrame.repaintThis();
		}	
		
		
	}
	
	public static void runCommandsOnClient() throws IOException{
		if(serverIsRunning) {
			
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        
			try {
	        	//Getting commands for client:
	        	
	        	ArrayList<String> commands = monteCarloService.getCommands(); 
	        	
	        	while(!commands.isEmpty()) {
	        		String singleCommand = commands.get(0);
	        		
	        		sendMessage(singleCommand, out);
	        		
	        		if(singleCommand.equals("sensor_0")) {
	        			for(int i = 0; i < 3; i++) {
	        				String response = readMessage(in);
		        			System.out.println("SENSORS: "+response);
		        			SensorValue sensorValue = trimSensorValues(response);
		        			ServerMain.sensorHistory.add(sensorValue);
		        			ServerMain.moveHistory.add(new BotMove(MonteCarloService.FORWARD, 0));
		        			System.out.println(sensorValue.toString());
		        			
	        			}
	        		}
	        		
	        		String response = readMessage(in);
	        		
		       		if(response.equals("DONE")) {
		       			System.out.println("Client has finally something right: "+singleCommand);
		       			//Adding command to history
		       			if(!singleCommand.equals("sensor_0"))
		       				moveHistory.add(trimCommands(singleCommand));
		       			
		       			commands.remove(0);
	        			MainFrame.redrawCommands();
		       		}
		        		/*else if (response.contains("."))
		        		{
		        			monteCarloService.monteCarlo(response);	        		
		        		}
		        		else if (response.equals("Infinity"))
		        		{
		        			//TODO: von der Straße abgekommen
		        		}
		        		*/
		        	else if(response.equals("CLOSE")) {
		        		System.out.println("Client "+clientSocket.getInetAddress()+" is disconnecting...");
		        			
		        		commands.remove(0);
		        		MainFrame.redrawCommands();
		        			
		        		try {
		    				serverSocket.close();
		    				clientSocket.close();
		    				System.out.println(">>> Server was shut down! <<<");
		    			} catch (Exception e) {
		    				// TODO: handle exception
		    			}
		        		break;
		        	}
		        	else {
		        		System.out.println("Client f+cked up again! "+response);
		        		sendMessage(singleCommand, out);
		        	}
		        		
		        		
	        	}
	        }
			catch (Exception e) {
				e.printStackTrace();
				System.out.println(">>> Server lost connection to client! <<<");       	
			}
		}	
		else {
			System.out.println(">>> Start server before running commands <<<");
		}
	}
	
	/* String sonic = "Dist: "+sampleSonic[0];
	 * String colorId = "Color ID: "+sampleColor[0];
	 * writeMsg(server, "" + sonic +" | ColorID: "+colorId);*/
	private static SensorValue trimSensorValues(String sensorString) {
		int indexOfCut = 0;
		
		for(int i = 0; i < sensorString.length(); i++) {
			if(sensorString.charAt(i) == '|')
				indexOfCut = i;
		}
		
		String valSonic = sensorString.substring(0, indexOfCut);
		String valColor = sensorString.substring(indexOfCut+1);
		
		//System.out.println(valSonic);
		//System.out.println(valColor);
		
		for(int i = 0; i < valSonic.length(); i++) {
			if(valSonic.charAt(i) == ':')
				valSonic = valSonic.substring(i+1);
		}
		
		System.out.println("ValSonic: "+valSonic);
		
		for(int i = 0; i < valColor.length(); i++) {
			if(valColor.charAt(i) == ':')
				valColor = valColor.substring(i+1);
		}
		
		//System.out.println("ValColor: "+valColor);
		
		double sonic = (valSonic.equals("Infinity")) ? Double.POSITIVE_INFINITY:Double.valueOf(valSonic);
		double color = Double.valueOf(valColor);
		
		String direction = "";
		if(sensorHistory.isEmpty()) direction = SensorValue.DIR_RIGHT;
		else {
			switch((sensorHistory.get(sensorHistory.size()-1).getMesuredDirection())) {
			
				case SensorValue.DIR_RIGHT:
					direction = SensorValue.DIR_LEFT;
					break;
					
				case SensorValue.DIR_LEFT:
					direction = SensorValue.DIR_FRONT;
					break;
					
				case SensorValue.DIR_FRONT:
					direction = SensorValue.DIR_RIGHT;
					break;
			}
		}
		
		return new SensorValue(sonic, color, direction);
	}
	
	
	private static BotMove trimCommands(String command) {
		BotMove move = null;
		
		
		for (int i = 0; i < command.length(); i++)
    	{
    		if(command.charAt(i) == '_')
    		{
    			String cmd = command.substring(0, i);
    			String value = command.substring(i+1);
    			move = new BotMove(cmd, Double.valueOf(value));
    		}
    	}
		return move;
	}
}
