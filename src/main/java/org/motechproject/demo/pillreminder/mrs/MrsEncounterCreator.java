package org.motechproject.demo.pillreminder.mrs;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.motechproject.mrs.domain.Encounter;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.model.OpenMRSEncounter;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.model.OpenMRSObservation;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSProvider;
import org.motechproject.mrs.services.EncounterAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class to create new MRS encounters for patients
 */
@Component
public class MrsEncounterCreator {

    private final MrsEntityFacade mrsEntityFacade;
    private final EncounterAdapter encounterAdapter;

    @Autowired
    public MrsEncounterCreator(MrsEntityFacade mrsEntityFacade, EncounterAdapter encounterAdapter) {
        this.mrsEntityFacade = mrsEntityFacade;
        this.encounterAdapter = encounterAdapter;
    }

    public void createPillTakenEncounterForPatient(String motechId) {
        OpenMRSPatient patient = mrsEntityFacade.findPatientByMotechId(motechId);
        Set<Observation> allObs = createObservationGroup();
        Encounter encounter = createEncounter(patient, allObs);

        encounterAdapter.createEncounter(encounter);
    }

    private Encounter createEncounter(OpenMRSPatient patient, Set<Observation> allObs) {
        OpenMRSProvider provider = mrsEntityFacade.findMotechUser();
        OpenMRSFacility facility = mrsEntityFacade.findMotechFacility();

        Encounter encounter = new OpenMRSEncounter.MRSEncounterBuilder().withDate(new Date()).withObservations(allObs)
                .withFacility(facility).withEncounterType(MrsConstants.PILL_REMINDER_ENCOUNTER_TYPE)
                .withProvider(provider).withPatient(patient).build();
        return encounter;
    }

    private Set<Observation> createObservationGroup() {
        OpenMRSObservation obs = new OpenMRSObservation(new Date(), MrsConstants.PILL_TAKEN_CONCEPT_NAME,
                MrsConstants.PILL_TAKEN_CONCEPT_YES_ANSWER);
        Set<Observation> allObs = new HashSet<>();
        allObs.add(obs);
        return allObs;
    }
}
