package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;

public class ANRecordCreationFrame extends JDialog implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Public static fields
	// ==============================================

	public static final int CREATE_RECORD_TYPE_ANY = 0;
	public static final int CREATE_RECORD_TYPE_2 = 1;
	public static final int CREATE_VALIDATE_RECORD = 3;
	public static final int CREATE_RECORD_TYPE_1 = 3;

	// ==============================================
	// Private fields
	// ==============================================

	private ANTemplate template;
	private ANRecord createdRecord;
	private int recordCreationType;
	private MainFrameEventListener listener;
	private Frame owner;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JButton btnOK;
	private JButton btnCancel;
	private JPanel buttonPanel;
	private JSpinner spinnerIdc;

	// ==============================================
	// Public constructor
	// ==============================================

	public ANRecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, "Add ANRecord", true);
		this.owner = owner;
		this.listener = listener;
		setPreferredSize(new Dimension(200, 130));
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		initializeComponents();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		Container contentPane = getContentPane();
		contentPane.setLayout(null);

		JLabel lblIDC = new JLabel("IDC");
		spinnerIdc = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnCancel.setVerifyInputWhenFocusTarget(false);

		buttonPanel.add(Box.createGlue());
		buttonPanel.add(btnOK);
		buttonPanel.add(Box.createHorizontalStrut(3));
		buttonPanel.add(btnCancel);

		contentPane.add(lblIDC);
		contentPane.add(spinnerIdc);
		contentPane.add(buttonPanel);
		lblIDC.setBounds(10, 10, 30, 15);
		spinnerIdc.setBounds(10, 28, 120, 20);
		buttonPanel.setBounds(10, 70, 170, 25);
		pack();
	}

	// ==============================================
	// Protected methods
	// ==============================================

	protected ANRecord onCreateRecord(ANTemplate template) {
		return null;
	}

	protected boolean buttonOKActionPerformed() {
		try {
			ANRecord record = onCreateRecord(getTemplate());
			setCreatedRecord(record);
			return listener.newRecordCreated(recordCreationType, getIdc(), record);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.toString());
		}
		return false;
	}

	protected final MainFrameEventListener getEventListener() {
		return listener;
	}

	protected final JPanel getButtonPanel() {
		return buttonPanel;
	}

	protected final JSpinner getSpinnerIdc() {
		return spinnerIdc;
	}

	// ==============================================
	// Public methods
	// ==============================================

	public final ANTemplate getTemplate() {
		return template;
	}

	public final void setTemplate(ANTemplate template) {
		this.template = template;
	}

	public final ANRecord getCreatedRecord() {
		return createdRecord;
	}

	public final void setCreatedRecord(ANRecord createdRecord) {
		this.createdRecord = createdRecord;
	}

	public final int getIdc() {
		return (Integer) spinnerIdc.getValue();
	}

	public final void setIdc(int value) {
		spinnerIdc.setValue(value);
	}

	public final void showRecordCreationFrame(int type) {
		recordCreationType = type;
		setLocationRelativeTo(owner);
		setVisible(true);
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnOK) {
			if (buttonOKActionPerformed()) {
				this.dispose();
			}
		} else if (source == btnCancel) {
			this.dispose();
		}
	}
}
