package Code;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import Utils.*;
import javax.swing.UIManager;

public class ServerMain {
	
	private static MainFrame mainFrame;
	private static MonteCarloService monteCarloService;
	private static MonteCarloFrame monteCarloFrame;
	private static boolean serverIsRunning = false;
	
	public static ServerSocket serverSocket = null;
	public static Socket clientSocket = null;
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(">>> LeJOS Server was started <<<");
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
//			e.printStackTrace();
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
	        			String response = readMessage(in);
	        			System.out.println("SENSORS: "+response);
	        		}
	        		
	        		String response = readMessage(in);
	        		if(response.equals("DONE")) {
	        			System.out.println("Client has finally something right: "+singleCommand);
	        			commands.remove(0);
	        			MainFrame.redrawCommands();
	        		}
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
				//e.printStackTrace();
				System.out.println(">>> Server lost connection to client! <<<");       	
			}
		}	
		else {
			System.out.println(">>> Start server before running commands <<<");
		}
	}
}
