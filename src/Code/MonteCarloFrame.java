package Code;

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
	public static int width, height;
	public static DrawPanel panel;
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
		this.setSize(width+50, height+50);
		
		//Iterate over the single lines in the XML:
		Iterator<Element> itr = group.elementIterator();
		while(itr.hasNext()) {
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

		
		//Drawing the inner square, where the particles are allowed
		panel = new DrawPanel();
		calcInnerSquare(lines);
		
		
		//Init particles:
		particles = MonteCarloService.getParticles();
		panel.particles = particles;
		panel.lines = lines;
		
		
		
		//drawing the map/lines
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
	
	
	public class DrawPanel extends JPanel{
		public ArrayList<MapLine> lines;
		public ArrayList<Particle> particles;
		//inner bounds
		public int x1 = -1, x2 = -1, y1 = -1, y2 = -1;
		
		//Please choose a even number:
		private int particleRadius = 10;
		
		public DrawPanel(){		}
		
		
		
		
		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
			
			if(x1 >= 0 && x2 >= 0 && y1 >= 0 && y2 >= 0) {
				g.setColor(Color.LIGHT_GRAY);
				g.drawRect(x1, y1, x2, y2-y1);				
				g.fillRect(x1, y1, x2, y2-y1);
			}
			
			//drawLines
			for(MapLine line : lines) {
				g.setColor(Color.decode(line.stroke));
				g.drawLine(line.x1, line.y1, line.x2, line.y2);
			}	
			
			
			
			
			
			//Draw Particles
			for(Particle p : particles) {
				g.setColor(Color.RED);
				g.drawOval(p.x - (int)particleRadius/2, p.y - (int)particleRadius/2, particleRadius, particleRadius);
				g.fillOval(p.x - (int)particleRadius/2, p.y - (int)particleRadius/2, particleRadius, particleRadius);
				
				//draw orientation of particle:
				int startX = p.x;
				int startY = p.y;
				int endX = 0;
				int endY = 0;
				/*
				double angle = p.rotation * Math.PI / 180;
				
				int endX   = (int) (p.x + 5 * Math.sin(p.rotation));
				int endY   = (int) (p.y + 5 * Math.cos(p.rotation));	*/
				
				switch(p.rotation) {
					case 0:
						endX = startX;
						endY = startY - 10;
						break;
						
					case 90:
						endX = startX + 10;
						endY = startY;
						break;
						
					case 180:
						endX = startX;
						endY = startY + 10;
						break;
						
					case 270:
						endX = startX - 10;
						endY = startY;
						break;
				
				}
				
				
				g.drawLine(startX, startY, endX, endY);
			}
			
			
			//draw Bot Current position
			
			
		}
		
		public void setInnerSquare(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
				
	}
	
	public static void calcInnerSquare(ArrayList<MapLine> lines) {
		//Known by the size of the map:
		int x1 = 0;
		int x2 = MonteCarloFrame.width;
		
		//To calc:
		int y1 = -1;
		int y2 = -1;
		
		ArrayList<Integer> candidates = new ArrayList<>();
		
		for(MapLine line : lines) {
			if(candidates.contains(line.y1)) continue;
			else candidates.add(line.y1);
			
			if(candidates.contains(line.y2)) continue;
			else candidates.add(line.y2);
			
		}
		
		for(int i = 0; i < candidates.size(); i++) {
			if(candidates.get(i) == 0) candidates.remove(i);
			if(candidates.get(i) == height) candidates.remove(i);
		}
		
		//now select the "middle" candidates
		if(candidates.get(0) > candidates.get(1)) {
			y1 = candidates.get(1);
			y2 = candidates.get(0);
		}
		else {
			y1 = candidates.get(0);
			y2 = candidates.get(1);
		}
		
		System.out.println("I've found "+candidates.size()+" candidates!");
		System.out.println("The inner square is from ("+x1+"|"+y1+") -> ("+x2+"|"+y2+")");
		
		panel.setInnerSquare(x1, y1, x2, y2);
	
}

}
