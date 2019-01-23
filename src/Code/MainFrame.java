package Code;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame {
	private static MainFrame mainFrame;
	private static JTextField txt_port;
	private static JButton btn_startServer;
	private static JPanel commandListPanel;
	
	//Controls for bot movements
	private static JTextField txt_forward;
	private static JButton btn_forward;
	private static JTextField txt_backward;
	private static JButton btn_backward;
	private static JTextField txt_rotateLeft;
	private static JButton btn_rotateLeft;
	private static JTextField txt_rotateRight;
	private static JButton btn_rotateRight;
	//No text for sensors or "end" command needed
	private static JButton btn_sensors;
	private static JButton btn_end;
	private static JButton btn_startMonteCarlo;

	public MainFrame() {
		this.setTitle("LeJOS Server - D_GRUEN");
		this.setSize(600, 600);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		
		
		this.add(createServerPanel(), BorderLayout.NORTH);
		this.add(createInstructionPanel(), BorderLayout.WEST);
		commandListPanel = createCommandListPanel();
		this.add(commandListPanel, BorderLayout.CENTER);
		this.add(createShortCutPanel(), BorderLayout.SOUTH);
		this.mainFrame = this;
		
		redrawCommands();
		
		this.repaint();
		this.revalidate();
	}
	
	
	private static JPanel createInstructionPanel() {
		JPanel instructionPanel = new JPanel();
		//instructionPanel.setLayout(new GridLayout(0, 3));
		instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));
		instructionPanel.setSize(new Dimension(100, 600));
		
		txt_forward = new JTextField();
		txt_forward.setText("0");
		txt_backward = new JTextField();
		txt_backward.setText("0");
		txt_rotateLeft = new JTextField();
		txt_rotateLeft.setText("0");
		txt_rotateRight = new JTextField();
		txt_rotateRight.setText("0");
		
		btn_forward = new JButton("Add");
		btn_forward.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ServerMain.sendCommandToClient(MonteCarloService.FORWARD, txt_forward.getText());
				txt_forward.setText("0");
				redrawCommands();
			}
		});
		
		btn_backward = new JButton("Add");
		btn_backward.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ServerMain.sendCommandToClient(MonteCarloService.BACKWARD, txt_backward.getText());
				txt_backward.setText("0");
				redrawCommands();
			}
		});
		
		btn_rotateLeft = new JButton("Add");
		btn_rotateLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ServerMain.sendCommandToClient(MonteCarloService.ROTATE_LEFT, txt_rotateLeft.getText());
				txt_rotateLeft.setText("0");
				redrawCommands();
			}
		});
		
		btn_rotateRight = new JButton("Add");
		btn_rotateRight.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ServerMain.sendCommandToClient(MonteCarloService.ROTATE_RIGHT, txt_rotateRight.getText());
				txt_rotateRight.setText("0");
				redrawCommands();
			}
		});
		
		btn_sensors = new JButton("Add");
		btn_sensors.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ServerMain.sendCommandToClient(MonteCarloService.SENSORS, "0");
				redrawCommands();
			}
		});	
		
		btn_end = new JButton("Disconnect");
		btn_end.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ServerMain.sendCommandToClient(MonteCarloService.END, "0");
				redrawCommands();
			}
		});	
		
		btn_startMonteCarlo = new JButton("Start localisation");
		btn_startMonteCarlo.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ServerMain.sendCommandToClient(MonteCarloService.SENSORS, "0");
				try {
					//ServerMain.sendCommandToClient(MonteCarloService.END, "0");
					ServerMain.runCommandsOnClient();
				} catch (Exception e2) {
					System.out.println("Couldn't run commands");
				}
				redrawCommands();
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ServerMain.startMonteCarlo();
			}
		});
		
		
		//Adding controls to UI
		instructionPanel.add(new JLabel("Travel forward: "));
		instructionPanel.add(txt_forward);
		instructionPanel.add(btn_forward);
		
		instructionPanel.add(new JLabel("Travel backward: "));
		instructionPanel.add(txt_backward);
		instructionPanel.add(btn_backward);
		
		instructionPanel.add(new JLabel("Rotate left: "));
		instructionPanel.add(txt_rotateLeft);
		instructionPanel.add(btn_rotateLeft);
		
		instructionPanel.add(new JLabel("Rotate right: "));
		instructionPanel.add(txt_rotateRight);
		instructionPanel.add(btn_rotateRight);
		
		instructionPanel.add(new JLabel("Sensors "));
		instructionPanel.add(new JLabel(""));
		instructionPanel.add(btn_sensors);	
		
		instructionPanel.add(new JLabel("Monte Carlo"));
		instructionPanel.add(new JLabel(""));
		instructionPanel.add(btn_startMonteCarlo);	
		
		instructionPanel.add(new JLabel("Disconnect "));
		instructionPanel.add(new JLabel(""));
		instructionPanel.add(btn_end);	
		
		return instructionPanel;
	
	}
	
	private static JPanel createServerPanel() {
		JPanel serverPanel = new JPanel();
		serverPanel.setLayout(new GridLayout(0, 4));
		txt_port = new JTextField();
		
		//Setting the default port to 6666:
		txt_port.setText("6666");
		btn_startServer = new JButton("Start Server");
		btn_startServer.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					ServerMain.startServer(Integer.valueOf(txt_port.getText()));
				} catch (IOException e1) {
					e1.printStackTrace();
					System.out.println("Server could not be started");
				}
			}
		});
		
		
		serverPanel.add(new JLabel("Port: "));
		serverPanel.add(txt_port);
		serverPanel.add(new JLabel(""));
		serverPanel.add(btn_startServer);
	
		
		
		return serverPanel;
	}
	
	private JPanel createCommandListPanel() {
		JPanel commandListPanel = new JPanel();
		commandListPanel.setLayout(new GridLayout(30, 1));
		commandListPanel.setSize(new Dimension(400, 600));
		commandListPanel.setBackground(Color.LIGHT_GRAY);
	
		return commandListPanel;
	}
	
	private JPanel createShortCutPanel() {
		JPanel shortcutPanel = new JPanel();
		shortcutPanel.setLayout(new GridLayout(1, 0));
		
		JButton btn_addAufgabe3_1 = new JButton("Aufgabe 3.1");
		btn_addAufgabe3_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ServerMain.getMonteCarloService().addAufgabe3_1Commands();
				redrawCommands();
			}
		});
		
		
		JButton btn_runOnClient = new JButton("Run commands");
		btn_runOnClient.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					//ServerMain.sendCommandToClient(MonteCarloService.END, "0");
					ServerMain.runCommandsOnClient();
				} catch (Exception e2) {
					System.out.println("Couldn't run commands");
				}
				redrawCommands();
			}
		});
		
		JButton btn_clearQueue = new JButton("Clear queue");
		btn_clearQueue.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ServerMain.getMonteCarloService().clearCommands();
				redrawCommands();
			}
		});
		
		shortcutPanel.add(btn_addAufgabe3_1);
		shortcutPanel.add(btn_clearQueue);
		shortcutPanel.add(btn_runOnClient);
		
		return shortcutPanel;
	}
	
	
	public static void redrawCommands() {
		
		ArrayList<String> commands = ServerMain.getMonteCarloService().getCommands();
		commandListPanel.removeAll();
		
		if(commands.isEmpty()) {
			System.out.println("No commands to print");
			commandListPanel.add(new JLabel("No commands added!", SwingConstants.CENTER));
		}
		else {
			for(String s : commands) {
				JLabel label = new JLabel(s, SwingConstants.CENTER);
				label.setBorder(BorderFactory.createDashedBorder(Color.black, 1, (float) 0.5));
				commandListPanel.add(label);
			}		
		}
		
		mainFrame.repaint();
		mainFrame.revalidate();
	}
}
