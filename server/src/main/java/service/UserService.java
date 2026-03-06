package service;

import dataaccess.MemoryAuthDao;
import dataaccess.MemoryUserDao;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

/**
 * The UserService is the class that contains the logic for user operations in the system.
 * It connects the handler requests to the database access objects.
 */
public class UserService {

    private final MemoryUserDao userDatabaseAccessObjectTool;
    private final MemoryAuthDao authDatabaseAccessObjectTool;

    public UserService(MemoryUserDao userDaoObjectParameter, MemoryAuthDao authDaoObjectParameter) {
        this.userDatabaseAccessObjectTool = userDaoObjectParameter;
        this.authDatabaseAccessObjectTool = authDaoObjectParameter;
    }

    /**
     * This method performs the registration of a new user in the database.
     * It checks if the user exists in database and then creates it if possible.
     * @param userRequestObjectInformation The user data to register.
     * @return The auth data object.
     * @throws DataAccessException If validation fails in the process.
     */
    public AuthData register(UserData userRequestObjectInformation) throws DataAccessException {
        // Checking if the information sent by user is bad request
        if (userRequestObjectInformation.username() == null ||
                userRequestObjectInformation.password() == null ||
                userRequestObjectInformation.email() == null) {
            throw new DataAccessException("Error: bad request");
        }

        UserData existingUserFromDatabaseTable = userDatabaseAccessObjectTool.getUser(userRequestObjectInformation.username());

        // Checking if the user already exist in database table
        if (existingUserFromDatabaseTable != null) {
            throw new DataAccessException("Error: already taken");
        }

        userDatabaseAccessObjectTool.createUser(userRequestObjectInformation);

        String newRandomTokenStringForUser = UUID.randomUUID().toString();
        AuthData newAuthenticationDataObjectForUser = new AuthData(newRandomTokenStringForUser, userRequestObjectInformation.username());
        authDatabaseAccessObjectTool.createAuth(newAuthenticationDataObjectForUser);

        return newAuthenticationDataObjectForUser;
    }

    /**
     * This function handles the login process by checking the password inside database.
     * @param loginRequestObjectInformation The user data to login.
     * @return The auth token object.
     * @throws DataAccessException If login fails in the process.
     */
    public AuthData login(UserData loginRequestObjectInformation) throws DataAccessException {
        UserData foundUserFromDatabaseTable = userDatabaseAccessObjectTool.getUser(loginRequestObjectInformation.username());

        // Checking if the user is null meaning not found in database
        if (foundUserFromDatabaseTable == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        // Using the bcrypt tool to verify if the password from user matches the database hash
        if (!BCrypt.checkpw(loginRequestObjectInformation.password(), foundUserFromDatabaseTable.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        String generatedTokenStringForUserLogin = UUID.randomUUID().toString();
        AuthData authResultObjectForUser = new AuthData(generatedTokenStringForUserLogin, loginRequestObjectInformation.username());
        authDatabaseAccessObjectTool.createAuth(authResultObjectForUser);

        return authResultObjectForUser;
    }

    /**
     * This method removes the session token to logout the user from database.
     * @param authTokenHeaderStringValue The token to invalidate.
     * @throws DataAccessException If unauthorized token in database.
     */
    public void logout(String authTokenHeaderStringValue) throws DataAccessException {
        AuthData existingTokenFromDatabaseTable = authDatabaseAccessObjectTool.getAuth(authTokenHeaderStringValue);

        // Checking if the token from the user is fake or null
        if (existingTokenFromDatabaseTable == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        authDatabaseAccessObjectTool.deleteAuth(authTokenHeaderStringValue);
    }
}