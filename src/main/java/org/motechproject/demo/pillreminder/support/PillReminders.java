package org.motechproject.demo.pillreminder.support;

import org.motechproject.demo.pillreminder.domain.PillReminderResponse;

public interface PillReminders {

    PillReminderResponse findPillReminderByMotechId(String motechId);

    void deletePillReminder(String motechId);

    void setDosageStatusKnownForPatient(String motechId);

    boolean isPatientInPillRegimen(String motechId);

    String registerNewPatientIntoPillRegimen(String motechId, String dosageStartTime);

}
