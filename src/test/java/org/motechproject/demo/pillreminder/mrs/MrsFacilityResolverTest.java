package org.motechproject.demo.pillreminder.mrs;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.services.FacilityAdapter;

public class MrsFacilityResolverTest {

    @Mock
    private FacilityAdapter facilityAdapter;
    private MrsFacilityResolver facilityResolver;

    @Before
    public void setUp() {
        initMocks(this);
        facilityResolver = new MrsFacilityResolver(facilityAdapter);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldReturnFirstFoundFacility() {
        OpenMRSFacility facility1 = new OpenMRSFacility("1");
        OpenMRSFacility facility2 = new OpenMRSFacility("2");
        List<OpenMRSFacility> facilities = Arrays.asList(facility1, facility2);
        when(facilityAdapter.getFacilities("Motech")).thenReturn((List) facilities);

        OpenMRSFacility resolvedFacility = facilityResolver.resolveMotechFacility();

        assertEquals("1", resolvedFacility.getId());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldCreateNewFacilityWhenNonFound() {
        when(facilityAdapter.getFacilities("Motech")).thenReturn((List)Collections.<OpenMRSFacility> emptyList());

        facilityResolver.resolveMotechFacility();

        verify(facilityAdapter).saveFacility(any(Facility.class));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldReturnNewlySavedFacilityWhenResolved() {
        when(facilityAdapter.getFacilities("Motech")).thenReturn((List)Collections.<OpenMRSFacility> emptyList());
        when(facilityAdapter.saveFacility(any(Facility.class))).thenReturn(new OpenMRSFacility("5"));

        OpenMRSFacility saved = facilityResolver.resolveMotechFacility();

        assertEquals("5", saved.getId());
    }
}
