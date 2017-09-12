package com.jcl.jcl_androidapplication;

public class AndroidSensorTab {
    private String type;
    private String value;
    private String device;

    public AndroidSensorTab(String type, String value, String device) {
        this.type = type;
        this.value = value;
        this.device = device;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}