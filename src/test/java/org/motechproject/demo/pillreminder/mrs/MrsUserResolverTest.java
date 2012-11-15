package org.motechproject.demo.pillreminder.mrs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSUserAdapter;

public class MrsUserResolverTest {

    @Mock
    private MRSUserAdapter userAdapter;

    private MrsUserResolver userResolver;

    @Before
    public void setUp() {
        initMocks(this);
        userResolver = new MrsUserResolver(userAdapter);
    }

    @Test
    public void shouldReturnMotechUserWhenFound() {
        when(userAdapter.getUserByUserName(MrsUserResolver.MOTECH_USERNAME)).thenReturn(new MRSUser());

        MRSUser user = userResolver.resolveMotechUser();

        assertNotNull(user);
    }

    @Test
    public void shouldCreateNewUserWhenMotechUserNotFound() throws UserAlreadyExistsException {
        userResolver.resolveMotechUser();

        verify(userAdapter).saveUser(any(MRSUser.class));
    }

    @Test
    public void shouldUseProviderRoleWhenCreatingMotechUser() throws UserAlreadyExistsException {
        userResolver.resolveMotechUser();

        ArgumentCaptor<MRSUser> savedUser = ArgumentCaptor.forClass(MRSUser.class);
        verify(userAdapter).saveUser(savedUser.capture());

        assertEquals("Provider", savedUser.getValue().getSecurityRole());
    }

    @Test
    public void shouldRequeryForUserIfSaveFails() throws UserAlreadyExistsException {
        when(userAdapter.getUserByUserName(MrsUserResolver.MOTECH_USERNAME)).thenReturn(null).thenReturn(new MRSUser().id("1"));
        when(userAdapter.saveUser(any(MRSUser.class))).thenThrow(new UserAlreadyExistsException());

        MRSUser user = userResolver.resolveMotechUser();
        
        assertEquals("1", user.getId());
    }
}
