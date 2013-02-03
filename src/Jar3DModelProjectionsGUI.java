import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.media.opengl.GLEventListener;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import com.sun.opengl.util.GLUT;

/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 *
 * This class represents the Jar 3D Model Projections GUI
 */

public class Jar3DModelProjectionsGUI implements GLEventListener, KeyListener {

	// The 4 possible views
	private enum view {
		TOP_VIEW, FRONT_VIEW, LEFT_VIEW, PROJECTION
	};

	// Default texture image file
	private static final String DEFAULT_IMAGE = "src/img/DJoseh.x24";
	
	// Projections default values
	private static final int AXON_DEFAULT_THETA = 340;
	private static final int AXON_DEFAULT_GAMMA = 19;
	private static final int AXON_DEFAULT_A = 42;
	private static final int AXON_DEFAULT_B = 7;
	private static final double OBL_DEFAULT_L = 0.5;
	private static final double OBL_DEFAULT_ALPHA = 63.4;
	private static final int PERSP_DEFAULT_D = 1;

	private static GLAutoDrawable glDraw;
	
	// Options checkboxes
	private static JCheckBox aabbCheckbox;
	private static JCheckBox wireframeCheckbox;
	private static JCheckBox textureCheckbox;
	private static JCheckBox controlPointsCheckbox;

	// Projections checkboxes
	private static JCheckBox viewObliqueCheckBox;
	private static JCheckBox viewAxonometricCheckBox;
	private static JCheckBox viewPerspectiveCheckBox;
	
	private static JButton resetButton;
	private static JFileChooser fileChooser;

	// Projections
	private static ObliqueProjection oblProj;
	private static PerspectiveProjection persProj;
	private static AxonometricProjection axonProj;

	private static File imageFile;
	private static JFrame frame;
	private GLUT glut;
	private double aspect;
	
	private static JSlider currentSlider;
	private static boolean activeControlPointsMesh;
	private static boolean activeWireframe;
	private static boolean activeAABB;
	private static boolean activeTexture;

	private Jar3DModel jarModel;
	private static Jar3DTexture jarTexture;
	
	private static JSlider axonGammaSlider;
	private static JSlider axonThetaSlider;
	private static JSlider oblAlphaSlider;
	private static JSlider oblLSlider;
	private static JSlider perspDSlider;
	
	private int currentWindowWidth;
	private int currentWindowHeight;

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void init(GLAutoDrawable gLDrawable) {
		glDraw = gLDrawable;
		GL gl = gLDrawable.getGL();
		glut = new GLUT();
		
		// Instantiation of the 3D Model and Texture of the jar
		jarModel = new Jar3DModel();
		jarTexture = new Jar3DTexture();
		
		// Instantiation of the Texture File Chooser
		fileChooser = new JFileChooser(".");
		fileChooser.setDialogTitle("Select a texture .x24 image file");
		fileChooser.setFileFilter(new ExtensionFileFilter());

		// Instantiation of the projections with the default values
		oblProj = new ObliqueProjection(OBL_DEFAULT_ALPHA, OBL_DEFAULT_L);
		persProj = new PerspectiveProjection();
		axonProj = new AxonometricProjection(AXON_DEFAULT_A, AXON_DEFAULT_B);
		
		// Try to load the default texture
		openFile(frame, true);
		
		// Background color will be black
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Enable multiple GL capabilities
		gl.glEnable(GL.GL_MAP2_VERTEX_3);
		gl.glEnable(GL.GL_MAP2_TEXTURE_COORD_2);
		gl.glEnable(GL.GL_TEXTURE_2D);

		// Z-Buffer
		gl.glDepthFunc(GL.GL_LESS);
		gl.glEnable(GL.GL_DEPTH_TEST);

		// Set texture parameters
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);
		
		// Add the key listener to all the sliders and to the glDraw
		axonGammaSlider.addKeyListener(this);
		axonThetaSlider.addKeyListener(this);
		oblAlphaSlider.addKeyListener(this);
		oblLSlider.addKeyListener(this);
		perspDSlider.addKeyListener(this);
		
		glDraw.addKeyListener(this);
	}

	/**
	 * Loads into memory a texture
	 * @param frame parent frame
	 * @param defaultMode if it will load the default texture
	 */
	private static void openFile(JFrame frame, boolean defaultMode) {
		try {
			if (defaultMode) {
				imageFile = new File(DEFAULT_IMAGE);
			} else {
				int val = fileChooser.showOpenDialog(frame);
				if (val == JFileChooser.APPROVE_OPTION)
					imageFile = fileChooser.getSelectedFile();
			}

		
			InputStream is;
			is = new FileInputStream(imageFile);
			// This buffer will contain the texture file bytes
			byte[] fileBytes = new byte[(int) imageFile.length()];
			is.read(fileBytes);
			
			// Change the texture image buffer to be the new loaded buffer
			jarTexture.setImageBuffer(ByteBuffer.wrap(fileBytes));

			textureCheckbox.setEnabled(true);
			textureCheckbox.setSelected(true);
			activeTexture = true;
			
			// Redraw the object after loading a new texture
			redraw();
		} catch (FileNotFoundException e) {
			if (defaultMode) // if the default texture was not found prompt the user to select another file
				openFile(frame, false);
			else {
				JOptionPane.showMessageDialog(null,
						"File " + imageFile.getAbsolutePath() + "\nnot found.");
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"There was an error while reading the texture. Try again");
		}
	}

	/**
	 * Updates the bottom right viewport
	 * @param gl gl object to work on
	 */
	private void updateBottomRightView(GL gl) {
		if (viewAxonometricCheckBox.isSelected()) {
			gl.glRotated(axonProj.getGamma(), 1, 0, 0);
			gl.glRotated(axonProj.getTheta(), 0, 1, 0);
		} else if (viewObliqueCheckBox.isSelected()) {
			gl.glMultMatrixd(oblProj.getMatrix(), 0);
		} else if (viewPerspectiveCheckBox.isSelected()) {
			gl.glMultMatrixd(persProj.getMatrix(), 0);
		}
	}

	/**
	 * Updates a viewport
	 * @param gl gl object to work on
	 * @param view view to update
	 */
	private void updateView(GL gl, view view) {
		switch (view) {
		case FRONT_VIEW:
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			break;
		case TOP_VIEW:
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glRotated(90, 1, 0, 0);
			break;
		case LEFT_VIEW:
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glRotated(-90, 0, 1, 0);
			break;
		case PROJECTION:
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			updateBottomRightView(gl);
			break;
		}
		// Save the current matrix
		gl.glPushMatrix();

		// Disable the texture so the other objects wont have the texture color
		gl.glDisable(GL.GL_TEXTURE_2D);

		// Draw the frame
		if (activeWireframe) {
			// White
			gl.glColor3d(1.0, 1.0, 1.0);
			drawWireFrame(gl);
		}
	
		// Draw the Axis Aligned Bounding Box
		if (activeAABB) {
			// Orange
			gl.glColor3d(1.0, 0.647059, 0.00);
			drawAxisAlignedBoundingBox(gl);
		}
		
		// Draw the Control Points
		if (activeControlPointsMesh) {
			// Green
			gl.glColor3d(0.0, 1.0, 0.0);
			drawControlPointsMesh(gl);
		}
		
		// Draw the texture
		if (activeTexture) {
			gl.glEnable(GL.GL_TEXTURE_2D);
			drawTexture(gl);
			gl.glDisable(GL.GL_TEXTURE_2D);
		}

		// Restore the saved matrix
		gl.glPopMatrix();
		
		gl.glFlush();

	}

	/**
	 * Draws the texture
	 * @param gl gl object to work on
	 */
	private void drawTexture(GL gl) {
		// Sets which buffer to use when drawing the texture
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, Jar3DTexture.TEXTURE_WIDTH, Jar3DTexture.TEXTURE_HEIGHT, 0, GL.GL_RGB,
				GL.GL_UNSIGNED_BYTE, jarTexture.getImageBuffer());
		
		// Draws the texture in the four bottom patches
		for (int i = 0; i < 2; i++) {
			// Bottom left patch
			gl.glMap2d(GL.GL_MAP2_VERTEX_3, 0.0, 1.0, 3, 4, 0.0, 1.0, 12, 4,
					jarModel.getBottomPoints(), 0);
			gl.glMap2d(GL.GL_MAP2_TEXTURE_COORD_2, 0, 1, 2, 2, 0, 1, 4, 2,
					jarTexture.getTextureBottomLeftPoints(), 0);
			gl.glMapGrid2f(16, 0.0f, 1.0f, 16, 0.0f, 1.0f);
			gl.glEvalMesh2(GL.GL_FILL, 0, 16, 0, 16);
			
			// Bottom right patch
			gl.glRotated(90.0, 0.0, 1.0, 0.0);
			gl.glMap2d(GL.GL_MAP2_VERTEX_3, 0.0, 1.0, 3, 4, 0.0, 1.0, 12, 4,
					jarModel.getBottomPoints(), 0);
			gl.glMap2d(GL.GL_MAP2_TEXTURE_COORD_2, 0, 1, 2, 2, 0, 1, 4, 2,
					jarTexture.getTextureBottomRightPoints(), 0); 
			gl.glMapGrid2f(16, 0.0f, 1.0f, 16, 0.0f, 1.0f);
			gl.glEvalMesh2(GL.GL_FILL, 0, 16, 0, 16);

			gl.glRotated(90.0, 0.0, 1.0, 0.0);

		}

		// Draws the texture in the four top patches
		for (int i = 0; i < 2; i++) {
			// Top left patch
			gl.glMap2d(GL.GL_MAP2_VERTEX_3, 0.0, 1.0, 3, 4, 0.0, 1.0, 12, 4,
					jarModel.getTopPoints(), 0);
			gl.glMap2d(GL.GL_MAP2_TEXTURE_COORD_2, 0, 1, 2, 2, 0, 1, 4, 2,
					jarTexture.getTextureTopLeftPoints(), 0);
			gl.glMapGrid2f(20, 0.0f, 1.0f, 20, 0.0f, 1.0f);
			gl.glEvalMesh2(GL.GL_FILL, 0, 20, 0, 20);

			// Top right patch
			gl.glRotated(90.0, 0.0, 1.0, 0.0);
			gl.glMap2d(GL.GL_MAP2_VERTEX_3, 0.0, 1.0, 3, 4, 0.0, 1.0, 12, 4,
					jarModel.getTopPoints(), 0);
			gl.glMap2d(GL.GL_MAP2_TEXTURE_COORD_2, 0, 1, 2, 2, 0, 1, 4, 2,
					jarTexture.getTextureTopRightPoints(), 0);
			gl.glMapGrid2f(20, 0.0f, 1.0f, 20, 0.0f, 1.0f);
			gl.glEvalMesh2(GL.GL_FILL, 0, 20, 0, 20);

			gl.glRotated(90.0, 0.0, 1.0, 0.0);

		}
	}

	/**
	 * Draws the Jar control points mesh
	 * @param gl gl object to work on
	 */
	private void drawControlPointsMesh(GL gl) {
		// Repeat the drawing to the four patches
		for (int k = 0; k < 4; k++) {
			// Draw the four vertical "rings"
			for (int i = 0; i < jarModel.getBottomPoints().length; i = i + 12) {
				gl.glBegin(GL.GL_LINE_STRIP);
				// Bottom "rings"
				for (int j = 0; j < 12; j = j + 3)
					gl.glVertex3d(jarModel.getBottomPoints()[i + j],
							jarModel.getBottomPoints()[(i + j) + 1],
							jarModel.getBottomPoints()[(i + j) + 2]);
				gl.glEnd();

				// Top "rings"
				gl.glBegin(GL.GL_LINE_STRIP);
				for (int j = 0; j < 12; j = j + 3)
					gl.glVertex3d(jarModel.getTopPoints()[i + j],
							jarModel.getTopPoints()[(i + j) + 1],
							jarModel.getTopPoints()[(i + j) + 2]);
				gl.glEnd();
			}

			int n = 0;
			while (n < 9) { // 3 lines on each patch
				gl.glBegin(GL.GL_LINE_STRIP);
				// Draw the bottom lines
				for (int j = 3; j < jarModel.getBottomPoints().length; j = j + 12)
					gl.glVertex3d(jarModel.getBottomPoints()[j + n],
							jarModel.getBottomPoints()[j + n + 1],
							jarModel.getBottomPoints()[j + n + 2]);
				gl.glEnd();

				// Draw the top lines
				gl.glBegin(GL.GL_LINE_STRIP);
				for (int j = 3; j < jarModel.getTopPoints().length; j = j + 12)
					gl.glVertex3d(jarModel.getTopPoints()[j + n],
							jarModel.getTopPoints()[j + n + 1],
							jarModel.getTopPoints()[j + n + 2]);
				gl.glEnd();

				n += 3; // each point has 3 coordinates
			}
			gl.glRotated(90, 0.0, 1.0, 0.0);
		}
	}

	/**
	 * Draws the Jar wireframe
	 * @param gl gl object to work on
	 */
	private void drawWireFrame(GL gl) {
		// Repeat to the four patches
		for (int i = 0; i < 4; i++) {
			// Bottom patch
			gl.glMap2d(GL.GL_MAP2_VERTEX_3, 0.0, 1.0, 3, 4, 0.0, 1.0, 12, 4,
					jarModel.getBottomPoints(), 0);
			gl.glMapGrid2f(16, 0.0f, 1.0f, 16, 0.0f, 1.0f);
			gl.glEvalMesh2(GL.GL_LINE, 0, 16, 0, 16);

			// Top patch
			gl.glMap2d(GL.GL_MAP2_VERTEX_3, 0.0, 1.0, 3, 4, 0.0, 1.0, 12, 4,
					jarModel.getTopPoints(), 0);
			gl.glMapGrid2f(16, 0.0f, 1.0f, 16, 0.0f, 1.0f);
			gl.glEvalMesh2(GL.GL_LINE, 0, 16, 0, 16);

			gl.glRotated(90.0, 0.0, 1.0, 0.0);
		}
	}

	/**
	 * Draws the Jar axis aligned boundign box
	 * @param gl gl object to work on
	 */
	private void drawAxisAlignedBoundingBox(GL gl) {
		// Front, left, back and right lines
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3d(-(Jar3DModel.MAX_WIDTH/2), 0, (Jar3DModel.MAX_WIDTH/2));
		gl.glVertex3d((Jar3DModel.MAX_WIDTH/2), 0, (Jar3DModel.MAX_WIDTH/2));
		gl.glVertex3d((Jar3DModel.MAX_WIDTH/2), 0, -(Jar3DModel.MAX_WIDTH/2));
		gl.glVertex3d(-(Jar3DModel.MAX_WIDTH/2), 0, -(Jar3DModel.MAX_WIDTH/2));
		gl.glEnd();

		// Front, top, back and bottom lines
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3d(0, -(Jar3DModel.MAX_WIDTH/2), (Jar3DModel.MAX_WIDTH/2));
		gl.glVertex3d(0, (Jar3DModel.MAX_WIDTH/2), (Jar3DModel.MAX_WIDTH/2));
		gl.glVertex3d(0, (Jar3DModel.MAX_WIDTH/2), -(Jar3DModel.MAX_WIDTH/2));
		gl.glVertex3d(0, -(Jar3DModel.MAX_WIDTH/2), -(Jar3DModel.MAX_WIDTH/2));
		gl.glEnd();

		// Top, right, bottom and left lines
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3d(-(Jar3DModel.MAX_WIDTH/2), -(Jar3DModel.MAX_WIDTH/2), 0);
		gl.glVertex3d(-(Jar3DModel.MAX_WIDTH/2), (Jar3DModel.MAX_WIDTH/2), 0);
		gl.glVertex3d((Jar3DModel.MAX_WIDTH/2), (Jar3DModel.MAX_WIDTH/2), 0);
		gl.glVertex3d((Jar3DModel.MAX_WIDTH/2), -(Jar3DModel.MAX_WIDTH/2), 0);
		gl.glEnd();

		// Axis lines
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(0, -(Jar3DModel.MAX_WIDTH/2), 0);
		gl.glVertex3d(0, (Jar3DModel.MAX_WIDTH/2), 0);
		gl.glVertex3d(-(Jar3DModel.MAX_WIDTH/2), 0, 0);
		gl.glVertex3d((Jar3DModel.MAX_WIDTH/2), 0, 0);
		gl.glEnd();

		// Bounding box
		glut.glutWireCube(0.95f);
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	public void display(GLAutoDrawable gLDrawable) {
		GL gl = gLDrawable.getGL();
		// Clear the color buffer and depth buffer
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();

		// Avoid distortion on window resize
		if (aspect > 1)
			gl.glOrtho(-1.0 * aspect, 1.0 * aspect, -1.0, 1.0, -1.0, 1.0);
		else
			gl.glOrtho(-1.0, 1.0, -1.0 / aspect, 1.0 / aspect, -1.0, 1.0);

		
		// Set the viewports
		// TOP VIEW
		gl.glViewport(0, 0, currentWindowWidth / 2, currentWindowHeight / 2);
		updateView(gl, view.TOP_VIEW);

		// FRONT VIEW
		gl.glViewport(0, currentWindowHeight / 2, currentWindowWidth / 2,
				currentWindowHeight / 2);
		updateView(gl, view.FRONT_VIEW);
		
		// LEFT VIEW
		gl.glViewport(currentWindowWidth / 2, currentWindowHeight / 2,
				currentWindowWidth / 2, currentWindowHeight / 2);
		updateView(gl, view.LEFT_VIEW);
		
		// PROJECTION VIEW
		gl.glViewport(currentWindowWidth / 2, 0, currentWindowWidth / 2,
				currentWindowHeight / 2); // PERSPECTIVE
		updateView(gl, view.PROJECTION);
		
		gl.glFlush();

	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width,
			int height) {
		GL gl = gLDrawable.getGL();
		
		// Change the width and height to be the new window dimension
		this.currentWindowWidth = width;
		this.currentWindowHeight = height;
		
		// Change the current aspect
		aspect = (double) width / (double) height;
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();

		// Update viewports
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glViewport(0, 0, width / 2, height / 2);
		gl.glViewport(width / 2, 0, width / 2, height / 2);
		gl.glViewport(0, height / 2, width / 2, height / 2);
		gl.glViewport(width / 2, height / 2, width / 2, height / 2);
	}

	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	public static void redraw() {
		glDraw.display();
	}

	/**
	 * Creathes the right panel
	 * @return created panel
	 */
	public static JPanel createPanel() {
		JPanel controlPanel = createControlPanel();
		JPanel aboutPanel = createAboutPanel();
		
		// Panel to return
		JPanel panel = new JPanel();
		
		// Set the panel layout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		panel.setLayout(layout);

		panel.add(controlPanel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		panel.add(createCheckBoxesControlPanel(), c);
		
		c.gridx = 0;
		c.gridy = 2;
		panel.add(aboutPanel, c);
		return panel;
	}

	/**
	 * Creates the axonometric tab content
	 * @return axonometric tab
	 */
	protected static JComponent axonometricTab() {
		JPanel slidersPanel = new JPanel();
		
		final JLabel gammaLabel = new JLabel("gamma=" + AXON_DEFAULT_GAMMA
				+ "�");
		final JLabel thetaLabel = new JLabel("theta=" + AXON_DEFAULT_THETA
				+ "�");

		axonGammaSlider = new JSlider(JSlider.VERTICAL, 0, 360,
				AXON_DEFAULT_GAMMA);	
		axonGammaSlider.setMajorTickSpacing(30);
		axonGammaSlider.setMinorTickSpacing(5);
		axonGammaSlider.setPaintTicks(true);
		axonGammaSlider.setPaintLabels(true);
		
		axonGammaSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int selectedAngle = axonGammaSlider.getValue();
				axonProj.setGamma(selectedAngle);
				String gammaLabelValue = "gamma=" + selectedAngle + "�";
				gammaLabel.setText(gammaLabelValue);
				if (viewAxonometricCheckBox.isSelected()) {
					// Change the current slider when the checkbox is selected
					currentSlider = axonGammaSlider;
					redraw();
				}
			}
		});

		// Set the default current slider to be the gamma slider
		currentSlider = axonGammaSlider;

		axonThetaSlider = new JSlider(JSlider.VERTICAL, 0, 360,
				AXON_DEFAULT_THETA);
		axonThetaSlider.setMajorTickSpacing(30);
		axonThetaSlider.setMinorTickSpacing(5);
		axonThetaSlider.setPaintTicks(true);
		axonThetaSlider.setPaintLabels(true);
		axonThetaSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent arg0) {
				int selectedAngle = axonThetaSlider.getValue();
				axonProj.setTheta(selectedAngle);
				String thetaLabelValue = "theta=" + selectedAngle + "�";
				thetaLabel.setText(thetaLabelValue);
				if (viewAxonometricCheckBox.isSelected()) {
					// Change the current slider when the checkbox is selected
					currentSlider = axonThetaSlider;
					redraw();
				}
			}

		});

		viewAxonometricCheckBox = new JCheckBox("Enabled");
		// Default selected checkbox
		viewAxonometricCheckBox.setSelected(true);
		viewAxonometricCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Unselect all the other checkboxes
				if (viewAxonometricCheckBox.isSelected()) {
					viewObliqueCheckBox.setSelected(false);
					viewPerspectiveCheckBox.setSelected(false);
					currentSlider = axonGammaSlider;
					redraw();
				}
			}
		});

		// Create another panel to contain all the components that are not sliders
		JPanel otherComponentsPanel = new JPanel();
		GridLayout layout = new GridLayout(2, 0);
		otherComponentsPanel.setLayout(layout);
		layout.setHgap(5);

		resetButton = new JButton("Reset");

		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Reset the projection default values
				if (viewAxonometricCheckBox.isSelected()) {
					axonGammaSlider.setValue(AXON_DEFAULT_GAMMA);
					axonThetaSlider.setValue(AXON_DEFAULT_THETA);
					axonProj.setAB(AXON_DEFAULT_A, AXON_DEFAULT_B);
					redraw();
				}
			}
		});

		otherComponentsPanel.add(gammaLabel);
		otherComponentsPanel.add(thetaLabel);
		otherComponentsPanel.add(viewAxonometricCheckBox);
		otherComponentsPanel.add(resetButton);
		
		slidersPanel.add(axonGammaSlider);
		slidersPanel.add(axonThetaSlider);
		slidersPanel.add(otherComponentsPanel);
		
		return slidersPanel;
	}

	/**
	 * Creates the oblique tab content
	 * @return oblique tab
	 */
	protected static JComponent obliqueTab() {
		JPanel slidersPanel = new JPanel();
		
		final JLabel alphaLabel = new JLabel("alpha=" + (int) OBL_DEFAULT_ALPHA
				+ "�");
		final JLabel lLabel = new JLabel("l=" + OBL_DEFAULT_L);
		
		oblAlphaSlider = new JSlider(JSlider.VERTICAL, 0, 360,
				(int) OBL_DEFAULT_ALPHA);
		oblAlphaSlider.setMajorTickSpacing(30);
		oblAlphaSlider.setMinorTickSpacing(5);


		oblAlphaSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent arg0) {
				int selectedAngle = oblAlphaSlider.getValue();
				oblProj.setAlpha(selectedAngle);
				String alphaLabelValue = "alpha=";
				alphaLabelValue += selectedAngle + "�";
				alphaLabel.setText(alphaLabelValue);
				if (viewObliqueCheckBox.isSelected()) {
					currentSlider = oblAlphaSlider;
					redraw();
				}
			}
		});
		oblAlphaSlider.setPaintTicks(true);
		oblAlphaSlider.setPaintLabels(true);

		oblLSlider = new JSlider(JSlider.VERTICAL, 0, 100,
				(int) (OBL_DEFAULT_L * 100));
		oblLSlider.setMajorTickSpacing(10);
		oblLSlider.setMinorTickSpacing(5);
		oblLSlider.setPaintTicks(true);
		oblLSlider.setPaintLabels(true);
		oblLSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent arg0) {
				double selectedAngle = oblLSlider.getValue();
				oblProj.setL((double) selectedAngle / 100);
				String lLabelValue = "l=" + (double) selectedAngle / 100;
				lLabel.setText(lLabelValue);
				if (viewObliqueCheckBox.isSelected()) {
					currentSlider = oblLSlider;
					redraw();
				}
			}
		});

		viewObliqueCheckBox = new JCheckBox("Enabled");
		viewObliqueCheckBox.setSelected(false);
		viewObliqueCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Unselect all the other checkboxes
				if (viewObliqueCheckBox.isSelected()) {
					viewAxonometricCheckBox.setSelected(false);
					viewPerspectiveCheckBox.setSelected(false);
					currentSlider = oblAlphaSlider;
					redraw();
				}
			}
		});

		// Create another panel to contain all the components that are not sliders
		JPanel otherComponentsPanel = new JPanel();
		GridLayout layout = new GridLayout(2, 0);
		otherComponentsPanel.setLayout(layout);
		layout.setHgap(5);

		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Reset the projection default values
				if (viewObliqueCheckBox.isSelected()) {
					oblAlphaSlider.setValue((int) OBL_DEFAULT_ALPHA);
					oblLSlider.setValue((int) (OBL_DEFAULT_L * 100));
					oblProj.setAlpha(OBL_DEFAULT_ALPHA);
					oblProj.setL(OBL_DEFAULT_L);
					redraw();
				}
			}
		});

		otherComponentsPanel.add(alphaLabel);
		otherComponentsPanel.add(lLabel);
		otherComponentsPanel.add(viewObliqueCheckBox);
		otherComponentsPanel.add(resetButton);
		
		slidersPanel.add(oblAlphaSlider);
		slidersPanel.add(oblLSlider);
		slidersPanel.add(otherComponentsPanel);

		return slidersPanel;
	}

	/**
	 * Creates the perspective tab content
	 * @return perspective tab
	 */
	protected static JComponent perspectiveTab() {
		JPanel slidersPanel = new JPanel();
		final JLabel dLabel = new JLabel("d=" + PERSP_DEFAULT_D);
		// Empty label just to layout
		final JLabel emptyLabel = new JLabel(" ");
		
		perspDSlider = new JSlider(JSlider.VERTICAL, 1, 100,
				PERSP_DEFAULT_D);
		perspDSlider.setMajorTickSpacing(9);
		perspDSlider.setMinorTickSpacing(5);
		perspDSlider.setPaintTicks(true);
		perspDSlider.setPaintLabels(true);
		perspDSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent arg0) {
				int selectedAngle = perspDSlider.getValue();
				persProj.setD(selectedAngle);
				String dLabelValue = "d=";
				dLabelValue += selectedAngle;
				dLabel.setText(dLabelValue);
				if (viewPerspectiveCheckBox.isSelected()) {
					currentSlider = perspDSlider;
					redraw();
				}
			}

		});

		viewPerspectiveCheckBox = new JCheckBox("Enabled");
		viewPerspectiveCheckBox.setSelected(false);
		viewPerspectiveCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Unselect all the other checkboxes
				if (viewPerspectiveCheckBox.isSelected()) {
					viewAxonometricCheckBox.setSelected(false);
					viewObliqueCheckBox.setSelected(false);
					currentSlider = perspDSlider;
					redraw();
				}
			}
		});
		
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Reset the projection default values
				if (viewPerspectiveCheckBox.isSelected()) {
					perspDSlider.setValue(PERSP_DEFAULT_D);
					persProj.setD(PERSP_DEFAULT_D);
					redraw();
				}
			}
		});
		
		// Create another panel to contain all the components that are not sliders
		JPanel otherComponentsPanel = new JPanel();
		GridLayout layout = new GridLayout(2, 0);
		otherComponentsPanel.setLayout(layout);
		layout.setHgap(5);

		otherComponentsPanel.add(dLabel);
		otherComponentsPanel.add(emptyLabel);
		otherComponentsPanel.add(viewPerspectiveCheckBox);
		otherComponentsPanel.add(resetButton);
		slidersPanel.add(perspDSlider);
		slidersPanel.add(otherComponentsPanel);

		return slidersPanel;
	}

	/**
	 * Creates the bottom control panel
	 * @return bottom control panel
	 */
	public static JPanel createBottomControlPanel() {
		JPanel checkBoxesPanel = createCheckBoxesControlPanel();

		JPanel bottomControlPanel = new JPanel();
		bottomControlPanel.add(checkBoxesPanel);

		return bottomControlPanel;
	}

	/**
	 * Creates the control panel
	 * @return control panel
	 */
	public static JPanel createControlPanel() {
		JPanel tabbedPanePanel = createTabbedPanePanel();

		JPanel controlPanel = new JPanel();
		controlPanel.add(tabbedPanePanel);

		return controlPanel;
	}

	/**
	 * Creates the tabbed pane panel
	 * @return tabbed pane panel
	 */
	public static JPanel createTabbedPanePanel() {
		JTabbedPane tabbedPane = new JTabbedPane();

		// Projections tabs
		JComponent obliqueTab = obliqueTab();
		JComponent axonometricTab = axonometricTab();
		JComponent perspectiveTab = perspectiveTab();

		// Set the preferred size of the tabs
		obliqueTab.setPreferredSize(new Dimension(310, 50));

		tabbedPane.add("Axonometric", axonometricTab);
		tabbedPane.add("Oblique", obliqueTab);
		tabbedPane.add("Perspective", perspectiveTab);
		
		JPanel panel = new JPanel();
		panel.add(tabbedPane);

		return panel;

	}

	/**
	 * Creates the about panel
	 * @return about panel
	 */
	public static JPanel createAboutPanel() {

		class BListener implements ActionListener {

			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(null,
						"OpenGL Jar Projections\nAlexandre Martins Garcia, 34625\nG23 P5");
			}
		}

		BListener listener = new BListener();

		JButton autor = new JButton("About");
		autor.addActionListener(listener);

		JPanel painel = new JPanel();
		painel.add(autor);
		return painel;
	}

	/**
	 * Creates the control panel with checkboxes and open file button
	 * @return control panel with checkboxes and open file button
	 */
	public static JPanel createCheckBoxesControlPanel() {

		class CBListener implements ActionListener {
			
			// Change the value of the boolean control variables to match the respective checkbox
			public void actionPerformed(ActionEvent event) {
				activeControlPointsMesh = controlPointsCheckbox.isSelected();
				activeTexture = textureCheckbox.isSelected();
				activeAABB = aabbCheckbox.isSelected();
				activeWireframe = wireframeCheckbox.isSelected();
				redraw();
			}
		}

		CBListener listener = new CBListener();

		// Create the checkboxes and set its initial state to be selected
		aabbCheckbox = new JCheckBox("AABB");
		aabbCheckbox.setSelected(true);
		aabbCheckbox.addActionListener(listener);

		wireframeCheckbox = new JCheckBox("Wireframe");
		wireframeCheckbox.setSelected(true);
		wireframeCheckbox.addActionListener(listener);

		controlPointsCheckbox = new JCheckBox("Control Points");
		controlPointsCheckbox.setSelected(true);
		controlPointsCheckbox.addActionListener(listener);

		textureCheckbox = new JCheckBox("Texture");
		textureCheckbox.addActionListener(listener);
		textureCheckbox.setEnabled(false); // in the beginning there's no texture

		// Set the value of the boolean control variables
		activeControlPointsMesh = controlPointsCheckbox.isSelected();
		activeTexture = textureCheckbox.isSelected();
		activeAABB = aabbCheckbox.isSelected();
		activeWireframe = wireframeCheckbox.isSelected();

		JButton openFile = new JButton("Open texture");
		openFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Show open file dialog
				openFile(frame, false);
			}

		});
		
		JPanel panel = new JPanel();
		
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		panel.add(aabbCheckbox, c);
		c.gridx = 1;
		c.gridy = 0;
		panel.add(wireframeCheckbox, c);
		c.gridx = 0;
		c.gridy = 1;
		panel.add(textureCheckbox, c);
		c.gridx = 3;
		c.gridy = 0;
		panel.add(controlPointsCheckbox, c);
		c.gridx = 1;
		c.gridy = 1;
		panel.add(openFile, c);
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
		
		return panel;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == '\u001B' || // escape
				e.getKeyChar() == 'Q' || e.getKeyChar() == 'q') { // quit
			System.exit(0);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_PLUS) {
			int n = currentSlider.getValue();
			currentSlider.setValue(n + 1);
			glDraw.display();
		} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
			int n = currentSlider.getValue();
			currentSlider.setValue(n - 1);
		}

	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		frame = new JFrame("OpenGL Jar Projections");
		frame.setSize(new Dimension(800, 600));
		GLCanvas canvas = new GLCanvas();
		canvas.addGLEventListener(new Jar3DModelProjectionsGUI());
		canvas.setSize(400, 280);
		frame.add(canvas, BorderLayout.CENTER);
		JPanel panel = createPanel();
		
		// Panel will be located at right side
		frame.add(panel, BorderLayout.EAST);
		frame.pack();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		canvas.requestFocusInWindow();
		frame.setVisible(true);
	}
}
