package org.motechproject.demo.pillreminder.support;

import org.joda.time.LocalDate;
import org.motechproject.demo.pillreminder.domain.PillReminderResponse;
import org.motechproject.server.pillreminder.api.contract.DosageResponse;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.api.service.PillReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PillReminders {

    private static final String NO_RESPONSE_CAPTURED_YET = "No response captured yet";
    private PillReminderService pillReminderService;

    @Autowired
    public PillReminders(PillReminderService pillReminderService) {
        this.pillReminderService = pillReminderService;
    }

    public PillReminderResponse findPillReminderByMotechId(String motechId) {
        PillRegimenResponse regimen = pillReminderService.getPillRegimen(motechId);
        return getPillReminderResponseFromRegimen(regimen);
    }

    private PillReminderResponse getPillReminderResponseFromRegimen(PillRegimenResponse regimen) {
        PillReminderResponse response = new PillReminderResponse();
        if (regimen == null) {
            return response;
        }

        DosageResponse dosageResponse = regimen.getDosages().get(0);
        response.setStartTime(dosageResponse.getDosageHour() + ":"
                + String.format("%02d", dosageResponse.getDosageMinute()));

        response.setLastCapturedDate(getMessageFromLastCapturedDate(dosageResponse.getResponseLastCapturedDate()));
        return response;
    }

    private String getMessageFromLastCapturedDate(LocalDate responseLastCapturedDate) {
        if (responseLastCapturedDate == null) {
            return NO_RESPONSE_CAPTURED_YET;
        }

        return responseLastCapturedDate.toString();
    }

    public void deletePillReminder(String motechId) {
        pillReminderService.remove(motechId);
    }
}
