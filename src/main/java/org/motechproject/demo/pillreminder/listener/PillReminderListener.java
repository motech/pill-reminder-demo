package org.motechproject.demo.pillreminder.listener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.motechproject.demo.pillreminder.PillReminderSettings;
import org.motechproject.demo.pillreminder.mrs.MrsConstants;
import org.motechproject.demo.pillreminder.mrs.MrsEntityFacade;
import org.motechproject.demo.pillreminder.support.CallRequestDataKeys;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.server.pillreminder.api.EventKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MOTECH Listener that handles a pill reminder event. Upon receiving the event,
 * this listener attempts to retrieve information about patient, specifically
 * the patients phone number. It then initiates a call to that patient using the
 * provider IVR service. Note: Though the IVRService is a generic interface and
 * in theory could be swapped out by another implementation, this listener is
 * dependent on the Verboice IVR Service because of details within that
 * implementation class
 */
@Component
public class PillReminderListener {
    private final Logger logger = LoggerFactory.getLogger(PillReminderListener.class);

    private final IVRService ivrService;
    private final MrsEntityFacade mrsEntityFacade;
    private final PillReminderSettings settings;

    @Autowired
    public PillReminderListener(IVRService ivrService, MrsEntityFacade mrsEntityFacade, PillReminderSettings settings) {
        this.ivrService = ivrService;
        this.mrsEntityFacade = mrsEntityFacade;
        this.settings = settings;
    }

    @MotechListener(subjects = EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT)
    public void handlePillReminderEvent(MotechEvent motechEvent) {
        if (maxRetryCountReached(motechEvent, settings.getMaxRetryCount())) {
            return;
        }

        String motechId = motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        MRSPatient patient = mrsEntityFacade.findPatientByMotechId(motechId);

        String phonenum = getPhoneFromAttributes(patient.getPerson().getAttributes());
        if (phonenum == null) {
            logger.error("No Phone Number attribute found on patient with MoTeCH Id: " + motechId);
            logger.error("Cannot initiate a phone call without a phone number");
            return;
        }

        initiateCall(motechId, phonenum);
    }

    private void initiateCall(String motechId, String phonenum) {
        CallRequest callRequest = new CallRequest(phonenum, 120, settings.getVerboiceChannelName());

        Map<String, String> payload = callRequest.getPayload();

        // it's important that we store the motech id in the call request
        // payload. The verboice ivr service will copy all payload data to the
        // flow session so that we can retrieve it at a later time
        payload.put(CallRequestDataKeys.MOTECH_ID, motechId);

        // the callback_url is used once verboice starts a call to retrieve the
        // data for the call (e.g. TwiML)
        String callbackUrl = settings.getMotechUrl() + "/module/pillreminder-demo/ivr/start?motech_call_id=%s";
        try {
            payload.put(CallRequestDataKeys.CALLBACK_URL,
                    URLEncoder.encode(String.format(callbackUrl, callRequest.getCallId()), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }

        ivrService.initiateCall(callRequest);
    }

    private boolean maxRetryCountReached(MotechEvent motechEvent, int maxRetryCount) {
        return Integer.parseInt(motechEvent.getParameters().get(EventKeys.PILLREMINDER_TIMES_SENT).toString()) >= maxRetryCount;
    }

    private String getPhoneFromAttributes(List<Attribute> attributes) {
        for (Attribute attr : attributes) {
            if (MrsConstants.PERSON_PHONE_NUMBER_ATTR_NAME.equals(attr.name())) {
                return attr.value();
            }
        }

        return null;
    }
}
