package org.motechproject.demo.pillreminder.mrs;

import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.model.OpenMRSProvider;
import org.motechproject.mrs.model.OpenMRSUser;
import org.motechproject.mrs.services.UserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resolves the Motech user from the OpenMRS application. If the Motech user
 * does not exist, it will create a new Motech user
 */
@Component
public class MrsUserResolver {

    public static final String MOTECH_USERNAME = "motech";
    private UserAdapter userAdapter;

    @Autowired
    public MrsUserResolver(UserAdapter userAdapter) {
        this.userAdapter = userAdapter;
    }

    public OpenMRSProvider resolveMotechUser() {
        OpenMRSUser user = (OpenMRSUser) userAdapter.getUserByUserName(MOTECH_USERNAME);
        if (user == null) {
            user = createMotechUser();
        }
        
        OpenMRSProvider provider = new OpenMRSProvider();
        provider.setProviderId(user.getPerson().getId());
        provider.setPerson(user.getPerson());
        return provider;
    }

    private OpenMRSUser createMotechUser() {
        OpenMRSUser user = createUserWithPerson(createPerson());
        return saveMotechUser(user);
    }

    private OpenMRSPerson createPerson() {
        OpenMRSPerson person = new OpenMRSPerson();
        person.firstName("Motech").lastName("Motech").address("None").dateOfBirth(DateUtil.now()).gender("F");
        return person;
    }

    private OpenMRSUser createUserWithPerson(OpenMRSPerson person) {
        OpenMRSUser user = new OpenMRSUser();
        user.userName(MOTECH_USERNAME).securityRole("Provider").person(person);
        return user;
    }

    private OpenMRSUser saveMotechUser(OpenMRSUser user) {
        try {
            user = (OpenMRSUser) userAdapter.saveUser(user).get(UserAdapter.USER_KEY);
        } catch (UserAlreadyExistsException e) {
            user = (OpenMRSUser) userAdapter.getUserByUserName(MOTECH_USERNAME);
        }
        return user;
    }
}
