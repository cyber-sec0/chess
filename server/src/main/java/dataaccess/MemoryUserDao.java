package dataaccess;

import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

/**
 * This class is the implementation of the data access for users in the database of the computer.
 * It uses mysql to store the users which is very safe to not loss data when computer turns off.
 */
public class MemoryUserDao {

    // This constructor initialize the database connection and creates table if not exist
    public MemoryUserDao() {
        try {
            DatabaseManager.createDatabase();
            String createTableStringCommandExecution = "CREATE TABLE IF NOT EXISTS usersDatabaseTableForStorage (" +
                    "usernameStringForDatabase VARCHAR(255) NOT NULL, " +
                    "passwordStringForDatabase VARCHAR(255) NOT NULL, " +
                    "emailStringForDatabase VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY (usernameStringForDatabase))";
            try (Connection connectionToDatabaseNetwork = DatabaseManager.getConnection()) {
                try (PreparedStatement preparedStatementForExecutionCommand =
                             connectionToDatabaseNetwork.prepareStatement(createTableStringCommandExecution)) {
                    preparedStatementForExecutionCommand.executeUpdate();
                }
            }
        } catch (DataAccessException | SQLException exceptionFromDatabaseNetworkError) {
            throw new RuntimeException("Error: " + exceptionFromDatabaseNetworkError.getMessage());
        }
    }

    /**
     * This function gets the user object from the database table using the name of the user.
     * It executes a select statement to find the correct line in the table.
     * @param usernameToFindInDatabaseTable The string name to search.
     * @return The user data object or null.
     * @throws DataAccessException if connection fails so server can return 500 error code.
     */
    public UserData getUser(String usernameToFindInDatabaseTable) throws DataAccessException {
        String selectCommandStringForDatabase = "SELECT usernameStringForDatabase, passwordStringForDatabase, " +
                "emailStringForDatabase FROM usersDatabaseTableForStorage WHERE usernameStringForDatabase = ?";
        try (Connection connectionToDatabaseNetwork = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatementForExecutionCommand =
                         connectionToDatabaseNetwork.prepareStatement(selectCommandStringForDatabase)) {
                preparedStatementForExecutionCommand.setString(1, usernameToFindInDatabaseTable);
                try (ResultSet resultFromDatabaseQueryExecution = preparedStatementForExecutionCommand.executeQuery()) {
                    if (resultFromDatabaseQueryExecution.next()) {
                        return new UserData(
                                resultFromDatabaseQueryExecution.getString("usernameStringForDatabase"),
                                resultFromDatabaseQueryExecution.getString("passwordStringForDatabase"),
                                resultFromDatabaseQueryExecution.getString("emailStringForDatabase")
                        );
                    }
                }
            }
        } catch (SQLException exceptionFromDatabaseNetworkError) {
            throw new DataAccessException(exceptionFromDatabaseNetworkError.getMessage());
        }
        return null;
    }

    /**
     * This function creates a new user inside the database table storage.
     * It hashes the password before saving for security reasons against hackers.
     * @param newUserDataToSaveInDatabaseTable The object with data.
     * @throws DataAccessException if connection fails so server can return 500 error code.
     */
    public void createUser(UserData newUserDataToSaveInDatabaseTable) throws DataAccessException {
        String insertCommandStringForDatabase = "INSERT INTO usersDatabaseTableForStorage " +
                "(usernameStringForDatabase, passwordStringForDatabase, emailStringForDatabase) VALUES (?, ?, ?)";
        try (Connection connectionToDatabaseNetwork = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatementForExecutionCommand =
                         connectionToDatabaseNetwork.prepareStatement(insertCommandStringForDatabase)) {
                preparedStatementForExecutionCommand.setString(1, newUserDataToSaveInDatabaseTable.username());
                String hashedPasswordStringForSecurity = BCrypt.hashpw(newUserDataToSaveInDatabaseTable.password(), BCrypt.gensalt());
                preparedStatementForExecutionCommand.setString(2, hashedPasswordStringForSecurity);
                preparedStatementForExecutionCommand.setString(3, newUserDataToSaveInDatabaseTable.email());
                preparedStatementForExecutionCommand.executeUpdate();
            }
        } catch (SQLException exceptionFromDatabaseNetworkError) {
            throw new DataAccessException(exceptionFromDatabaseNetworkError.getMessage());
        }
    }

    /**
     * This function removes all the data from the user table completely.
     * It is used for the clear system command.
     * @throws DataAccessException if connection fails so server can return 500 error code.
     */
    public void clear() throws DataAccessException {
        String deleteCommandStringForDatabase = "TRUNCATE TABLE usersDatabaseTableForStorage";
        try (Connection connectionToDatabaseNetwork = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatementForExecutionCommand =
                         connectionToDatabaseNetwork.prepareStatement(deleteCommandStringForDatabase)) {
                preparedStatementForExecutionCommand.executeUpdate();
            }
        } catch (SQLException exceptionFromDatabaseNetworkError) {
            throw new DataAccessException(exceptionFromDatabaseNetworkError.getMessage());
        }
    }
}