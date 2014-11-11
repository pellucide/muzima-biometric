package com.neurotec.samples.biometrics.standards.events;

import java.util.EventListener;

import com.neurotec.biometrics.standards.ANPenVector;

public interface PenVectorCreationListener extends EventListener {

	void vectorsCreated(ANPenVector[] vectors);

}
