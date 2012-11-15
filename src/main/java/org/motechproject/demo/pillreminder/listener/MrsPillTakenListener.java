package org.motechproject.demo.pillreminder.listener;

import org.motechproject.demo.pillreminder.events.EventKeys;
import org.motechproject.demo.pillreminder.events.Events;
import org.motechproject.demo.pillreminder.mrs.MrsEncounterUpdater;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MrsPillTakenListener {

    private MrsEncounterUpdater encounterUpdater;

    @Autowired
    public MrsPillTakenListener(MrsEncounterUpdater encounterUpdater) {
        this.encounterUpdater = encounterUpdater;
    }

    @MotechListener(subjects = Events.PATIENT_TOOK_DOSAGE)
    public void handleMrsUpdate(MotechEvent event) {
        String motechId = event.getParameters().get(EventKeys.MOTECH_ID).toString();
        encounterUpdater.addPillTakenEncounterToPatient(motechId);
    }

}
