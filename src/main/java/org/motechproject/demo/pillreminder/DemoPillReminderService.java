package org.motechproject.demo.pillreminder;

import java.util.List;

import org.motechproject.decisiontree.core.FlowSession;
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

    private MRSPatientAdapter patientAdapter;
    private FlowSessionService flowSessionService;

    @Autowired
    public DemoPillReminderService(MRSPatientAdapter patientAdapter, FlowSessionService flowSessionService) {
        this.patientAdapter = patientAdapter;
        this.flowSessionService = flowSessionService;
    }

    public ModelAndView getViewForPatient(String motechId, String sessionId, String phoneNum) {
        MRSPatient patient = patientAdapter.getPatientByMotechId(motechId);
        if (patient == null) {
            // this should never be the case because a call cannot initiate
            // without the presence of a patient. Guard against the case
            // there is a problem retrieving the patient
            logger.error("There was a problem retrieving patient with id: " + motechId);
            return new ModelAndView("patient-not-found");
        }

        FlowSession session = flowSessionService.getSession(sessionId);

        if (session == null) {
            // a null session indicates we are first establishing a connection
            // with the user
            // they are presented with a security login. Pins are retrieved
            // through the OpenMRS

            // the security pin is derived from an OpenMRS Person attribute
            MRSPerson person = patient.getPerson();
            if (person == null) {
                // guard against the case person is null - unlikely, but possible
                return createSecurityPinView(sessionId, DEFAULT_PIN);
            }
            
            String pin = readPinAttributeValue(patient);

            if (pin == null) {
                pin = DEFAULT_PIN;
            }

            return createSecurityPinView(sessionId, pin);
        }

        return null;
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

}
