package com.neurotec.samples.biometrics.standards.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.neurotec.biometrics.standards.ANType1Record;
import com.neurotec.biometrics.standards.ANValidationLevel;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;

public final class ANType1RecordCreationFrame extends ANRecordCreationFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JCheckBox chkUserNISTMinutiaNeighbors;
	private JCheckBox chkUseTwoDigitIDC;
	private JCheckBox chkUseTwoDigitFN;
	private JCheckBox chkUseTwoDigitFNT1;

	private JComboBox cmbValidationLevel;

	private JTextField txtTransactionType;
	private JTextField txtDestinationAgency;
	private JTextField txtOriginatingAgency;
	private JTextField txtTransactionControlId;

	// ==============================================
	// Public constructor
	// ==============================================

	public ANType1RecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		setPreferredSize(new Dimension(255, 380));
		setTitle("New Template");
		initializeComponents();

		getSpinnerIdc().setEnabled(false);
		getSpinnerIdc().setModel(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));

		for (ANValidationLevel item : ANValidationLevel.values()) {
			cmbValidationLevel.addItem(item);
		}
		cmbValidationLevel.setSelectedIndex(0);
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		JPanel type1Panel = new JPanel();
		type1Panel.setLayout(new BoxLayout(type1Panel, BoxLayout.Y_AXIS));

		cmbValidationLevel = new JComboBox();
		cmbValidationLevel.addActionListener(this);

		chkUserNISTMinutiaNeighbors = new JCheckBox("Use NIST minutia neighbors");
		chkUseTwoDigitIDC = new JCheckBox("Use two-digit IDC");
		chkUseTwoDigitFN = new JCheckBox("Use two-digit FN");
		chkUseTwoDigitFNT1 = new JCheckBox("Use two-digit FN T1");

		txtTransactionType = new JTextField();
		txtTransactionType.setInputVerifier(new TransactionTypeVerifier());

		txtDestinationAgency = new JTextField();
		txtOriginatingAgency = new JTextField();
		txtTransactionControlId = new JTextField();

		JPanel transactionTypePanel = new JPanel(new BorderLayout());
		transactionTypePanel.add(new JLabel("Transaction type:"), BorderLayout.BEFORE_LINE_BEGINS);

		JPanel destinationAgencyPanel = new JPanel(new BorderLayout());
		destinationAgencyPanel.add(new JLabel("Destination agency:"), BorderLayout.BEFORE_LINE_BEGINS);

		JPanel originatingAgencyPanel = new JPanel(new BorderLayout());
		originatingAgencyPanel.add(new JLabel("Originating agency:"), BorderLayout.BEFORE_LINE_BEGINS);

		JPanel transactionControlIdPanel = new JPanel(new BorderLayout());
		transactionControlIdPanel.add(new JLabel("Transaction control identifier:"), BorderLayout.BEFORE_LINE_BEGINS);

		JPanel validationLevelPanel = new JPanel();
		validationLevelPanel.setLayout(new BoxLayout(validationLevelPanel, BoxLayout.X_AXIS));
		validationLevelPanel.add(new JLabel("Validation level:"));
		validationLevelPanel.add(Box.createHorizontalStrut(5));
		validationLevelPanel.add(cmbValidationLevel);
		validationLevelPanel.add(Box.createHorizontalStrut(5));

		JPanel checkBoxPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		checkBoxPanel.add(chkUserNISTMinutiaNeighbors, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		checkBoxPanel.add(chkUseTwoDigitIDC, c);

		c.gridx = 0;
		c.gridy = 2;
		checkBoxPanel.add(chkUseTwoDigitFN, c);

		c.gridx = 1;
		c.gridy = 2;
		checkBoxPanel.add(chkUseTwoDigitFNT1, c);

		type1Panel.add(validationLevelPanel);
		type1Panel.add(checkBoxPanel);
		type1Panel.add(transactionTypePanel);
		type1Panel.add(txtTransactionType);
		type1Panel.add(destinationAgencyPanel);
		type1Panel.add(txtDestinationAgency);
		type1Panel.add(originatingAgencyPanel);
		type1Panel.add(txtOriginatingAgency);
		type1Panel.add(transactionControlIdPanel);
		type1Panel.add(txtTransactionControlId);

		getContentPane().add(type1Panel);
		type1Panel.setBounds(10, 60, 235, 255);
		getButtonPanel().setBounds(10, 320, 235, 25);
		pack();
	}

	// ==============================================
	// Protected overridden methods
	// ==============================================

	@Override
	protected boolean buttonOKActionPerformed() {
		try {
			setCreatedRecord(onCreateRecord(getTemplate()));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.toString());
		}
		return getEventListener().newType1RecordCreated(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == cmbValidationLevel) {
			boolean isStandard = getValidationLevel() == ANValidationLevel.STANDARD;
			txtDestinationAgency.setEnabled(isStandard);
			txtOriginatingAgency.setEnabled(isStandard);
			txtTransactionControlId.setEnabled(isStandard);
			txtTransactionType.setEnabled(isStandard);

		} else {
			super.actionPerformed(e);
		}
	}

	// ==============================================
	// Public methods
	// ==============================================

	public String getTransactionType() {
		return txtTransactionType.getText();
	}

	public void setTransactionType(String value) {
		txtTransactionType.setText(value);
	}

	public String getDestinationAgency() {
		return txtDestinationAgency.getText();
	}

	public void setDestinationAgency(String value) {
		txtDestinationAgency.setText(value);
	}

	public String getOriginatingAgency() {
		return txtOriginatingAgency.getText();
	}

	public void setOriginatingAgency(String value) {
		txtOriginatingAgency.setText(value);
	}

	public String getTransactionControl() {
		return txtTransactionControlId.getText();
	}

	public void setTransactionControl(String value) {
		txtTransactionControlId.setText(value);
	}

	public ANValidationLevel getValidationLevel() {
		return (ANValidationLevel) cmbValidationLevel.getSelectedItem();
	}

	public void setValidationLevel(ANValidationLevel value) {
		cmbValidationLevel.setSelectedItem(value);
	}

	public boolean isUseNistMinutiaNeighbors() {
		return chkUserNISTMinutiaNeighbors.isSelected();
	}

	public void setUseNistMinutiaNeighbors(boolean value) {
		chkUserNISTMinutiaNeighbors.setSelected(value);
	}

	public boolean isUseTwoDigitIdc() {
		return chkUseTwoDigitIDC.isSelected();
	}

	public void setUseTwoDigitIdc(boolean value) {
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

	// ======================================================================
	// Private class extending InputVerifier to verify transaction type 
	// ======================================================================

	private final class TransactionTypeVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent c) {
			String value = ((JTextField) c).getText();

			if (value.length() < ANType1Record.MIN_TRANSACTION_TYPE_LENGTH_V4 || value.length() > ANType1Record.MAX_TRANSACTION_TYPE_LENGTH_V4) {
				JOptionPane.showMessageDialog(ANType1RecordCreationFrame.this, String.format("Transaction type value must be %s to %s characters long", ANType1Record.MIN_TRANSACTION_TYPE_LENGTH_V4, ANType1Record.MAX_TRANSACTION_TYPE_LENGTH_V4));
				return false;
			}
			return true;
		}

	}

}
