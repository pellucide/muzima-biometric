package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.nio.ByteBuffer;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.neurotec.biometrics.standards.ANImageColorSpace;
import com.neurotec.biometrics.standards.ANImageCompressionAlgorithm;
import com.neurotec.biometrics.standards.ANImageType;
import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.BDIFScaleUnits;
import com.neurotec.images.NImage;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;

public final class ANType10RecordCreationFrame extends ANRecordCreationFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JComboBox cmbImageType;
	private JTextField txtSmt;
	private ImageLoaderPanel loaderPanel;

	// ==============================================
	// Public constructor
	// ==============================================

	public ANType10RecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		setPreferredSize(new Dimension(320, 580));
		setTitle("Add Type-10 ANRecord");
		initializeComponents();

		for (ANImageType value : ANImageType.values()) {
			cmbImageType.addItem(value);
		}
		cmbImageType.setSelectedIndex(0);
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {

		JPanel type10Panel = new JPanel();
		GridLayout type10Layout = new GridLayout(2, 2);
		type10Layout.setHgap(4);
		type10Layout.setVgap(4);
		type10Panel.setLayout(type10Layout);

		cmbImageType = new JComboBox();
		txtSmt = new JTextField();

		type10Panel.add(new JLabel("Image type:"));
		type10Panel.add(cmbImageType);
		type10Panel.add(new JLabel("Smt:"));
		type10Panel.add(txtSmt);

		loaderPanel = new ImageLoaderPanel();
		loaderPanel.setColorSpace(ANImageColorSpace.UNSPECIFIED);
		loaderPanel.setCompressionAlgorithm(ANImageCompressionAlgorithm.NONE);
		loaderPanel.setHasBpx(false);
		loaderPanel.setHasColorSpace(true);
		loaderPanel.setHll(0);
		loaderPanel.setHps(0);
		loaderPanel.setScaleUnits(BDIFScaleUnits.NONE);
		loaderPanel.setSrc("");
		loaderPanel.setVll(0);
		loaderPanel.setVps(0);

		Container contentPane = getContentPane();
		contentPane.add(type10Panel);
		contentPane.add(loaderPanel);
		type10Panel.setBounds(5, 60, 305, 50);
		loaderPanel.setBounds(5, 114, 305, 390);
		getButtonPanel().setBounds(0, 520, 310, 25);
		pack();

	}

	private ANImageType getImageType() {
		return (ANImageType) cmbImageType.getSelectedItem();
	}

	private String getSmt() {
		return txtSmt.getText();
	}

	// ==============================================
	// Protected overridden methods
	// ==============================================

	@Override
	protected ANRecord onCreateRecord(ANTemplate template) {
		String source = loaderPanel.getSrc();
		if (source != null) {
			if (loaderPanel.isCreateFromImage()) {
				NImage image = loaderPanel.getImage();
				if (image != null) {
					return getTemplate().getRecords().addType10(getIdc(), getImageType(), source, loaderPanel.getScaleUnits(), loaderPanel.getCompressionAlgorithm(), getSmt(), image);
				}
			} else {
				byte[] imageData = loaderPanel.getImageData();
				if (imageData != null) {
					return getTemplate().getRecords().addType10(getIdc(), getImageType(), source, (short) loaderPanel.getHll(), (short) loaderPanel.getVll(), loaderPanel.getScaleUnits(), (short) loaderPanel.getHps(), (short) loaderPanel.getVps(), loaderPanel.getCompressionAlgorithm(), loaderPanel.getColorSpace(), getSmt(), ByteBuffer.wrap(imageData));
				}
			}
		}
		return null;
	}

}
