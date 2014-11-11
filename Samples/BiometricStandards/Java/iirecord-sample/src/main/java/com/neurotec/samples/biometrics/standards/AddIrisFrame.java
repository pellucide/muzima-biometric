package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.BDIFEyePosition;

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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AddIrisFrame extends JDialog implements ActionListener {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final long serialVersionUID = 1L;

	// ===========================================================
	// Private fields
	// ===========================================================

	private final MainFrameEventListener listener;
	private GridBagLayout comboPanelLayout;

	// ===========================================================
	// Private GUI controls
	// ===========================================================

	private JComboBox cmbIrisPosition;
	private JPanel comboPanel;
	private JPanel buttonPanel;
	private JButton btnOK;
	private JButton btnCancel;

	// ===========================================================
	// Public constructor
	// ===========================================================

	public AddIrisFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, "AddIrisFrame", true);
		this.listener = listener;
		this.setPreferredSize(new Dimension(295, 110));
		this.setResizable(false);
		initializeComponents();

		for (BDIFEyePosition position : BDIFEyePosition.values()) {
			cmbIrisPosition.addItem(position.name());
		}
		cmbIrisPosition.setSelectedIndex(0);

	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private void initializeComponents() {
		Container contentPane = this.getContentPane();
		contentPane.setLayout(null);

		cmbIrisPosition = new JComboBox();

		comboPanelLayout = new GridBagLayout();
		comboPanelLayout.columnWidths = new int[] {130, 110};
		comboPanelLayout.rowHeights = new int[] {5, 25};

		comboPanel = new JPanel(comboPanelLayout);
		comboPanel.setBorder(BorderFactory.createTitledBorder(""));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 1;
		comboPanel.add(new JLabel("Iris position:"), c);

		c.gridx = 1;
		c.gridy = 1;
		comboPanel.add(cmbIrisPosition, c);

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

		contentPane.add(comboPanel);
		contentPane.add(buttonPanel);
		comboPanel.setBounds(12, 5, 265, 45);
		buttonPanel.setBounds(12, 55, 265, 25);
		this.pack();
	}

	// ===========================================================
	// Protected methods
	// ===========================================================

	protected void buttonOKActionPerformed() {
		listener.addFromImage(getEyePosition());
	}

	// ===========================================================
	// Public getters and setters
	// ===========================================================

	public final BDIFEyePosition getEyePosition() {
		return BDIFEyePosition.valueOf((String) cmbIrisPosition.getSelectedItem());
	}

	public final void setEyePosition(BDIFEyePosition position) {
		cmbIrisPosition.setSelectedItem(position.name());
	}

	public final JPanel getComboPanel() {
		return comboPanel;
	}

	public final GridBagLayout getComboPanelLayout() {
		return comboPanelLayout;
	}

	public final JPanel getButtonPanel() {
		return buttonPanel;
	}

	public final MainFrameEventListener getOwnerFrameListener() {
		return listener;
	}

	// ===========================================================
	// Event handling
	// ===========================================================
	
	public final void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnOK) {
			buttonOKActionPerformed();
			this.dispose();
		} else if (source == btnCancel) {
			this.dispose();
		}
	}

}
