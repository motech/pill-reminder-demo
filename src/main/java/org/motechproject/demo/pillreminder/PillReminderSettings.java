package org.motechproject.demo.pillreminder;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PillReminderSettings {
    private final Logger logger = LoggerFactory.getLogger(PillReminderSettings.class);

    private static final String MAX_RETRY_COUNT_PROPERTY = "max.reminder.retry";
    private static final String MOTECH_URL_PROPERTY = "motech.url";
    private static final String CALLER_ID_PROPERTY = "caller.id";

    private SettingsFacade settingsFacade;

    @Autowired
    public PillReminderSettings(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    public String getMotechUrl() {
        return settingsFacade.getProperty(MOTECH_URL_PROPERTY);
    }

    public int getMaxRetryCount() {
        int maxRetryCount = 1;
        try {
            maxRetryCount = Integer.parseInt(settingsFacade.getProperty(MAX_RETRY_COUNT_PROPERTY));
        } catch (Exception e) {
            logger.warn(MAX_RETRY_COUNT_PROPERTY + " property does not have a valid value");
        }

        return maxRetryCount;
    }

    public String getCallerId() {
        String callerId = "";
        if (StringUtils.isNotBlank(settingsFacade.getProperty(CALLER_ID_PROPERTY))) {
            callerId = settingsFacade.getProperty(CALLER_ID_PROPERTY);
        }
        return callerId;
    }
}
