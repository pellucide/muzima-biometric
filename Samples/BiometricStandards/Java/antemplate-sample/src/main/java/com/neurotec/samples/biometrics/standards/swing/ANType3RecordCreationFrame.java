package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Frame;
import java.nio.ByteBuffer;

import com.neurotec.biometrics.standards.ANImageCompressionAlgorithm;
import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;

public final class ANType3RecordCreationFrame extends ANImageBinaryRecordCreationFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Public constructor
	// ==============================================

	public ANType3RecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		setTitle("Add Type-3 ANRecord");
		setLowResolution(true);
	}

	// ==============================================
	// Overridden protected methods
	// ==============================================

	@Override
	protected ANRecord onCreateRecord(ANTemplate template) {
		if (isCreateFromImage()) {
			return template.getRecords().addType3(getIdc(), isIsrFlagUp(), getIsrValue(), (ANImageCompressionAlgorithm) getCompressionAlgorithm(), getImage(), 0);
		} else {
			byte[] imageData = getImageData();
			if (imageData != null) {
				return template.getRecords().addType3(getIdc(), isIsrFlagUp(), getIsrValue(), getIr(), (short) getHll(), (short) getVll(), (ANImageCompressionAlgorithm) getCompressionAlgorithm(), (byte) getVendorCA(), ByteBuffer.wrap(imageData));
			}
		}
		return null;
	}

	@Override
	protected Class<?> getCompressionFormatsType() {
		return ANImageCompressionAlgorithm.class;
	}

}
