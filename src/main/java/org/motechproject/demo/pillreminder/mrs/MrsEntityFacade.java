package org.motechproject.demo.pillreminder.mrs;

import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.model.OpenMRSProvider;
import org.motechproject.mrs.services.PatientAdapter;
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

    private PatientAdapter patientAdapter;
    private MrsUserResolver userResolver;
    private MrsFacilityResolver facilityResolver;

    @Autowired
    public MrsEntityFacade(PatientAdapter patientAdapter, MrsUserResolver userResolver,
            MrsFacilityResolver facilityResolver) {
        this.patientAdapter = patientAdapter;
        this.userResolver = userResolver;
        this.facilityResolver = facilityResolver;
    }

    public OpenMRSProvider findMotechUser() {
        return userResolver.resolveMotechUser();
    }

    public OpenMRSFacility findMotechFacility() {
        return facilityResolver.resolveMotechFacility();
    }

    public OpenMRSPatient findPatientByMotechId(String motechId) {
        return (OpenMRSPatient) patientAdapter.getPatientByMotechId(motechId);
    }

    public Patient createDumbyPatient(String patientMotechId) {
        OpenMRSPerson person = new OpenMRSPerson();
        person.firstName(DEFAULT_FIRST_NAME);
        person.lastName(DEFAULT_LAST_NAME);
        person.gender(DEFAULT_GENDER);
        person.setDateOfBirth(DateUtil.now());

        OpenMRSPatient patient = new OpenMRSPatient(patientMotechId, person, facilityResolver.resolveMotechFacility());
        return patientAdapter.savePatient(patient);
    }
}
