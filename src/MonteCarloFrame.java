import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.net.URI;
import org.apache.batik.*;
import org.apache.batik.swing.JSVGCanvas;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MonteCarloFrame extends JFrame {
	private static final String PATH = "res/3.2_Houses-1819.svg";
	private int width, height;
	
	
	public MonteCarloFrame() {
		//this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setVisible(true);
		this.setTitle("LeJOS Monte Carlo Map - D_GRUEN");

		//Defining the frame size by the width and height of the read map
		this.setSize(800, 500);
		
		
		//loading the map
		try {
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.setSize(new  Dimension(800, 500));
			
			URI uri = new File(PATH).toURI();
		    JSVGCanvas canvas = new JSVGCanvas();
		    canvas.setURI(uri.toString());
		    
			panel.add(canvas, BorderLayout.CENTER);
			this.getContentPane().add(panel);
			
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println(">>> ERROR: File could not be loaded!");
		}	
	
			
		
		//Other stuff
		this.repaint();
		this.revalidate();
		
		System.out.println("MonteCarloFrame initialized.");
	}
	

}
