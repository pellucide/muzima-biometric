package com.neurotec.samples.biometrics.standards;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.neurotec.samples.biometrics.standards.events.ItemChangeListener;

public final class ItemFrame extends JDialog implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private fields
	// ==============================================

	private ItemChangeListener listener;
	private FieldFrameOperation operation;
	private JDialog owner;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JButton btnOK;
	private JButton btnCancel;

	private JTextArea txtValue;

	// ==============================================
	// Public constructor
	// ==============================================

	public ItemFrame(JDialog owner, ItemChangeListener listener) {
		super(owner, "Edit Item", true);
		this.owner = owner;
		this.listener = listener;
		setPreferredSize(new Dimension(310, 205));
		setMinimumSize(new Dimension(300, 200));
		initializeComponents();
		onIsReadOnlyChanged();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		txtValue = new JTextArea();

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		buttonPanel.add(Box.createGlue());
		buttonPanel.add(btnOK);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(btnCancel);

		mainPanel.add(new JLabel("Value:"), BorderLayout.BEFORE_FIRST_LINE);
		mainPanel.add(txtValue, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.AFTER_LAST_LINE);

		getContentPane().add(mainPanel);
		pack();
	}

	private void onIsReadOnlyChanged() {
		btnOK.setVisible(txtValue.isEditable());
		btnCancel.setText(txtValue.isEditable() ? "Cancel" : "Close");
	}

	// ==============================================
	// Public methods
	// ==============================================

	public String getValue() {
		return txtValue.getText();
	}

	public void setValue(String value) {
		txtValue.setText(value);
	}

	public boolean isReadOnly() {
		return !txtValue.isEditable();
	}

	public void setReadOnly(boolean value) {
		txtValue.setEditable(!value);
		onIsReadOnlyChanged();
	}

	public void showItemFrame(FieldFrameOperation operation) {
		this.operation = operation;
		setLocationRelativeTo(owner);
		setVisible(true);
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnOK) {
			listener.itemChanged(operation, getValue());
			dispose();
		} else if (source == btnCancel) {
			dispose();
		}
	}

}
