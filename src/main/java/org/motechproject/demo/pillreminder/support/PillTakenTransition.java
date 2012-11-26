package org.motechproject.demo.pillreminder.support;

import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.INodeOperation;
import org.motechproject.decisiontree.core.model.ITransition;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.event.listener.EventRelay;
import org.springframework.beans.factory.annotation.Autowired;

public class PillTakenTransition implements ITransition {

    @Autowired
    private EventRelay eventRelay;
    
    @Override
    public Node getDestinationNode(String arg0, FlowSession arg1) {
        return createOpenMrsNode();
    }

    private Node createOpenMrsNode() {
        Node node = getPillTakenNode();
        node.addOperations(getNewOpenMrsUpdateOperation());

        return node;
    }

    private Node getPillTakenNode() {
        return new Node().setPrompts(new TextToSpeechPrompt()
                .setMessage("You answered yes. Thank you for your response."));
    }

    private INodeOperation getNewOpenMrsUpdateOperation() {
        return new UpdateMrsOperation(eventRelay);
    }

}
