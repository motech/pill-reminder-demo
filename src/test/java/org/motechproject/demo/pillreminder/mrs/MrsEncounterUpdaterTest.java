package org.motechproject.demo.pillreminder.mrs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSEncounterAdapter;

public class MrsEncounterUpdaterTest {

    @Mock
    private MrsEntityFinder mrsEntityFinder;

    @Mock
    private MRSEncounterAdapter encounterAdapter;

    private MrsEncounterUpdater updater;

    @Before
    public void setUp() {
        initMocks(this);
        updater = new MrsEncounterUpdater(mrsEntityFinder, encounterAdapter);
    }

    @Test
    public void shouldCreateEncounter() {
        when(mrsEntityFinder.findPatientByMotechId("700")).thenReturn(new MRSPatient("700"));
        when(mrsEntityFinder.findMotechFacility()).thenReturn(new MRSFacility(null));
        when(mrsEntityFinder.findMotechUser()).thenReturn(new MRSUser());
        
        updater.addPillTakenEncounterToPatient("700");
        
        verify(encounterAdapter).createEncounter(any(MRSEncounter.class));
    }
}
