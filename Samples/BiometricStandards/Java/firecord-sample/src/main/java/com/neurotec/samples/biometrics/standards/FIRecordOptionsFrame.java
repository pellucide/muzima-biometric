package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.FIRecord;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public final class FIRecordOptionsFrame extends BDIFOptionsFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JCheckBox chkProcessFirstFingerOnly;
	private JCheckBox chkProcessFirstFingerViewOnly;

	// ==============================================
	// Public constructor
	// ==============================================

	public FIRecordOptionsFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		this.setTitle("FIRecordOptionsFrame");
		this.setPreferredSize(new Dimension(285, 285));
		initializeComponents();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		chkProcessFirstFingerOnly = new JCheckBox("Process first finger only");
		chkProcessFirstFingerViewOnly = new JCheckBox("Process first fingerView only");

		JPanel fiRecordPanel = new JPanel();
		fiRecordPanel.setPreferredSize(new Dimension(265, 90));
		fiRecordPanel.setBorder(BorderFactory.createTitledBorder("FIRecord"));
		fiRecordPanel.setLayout(new BoxLayout(fiRecordPanel, BoxLayout.Y_AXIS));
		fiRecordPanel.add(chkProcessFirstFingerOnly);
		fiRecordPanel.add(chkProcessFirstFingerViewOnly);

		this.getContentPane().add(fiRecordPanel);
		fiRecordPanel.setBounds(12, 130, 265, 90);
		getButtonPanel().setBounds(12, 220, 265, 25);
		this.pack();
	}

	// ==============================================
	// Overridden methods
	// ==============================================

	@Override
	protected void onModeChanged() {
		super.onModeChanged();
		switch (getMode()) {
		case NEW:
			chkProcessFirstFingerOnly.setEnabled(false);
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
		if (chkProcessFirstFingerOnly.isSelected()) {
			flags |= FIRecord.FLAG_PROCESS_FIRST_FINGER_IMAGE_ONLY;
		}
		if (chkProcessFirstFingerViewOnly.isSelected()) {
			flags |= FIRecord.FLAG_PROCESS_FIRST_FINGER_VIEW_ONLY;
		}
		return flags;
	}

	@Override
	public void setFlags(int flags) {
		if ((flags & FIRecord.FLAG_PROCESS_FIRST_FINGER_IMAGE_ONLY) == FIRecord.FLAG_PROCESS_FIRST_FINGER_IMAGE_ONLY) {
			chkProcessFirstFingerOnly.setSelected(true);
		}
		if ((flags & FIRecord.FLAG_PROCESS_FIRST_FINGER_VIEW_ONLY) == FIRecord.FLAG_PROCESS_FIRST_FINGER_VIEW_ONLY) {
			chkProcessFirstFingerViewOnly.setSelected(true);
		}
		super.setFlags(flags);
	}

}
