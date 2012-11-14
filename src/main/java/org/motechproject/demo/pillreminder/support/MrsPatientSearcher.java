package org.motechproject.demo.pillreminder.support;

import java.util.List;

import org.motechproject.demo.pillreminder.domain.MrsPatientSearchResult;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MrsPatientSearcher {

    private MRSPatientAdapter patientAdapter;

    @Autowired
    public MrsPatientSearcher(MRSPatientAdapter patientAdapter) {
        this.patientAdapter = patientAdapter;
    }
    
    public MrsPatientSearchResult searchForPatientsWithMotechId(String motechId) {
        MRSPatient patient = patientAdapter.getPatientByMotechId(motechId);
        MrsPatientSearchResult result = new MrsPatientSearchResult();
        if (patient != null) {
            result.setFirstName(patient.getPerson().getFirstName());
            result.setLastName(patient.getPerson().getLastName());
            result.setMotechId(patient.getMotechId());
            MRSPerson person = patient.getPerson();
            result.setPin(getPinNumberFromPersonAttribute(person.getAttributes()));
        }
        
        return result;
    }

    private String getPinNumberFromPersonAttribute(List<Attribute> attributes) {
        for(Attribute attr : attributes) {
            if ("pin".equalsIgnoreCase(attr.name())) {
                return attr.value();
            }
        }
        
        return "1234";
    }
}
