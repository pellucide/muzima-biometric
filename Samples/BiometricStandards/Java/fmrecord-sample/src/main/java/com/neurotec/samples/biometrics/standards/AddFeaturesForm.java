package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.BDIFFPMinutiaType;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

public final class AddFeaturesForm extends JDialog implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 8647889893984063499L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JLabel lblFeature;
	private JLabel lblType;
	private JButton btnOk;
	private JButton btnCancel;
	private JComboBox cmbFeature;
	private JComboBox cmbType;
	private final MainFrame mainFrame;

	// ==============================================
	// Public constructor
	// ==============================================

	public AddFeaturesForm(MainFrame mainFrame) {
		super(mainFrame, "AddFeatures", true);

		initializeComponents();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2	- getSize().height / 2);
		setResizable(false);

		this.mainFrame = mainFrame;
		setTitle("Add Features Form");
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		GridLayout layout = new GridLayout(3, 2);
		layout.setHgap(5);
		layout.setVgap(5);
		setLayout(layout);

		// cmbFeature
		cmbFeature = new JComboBox();
		cmbFeature.addItem("Minutia");
		cmbFeature.addItem("Core");
		cmbFeature.addItem("Delta");
		cmbFeature.setSelectedIndex(0);
		cmbFeature.addActionListener(this);
		cmbFeature.setSize(110, 21);

		// lblFeature
		lblFeature = new JLabel("Feature");
		lblFeature.setSize(43, 13);

		// btnOk
		btnOk = new JButton("OK");
		btnOk.setSize(75, 23);
		btnOk.addActionListener(this);

		// btnCancel
		btnCancel = new JButton("Cancel");
		btnCancel.setSize(75, 23);
		btnCancel.addActionListener(this);

		// cmbType
		cmbType = new JComboBox();
		cmbType.addItem("End");
		cmbType.addItem("Bifurcation");
		cmbType.addItem("Other");
		cmbType.setSelectedIndex(0);
		cmbType.setSize(110, 21);

		// lblType
		lblType = new JLabel("Type");
		lblType.setSize(34, 13);
		add(lblFeature);
		add(cmbFeature);
		add(lblType);
		add(cmbType);
		add(btnOk);
		add(btnCancel);
		setSize(200, 100);
	}

	// ==============================================
	// Public methods
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cmbFeature) {
			if (cmbFeature.getSelectedIndex() == 0) {
				cmbType.setSelectedIndex(0);
				cmbType.setEnabled(true);
				lblType.setEnabled(true);
			} else {
				cmbType.setSelectedIndex(0);
				cmbType.setEnabled(false);
				lblType.setEnabled(false);
			}
		} else if (e.getSource() == btnOk) {
			mainFrame.updateFeatureFormDetails(cmbFeature.getSelectedIndex(), getMinutiaType());
			mainFrame.setDialogResultOK(true);
			dispose();
		} else if (e.getSource() == btnCancel) {
			mainFrame.setDialogResultOK(false);
			dispose();
		}
	}

	public BDIFFPMinutiaType getMinutiaType() {
		switch (cmbType.getSelectedIndex()) {
		case 0:
			return BDIFFPMinutiaType.END;
		case 1:
			return BDIFFPMinutiaType.BIFURCATION;
		case 2:
			return BDIFFPMinutiaType.OTHER;
		default:
			throw new AssertionError("UNKNOWN");
		}
	}

}
