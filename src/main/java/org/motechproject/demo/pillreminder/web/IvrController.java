package org.motechproject.demo.pillreminder.web;

import javax.servlet.http.HttpServletRequest;

import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.demo.pillreminder.PillReminderSettings;
import org.motechproject.demo.pillreminder.support.DecisionTreeSessionHandler;
import org.motechproject.ivr.service.IVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/ivr")
public class IvrController {

    private DecisionTreeSessionHandler decisionTreeSessionHandler;
    private PillReminderSettings settings;

    @Autowired
    IVRService ivrService;

    @Autowired
    FlowSessionService flowSessionService;

    @Autowired
    public IvrController(DecisionTreeSessionHandler decisionTreeSessionHandler, PillReminderSettings settings) {
        this.decisionTreeSessionHandler = decisionTreeSessionHandler;
        this.settings = settings;
    }

    /**
     * This is the first callback invoked by the Verboice application. At this
     * point, a call has been initiated to a specific patient. <br />
     * It should be noted that Verboice uses their own internal variable to
     * identify a call. Since we have no control over the value of this
     * identifier, we must update the flow session to use this value.
     */
    @RequestMapping("/start")
    public ModelAndView generateSecurityPinTwiML(HttpServletRequest request) {
        String verboiceId = request.getParameter("CallSid");
        String motechId = request.getParameter("motech_call_id");
        ModelAndView view = new ModelAndView("security-pin");

        decisionTreeSessionHandler.updateFlowSessionIdToVerboiceId(motechId, verboiceId);

        view.addObject("path", settings.getMotechUrl());
        view.addObject("sessionId", verboiceId);

        return view;
    }

    /**
     * Attempts to authenticate the pin number entered by the patient. If the
     * pin is accepted, that is the digits entered by the user match the value
     * of the Pin attribute on the MRS Patient, then it redirects to the
     * motech-verboice module to handle the rest of the request. If it is not
     * accepted, the call ends
     */
    @RequestMapping("/authenticate")
    public ModelAndView authenticate(HttpServletRequest request) {
        String sessionId = request.getParameter("CallSid");
        String digits = request.getParameter("Digits");

        ModelAndView view = null;
        if (decisionTreeSessionHandler.digitsMatchPatientPin(sessionId, digits)) {
            view = new ModelAndView("redirect");
            view.addObject("path", settings.getMotechUrl());
            view.addObject("sessionId", sessionId);
        } else {
            view = new ModelAndView("failed-authentication");
        }

        return view;
    }

}
