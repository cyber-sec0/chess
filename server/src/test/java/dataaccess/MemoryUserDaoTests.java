package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

/**
 * This class is responsible for the tests of the user database tool.
 * The software need to store the data correct without lose information.
 * It is very important for security against hackers that the database works good.
 */
public class MemoryUserDaoTests {

    private MemoryUserDao memoryUserDatabaseAccessObjectForTestingPurpose;

    /**
     * This function prepare the environment before test run so database is clean.
     * It is necessary for don't have conflict with old data in the tables.
     * @throws DataAccessException If the database connection fails to execute.
     */
    @BeforeEach
    public void initializeDatabaseToolBeforeEachTestExecution() throws DataAccessException {
        // If the tool is null, it makes instantiation of the object for use
        memoryUserDatabaseAccessObjectForTestingPurpose = new MemoryUserDao();
        memoryUserDatabaseAccessObjectForTestingPurpose.clear();
    }

    /**
     * This test verify if the create user function works correct when data is good.
     * The system must save the password hashed for security reasons in the table.
     * @throws DataAccessException If the database throws error.
     */
    @Test
    public void createUserPositiveSuccessTest() throws DataAccessException {
        // If the user send correct information, the system must save in the database without crash
        String usernameStringForInsertion = "cyber_student_test";
        String passwordStringForInsertion = "senha123";
        String emailStringForInsertion = "aluno@byu.edu";

        UserData newUserDataObjectForDatabaseInsertion = new UserData(
                usernameStringForInsertion,
                passwordStringForInsertion,
                emailStringForInsertion
        );

        memoryUserDatabaseAccessObjectForTestingPurpose.createUser(newUserDataObjectForDatabaseInsertion);

        UserData retrievedUserDataObjectFromDatabaseStorage = memoryUserDatabaseAccessObjectForTestingPurpose
                .getUser(usernameStringForInsertion);

        Assertions.assertNotNull(retrievedUserDataObjectFromDatabaseStorage);
        Assertions.assertEquals(usernameStringForInsertion, retrievedUserDataObjectFromDatabaseStorage.username());
        Assertions.assertEquals(emailStringForInsertion, retrievedUserDataObjectFromDatabaseStorage.email());

        // It is necessary for check if the password is valid using bcrypt tool
        boolean passwordMatchVerificationResultValue = BCrypt.checkpw(
                passwordStringForInsertion,
                retrievedUserDataObjectFromDatabaseStorage.password()
        );
        Assertions.assertTrue(passwordMatchVerificationResultValue);
    }

    /**
     * This test verify if the create user function fails when the username is already exist.
     * Database must reject duplicate primary key for prevent injection or conflicts.
     */
    @Test
    public void createUserNegativeDuplicateFailureTest() throws DataAccessException {
        // If the user exist in the table, it must throw the exception for block creation
        String duplicateUsernameStringValueToTest = "hacker_user_name";
        UserData firstUserDataObjectForConflict = new UserData(
                duplicateUsernameStringValueToTest,
                "first_pass",
                "first@email.com"
        );
        UserData secondUserDataObjectForConflict = new UserData(
                duplicateUsernameStringValueToTest,
                "second_pass",
                "second@email.com"
        );

        memoryUserDatabaseAccessObjectForTestingPurpose.createUser(firstUserDataObjectForConflict);

        Assertions.assertThrows(DataAccessException.class, () -> {
            memoryUserDatabaseAccessObjectForTestingPurpose.createUser(secondUserDataObjectForConflict);
        });
    }

    /**
     * This test checks if the get user function returns the correct line from database.
     * It makes sure the object is exact same of the expected.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void getUserPositiveSuccessTest() throws DataAccessException {
        // If the target user is saved, the select command must find the data correct
        String targetUsernameToFindInTable = "target_search_user";
        UserData targetUserDataObject = new UserData(targetUsernameToFindInTable, "hidden", "target@byu.edu");

        memoryUserDatabaseAccessObjectForTestingPurpose.createUser(targetUserDataObject);
        UserData foundUserDataObjectFromTable = memoryUserDatabaseAccessObjectForTestingPurpose
                .getUser(targetUsernameToFindInTable);

        Assertions.assertNotNull(foundUserDataObjectFromTable);
        Assertions.assertEquals(targetUsernameToFindInTable, foundUserDataObjectFromTable.username());
    }

    /**
     * This negative test verify if the system return null when the user is ghost.
     * It is important for login failure logic to work correct.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void getUserNegativeNotFoundTest() throws DataAccessException {
        // If the username is not in the memory, it must return null value for the logic
        String ghostUsernameStringValue = "nobody_here_123";
        UserData missingUserDataObjectResult = memoryUserDatabaseAccessObjectForTestingPurpose
                .getUser(ghostUsernameStringValue);

        Assertions.assertNull(missingUserDataObjectResult);
    }

    /**
     * This test confirm that the clear command drops everything in the user table.
     * The table need to be clean for the system restart process.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void clearPositiveWithDataTest() throws DataAccessException {
        // If the database has data, the clear command must remove all the rows
        String tempUserStringName = "temp_user_for_delete";
        memoryUserDatabaseAccessObjectForTestingPurpose.createUser(new UserData(tempUserStringName, "p", "e"));

        memoryUserDatabaseAccessObjectForTestingPurpose.clear();

        UserData checkDeletedUserResult = memoryUserDatabaseAccessObjectForTestingPurpose.getUser(tempUserStringName);
        Assertions.assertNull(checkDeletedUserResult);
    }

    /**
     * This negative test verify if clear command runs safe when the table is empty already.
     * It should not crash the server if try to truncate empty memory.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void clearNegativeEmptyDatabaseTest() throws DataAccessException {
        // If the table is empty, the clear method must execute normal without exception
        memoryUserDatabaseAccessObjectForTestingPurpose.clear();
        memoryUserDatabaseAccessObjectForTestingPurpose.clear(); // Second time for verify stability

        UserData ghostCheckResultValue = memoryUserDatabaseAccessObjectForTestingPurpose.getUser("ghost");
        Assertions.assertNull(ghostCheckResultValue);
    }
}