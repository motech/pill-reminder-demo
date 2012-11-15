package org.motechproject.demo.pillreminder.mrs;

import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MrsEntityFinder {

    private MRSPatientAdapter patientAdapter;
    private MrsUserResolver userResolver;
    private MrsFacilityResolver facilityResolver;

    @Autowired
    public MrsEntityFinder(MRSPatientAdapter patientAdapter, MrsUserResolver userResolver,
            MrsFacilityResolver facilityResolver) {
        this.patientAdapter = patientAdapter;
        this.userResolver = userResolver;
        this.facilityResolver = facilityResolver;
    }

    public MRSUser findMotechUser() {
        return userResolver.resolveMotechUser();
    }

    public MRSFacility findMotechFacility() {
        return facilityResolver.resolveMotechFacility();
    }

    public MRSPatient findPatientByMotechId(String motechId) {
        return patientAdapter.getPatientByMotechId(motechId);
    }
}
