package com.neurotec.samples.biometrics.standards;

import java.util.ArrayList;

public final class FNGPropertiesTable extends ArrayList<String> {

	// ==============================================
	// Private static Fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Public constructor
	// ==============================================

	public FNGPropertiesTable() {
		super();
		loadProperties();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void loadProperties() {
		this.add("captureEquipmentCompliance");
		this.add("captureEquipmentId");
		this.add("CBEFFProductId");
		this.add("fingers");
		this.add("fingerViews");
		this.add("flags");
		this.add("resolutionX");
		this.add("resolutionY");
		this.add("sizeX");
		this.add("sizeY");
		this.add("standard");

		this.add("position");
		this.add("fingerViews");
		this.add("owner");

		this.add("cores");
		this.add("deltas");
		this.add("fingerQuality");
		this.add("hasEightNeighborRidgeCounts");
		this.add("hasFourNeighborRidgeCounts");
		this.add("impressionType");
		this.add("Minutiae");
		this.add("MinutiaeEightNeighbors");
		this.add("MinutiaeFourNeighbors");
		this.add("viewCount");
		this.add("viewNumber");

		this.add("delta");
		this.add("core");
		this.add("minutia");

	}

}
