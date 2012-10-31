package org.motechproject.demo.pillreminder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.server.service.DecisionTreeServer;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.springframework.web.servlet.ModelAndView;

public class PillReminderTest {

    @Mock
    MRSPatientAdapter patientAdapter;

    @Mock
    FlowSessionService flowSessionService;

    @Mock
    DecisionTreeServer decisionTreeServer;

    private DemoPillReminderService reminder;

    @Before
    public void setUp() {
        initMocks(this);
        reminder = new DemoPillReminderService(patientAdapter, flowSessionService, decisionTreeServer);
    }

    @Test
    public void shouldReturnPatientNotFoundView() {
        ModelAndView view = reminder.getSecurityPinView("500", null);

        assertEquals("patient-not-found", view.getViewName());
    }

    @Test
    public void shouldUseDefaultPinOnFirstConnectionWithNullPerson() {
        when(patientAdapter.getPatientByMotechId("500")).thenReturn(new MRSPatient("500"));

        ModelAndView view = reminder.getSecurityPinView("500", "SessionId");

        assertEquals("security-pin", view.getViewName());
        assertEquals(DemoPillReminderService.DEFAULT_PIN, view.getModel().get("pin").toString());
    }

    @Test
    public void shouldUseDefaultPinOnEmptyAttributeList() {
        MRSPerson person = new MRSPerson();
        MRSPatient patient = new MRSPatient("500", person, null);
        when(patientAdapter.getPatientByMotechId("500")).thenReturn(patient);

        ModelAndView view = reminder.getSecurityPinView("500", "SessionId");
        assertEquals(DemoPillReminderService.DEFAULT_PIN, view.getModel().get("pin").toString());
    }

    @Test
    public void shouldUsePinAttributeOnPerson() {
        MRSPerson person = new MRSPerson();
        person.addAttribute(new Attribute("pin", "5432"));
        MRSPatient patient = new MRSPatient("500", person, null);
        when(patientAdapter.getPatientByMotechId("500")).thenReturn(patient);

        ModelAndView view = reminder.getSecurityPinView("500", "SessionId");
        assertEquals("5432", view.getModel().get("pin").toString());
    }
}
