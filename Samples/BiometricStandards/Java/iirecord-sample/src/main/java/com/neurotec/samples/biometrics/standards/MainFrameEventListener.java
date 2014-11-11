package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.BDIFEyePosition;
import com.neurotec.biometrics.standards.BDIFStandard;
import com.neurotec.biometrics.standards.IIRImageFormat;

public interface MainFrameEventListener {

	void newFromImage(IIRImageFormat imageFormat, BDIFEyePosition eyePosition);

	void addFromImage(BDIFEyePosition eyePosition);

	void setDialogResultOK(boolean isDialogResultOK);

	void setCurrentStandard(BDIFStandard currentStandard);

	void setCurrentFlags(int currentFlags);

}
