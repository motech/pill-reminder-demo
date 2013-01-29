package org.motechproject.demo.pillreminder.support;

import java.util.Iterator;

import org.motechproject.demo.pillreminder.domain.EnrollmentRequest;
import org.motechproject.demo.pillreminder.domain.EnrollmentResponse;
import org.motechproject.demo.pillreminder.mrs.MrsConstants;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.model.OpenMRSAttribute;
import org.motechproject.mrs.services.PatientAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class will enroll patients into a prebuilt pill reminder regimen
 */
@Component
public class PillReminderEnroller {

    private final PillReminders pillReminders;
    private final PatientAdapter patientAdapter;

    @Autowired
    public PillReminderEnroller(PillReminders pillReminders, PatientAdapter patientAdapter) {
        this.pillReminders = pillReminders;
        this.patientAdapter = patientAdapter;
    }

    public EnrollmentResponse enrollPatientWithId(EnrollmentRequest request) {
        EnrollmentResponse response = new EnrollmentResponse();

        if (pillReminders.isPatientInPillRegimen(request.getMotechId())) {
            response.addError("Patient is already enrolled in Pill Reminder Regimen.");
            return response;
        }

        Patient patient = patientAdapter.getPatientByMotechId(request.getMotechId());
        if (patient == null) {
            response.addError("No MRS Patient Found with id: " + request.getMotechId());
            return response;
        }

        setAttribute(patient.getPerson(), request.getPin(), MrsConstants.PERSON_PIN_ATTR_NAME);
        setAttribute(patient.getPerson(), request.getPhonenumber(), MrsConstants.PERSON_PHONE_NUMBER_ATTR_NAME);
        try {
            patientAdapter.updatePatient(patient);
        } catch (Exception e) {
            // if OpenMRS does not have attribute types of Pin or Phone Number
            // an exception will be thrown
            response.addError("OpenMRS does not have person attribute type: Pin or Phone Number. Please add them");
            return response;
        }

        String actualStartTime = pillReminders.registerNewPatientIntoPillRegimen(request.getMotechId(), request.getDosageStartTime());
        response.setStartTime(actualStartTime);

        return response;
    }

    private void setAttribute(Person person, String attrValue, String attrName) {
        Iterator<Attribute> attrs = person.getAttributes().iterator();
        while (attrs.hasNext()) {
            Attribute attr = attrs.next();
            if (attrName.equalsIgnoreCase(attr.getName())) {
                attrs.remove();
                break;
            }
        }

        person.getAttributes().add(new OpenMRSAttribute(attrName, attrValue));
    }

}
