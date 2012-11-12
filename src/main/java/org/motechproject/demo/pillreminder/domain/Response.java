package org.motechproject.demo.pillreminder.domain;

import java.util.ArrayList;
import java.util.List;

public class Response {

    private String message;
    private List<String> errors = new ArrayList<>();
    
    public void addError(String error) {
        errors.add(error);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
