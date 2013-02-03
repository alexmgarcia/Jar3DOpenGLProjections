import java.nio.ByteBuffer;

/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 *
 * This class represents the Extension File Filter to use when selecting a .x24 file
 */
public class Jar3DTexture {
	
	// Texture size
	public static final int TEXTURE_WIDTH = 256;
	public static final int TEXTURE_HEIGHT = 256;
	
	// Bottom left points of the texture
	private double[] textureBottomLeftPoints = {0.5, 0.5,	0,   0.5,	
			0.5, 0,		0, 0};
	
	// Bottom right points of the texture
	private double[] textureBottomRightPoints = {1.0, 0.5, 	0.5, 0.5,	
			1.0, 0,			0.5, 0};
	
	// Top left points of the texture
	private double[] textureTopLeftPoints = {0.5, 1.0,	  0, 1.0,	
			0.5, 0.5,		0, 0.5};
	
	// Top right points of the texture
	private double[] textureTopRightPoints = {1.0, 1.0, 	0.5, 1.0, 
			1.0, 0.5,		0.5, 0.5 };
	
	// Buffer that contains the texture bytes
	private ByteBuffer imageBuffer;
		
	/**
	 * Sets the buffer that contains the texture bytes
	 * @param newBuffer new buffer that contains the texture bytes
	 */
	public void setImageBuffer(ByteBuffer newBuffer) {
		imageBuffer = newBuffer;
	}
	
	/**
	 * Returns the buffer that contains the texture bytes
	 * @return buffer that contains the texture bytes
	 */
	public ByteBuffer getImageBuffer() {
		return imageBuffer;
	}
	
	/**
	 * Returns the bottom left points of the texture
	 * @return bottom left points of the texture
	 */
	public double[] getTextureBottomLeftPoints() {
		return textureBottomLeftPoints;
	}
	
	/**
	 * Returns the bottom right points of the texture
	 * @return bottom right points of the texture
	 */
	public double[] getTextureBottomRightPoints() {
		return textureBottomRightPoints;
	}
	
	/**
	 * Returns the top left points of the texture
	 * @return top left points of the texture
	 */
	public double[] getTextureTopLeftPoints() {
		return textureTopLeftPoints;
	}
	
	/**
	 * Returns the top right points of the texture
	 * @return top right points of the texture
	 */
	public double[] getTextureTopRightPoints() {
		return textureTopRightPoints;
	}
	
	
	
}
