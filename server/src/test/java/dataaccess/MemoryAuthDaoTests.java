package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class is the test suite for the authentication database tool.
 * It is necessary to guarantee session hijacking prevention is active.
 */
public class MemoryAuthDaoTests {

    private MemoryAuthDao memoryAuthenticationDatabaseAccessObjectForTestExecution;

    /**
     * This function resets the auth memory table before the test start.
     * @throws DataAccessException If the connection drops.
     */
    @BeforeEach
    public void setupTestEnvironmentForAuthDao() throws DataAccessException {
        // If the variable is empty, instantiate the class for usage in the test
        memoryAuthenticationDatabaseAccessObjectForTestExecution = new MemoryAuthDao();
        memoryAuthenticationDatabaseAccessObjectForTestExecution.clear();
    }

    /**
     * This test verify if the create auth method saves the token correct in database.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void createAuthPositiveSuccessTest() throws DataAccessException {
        // If the token is unique, the system must save it successful in the row
        String generatedSecureTokenStringValue = "secure-token-abc-123";
        String linkedUsernameStringValue = "auth_user_test";

        AuthData newAuthenticationDataObjectForInsertion = new AuthData(
                generatedSecureTokenStringValue,
                linkedUsernameStringValue
        );

        memoryAuthenticationDatabaseAccessObjectForTestExecution.createAuth(newAuthenticationDataObjectForInsertion);

        AuthData retrievedAuthDataObjectFromDatabase = memoryAuthenticationDatabaseAccessObjectForTestExecution
                .getAuth(generatedSecureTokenStringValue);

        Assertions.assertNotNull(retrievedAuthDataObjectFromDatabase);
        Assertions.assertEquals(linkedUsernameStringValue, retrievedAuthDataObjectFromDatabase.username());
    }

    /**
     * This negative test checks if the database reject identical token strings.
     * Tokens must be unique primary keys in the database structure.
     */
    @Test
    public void createAuthNegativeDuplicateTokenTest() throws DataAccessException {
        // If the token is already inside the table, it must throw exception for duplicate primary key
        String duplicateTokenStringValueForConflict = "conflict-token-999";
        AuthData firstAuthDataObject = new AuthData(duplicateTokenStringValueForConflict, "user_one");
        AuthData secondAuthDataObject = new AuthData(duplicateTokenStringValueForConflict, "user_two");

        memoryAuthenticationDatabaseAccessObjectForTestExecution.createAuth(firstAuthDataObject);

        Assertions.assertThrows(DataAccessException.class, () -> {
            memoryAuthenticationDatabaseAccessObjectForTestExecution.createAuth(secondAuthDataObject);
        });
    }

    /**
     * This test verify if the get auth method finds the exact token object.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void getAuthPositiveSuccessTest() throws DataAccessException {
        // If the search token exist, the database must return the object with all data
        String searchTokenStringValueToFind = "find-me-token";
        memoryAuthenticationDatabaseAccessObjectForTestExecution.createAuth(
                new AuthData(searchTokenStringValueToFind, "search_user")
        );

        AuthData foundAuthResultObject = memoryAuthenticationDatabaseAccessObjectForTestExecution
                .getAuth(searchTokenStringValueToFind);

        Assertions.assertNotNull(foundAuthResultObject);
        Assertions.assertEquals(searchTokenStringValueToFind, foundAuthResultObject.authToken());
    }

    /**
     * This negative test verify if the get auth method handles missing tokens proper.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void getAuthNegativeMissingTokenTest() throws DataAccessException {
        // If the requested token is fake, the database must return null value
        String fakeTokenStringValueForSearch = "fake-hacker-token";
        AuthData missingAuthResultObject = memoryAuthenticationDatabaseAccessObjectForTestExecution
                .getAuth(fakeTokenStringValueForSearch);

        Assertions.assertNull(missingAuthResultObject);
    }

    /**
     * This test validates if the delete auth method removes the session from table.
     * It is required for the user logout process logic.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void deleteAuthPositiveSuccessTest() throws DataAccessException {
        // If the delete command is called, the specific token must vanish from database
        String tokenToDeleteStringValue = "delete-this-token";
        memoryAuthenticationDatabaseAccessObjectForTestExecution.createAuth(
                new AuthData(tokenToDeleteStringValue, "logout_user")
        );

        memoryAuthenticationDatabaseAccessObjectForTestExecution.deleteAuth(tokenToDeleteStringValue);

        AuthData verificationAuthResultObject = memoryAuthenticationDatabaseAccessObjectForTestExecution
                .getAuth(tokenToDeleteStringValue);
        Assertions.assertNull(verificationAuthResultObject);
    }

    /**
     * This negative test verify if deleting a token does not delete other tokens.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void deleteAuthNegativeProtectOtherTokensTest() throws DataAccessException {
        // If one token is deleted, the other unrelated token must stay safe in the table
        String safeTokenStringValue = "safe-token-keep";
        String doomedTokenStringValue = "doomed-token-remove";

        memoryAuthenticationDatabaseAccessObjectForTestExecution.createAuth(new AuthData(safeTokenStringValue, "userA"));
        memoryAuthenticationDatabaseAccessObjectForTestExecution.createAuth(new AuthData(doomedTokenStringValue, "userB"));

        memoryAuthenticationDatabaseAccessObjectForTestExecution.deleteAuth(doomedTokenStringValue);

        AuthData survivingAuthResultObject = memoryAuthenticationDatabaseAccessObjectForTestExecution
                .getAuth(safeTokenStringValue);
        Assertions.assertNotNull(survivingAuthResultObject);
    }

    /**
     * This test checks if clear command destroys all auth records.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void clearAuthPositiveWithDataTest() throws DataAccessException {
        // If clear is executed, the auth table must become completely empty
        memoryAuthenticationDatabaseAccessObjectForTestExecution.createAuth(new AuthData("tok", "usr"));
        memoryAuthenticationDatabaseAccessObjectForTestExecution.clear();

        Assertions.assertNull(memoryAuthenticationDatabaseAccessObjectForTestExecution.getAuth("tok"));
    }

    /**
     * This negative test checks if clearing empty database works fine.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void clearAuthNegativeEmptyTableTest() throws DataAccessException {
        // If the table has zero rows, the truncate logic must not throw exception crash
        memoryAuthenticationDatabaseAccessObjectForTestExecution.clear();
        memoryAuthenticationDatabaseAccessObjectForTestExecution.clear(); // Execute twice to test resilience

        Assertions.assertNull(memoryAuthenticationDatabaseAccessObjectForTestExecution.getAuth("ghost"));
    }
}