package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.BDIFFPPosition;
import com.neurotec.biometrics.standards.FMRFinger;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public final class AddFingerForm extends JDialog implements KeyListener, ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 509599592429941733L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JComboBox cmbFingerPosition;
	private JTextField txtFingerViewsCount;
	private JButton cmdOk;
	private JButton cmdCancel;

	// ==============================================
	// Private fields
	// ==============================================

	private final MainFrame mainFrame;

	// ==============================================
	// Public constructor
	// ==============================================

	public AddFingerForm(MainFrame frame) {
		super();
		mainFrame = frame;
		setResizable(false);

		initializeComponents();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2	- getSize().height / 2);
		setTitle("Add Finger Form");
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {

		JLabel lblFingerPosition = new JLabel("Finger Position");
		JLabel lblFingerViewCount = new JLabel("FingerViews Count");

		lblFingerViewCount.setText(lblFingerViewCount.getText() + "(max: " + FMRFinger.MAX_FINGER_VIEW_COUNT + " )");
		cmbFingerPosition = new JComboBox(BDIFFPPosition.values());
		cmbFingerPosition.setSelectedIndex(0);
		txtFingerViewsCount = new JTextField("1");
		txtFingerViewsCount.addKeyListener(this);
		cmdOk = new JButton("Ok");
		cmdOk.addActionListener(this);
		cmdCancel = new JButton("Cancel");
		cmdCancel.addActionListener(this);

		setLayout(new GridLayout(3, 2, 5, 5));
		add(lblFingerPosition);
		add(cmbFingerPosition);
		add(lblFingerViewCount);
		add(txtFingerViewsCount);
		add(cmdOk);
		add(cmdCancel);

		pack();
	}

	// ==============================================
	// Public methods
	// ==============================================

	public void keyPressed(KeyEvent e) {
		// Do nothing.
	}

	public void keyReleased(KeyEvent e) {
		if (e.getSource() == txtFingerViewsCount) {
			if (!Character.isDigit(e.getKeyChar())) {
				Toolkit.getDefaultToolkit().beep();
				String s = txtFingerViewsCount.getText();
				s = s.substring(0, s.length() - 1);
				txtFingerViewsCount.setText(s);
			}
		}
	}

	public void keyTyped(KeyEvent e) {
		// Do nothing.
	}

	public void actionPerformed(ActionEvent actionevent) {
		Object source = actionevent.getSource();
		if (source == cmdOk) {
			mainFrame.addFinger(getFingerPosition(), getFingerViewCount());
			dispose();
		} else if (source == cmdCancel) {
			dispose();
		}
	}

	public BDIFFPPosition getFingerPosition() {
		return BDIFFPPosition.valueOf(cmbFingerPosition.getSelectedItem().toString());
	}

	public String getFingerViewCount() {
		return txtFingerViewsCount.getText();
	}

}
