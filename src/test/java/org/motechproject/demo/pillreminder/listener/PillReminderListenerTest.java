package org.motechproject.demo.pillreminder.listener;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.demo.pillreminder.mrs.MrsEntityFinder;
import org.motechproject.event.MotechEvent;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.server.pillreminder.api.EventKeys;

public class PillReminderListenerTest {

    @Mock
    private IVRService ivrService;

    @Mock
    private MrsEntityFinder mrsEntityFinder;

    private PillReminderListener listener;

    @Before
    public void setUp() {
        initMocks(this);
        listener = new PillReminderListener(ivrService, mrsEntityFinder);
    }

    @Test
    public void shouldNotSendCallIfNoPhoneNumber() {
        MRSPerson person = new MRSPerson();
        MRSPatient patient = new MRSPatient(null, person, null);

        when(mrsEntityFinder.findPatientByMotechId("700")).thenReturn(patient);

        MotechEvent motechEvent = new MotechEvent();
        motechEvent.getParameters().put(EventKeys.PILLREMINDER_TIMES_SENT, "0");
        motechEvent.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "700");

        listener.handlePillReminderEvent(motechEvent);

        verify(ivrService, times(0)).initiateCall(any(CallRequest.class));
    }

    @Test
    public void shouldNotSendCallIfNotFirstAttempt() {
        MRSPerson person = new MRSPerson();
        person.getAttributes().add(new Attribute("Phone Number", "555"));
        MRSPatient patient = new MRSPatient(null, person, null);

        when(mrsEntityFinder.findPatientByMotechId("700")).thenReturn(patient);

        MotechEvent motechEvent = new MotechEvent();
        motechEvent.getParameters().put(EventKeys.PILLREMINDER_TIMES_SENT, "1");
        motechEvent.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "700");

        listener.handlePillReminderEvent(motechEvent);

        verify(ivrService, times(0)).initiateCall(any(CallRequest.class));
    }
}
