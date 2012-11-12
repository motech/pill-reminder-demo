package org.motechproject.demo.pillreminder.domain;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentResponse {

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

}
