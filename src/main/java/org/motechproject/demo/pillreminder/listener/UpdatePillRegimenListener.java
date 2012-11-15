package org.motechproject.demo.pillreminder.listener;

import java.util.List;

import org.joda.time.LocalDate;
import org.motechproject.demo.pillreminder.events.EventKeys;
import org.motechproject.demo.pillreminder.events.Events;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.server.pillreminder.api.contract.DosageResponse;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.api.service.PillReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdatePillRegimenListener {

    private PillReminderService pillReminderService;

    @Autowired
    public UpdatePillRegimenListener(PillReminderService pillReminderService) {
        this.pillReminderService = pillReminderService;
    }

    @MotechListener(subjects = Events.PATIENT_TOOK_DOSAGE)
    public void handleDosageTaken(MotechEvent motechEvent) {
        String motechId = motechEvent.getParameters().get(EventKeys.MOTECH_ID).toString();

        PillRegimenResponse response = pillReminderService.getPillRegimen(motechId);
        String regimenId = response.getPillRegimenId();
        String dosageId = getDosageId(response.getDosages());

        pillReminderService.dosageStatusKnown(regimenId, dosageId, LocalDate.now());
    }

    private String getDosageId(List<DosageResponse> dosages) {
        return dosages.get(0).getDosageId();
    }
}
