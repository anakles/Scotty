import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import Utils.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class MonteCarloFrame extends JFrame {
	private static final String PATH = "res/3.2_Houses-1819.svg";
	private int width, height;
	private DrawPanel panel;
	ArrayList<MapLine> lines = new ArrayList<>();
	ArrayList<Particle> particles = new ArrayList<>();
	
	
	public MonteCarloFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setTitle("LeJOS Monte Carlo Map - D_GRUEN");
		this.setLayout(new BorderLayout());
		
		//Loading the Map
		Document svgMap = loadMap();
		//System.out.println("XML: "+svgMap.asXML());
		Element classElement = svgMap.getRootElement();
		Element group = classElement.element("g");
		
		//Defining the frame size by the width and height of the read map
		String pxWidth = classElement.attributeValue("width");
		String pxHeight = classElement.attributeValue("height");
		width = Integer.valueOf(pxWidth.substring(0, pxWidth.length()-2));
		height = Integer.valueOf(pxHeight.substring(0, pxHeight.length()-2));
		
		
		//Iterate over the single lines in the XML:
		Iterator<Element> itr = group.elementIterator();
		while(itr.hasNext()) {
			//System.out.println(itr.next().toString());
			Element e = itr.next();
			int x1 = MapLine.convertPxToInt(e.attributeValue("x1"));
			int x2 = MapLine.convertPxToInt(e.attributeValue("x2"));
			int y1 = MapLine.convertPxToInt(e.attributeValue("y1"));
			int y2 = MapLine.convertPxToInt(e.attributeValue("y2"));
			
			String stroke = e.attributeValue("stroke");
			
			MapLine line = new MapLine(x1, x2, y1, y2, stroke);
			System.out.println(line.toString());
			
			lines.add(line);
			
		}
		
		//Init paricles
		
		
		

		//Other stuff
		this.setSize(width+50, height+50);
		
		
		
		//drawing the map/lines
		panel = new DrawPanel(lines, particles);
		this.add(panel, BorderLayout.CENTER);
		this.repaint();
		this.revalidate();
		
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("MonteCarloFrame initialized.");
	}
	
	
	private Document loadMap() {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(PATH);
			return document;
		}
		catch(DocumentException e) {
			e.printStackTrace();
			System.out.println("ERROR >>> Could not parse the XML/SVG map");
		}
		
		return null;
		
	}
	
	
	private class DrawPanel extends JPanel{
		private ArrayList<MapLine> lines;
		private ArrayList<Particle> particles;
		
		public DrawPanel(ArrayList<MapLine> lines, ArrayList<Particle> particles){
			this.lines = lines;
			this.particles = particles;
		}
		
		
		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
			
			//drawLines
			for(MapLine line : lines) {
				g.setColor(Color.decode(line.stroke));
				g.drawLine(line.x1, line.y1, line.x2, line.y2);
			}	
			
			/*
			//Draw Particles
			for(Particle p : particles) {
				//draw dot
			}
			
			//draw Bot Current position
			*/
		}
				
	}

}
