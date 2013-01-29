package org.motechproject.demo.pillreminder.mrs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.mrs.domain.User;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.model.OpenMRSProvider;
import org.motechproject.mrs.model.OpenMRSUser;
import org.motechproject.mrs.services.UserAdapter;

public class MrsUserResolverTest {

    @Mock
    private UserAdapter userAdapter;

    private MrsUserResolver userResolver;

    @Before
    public void setUp() {
        initMocks(this);
        userResolver = new MrsUserResolver(userAdapter);
    }

    @Test
    public void shouldReturnMotechUserWhenFound() {
        when(userAdapter.getUserByUserName(MrsUserResolver.MOTECH_USERNAME)).thenReturn(stubUser());

        OpenMRSProvider provider = userResolver.resolveMotechUser();

        assertNotNull(provider);
    }

    private User stubUser() {
        OpenMRSUser user = new OpenMRSUser();
        user.setPerson(new OpenMRSPerson());
        user.getPerson().setPersonId("1");
        return user;
    }

    @Test
    public void shouldCreateNewUserWhenMotechUserNotFound() throws UserAlreadyExistsException {
        stubSaveUser();
        userResolver.resolveMotechUser();

        verify(userAdapter).saveUser(any(User.class));
    }

    private void stubSaveUser() throws UserAlreadyExistsException {
        Map<String, Object> saved = new HashMap<>();
        saved.put(UserAdapter.USER_KEY, stubUser());
        when(userAdapter.saveUser(any(User.class))).thenReturn(saved);
    }

    @Test
    public void shouldUseProviderRoleWhenCreatingMotechUser() throws UserAlreadyExistsException {
        stubSaveUser();
        userResolver.resolveMotechUser();

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userAdapter).saveUser(savedUser.capture());

        assertEquals("Provider", savedUser.getValue().getSecurityRole());
    }

    @Test
    public void shouldRequeryForUserIfSaveFails() throws UserAlreadyExistsException {
        when(userAdapter.getUserByUserName(MrsUserResolver.MOTECH_USERNAME)).thenReturn(null).thenReturn(stubUser());
        when(userAdapter.saveUser(any(User.class))).thenThrow(new UserAlreadyExistsException());

        OpenMRSProvider provider = userResolver.resolveMotechUser();
        
        assertEquals("1", provider.getProviderId());
    }
}
