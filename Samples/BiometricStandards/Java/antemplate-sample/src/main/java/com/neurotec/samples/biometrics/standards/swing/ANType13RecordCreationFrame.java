package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.nio.ByteBuffer;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.neurotec.biometrics.standards.ANImageColorSpace;
import com.neurotec.biometrics.standards.ANImageCompressionAlgorithm;
import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.BDIFFPImpressionType;
import com.neurotec.biometrics.standards.BDIFScaleUnits;
import com.neurotec.images.NImage;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;

public final class ANType13RecordCreationFrame extends ANRecordCreationFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JComboBox cmbFPImpressionType;
	private ImageLoaderPanel imageLoader;

	// ==============================================
	// Public constructor
	// ==============================================

	public ANType13RecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		setPreferredSize(new Dimension(330, 540));
		setTitle("Add Type-13 ANRecord");
		initializeComponents();
		for (BDIFFPImpressionType value : BDIFFPImpressionType.values()) {
			cmbFPImpressionType.addItem(value);
		}
		cmbFPImpressionType.setSelectedIndex(0);
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {

		JPanel type13Panel = new JPanel();
		GridLayout type13Layout = new GridLayout(1, 2);
		type13Layout.setHgap(4);
		type13Layout.setVgap(4);
		type13Panel.setLayout(type13Layout);

		cmbFPImpressionType = new JComboBox();

		type13Panel.add(new JLabel("FP Impression type:"));
		type13Panel.add(cmbFPImpressionType);

		imageLoader = new ImageLoaderPanel();
		imageLoader.setBpx(0);
		imageLoader.setColorSpace(ANImageColorSpace.UNSPECIFIED);
		imageLoader.setCompressionAlgorithm(ANImageCompressionAlgorithm.NONE);
		imageLoader.setHasBpx(true);
		imageLoader.setHasColorSpace(false);
		imageLoader.setHll(0);
		imageLoader.setHps(0);
		imageLoader.setScaleUnits(BDIFScaleUnits.NONE);
		imageLoader.setSrc("");
		imageLoader.setVll(0);
		imageLoader.setVps(0);

		Container contentPane = getContentPane();
		contentPane.add(type13Panel);
		contentPane.add(imageLoader);
		type13Panel.setBounds(10, 55, 305, 22);
		imageLoader.setBounds(10, 81, 305, 390);
		getButtonPanel().setBounds(0, 480, 310, 25);
		pack();

	}

	private BDIFFPImpressionType getImpressionType() {
		return (BDIFFPImpressionType) cmbFPImpressionType.getSelectedItem();
	}

	// ==============================================
	// Protected overridden methods
	// ==============================================

	@Override
	protected ANRecord onCreateRecord(ANTemplate template) {
		String source = imageLoader.getSrc();
		if (source != null) {
			if (imageLoader.isCreateFromImage()) {
				NImage image = imageLoader.getImage();

				if (image != null) {
					return getTemplate().getRecords().addType13(getIdc(), getImpressionType(), source, imageLoader.getScaleUnits(), imageLoader.getCompressionAlgorithm(), image);
				}
			} else {
				byte[] imageData = imageLoader.getImageData();
				if (imageData != null) {
					return getTemplate().getRecords().addType13(getIdc(), getImpressionType(), source, (short) imageLoader.getHll(), (short) imageLoader.getVll(), imageLoader.getScaleUnits(), (short) imageLoader.getHps(), (short) imageLoader.getVps(), imageLoader.getCompressionAlgorithm(), (byte) imageLoader.getBpx(), ByteBuffer.wrap(imageData));
				}
			}
		}
		return null;
	}

}
