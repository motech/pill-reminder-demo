package org.motechproject.demo.pillreminder;

import java.util.List;

import javax.annotation.PostConstruct;

import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Prompt;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.decisiontree.server.service.DecisionTreeService;
import org.motechproject.demo.pillreminder.operation.UpdateMrsOperation;
import org.motechproject.event.listener.EventRelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Constructs the Decision Tree
 */
@Component
public class DecisionTreeBuilder {
    private final Logger logger = LoggerFactory.getLogger(DecisionTreeBuilder.class);
    private final DecisionTreeService decisionTreeService;
    private final EventRelay eventRelay;

    @Autowired
    public DecisionTreeBuilder(DecisionTreeService decisionTreeService, EventRelay eventRelay) {
        this.decisionTreeService = decisionTreeService;
        this.eventRelay = eventRelay;
    }

    @PostConstruct
    public void buildTree() {
        logger.info("Creating a new demo decision tree");

        List<Tree> trees = decisionTreeService.getDecisionTrees();
        for (Tree tree : trees) {
            if ("Demo Tree".equals(tree.getName())) {
                decisionTreeService.deleteDecisionTree(tree.getId());
                break;
            }
        }

        Tree tree = new Tree();
        tree.setName("Demo Tree");
        Transition rootTransition = new Transition();

        LoggingOperation lo1 = new LoggingOperation();
        lo1.setMessage("User Pressed 1");

        LoggingOperation lo2 = new LoggingOperation();
        lo2.setMessage("User Pressed 2");

        rootTransition
                .setDestinationNode(new Node()
                        .setPrompts(
                                new Prompt[] {
                                        new TextToSpeechPrompt()
                                                .setMessage("Have you taken the recommended dosage of medicine A?"),
                                        new TextToSpeechPrompt().setMessage("If yes, then press 1. Otherwise press 3") })
                        .setTransitions(
                                new Object[][] {
                                        {
                                                "1",
                                                new Transition()
                                                        .setName("pressed1")
                                                        .setDestinationNode(
                                                                new Node()
                                                                        .setPrompts(
                                                                                new TextToSpeechPrompt()
                                                                                        .setMessage("You answered yes. Thank you for your response."))
                                                                        .addOperations(new UpdateMrsOperation())) },
                                        {
                                                "3",
                                                new Transition()
                                                        .setName("pressed2")
                                                        .setDestinationNode(
                                                                new Node()
                                                                        .setPrompts(
                                                                                new TextToSpeechPrompt()
                                                                                        .setMessage("You answered no. Thank you for your response."))
                                                                        .addOperations(lo2)) } }));
        tree.setRootTransition(rootTransition);

        decisionTreeService.saveDecisionTree(tree);
    }
}
