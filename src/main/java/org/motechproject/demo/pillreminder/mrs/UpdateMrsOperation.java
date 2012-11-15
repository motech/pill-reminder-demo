package org.motechproject.demo.pillreminder.mrs;

import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.INodeOperation;
import org.motechproject.demo.pillreminder.events.EventKeys;
import org.motechproject.demo.pillreminder.events.Events;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;

/**
 * Operation that will raise an event to update the MRS patient
 */
public class UpdateMrsOperation implements INodeOperation {

    private EventRelay eventRelay;

    public UpdateMrsOperation(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    @Override
    public void perform(String transitionKey, FlowSession flowSession) {
        String motechId = flowSession.get("motechId");
        MotechEvent event = new MotechEvent(Events.PATIENT_TOOK_DOSAGE);
        event.getParameters().put(EventKeys.MOTECH_ID, motechId);

        eventRelay.sendEventMessage(event);
    }

}
