package com.neurotec.samples.biometrics.standards;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.ANValidationLevel;
import com.neurotec.biometrics.standards.BDIFTypes;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;

public final class OptionsFrame extends JDialog implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private fields
	// ==============================================

	private boolean isTemplateOpenMode;
	private MainFrameEventListener listener;
	private Frame owner;
	private final ANTemplateSettings settings = ANTemplateSettings.getInstance();

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JComboBox cmbValidationLevel;
	private JCheckBox chkUserNISTMinutiaNeighbors;
	private JCheckBox chkUseTwoDigitIDC;
	private JCheckBox chkUseTwoDigitFN;
	private JCheckBox chkUseTwoDigitFNT1;
	private JCheckBox chkNonStrictRead;
	private JCheckBox chkMergeDuplicateFields;
	private JCheckBox chkLeaveInvalidRecordsUnvalidated;
	private JCheckBox chkRecoverFromBinaryData;

	private JButton btnOk;
	private JButton btnCancel;

	// ==============================================
	// Public constructor
	// ==============================================

	public OptionsFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, "Options", true);
		this.listener = listener;
		this.owner = owner;
		setPreferredSize(new Dimension(340, 190));
		setResizable(false);
		initializeComponents();

		for (ANValidationLevel item : ANValidationLevel.values()) {
			cmbValidationLevel.addItem(item);
		}
		cmbValidationLevel.setSelectedIndex(0);
		loadSettings();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

		cmbValidationLevel = new JComboBox();

		topPanel.add(new JLabel("Validation level"));
		topPanel.add(Box.createHorizontalStrut(5));
		topPanel.add(cmbValidationLevel);
		topPanel.add(Box.createGlue());

		JPanel middlePanel = new JPanel(new GridLayout(4, 2));

		chkNonStrictRead = new JCheckBox("Non-strict read");
		chkUserNISTMinutiaNeighbors = new JCheckBox("Use NIST minutia neighbors");
		chkMergeDuplicateFields = new JCheckBox("Merge duplicate fields");
		chkLeaveInvalidRecordsUnvalidated = new JCheckBox("Leave invalid records unvalidated");
		chkUseTwoDigitIDC = new JCheckBox("Use two-digit IDC");
		chkRecoverFromBinaryData = new JCheckBox("Recover from binary data");
		chkUseTwoDigitFN = new JCheckBox("Use two-digit FN");
		chkUseTwoDigitFNT1 = new JCheckBox("Use two-digit FN T1");

		middlePanel.add(chkNonStrictRead);
		middlePanel.add(chkUserNISTMinutiaNeighbors);
		middlePanel.add(chkMergeDuplicateFields);
		middlePanel.add(chkLeaveInvalidRecordsUnvalidated);
		middlePanel.add(chkUseTwoDigitIDC);
		middlePanel.add(chkRecoverFromBinaryData);
		middlePanel.add(chkUseTwoDigitFN);
		middlePanel.add(chkUseTwoDigitFNT1);

		mainPanel.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);
		mainPanel.add(middlePanel, BorderLayout.CENTER);
		mainPanel.add(createButtonPanel(), BorderLayout.AFTER_LAST_LINE);

		getContentPane().add(mainPanel);
		pack();

	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		btnOk = new JButton("OK");
		btnOk.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		buttonPanel.add(Box.createGlue());
		buttonPanel.add(btnOk);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(btnCancel);
		return buttonPanel;
	}

	private void loadSettings() {
		setValidationLevel(settings.getValidationLevel());
		setUseNistMinutiaeNeighbors(settings.isUseNistMinutiaNeighbors());
		setUseTwoDigitIDC(settings.isUseTwoDigitIdc());
		setUseTwoDigitFieldNumber(settings.isUseTwoDigitFieldNumber());
		setUseTwoDigitFieldNumberType1(settings.isUseTwoDigitFieldNumberType1());
		setNonStrictRead(settings.isNonStrictRead());
		setMergeDuplicateFields(settings.isMergeDuplicateFields());
		setRecoverFromBinaryData(settings.isRecoverFromBinaryData());
		setLeaveInvalidRecordsUnvalidated(settings.isLeaveInvalidRecordsUnvalidated());
	}

	private void saveSettings() {
		settings.setValidationLevel(getValidationLevel());
		settings.setUseNistMinutiaNeighbors(isUseNistMinutiaeNeighbors());
		settings.setUseTwoDigitIdc(isUseTwoDigitIDC());
		settings.setUseTwoDigitFieldNumber(isUseTwoDigitFieldNumber());
		settings.setUseTwoDigitFieldNumberType1(isUseTwoDigitFieldNumberType1());
		settings.setNonStrictRead(isNonStrictRead());
		settings.setMergeDuplicateFields(isMergeDuplicateFields());
		settings.setRecoverFromBinaryData(isRecoverFromBinaryData());
		settings.setLeaveInvalidRecordsUnvalidated(isLeaveInvalidRecordsUnvalidated());
		settings.save();
	}

	// ==============================================
	// Public methods
	// ==============================================

	public boolean isUseNistMinutiaeNeighbors() {
		return chkUserNISTMinutiaNeighbors.isSelected();
	}

	public void setUseNistMinutiaeNeighbors(boolean value) {
		chkUserNISTMinutiaNeighbors.setSelected(value);
	}

	public boolean isUseTwoDigitIDC() {
		return chkUseTwoDigitIDC.isSelected();
	}

	public void setUseTwoDigitIDC(boolean value) {
		chkUseTwoDigitIDC.setSelected(value);
	}

	public boolean isUseTwoDigitFieldNumber() {
		return chkUseTwoDigitFN.isSelected();
	}

	public void setUseTwoDigitFieldNumber(boolean value) {
		chkUseTwoDigitFN.setSelected(value);
	}

	public boolean isUseTwoDigitFieldNumberType1() {
		return chkUseTwoDigitFNT1.isSelected();
	}

	public void setUseTwoDigitFieldNumberType1(boolean value) {
		chkUseTwoDigitFNT1.setSelected(value);
	}

	public boolean isNonStrictRead() {
		return chkNonStrictRead.isSelected();
	}

	public void setNonStrictRead(boolean value) {
		chkNonStrictRead.setSelected(value);
	}

	public boolean isMergeDuplicateFields() {
		return chkMergeDuplicateFields.isSelected();
	}

	public void setMergeDuplicateFields(boolean value) {
		chkMergeDuplicateFields.setSelected(value);
	}

	public boolean isLeaveInvalidRecordsUnvalidated() {
		return chkLeaveInvalidRecordsUnvalidated.isSelected();
	}

	public void setLeaveInvalidRecordsUnvalidated(boolean value) {
		chkLeaveInvalidRecordsUnvalidated.setSelected(value);
	}

	public boolean isRecoverFromBinaryData() {
		return chkRecoverFromBinaryData.isSelected();
	}

	public void setRecoverFromBinaryData(boolean value) {
		chkRecoverFromBinaryData.setSelected(value);
	}

	public ANValidationLevel getValidationLevel() {
		return (ANValidationLevel) cmbValidationLevel.getSelectedItem();
	}

	public void setValidationLevel(ANValidationLevel value) {
		cmbValidationLevel.setSelectedItem(value);
	}

	public int getFlags() {
		int value = 0;
		if (isUseNistMinutiaeNeighbors()) {
			value |= ANTemplate.FLAG_USE_NIST_MINUTIA_NEIGHBORS;
		}
		if (isUseTwoDigitIDC()) {
			value |= ANTemplate.FLAG_USE_TWO_DIGIT_IDC;
		}
		if (isUseTwoDigitFieldNumber()) {
			value |= ANTemplate.FLAG_USE_TWO_DIGIT_FIELD_NUMBER;
		}
		if (isUseTwoDigitFieldNumberType1()) {
			value |= ANTemplate.FLAG_USE_TWO_DIGIT_FIELD_NUMBER_TYPE_1;
		}
		if (isNonStrictRead()) {
			value |= BDIFTypes.FLAG_NON_STRICT_READ;
		}
		if (isMergeDuplicateFields()) {
			value |= ANRecord.FLAG_MERGE_DUBLICATE_FIELDS;
		}
		if (isLeaveInvalidRecordsUnvalidated()) {
			value |= ANTemplate.FLAG_LEAVE_INVALID_RECORDS_UNVALIDATED;
		}
		if (isRecoverFromBinaryData()) {
			value |= ANRecord.FLAG_RECOVER_FROM_BINARY_DATA;
		}
		return value;
	}

	public void setFlags(int value) {
		value = value & ANTemplate.FLAG_USE_NIST_MINUTIA_NEIGHBORS;
		setUseNistMinutiaeNeighbors(value != 0);
		value = value | ANTemplate.FLAG_USE_TWO_DIGIT_IDC;
		setUseTwoDigitIDC(value != 0);
		value = value | ANTemplate.FLAG_USE_TWO_DIGIT_FIELD_NUMBER;
		setUseTwoDigitFieldNumber(value != 0);
		value = value | ANTemplate.FLAG_USE_TWO_DIGIT_FIELD_NUMBER_TYPE_1;
		setUseTwoDigitFieldNumberType1(value != 0);
		value = value | BDIFTypes.FLAG_NON_STRICT_READ;
		setNonStrictRead(value != 0);
		value = value | ANRecord.FLAG_MERGE_DUBLICATE_FIELDS;
		setMergeDuplicateFields(value != 0);
		value = value | ANTemplate.FLAG_LEAVE_INVALID_RECORDS_UNVALIDATED;
		setLeaveInvalidRecordsUnvalidated(value != 0);
		value = value | ANRecord.FLAG_RECOVER_FROM_BINARY_DATA;
		setRecoverFromBinaryData(value != 0);
	}

	public void showOptionsDialog(boolean isTemplateOpenMode) {
		this.isTemplateOpenMode = isTemplateOpenMode;
		setLocationRelativeTo(owner);
		setVisible(true);
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnOk) {
			saveSettings();
			listener.optionsSelected(isTemplateOpenMode, getValidationLevel(), getFlags());
			dispose();
		} else if (source == btnCancel) {
			dispose();
		}
	}

}
