package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Dimension;
import java.awt.Frame;
import java.nio.ByteBuffer;

import com.neurotec.biometrics.standards.ANImageColorSpace;
import com.neurotec.biometrics.standards.ANImageCompressionAlgorithm;
import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.BDIFScaleUnits;
import com.neurotec.images.NImage;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;

public final class ANType17RecordCreationFrame extends ANRecordCreationFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private ImageLoaderPanel imageLoader;

	// ==============================================
	// Public constructor
	// ==============================================

	public ANType17RecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		setPreferredSize(new Dimension(325, 520));
		setTitle("Add Type-17 ANRecord");
		initializeComponents();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {

		imageLoader = new ImageLoaderPanel();
		imageLoader.setBpx(0);
		imageLoader.setColorSpace(ANImageColorSpace.UNSPECIFIED);
		imageLoader.setCompressionAlgorithm(ANImageCompressionAlgorithm.NONE);
		imageLoader.setHasBpx(true);
		imageLoader.setHasColorSpace(true);
		imageLoader.setHll(0);
		imageLoader.setHps(0);
		imageLoader.setScaleUnits(BDIFScaleUnits.NONE);
		imageLoader.setSrc("");
		imageLoader.setVll(0);
		imageLoader.setVps(0);

		getContentPane().add(imageLoader);
		imageLoader.setBounds(5, 50, 305, 390);
		getButtonPanel().setBounds(0, 460, 310, 25);
		pack();

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
					return getTemplate().getRecords().addType17(getIdc(), source, imageLoader.getScaleUnits(), imageLoader.getCompressionAlgorithm(), image);
				}
			} else {
				byte[] imageData = imageLoader.getImageData();
				if (imageData != null) {
					return getTemplate().getRecords().addType17(getIdc(), source, (short) imageLoader.getHll(), (short) imageLoader.getVll(), imageLoader.getScaleUnits(), (short) imageLoader.getHps(), (short) imageLoader.getVps(), imageLoader.getCompressionAlgorithm(), (byte) imageLoader.getBpx(), imageLoader.getColorSpace(), ByteBuffer.wrap(imageData));
				}
			}
		}
		return null;
	}

}
