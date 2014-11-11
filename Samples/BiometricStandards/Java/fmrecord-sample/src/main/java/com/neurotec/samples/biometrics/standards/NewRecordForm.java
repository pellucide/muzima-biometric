package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.BDIFStandard;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

public final class NewRecordForm extends JDialog implements KeyListener, ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = -5831585798742221550L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JButton btnOk;
	private JButton btnCancel;
	private JLabel lblVSize;
	private JLabel lblVResolution;
	private JLabel lblHSize;
	private JLabel lblHResolution;
	private JTextField textVSize;
	private JTextField textHSize;
	private JTextField textVResolution;
	private JTextField textHResolution;
	private JComboBox cmbStandard;
	private JLabel lblStandred;
	private final MainFrame frame;

	// ==============================================
	// Public constructor
	// ==============================================

	public NewRecordForm(MainFrame owner, String title) {
		super();
		setResizable(false);
		initializeComponents();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2	- getSize().height / 2);
		setTitle(title);
		frame = owner;
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {

		// btnOk
		btnOk = new JButton("OK");
		btnOk.addActionListener(this);

		// btnCancel
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		// labels
		lblVSize = new JLabel("Vertical Size");
		lblHSize = new JLabel("Horizontal Size");
		lblVResolution = new JLabel("Vertical Resolution");
		lblHResolution = new JLabel("Horizontal Resolution");
		lblStandred = new JLabel("Standard");

		// textVSize
		textVSize = new JTextField();
		textVSize.addKeyListener(this);
		textVSize.setText("400");

		// textHSize
		textHSize = new JTextField();
		textHSize.addKeyListener(this);
		textHSize.setText("400");

		// textVResolution
		textVResolution = new JTextField();
		textVResolution.addKeyListener(this);
		textVResolution.setText("500");

		// textHResolution
		textHResolution = new JTextField();
		textHResolution.addKeyListener(this);
		textHResolution.setText("500");

		// cmbStandard
		cmbStandard = new JComboBox();
		cmbStandard.addItem("ANSI");
		cmbStandard.addItem("ISO");
		cmbStandard.setSelectedIndex(0);

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JPanel settings = new JPanel();
		settings.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		settings.setLayout(new GridLayout(2, 4, 5, 5));
		settings.add(lblVSize);
		settings.add(textVSize);
		settings.add(lblHSize);
		settings.add(textHSize);
		settings.add(lblVResolution);
		settings.add(textVResolution);
		settings.add(lblHResolution);
		settings.add(textHResolution);

		mainSplitPane.setLeftComponent(settings);

		JPanel buttons = new JPanel();

		buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttons.setLayout(new GridLayout(1, 4, 5, 5));
		buttons.add(lblStandred);
		buttons.add(cmbStandard);
		buttons.add(btnOk);
		buttons.add(btnCancel);

		mainSplitPane.setRightComponent(buttons);
		add(mainSplitPane);

		pack();
	}

	// ==============================================
	// Public methods
	// ==============================================

	public void keyPressed(KeyEvent e) {
		// Do nothing.
	}

	public  void keyReleased(KeyEvent e) {
		// Do nothing.
	}

	public  void keyTyped(KeyEvent e) {
		char c = e.getKeyChar();
		if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
			e.consume();
			Toolkit.getDefaultToolkit().beep();
		}
	}

	public void actionPerformed(ActionEvent ev) {
		Object source = ev.getSource();
		if (source == btnOk) {
			try {
				int hSize = Integer.parseInt(getHSize());
				int vSize = Integer.parseInt(getVSize());
				int vResolution = Integer.parseInt(getVResolution());
				int hResolution = Integer.parseInt(getHResolution());
				frame.menuItemNewFMRecord(hSize, vSize, hResolution, vResolution, getStandred());
				frame.setDialogResultOK(true);
				dispose();
			} catch (NumberFormatException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,	"Parameters can't be parsed!", "FMRecord Editor", JOptionPane.ERROR_MESSAGE);
			}
		} else if (source == btnCancel) {
			frame.setDialogResultOK(false);
			dispose();
		}

	}

	public  String getVSize() {
		return textVSize.getText();
	}

	public  String getHSize() {
		return textHSize.getText();
	}

	public  String getVResolution() {
		return textVResolution.getText();
	}

	public  String getHResolution() {
		return textHResolution.getText();
	}

	public  BDIFStandard getStandred() {
		if (cmbStandard.getSelectedIndex() == 0) {
			return BDIFStandard.ANSI;
		} else {
			return BDIFStandard.ISO;
		}
	}

}
