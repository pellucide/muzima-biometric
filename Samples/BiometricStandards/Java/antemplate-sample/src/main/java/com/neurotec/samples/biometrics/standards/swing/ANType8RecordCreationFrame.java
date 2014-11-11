package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.nio.ByteBuffer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.neurotec.biometrics.standards.ANPenVector;
import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANSignatureRepresentationType;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.samples.biometrics.standards.CreateANPenVectorArrayFrame;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;
import com.neurotec.samples.biometrics.standards.events.PenVectorCreationListener;

public final class ANType8RecordCreationFrame extends ANImageBinaryRecordCreationFrame implements PenVectorCreationListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private fields
	// ==============================================

	private ANPenVector[] vectors = new ANPenVector[0];

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JPanel fromVectorsPanel;
	private JRadioButton radioFromVectors;
	private ResolutionEditBox resEditBox;
	private JButton btnEdit;
	private JLabel lblResolution;
	private JLabel lblVectors;

	// ==============================================
	// Public constructor
	// ==============================================

	public ANType8RecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		setPreferredSize(new Dimension(330, 595));
		setTitle("Add Type-8 ANRecord");
		initializeComponents();

		setLowResolution(false);
		setFromVectorsIr(getIsrValue());

		enableFromVectorPanel(radioFromVectors.isSelected());
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		radioFromVectors = new JRadioButton("From vectors:");
		radioFromVectors.addActionListener(this);
		getGroup().add(radioFromVectors);

		fromVectorsPanel = new JPanel();
		GridBagLayout vectorsPanelLayout = new GridBagLayout();
		vectorsPanelLayout.columnWidths = new int[] { 120, 75, 75 };
		fromVectorsPanel.setLayout(vectorsPanelLayout);

		lblResolution = new JLabel("Image resolution:");
		lblVectors = new JLabel("Vectors:");
		resEditBox = new ResolutionEditBox();
		btnEdit = new JButton("Edit...");
		btnEdit.addActionListener(this);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 2, 2, 2);

		c.gridx = 0;
		c.gridy = 0;
		fromVectorsPanel.add(lblResolution, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		fromVectorsPanel.add(resEditBox, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		fromVectorsPanel.add(lblVectors, c);

		c.gridx = 1;
		c.gridy = 1;
		fromVectorsPanel.add(btnEdit, c);

		JPanel binaryPanel = getBinaryPanel();
		binaryPanel.add(Box.createVerticalStrut(15));
		binaryPanel.add(radioFromVectors);
		radioFromVectors.setAlignmentX(LEFT_ALIGNMENT);
		binaryPanel.add(fromVectorsPanel);
		fromVectorsPanel.setAlignmentX(LEFT_ALIGNMENT);

		binaryPanel.setBounds(10, 65, 300, 410);
		getButtonPanel().setBounds(10, 530, 300, 25);
		pack();
	}

	private void enableFromVectorPanel(boolean enabled) {
		lblResolution.setEnabled(enabled);
		resEditBox.setEnabled(enabled);
		lblVectors.setEnabled(enabled);
		btnEdit.setEnabled(enabled);
	}

	// ==============================================
	// Protected overridden methods
	// ==============================================

	@Override
	protected ANRecord onCreateRecord(ANTemplate template) {
		if (isCreateFromImage()) {
			return template.getRecords().addType8(getIdc(), (ANSignatureRepresentationType) getCompressionAlgorithm(), isIsrFlagUp(), getIsrValue(), getImage());
		} else if (isCreateFromVector()) {
			return template.getRecords().addType8(getIdc(), getIsrValue(), getFromVectorsIr(), getVectors());
		} else {
			byte[] imageData = getImageData();
			if (imageData != null) {
				return template.getRecords().addType8(getIdc(), (ANSignatureRepresentationType) getCompressionAlgorithm(), isIsrFlagUp(), getIsrValue(), getIr(), (short) getHll(), (short) getVll(), ByteBuffer.wrap(imageData));
			}
		}
		return null;
	}

	@Override
	protected Class<?> getCompressionFormatsType() {
		return ANSignatureRepresentationType.class;
	}

	@Override
	protected void enablePanels() {
		super.enablePanels();
		enableFromVectorPanel(radioFromVectors.isSelected());
	}

	// ==============================================
	// Protected methods
	// ==============================================

	protected boolean isCreateFromVector() {
		return radioFromVectors.isSelected();
	}

	protected int getFromVectorsIr() {
		return (int) Math.round(resEditBox.getPpmValue());
	}

	protected void setFromVectorsIr(int value) {
		resEditBox.setPpmValue(value);
	}

	protected ANPenVector[] getVectors() {
		if (vectors != null) {
			return vectors.clone();
		}
		return null;
	}

	protected void setVectors(ANPenVector[] value) {
		if (value != null) {
			vectors = value.clone();
		} else {
			vectors = null;
		}
	}

	// ==============================================
	// Public methods
	// ==============================================

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == radioFromVectors) {
			enablePanels();
		} else if (source == btnEdit) {
			CreateANPenVectorArrayFrame dialog = new CreateANPenVectorArrayFrame(this, this);
			dialog.setVectors(getVectors());
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
		}
		super.actionPerformed(e);
	}

	// ====================================================
	// Public interface methods PenVectorCreationListener
	// ====================================================

	public void vectorsCreated(ANPenVector[] vectors) {
		setVectors(vectors);
	}

}
