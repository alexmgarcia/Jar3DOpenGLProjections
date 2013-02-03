/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 *
 * This class represents the Axonometric Projection
 */
public class AxonometricProjection {

	// Angles
	private double theta;
	private double gamma;
	
	/**
	 * Constructs an axonometric projection
	 * @param A initial A angle in degrees
	 * @param B initial B angle in degrees
	 */
	public AxonometricProjection(double A, double B) {
		this.gamma = Math.asin(Math.sqrt(Math.tan(Math.toRadians(A))*Math.tan(Math.toRadians(B))));
		this.theta = Math.atan(Math.sqrt(Math.tan(Math.toRadians(A))/Math.tan(Math.toRadians(B))))-Math.PI/2;
	}
	
	/**
	 * Sets the gamma and theta angles using a new A and B angles
	 * @param A new A angle in degrees
	 * @param B new B angle in degrees
	 */
	public void setAB(double A, double B) {
		this.gamma = Math.asin(Math.sqrt(Math.tan(Math.toRadians(A))*Math.tan(Math.toRadians(B))));
		this.theta = Math.atan(Math.sqrt(Math.tan(Math.toRadians(A))/Math.tan(Math.toRadians(B))))-Math.PI/2;
	}
		
	/**
	 * Returns the gamma angle value in degrees
	 * @return gamma value in degrees
	 */
	public double getGamma() {
		return Math.toDegrees(gamma);
	}
	
	/**
	 * Returns the theta angle value in degrees
	 * @return theta angle value in degrees
	 */
	public double getTheta() {
		return Math.toDegrees(theta);
	}
	
	/**
	 * Sets the gamma angle value
	 * @param gamma new gamma angle value in degrees
	 */
	public void setGamma(double gamma) {
		this.gamma = Math.toRadians(gamma);
	}
	
	/**
	 * Sets the theta angle value
	 * @param theta new theta angle value in degrees
	 */
	public void setTheta(double theta) {
		this.theta = Math.toRadians(theta);
	}
	
}
