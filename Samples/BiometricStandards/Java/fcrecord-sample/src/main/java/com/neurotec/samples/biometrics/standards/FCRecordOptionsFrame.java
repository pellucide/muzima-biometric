package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.FCRFaceImage;
import com.neurotec.biometrics.standards.FCRecord;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public final class FCRecordOptionsFrame extends BDIFOptionsFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JCheckBox chkProcessFirstImageOnly;
	private JCheckBox chkSkipFeaturePoints;

	// ==============================================
	// Public constructor
	// ==============================================

	public FCRecordOptionsFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		this.setTitle("FCRecordOptionsFrame");
		this.setPreferredSize(new Dimension(290, 300));
		initializeComponents();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		chkProcessFirstImageOnly = new JCheckBox("Process first image only");
		chkSkipFeaturePoints = new JCheckBox("Skip feature points");

		JPanel fcRecordPanel = new JPanel();
		fcRecordPanel.setPreferredSize(new Dimension(265, 95));
		fcRecordPanel.setBorder(BorderFactory.createTitledBorder("FCRecord"));
		fcRecordPanel.setLayout(new BoxLayout(fcRecordPanel, BoxLayout.Y_AXIS));
		fcRecordPanel.add(chkProcessFirstImageOnly);
		fcRecordPanel.add(chkSkipFeaturePoints);

		this.getContentPane().add(fcRecordPanel);
		fcRecordPanel.setBounds(12, 130, 265, 95);
		getButtonPanel().setBounds(12, 235, 265, 25);
		this.pack();
	}

	// ==============================================
	// Overridden methods
	// ==============================================

	@Override
	protected void buttonOKActionPerformed() {
		getOwnerFrameListener().setCurrentStandard(getStandard());
		getOwnerFrameListener().setCurrentFlags(getFlags());
	}

	@Override
	public int getFlags() {
		int flags = super.getFlags();
		if (chkProcessFirstImageOnly.isSelected()) {
			flags |= FCRecord.FLAG_PROCESS_FIRST_FACE_IMAGE_ONLY;
		}
		if (chkSkipFeaturePoints.isSelected()) {
			flags |= FCRFaceImage.FLAG_SKIP_FEATURE_POINTS;
		}
		return flags;
	}

	@Override
	public void setFlags(int flags) {
		if ((flags & FCRecord.FLAG_PROCESS_FIRST_FACE_IMAGE_ONLY) == FCRecord.FLAG_PROCESS_FIRST_FACE_IMAGE_ONLY) {
			chkProcessFirstImageOnly.setSelected(true);
		}
		if ((flags & FCRFaceImage.FLAG_SKIP_FEATURE_POINTS) == FCRFaceImage.FLAG_SKIP_FEATURE_POINTS) {
			chkSkipFeaturePoints.setSelected(true);
		}
		super.setFlags(flags);
	}

}
