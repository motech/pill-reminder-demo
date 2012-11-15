package org.motechproject.demo.pillreminder.mrs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.demo.pillreminder.domain.MrsPatientSearchResult;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;

public class MrsPatientSearcherTest {

    @Mock
    private MrsEntityFinder mrsEntityFinder;

    private MrsPatientSearcher patientSearcher;

    @Before
    public void setUp() {
        initMocks(this);
        patientSearcher = new MrsPatientSearcher(mrsEntityFinder);
    }

    @Test
    public void shouldReturnEmptyResultWhenNoPatientFound() {
        MrsPatientSearchResult result = patientSearcher.searchForPatientsWithMotechId("700");

        assertNotNull(result);
        assertNull(result.getMotechId());
    }

    @Test
    public void shouldFillInPatientFieldsWhenFound() {
        MRSPerson person = new MRSPerson().firstName("Poland").lastName("Spring");
        MRSPatient patient = new MRSPatient("700", person, null);

        when(mrsEntityFinder.findPatientByMotechId("700")).thenReturn(patient);
        
        MrsPatientSearchResult result = patientSearcher.searchForPatientsWithMotechId("700");
        
        assertEquals("Poland", result.getFirstName());
        assertEquals("Spring", result.getLastName());
        assertEquals("700", result.getMotechId());
    }

}
