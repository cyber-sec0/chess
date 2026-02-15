package service;

import dataaccess.MemoryAuthDao;
import dataaccess.MemoryUserDao;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import java.util.UUID;

/**
 * The UserService is the class that contains the logic for user operations.
 * It connects the handler requests to the data access objects.
 */
public class UserService {

    private final MemoryUserDao userDatabaseAccess;
    private final MemoryAuthDao authDatabaseAccess;

    public UserService(MemoryUserDao userDao, MemoryAuthDao authDao) {
        this.userDatabaseAccess = userDao;
        this.authDatabaseAccess = authDao;
    }

    /**
     * This method performs the registration of a new user.
     * It checks if the user exists and then creates it if possible.
     * @param userRequest The user data to register.
     * @return The auth data.
     * @throws DataAccessException If validation fails.
     */
    public AuthData register(UserData userRequest) throws DataAccessException {
        if (userRequest.username() == null || userRequest.password() == null || userRequest.email() == null) {
            throw new DataAccessException("Error: bad request");
        }

        UserData existingUser = userDatabaseAccess.getUser(userRequest.username());

        if (existingUser != null) {
            throw new DataAccessException("Error: already taken");
        }

        userDatabaseAccess.createUser(userRequest);

        String newRandomToken = UUID.randomUUID().toString();
        AuthData newAuthenticationData = new AuthData(newRandomToken, userRequest.username());
        authDatabaseAccess.createAuth(newAuthenticationData);

        return newAuthenticationData;
    }

    /**
     * This function handles the login process by checking the password.
     * @param loginRequest The user data to login.
     * @return The auth token.
     * @throws DataAccessException If login fails.
     */
    public AuthData login(UserData loginRequest) throws DataAccessException {
        UserData foundUser = userDatabaseAccess.getUser(loginRequest.username());

        if (foundUser == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        if (!foundUser.password().equals(loginRequest.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        String generatedTokenString = UUID.randomUUID().toString();
        AuthData authResult = new AuthData(generatedTokenString, loginRequest.username());
        authDatabaseAccess.createAuth(authResult);

        return authResult;
    }

    /**
     * This method removes the session token to logout the user.
     * @param authTokenHeader The token to invalidate.
     * @throws DataAccessException If unauthorized.
     */
    public void logout(String authTokenHeader) throws DataAccessException {
        AuthData existingToken = authDatabaseAccess.getAuth(authTokenHeader);
        if (existingToken == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDatabaseAccess.deleteAuth(authTokenHeader);
    }
}