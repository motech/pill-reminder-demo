package org.motechproject.demo.pillreminder.support;

import java.util.List;

import javax.annotation.PostConstruct;

import org.motechproject.decisiontree.core.DecisionTreeService;
import org.motechproject.decisiontree.core.model.EventTransition;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Prompt;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.demo.pillreminder.events.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Constructs the Decision Tree used in the demo
 */
@Component
public class DecisionTreeBuilder {
    private final Logger logger = LoggerFactory.getLogger(DecisionTreeBuilder.class);
    private final DecisionTreeService decisionTreeService;

    @Autowired
    public DecisionTreeBuilder(DecisionTreeService decisionTreeService) {
        this.decisionTreeService = decisionTreeService;
    }

    @PostConstruct
    public void buildTree() {
        logger.info("Creating a new demo decision tree");

        // spinning up a new thread so that this will not block the bundle
        // loading process
        new Thread(new Runnable() {

            @Override
            public void run() {
                deleteOldTree();
                createDecisionTree();
            }

        }).start();
    }

    private void deleteOldTree() {
        List<Tree> trees = decisionTreeService.getDecisionTrees();
        for (Tree tree : trees) {
            if ("DemoTree".equals(tree.getName())) {
                decisionTreeService.deleteDecisionTree(tree.getId());
                break;
            }
        }
    }

    private void createDecisionTree() {
        Tree tree = new Tree();
        tree.setName("DemoTree");
        Transition rootTransition = new Transition();

        rootTransition.setDestinationNode(new Node().setNoticePrompts(
                new Prompt[] {
                        new TextToSpeechPrompt()
                                .setMessage("Have you taken the recommended dosage of the demo medicine?"),
                        new TextToSpeechPrompt().setMessage("If yes, then press 1. Otherwise press 3") })
                .setTransitions(
                        new Object[][] { { "1", getPillTakenTransition() }, { "3", getPillNotTakenTransition() } }));
        tree.setRootTransition(rootTransition);

        decisionTreeService.saveDecisionTree(tree);
    }

    private Transition getPillNotTakenTransition() {
        EventTransition transition = new EventTransition();
        transition.setEventSubject(Events.PATIENT_MISSED_DOSAGE);
        transition.setDestinationNode(new Node().setPrompts(new TextToSpeechPrompt()
                .setMessage("You answered no. Thank you for your response.")));
        transition.setName("missed dosage");
        return transition;
    }

    private Transition getPillTakenTransition() {
        EventTransition transition = new EventTransition();
        transition.setEventSubject(Events.PATIENT_TOOK_DOSAGE);
        transition.setDestinationNode(new Node().setPrompts(new TextToSpeechPrompt()
                .setMessage("You answered yes. Thank you for your response.")));
        transition.setName("pill taken");
        return transition;
    }
}
