package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.BDIFScaleUnits;
import com.neurotec.biometrics.standards.FIRImageCompressionAlgorithm;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class AddNewFingerFromImageFrame extends AddFingerFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JComboBox cmbScaleUnits;
	private JComboBox cmbCompressionAlgorithm;

	// ==============================================
	// Public constructor
	// ==============================================

	public AddNewFingerFromImageFrame(Frame owner, MainFrameEventListener listener, boolean isAddOrCreate) {
		super(owner, listener, isAddOrCreate);
		this.setTitle("AddNewFingerFromImageFrame");
		this.setPreferredSize(new Dimension(275, 150));
		initializeComponents();

		for (BDIFScaleUnits scaleUnit : BDIFScaleUnits.values()) {
			cmbScaleUnits.addItem(scaleUnit.name());
		}
		cmbScaleUnits.setSelectedIndex(1);

		for (FIRImageCompressionAlgorithm algorithm : FIRImageCompressionAlgorithm.values()) {
			cmbCompressionAlgorithm.addItem(algorithm.name());
		}
		cmbCompressionAlgorithm.setSelectedIndex(5);
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		cmbScaleUnits = new JComboBox();
		cmbScaleUnits.setPreferredSize(new Dimension(150, 21));

		cmbCompressionAlgorithm = new JComboBox();
		cmbCompressionAlgorithm.setPreferredSize(new Dimension(100, 21));

		JPanel mainPanel = getMainPanel();
		getMainPanelLayout().rowHeights = new int[] {25, 25, 25};

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 1;
		mainPanel.add(new JLabel("Scale units:"), c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		mainPanel.add(cmbScaleUnits, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		mainPanel.add(new JLabel("Image compression algorithm:"), c);

		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		mainPanel.add(cmbCompressionAlgorithm, c);

		mainPanel.setBounds(5, 9, 260, 80);
		getButtonPanel().setBounds(0, 95, 265, 25);
		this.pack();
	}

	// ==============================================
	// Protected methods
	// ==============================================

	@Override
	protected void addOrCreate() {
		getOwnerFrameListener().addOrCreateFinger(getScaleUnits(), getImageCompressionAlgorithm(), getFingerPosition());
	}

	// ==============================================
	// Public getters and setters
	// ==============================================

	public BDIFScaleUnits getScaleUnits() {
		return BDIFScaleUnits.valueOf((String) cmbScaleUnits.getSelectedItem());
	}

	public void setScaleUnits(BDIFScaleUnits value) {
		cmbScaleUnits.setSelectedItem(value.name());
	}

	public FIRImageCompressionAlgorithm getImageCompressionAlgorithm() {
		return FIRImageCompressionAlgorithm.valueOf((String) cmbCompressionAlgorithm.getSelectedItem());
	}

	public void setImageCompressionAlgorithm(FIRImageCompressionAlgorithm value) {
		cmbCompressionAlgorithm.setSelectedItem(value.name());
	}

}
