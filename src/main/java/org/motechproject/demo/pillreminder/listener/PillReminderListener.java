package org.motechproject.demo.pillreminder.listener;

import java.util.List;
import java.util.Map;

import org.motechproject.demo.pillreminder.PillReminderSettings;
import org.motechproject.demo.pillreminder.mrs.MrsEntityFinder;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.server.pillreminder.api.EventKeys;
import org.motechproject.server.voxeo.VoxeoIVRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PillReminderListener {
    private final Logger logger = LoggerFactory.getLogger(PillReminderListener.class);

    private static final String PILLREMINDER_PROPERTY = "pillreminder";

    private static final String MRS_PHONE_NUMBER_ATTR_NAME = "Phone Number";

    private final IVRService ivrService;
    private final MrsEntityFinder mrsEntityFinder;
    private final PillReminderSettings settings;

    @Autowired
    public PillReminderListener(IVRService ivrService, MrsEntityFinder mrsEntityFinder, PillReminderSettings settings) {
        this.ivrService = ivrService;
        this.mrsEntityFinder = mrsEntityFinder;
        this.settings = settings;
    }

    @MotechListener(subjects = EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT)
    public void handlePillReminderEvent(MotechEvent motechEvent) {
        if (maxRetryCountReached(motechEvent, settings.getMaxRetryCount())) {
            return;
        }

        String motechId = motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        MRSPatient patient = mrsEntityFinder.findPatientByMotechId(motechId);

        String phonenum = getPhoneFromAttributes(patient.getPerson().getAttributes());
        if (phonenum == null) {
            logger.error("No Phone Number attribute found on patient with MoTeCH Id: " + motechId);
            logger.error("Cannot initiate a phone call without a phone number");
            return;
        }

        initiateCall(motechId, phonenum);
    }

    private void initiateCall(String motechId, String phonenum) {
        String callbackUrl = settings.getMotechUrl() + "/module/pillreminder-demo/ivr";
        CallRequest callRequest = new CallRequest(phonenum, 120, callbackUrl);

        Map<String, String> payload = callRequest.getPayload();
        payload.put(VoxeoIVRService.APPLICATION_NAME, PILLREMINDER_PROPERTY);
        payload.put(VoxeoIVRService.MOTECH_ID, motechId);
        payload.put(VoxeoIVRService.CALLER_ID, settings.getCallerId());

        ivrService.initiateCall(callRequest);
    }

    private boolean maxRetryCountReached(MotechEvent motechEvent, int maxRetryCount) {
        return Integer.parseInt(motechEvent.getParameters().get(EventKeys.PILLREMINDER_TIMES_SENT).toString()) >= maxRetryCount;
    }

    private String getPhoneFromAttributes(List<Attribute> attributes) {
        for (Attribute attr : attributes) {
            if (MRS_PHONE_NUMBER_ATTR_NAME.equals(attr.name())) {
                return attr.value();
            }
        }

        return null;
    }
}
