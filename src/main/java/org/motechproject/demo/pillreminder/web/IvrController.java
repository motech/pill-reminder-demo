package org.motechproject.demo.pillreminder.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.demo.pillreminder.service.DemoPillReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IvrController {

    private DemoPillReminderService reminder;

    @Autowired
    public IvrController(DemoPillReminderService reminder) {
        this.reminder = reminder;
    }

    /**
     * The CCXML will use this URL first to obtain the pin number patient. Once
     * the user has successfully entered a pin, they will be redirected to the
     * decision tree
     */
    @RequestMapping("/ivr")
    public ModelAndView generateVxml(HttpServletRequest request, HttpServletResponse response) {
        String motechId = request.getParameter("motechid");
        String phoneNum = request.getParameter("phonenum");
        String sessionId = reminder.registerNewFlowSession(phoneNum, motechId);

        ModelAndView view = reminder.getSecurityPinView(motechId, sessionId);
        view.addObject("sessionId", sessionId);

        return view;
    }

    /**
     * This URL builds VXML documents based on the demo decision tree. When a
     * patient first retrieves this resource, a new flow session will be
     * established using the session id of the CCXML. Subsequent request will
     * transition the user to different nodes of the decision tree based on
     * their responses from the VXML
     */
    @RequestMapping("/decisiontree")
    public ModelAndView transition(HttpServletRequest request) {
        String sessionId = request.getParameter("sessionId");
        String transitionKey = request.getParameter("trK");

        ModelAndView view = reminder.getViewForNodeTransition(sessionId, transitionKey);

        view.addObject("contextPath", request.getContextPath());
        view.addObject("servletPath", request.getServletPath());
        view.addObject("host", request.getHeader("Host"));
        view.addObject("scheme", request.getScheme());

        // decision tree returns a view name of /vm/node-voxeo
        // TODO: get view resolver to correctly render /vm/node-voxeo view name
        view.setViewName("node-voxeo");
        return view;
    }
}
