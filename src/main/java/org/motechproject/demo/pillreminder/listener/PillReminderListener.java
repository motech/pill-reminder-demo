package org.motechproject.demo.pillreminder.listener;

import java.util.List;

import org.motechproject.demo.pillreminder.mrs.MrsEntityFinder;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.server.pillreminder.api.EventKeys;
import org.motechproject.server.voxeo.VoxeoIVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PillReminderListener {

    private final IVRService ivrService;
    private MrsEntityFinder mrsEntityFinder;

    @Autowired
    public PillReminderListener(IVRService ivrService, MrsEntityFinder mrsEntityFinder) {
        this.ivrService = ivrService;
        this.mrsEntityFinder = mrsEntityFinder;
    }

    @MotechListener(subjects = EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT)
    public void handlePillReminderEvent(MotechEvent motechEvent) {
        if (Integer.parseInt(motechEvent.getParameters().get(EventKeys.PILLREMINDER_TIMES_SENT).toString()) > 0) {
            return;
        }

        String motechId = motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        MRSPatient patient = mrsEntityFinder.findPatientByMotechId(motechId);

        String phonenum = getPhoneFromAttributes(patient.getPerson().getAttributes());
        if (phonenum == null) {
            return;
        }

        CallRequest callRequest = new CallRequest(phonenum, 120, "none");
        callRequest.getPayload().put(VoxeoIVRService.APPLICATION_NAME, "pillreminder");
        callRequest.setVxml("http://130.111.132.27:8080/motech-platform-server/module/pillreminder-demo/ivr");
        callRequest.setMotechId(motechId);

        ivrService.initiateCall(callRequest);
    }

    private String getPhoneFromAttributes(List<Attribute> attributes) {
        for (Attribute attr : attributes) {
            if ("Phone Number".equals(attr.name())) {
                return attr.value();
            }
        }

        return null;
    }
}
