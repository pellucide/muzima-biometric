package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.IIRIris;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public final class IIRecordOptionsFrame extends BDIFOptionsFrame {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final long serialVersionUID = 1L;

	// ===========================================================
	// Private GUI controls
	// ===========================================================

	private JCheckBox chkProcessFirstImageOnly;

	// ===========================================================
	// Public constructor
	// ===========================================================

	public IIRecordOptionsFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		this.setTitle("IIRecordOptionsFrame");
		this.setPreferredSize(new Dimension(295, 255));
		initializeComponents();
	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private void initializeComponents() {

		chkProcessFirstImageOnly = new JCheckBox("Process first iris image only");

		JPanel fcRecordPanel = new JPanel(new BorderLayout());
		fcRecordPanel.setBorder(BorderFactory.createTitledBorder("IIRecord"));
		fcRecordPanel.add(chkProcessFirstImageOnly, BorderLayout.CENTER);

		this.getContentPane().add(fcRecordPanel);
		fcRecordPanel.setBounds(12, 130, 265, 55);
		getButtonPanel().setBounds(12, 190, 265, 25);
		this.pack();

	}

	// ===========================================================
	// Overridden methods
	// ===========================================================

	@Override
	protected void onModeChanged() {
		super.onModeChanged();

		switch (getMode()) {
		case NEW:
			chkProcessFirstImageOnly.setEnabled(false);
			break;
		default:
		}
	}

	@Override
	protected void buttonOKActionPerformed() {
		getOwnerFrameListener().setCurrentStandard(getStandard());
		getOwnerFrameListener().setCurrentFlags(getFlags());

	}

	@Override
	public int getFlags() {
		int flags = super.getFlags();
		if (chkProcessFirstImageOnly.isSelected()) {
			flags |= IIRIris.FLAG_PROCESS_FIRST_IRIS_IMAGE_ONLY;
		}
		return flags;
	}

	@Override
	public void setFlags(int flags) {
		if ((flags & IIRIris.FLAG_PROCESS_FIRST_IRIS_IMAGE_ONLY) == IIRIris.FLAG_PROCESS_FIRST_IRIS_IMAGE_ONLY) {
			chkProcessFirstImageOnly.setSelected(true);
		}
		super.setFlags(flags);
	}

}
