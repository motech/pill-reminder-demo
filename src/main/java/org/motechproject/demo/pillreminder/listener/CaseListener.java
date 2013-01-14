package org.motechproject.demo.pillreminder.listener;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.motechproject.commcare.events.CaseEvent;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.demo.pillreminder.domain.EnrollmentRequest;
import org.motechproject.demo.pillreminder.mrs.MrsFacilityResolver;
import org.motechproject.demo.pillreminder.support.PillReminderEnroller;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CaseListener {

    private static final String DEFAULT_FIRST_NAME = "MOTECH First Name";
    private static final String DEFAULT_LAST_NAME = "MOTECH Last Name";
    private static final String DEFAULT_GENDER = "M";

    private final MRSPatientAdapter patientAdapter;
    private final PillReminderEnroller enroller;
    private final MrsFacilityResolver facilityResolver;

    @Autowired
    public CaseListener(MRSPatientAdapter patientAdapter, MrsFacilityResolver facilityResolver,
            PillReminderEnroller enroller) {
        this.patientAdapter = patientAdapter;
        this.facilityResolver = facilityResolver;
        this.enroller = enroller;
    }

    @MotechListener(subjects = EventSubjects.CASE_EVENT)
    public void handleCase(MotechEvent event) {
        CaseEvent caseEvent = new CaseEvent(event);
        Map<String, String> caseValues = caseEvent.getFieldValues();
        String motechId = createPatient(caseValues);
        enrollInPillReminder(caseValues, motechId);
    }

    private void enrollInPillReminder(Map<String, String> caseValues, String motechId) {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setMotechId(motechId);
        request.setPhonenumber(caseValues.get("phone_number"));
        request.setPin(caseValues.get("pin"));
        DateTime dateTime = DateUtil.now().plusMinutes(2);
        request.setDosageStartTime(String.format("%02d:%02d", dateTime.getHourOfDay(), dateTime.getMinuteOfHour()));
        enroller.enrollPatientWithId(request);
    }

    private String createPatient(Map<String, String> caseValues) {
        MRSPerson person = new MRSPerson();
        person.firstName(DEFAULT_FIRST_NAME);
        person.lastName(DEFAULT_LAST_NAME);
        person.gender(DEFAULT_GENDER);
        person.dateOfBirth(new Date());

        String motechId = caseValues.get("patient_number");
        MRSPatient patient = new MRSPatient(motechId, person, facilityResolver.resolveMotechFacility());
        patientAdapter.savePatient(patient);
        return motechId;
    }

}
