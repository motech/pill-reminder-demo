package org.motechproject.demo.pillreminder.listener;

import java.util.List;

import org.joda.time.LocalDate;
import org.motechproject.demo.pillreminder.events.Events;
import org.motechproject.demo.pillreminder.support.DecisionTreeSessionHandler;
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
    private DecisionTreeSessionHandler decisionTreeSessionHandler;

    @Autowired
    public UpdatePillRegimenListener(PillReminderService pillReminderService,
            DecisionTreeSessionHandler decisionTreeSessionHandler) {
        this.pillReminderService = pillReminderService;
        this.decisionTreeSessionHandler = decisionTreeSessionHandler;
    }

    @MotechListener(subjects = Events.PATIENT_TOOK_DOSAGE)
    public void handleDosageTaken(MotechEvent event) {
        String motechId = decisionTreeSessionHandler.getMotechIdForSessionWithId(event.getParameters()
                .get("flowSessionId").toString());

        PillRegimenResponse response = pillReminderService.getPillRegimen(motechId);
        String regimenId = response.getPillRegimenId();
        String dosageId = getDosageId(response.getDosages());

        pillReminderService.dosageStatusKnown(regimenId, dosageId, LocalDate.now());
    }

    private String getDosageId(List<DosageResponse> dosages) {
        return dosages.get(0).getDosageId();
    }
}
