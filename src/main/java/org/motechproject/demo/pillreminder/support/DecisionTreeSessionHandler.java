package org.motechproject.demo.pillreminder.support;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.demo.pillreminder.mrs.MrsConstants;
import org.motechproject.demo.pillreminder.mrs.MrsEntityFacade;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DecisionTreeSessionHandler {

    private final MrsEntityFacade mrsEntityFacade;
    private final FlowSessionService flowSessionService;

    @Autowired
    public DecisionTreeSessionHandler(MrsEntityFacade mrsEntityFacade, FlowSessionService flowSessionService) {
        this.mrsEntityFacade = mrsEntityFacade;
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
        String motechId = getMotechIdForSessionWithId(sessionId);
        MRSPatient patient = mrsEntityFacade.findPatientByMotechId(motechId);
        String pin = readPinAttributeValue(patient);

        if (StringUtils.isNotBlank(digits) && digits.equals(pin)) {
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
        return session.get(CallRequestDataKeys.MOTECH_ID);
    }

}
