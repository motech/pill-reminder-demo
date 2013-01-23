package org.motechproject.demo.pillreminder.listener;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.demo.pillreminder.domain.EnrollmentRequest;
import org.motechproject.demo.pillreminder.mrs.MrsEntityFacade;
import org.motechproject.demo.pillreminder.support.PillReminderEnroller;
import org.motechproject.event.MotechEvent;

public class CaseListenerTest {

    @Mock
    PillReminderEnroller enroller;

    @Mock
    MrsEntityFacade mrsEntityFacade;

    CaseListener caseListener;

    @Before
    public void setUp() {
        initMocks(this);
        caseListener = new CaseListener(enroller, mrsEntityFacade);
    }

    @Test
    public void shouldCreateDumbyPatientOnNewCase() {
        MotechEvent event = new MotechEvent();
        Map<String, Object> params = event.getParameters();
        params.put(EventDataKeys.FIELD_VALUES, getPatientNumberCaseElement());

        caseListener.handleCase(event);

        verify(mrsEntityFacade).createDumbyPatient("700");
    }

    private Map<String, String> getPatientNumberCaseElement() {
        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put(CommcareConstants.PATIENT_NUMBER_CASE_ELEMENT, "700");
        return fieldValues;
    }

    @Test
    public void shouldEnrollIntoPillReminder() {
        MotechEvent event = new MotechEvent();
        Map<String, Object> params = event.getParameters();
        params.put(EventDataKeys.FIELD_VALUES, getPinAndPhoneCaseElements());

        caseListener.handleCase(event);

        verify(enroller).enrollPatientWithId(any(EnrollmentRequest.class));
    }

    private Map<String, String> getPinAndPhoneCaseElements() {
        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put(CommcareConstants.PHONE_NUMBER_CASE_ELEMENT, "12075557777");
        fieldValues.put(CommcareConstants.PIN_CASE_ELEMENT, "5522");
        return fieldValues;
    }

}
