package org.motechproject.demo.pillreminder.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.demo.pillreminder.domain.EnrollmentRequest;
import org.motechproject.demo.pillreminder.domain.EnrollmentResponse;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.model.OpenMRSAttribute;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.services.PatientAdapter;

public class PillReminderEnrollerTest {

    @Mock
    PillReminders pillReminders;

    @Mock
    PatientAdapter patientAdapter;

    PillReminderEnroller enroller;

    @Before
    public void setUp() {
        initMocks(this);
        enroller = new PillReminderEnroller(pillReminders, patientAdapter);
    }

    @Test
    public void shouldGiveErrorOnInvalidMrsPatient() {
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
        OpenMRSPerson person = new OpenMRSPerson();
        OpenMRSPatient patient = new OpenMRSPatient(null, person, null);

        when(patientAdapter.getPatientByMotechId("558")).thenReturn(patient);
        when(patientAdapter.updatePatient(any(Patient.class))).thenThrow(
                new RuntimeException("Attribute type not found"));

        EnrollmentResponse response = enroller.enrollPatientWithId(getRequest("558", "1234", "notused", null));

        assertEquals(1, response.errorCount());
        assertTrue(response.getError(0).contains("OpenMRS does not have person attribute type: Pin or Phone Number."));
    }

    @Test
    public void shouldGiveErrorIfAlreadyEnrolledInRegimen() {
        when(pillReminders.isPatientInPillRegimen("558")).thenReturn(true);

        EnrollmentResponse response = enroller.enrollPatientWithId(getRequest("558", "1234", "notused", null));

        assertTrue(response.getError(0).contains("Patient is already enrolled in Pill Reminder Regimen"));
    }
    
    @Test
    public void shouldUpdateMrsPatientAttributes() {
        stubValidPatient();
        enroller.enrollPatientWithId(getRequest("558", null, null, "10:05"));

        verify(patientAdapter).updatePatient(any(Patient.class));
    }

    @Test
    public void shouldRegisterPatient() {
        stubValidPatient();
        enroller.enrollPatientWithId(getRequest("558", null, null, "10:05"));

        verify(pillReminders).registerNewPatientIntoPillRegimen(any(String.class), any(String.class));
    }

    private void stubValidPatient() {
        OpenMRSPerson savedPerson = new OpenMRSPerson();
        savedPerson.addAttribute(new OpenMRSAttribute("Phone Number", ""));
        savedPerson.addAttribute(new OpenMRSAttribute("Pin", ""));
        OpenMRSPatient savedPatient = new OpenMRSPatient(null, savedPerson, null);
        when(patientAdapter.getPatientByMotechId("558")).thenReturn(savedPatient);
    }
}
