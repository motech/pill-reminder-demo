package org.motechproject.demo.pillreminder.domain;


public class PillReminderResponse {

    private String startTime;
    private String lastCapturedDate;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLastCapturedDate() {
        return lastCapturedDate;
    }

    public void setLastCapturedDate(String lastCapturedDate) {
        this.lastCapturedDate = lastCapturedDate;
    }

}
