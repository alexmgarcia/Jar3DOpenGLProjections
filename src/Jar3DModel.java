/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 *
 * This class represents the 3D Model of a Jar
 */
public class Jar3DModel {
	
	// Jar max width
	public static final double MAX_WIDTH = 0.95;

	// Jar 3D Model bottom points
	private double[] bottomPoints = { 
	0, -0.03, (MAX_WIDTH/4),   -(MAX_WIDTH/8), -0.03, (MAX_WIDTH/4),    -(MAX_WIDTH/4), -0.03, (MAX_WIDTH/8),    -(MAX_WIDTH/4), -0.03, 0,
	0, -(MAX_WIDTH/4), (MAX_WIDTH/2),   -(MAX_WIDTH/4), -(MAX_WIDTH/4), (MAX_WIDTH/2),    -(MAX_WIDTH/2), -(MAX_WIDTH/4), (MAX_WIDTH/4),   -(MAX_WIDTH/2), -(MAX_WIDTH/4), 0,  
	0, -(MAX_WIDTH/2), (MAX_WIDTH/2),   -(MAX_WIDTH/4), -(MAX_WIDTH/2), (MAX_WIDTH/2),	 -(MAX_WIDTH/2), -(MAX_WIDTH/2), (MAX_WIDTH/4),   -(MAX_WIDTH/2), -(MAX_WIDTH/2), 0,  
	0, -(MAX_WIDTH/2), 0,   		 0, -(MAX_WIDTH/2), 0,	     0, -(MAX_WIDTH/2), 0,	  0, -(MAX_WIDTH/2), 0
	

};

// Jar 3D Model top points
	private double[] topPoints = { 
	  0, (MAX_WIDTH/2), (MAX_WIDTH/4),   -(MAX_WIDTH/8), (MAX_WIDTH/2), (MAX_WIDTH/4),     -(MAX_WIDTH/4), (MAX_WIDTH/2), (MAX_WIDTH/8),    -(MAX_WIDTH/4), (MAX_WIDTH/2), 0,
	  0, 0.40, (MAX_WIDTH/4),    -(MAX_WIDTH/8), 0.40, (MAX_WIDTH/4),   -(MAX_WIDTH/4), 0.40, (MAX_WIDTH/8),     -(MAX_WIDTH/4), 0.40, 0,
	  0, 0.17375, 0,		0, 0.17375, 0,	    0, 0.17375, 0,		  0, 0.17375, 0,		
	  0, -0.03, (MAX_WIDTH/4),   -(MAX_WIDTH/8), -0.03, (MAX_WIDTH/4),    -(MAX_WIDTH/4), -0.03, (MAX_WIDTH/8),    -(MAX_WIDTH/4), -0.03, 0
	};
	
	/**
	 * Returns the jar bottom points
	 * @return jar bottom points
	 */
	public double[] getBottomPoints() {
		return bottomPoints;
	}
	
	/**
	 * Returns the jar top points
	 * @return jar top points
	 */
	public double[] getTopPoints() {
		return topPoints;
	}

}
