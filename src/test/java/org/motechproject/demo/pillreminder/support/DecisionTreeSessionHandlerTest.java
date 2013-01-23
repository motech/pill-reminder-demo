package org.motechproject.demo.pillreminder.support;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.demo.pillreminder.mrs.MrsConstants;
import org.motechproject.demo.pillreminder.mrs.MrsEntityFacade;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;

public class DecisionTreeSessionHandlerTest {

    @Mock
    MrsEntityFacade mrsEntityFacade;

    @Mock
    FlowSessionService flowSessionService;

    @Mock
    FlowSession flowSession;

    private DecisionTreeSessionHandler reminder;

    @Before
    public void setUp() {
        initMocks(this);
        reminder = new DecisionTreeSessionHandler(mrsEntityFacade, flowSessionService);
    }

    @Test
    public void shouldUsePinAttributeOnPerson() {
        stubFlowSession();
        stubEntityFinder();

        assertTrue(reminder.digitsMatchPatientPin("SessionId", "5432"));
    }

    private void stubEntityFinder() {
        MRSPerson person = new MRSPerson();
        person.addAttribute(new Attribute(MrsConstants.PERSON_PIN_ATTR_NAME, "5432"));
        MRSPatient patient = new MRSPatient("500", person, null);
        when(mrsEntityFacade.findPatientByMotechId("500")).thenReturn(patient);
    }

    private void stubFlowSession() {
        when(flowSessionService.getSession("SessionId")).thenReturn(flowSession);
        when(flowSession.get("motechId")).thenReturn("500");
    }

    @Test
    public void shouldFailWhenDigitsIsNull() {
        stubFlowSession();
        stubEntityFinder();

        assertFalse(reminder.digitsMatchPatientPin("SessionId", null));
    }

    @Test
    public void shouldFailWhenDigitsIsEmpty() {
        stubFlowSession();
        stubEntityFinder();

        assertFalse(reminder.digitsMatchPatientPin("SessionId", ""));
    }

    @Test
    public void shouldFailWhenDigitsDontMatch() {
        stubFlowSession();
        stubEntityFinder();

        assertFalse(reminder.digitsMatchPatientPin("SessionId", "1234"));
    }
}
