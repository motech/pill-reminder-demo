package org.motechproject.demo.pillreminder.support;

import java.util.Arrays;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.motechproject.demo.pillreminder.domain.EnrollmentResponse;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
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

    public EnrollmentResponse enrollPatientWithId(String externalId, String pin, String phonenumber) {
        EnrollmentResponse response = new EnrollmentResponse();

        MRSPatient patient = patientAdapter.getPatientByMotechId(externalId);
        if (patient == null) {
            response.addError("No MRS Patient Found with id: " + externalId);
            return response;
        }

        setAttribute(patient.getPerson(), pin, "Pin");
        setAttribute(patient.getPerson(), phonenumber, "Phone Number");
        try {
            patientAdapter.updatePatient(patient);
        } catch(Exception e) {
            // if OpenMRS does not have attribute types of Pin or Phone Number
            // an exception will be thrown
            
        }
        patient = patientAdapter.getPatientByMotechId(externalId);
        validatePatientHasAttributes(patient.getPerson(), response);
        if (response.errorCount() > 0) {
            return response;
        }

        DosageRequest dosageRequest = buildDosageRequest();
        DailyPillRegimenRequest request = new DailyPillRegimenRequest(externalId, 1, 5, 1, Arrays.asList(dosageRequest));

        pillReminderService.createNew(request);

        return null;
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

    private void validatePatientHasAttributes(MRSPerson person, EnrollmentResponse response) {
        boolean hasPin = false, hasPhone = false;
        for (Attribute attr : person.getAttributes()) {
            if ("Pin".equals(attr.name())) {
                hasPin = true;
                continue;
            }

            if ("Phone Number".equals(attr.name())) {
                hasPhone = true;
                continue;
            }
        }

        if (!hasPin) {
            response.addError("Could not set Pin attribute on person. You need to make sure OpenMRS has a person attribute type with the name Pin");
        }

        if (!hasPhone) {
            response.addError("Could not set Phone Number attribute on person. You need to make sure OpenMRS has a person attribute type with the name Phone Number");
        }
    }

    private DosageRequest buildDosageRequest() {
        DateTime currentTime = DateUtil.now();
        DateTime tomorrow = currentTime.plusDays(1);
        DateTime timeToSchedule = currentTime.plusMinutes(2);

        MedicineRequest medicineRequest = new MedicineRequest("Demo Prescription", currentTime.toLocalDate(),
                tomorrow.toLocalDate());

        DosageRequest request = new DosageRequest(timeToSchedule.getHourOfDay(), timeToSchedule.getMinuteOfHour(),
                Arrays.asList(medicineRequest));

        return request;
    }

}
