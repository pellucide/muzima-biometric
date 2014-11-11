package com.neurotec.samples.biometrics.standards;

import java.util.ArrayList;

public final class ANTemplatePropertiesTable extends ArrayList<String> {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Public constructor
	// ==============================================

	public ANTemplatePropertiesTable() {
		super();
		loadProperties();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void loadProperties() {

		add("acquisitionLightingSpectrum");
		add("alternateSegments");
		add("alternateSegmentsVertices");
		add("amputations");

		add("baldSubjectHairColor");
		add("bdbFormatOwner");
		add("bdbFormatType");
		add("biometricCreationDate");
		add("biometricDataQuality");
		add("biometricType");
		add("bitsPerPixel");

		add("colorSpace");
		add("comment");
		add("compressionAlgorithm");
		add("cores");
		add("charsets");

		add("date");
		add("deltas");
		add("destinationAgency");
		add("deviceMonitoringMode");
		add("deviceUniqueIdentifier");
		add("domain");

		add("eyeColor");

		add("facialFeaturePoints");
		add("featureIdentifier");
		add("fingerprintCaptureDate");
		add("flags");

		add("gmt");
		add("guid");

		add("hasMinutiae");
		add("hasMinutiaeRidgeCounts");
		add("hasMinutiaeRidgeCountsIndicator");
		add("headerVersion");
		add("horzLineLength");
		add("horzPixelScale");

		add("idc");
		add("imageProperties");
		add("imageQualityScore");
		add("imageResolution");
		add("imageScanResolution");
		add("imageScanResolutionValue");
		add("imageType");
		add("impressionType");
		add("irisCaptureDate");
		add("irisDiameter");

		add("latentCaptureDate");

		add("make");
		add("makeModelSerialNumber");
		add("minutiae");
		add("minutiaeFormat");
		add("minutiaeNeighbors");
		add("model");

		add("nativeScanningResolution");
		add("ncicDesignationCodes");
		add("nistQualityMetrics");
		add("nominalTransmittingResolution");

		add("ofrs");
		add("originatingAgency");
		add("otherPhotoCharacteristics");

		add("palmprintCaptureDate");
		add("patternClasses");
		add("penVectors");
		add("photoAcquisitionSource");
		add("photoAcquisitionSourceEx");
		add("photoAttributes");
		add("photoDate");
		add("physicalPhotoCharacteristics");
		add("poseOffsetAngle");
		add("positions");
		add("printPositionDescriptor");
		add("printPositions");
		add("priority");

		add("qualityMetrics");

		add("rotationAngle");
		add("rotationAngleUncertainty");

		add("scaleUnits");
		add("scanHorzPixelScale");
		add("scanVertPixelScale");
		add("searchPositionDescriptors");
		add("segmentationQualityMetrics");
		add("segments");
		add("serialNumber");
		add("signatureRepresentation");
		add("signatureType");
		add("smts");
		add("smtsColors");
		add("smtSize");
		add("sourceAgency");
		add("subjectAcquisitionProfile");
		add("subjectEyeColor");
		add("subjectFacialAttributes");
		add("subjectFacialCharacteristics");
		add("subjectFacialExpression");
		add("subjectHairColor");
		add("subjectHairColorEx");
		add("subjectPose");
		add("subjectPoseAngles");
		add("subjectQualityScores");

		add("transactionControl");
		add("transactionControlReference");
		add("transactionType");

		add("userDefinedImage");
		add("userDefinedQualityScore");
		add("userDefinedTestingDate");

		add("validated");
		add("vendorCompressionAlgorithm");
		add("vendorPhotoAcquisitionSource");
		add("vertLineLength");
		add("vertPixelScale");

		add("length");
		add("isValidated");

	}
}
