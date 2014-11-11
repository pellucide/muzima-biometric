package com.neurotec.samples.biometrics.standards.events;

import java.util.EventListener;

import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANRecordType;
import com.neurotec.biometrics.standards.ANValidationLevel;
import com.neurotec.samples.biometrics.standards.swing.ANType1RecordCreationFrame;
import com.neurotec.util.NVersion;

public interface MainFrameEventListener extends EventListener {

	void fieldNumberSelected(int fieldNumber);

	void versionChanged(NVersion selectedVersion);

	void recordTypeSelected(ANRecordType recordType);

	boolean newRecordCreated(int type, int idc, ANRecord createdRecord);

	boolean newType1RecordCreated(ANType1RecordCreationFrame type1CreateForm);

	void optionsSelected(boolean isTemplateOpenMode, ANValidationLevel validation, int flags);

}
