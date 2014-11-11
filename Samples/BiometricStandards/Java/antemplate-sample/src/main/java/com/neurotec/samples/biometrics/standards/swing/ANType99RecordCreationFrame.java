package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.neurotec.biometrics.standards.ANASCIIBinaryRecord;
import com.neurotec.biometrics.standards.ANBiometricType;
import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.ANType99Record;
import com.neurotec.samples.biometrics.standards.ANTemplateSettings;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;
import com.neurotec.util.NVersion;

public final class ANType99RecordCreationFrame extends ANRecordCreationFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private fields
	// ==============================================

	private Vector<JCheckBox> checkBoxVector = new Vector<JCheckBox>();
	private Vector<ANBiometricType> biometricTypesVector = new Vector<ANBiometricType>();
	private boolean isUpdating;
	private GridBagConstraints c;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JComboBox cmbVersion;
	private JSpinner spinnerOwner;
	private JSpinner spinnerType;
	private JPanel biometricTypesPanel;
	private JTextField txtDataPath;
	private JTextField txtSource;
	private JButton btnBrowse;
	private JFileChooser dataOpenFileDialog = new JFileChooser();

	// ==============================================
	// Public constructor
	// ==============================================

	public ANType99RecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		setPreferredSize(new Dimension(380, 370));
		setTitle("Add Type-99 ANRecord");
		initializeComponents();

		cmbVersion.addItem(ANType99Record.HEADER_VERSION_1_0);
		cmbVersion.addItem(ANType99Record.HEADER_VERSION_1_1);
		cmbVersion.setSelectedIndex(cmbVersion.getItemCount() - 1);

		spinnerOwner.setModel(new SpinnerNumberModel(0, 0, Short.MAX_VALUE, 1));
		spinnerType.setModel(new SpinnerNumberModel(0, 0, Short.MAX_VALUE, 1));

		for (ANBiometricType value : ANBiometricType.values()) {
			JCheckBox chkBox = new JCheckBox(value.name());
			chkBox.addActionListener(new BiometricTypeCheckBoxListener());
			chkBox.setBackground(Color.WHITE);
			chkBox.setOpaque(true);
			checkBoxVector.add(chkBox);
			biometricTypesVector.add(value);
			biometricTypesPanel.add(chkBox);
		}
		checkBoxVector.get(0).setSelected(true);
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {

		JPanel type99Panel = new JPanel();
		GridBagLayout type99GridBagLayout = new GridBagLayout();
		type99GridBagLayout.columnWidths = new int[] { 80, 40, 75, 75, 75 };
		type99Panel.setLayout(type99GridBagLayout);

		txtSource = new JTextField();
		txtSource.setInputVerifier(new SoruceVerifier());

		cmbVersion = new JComboBox();

		biometricTypesPanel = new JPanel();
		biometricTypesPanel.setBackground(Color.WHITE);
		biometricTypesPanel.setOpaque(true);
		biometricTypesPanel.setLayout(new BoxLayout(biometricTypesPanel, BoxLayout.Y_AXIS));

		JScrollPane scrollCheckBox = new JScrollPane(biometricTypesPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollCheckBox.setPreferredSize(new Dimension(215, 95));

		spinnerOwner = new JSpinner();
		spinnerType = new JSpinner();
		txtDataPath = new JTextField();
		btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(this);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 2, 2, 2);

		addToGridBagLayout(0, 0, type99Panel, new JLabel("Source agency:"));
		addToGridBagLayout(2, 0, 3, 1, type99Panel, txtSource);
		addToGridBagLayout(0, 1, 1, 1, type99Panel, new JLabel("Version:"));
		addToGridBagLayout(2, 1, 3, 1, type99Panel, cmbVersion);
		addToGridBagLayout(0, 2, 2, 1, type99Panel, new JLabel("Biometric type:"));
		addToGridBagLayout(2, 2, 3, 4, type99Panel, scrollCheckBox);
		addToGridBagLayout(0, 6, 2, 1, type99Panel, new JLabel("Biometric format owner:"));
		addToGridBagLayout(2, 6, 1, 1, type99Panel, spinnerOwner);
		addToGridBagLayout(0, 7, 2, 1, type99Panel, new JLabel("Biometric format type:"));
		addToGridBagLayout(2, 7, 1, 1, type99Panel, spinnerType);
		addToGridBagLayout(0, 8, 1, 1, type99Panel, new JLabel("Biometric data:"));
		addToGridBagLayout(1, 8, 3, 1, type99Panel, txtDataPath);
		addToGridBagLayout(4, 8, 1, 1, type99Panel, btnBrowse);

		getContentPane().add(type99Panel);
		type99Panel.setBounds(10, 60, 360, 225);
		getButtonPanel().setBounds(10, 305, 360, 25);
		pack();

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

	private void browseData() {
		ANTemplateSettings settings = ANTemplateSettings.getInstance();
		dataOpenFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
		if (dataOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			settings.setLastDirectory(dataOpenFileDialog.getSelectedFile().getParentFile().getPath());
			txtDataPath.setText(dataOpenFileDialog.getSelectedFile().getPath());
		}
	}

	// ==============================================
	// Protected methods
	// ==============================================

	@Override
	protected ANRecord onCreateRecord(ANTemplate template) {

		String filePath = txtDataPath.getText();
		if (filePath == null || filePath.isEmpty()) {
			JOptionPane.showMessageDialog(this, "File path can not be empty");
			return null;
		}

		File f = new File(filePath);
		if (!f.exists()) {
			return null;
		}

		byte[] data = new byte[(int) f.length()];
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(f));
			dis.readFully(data);
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

		String source = txtSource.getText();

		if (source == null || source.isEmpty()) {
			JOptionPane.showMessageDialog(ANType99RecordCreationFrame.this, String.format("Source agency field length must be between %s and %s characters.", ANASCIIBinaryRecord.MIN_SOURCE_AGENCY_LENGTH, ANASCIIBinaryRecord.MAX_SOURCE_AGENCY_LENGTH_V4));
			return null;
		}
		return template.getRecords().addType99(getIdc(), source, (NVersion) cmbVersion.getSelectedItem(), getBiometricType(), Short.parseShort(String.valueOf(spinnerOwner.getValue())), Short.parseShort(String.valueOf(spinnerType.getValue())), ByteBuffer.wrap(data));
	}

	protected EnumSet<ANBiometricType> getBiometricType() {
		EnumSet<ANBiometricType> enumset = EnumSet.noneOf(ANBiometricType.class);
		List<ANBiometricType> selected = new ArrayList<ANBiometricType>();
		for (int i = 0; i < checkBoxVector.size(); i++) {
			if (checkBoxVector.get(i).isSelected()) {
				selected.add(biometricTypesVector.get(i));
				enumset.add(biometricTypesVector.get(i));
			}
		}
		return enumset;
	}

	// ==============================================
	// Public methods
	// ==============================================

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnBrowse) {
			browseData();
		}
		super.actionPerformed(e);
	}

	// ======================================================================
	// Private class extending InputVerifier to verify source agency
	// ======================================================================

	private final class SoruceVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			if (txtSource.getText().length() < ANASCIIBinaryRecord.MIN_SOURCE_AGENCY_LENGTH || txtSource.getText().length() > ANASCIIBinaryRecord.MAX_SOURCE_AGENCY_LENGTH_V4) {
				JOptionPane.showMessageDialog(ANType99RecordCreationFrame.this, String.format("Source agency field length must be between %s and %s characters.", ANASCIIBinaryRecord.MIN_SOURCE_AGENCY_LENGTH, ANASCIIBinaryRecord.MAX_SOURCE_AGENCY_LENGTH_V4));
				return false;
			}
			return true;
		}

	}

	// ====================================================
	// Event handling of biometric types check boxes
	// ====================================================

	private final class BiometricTypeCheckBoxListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			JCheckBox chkBox = (JCheckBox) e.getSource();
			if (isUpdating) {
				return;
			}

			isUpdating = true;
			if (checkBoxVector.indexOf(chkBox) == 0) {
				if (chkBox.isSelected()) {
					for (int i = 1; i < checkBoxVector.size(); i++) {
						checkBoxVector.get(i).setSelected(false);
					}
				}
			} else {
				checkBoxVector.get(0).setSelected(false);
			}
			isUpdating = false;
		}
	}

}
