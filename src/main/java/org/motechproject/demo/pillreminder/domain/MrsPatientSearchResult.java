package org.motechproject.demo.pillreminder.domain;

import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;

public class MrsPatientSearchResult {

    private String firstName;
    private String lastName;
    private String motechId;

    public void setFieldsFromMrsPerson(MRSPerson person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public static MrsPatientSearchResult fromMrsPatient(MRSPatient findPatientByMotechId) {
        MrsPatientSearchResult result = new MrsPatientSearchResult();
        if (findPatientByMotechId != null) {
            result.firstName = findPatientByMotechId.getPerson().getFirstName();
            result.lastName = findPatientByMotechId.getPerson().getLastName();
            result.motechId = findPatientByMotechId.getMotechId();
        }
        return result;
    }

}
