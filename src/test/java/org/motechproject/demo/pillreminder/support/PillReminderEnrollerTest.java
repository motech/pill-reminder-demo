package org.motechproject.demo.pillreminder.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.demo.pillreminder.domain.EnrollmentResponse;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.api.contract.DosageRequest;
import org.motechproject.server.pillreminder.api.service.PillReminderService;
import org.motechproject.util.DateUtil;

public class PillReminderEnrollerTest {

    @Mock
    PillReminderService pillReminderService;
    
    @Mock
    MRSPatientAdapter patientAdapter;
    
    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGiveErrorOnInvalidMrsPatient() {
        PillReminderEnroller enroller = new PillReminderEnroller(pillReminderService, patientAdapter);
        EnrollmentResponse response = enroller.enrollPatientWithId("558");
        
        assertEquals(1, response.errorCount());
        assertTrue(response.getError(0).contains("No MRS Patient Found with id"));
    }
    
    @Test
    public void shouldEnrollWithStartTimeInFiveMinutes() {
        PillReminderEnroller enroller = new PillReminderEnroller(pillReminderService, patientAdapter);
        
        when(patientAdapter.getPatientByMotechId("558")).thenReturn(new MRSPatient(null));
        
        enroller.enrollPatientWithId("558");
        
        ArgumentCaptor<DailyPillRegimenRequest> captor = ArgumentCaptor.forClass(DailyPillRegimenRequest.class);
        verify(pillReminderService).createNew(captor.capture());
        
        DailyPillRegimenRequest request = captor.getValue();
        DosageRequest dosage = request.getDosageRequests().get(0);

        DateTime now = DateUtil.now();
        DateTime inFiveMinutes = now.plusMinutes(5);
        
        int hour = inFiveMinutes.getHourOfDay();
        int minute = inFiveMinutes.getMinuteOfHour();
        
        assertEquals(hour, dosage.getStartHour());
        assertEquals(minute, dosage.getStartMinute());
    }
    
    @Test
    public void shouldEnrollWithPrebuiltRegimenWithOneDosage() {
        PillReminderEnroller enroller = new PillReminderEnroller(pillReminderService, patientAdapter);
        
        when(patientAdapter.getPatientByMotechId("558")).thenReturn(new MRSPatient(null));
        
        enroller.enrollPatientWithId("558");
        
        ArgumentCaptor<DailyPillRegimenRequest> captor = ArgumentCaptor.forClass(DailyPillRegimenRequest.class);
        verify(pillReminderService).createNew(captor.capture());
        
        DailyPillRegimenRequest request = captor.getValue();
        assertEquals(1, request.getDosageRequests().size());
    }

    @Test
    public void shouldEnrollWithPrebuiltRegimenWithOneMedicine() {
        PillReminderEnroller enroller = new PillReminderEnroller(pillReminderService, patientAdapter);
        
        when(patientAdapter.getPatientByMotechId("558")).thenReturn(new MRSPatient(null));
        
        enroller.enrollPatientWithId("558");
        
        ArgumentCaptor<DailyPillRegimenRequest> captor = ArgumentCaptor.forClass(DailyPillRegimenRequest.class);
        verify(pillReminderService).createNew(captor.capture());
        
        DailyPillRegimenRequest request = captor.getValue();
        List<DosageRequest> dosageRequest = request.getDosageRequests();
        assertEquals(1, dosageRequest.size());
    }

    
    @Test
    public void shouldEnrollWithEndDateInOneDay() {
        PillReminderEnroller enroller = new PillReminderEnroller(pillReminderService, patientAdapter);
        
        when(patientAdapter.getPatientByMotechId("558")).thenReturn(new MRSPatient(null));
        
        enroller.enrollPatientWithId("558");
        
        ArgumentCaptor<DailyPillRegimenRequest> captor = ArgumentCaptor.forClass(DailyPillRegimenRequest.class);
        verify(pillReminderService).createNew(captor.capture());
        
        DailyPillRegimenRequest request = captor.getValue();
        DosageRequest dosage = request.getDosageRequests().get(0);

        DateTime now = DateUtil.now();
        DateTime tomorrow = now.plusDays(1);
        
        assertEquals(tomorrow.toLocalDate(), dosage.getMedicineRequests().get(0).getEndDate());
    }
}
