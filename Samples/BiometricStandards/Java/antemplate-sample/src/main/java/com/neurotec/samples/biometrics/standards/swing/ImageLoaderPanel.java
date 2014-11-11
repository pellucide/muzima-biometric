package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.neurotec.biometrics.standards.ANASCIIBinaryRecord;
import com.neurotec.biometrics.standards.ANImageColorSpace;
import com.neurotec.biometrics.standards.ANImageCompressionAlgorithm;
import com.neurotec.biometrics.standards.BDIFScaleUnits;
import com.neurotec.images.NImage;
import com.neurotec.images.NImages;
import com.neurotec.samples.biometrics.standards.ANTemplateSettings;
import com.neurotec.samples.util.Utils;

public final class ImageLoaderPanel extends JPanel implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private fields
	// ==============================================

	private boolean hasBpx;
	private boolean hasColorspace;
	private GridBagConstraints c;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JComboBox cmbScaleUnits;
	private JComboBox cmbCompressionAlgorithm;
	private JComboBox cmbColorSpace;
	private JSpinner spinnerHLineLength;
	private JSpinner spinnerVLineLength;
	private JSpinner spinnerHPixelScale;
	private JSpinner spinnerVPixelScale;
	private JSpinner spinnerBitsPerPixel;

	private JRadioButton radioFromImage;
	private JRadioButton radioFromData;
	private JPanel fromImagePanel;
	private JPanel fromDataPanel;

	private JTextField txtSource;
	private JTextField txtImagePath;
	private JTextField txtImageDataPath;

	private JButton btnBrowseImage;
	private JButton btnBrowseData;
	private JLabel lblBpx;
	private JLabel lblColorSpace;

	private JFileChooser imageOpenFileDialog = new JFileChooser();
	private JFileChooser imageDataOpenFileDialog = new JFileChooser();

	private JLabel lblHLineLength;
	private JLabel lblVLineLength;
	private JLabel lblHPixelScale;
	private JLabel lblVPixelScale;
	private JLabel lblImageDataPath;

	// ==============================================
	// Public constructor
	// ==============================================

	public ImageLoaderPanel() {
		setPreferredSize(new Dimension(280, 390));
		initializeComponents();
		loadInitialSettings();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void loadInitialSettings() {
		radioFromImage.setSelected(true);

		for (BDIFScaleUnits value : BDIFScaleUnits.values()) {
			cmbScaleUnits.addItem(value);
		}
		cmbScaleUnits.setSelectedIndex(0);

		for (ANImageCompressionAlgorithm value : ANImageCompressionAlgorithm.values()) {
			cmbCompressionAlgorithm.addItem(value);
		}
		cmbCompressionAlgorithm.setSelectedIndex(0);

		for (ANImageColorSpace value : ANImageColorSpace.values()) {
			cmbColorSpace.addItem(value);
		}
		cmbColorSpace.setSelectedIndex(0);

		spinnerHLineLength.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		spinnerVLineLength.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		spinnerHPixelScale.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		spinnerVPixelScale.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		spinnerBitsPerPixel.setModel(new SpinnerNumberModel(0, 0, 255, 1));

		enableFromImagePanel(radioFromImage.isSelected());
		enableFromDataPanel(radioFromData.isSelected());
	}

	private void initializeComponents() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel topPanel = createTopPanel();

		radioFromImage = new JRadioButton("From image:");
		radioFromImage.addActionListener(this);
		radioFromData = new JRadioButton("From data");
		radioFromData.addActionListener(this);

		ButtonGroup group = new ButtonGroup();
		group.add(radioFromImage);
		group.add(radioFromData);

		createFromImagePanel();
		createFromDataPanel();

		add(topPanel);
		topPanel.setAlignmentX(LEFT_ALIGNMENT);
		add(Box.createVerticalStrut(3));
		add(radioFromImage);
		radioFromImage.setAlignmentX(LEFT_ALIGNMENT);
		add(fromImagePanel);
		fromImagePanel.setAlignmentX(LEFT_ALIGNMENT);
		add(Box.createVerticalStrut(15));
		add(radioFromData);
		radioFromData.setAlignmentX(LEFT_ALIGNMENT);
		add(fromDataPanel);
		fromDataPanel.setAlignmentX(LEFT_ALIGNMENT);

	}

	private JPanel createTopPanel() {

		JPanel topPanel = new JPanel();
		GridLayout topPanelLayout = new GridLayout(3, 2);
		topPanelLayout.setHgap(4);
		topPanelLayout.setVgap(4);
		topPanel.setLayout(topPanelLayout);

		txtSource = new JTextField();
		txtSource.setInputVerifier(new SourceInputVerifier());

		cmbScaleUnits = new JComboBox();
		cmbCompressionAlgorithm = new JComboBox();

		topPanel.add(new JLabel("Source agency:"));
		topPanel.add(txtSource);
		topPanel.add(new JLabel("Scale units:"));
		topPanel.add(cmbScaleUnits);
		topPanel.add(new JLabel("Compression algorithm:"));
		topPanel.add(cmbCompressionAlgorithm);
		return topPanel;
	}

	private void createFromImagePanel() {
		fromImagePanel = new JPanel();
		fromImagePanel.setPreferredSize(new Dimension(280, 25));
		fromImagePanel.setMaximumSize(new Dimension(280, 25));
		fromImagePanel.setLayout(new BoxLayout(fromImagePanel, BoxLayout.X_AXIS));

		txtImagePath = new JTextField();
		btnBrowseImage = new JButton("Browse...");
		btnBrowseImage.addActionListener(this);

		fromImagePanel.add(txtImagePath);
		fromImagePanel.add(Box.createHorizontalStrut(3));
		fromImagePanel.add(btnBrowseImage);
	}

	private void createFromDataPanel() {
		fromDataPanel = new JPanel();
		GridBagLayout fromDataLayout = new GridBagLayout();
		fromDataLayout.columnWidths = new int[] { 130, 100, 75 };
		fromDataPanel.setLayout(fromDataLayout);

		spinnerHLineLength = new JSpinner();
		spinnerVLineLength = new JSpinner();
		spinnerHPixelScale = new JSpinner();
		spinnerVPixelScale = new JSpinner();
		spinnerBitsPerPixel = new JSpinner();

		lblHLineLength = new JLabel("Horizontal line length:");
		lblVLineLength = new JLabel("Vertical line length:");
		lblHPixelScale = new JLabel("Horizontal pixel scale:");
		lblVPixelScale = new JLabel("Vertical pixel scale:");

		lblBpx = new JLabel("Bits per pixel:");
		lblColorSpace = new JLabel("Colorspace:");

		cmbColorSpace = new JComboBox();
		lblImageDataPath = new JLabel("Image data file:");
		txtImageDataPath = new JTextField();
		btnBrowseData = new JButton("Browse...");
		btnBrowseData.addActionListener(this);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 2, 2, 2);

		addToGridBagLayout(0, 0, fromDataPanel, lblHLineLength);
		addToGridBagLayout(0, 1, fromDataPanel, lblVLineLength);
		addToGridBagLayout(0, 2, fromDataPanel, lblHPixelScale);
		addToGridBagLayout(0, 3, fromDataPanel, lblVPixelScale);
		addToGridBagLayout(0, 4, fromDataPanel, lblBpx);
		addToGridBagLayout(1, 0, 1, 1, 1.0, 0, fromDataPanel, spinnerHLineLength);
		addToGridBagLayout(1, 1, fromDataPanel, spinnerVLineLength);
		addToGridBagLayout(1, 2, fromDataPanel, spinnerHPixelScale);
		addToGridBagLayout(1, 3, fromDataPanel, spinnerVPixelScale);
		addToGridBagLayout(1, 4, fromDataPanel, spinnerBitsPerPixel);
		addToGridBagLayout(0, 5, fromDataPanel, lblColorSpace);
		addToGridBagLayout(1, 5, 2, 1, 0, 0, fromDataPanel, cmbColorSpace);
		addToGridBagLayout(0, 6, 1, 1, fromDataPanel, lblImageDataPath);
		addToGridBagLayout(0, 7, 2, 1, fromDataPanel, txtImageDataPath);
		addToGridBagLayout(2, 7, 1, 1, fromDataPanel, btnBrowseData);
	}

	private void addToGridBagLayout(int x, int y, JPanel parent, JComponent component) {
		c.gridx = x;
		c.gridy = y;
		parent.add(component, c);
	}

	private void addToGridBagLayout(int x, int y, int gridWidth, int geidHeight, JPanel parent, JComponent component) {
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = gridWidth;
		c.gridheight = geidHeight;
		parent.add(component, c);
	}

	private void addToGridBagLayout(int x, int y, int gridWidth, int geidHeight, double weightx, double weighty, JPanel parent, JComponent component) {
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = gridWidth;
		c.gridheight = geidHeight;
		c.weightx = weightx;
		c.weighty = weighty;
		parent.add(component, c);
	}

	private void enableFromImagePanel(boolean enabled) {
		txtImagePath.setEnabled(enabled);
		btnBrowseImage.setEnabled(enabled);
	}

	private void enableFromDataPanel(boolean enabled) {
		lblHLineLength.setEnabled(enabled);
		spinnerHLineLength.setEnabled(enabled);
		lblVLineLength.setEnabled(enabled);
		spinnerVLineLength.setEnabled(enabled);
		lblHPixelScale.setEnabled(enabled);
		spinnerHPixelScale.setEnabled(enabled);
		lblVPixelScale.setEnabled(enabled);
		spinnerVPixelScale.setEnabled(enabled);
		lblBpx.setEnabled(enabled);
		spinnerBitsPerPixel.setEnabled(enabled);
		lblColorSpace.setEnabled(enabled);
		cmbColorSpace.setEnabled(enabled);
		lblImageDataPath.setEnabled(enabled);
		txtImageDataPath.setEnabled(enabled);
		btnBrowseData.setEnabled(enabled);
	}

	private void browseImage() {
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImages.getOpenFileFilter()));
		ANTemplateSettings settings = ANTemplateSettings.getInstance();
		imageOpenFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
		if (imageOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			settings.setLastDirectory(imageOpenFileDialog.getSelectedFile().getParentFile().getPath());
			txtImagePath.setText(imageOpenFileDialog.getSelectedFile().getPath());
		}
	}

	private void browseImageData() {
		ANTemplateSettings settings = ANTemplateSettings.getInstance();
		imageDataOpenFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
		if (imageDataOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			settings.setLastDirectory(imageDataOpenFileDialog.getSelectedFile().getParentFile().getPath());
			txtImageDataPath.setText(imageDataOpenFileDialog.getSelectedFile().getPath());
		}
	}

	private void radioFromDataStatusChanged() {
		boolean fromDataSelected = radioFromData.isSelected();
		enableFromDataPanel(fromDataSelected);
		lblBpx.setEnabled(fromDataSelected && hasBpx);
		spinnerBitsPerPixel.setEnabled(fromDataSelected && hasBpx);
		lblColorSpace.setEnabled(fromDataSelected && hasColorspace);
		cmbColorSpace.setEnabled(fromDataSelected && hasColorspace);
	}

	// ==============================================
	// Public methods
	// ==============================================

	public String getSrc() {
		String source = txtSource.getText();
		if (source == null || source.isEmpty()) {
			JOptionPane.showMessageDialog(ImageLoaderPanel.this, String.format("Source agency field length must be between %s and %s characters", ANASCIIBinaryRecord.MIN_SOURCE_AGENCY_LENGTH, ANASCIIBinaryRecord.MAX_SOURCE_AGENCY_LENGTH_V4));
			return null;
		}
		return source;
	}

	public void setSrc(String value) {
		txtSource.setText(value);
	}

	public BDIFScaleUnits getScaleUnits() {
		return (BDIFScaleUnits) cmbScaleUnits.getSelectedItem();
	}

	public void setScaleUnits(BDIFScaleUnits value) {
		cmbScaleUnits.setSelectedItem(value);
	}

	public ANImageCompressionAlgorithm getCompressionAlgorithm() {
		return (ANImageCompressionAlgorithm) cmbCompressionAlgorithm.getSelectedItem();
	}

	public void setCompressionAlgorithm(ANImageCompressionAlgorithm value) {
		cmbCompressionAlgorithm.setSelectedItem(value);
	}

	public boolean isCreateFromImage() {
		return radioFromImage.isSelected();
	}

	public boolean isCreateFromData() {
		return radioFromData.isSelected();
	}

	public int getHll() {
		return (Integer) spinnerHLineLength.getValue();
	}

	public void setHll(int value) {
		spinnerHLineLength.setValue(value);
	}

	public int getVll() {
		return (Integer) spinnerVLineLength.getValue();
	}

	public void setVll(int value) {
		spinnerVLineLength.setValue(value);
	}

	public int getHps() {
		return (Integer) spinnerHPixelScale.getValue();
	}

	public void setHps(int value) {
		spinnerHPixelScale.setValue(value);
	}

	public int getVps() {
		return (Integer) spinnerVPixelScale.getValue();
	}

	public void setVps(int value) {
		spinnerVPixelScale.setValue(value);
	}

	public boolean hasBpx() {
		return hasBpx;
	}

	public void setHasBpx(boolean value) {
		hasBpx = value;
	}

	public int getBpx() {
		return (Integer) spinnerBitsPerPixel.getValue();
	}

	public void setBpx(int value) {
		spinnerBitsPerPixel.setValue(value);
	}

	public boolean hasColorSpace() {
		return hasColorspace;
	}

	public void setHasColorSpace(boolean value) {
		hasColorspace = value;
	}

	public ANImageColorSpace getColorSpace() {
		return (ANImageColorSpace) cmbColorSpace.getSelectedItem();
	}

	public void setColorSpace(ANImageColorSpace value) {
		cmbColorSpace.setSelectedItem(value);
	}

	public NImage getImage() {
		try {
			String imagePath = txtImagePath.getText();
			if (imagePath == null || imagePath.isEmpty()) {
				JOptionPane.showMessageDialog(this, "File path can not be empty");
				return null;
			}
			return NImage.fromFile(txtImagePath.getText());
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.toString());
		}
		return null;
	}

	public byte[] getImageData() {
		String dataPath = txtImageDataPath.getText();
		if (dataPath == null || dataPath.isEmpty()) {
			JOptionPane.showMessageDialog(this, "File path can not be empty");
			return null;
		}

		File f = new File(dataPath);
		if (!f.exists()) {
			return null;
		}

		byte[] fileData = new byte[(int) f.length()];
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(f));
			dis.readFully(fileData);
			return fileData;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.toString());
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnBrowseImage) {
			browseImage();
		} else if (source == btnBrowseData) {
			browseImageData();
		} else if (source == radioFromImage || source == radioFromData) {
			enableFromImagePanel(radioFromImage.isSelected());
			radioFromDataStatusChanged();
		}
	}

	// ============================================================================
	// Private class extending InputVerifier to verify source agency field
	// ============================================================================

	private final class SourceInputVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			String text = txtSource.getText();
			if (text == null || text.isEmpty() || text.length() < ANASCIIBinaryRecord.MIN_SOURCE_AGENCY_LENGTH || text.length() > ANASCIIBinaryRecord.MAX_SOURCE_AGENCY_LENGTH_V4) {
				JOptionPane.showMessageDialog(ImageLoaderPanel.this, String.format("Source agency field length must be between %s and %s characters", ANASCIIBinaryRecord.MIN_SOURCE_AGENCY_LENGTH, ANASCIIBinaryRecord.MAX_SOURCE_AGENCY_LENGTH_V4));
				return false;
			}
			return true;
		}

	}

}
