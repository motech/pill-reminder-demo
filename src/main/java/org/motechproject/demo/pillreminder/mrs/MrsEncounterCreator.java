package org.motechproject.demo.pillreminder.mrs;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class to create new MRS encounters for patients
 */
@Component
public class MrsEncounterCreator {

    private final MrsEntityFacade mrsEntityFacade;
    private final MRSEncounterAdapter encounterAdapter;

    @Autowired
    public MrsEncounterCreator(MrsEntityFacade mrsEntityFacade, MRSEncounterAdapter encounterAdapter) {
        this.mrsEntityFacade = mrsEntityFacade;
        this.encounterAdapter = encounterAdapter;
    }

    public void createPillTakenEncounterForPatient(String motechId) {
        MRSPatient patient = mrsEntityFacade.findPatientByMotechId(motechId);
        Set<MRSObservation> allObs = createObservationGroup();
        MRSEncounter encounter = createEncounter(patient, allObs);

        encounterAdapter.createEncounter(encounter);
    }

    private MRSEncounter createEncounter(MRSPatient patient, Set<MRSObservation> allObs) {
        MRSUser user = mrsEntityFacade.findMotechUser();
        MRSFacility facility = mrsEntityFacade.findMotechFacility();

        MRSEncounter encounter = new MRSEncounter.MRSEncounterBuilder().withDate(new Date()).withObservations(allObs)
                .withFacility(facility).withEncounterType(MrsConstants.PILL_REMINDER_ENCOUNTER_TYPE)
                .withProvider(user.getPerson()).withPatient(patient).build();
        return encounter;
    }

    private Set<MRSObservation> createObservationGroup() {
        MRSObservation obs = new MRSObservation(new Date(), MrsConstants.PILL_TAKEN_CONCEPT_NAME,
                MrsConstants.PILL_TAKEN_CONCEPT_YES_ANSWER);
        Set<MRSObservation> allObs = new HashSet<>();
        allObs.add(obs);
        return allObs;
    }
}
