package org.motechproject.demo.pillreminder.mrs;

import java.util.List;

import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.services.FacilityAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resolves the MOTECH facility from the OpenMRS. If the facility does not
 * exist, then a default facility is created
 */
@Component
public class MrsFacilityResolver {

    private FacilityAdapter facilityAdapter;

    @Autowired
    public MrsFacilityResolver(FacilityAdapter facilityAdapter) {
        this.facilityAdapter = facilityAdapter;
    }

    public OpenMRSFacility resolveMotechFacility() {
        OpenMRSFacility motechFacility = searchForMotechFacility();

        if (motechFacility == null) {
            motechFacility = createMotechFacility();
        }

        return motechFacility;
    }

    private OpenMRSFacility searchForMotechFacility() {
        List<? extends Facility> facilities = facilityAdapter.getFacilities("Motech");
        return facilities.isEmpty() ? null : (OpenMRSFacility) facilities.get(0);
    }

    private OpenMRSFacility createMotechFacility() {
        OpenMRSFacility facility = new OpenMRSFacility("Motech", "USA", "King County", "Seattle", "WA");
        facility = (OpenMRSFacility) facilityAdapter.saveFacility(facility);
        return facility;
    }
}
