package org.motechproject.demo.pillreminder.listener;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.demo.pillreminder.PillReminderSettings;
import org.motechproject.demo.pillreminder.mrs.MrsEntityFacade;
import org.motechproject.event.MotechEvent;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.mrs.model.OpenMRSAttribute;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.server.pillreminder.api.EventKeys;

public class PillReminderListenerTest {

    @Mock
    private IVRService ivrService;

    @Mock
    private MrsEntityFacade mrsEntityFacade;

    @Mock
    private PillReminderSettings settings;

    private PillReminderListener listener;

    @Before
    public void setUp() {
        initMocks(this);
        listener = new PillReminderListener(ivrService, mrsEntityFacade, settings);
    }

    @Test
    public void shouldNotSendCallIfNoPhoneNumber() {
        OpenMRSPerson person = new OpenMRSPerson();
        OpenMRSPatient patient = new OpenMRSPatient(null, person, null);

        when(mrsEntityFacade.findPatientByMotechId("700")).thenReturn(patient);

        MotechEvent motechEvent = new MotechEvent();
        motechEvent.getParameters().put(EventKeys.PILLREMINDER_TIMES_SENT, "0");
        motechEvent.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "700");

        listener.handlePillReminderEvent(motechEvent);

        verify(ivrService, times(0)).initiateCall(any(CallRequest.class));
    }

    @Test
    public void shouldNotSendCallIfNotFirstAttempt() {
        OpenMRSPerson person = new OpenMRSPerson();
        person.getAttributes().add(new OpenMRSAttribute("Phone Number", "555"));
        OpenMRSPatient patient = new OpenMRSPatient(null, person, null);

        when(mrsEntityFacade.findPatientByMotechId("700")).thenReturn(patient);

        MotechEvent motechEvent = new MotechEvent();
        motechEvent.getParameters().put(EventKeys.PILLREMINDER_TIMES_SENT, "1");
        motechEvent.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "700");

        listener.handlePillReminderEvent(motechEvent);

        verify(ivrService, times(0)).initiateCall(any(CallRequest.class));
    }
}
