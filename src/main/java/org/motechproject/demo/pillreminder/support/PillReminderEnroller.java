package org.motechproject.demo.pillreminder.support;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.motechproject.demo.pillreminder.domain.EnrollmentResponse;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.api.contract.DosageRequest;
import org.motechproject.server.pillreminder.api.contract.MedicineRequest;
import org.motechproject.server.pillreminder.api.service.PillReminderService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PillReminderEnroller {

    private final PillReminderService pillReminderService;
    private final MRSPatientAdapter patientAdapter;

    @Autowired
    public PillReminderEnroller(PillReminderService pillReminderService, MRSPatientAdapter patientAdapter) {
        this.pillReminderService = pillReminderService;
        this.patientAdapter = patientAdapter;
    }

    public EnrollmentResponse enrollPatientWithId(String externalId) {
        EnrollmentResponse response = new EnrollmentResponse();

        MRSPatient patient = patientAdapter.getPatientByMotechId(externalId);
        if (patient == null) {
            response.addError("No MRS Patient Found with id: " + externalId);
            return response;
        }

        DosageRequest dosageRequest = buildDosageRequest();
        DailyPillRegimenRequest request = new DailyPillRegimenRequest(externalId, 1, 1, 0, Arrays.asList(dosageRequest));

        pillReminderService.createNew(request);

        return null;
    }

    private DosageRequest buildDosageRequest() {
        DateTime currentTime = DateUtil.now();
        DateTime tomorrow = currentTime.plusDays(1);
        DateTime timeToSchedule = currentTime.plusMinutes(5);

        MedicineRequest medicineRequest = new MedicineRequest("Demo Prescription", currentTime.toLocalDate(),
                tomorrow.toLocalDate());

        DosageRequest request = new DosageRequest(timeToSchedule.getHourOfDay(), timeToSchedule.getMinuteOfHour(),
                Arrays.asList(medicineRequest));

        return request;
    }

}
