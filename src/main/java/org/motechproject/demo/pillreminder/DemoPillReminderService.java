package org.motechproject.demo.pillreminder;

import java.util.List;

import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.service.DecisionTreeServer;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class DemoPillReminderService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_PIN = "1234";

    private final MRSPatientAdapter patientAdapter;
    private final FlowSessionService flowSessionService;
    private final DecisionTreeServer decisionTreeServer;

    @Autowired
    public DemoPillReminderService(MRSPatientAdapter patientAdapter, FlowSessionService flowSessionService,
            DecisionTreeServer decisionTreeServer) {
        this.patientAdapter = patientAdapter;
        this.flowSessionService = flowSessionService;
        this.decisionTreeServer = decisionTreeServer;
    }

    public String registerNewFlowSession(String phoneNumber, String motechId) {
        FlowSession session = flowSessionService.findOrCreate(null, phoneNumber);
        session.set("motechId", motechId);
        flowSessionService.updateSession(session);

        return session.getSessionId();
    }

    public ModelAndView getSecurityPinView(String motechId, String sessionId) {
        MRSPatient patient = patientAdapter.getPatientByMotechId(motechId);
        if (patient == null) {
            // this should never be the case because a call cannot initiate
            // without the presence of a patient. Guard against the case
            // there is a problem retrieving the patient
            logger.error("There was a problem retrieving patient with id: " + motechId);
            return new ModelAndView("patient-not-found");
        }

        // the security pin is derived from an OpenMRS Person attribute
        MRSPerson person = patient.getPerson();
        if (person == null) {
            // guard against the case person is null - unlikely, but
            // possible
            return createSecurityPinView(sessionId, DEFAULT_PIN);
        }

        String pin = readPinAttributeValue(patient);

        if (pin == null) {
            pin = DEFAULT_PIN;
        }

        return createSecurityPinView(sessionId, pin);
    }

    private String readPinAttributeValue(MRSPatient patient) {
        List<Attribute> attrs = patient.getPerson().getAttributes();
        String pin = null;
        for (Attribute attr : attrs) {
            if ("pin".equalsIgnoreCase(attr.name())) {
                pin = attr.value();
            }
        }
        return pin;
    }

    private ModelAndView createSecurityPinView(String sessionId, String pin) {
        ModelAndView mv = new ModelAndView("security-pin");
        mv.addObject("sessionId", sessionId);
        mv.addObject("pin", pin);
        return mv;
    }

    public ModelAndView getViewForNodeTransition(String sessionId, String transitionKey) {
        FlowSession session = flowSessionService.getSession(sessionId);
        ModelAndView view = decisionTreeServer.getResponse(sessionId, session.getPhoneNumber(), "voxeo", "Demo Tree",
                transitionKey, "en");

        // sessionId identifies a patients state within the decision tree (see
        // {@link FlowSession})
        view.addObject("sessionId", sessionId);

        return view;
    }

}
