package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.neurotec.biometrics.standards.ANImageCompressionAlgorithm;
import com.neurotec.biometrics.standards.ANType1Record;
import com.neurotec.images.NImage;
import com.neurotec.images.NImages;
import com.neurotec.samples.biometrics.standards.ANTemplateSettings;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;
import com.neurotec.samples.util.Utils;

public class ANImageBinaryRecordCreationFrame extends ANRecordCreationFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private fields
	// ==============================================

	private boolean isLowResolution;
	private ButtonGroup group;
	private GridBagConstraints c;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JComboBox cmbCompressionAlgorithm;
	private JPanel fromImagePanel;
	private JPanel fromDataPanel;
	private JRadioButton radioFromImage;
	private JRadioButton radioFromData;
	private JCheckBox chkResolutionFlag;
	private ResolutionEditBox isrResEditBox;
	private JTextField txtImagePath;
	private JTextField txtHLineLength;
	private JTextField txtVLineLength;
	private ResolutionEditBox irResEditBox;
	private JTextField txtVendorCA;
	private JTextField txtImageDataPath;
	private JButton btnBrowseImage;
	private JButton btnBrowseData;
	private JLabel lblVenderCA;
	private JLabel lblHLineLength;
	private JLabel lblVLineLength;
	private JLabel lblResolution;
	private JLabel lblImageDataPath;
	private JPanel binaryPanel;

	private JFileChooser imageOpenFileDialog = new JFileChooser();

	// ==============================================
	// Public constructor
	// ==============================================

	public ANImageBinaryRecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		setPreferredSize(new Dimension(330, 500));
		setTitle("ANImageBinaryRecordCreationFrame");
		initializeComponents();
		loadInitialSettings();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void loadInitialSettings() {
		radioFromImage.setSelected(true);

		Class<?> compressionFormatsType = getCompressionFormatsType();
		if (compressionFormatsType != null) {
			for (Object value : compressionFormatsType.getEnumConstants()) {
				cmbCompressionAlgorithm.addItem(value);
			}
			if (cmbCompressionAlgorithm.getItemCount() > 0) {
				cmbCompressionAlgorithm.setSelectedIndex(0);
			}
		} else {
			cmbCompressionAlgorithm.setEnabled(false);
		}

		enableFromImagePanel(radioFromImage.isSelected());
		enableFromDataPanel(radioFromData.isSelected());
	}

	private void initializeComponents() {
		binaryPanel = new JPanel();
		binaryPanel.setLayout(new BoxLayout(binaryPanel, BoxLayout.Y_AXIS));

		chkResolutionFlag = new JCheckBox("Image scanning resolution flag");

		isrResEditBox = new ResolutionEditBox();
		isrResEditBox.setInputVerifier(new ImageScanningResolutionVerifier());

		JPanel compressionAlgorithmPanel = createCompressionAlgorithmPanel();

		radioFromImage = new JRadioButton("From image:");
		radioFromImage.addActionListener(this);
		radioFromData = new JRadioButton("From data");
		radioFromData.addActionListener(this);

		group = new ButtonGroup();
		group.add(radioFromImage);
		group.add(radioFromData);

		createFromImagePanel();

		createFromDataPanel();

		binaryPanel.add(chkResolutionFlag);
		chkResolutionFlag.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lblRsolution = new JLabel("Image scanning resolution:");
		binaryPanel.add(lblRsolution);
		lblRsolution.setAlignmentX(LEFT_ALIGNMENT);
		binaryPanel.add(Box.createVerticalStrut(2));
		binaryPanel.add(isrResEditBox);
		isrResEditBox.setAlignmentX(LEFT_ALIGNMENT);
		binaryPanel.add(Box.createVerticalStrut(4));

		binaryPanel.add(compressionAlgorithmPanel);
		compressionAlgorithmPanel.setAlignmentX(LEFT_ALIGNMENT);
		binaryPanel.add(Box.createVerticalStrut(2));
		binaryPanel.add(radioFromImage);
		radioFromImage.setAlignmentX(LEFT_ALIGNMENT);

		binaryPanel.add(fromImagePanel);
		fromImagePanel.setAlignmentX(LEFT_ALIGNMENT);

		binaryPanel.add(Box.createVerticalStrut(15));
		binaryPanel.add(radioFromData);
		radioFromData.setAlignmentX(LEFT_ALIGNMENT);

		binaryPanel.add(fromDataPanel);
		fromDataPanel.setAlignmentX(LEFT_ALIGNMENT);

		getContentPane().add(binaryPanel);
		binaryPanel.setBounds(10, 65, 300, 330);
		getButtonPanel().setBounds(10, 435, 300, 25);
		pack();
	}

	private JPanel createCompressionAlgorithmPanel() {
		JPanel compressionAlgorithmPanel = new JPanel();
		compressionAlgorithmPanel.setLayout(new BoxLayout(compressionAlgorithmPanel, BoxLayout.X_AXIS));
		compressionAlgorithmPanel.setPreferredSize(new Dimension(300, 25));
		compressionAlgorithmPanel.setMaximumSize(new Dimension(300, 25));
		cmbCompressionAlgorithm = new JComboBox();
		cmbCompressionAlgorithm.addActionListener(this);

		compressionAlgorithmPanel.add(new JLabel("Compression algorithm:"));
		compressionAlgorithmPanel.add(Box.createHorizontalStrut(3));
		compressionAlgorithmPanel.add(cmbCompressionAlgorithm);
		return compressionAlgorithmPanel;
	}

	private void createFromImagePanel() {
		fromImagePanel = new JPanel();
		fromImagePanel.setPreferredSize(new Dimension(300, 25));
		fromImagePanel.setMaximumSize(new Dimension(300, 25));
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
		fromDataLayout.columnWidths = new int[] { 115, 100, 75 };
		fromDataPanel.setLayout(fromDataLayout);

		txtHLineLength = new JTextField("0");
		txtHLineLength.setInputVerifier(new HLineLengthVerifier());
		lblHLineLength = new JLabel("Horizontal line length:");

		txtVLineLength = new JTextField("0");
		txtVLineLength.setInputVerifier(new VLineLengthVerfier());
		lblVLineLength = new JLabel("Vertical line length:");

		irResEditBox = new ResolutionEditBox();
		irResEditBox.setInputVerifier(new NativeScanningResolutionVerifier());
		lblResolution = new JLabel("Image Resolution:");

		lblVenderCA = new JLabel("Vendor CA:");
		txtVendorCA = new JTextField("0");
		txtVendorCA.setInputVerifier(new VenderCAVerfier());

		lblImageDataPath = new JLabel("Image data file:");
		txtImageDataPath = new JTextField();
		btnBrowseData = new JButton("Browse...");
		btnBrowseData.addActionListener(this);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 2, 2, 2);

		addToGridBagLayout(0, 0, fromDataPanel, lblHLineLength);
		addToGridBagLayout(1, 0, fromDataPanel, txtHLineLength);
		addToGridBagLayout(0, 1, fromDataPanel, lblVLineLength);
		addToGridBagLayout(1, 1, fromDataPanel, txtVLineLength);
		addToGridBagLayout(0, 2, fromDataPanel, lblResolution);
		addToGridBagLayout(1, 2, fromDataPanel, irResEditBox);
		addToGridBagLayout(0, 3, 2, 1, fromDataPanel, lblVenderCA);
		addToGridBagLayout(1, 3, 1, 1, fromDataPanel, txtVendorCA);
		addToGridBagLayout(0, 4, fromDataPanel, lblImageDataPath);
		addToGridBagLayout(0, 5, 2, 1, fromDataPanel, txtImageDataPath);
		addToGridBagLayout(2, 5, 1, 1, fromDataPanel, btnBrowseData);
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

	private void enableFromImagePanel(boolean enabled) {
		txtImagePath.setEnabled(enabled);
		btnBrowseImage.setEnabled(enabled);
	}

	private void enableFromDataPanel(boolean enabled) {
		lblHLineLength.setEnabled(enabled);
		txtHLineLength.setEnabled(enabled);
		lblVLineLength.setEnabled(enabled);
		txtVLineLength.setEnabled(enabled);
		lblResolution.setEnabled(enabled);
		irResEditBox.setEnabled(enabled);
		lblVenderCA.setEnabled(enabled && getCompressionAlgorithm().equals(ANImageCompressionAlgorithm.VENDOR));
		txtVendorCA.setEnabled(enabled && getCompressionAlgorithm().equals(ANImageCompressionAlgorithm.VENDOR));
		lblImageDataPath.setEnabled(enabled);
		txtImageDataPath.setEnabled(enabled);
		btnBrowseData.setEnabled(enabled);
	}

	protected void enablePanels() {
		enableFromImagePanel(radioFromImage.isSelected());
		enableFromDataPanel(radioFromData.isSelected());
	}

	private void browseImage() {
		ANTemplateSettings settings = ANTemplateSettings.getInstance();
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImages.getOpenFileFilter()));
		imageOpenFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
		if (imageOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			settings.setLastDirectory(imageOpenFileDialog.getSelectedFile().getParentFile().getPath());
			txtImagePath.setText(imageOpenFileDialog.getSelectedFile().getPath());
			try {
				NImage image = getImage();

				setIsrValuePpi(image.getHorzResolution() * (isLowResolution() ? 2 : 1));
				setIrValuePpi(image.getHorzResolution());

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Could not load image from specified file.");
			}
		}
	}

	private void browseData() {
		ANTemplateSettings settings = ANTemplateSettings.getInstance();
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImages.getOpenFileFilter()));
		imageOpenFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
		if (imageOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			settings.setLastDirectory(imageOpenFileDialog.getSelectedFile().getParentFile().getPath());
			txtImageDataPath.setText(imageOpenFileDialog.getSelectedFile().getPath());
			try {
				NImage image = NImage.fromFile(txtImageDataPath.getText());

				setIsrValuePpi(image.getHorzResolution() * (isLowResolution() ? 2 : 1));
				setIrValuePpi(image.getHorzResolution());

			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.toString());
			}
		}
	}

	private void compressionAlgorithmChanged() {
		boolean enable = getCompressionAlgorithm().equals(ANImageCompressionAlgorithm.VENDOR) && radioFromData.isSelected();
		lblVenderCA.setEnabled(enable);
		txtVendorCA.setEnabled(enable);
	}

	// ==============================================
	// Protected methods
	// ==============================================

	protected Class<?> getCompressionFormatsType() {
		return null;
	}

	protected final ButtonGroup getGroup() {
		return group;
	}

	protected final JPanel getBinaryPanel() {
		return binaryPanel;
	}

	// ==============================================
	// Public methods
	// ==============================================

	public final boolean isLowResolution() {
		return isLowResolution;
	}

	public final void setLowResolution(boolean value) {
		isLowResolution = value;
		setIsrValue(ANType1Record.MIN_SCANNING_RESOLUTION);
		setIr(getIsrValue() / (value ? 2 : 1));

	}

	public final boolean isIsrFlagUp() {
		return chkResolutionFlag.isSelected();
	}

	public final int getIsrValue() {
		return (int) Math.round(isrResEditBox.getPpmValue());
	}

	public final void setIsrValue(int value) {
		isrResEditBox.setPpmValue(value);
	}

	public final double getIsrValuePpi() {
		return Math.round(isrResEditBox.getPpiValue());
	}

	public final void setIsrValuePpi(double value) {
		isrResEditBox.setPpiValue(value);
	}

	public final Object getCompressionAlgorithm() {
		return cmbCompressionAlgorithm.getSelectedItem();
	}

	public final void setCompressionAlgorithm(Object value) {
		cmbCompressionAlgorithm.setSelectedItem(value);
	}

	public final boolean isCreateFromImage() {
		return radioFromImage.isSelected();
	}

	public final boolean isCreateFromData() {
		return radioFromData.isSelected();
	}

	public final NImage getImage() {
		try {
			String imagePath = txtImagePath.getText();
			if (imagePath != null && !imagePath.isEmpty()) {
				return NImage.fromFile(imagePath);
			}
			JOptionPane.showMessageDialog(this, "File path can not be empty");
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.toString());
		}
		return null;
	}

	public final int getHll() {
		return Integer.parseInt(txtHLineLength.getText());
	}

	public final int getVll() {
		return Integer.parseInt(txtVLineLength.getText());
	}

	public final int getIr() {
		return (int) Math.round(irResEditBox.getPpmValue());
	}

	public final void setIr(int value) {
		irResEditBox.setPpmValue(value);
	}

	public final double getIrValuePpi() {
		return Math.round(irResEditBox.getPpiValue());
	}

	public final void setIrValuePpi(double value) {
		irResEditBox.setPpiValue(value);
	}

	public final int getVendorCA() {
		return Integer.parseInt(txtVendorCA.getText());
	}

	public final byte[] getImageData() {
		String dataPath = txtImageDataPath.getText();
		if (dataPath == null || dataPath.isEmpty()) {
			JOptionPane.showMessageDialog(this, "File path can not be empty");
			return null;
		}

		File f = new File(dataPath);
		DataInputStream dis = null;

		if (f.exists()) {
			byte[] fileData = new byte[(int) f.length()];
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
		}
		return null;
	}

	// ==============================================
	// Event handling
	// ==============================================

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == radioFromImage || source == radioFromData) {
			enablePanels();
		} else if (source == btnBrowseImage) {
			browseImage();
		} else if (source == btnBrowseData) {
			browseData();
		} else if (source == cmbCompressionAlgorithm) {
			compressionAlgorithmChanged();
		} else {
			super.actionPerformed(e);
		}
	}

	// ==============================================================================
	// Private class extending InputVerifier to verify image scanning resolution
	// ==============================================================================

	private final class ImageScanningResolutionVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			try {
				getIsrValue();

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ANImageBinaryRecordCreationFrame.this, "Image scanning resolution value must be a valid integer number.");
				return false;
			}
			return true;
		}

	}

	// ==============================================================================
	// Private class extending InputVerifier to verify horizontal line length
	// ==============================================================================

	private final class HLineLengthVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			try {
				getHll();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ANImageBinaryRecordCreationFrame.this, "Horizontal line length value must be a valid integer number.");
				return false;
			}
		}

	}

	// ==============================================================================
	// Private class extending InputVerifier to verify vertical line length
	// ==============================================================================

	private final class VLineLengthVerfier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			try {
				getVll();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ANImageBinaryRecordCreationFrame.this, "Vertical line length value must be a valid integer number.");
				return false;
			}
		}

	}

	// ==============================================================================
	// Private class extending InputVerifier to verify native scanning resolution
	// ==============================================================================

	private final class NativeScanningResolutionVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			try {
				getIr();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ANImageBinaryRecordCreationFrame.this, "Image resolution value must be a valid integer number.");
				return false;
			}
		}

	}

	// ==============================================================================
	// Private class extending InputVerifier to verify vender CA
	// ==============================================================================

	private final class VenderCAVerfier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			try {
				getVendorCA();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ANImageBinaryRecordCreationFrame.this, "Vender compression algorithm value must be a valid integer number.");
				return false;
			}
		}

	}

}
