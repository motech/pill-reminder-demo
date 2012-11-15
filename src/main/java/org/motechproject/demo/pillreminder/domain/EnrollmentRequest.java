package org.motechproject.demo.pillreminder.domain;

public class EnrollmentRequest {

    private String motechId;
    private String pin;
    private String phonenumber;
    private String dosageStarTime;

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setDosageStartTime(String dosageStartTime) {
        this.dosageStarTime = dosageStartTime;
    }

    public String getDosageStartTime() {
        return dosageStarTime;
    }
}
