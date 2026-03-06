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
 * It is important to clean the database before every test so we dont have trash data.
 */
public class UserServiceTests {

    private MemoryUserDao memoryUserDaoToolForTesting;
    private MemoryAuthDao memoryAuthDaoToolForTesting;
    private UserService userServiceLogicForTesting;

    @BeforeEach
    public void setupMethodForTests() throws DataAccessException {
        //Adding throws exception because the database clear process can fail and we need to catch
        memoryUserDaoToolForTesting = new MemoryUserDao();
        memoryAuthDaoToolForTesting = new MemoryAuthDao();

        //Clear the data from the database tables because we need empty start
        memoryUserDaoToolForTesting.clear();
        memoryAuthDaoToolForTesting.clear();

        userServiceLogicForTesting = new UserService(memoryUserDaoToolForTesting, memoryAuthDaoToolForTesting);
    }

    //This test verifies if the registration process is capable to create a valid user with the correct username information.
    @Test
    public void registerSuccessTest() throws DataAccessException {
        //Creating user object to test the registration feature inside the database
        UserData userInformationObjectForRegistrationTest = new UserData("benjamim", "password123", "ben@byu.edu");
        AuthData resultDataObjectFromRegistration = userServiceLogicForTesting.register(userInformationObjectForRegistrationTest);
        Assertions.assertNotNull(resultDataObjectFromRegistration.authToken());
        Assertions.assertEquals("benjamim", resultDataObjectFromRegistration.username());
    }

    //Ensure that the system prevents the creation of a user that already exists in the memory database table.
    @Test
    public void registerFailDuplicateTest() throws DataAccessException {
        //Testing if the duplicate block works when we send the same user two times
        UserData userInformationObjectForDuplicateTest = new UserData("benjamim", "password123", "ben@byu.edu");
        userServiceLogicForTesting.register(userInformationObjectForDuplicateTest);
        Assertions.assertThrows(DataAccessException.class, () -> userServiceLogicForTesting.register(userInformationObjectForDuplicateTest));
    }

    //This function checks if the login feature returns a valid token when the credentials are completely correct.
    @Test
    public void loginSuccessTest() throws DataAccessException {
        //Verifying if the login creates a good token when user inputs the correct password
        UserData userInformationObjectForLoginTest = new UserData("benjamim", "password123", "ben@byu.edu");
        userServiceLogicForTesting.register(userInformationObjectForLoginTest);
        AuthData loginResultDataObjectFromDatabase = userServiceLogicForTesting.login(userInformationObjectForLoginTest);
        Assertions.assertNotNull(loginResultDataObjectFromDatabase.authToken());
    }

    //It is important to verify that the system denies access when the password provided does not match the stored one.
    @Test
    public void loginFailWrongPasswordTest() throws DataAccessException {
        //Checking if the bad password blocks the user to login in the system
        UserData userInformationObjectForGoodLogin = new UserData("benjamim", "password123", "ben@byu.edu");
        userServiceLogicForTesting.register(userInformationObjectForGoodLogin);
        UserData wrongPassUserInformationObjectForTest = new UserData("benjamim", "wrong", "ben@byu.edu");
        Assertions.assertThrows(DataAccessException.class, () -> userServiceLogicForTesting.login(wrongPassUserInformationObjectForTest));
    }

    //This test ensures that after the logout the authentication token is removed from the volatile memory storage.
    @Test
    public void logoutSuccessTest() throws DataAccessException {
        //Testing if the token is deleted from the database table when the user click logout
        UserData userInformationObjectForLogoutTest = new UserData("benjamim", "password123", "ben@byu.edu");
        AuthData authDataObjectForLogoutAction = userServiceLogicForTesting.register(userInformationObjectForLogoutTest);
        userServiceLogicForTesting.logout(authDataObjectForLogoutAction.authToken());
        Assertions.assertNull(memoryAuthDaoToolForTesting.getAuth(authDataObjectForLogoutAction.authToken()));
    }

    //Check if the system throws an error when trying to logout with a token that does not exist anymore.
    @Test
    public void logoutFailBadTokenTest() {
        //Ensuring that fake tokens gives error when trying to execute logout
        Assertions.assertThrows(DataAccessException.class, () -> userServiceLogicForTesting.logout("fake-token-123"));
    }
}