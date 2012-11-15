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

@Component
public class MrsEncounterUpdater {

    private final MrsEntityFinder mrsEntityFinder;
    private final MRSEncounterAdapter encounterAdapter;

    @Autowired
    public MrsEncounterUpdater(MrsEntityFinder mrsEntityFinder, MRSEncounterAdapter encounterAdapter) {
        this.mrsEntityFinder = mrsEntityFinder;
        this.encounterAdapter = encounterAdapter;
    }

    public void addPillTakenEncounterToPatient(String motechId) {
        MRSPatient patient = mrsEntityFinder.findPatientByMotechId(motechId);
        Set<MRSObservation> allObs = createObservationGroup();
        MRSEncounter encounter = createEncounter(patient, allObs);

        encounterAdapter.createEncounter(encounter);
    }

    private MRSEncounter createEncounter(MRSPatient patient, Set<MRSObservation> allObs) {
        MRSUser user = mrsEntityFinder.findMotechUser();
        MRSFacility facility = mrsEntityFinder.findMotechFacility();

        MRSEncounter encounter = new MRSEncounter.MRSEncounterBuilder().withDate(new Date()).withObservations(allObs)
                .withFacility(facility).withEncounterType("PILL REMINDER").withProvider(user.getPerson())
                .withPatient(patient).build();
        return encounter;
    }

    private Set<MRSObservation> createObservationGroup() {
        MRSObservation obs = new MRSObservation(new Date(), "Taken Pill", "TRUE");
        Set<MRSObservation> allObs = new HashSet<>();
        allObs.add(obs);
        return allObs;
    }
}
