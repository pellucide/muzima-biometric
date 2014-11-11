package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.neurotec.samples.biometrics.standards.ResolutionUnits;

public final class ResolutionEditBox extends JPanel implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private fields
	// ==============================================

	private ResolutionUnits.Unit currentUnit;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JComboBox cmbScaleUnits;
	private JTextField txtValue;

	// ==============================================
	// Public constructor
	// ==============================================

	public ResolutionEditBox() {
		setPreferredSize(new Dimension(215, 25));
		setMaximumSize(new Dimension(215, 25));
		initializeComponents();

		for (ResolutionUnits.Unit u : ResolutionUnits.getResolutionUnits().getUnits()) {
			cmbScaleUnits.addItem(u);
		}
		setRawValue(0);
		currentUnit = ResolutionUnits.getResolutionUnits().getPpmUnit();
		cmbScaleUnits.setSelectedItem(currentUnit);

	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		txtValue = new JTextField();
		cmbScaleUnits = new JComboBox();
		cmbScaleUnits.addActionListener(this);

		add(txtValue);
		add(Box.createHorizontalStrut(3));
		add(cmbScaleUnits);
	}

	// ==============================================
	// Public methods
	// ==============================================

	public double getRawValue() {
		return Double.parseDouble(txtValue.getText());
	}

	private void setRawValue(double value) {
		txtValue.setText(String.format("%d", (int) value));
	}

	public double getPpmValue() {
		return ResolutionUnits.Unit.convert(currentUnit, ResolutionUnits.getResolutionUnits().getPpmUnit(), getRawValue());
	}

	public void setPpmValue(double value) {
		cmbScaleUnits.setSelectedItem(ResolutionUnits.getResolutionUnits().getPpmUnit());
		setRawValue(value);
	}

	public double getPpcmValue() {
		return ResolutionUnits.Unit.convert(currentUnit, ResolutionUnits.getResolutionUnits().getPpcmUnit(), getRawValue());
	}

	public void setPpcmValue(double value) {
		cmbScaleUnits.setSelectedItem(ResolutionUnits.getResolutionUnits().getPpcmUnit());
		setRawValue(value);
	}

	public double getPpmmValue() {
		return ResolutionUnits.Unit.convert(currentUnit, ResolutionUnits.getResolutionUnits().getPpmmUnit(), getRawValue());
	}

	public void setPpmmValue(double value) {
		cmbScaleUnits.setSelectedItem(ResolutionUnits.getResolutionUnits().getPpmmUnit());
		setRawValue(value);
	}

	public double getPpiValue() {
		return ResolutionUnits.Unit.convert(currentUnit, ResolutionUnits.getResolutionUnits().getPpiUnit(), getRawValue());
	}

	public void setPpiValue(double value) {
		cmbScaleUnits.setSelectedItem(ResolutionUnits.getResolutionUnits().getPpiUnit());
		setRawValue(value);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		txtValue.setEnabled(enabled);
		cmbScaleUnits.setEnabled(enabled);
	}

	@Override
	public void setInputVerifier(InputVerifier verifier) {
		super.setInputVerifier(verifier);
		txtValue.setInputVerifier(verifier);
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == cmbScaleUnits) {
			if (currentUnit != null && cmbScaleUnits.getSelectedItem() != currentUnit) {
				double newValue = ResolutionUnits.Unit.convert(currentUnit, (ResolutionUnits.Unit) cmbScaleUnits.getSelectedItem(), getRawValue());
				setRawValue(newValue);
				currentUnit = (ResolutionUnits.Unit) cmbScaleUnits.getSelectedItem();
			}
		}
	}

}
