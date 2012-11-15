package org.motechproject.demo.pillreminder.mrs;

import java.util.Date;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MrsUserResolver {

    public static final String MOTECH_USERNAME = "motech";
    private MRSUserAdapter userAdapter;

    @Autowired
    public MrsUserResolver(MRSUserAdapter userAdapter) {
        this.userAdapter = userAdapter;
    }

    public MRSUser resolveMotechUser() {
        MRSUser user = userAdapter.getUserByUserName(MOTECH_USERNAME);
        if (user == null) {
            user = createMotechUser();
        }

        return user;
    }

    private MRSUser createMotechUser() {
        MRSUser user = createUserWithPerson(createPerson());
        return saveMotechUser(user);
    }

    private MRSPerson createPerson() {
        MRSPerson person = new MRSPerson();
        person.firstName("Motech").lastName("Motech").address("None").dateOfBirth(new Date()).gender("F");
        return person;
    }

    private MRSUser createUserWithPerson(MRSPerson person) {
        MRSUser user = new MRSUser();
        user.userName(MOTECH_USERNAME).securityRole("Provider").person(person);
        return user;
    }

    private MRSUser saveMotechUser(MRSUser user) {
        try {
            user = (MRSUser) userAdapter.saveUser(user).get(MRSUserAdapter.USER_KEY);
        } catch (UserAlreadyExistsException e) {
            user = userAdapter.getUserByUserName(MOTECH_USERNAME);
        }
        return user;
    }
}
