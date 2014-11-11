package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.BDIFFPPosition;
import com.neurotec.biometrics.standards.BDIFScaleUnits;
import com.neurotec.biometrics.standards.BDIFStandard;
import com.neurotec.biometrics.standards.FIRImageCompressionAlgorithm;

public interface MainFrameEventListener {
	
	void addOrCreateFinger(BDIFScaleUnits scaleUnits, FIRImageCompressionAlgorithm algorithm, BDIFFPPosition position);
	
	void addFinger(BDIFFPPosition position);
	
	void setDialogResultOK(boolean isDialogResultOK);
	
	void setCurrentStandard(BDIFStandard currentStandard);
	
	void setCurrentFlags(int currentFlags);

}
