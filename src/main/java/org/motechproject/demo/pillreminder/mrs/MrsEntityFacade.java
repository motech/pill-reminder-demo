package org.motechproject.demo.pillreminder.mrs;

import java.util.Date;

import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Convenience class to access MRS entity objects
 */
@Component
public class MrsEntityFacade {

    private static final String DEFAULT_FIRST_NAME = "MOTECH First Name";
    private static final String DEFAULT_LAST_NAME = "MOTECH Last Name";
    private static final String DEFAULT_GENDER = "M";

    private MRSPatientAdapter patientAdapter;
    private MrsUserResolver userResolver;
    private MrsFacilityResolver facilityResolver;

    @Autowired
    public MrsEntityFacade(MRSPatientAdapter patientAdapter, MrsUserResolver userResolver,
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

    public MRSPatient createDumbyPatient(String patientMotechId) {
        MRSPerson person = new MRSPerson();
        person.firstName(DEFAULT_FIRST_NAME);
        person.lastName(DEFAULT_LAST_NAME);
        person.gender(DEFAULT_GENDER);
        person.dateOfBirth(new Date());

        MRSPatient patient = new MRSPatient(patientMotechId, person, facilityResolver.resolveMotechFacility());
        return patientAdapter.savePatient(patient);
    }
}
