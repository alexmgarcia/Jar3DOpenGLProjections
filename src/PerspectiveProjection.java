/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 *
 * This class represents the Perspective Projection
 */
public class PerspectiveProjection {
	
	// Position in matrix of d value
	private static int D_POS = 11;
	
	// Projection matrix
	private double matrix[] = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, -1, 0, 0, 0, 1};
	
	private double d;

	/**
	 * Constructs a Perspective Projection
	 * @param d initial d value
	 */
	public PerspectiveProjection(double d) {
		this.d = d;
	}
	
	/**
	 * Constructs a Perspective Projection with an initial d of value 2
	 */
	public PerspectiveProjection() {
		this.d = 1.0;
	}
	
	/**
	 * Returns d value
	 * @return d d value
	 */
	public double getD() {
		return d;
	}
	
	/**
	 * Sets d value
	 * @param d d value
	 */
	public void setD(double d) {
		this.d = d;
	}
	
	/**
	 * Computes the perspective projection matrix
	 */
	private void computeMatrix() {
		matrix[D_POS] = -1.0/d;
	}
	
	/**
	 * Returns the perspective projection matrix
	 * @return perspective projection matrix
	 */
	public double[] getMatrix() {
		computeMatrix();
		return matrix;
	}
	
}
