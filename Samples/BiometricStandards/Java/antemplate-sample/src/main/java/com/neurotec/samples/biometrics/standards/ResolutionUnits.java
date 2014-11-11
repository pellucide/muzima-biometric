package com.neurotec.samples.biometrics.standards;

import java.util.ArrayList;
import java.util.List;

public final class ResolutionUnits {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final Unit PPM_UNIT = new Unit("ppm", 1.0);
	private static final Unit PPCM_UNIT = new Unit("ppcm", 0.01);
	private static final Unit PPMM_UNIT = new Unit("ppmm", 0.001);
	private static final Unit PPI_UNIT = new Unit("ppi", 0.0254);
	private static final List<Unit> RESOLUTION_UNITS = new ArrayList<Unit>();
	private static ResolutionUnits self;

	// ==============================================
	// static initializer
	// ==============================================

	static {
		RESOLUTION_UNITS.add(PPM_UNIT);
		RESOLUTION_UNITS.add(PPCM_UNIT);
		RESOLUTION_UNITS.add(PPMM_UNIT);
		RESOLUTION_UNITS.add(PPI_UNIT);
	}

	// ==============================================
	// Public static methods
	// ==============================================

	public static ResolutionUnits getResolutionUnits() {
		synchronized (ResolutionUnits.class) {
			if (self == null) {
				self = new ResolutionUnits();
			}
			return self;
		}
	}

	// ==============================================
	// Private constructor
	// ==============================================
	private ResolutionUnits() {

	}

	// ==============================================
	// Public methods
	// ==============================================

	public List<Unit> getUnits() {
		return RESOLUTION_UNITS;
	}

	public Unit getPpmUnit() {
		return PPM_UNIT;
	}

	public Unit getPpcmUnit() {
		return PPCM_UNIT;
	}

	public Unit getPpmmUnit() {
		return PPMM_UNIT;
	}

	public Unit getPpiUnit() {
		return PPI_UNIT;
	}

	// ==============================================
	// Static class
	// ==============================================

	public static final class Unit {

		// ==============================================
		// Public static methods
		// ==============================================

		public static double convert(Unit sourceUnit, Unit destinationUnit, double value) {
			return value * destinationUnit.relationWithMeter / sourceUnit.relationWithMeter;
		}

		// ==============================================
		// Private fields
		// ==============================================

		private final String name;
		private final double relationWithMeter;

		// ==============================================
		// Package private constructor
		// ==============================================

		Unit(String name, double relationWithMeter) {
			this.name = name;
			this.relationWithMeter = relationWithMeter;
		}

		// ==============================================
		// Overridden methods
		// ==============================================

		@Override
		public String toString() {
			return name;
		}

	}

}
