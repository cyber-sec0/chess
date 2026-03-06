package dataaccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class manages the authentication tokens in the database table memory.
 * It uses mysql to store the tokens for users so they do not get disconnected.
 */
public class MemoryAuthDao {

    // This constructor initialize the database connection and creates table if not exist
    public MemoryAuthDao() {
        try {
            DatabaseManager.createDatabase();
            String createTableStringCommandExecution = "CREATE TABLE IF NOT EXISTS authDatabaseTableForStorage (" +
                    "tokenStringForDatabase VARCHAR(255) NOT NULL, " +
                    "usernameStringForDatabase VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY (tokenStringForDatabase))";
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
     * This method inserts the new auth token in the database table.
     * @param newAuthDataToSaveInDatabaseTable The auth token object.
     * @throws DataAccessException if connection fails so server can return 500 error code.
     */
    public void createAuth(AuthData newAuthDataToSaveInDatabaseTable) throws DataAccessException {
        String insertCommandStringForDatabase = "INSERT INTO authDatabaseTableForStorage " +
                "(tokenStringForDatabase, usernameStringForDatabase) VALUES (?, ?)";
        try (Connection connectionToDatabaseNetwork = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatementForExecutionCommand =
                         connectionToDatabaseNetwork.prepareStatement(insertCommandStringForDatabase)) {
                preparedStatementForExecutionCommand.setString(1, newAuthDataToSaveInDatabaseTable.authToken());
                preparedStatementForExecutionCommand.setString(2, newAuthDataToSaveInDatabaseTable.username());
                preparedStatementForExecutionCommand.executeUpdate();
            }
        } catch (SQLException exceptionFromDatabaseNetworkError) {
            throw new DataAccessException(exceptionFromDatabaseNetworkError.getMessage());
        }
    }

    /**
     * This function gets the auth token object from the database table.
     * @param tokenStringToFindInDatabaseTable The token string.
     * @return The auth object.
     * @throws DataAccessException if connection fails so server can return 500 error code.
     */
    public AuthData getAuth(String tokenStringToFindInDatabaseTable) throws DataAccessException {
        String selectCommandStringForDatabase = "SELECT tokenStringForDatabase, usernameStringForDatabase " +
                "FROM authDatabaseTableForStorage WHERE tokenStringForDatabase = ?";
        try (Connection connectionToDatabaseNetwork = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatementForExecutionCommand =
                         connectionToDatabaseNetwork.prepareStatement(selectCommandStringForDatabase)) {
                preparedStatementForExecutionCommand.setString(1, tokenStringToFindInDatabaseTable);
                try (ResultSet resultFromDatabaseQueryExecution = preparedStatementForExecutionCommand.executeQuery()) {
                    if (resultFromDatabaseQueryExecution.next()) {
                        return new AuthData(
                                resultFromDatabaseQueryExecution.getString("tokenStringForDatabase"),
                                resultFromDatabaseQueryExecution.getString("usernameStringForDatabase")
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
     * This function deletes the token from the database table when user logout.
     * @param tokenStringToDeleteFromDatabaseTable The token string.
     * @throws DataAccessException if connection fails so server can return 500 error code.
     */
    public void deleteAuth(String tokenStringToDeleteFromDatabaseTable) throws DataAccessException {
        String deleteCommandStringForDatabase = "DELETE FROM authDatabaseTableForStorage WHERE tokenStringForDatabase = ?";
        try (Connection connectionToDatabaseNetwork = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatementForExecutionCommand =
                         connectionToDatabaseNetwork.prepareStatement(deleteCommandStringForDatabase)) {
                preparedStatementForExecutionCommand.setString(1, tokenStringToDeleteFromDatabaseTable);
                preparedStatementForExecutionCommand.executeUpdate();
            }
        } catch (SQLException exceptionFromDatabaseNetworkError) {
            throw new DataAccessException(exceptionFromDatabaseNetworkError.getMessage());
        }
    }

    /**
     * This function removes all the data from the auth table completely.
     * @throws DataAccessException if connection fails so server can return 500 error code.
     */
    public void clear() throws DataAccessException {
        String deleteCommandStringForDatabase = "TRUNCATE TABLE authDatabaseTableForStorage";
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