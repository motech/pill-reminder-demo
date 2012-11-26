package org.motechproject.demo.pillreminder.support;

import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.INodeOperation;
import org.motechproject.demo.pillreminder.events.EventKeys;
import org.motechproject.demo.pillreminder.events.Events;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;

/**
 * Decision Tree Node Operation that will raise an event to indicate a patient
 * has responded to have taken a pill dosage
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
