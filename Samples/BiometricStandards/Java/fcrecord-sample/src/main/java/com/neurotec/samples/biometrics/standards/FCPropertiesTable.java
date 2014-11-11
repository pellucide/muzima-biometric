package com.neurotec.samples.biometrics.standards;

import java.util.ArrayList;

public final class FCPropertiesTable extends ArrayList<String> {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Public constructor
	// ==============================================

	public FCPropertiesTable() {
		super();
		loadProperties();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void loadProperties() {
		this.add("deviceType");
		this.add("expression");
		this.add("eyeColor");
		this.add("faceImageType");
		this.add("featurePoints");
		this.add("flags");
		this.add("gender");
		this.add("hairColor");
		this.add("height");
		this.add("imageColorSpace");
		this.add("imageData");
		this.add("imageDataLength");
		this.add("imageDataType");
		this.add("owner");
		this.add("poseAnglePitch");
		this.add("poseAnglePitchRaw");
		this.add("poseAngleRoll");
		this.add("poseAngleRollRaw");
		this.add("poseAngleUncertaintyPitch");
		this.add("poseAngleUncertaintyPitchRaw");
		this.add("poseAngleUncertaintyRoll");
		this.add("poseAngleUncertaintyRollRaw");
		this.add("poseAngleUncertaintyYaw");
		this.add("poseAngleUncertaintyYawRaw");
		this.add("poseAngleYaw");
		this.add("poseAngleYawRaw");
		this.add("properties");
		this.add("quality");
		this.add("sourceType");
		this.add("vendorExpression");
		this.add("vendorImageColorSpace");
		this.add("vendorSourceType");
		this.add("width");
		this.add("faceImages");
		this.add("standard");
	}

}
