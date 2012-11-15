package org.motechproject.demo.pillreminder.mrs;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.services.MRSFacilityAdapter;

public class MrsFacilityResolverTest {

    @Mock
    private MRSFacilityAdapter facilityAdapter;
    private MrsFacilityResolver facilityResolver;

    @Before
    public void setUp() {
        initMocks(this);
        facilityResolver = new MrsFacilityResolver(facilityAdapter);
    }

    @Test
    public void shouldReturnFirstFoundFacility() {
        MRSFacility facility1 = new MRSFacility("1");
        MRSFacility facility2 = new MRSFacility("2");

        when(facilityAdapter.getFacilities("Motech")).thenReturn(Arrays.asList(facility1, facility2));

        MRSFacility resolvedFacility = facilityResolver.resolveMotechFacility();

        assertEquals("1", resolvedFacility.getId());
    }

    @Test
    public void shouldCreateNewFacilityWhenNonFound() {
        when(facilityAdapter.getFacilities("Motech")).thenReturn(Collections.<MRSFacility> emptyList());

        facilityResolver.resolveMotechFacility();

        verify(facilityAdapter).saveFacility(any(MRSFacility.class));
    }

    @Test
    public void shouldReturnNewlySavedFacilityWhenResolved() {
        when(facilityAdapter.getFacilities("Motech")).thenReturn(Collections.<MRSFacility> emptyList());
        when(facilityAdapter.saveFacility(any(MRSFacility.class))).thenReturn(new MRSFacility("5"));

        MRSFacility saved = facilityResolver.resolveMotechFacility();

        assertEquals("5", saved.getId());
    }
}
