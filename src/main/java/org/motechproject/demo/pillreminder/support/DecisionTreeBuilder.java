package org.motechproject.demo.pillreminder.support;

import java.util.List;

import javax.annotation.PostConstruct;

import org.motechproject.decisiontree.core.DecisionTreeService;
import org.motechproject.decisiontree.core.model.AudioPrompt;
import org.motechproject.decisiontree.core.model.EventTransition;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Prompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.demo.pillreminder.PillReminderSettings;
import org.motechproject.demo.pillreminder.content.SoundFiles;
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

    private static final String CMSLITE_STREAM_PATH = "/module/cmsliteapi/stream/";

    private final DecisionTreeService decisionTreeService;
    private final PillReminderSettings settings;

    @Autowired
    public DecisionTreeBuilder(DecisionTreeService decisionTreeService, PillReminderSettings settings) {
        this.decisionTreeService = decisionTreeService;
        this.settings = settings;
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
                new Prompt[] { new AudioPrompt().setAudioFileUrl(getCmsliteUrlFor(SoundFiles.DOSAGE_QUESTION_1)),
                        new AudioPrompt().setAudioFileUrl(getCmsliteUrlFor(SoundFiles.DOSAGE_QUESTION_2)) })
                .setTransitions(
                        new Object[][] { { "1", getPillTakenTransition() }, { "3", getPillNotTakenTransition() } }));
        tree.setRootTransition(rootTransition);

        decisionTreeService.saveDecisionTree(tree);
    }

    private String getCmsliteUrlFor(String soundFilename) {
        return settings.getMotechUrl() + CMSLITE_STREAM_PATH + "en/" + soundFilename;
    }

    private Transition getPillNotTakenTransition() {
        EventTransition transition = new EventTransition();
        transition.setEventSubject(Events.PATIENT_MISSED_DOSAGE);
        transition.setDestinationNode(new Node().setPrompts(new AudioPrompt()
                .setAudioFileUrl(getCmsliteUrlFor(SoundFiles.DOSAGE_ANSWER_NO))));
        transition.setName("missed dosage");
        return transition;
    }

    private Transition getPillTakenTransition() {
        EventTransition transition = new EventTransition();
        transition.setEventSubject(Events.PATIENT_TOOK_DOSAGE);
        transition.setDestinationNode(new Node().setPrompts(new AudioPrompt()
                .setAudioFileUrl(getCmsliteUrlFor(SoundFiles.DOSAGE_ANSWER_YES))));
        transition.setName("pill taken");
        return transition;
    }
}
