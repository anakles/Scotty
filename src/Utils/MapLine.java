package Utils;

public class MapLine {
	public int x1;
	public int y1;
	public int x2;
	public int y2;
	
	public String stroke;
	
	public MapLine(int x1, int x2, int y1, int y2, String stroke) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		
		this.stroke = stroke;	
		
	}
	
	public static int convertPxToInt(String s) {
		return Integer.valueOf(s.substring(0, s.length()-2));		
	}
	
	@Override
	public String toString() {
		return "MapLine: ("+x1+"|"+y1+")\t-> ("+x2+"|"+y2+")\tStroke: "+stroke;
	}
}
