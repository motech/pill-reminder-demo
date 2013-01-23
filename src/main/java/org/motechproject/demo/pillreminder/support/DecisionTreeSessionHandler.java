package org.motechproject.demo.pillreminder.support;

import java.util.List;

import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.demo.pillreminder.mrs.MrsConstants;
import org.motechproject.demo.pillreminder.mrs.MrsEntityFinder;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DecisionTreeSessionHandler {
    public static final String MOTECH_ID_KEY = "motechId";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_PIN = "1234";

    private final MrsEntityFinder mrsEntityFinder;
    private final FlowSessionService flowSessionService;

    @Autowired
    public DecisionTreeSessionHandler(MrsEntityFinder mrsEntityFinder, FlowSessionService flowSessionService) {
        this.mrsEntityFinder = mrsEntityFinder;
        this.flowSessionService = flowSessionService;
    }

    public boolean updateFlowSessionIdToVerboiceId(String oldSessionId, String newSessionId) {
        FlowSession session = flowSessionService.getSession(oldSessionId);
        if (session == null) {
            return false;
        }

        flowSessionService.updateSessionId(oldSessionId, newSessionId);
        return true;
    }

    public boolean digitsMatchPatientPin(String sessionId, String digits) {
        FlowSession session = flowSessionService.getSession(sessionId);
        String motechId = session.get(MOTECH_ID_KEY);
        MRSPatient patient = mrsEntityFinder.findPatientByMotechId(motechId);
        String pin = readPinAttributeValue(patient);

        if (digits.equals(pin)) {
            return true;
        } else {
            return false;
        }
    }

    private String readPinAttributeValue(MRSPatient patient) {
        List<Attribute> attrs = patient.getPerson().getAttributes();
        String pin = null;
        for (Attribute attr : attrs) {
            if (MrsConstants.PERSON_PIN_ATTR_NAME.equals(attr.name())) {
                pin = attr.value();
            }
        }
        return pin;
    }
    
    public String getMotechIdForSessionWithId(String sessionId) {
        FlowSession session = flowSessionService.getSession(sessionId);
        return session.get(DecisionTreeSessionHandler.MOTECH_ID_KEY);
    }

}
