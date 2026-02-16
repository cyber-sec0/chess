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
 * This class is testing the user service logic.
 * It is important to clean the database before every test.
 */
public class UserServiceTests {

    private MemoryUserDao memoryUserDaoTool;
    private MemoryAuthDao memoryAuthDaoTool;
    private UserService userServiceLogic;

    @BeforeEach
    public void setupMethodForTests() {
        memoryUserDaoTool = new MemoryUserDao();
        memoryAuthDaoTool = new MemoryAuthDao();
        
        // Clear the data because the list is static
        memoryUserDaoTool.clear();
        memoryAuthDaoTool.clear();
        
        userServiceLogic = new UserService(memoryUserDaoTool, memoryAuthDaoTool);
    }

    @Test
    public void registerSuccessTest() throws DataAccessException {
        UserData userInformation = new UserData("benjamim", "password123", "ben@byu.edu");
        AuthData resultData = userServiceLogic.register(userInformation);
        Assertions.assertNotNull(resultData.authToken());
        Assertions.assertEquals("benjamim", resultData.username());
    }

    @Test
    public void registerFailDuplicateTest() throws DataAccessException {
        UserData userInformation = new UserData("benjamim", "password123", "ben@byu.edu");
        userServiceLogic.register(userInformation);
        Assertions.assertThrows(DataAccessException.class, () -> userServiceLogic.register(userInformation));
    }

    @Test
    public void loginSuccessTest() throws DataAccessException {
        UserData userInformation = new UserData("benjamim", "password123", "ben@byu.edu");
        userServiceLogic.register(userInformation);
        AuthData loginResultData = userServiceLogic.login(userInformation);
        Assertions.assertNotNull(loginResultData.authToken());
    }

    @Test
    public void loginFailWrongPasswordTest() throws DataAccessException {
        UserData userInformation = new UserData("benjamim", "password123", "ben@byu.edu");
        userServiceLogic.register(userInformation);
        UserData wrongPassUserInformation = new UserData("benjamim", "wrong", "ben@byu.edu");
        Assertions.assertThrows(DataAccessException.class, () -> userServiceLogic.login(wrongPassUserInformation));
    }

    @Test
    public void logoutSuccessTest() throws DataAccessException {
        UserData userInformation = new UserData("benjamim", "password123", "ben@byu.edu");
        AuthData authData = userServiceLogic.register(userInformation);
        userServiceLogic.logout(authData.authToken());
        Assertions.assertNull(memoryAuthDaoTool.getAuth(authData.authToken()));
    }

    @Test
    public void logoutFailBadTokenTest() {
        Assertions.assertThrows(DataAccessException.class, () -> userServiceLogic.logout("fake-token-123"));
    }
}