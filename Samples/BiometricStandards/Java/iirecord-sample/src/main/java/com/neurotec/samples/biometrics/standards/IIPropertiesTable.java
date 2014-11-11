package com.neurotec.samples.biometrics.standards;

import java.util.ArrayList;

public final class IIPropertiesTable extends ArrayList<String> {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Public constructor
	// ==============================================

	public IIPropertiesTable() {
		super();
		loadProperties();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void loadProperties() {
		this.add("deviceUniqueIdentifier");
		this.add("flags");
		this.add("guid");
		this.add("imageFormat");
		this.add("imageTransformation");
		this.add("intensityDepth");
		this.add("irisBoundaryExtraction");
		this.add("irisDiameter");
		this.add("irises");
		this.add("irisImages");
		this.add("irisOcclusionFilling");
		this.add("irisOcclusions");
		this.add("irisScanType");
		this.add("rawImageHeight");
		this.add("rawImageWidth");
		this.add("standard");
		this.add("owner");
		this.add("position");
		this.add("imageCount");
		this.add("imageData");
		this.add("imageDataLength");
		this.add("imageNumber");
		this.add("quality");
		this.add("rotationAngle");
		this.add("rotationAngleRaw");
		this.add("rotationAngleUncertainty");
		this.add("rotationAngleUncertaintyRaw");
	}

}
