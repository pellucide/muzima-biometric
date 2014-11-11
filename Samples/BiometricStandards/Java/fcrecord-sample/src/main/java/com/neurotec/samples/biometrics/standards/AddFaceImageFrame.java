package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.FCRFaceImageType;
import com.neurotec.biometrics.standards.FCRImageDataType;

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

public class AddFaceImageFrame extends JDialog implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JComboBox cmbFaceImageType;
	private JComboBox cmbImageDataType;
	private JButton btnOK;
	private JButton btnCancel;
	private JPanel comboPanel;
	private JPanel buttonPanel;

	// ==============================================
	// Private fields
	// ==============================================

	private final MainFrameEventListener listener;
	private GridBagLayout comboPanelLayout;

	// ==============================================
	// Public constructor
	// ==============================================

	public AddFaceImageFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, "Add face from image", true);
		this.listener = listener;
		this.setPreferredSize(new Dimension(295, 180));
		this.setResizable(false);
		initializeComponents();

		FCRFaceImageType[] imageTypes = FCRFaceImageType.values();
		for (FCRFaceImageType imageType : imageTypes) {
			cmbFaceImageType.addItem(imageType.name());
		}

		FCRImageDataType[] imageDataTypes = FCRImageDataType.values();
		for (FCRImageDataType imageDataType : imageDataTypes) {
			cmbImageDataType.addItem(imageDataType.name());
		}

		cmbFaceImageType.setSelectedIndex(0);
		cmbImageDataType.setSelectedIndex(0);
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		Container contentPane = this.getContentPane();
		contentPane.setLayout(null);

		cmbFaceImageType = new JComboBox();
		cmbImageDataType = new JComboBox();

		comboPanelLayout = new GridBagLayout();
		comboPanelLayout.columnWidths = new int[] {130, 110};
		comboPanelLayout.rowHeights = new int[] {20, 25, 25, 20};
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		comboPanel = new JPanel(comboPanelLayout);
		comboPanel.setBorder(BorderFactory.createTitledBorder(""));

		c.gridx = 0;
		c.gridy = 1;
		comboPanel.add(new JLabel("Face image type:"), c);

		c.gridx = 1;
		c.gridy = 1;
		comboPanel.add(cmbFaceImageType, c);

		c.gridx = 0;
		c.gridy = 2;
		comboPanel.add(new JLabel("Face image data type:"), c);

		c.gridx = 1;
		c.gridy = 2;
		comboPanel.add(cmbImageDataType, c);

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
		comboPanel.setBounds(12, 5, 265, 101);
		buttonPanel.setBounds(12, 115, 265, 25);
		this.pack();

	}

	// ==============================================
	// Protected methods
	// ==============================================

	protected void buttonOKActionPerformed() {
		listener.addImage(getFaceImageType(), getImageDataType());
	}

	// ==============================================
	// Public getters and setters
	// ==============================================

	public final FCRFaceImageType getFaceImageType() {
		return FCRFaceImageType.valueOf((String) cmbFaceImageType.getSelectedItem());
	}

	public final void setFaceImageType(FCRFaceImageType faceImageType) {
		cmbFaceImageType.setSelectedItem(faceImageType.name());
	}

	public final FCRImageDataType getImageDataType() {
		return FCRImageDataType.valueOf((String) cmbImageDataType.getSelectedItem());
	}

	public final void setImageDataType(FCRImageDataType imageDataType) {
		cmbImageDataType.setSelectedItem(imageDataType.name());
	}

	public final JPanel getComboPanel() {
		return comboPanel;
	}

	public final JPanel getButtonPanel() {
		return buttonPanel;
	}

	public final MainFrameEventListener getOwnerFrameListener() {
		return listener;
	}

	public final GridBagLayout getComboPanelLayout() {
		return comboPanelLayout;
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnOK) {
			buttonOKActionPerformed();
			this.dispose();
		} else if (source == btnCancel) {
			this.dispose();
		}
	}

}
