package org.motechproject.demo.pillreminder.domain;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentResponse {
    private String startTime;

    private List<String> errors = new ArrayList<>();

    public int errorCount() {
        return errors.size();
    }

    public String getError(int i) {
        return errors.get(i);
    }

    public void addError(String string) {
        errors.add(string);
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
