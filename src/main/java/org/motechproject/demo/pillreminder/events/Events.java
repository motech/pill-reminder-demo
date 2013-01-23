package org.motechproject.demo.pillreminder.events;

public interface Events {
    public static final String BASE_SUBJECT = "org.motechproject.demo.pillreminder.";
    
    public static final String PATIENT_TOOK_DOSAGE = BASE_SUBJECT + "PatientTookDosage";
    
    public static final String PATIENT_MISSED_DOSAGE = BASE_SUBJECT + "PatientMissedDosage";

}
