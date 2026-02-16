package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryUserDao;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class tests the UserService.
 */
public class UserServiceTests {

    private MemoryUserDao userDao;
    private MemoryAuthDao authDao;
    private UserService service;

    @BeforeEach
    public void setup() {
        userDao = new MemoryUserDao();
        authDao = new MemoryAuthDao();
        service = new UserService(userDao, authDao);
    }

    @Test
    public void registerSuccess() throws DataAccessException {
        UserData user = new UserData("benjamim", "password123", "ben@byu.edu");
        AuthData result = service.register(user);
        
        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("benjamim", result.username());
    }

    @Test
    public void registerFailDuplicate() throws DataAccessException {
        UserData user = new UserData("benjamim", "password123", "ben@byu.edu");
        service.register(user);
        
        // Try to register same user again
        Assertions.assertThrows(DataAccessException.class, () -> {
             service.register(user);
        });
    }

    @Test
    public void loginSuccess() throws DataAccessException {
        UserData user = new UserData("benjamim", "password123", "ben@byu.edu");
        service.register(user);
        
        AuthData loginResult = service.login(user);
        Assertions.assertNotNull(loginResult.authToken());
    }

    @Test
    public void loginFailWrongPassword() throws DataAccessException {
        UserData user = new UserData("benjamim", "password123", "ben@byu.edu");
        service.register(user);
        
        UserData wrongPassUser = new UserData("benjamim", "wrong", "ben@byu.edu");
        
        Assertions.assertThrows(DataAccessException.class, () -> {
             service.login(wrongPassUser);
        });
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        UserData user = new UserData("benjamim", "password123", "ben@byu.edu");
        AuthData auth = service.register(user);
        
        // Should not throw exception
        service.logout(auth.authToken());
        
        // Verify token is gone (checking dao manually just to be sure)
        Assertions.assertNull(authDao.getAuth(auth.authToken()));
    }

    @Test
    public void logoutFailBadToken() {
        Assertions.assertThrows(DataAccessException.class, () -> {
             service.logout("fake-token-123");
        });
    }
}