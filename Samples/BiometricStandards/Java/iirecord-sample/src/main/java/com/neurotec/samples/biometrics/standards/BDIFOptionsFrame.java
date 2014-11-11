package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.BDIFStandard;
import com.neurotec.biometrics.standards.BDIFTypes;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

public abstract class BDIFOptionsFrame extends JDialog implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JComboBox cmbBiometricStandard;
	private JCheckBox chkNoStrictRead;
	private JCheckBox chkDoNotCheckCbeffProductId;
	private JButton btnOK;
	private JButton btnCancel;
	private JPanel commonPanel;
	private JPanel buttonPanel;

	// ==============================================
	// Private fields
	// ==============================================

	private final MainFrameEventListener listener;
	private BDIFOptionsFormMode mode = BDIFOptionsFormMode.NEW;

	// ==============================================
	// Public constructor
	// ==============================================

	public BDIFOptionsFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, "BDIFOptionsFrame", true);
		this.listener = listener;
		this.setPreferredSize(new Dimension(290, 190));
		this.setResizable(false);
		initializeComponents();

		cmbBiometricStandard.addItem(BDIFStandard.ANSI);
		cmbBiometricStandard.addItem(BDIFStandard.ISO);
		cmbBiometricStandard.setSelectedIndex(0);
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		Container contentPane = this.getContentPane();
		contentPane.setLayout(null);
		
		cmbBiometricStandard = new JComboBox();

		chkNoStrictRead = new JCheckBox("Non-strict read");
		chkDoNotCheckCbeffProductId = new JCheckBox("Do not check CBEFF product id");

		commonPanel = new JPanel();
		commonPanel.setPreferredSize(new Dimension(265, 110));
		commonPanel.setBorder(BorderFactory.createTitledBorder("Common"));

		GridBagLayout commonPanelLayout = new GridBagLayout();
		commonPanelLayout.columnWidths = new int[] { 120, 90 };
		commonPanelLayout.rowHeights = new int[] { 30, 25, 25 };
		commonPanel.setLayout(commonPanelLayout);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 0;
		commonPanel.add(new JLabel("Biometric standard:"), c);

		c.gridx = 1;
		c.gridy = 0;
		commonPanel.add(cmbBiometricStandard, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		commonPanel.add(chkNoStrictRead, c);

		c.gridx = 0;
		c.gridy = 2;
		commonPanel.add(chkDoNotCheckCbeffProductId, c);

		btnOK = new JButton("OK");
		btnOK.setPreferredSize(new Dimension(75, 25));
		btnOK.addActionListener(this);

		btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(75, 25));
		btnCancel.addActionListener(this);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(btnOK);
		buttonPanel.add(btnCancel);

		contentPane.add(commonPanel);
		contentPane.add(buttonPanel);
		commonPanel.setBounds(12, 12, 265, 110);
		buttonPanel.setBounds(12, 130, 265, 25);
		this.pack();
	}

	// ==============================================
	// Protected methods
	// ==============================================

	protected void onModeChanged() {
		setTitle(mode.name());

		switch (mode) {
		case NEW:
		case SAVE:
		case CONVERT:
			chkNoStrictRead.setEnabled(false);
			break;
		default:
			chkNoStrictRead.setEnabled(true);
		}
	}

	protected abstract void buttonOKActionPerformed();

	// ==============================================
	// Public getters and setters
	// ==============================================

	public final BDIFOptionsFormMode getMode() {
		return mode;
	}

	public final void setMode(BDIFOptionsFormMode mode) {
		if (this.mode != mode) {
			this.mode = mode;
			onModeChanged();
		}
	}

	public final BDIFStandard getStandard() {
		return (BDIFStandard) cmbBiometricStandard.getSelectedItem();
	}

	public final void setStandard(BDIFStandard standard) {
		cmbBiometricStandard.setSelectedItem(standard);
	}

	public int getFlags() {
		int flags = 0;
		if (chkDoNotCheckCbeffProductId.isSelected()) {
			flags |= BDIFTypes.FLAG_DO_NOT_CHECK_CBEFF_PRODUCT_ID;
		}
		if (chkNoStrictRead.isSelected()) {
			flags |= BDIFTypes.FLAG_NON_STRICT_READ;
		}
		return flags;
	}

	public void setFlags(int flags) {
		if ((flags & BDIFTypes.FLAG_DO_NOT_CHECK_CBEFF_PRODUCT_ID) == BDIFTypes.FLAG_DO_NOT_CHECK_CBEFF_PRODUCT_ID) {
			chkDoNotCheckCbeffProductId.setSelected(true);
		}
		if ((flags & BDIFTypes.FLAG_NON_STRICT_READ) == BDIFTypes.FLAG_NON_STRICT_READ) {
			chkNoStrictRead.setSelected(true);
		}
	}

	public final JPanel getButtonPanel() {
		return buttonPanel;
	}

	public final MainFrameEventListener getOwnerFrameListener() {
		return listener;
	}

	// ==============================================
	// Event handling
	// ==============================================

	public final void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnOK) {
			buttonOKActionPerformed();
			listener.setDialogResultOK(true);
			this.dispose();
		} else if (source == btnCancel) {
			listener.setDialogResultOK(false);
			this.dispose();
		}
	}

}
