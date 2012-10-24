package org.motechproject.demo.pillreminder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @RequestMapping("/ivr")
    public ModelAndView generateVxml(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = request.getParameter("sessionId");
        String motechId = request.getParameter("motechId");
        String phoneNum = request.getParameter("phoneNum");

        ModelAndView view = reminder.getViewForPatient(motechId, sessionId, phoneNum);
        view.addObject("sessionId", sessionId);
        view.addObject("motechId", motechId);
        view.addObject("phoneNum", phoneNum);
        
        return view;
    }
}
