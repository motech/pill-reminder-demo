package org.motechproject.demo.pillreminder.support;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.demo.pillreminder.content.SoundFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class SoundContentLoader {
    private final Logger logger = LoggerFactory.getLogger(SoundContentLoader.class);

    private CMSLiteService cmsliteService;
    private ResourceLoader resourceLoader;

    @Autowired
    public SoundContentLoader(CMSLiteService cmsliteService, ResourceLoader resourceLoader) {
        this.cmsliteService = cmsliteService;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadSoundContent() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                loadSoundFiles();
            }

        }).start();
    }

    private void loadSoundFiles() {
        logger.info("Attempting to load and save all wav files");

        loadSoundFile(SoundFiles.PIN_REQUEST);
        loadSoundFile(SoundFiles.INCORRECT_PIN);
        loadSoundFile(SoundFiles.DOSAGE_QUESTION_1);
        loadSoundFile(SoundFiles.DOSAGE_QUESTION_2);
        loadSoundFile(SoundFiles.DOSAGE_ANSWER_YES);
        loadSoundFile(SoundFiles.DOSAGE_ANSWER_NO);
    }

    private void loadSoundFile(String soundFilename) {
        Resource soundFile = resourceLoader.getResource(SoundFiles.SOUNDS_DIRECTORY + soundFilename
                + SoundFiles.SOUND_EXTENSION);

        StreamContent content;
        try {
            content = new StreamContent("en", soundFilename, soundFile.getInputStream(), null,
                    SoundFiles.SOUND_CONTENT_TYPE);
            cmsliteService.addContent(content);
        } catch (IOException e) {
            logger.error("There was a problem reading the sound file " + soundFilename + SoundFiles.SOUND_EXTENSION);
        } catch (CMSLiteException e) {
            logger.warn("CMSLite aleady has sound file " + soundFilename + SoundFiles.SOUND_EXTENSION);
        }
    }
}
