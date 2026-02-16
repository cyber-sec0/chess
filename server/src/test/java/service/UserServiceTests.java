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

    // This test verifies if the registration process is capable to create a valid user with the correct username information.
    @Test
    public void registerSuccessTest() throws DataAccessException {
        UserData userInformation = new UserData("benjamim", "password123", "ben@byu.edu");
        AuthData resultData = userServiceLogic.register(userInformation);
        Assertions.assertNotNull(resultData.authToken());
        Assertions.assertEquals("benjamim", resultData.username());
    }

    // Ensure that the system prevents the creation of a user that already exists in the memory database.
    @Test
    public void registerFailDuplicateTest() throws DataAccessException {
        UserData userInformation = new UserData("benjamim", "password123", "ben@byu.edu");
        userServiceLogic.register(userInformation);
        Assertions.assertThrows(DataAccessException.class, () -> userServiceLogic.register(userInformation));
    }

    // This function checks if the login feature returns a valid token when the credentials are completely correct.
    @Test
    public void loginSuccessTest() throws DataAccessException {
        UserData userInformation = new UserData("benjamim", "password123", "ben@byu.edu");
        userServiceLogic.register(userInformation);
        AuthData loginResultData = userServiceLogic.login(userInformation);
        Assertions.assertNotNull(loginResultData.authToken());
    }

    // It is important to verify that the system denies access when the password provided does not match the stored one.
    @Test
    public void loginFailWrongPasswordTest() throws DataAccessException {
        UserData userInformation = new UserData("benjamim", "password123", "ben@byu.edu");
        userServiceLogic.register(userInformation);
        UserData wrongPassUserInformation = new UserData("benjamim", "wrong", "ben@byu.edu");
        Assertions.assertThrows(DataAccessException.class, () -> userServiceLogic.login(wrongPassUserInformation));
    }

    // This test ensures that after the logout the authentication token is removed from the volatile memory storage.
    @Test
    public void logoutSuccessTest() throws DataAccessException {
        UserData userInformation = new UserData("benjamim", "password123", "ben@byu.edu");
        AuthData authData = userServiceLogic.register(userInformation);
        userServiceLogic.logout(authData.authToken());
        Assertions.assertNull(memoryAuthDaoTool.getAuth(authData.authToken()));
    }

    // Check if the system throws an error when trying to logout with a token that does not exist anymore.
    @Test
    public void logoutFailBadTokenTest() {
        Assertions.assertThrows(DataAccessException.class, () -> userServiceLogic.logout("fake-token-123"));
    }
}