package com.neurotec.samples.biometrics.standards.events;

import java.util.EventListener;

import com.neurotec.samples.biometrics.standards.FieldFrameOperation;

public interface ItemChangeListener extends EventListener {

	void itemChanged(FieldFrameOperation operation, String value);

}
