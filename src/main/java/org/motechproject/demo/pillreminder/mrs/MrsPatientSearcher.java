package org.motechproject.demo.pillreminder.mrs;

import org.motechproject.demo.pillreminder.domain.MrsPatientSearchResult;
import org.motechproject.mrs.model.MRSPatient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MrsPatientSearcher {

    private MrsEntityFinder mrsEntityFinder;

    @Autowired
    public MrsPatientSearcher(MrsEntityFinder mrsEntityFinder) {
        this.mrsEntityFinder = mrsEntityFinder;
    }

    public MrsPatientSearchResult searchForPatientsWithMotechId(String motechId) {
        MRSPatient patient = mrsEntityFinder.findPatientByMotechId(motechId);
        return createPatientSearchResult(patient);
    }

    private MrsPatientSearchResult createPatientSearchResult(MRSPatient patient) {
        MrsPatientSearchResult result = new MrsPatientSearchResult();
        if (patient != null) {
            result.setFieldsFromMrsPerson(patient.getPerson());
            result.setMotechId(patient.getMotechId());
        }
        return result;
    }

}
