package org.motechproject.demo.pillreminder.mrs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.domain.Encounter;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSProvider;
import org.motechproject.mrs.services.EncounterAdapter;

public class MrsEncounterUpdaterTest {

    @Mock
    private MrsEntityFacade mrsEntityFacade;

    @Mock
    private EncounterAdapter encounterAdapter;

    private MrsEncounterCreator updater;

    @Before
    public void setUp() {
        initMocks(this);
        updater = new MrsEncounterCreator(mrsEntityFacade, encounterAdapter);
    }

    @Test
    public void shouldCreateEncounter() {
        when(mrsEntityFacade.findPatientByMotechId("700")).thenReturn(new OpenMRSPatient("700"));
        when(mrsEntityFacade.findMotechFacility()).thenReturn(new OpenMRSFacility(null));
        when(mrsEntityFacade.findMotechUser()).thenReturn(new OpenMRSProvider());

        updater.createPillTakenEncounterForPatient("700");

        verify(encounterAdapter).createEncounter(any(Encounter.class));
    }
}
