package org.motechproject.demo.pillreminder;

import java.util.List;

import javax.annotation.PostConstruct;

import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.decisiontree.server.service.DecisionTreeService;
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
    private DecisionTreeService decisionTreeService;

    @Autowired
    public DecisionTreeBuilder(DecisionTreeService decisionTreeService) {
        this.decisionTreeService = decisionTreeService;
    }

    @PostConstruct
    public void buildTree() {
        logger.info("Creating a new demo decision tree");
        
        List<Tree> trees = decisionTreeService.getDecisionTrees();
        for(Tree tree : trees) {
            if ("Demo Tree".equals(tree.getName())) {
                decisionTreeService.deleteDecisionTree(tree.getId());
                break;
            }
        }

        Tree tree = new Tree();
        tree.setName("Demo Tree");
        Transition rootTransition = new Transition();
        rootTransition.setDestinationNode(new Node().setPrompts(new TextToSpeechPrompt().setMessage("Testing Message"))
                .setTransitions(
                        new Object[][] {
                                {
                                        "1",
                                        new Transition().setName("pressed1")
                                                .setDestinationNode(
                                                        new Node().setPrompts(new TextToSpeechPrompt()
                                                                .setMessage("Pressed 1"))) },
                                {
                                        "2",
                                        new Transition().setName("pressed2")
                                                .setDestinationNode(
                                                        new Node().setPrompts(new TextToSpeechPrompt()
                                                                .setMessage("Pressed 2"))) } }));
        tree.setRootTransition(rootTransition);

        decisionTreeService.saveDecisionTree(tree);
    }
}
