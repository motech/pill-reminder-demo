package org.motechproject.demo.pillreminder.listener;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.motechproject.demo.pillreminder.Events;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateMrsListener {

    private final MRSPatientAdapter patientAdapter;
    private final MRSEncounterAdapter encounterAdapter;
    private final MRSUserAdapter userAdapter;
    private final MRSFacilityAdapter facilityAdapter;

    @Autowired
    public UpdateMrsListener(MRSPatientAdapter patientAdapter, MRSEncounterAdapter encounterAdapter,
            MRSUserAdapter userAdapter, MRSFacilityAdapter facilityAdapter) {
        this.patientAdapter = patientAdapter;
        this.encounterAdapter = encounterAdapter;
        this.userAdapter = userAdapter;
        this.facilityAdapter = facilityAdapter;
    }

    @MotechListener(subjects = Events.UPDATE_MRS_PATIENT_EVENT)
    public void handleMrsUpdate(MotechEvent event) {
        String motechId = event.getParameters().get("motechId").toString();
        MRSPatient patient = patientAdapter.getPatientByMotechId(motechId);

        MRSUser user = getMotechUser();
        MRSFacility facility = getMotechFacility();

        MRSObservation obs = new MRSObservation(new Date(), "Taken Pill", "YES");
        Set<MRSObservation> allObs = new HashSet<>();
        allObs.add(obs);
        MRSEncounter encounter = new MRSEncounter.MRSEncounterBuilder().withDate(new Date()).withObservations(allObs)
                .withFacility(facility).withEncounterType("PILL REMINDER").withProvider(user.getPerson())
                .withPatient(patient).build();

        encounterAdapter.createEncounter(encounter);
    }

    private MRSFacility getMotechFacility() {
        List<MRSFacility> facilities = facilityAdapter.getFacilities("Motech");
        MRSFacility facility = null;
        if (facilities.isEmpty()) {
            facility = new MRSFacility("Motech", "USA", "King County", "Seattle", "WA");
            facility = facilityAdapter.saveFacility(facility);
        } else {
            facility = facilities.get(0);
        }

        return facility;
    }

    private MRSUser getMotechUser() {
        MRSUser user = userAdapter.getUserByUserName("motech");
        if (user == null) {
            MRSPerson person = new MRSPerson();
            person.firstName("Motech").lastName("Motech").address("None").dateOfBirth(new Date()).gender("F");
            user = new MRSUser();
            user.userName("motech").securityRole("Provider").person(person);
            try {
                user = (MRSUser) userAdapter.saveUser(user).get(MRSUserAdapter.USER_KEY);
            } catch (UserAlreadyExistsException e) {
                // should never happen
                user = userAdapter.getUserByUserName("motech");
            }
        }

        return user;
    }
}
