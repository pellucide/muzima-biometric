package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.BDIFStandard;
import com.neurotec.biometrics.standards.FCRFaceImageType;
import com.neurotec.biometrics.standards.FCRImageColorSpace;
import com.neurotec.biometrics.standards.FCRImageDataType;

public interface MainFrameEventListener {

	void addImage(FCRFaceImageType imageType, FCRImageDataType dataType);

	void addImageFromData(FCRFaceImageType imageType, FCRImageDataType dataType, int width, int height, FCRImageColorSpace imageColorSpace, int vendorImageColorSpace);

	void setDialogResultOK(boolean isDialogResultOK);

	void setCurrentStandard(BDIFStandard currentStandard);

	void setCurrentFlags(int currentFlags);

}
