package com.neurotec.samples.biometrics.standards;

public enum BDIFOptionsFormMode {

	NEW(1),
	OPEN(2),
	SAVE(3),
	CONVERT(4);

	private int value;

	private BDIFOptionsFormMode(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
