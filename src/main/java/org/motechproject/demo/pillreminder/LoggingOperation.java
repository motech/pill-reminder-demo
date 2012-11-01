package org.motechproject.demo.pillreminder;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.INodeOperation;
import org.motechproject.event.listener.EventRelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LoggingOperation implements INodeOperation {
    private final Logger logger = LoggerFactory.getLogger(LoggingOperation.class);
    
    @JsonProperty
    private String message;
    
    @Autowired
    private EventRelay eventRelay;

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void perform(String arg0, FlowSession arg1) {
        logger.info(message);
    }

}
