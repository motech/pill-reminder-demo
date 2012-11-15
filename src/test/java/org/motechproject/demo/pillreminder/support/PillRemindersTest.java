package org.motechproject.demo.pillreminder.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.demo.pillreminder.domain.PillReminderResponse;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.api.contract.DosageResponse;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.api.service.PillReminderService;

public class PillRemindersTest {

    @Mock
    private PillReminderService pillReminderService;

    private PillReminders pillReminders;

    @Before
    public void setUp() {
        initMocks(this);
        pillReminders = new PillReminders(pillReminderService);
    }

    @Test
    public void shouldTransformFoundRegimenIntoResponse() {
        when(pillReminderService.getPillRegimen("700")).thenReturn(createPillRegimentResponse());

        PillReminderResponse response = pillReminders.findPillReminderByMotechId("700");

        assertEquals("11:00", response.getStartTime());
        assertEquals("2012-05-18", response.getLastCapturedDate());
    }

    private PillRegimenResponse createPillRegimentResponse() {
        DosageResponse dosageResponse = new DosageResponse(null, new Time(11, 0), null, null,
                new LocalDate(2012, 5, 18), null);
        return regimenWithDosage(dosageResponse);
    }

    private PillRegimenResponse regimenWithDosage(DosageResponse dosageResponse) {
        PillRegimenResponse response = new PillRegimenResponse(null, null, 0, 0, 0, Arrays.asList(dosageResponse));
        return response;
    }

    @Test
    public void shouldShowNoResponseDateIfNull() {
        when(pillReminderService.getPillRegimen("700")).thenReturn(createPillRegimenWithoutLastResponse());

        PillReminderResponse response = pillReminders.findPillReminderByMotechId("700");

        assertTrue(response.getLastCapturedDate().contains("No response captured yet"));
    }

    private PillRegimenResponse createPillRegimenWithoutLastResponse() {
        DosageResponse dosageResponse = new DosageResponse(null, new Time(11, 0), null, null, null, null);
        return regimenWithDosage(dosageResponse);
    }

    @Test
    public void shouldReturnEmptyResponseWhenNoReminderFound() {
        PillReminderResponse response = pillReminders.findPillReminderByMotechId("700");

        assertNotNull(response);
    }

    @Test
    public void shouldDeletePillReminder() {
        pillReminders.deletePillReminder("700");

        verify(pillReminderService).remove("700");
    }
}
