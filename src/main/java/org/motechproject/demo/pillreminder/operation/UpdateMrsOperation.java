package org.motechproject.demo.pillreminder.operation;

import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.INodeOperation;
import org.motechproject.demo.pillreminder.Events;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Operation that will raise an event to update the MRS patient
 */
public class UpdateMrsOperation implements INodeOperation {

    @Autowired
    private EventRelay eventRelay;
    
    @Override
    public void perform(String transitionKey, FlowSession flowSession) {
        String motechId = flowSession.get("motechId");
        MotechEvent event = new MotechEvent(Events.UPDATE_MRS_PATIENT_EVENT);
        event.getParameters().put("motechId", motechId);
        
        eventRelay.sendEventMessage(event);
    }

}
