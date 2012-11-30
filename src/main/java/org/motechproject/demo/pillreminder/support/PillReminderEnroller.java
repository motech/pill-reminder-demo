package org.motechproject.demo.pillreminder.support;

import java.util.Arrays;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.motechproject.demo.pillreminder.domain.EnrollmentRequest;
import org.motechproject.demo.pillreminder.domain.EnrollmentResponse;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.api.contract.DosageRequest;
import org.motechproject.server.pillreminder.api.contract.MedicineRequest;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.api.service.PillReminderService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class will enroll patients into a prebuilt pill reminder regimen
 */
@Component
public class PillReminderEnroller {

    private static final int REMINDER_RETRY_INTERVAL_IN_MINUTES = 4;
    private static final int REMINDER_BUFFER_TIME = 1;
    private final PillReminderService pillReminderService;
    private final MRSPatientAdapter patientAdapter;

    @Autowired
    public PillReminderEnroller(PillReminderService pillReminderService, MRSPatientAdapter patientAdapter) {
        this.pillReminderService = pillReminderService;
        this.patientAdapter = patientAdapter;
    }

    public EnrollmentResponse enrollPatientWithId(EnrollmentRequest request) {
        EnrollmentResponse response = new EnrollmentResponse();

        PillRegimenResponse regimenResponse = pillReminderService.getPillRegimen(request.getMotechId());
        if (regimenResponse != null) {
            response.addError("Patient is already enrolled in Pill Reminder Regimen.");
            return response;
        }

        MRSPatient patient = patientAdapter.getPatientByMotechId(request.getMotechId());
        if (patient == null) {
            response.addError("No MRS Patient Found with id: " + request.getMotechId());
            return response;
        }

        setAttribute(patient.getPerson(), request.getPin(), "Pin");
        setAttribute(patient.getPerson(), request.getPhonenumber(), "Phone Number");
        try {
            patientAdapter.updatePatient(patient);
        } catch (Exception e) {
            // if OpenMRS does not have attribute types of Pin or Phone Number
            // an exception will be thrown
            response.addError("OpenMRS does not have person attribute type: Pin or Phone Number. Please add them");
            return response;
        }

        DosageRequest dosageRequest = buildDosageRequest(request.getDosageStartTime());
        DailyPillRegimenRequest regimenRequest = new DailyPillRegimenRequest(request.getMotechId(), 1,
                REMINDER_RETRY_INTERVAL_IN_MINUTES, REMINDER_BUFFER_TIME, Arrays.asList(dosageRequest));

        pillReminderService.createNew(regimenRequest);

        return setDosageFieldsOnResponse(dosageRequest, response);
    }

    private EnrollmentResponse setDosageFieldsOnResponse(DosageRequest dosageRequest, EnrollmentResponse response) {
        response.setStartTime(dosageRequest.getStartHour() + ":"
                + String.format("%02d", (dosageRequest.getStartMinute() + REMINDER_BUFFER_TIME)));
        return response;
    }

    private void setAttribute(MRSPerson person, String attrValue, String attrName) {
        Iterator<Attribute> attrs = person.getAttributes().iterator();
        while (attrs.hasNext()) {
            Attribute attr = attrs.next();
            if (attrName.equalsIgnoreCase(attr.name())) {
                attrs.remove();
                break;
            }
        }

        person.getAttributes().add(new Attribute(attrName, attrValue));
    }

    private DosageRequest buildDosageRequest(String dosageStartTime) {
        DateTime currentTime = DateUtil.now();
        DateTime tomorrow = currentTime.plusDays(1);
        String[] time = dosageStartTime.split(":");

        MedicineRequest medicineRequest = new MedicineRequest("Demo Prescription", currentTime.toLocalDate(),
                tomorrow.toLocalDate());

        DosageRequest request = new DosageRequest(Integer.parseInt(time[0]), Integer.parseInt(time[1]),
                Arrays.asList(medicineRequest));

        return request;
    }

}
