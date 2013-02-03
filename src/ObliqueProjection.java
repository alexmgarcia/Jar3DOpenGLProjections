/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 *
 * This class represents the Oblique Projection
 */
public class ObliqueProjection {
	
	// Positions in the matrix that will have its values changed
	private static int LCOS = 8;
	private static int LSIN = 9;
	
	// Projection l and angle parameters
	private double alpha;
	private double l;
	
	// Projection matrix
	private double matrix[] = {1, 0, 0, 0,   0, 1, 0, 0,   -1, 0, 1, 0,    0, 0, 0, 1};
	
	/**
	 * Constructs an Oblique Projection
	 * @param alpha initial alpha angle value in degrees
	 * @param l initial l value
	 */
	public ObliqueProjection(double alpha, double l) {
		this.alpha = alpha;
		this.l = l;
		matrix[LCOS] = -l*Math.cos(Math.toRadians(alpha));
		matrix[LSIN] = -l*Math.sin(Math.toRadians(alpha));
	}
	
	/**
	 * Constructs an Oblique Projection with an initial alpha angle value of 0 and
	 * l value of 1
	 */
	public ObliqueProjection() {
		this.alpha = 0;
		this.l = 1;
	}
		
	/**
	 * Returns the alpha angle value
	 * @return alpha angle value in degrees
	 */
	public double getAlpha() {
		return Math.toDegrees(alpha);
	}
	
	/**
	 * Returns the l value
	 * @return l value
	 */
	public double getL() {
		return l;
	}
	
	/**
	 * Sets the alpha angle value
	 * @param alpha new alpha angle value in degrees
	 */
	public void setAlpha(double alpha) {
		this.alpha = Math.toRadians(alpha);
	}
	
	/**
	 * Sets the l value
	 * @param l
	 */
	public void setL(double l) {
		this.l = l;
	}
	
	/**
	 * Computes the oblique projection matrix
	 */
	private void computeMatrix() {
		matrix[LCOS] = -l*Math.cos(alpha);
		matrix[LSIN] = -l*Math.sin(alpha);
	}
	
	/**
	 * Returns the oblique projection matrix
	 * @return oblique projection matrix
	 */
	public double[] getMatrix() {
		computeMatrix();
		return matrix;
	}
}
