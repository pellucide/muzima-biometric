package com.neurotec.samples.biometrics.standards;

import java.util.ArrayList;

public final class FIPropertiesTable extends ArrayList<String> {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Public constructor
	// ==============================================

	public FIPropertiesTable() {
		super();
		loadProperties();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void loadProperties() {
		this.add("captureDeviceId");
		this.add("cbeffProductId");
		this.add("fingers");
		this.add("fingerViews");
		this.add("flags");
		this.add("horzImageResolution");
		this.add("horzScanResolution");
		this.add("imageAcqusitionLevel");
		this.add("imageCompressionAlgorithm");
		this.add("pixelDepth");
		this.add("scaleUnits");
		this.add("standard");
		this.add("vertImageResolution");
		this.add("vertScanResolution");
		this.add("fingerViews");
		this.add("owner");
		this.add("position");
		this.add("horzLineLength");
		this.add("imageData");
		this.add("imageDataLength");
		this.add("imageQuality");
		this.add("impressionType");
		this.add("vertLineLength");
		this.add("viewCount");
		this.add("viewNumber");
	}

}
