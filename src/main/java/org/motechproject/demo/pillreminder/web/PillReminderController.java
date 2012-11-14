package org.motechproject.demo.pillreminder.web;

import javax.servlet.http.HttpServletRequest;

import org.motechproject.demo.pillreminder.domain.EnrollmentRequest;
import org.motechproject.demo.pillreminder.domain.EnrollmentResponse;
import org.motechproject.demo.pillreminder.domain.MrsPatientSearchResult;
import org.motechproject.demo.pillreminder.support.MrsPatientSearcher;
import org.motechproject.demo.pillreminder.support.PillReminderEnroller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PillReminderController {

    private final PillReminderEnroller enroller;
    private final MrsPatientSearcher patientSearcher;

    @Autowired
    public PillReminderController(PillReminderEnroller enroller, MrsPatientSearcher patientSearcher) {
        this.enroller = enroller;
        this.patientSearcher = patientSearcher;
    }

    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request) {
        String scheme = request.getScheme();
        String host = request.getHeader("Host");
        String contextPath = request.getContextPath();

        ModelAndView mv = new ModelAndView("index");
        mv.addObject("path", scheme + "://" + host + contextPath);
        return mv;
    }

    @RequestMapping(value = "/enrollment", method = RequestMethod.POST)
    @ResponseBody
    public EnrollmentResponse addNewEnrollment(@RequestBody EnrollmentRequest request) {
        return enroller.enrollPatientWithId(request.getMotechId(), request.getPin(), request.getPhonenumber());
    }

    @RequestMapping(value = "/search-patient/{motechId}", method = RequestMethod.GET)
    @ResponseBody
    public MrsPatientSearchResult searchForPatient(@PathVariable String motechId) {
        return patientSearcher.searchForPatientsWithMotechId(motechId);
    }
}
