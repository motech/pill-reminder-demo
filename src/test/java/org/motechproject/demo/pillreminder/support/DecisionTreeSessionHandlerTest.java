package org.motechproject.demo.pillreminder.support;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.service.DecisionTreeServer;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.demo.pillreminder.mrs.MrsEntityFinder;
import org.motechproject.demo.pillreminder.support.DecisionTreeSessionHandler;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.springframework.web.servlet.ModelAndView;

public class DecisionTreeSessionHandlerTest {

    @Mock
    MrsEntityFinder mrsEntityFinder;

    @Mock
    FlowSessionService flowSessionService;
    
    @Mock
    FlowSession flowSession;

    @Mock
    DecisionTreeServer decisionTreeServer;

    private DecisionTreeSessionHandler reminder;

    @Before
    public void setUp() {
        initMocks(this);
        reminder = new DecisionTreeSessionHandler(mrsEntityFinder, flowSessionService, decisionTreeServer);
    }

    @Test
    public void shouldRegisterMotechIdOnFlowSession() {
        when(flowSessionService.findOrCreate(null, "5555")).thenReturn(flowSession);
        
        reminder.registerNewDecisionTreeSession("5555", "700");
        
        verify(flowSession).set("motechId", "700");
    }

    @Test
    public void shouldUseDefaultPinOnWithNullPerson() {
        stubSessionCalls();
        when(mrsEntityFinder.findPatientByMotechId("500")).thenReturn(new MRSPatient("500"));
        
        ModelAndView view = reminder.generateSecurityPinViewForSession("SessionId");

        assertEquals("security-pin", view.getViewName());
        assertEquals(DecisionTreeSessionHandler.DEFAULT_PIN, view.getModel().get("pin").toString());
    }

    private void stubSessionCalls() {
        when(flowSessionService.getSession("SessionId")).thenReturn(flowSession);
        when(flowSession.get("motechId")).thenReturn("500");
    }

    @Test
    public void shouldUseDefaultPinOnEmptyAttributeList() {
        stubSessionCalls();
        
        MRSPerson person = new MRSPerson();
        MRSPatient patient = new MRSPatient("500", person, null);
        when(mrsEntityFinder.findPatientByMotechId("500")).thenReturn(patient);

        ModelAndView view = reminder.generateSecurityPinViewForSession("SessionId");
        assertEquals(DecisionTreeSessionHandler.DEFAULT_PIN, view.getModel().get("pin").toString());
    }

    @Test
    public void shouldUsePinAttributeOnPerson() {
        stubSessionCalls();
        
        MRSPerson person = new MRSPerson();
        person.addAttribute(new Attribute("pin", "5432"));
        MRSPatient patient = new MRSPatient("500", person, null);
        when(mrsEntityFinder.findPatientByMotechId("500")).thenReturn(patient);

        ModelAndView view = reminder.generateSecurityPinViewForSession("SessionId");
        assertEquals("5432", view.getModel().get("pin").toString());
    }
}
