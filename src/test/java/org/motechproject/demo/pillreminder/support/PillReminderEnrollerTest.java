package org.motechproject.demo.pillreminder.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.demo.pillreminder.domain.EnrollmentRequest;
import org.motechproject.demo.pillreminder.domain.EnrollmentResponse;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.api.contract.DosageRequest;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.api.service.PillReminderService;

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
        EnrollmentResponse response = enroller.enrollPatientWithId(getRequest("558", null, null, null));

        assertEquals(1, response.errorCount());
        assertTrue(response.getError(0).contains("No MRS Patient Found with id"));
    }

    private EnrollmentRequest getRequest(String motechId, String pin, String phonenumber, String dosageStartTime) {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setMotechId(motechId);
        request.setPin(pin);
        request.setPhonenumber(phonenumber);
        request.setDosageStartTime(dosageStartTime);
        return request;
    }

    @Test
    public void shouldGiveErrorIfAttributeTypeIsMissingFromOpenMrs() {
        PillReminderEnroller enroller = new PillReminderEnroller(pillReminderService, patientAdapter);

        MRSPerson person = new MRSPerson();
        MRSPatient patient = new MRSPatient(null, person, null);

        when(patientAdapter.getPatientByMotechId("558")).thenReturn(patient);
        when(patientAdapter.updatePatient(any(MRSPatient.class))).thenThrow(
                new RuntimeException("Attribute type not found"));

        EnrollmentResponse response = enroller.enrollPatientWithId(getRequest("558", "1234", "notused", null));

        assertEquals(1, response.errorCount());
        assertTrue(response.getError(0).contains("OpenMRS does not have person attribute type: Pin or Phone Number."));
    }

    @Test
    public void shouldGiveErrorIfAlreadyEnrolledInRegimen() {
        PillReminderEnroller enroller = new PillReminderEnroller(pillReminderService, patientAdapter);
        when(pillReminderService.getPillRegimen("558")).thenReturn(new PillRegimenResponse("558", null, 0, 0, 0, null));

        EnrollmentResponse response = enroller.enrollPatientWithId(getRequest("558", "1234", "notused", null));
        
        assertTrue(response.getError(0).contains("Patient is already enrolled in Pill Reminder Regimen"));
    }

    @Test
    public void shouldEnrollWithStartTimeInTwoMinutes() {
        PillReminderEnroller enroller = new PillReminderEnroller(pillReminderService, patientAdapter);

        stubValidPatient();

        enroller.enrollPatientWithId(getRequest("558", null, null, "10:05"));

        ArgumentCaptor<DailyPillRegimenRequest> captor = ArgumentCaptor.forClass(DailyPillRegimenRequest.class);
        verify(pillReminderService).createNew(captor.capture());

        DailyPillRegimenRequest request = captor.getValue();
        DosageRequest dosage = request.getDosageRequests().get(0);

        assertEquals(10, dosage.getStartHour());
        assertEquals(5, dosage.getStartMinute());
    }

    private void stubValidPatient() {
        MRSPerson savedPerson = new MRSPerson();
        savedPerson.addAttribute(new Attribute("Phone Number", ""));
        savedPerson.addAttribute(new Attribute("Pin", ""));
        MRSPatient savedPatient = new MRSPatient(null, savedPerson, null);
        when(patientAdapter.getPatientByMotechId("558")).thenReturn(savedPatient);
    }

    @Test
    public void shouldEnrollWithPrebuiltRegimenWithOneDosage() {
        PillReminderEnroller enroller = new PillReminderEnroller(pillReminderService, patientAdapter);

        stubValidPatient();

        enroller.enrollPatientWithId(getRequest("558", null, null, "10:05"));

        ArgumentCaptor<DailyPillRegimenRequest> captor = ArgumentCaptor.forClass(DailyPillRegimenRequest.class);
        verify(pillReminderService).createNew(captor.capture());

        DailyPillRegimenRequest request = captor.getValue();
        assertEquals(1, request.getDosageRequests().size());
    }

    @Test
    public void shouldEnrollWithPrebuiltRegimenWithOneMedicine() {
        PillReminderEnroller enroller = new PillReminderEnroller(pillReminderService, patientAdapter);

        stubValidPatient();

        enroller.enrollPatientWithId(getRequest("558", null, null, "10:05"));

        ArgumentCaptor<DailyPillRegimenRequest> captor = ArgumentCaptor.forClass(DailyPillRegimenRequest.class);
        verify(pillReminderService).createNew(captor.capture());

        DailyPillRegimenRequest request = captor.getValue();
        List<DosageRequest> dosageRequest = request.getDosageRequests();
        assertEquals(1, dosageRequest.size());
    }

    @Test
    public void shouldEnrollWithEndDateInOneDay() {
        PillReminderEnroller enroller = new PillReminderEnroller(pillReminderService, patientAdapter);

        stubValidPatient();

        enroller.enrollPatientWithId(getRequest("558", null, null, "10:05"));

        ArgumentCaptor<DailyPillRegimenRequest> captor = ArgumentCaptor.forClass(DailyPillRegimenRequest.class);
        verify(pillReminderService).createNew(captor.capture());

        DailyPillRegimenRequest request = captor.getValue();
        DosageRequest dosage = request.getDosageRequests().get(0);

        DateTime now = DateUtil.now();
        DateTime tomorrow = now.plusDays(1);

        assertEquals(tomorrow.toLocalDate(), dosage.getMedicineRequests().get(0).getEndDate());
    }

    @Test
    public void shouldReturnStartingTimeOfDosageWithBufferIncluded() {
        PillReminderEnroller enroller = new PillReminderEnroller(pillReminderService, patientAdapter);

        stubValidPatient();

        EnrollmentResponse response = enroller.enrollPatientWithId(getRequest("558", null, null, "10:05"));

        assertEquals("10:06", response.getStartTime());
    }
}
