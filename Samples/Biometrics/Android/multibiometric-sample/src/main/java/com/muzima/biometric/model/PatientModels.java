package com.muzima.biometric.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PatientModels implements Serializable {
    static final long serialVersionUID =8241935396854275493L;
    private ArrayList<PatientModel> patientModels;

    public PatientModels(ArrayList<PatientModel> patientModels) {
        this.patientModels = patientModels;
    }

    public ArrayList<PatientModel> getPatientModels() {
        return patientModels;
    }

    public void setPatientModels(ArrayList<PatientModel> patientModels) {
        this.patientModels = patientModels;
    }
}
