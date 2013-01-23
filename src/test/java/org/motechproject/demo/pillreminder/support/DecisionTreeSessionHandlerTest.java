package org.motechproject.demo.pillreminder.support;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.demo.pillreminder.mrs.MrsConstants;
import org.motechproject.demo.pillreminder.mrs.MrsEntityFinder;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;

public class DecisionTreeSessionHandlerTest {

    @Mock
    MrsEntityFinder mrsEntityFinder;

    @Mock
    FlowSessionService flowSessionService;

    @Mock
    FlowSession flowSession;

    private DecisionTreeSessionHandler reminder;

    @Before
    public void setUp() {
        initMocks(this);
        reminder = new DecisionTreeSessionHandler(mrsEntityFinder, flowSessionService);
    }

    @Test
    public void shouldUsePinAttributeOnPerson() {
        MRSPerson person = new MRSPerson();
        person.addAttribute(new Attribute(MrsConstants.PERSON_PIN_ATTR_NAME, "5432"));
        MRSPatient patient = new MRSPatient("500", person, null);

        when(mrsEntityFinder.findPatientByMotechId("500")).thenReturn(patient);
        when(flowSessionService.getSession("SessionId")).thenReturn(flowSession);
        when(flowSession.get("motechId")).thenReturn("500");

        assertTrue(reminder.digitsMatchPatientPin("SessionId", "5432"));
    }

}
