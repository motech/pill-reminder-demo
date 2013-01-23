package org.motechproject.demo.pillreminder.web;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.demo.pillreminder.domain.EnrollmentRequest;
import org.motechproject.demo.pillreminder.domain.EnrollmentResponse;
import org.motechproject.demo.pillreminder.domain.MrsPatientSearchResult;
import org.motechproject.demo.pillreminder.domain.PillReminderResponse;
import org.motechproject.demo.pillreminder.mrs.MrsEntityFacade;
import org.motechproject.demo.pillreminder.support.PillReminderEnroller;
import org.motechproject.demo.pillreminder.support.PillReminders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PillReminderController {

    private final PillReminderEnroller enroller;
    private final MrsEntityFacade mrsEntityFacade;
    private final PillReminders pillReminders;

    @Autowired
    public PillReminderController(PillReminderEnroller enroller, MrsEntityFacade mrsEntityFacade,
            PillReminders pillReminders) {
        this.enroller = enroller;
        this.mrsEntityFacade = mrsEntityFacade;
        this.pillReminders = pillReminders;
    }

    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request) {
        return modelWithPath("index", request);
    }

    private ModelAndView modelWithPath(String viewName, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView(viewName);
        mv.addObject("path", getFullPathFromRequest(request));
        return mv;
    }

    private String getFullPathFromRequest(HttpServletRequest request) {
        String scheme = request.getScheme();
        String host = request.getHeader("Host");
        String contextPath = request.getContextPath();

        return scheme + "://" + host + contextPath;
    }

    @RequestMapping(value = "/enroll", method = RequestMethod.GET)
    public ModelAndView getEnrollPartial(HttpServletRequest request) {
        return modelWithPath("enroll", request);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView getSearchPartial(HttpServletRequest request) {
        return new ModelAndView("search");
    }

    @RequestMapping(value = "/enrollment", method = RequestMethod.POST)
    @ResponseBody
    public EnrollmentResponse addNewEnrollment(@RequestBody EnrollmentRequest request) {
        DateTime now = DateUtil.now();
        // round up to nearest minute
        // for example, Hour=10,Minute=20,second=30
        // start time will be Hour=10,Minute=21
        if (now.getSecondOfMinute() % 60 != 0) {
            now = now.plusMinutes(1);
        }
        request.setDosageStartTime(now.getHourOfDay() + ":" + String.format("%02d", now.getMinuteOfHour()));
        return enroller.enrollPatientWithId(request);
    }

    @RequestMapping(value = "/search-patient/{motechId}", method = RequestMethod.GET)
    @ResponseBody
    public MrsPatientSearchResult searchForPatient(@PathVariable String motechId) {
        return MrsPatientSearchResult.fromMrsPatient(mrsEntityFacade.findPatientByMotechId(motechId));
    }

    @RequestMapping(value = "/pillreminders/{motechId}", method = RequestMethod.GET)
    @ResponseBody
    public PillReminderResponse searchForPillReminder(@PathVariable String motechId) {
        return pillReminders.findPillReminderByMotechId(motechId);
    }

    @RequestMapping(value = "/pillreminders/{motechId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deletePatient(@PathVariable String motechId) {
        pillReminders.deletePillReminder(motechId);
    }
}
