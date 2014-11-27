package com.muzima.biometric.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PatientModel implements Serializable {
    static final long serialVersionUID =8241935396854275492L;
    private String id;
    private String templateBuffer;

    public PatientModel(String id, String templateBuffer) {
        this.id = id;
        this.templateBuffer = templateBuffer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateBuffer() {
        return templateBuffer;
    }

    public void setTemplateBuffer(String templateBuffer) {
        this.templateBuffer = templateBuffer;
    }
}

