package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.IIRImageFormat;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class AddNewIrisImageFrame extends AddIrisFrame {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final long serialVersionUID = 1L;

	// ===========================================================
	// Private GUI controls
	// ===========================================================

	private JComboBox cmbIrisImageFormat;

	// ===========================================================
	// Public constructor
	// ===========================================================

	public AddNewIrisImageFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		this.setTitle("AddNewIrisImageFrame");
		this.setPreferredSize(new Dimension(295, 140));
		initializeComponents();

		for (IIRImageFormat format : IIRImageFormat.values()) {
			cmbIrisImageFormat.addItem(format.name());
		}
		cmbIrisImageFormat.setSelectedIndex(0);
	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private void initializeComponents() {
		cmbIrisImageFormat = new JComboBox();

		JPanel comboPanel = getComboPanel();
		getComboPanelLayout().rowHeights = new int[] {5, 25, 25};
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 2;
		comboPanel.add(new JLabel("Iris image format:"), c);

		c.gridx = 1;
		c.gridy = 2;
		comboPanel.add(cmbIrisImageFormat, c);

		comboPanel.setBounds(12, 5, 265, 75);
		getButtonPanel().setBounds(12, 85, 265, 25);

		this.pack();
	}

	// ===========================================================
	// Protected overridden methods
	// ===========================================================

	@Override
	protected void buttonOKActionPerformed() {
		getOwnerFrameListener().newFromImage(getIrisImageFormat(), getEyePosition());
	}

	// ===========================================================
	// Public getters and setters
	// ===========================================================

	public IIRImageFormat getIrisImageFormat() {
		return IIRImageFormat.valueOf((String) cmbIrisImageFormat.getSelectedItem());
	}

	public void setIrisImageFormat(IIRImageFormat format) {
		cmbIrisImageFormat.setSelectedItem(format.name());
	}

}
