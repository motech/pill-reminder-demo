package org.motechproject.demo.pillreminder.mrs;

import java.util.List;

import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resolves the MOTECH facility from the OpenMRS. If the facility does not
 * exist, then a default facility is created
 */
@Component
public class MrsFacilityResolver {

    private MRSFacilityAdapter facilityAdapter;

    @Autowired
    public MrsFacilityResolver(MRSFacilityAdapter facilityAdapter) {
        this.facilityAdapter = facilityAdapter;
    }

    public MRSFacility resolveMotechFacility() {
        MRSFacility motechFacility = searchForMotechFacility();

        if (motechFacility == null) {
            motechFacility = createMotechFacility();
        }

        return motechFacility;
    }

    private MRSFacility searchForMotechFacility() {
        List<MRSFacility> facilities = facilityAdapter.getFacilities("Motech");
        return facilities.isEmpty() ? null : facilities.get(0);
    }

    private MRSFacility createMotechFacility() {
        MRSFacility facility = new MRSFacility("Motech", "USA", "King County", "Seattle", "WA");
        facility = facilityAdapter.saveFacility(facility);
        return facility;
    }
}
