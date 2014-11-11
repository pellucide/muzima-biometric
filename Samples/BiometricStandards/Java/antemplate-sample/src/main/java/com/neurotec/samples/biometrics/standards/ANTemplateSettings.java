package com.neurotec.samples.biometrics.standards;

import java.io.File;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.neurotec.biometrics.standards.ANValidationLevel;
import com.neurotec.samples.util.Utils;

public final class ANTemplateSettings implements Cloneable {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static ANTemplateSettings instance;
	private static ANTemplateSettings defaultInstance;
	private static final String PROJECT_NAME = "antemplate-sample";
	private static final String PROJECT_DATA_FOLDER = Utils.getHomeDirectory() + Utils.FILE_SEPARATOR + ".neurotec" + Utils.FILE_SEPARATOR + PROJECT_NAME;
	private static final String SETTINGS_FILE_PATH = getSettingsFolder() + Utils.FILE_SEPARATOR + "user.xml";

	// ===========================================================
	// Private static methods
	// ===========================================================

	private static String getSettingsFolder() {
		File settingsFolder = new File(PROJECT_DATA_FOLDER);
		if (!settingsFolder.exists()) {
			settingsFolder.mkdirs();
		}
		return settingsFolder.getAbsolutePath();
	}

	// ===========================================================
	// Public static methods
	// ===========================================================

	public static synchronized ANTemplateSettings getDefaultInstance() {
		synchronized (ANTemplateSettings.class) {
			if (defaultInstance == null) {
				defaultInstance = new ANTemplateSettings();
				defaultInstance.loadDefault();
			}
			return defaultInstance;
		}
	}

	public static synchronized ANTemplateSettings getInstance() {
		synchronized (ANTemplateSettings.class) {
			if (instance == null) {
				instance = new ANTemplateSettings();
				instance.load();
			}
			return instance;
		}
	}

	// ===========================================================
	// Private fields
	// ===========================================================

	@Element
	private String lastDirectory;
	@Element
	private ANValidationLevel newValidationLevel;
	@Element
	private ANValidationLevel validationLevel;
	@Element
	private boolean isUseNistMinutiaNeighborsNew;
	@Element
	private boolean isUseNistMinutiaNeighbors;
	@Element
	private boolean isNonStrictRead;
	@Element
	private boolean isMergeDuplicateFields;
	@Element
	private boolean isRecoverFromBinaryData;
	@Element
	private boolean isLeaveInvalidRecordsUnvalidated;
	@Element
	private String lastValidateDirectory;
	@Element
	private boolean isUseTwoDigitIdc;
	@Element
	private boolean isUseTwoDigitIdcNew;
	@Element
	private boolean isUseTwoDigitFieldNumber;
	@Element
	private boolean isUseTwoDigitFieldNumberNew;
	@Element
	private boolean isUseTwoDigitFieldNumberType1;
	@Element
	private boolean isUseTwoDigitFieldNumberType1New;

	// ===========================================================
	// Private constructor
	// ===========================================================

	private ANTemplateSettings() {

	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private void load() {
		try {
			File file = new File(SETTINGS_FILE_PATH);
			if (file.exists()) {
				Serializer serializer = new Persister();
				instance = serializer.read(ANTemplateSettings.class, file);
			} else {
				instance = (ANTemplateSettings) getDefaultInstance().clone();
			}
		} catch (Exception e) {
			try {
				instance = (ANTemplateSettings) getDefaultInstance().clone();
			} catch (CloneNotSupportedException e1) {
				throw new AssertionError("Cannot happen");
			}
		}
	}

	private void loadDefault() {
		setLastDirectory(System.getProperty("user.home"));
		setNewValidationLevel(ANValidationLevel.STANDARD);
		setValidationLevel(ANValidationLevel.STANDARD);
		setUseNistMinutiaNeighborsNew(false);
		setUseNistMinutiaNeighbors(false);
		setNonStrictRead(false);
		setMergeDuplicateFields(false);
		setRecoverFromBinaryData(false);
		setLeaveInvalidRecordsUnvalidated(false);
		setLastValidateDirectory(System.getProperty("user.home"));
		setUseTwoDigitIdc(false);
		setUseTwoDigitIdcNew(false);
		setUseTwoDigitFieldNumber(false);
		setUseTwoDigitFieldNumberNew(false);
		setUseTwoDigitFieldNumberType1(false);
		setUseTwoDigitFieldNumberType1New(false);
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	public void save() {
		Serializer serializer = new Persister();
		File file = new File(SETTINGS_FILE_PATH);
		try {
			serializer.write(this, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getLastDirectory() {
		return lastDirectory;
	}

	public void setLastDirectory(String lastDirectory) {
		this.lastDirectory = lastDirectory;
	}

	public ANValidationLevel getNewValidationLevel() {
		return newValidationLevel;
	}

	public void setNewValidationLevel(ANValidationLevel newValidationLevel) {
		this.newValidationLevel = newValidationLevel;
	}

	public ANValidationLevel getValidationLevel() {
		return validationLevel;
	}

	public void setValidationLevel(ANValidationLevel validationLevel) {
		this.validationLevel = validationLevel;
	}

	public boolean isUseNistMinutiaNeighborsNew() {
		return isUseNistMinutiaNeighborsNew;
	}

	public void setUseNistMinutiaNeighborsNew(boolean isUseNistMinutiaNeighborsNew) {
		this.isUseNistMinutiaNeighborsNew = isUseNistMinutiaNeighborsNew;
	}

	public boolean isUseNistMinutiaNeighbors() {
		return isUseNistMinutiaNeighbors;
	}

	public void setUseNistMinutiaNeighbors(boolean isUseNistMinutiaNeighbors) {
		this.isUseNistMinutiaNeighbors = isUseNistMinutiaNeighbors;
	}

	public boolean isNonStrictRead() {
		return isNonStrictRead;
	}

	public void setNonStrictRead(boolean isNonStrictRead) {
		this.isNonStrictRead = isNonStrictRead;
	}

	public boolean isMergeDuplicateFields() {
		return isMergeDuplicateFields;
	}

	public void setMergeDuplicateFields(boolean isMergeDuplicateFields) {
		this.isMergeDuplicateFields = isMergeDuplicateFields;
	}

	public boolean isRecoverFromBinaryData() {
		return isRecoverFromBinaryData;
	}

	public void setRecoverFromBinaryData(boolean isRecoverFromBinaryData) {
		this.isRecoverFromBinaryData = isRecoverFromBinaryData;
	}

	public boolean isLeaveInvalidRecordsUnvalidated() {
		return isLeaveInvalidRecordsUnvalidated;
	}

	public void setLeaveInvalidRecordsUnvalidated(boolean isLeaveInvalidRecordsUnvalidated) {
		this.isLeaveInvalidRecordsUnvalidated = isLeaveInvalidRecordsUnvalidated;
	}

	public String getLastValidateDirectory() {
		return lastValidateDirectory;
	}

	public void setLastValidateDirectory(String lastValidateDirectory) {
		this.lastValidateDirectory = lastValidateDirectory;
	}

	public boolean isUseTwoDigitIdc() {
		return isUseTwoDigitIdc;
	}

	public void setUseTwoDigitIdc(boolean isUseTwoDigitIdc) {
		this.isUseTwoDigitIdc = isUseTwoDigitIdc;
	}

	public boolean isUseTwoDigitIdcNew() {
		return isUseTwoDigitIdcNew;
	}

	public void setUseTwoDigitIdcNew(boolean isUseTwoDigitIdcNew) {
		this.isUseTwoDigitIdcNew = isUseTwoDigitIdcNew;
	}

	public boolean isUseTwoDigitFieldNumber() {
		return isUseTwoDigitFieldNumber;
	}

	public void setUseTwoDigitFieldNumber(boolean isUseTwoDigitFieldNumber) {
		this.isUseTwoDigitFieldNumber = isUseTwoDigitFieldNumber;
	}

	public boolean isUseTwoDigitFieldNumberNew() {
		return isUseTwoDigitFieldNumberNew;
	}

	public void setUseTwoDigitFieldNumberNew(boolean isUseTwoDigitFieldNumberNew) {
		this.isUseTwoDigitFieldNumberNew = isUseTwoDigitFieldNumberNew;
	}

	public boolean isUseTwoDigitFieldNumberType1() {
		return isUseTwoDigitFieldNumberType1;
	}

	public void setUseTwoDigitFieldNumberType1(boolean isUseTwoDigitFieldNumberType1) {
		this.isUseTwoDigitFieldNumberType1 = isUseTwoDigitFieldNumberType1;
	}

	public boolean isUseTwoDigitFieldNumberType1New() {
		return isUseTwoDigitFieldNumberType1New;
	}

	public void setUseTwoDigitFieldNumberType1New(boolean isUseTwoDigitFieldNumberType1New) {
		this.isUseTwoDigitFieldNumberType1New = isUseTwoDigitFieldNumberType1New;
	}

}
