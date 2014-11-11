package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Frame;
import java.nio.ByteBuffer;

import com.neurotec.biometrics.standards.ANBinaryImageCompressionAlgorithm;
import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;

public final class ANType5RecordCreationFrame extends ANImageBinaryRecordCreationFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Public constructor
	// ==============================================

	public ANType5RecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		setTitle("Add Type-5 ANRecord");
		setLowResolution(true);
	}

	// ==============================================
	// Protected overridden methods
	// ==============================================

	@Override
	protected ANRecord onCreateRecord(ANTemplate template) {
		if (isCreateFromImage()) {
			return template.getRecords().addType5(getIdc(), isIsrFlagUp(), getIsrValue(), (ANBinaryImageCompressionAlgorithm) getCompressionAlgorithm(), getImage(), 0);
		} else {
			byte[] imageData = getImageData();
			if (imageData != null) {
				return template.getRecords().addType5(getIdc(), isIsrFlagUp(), getIsrValue(), getIr(), (short) getHll(), (short) getVll(), (ANBinaryImageCompressionAlgorithm) getCompressionAlgorithm(), (byte) getVendorCA(), ByteBuffer.wrap(imageData));
			}
		}
		return null;
	}

	@Override
	protected Class<?> getCompressionFormatsType() {
		return ANBinaryImageCompressionAlgorithm.class;
	}

}
