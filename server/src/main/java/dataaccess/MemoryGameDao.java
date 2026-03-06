package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import chess.ChessGame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class handles the operations for the games data using the database memory.
 * It is important to keep track of the games so players can play and save.
 */
public class MemoryGameDao {

    private final Gson objectToJsonTranslatorForDatabase = new Gson();

    // This constructor initialize the database connection and creates table if not exist
    public MemoryGameDao() {
        try {
            DatabaseManager.createDatabase();
            String createTableStringCommandExecution = "CREATE TABLE IF NOT EXISTS gameDatabaseTableForStorage (" +
                    "gameNumericIdForDatabase INT NOT NULL, " +
                    "whiteUsernameStringForDatabase VARCHAR(255), " +
                    "blackUsernameStringForDatabase VARCHAR(255), " +
                    "gameNameStringForDatabase VARCHAR(255) NOT NULL, " +
                    "gameJsonObjectStringForDatabase TEXT NOT NULL, " +
                    "PRIMARY KEY (gameNumericIdForDatabase))";
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
     * This method adds a new game to the table database list.
     * @param gameToAddInDatabaseTable The game object to add.
     * @throws DataAccessException if connection fails so server can return 500 error code.
     */
    public void createGame(GameData gameToAddInDatabaseTable) throws DataAccessException {
        String insertCommandStringForDatabase = "INSERT INTO gameDatabaseTableForStorage " +
                "(gameNumericIdForDatabase, whiteUsernameStringForDatabase, blackUsernameStringForDatabase, " +
                "gameNameStringForDatabase, gameJsonObjectStringForDatabase) VALUES (?, ?, ?, ?, ?)";
        try (Connection connectionToDatabaseNetwork = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatementForExecutionCommand =
                         connectionToDatabaseNetwork.prepareStatement(insertCommandStringForDatabase)) {
                preparedStatementForExecutionCommand.setInt(1, gameToAddInDatabaseTable.gameID());
                preparedStatementForExecutionCommand.setString(2, gameToAddInDatabaseTable.whiteUsername());
                preparedStatementForExecutionCommand.setString(3, gameToAddInDatabaseTable.blackUsername());
                preparedStatementForExecutionCommand.setString(4, gameToAddInDatabaseTable.gameName());
                String jsonGameStringForDatabase = objectToJsonTranslatorForDatabase.toJson(gameToAddInDatabaseTable.game());
                preparedStatementForExecutionCommand.setString(5, jsonGameStringForDatabase);
                preparedStatementForExecutionCommand.executeUpdate();
            }
        } catch (SQLException exceptionFromDatabaseNetworkError) {
            throw new DataAccessException(exceptionFromDatabaseNetworkError.getMessage());
        }
    }

    /**
     * This function searches for a game using the unique numeric identifier in database.
     * @param gameNumericIdToFindInDatabase The ID to search for.
     * @return The game data if found.
     * @throws DataAccessException if connection fails so server can return 500 error code.
     */
    public GameData getGame(int gameNumericIdToFindInDatabase) throws DataAccessException {
        String selectCommandStringForDatabase = "SELECT gameNumericIdForDatabase, whiteUsernameStringForDatabase, " +
                "blackUsernameStringForDatabase, gameNameStringForDatabase, gameJsonObjectStringForDatabase " +
                "FROM gameDatabaseTableForStorage WHERE gameNumericIdForDatabase = ?";
        try (Connection connectionToDatabaseNetwork = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatementForExecutionCommand =
                         connectionToDatabaseNetwork.prepareStatement(selectCommandStringForDatabase)) {
                preparedStatementForExecutionCommand.setInt(1, gameNumericIdToFindInDatabase);
                try (ResultSet resultFromDatabaseQueryExecution = preparedStatementForExecutionCommand.executeQuery()) {
                    if (resultFromDatabaseQueryExecution.next()) {
                        String gameJsonObjectStringForDatabase = resultFromDatabaseQueryExecution.getString("gameJsonObjectStringForDatabase");
                        ChessGame deserializedGameObjectForGame = objectToJsonTranslatorForDatabase
                                .fromJson(gameJsonObjectStringForDatabase, ChessGame.class);
                        return new GameData(
                                resultFromDatabaseQueryExecution.getInt("gameNumericIdForDatabase"),
                                resultFromDatabaseQueryExecution.getString("whiteUsernameStringForDatabase"),
                                resultFromDatabaseQueryExecution.getString("blackUsernameStringForDatabase"),
                                resultFromDatabaseQueryExecution.getString("gameNameStringForDatabase"),
                                deserializedGameObjectForGame
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
     * This returns all the games currently in the system database.
     * @return A collection of games.
     * @throws DataAccessException if connection fails so server can return 500 error code.
     */
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> collectionOfGamesFromDatabase = new ArrayList<>();
        String selectCommandStringForDatabase = "SELECT gameNumericIdForDatabase, whiteUsernameStringForDatabase, " +
                "blackUsernameStringForDatabase, gameNameStringForDatabase, gameJsonObjectStringForDatabase " +
                "FROM gameDatabaseTableForStorage";
        try (Connection connectionToDatabaseNetwork = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatementForExecutionCommand =
                         connectionToDatabaseNetwork.prepareStatement(selectCommandStringForDatabase)) {
                try (ResultSet resultFromDatabaseQueryExecution = preparedStatementForExecutionCommand.executeQuery()) {
                    while (resultFromDatabaseQueryExecution.next()) {
                        String gameJsonObjectStringForDatabase = resultFromDatabaseQueryExecution.getString("gameJsonObjectStringForDatabase");
                        ChessGame deserializedGameObjectForGame = objectToJsonTranslatorForDatabase
                                .fromJson(gameJsonObjectStringForDatabase, ChessGame.class);
                        GameData individualGameObjectFromDatabase = new GameData(
                                resultFromDatabaseQueryExecution.getInt("gameNumericIdForDatabase"),
                                resultFromDatabaseQueryExecution.getString("whiteUsernameStringForDatabase"),
                                resultFromDatabaseQueryExecution.getString("blackUsernameStringForDatabase"),
                                resultFromDatabaseQueryExecution.getString("gameNameStringForDatabase"),
                                deserializedGameObjectForGame
                        );
                        collectionOfGamesFromDatabase.add(individualGameObjectFromDatabase);
                    }
                }
            }
        } catch (SQLException exceptionFromDatabaseNetworkError) {
            throw new DataAccessException(exceptionFromDatabaseNetworkError.getMessage());
        }
        return collectionOfGamesFromDatabase;
    }

    /**
     * This function updates a game by running update command in database.
     * @param updatedGameInformationObjectToSave The new state of the game.
     * @throws DataAccessException if connection fails so server can return 500 error code.
     */
    public void updateGame(GameData updatedGameInformationObjectToSave) throws DataAccessException {
        String updateCommandStringForDatabase = "UPDATE gameDatabaseTableForStorage SET whiteUsernameStringForDatabase = ?, " +
                "blackUsernameStringForDatabase = ?, gameNameStringForDatabase = ?, gameJsonObjectStringForDatabase = ? " +
                "WHERE gameNumericIdForDatabase = ?";
        try (Connection connectionToDatabaseNetwork = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatementForExecutionCommand =
                         connectionToDatabaseNetwork.prepareStatement(updateCommandStringForDatabase)) {
                preparedStatementForExecutionCommand.setString(1, updatedGameInformationObjectToSave.whiteUsername());
                preparedStatementForExecutionCommand.setString(2, updatedGameInformationObjectToSave.blackUsername());
                preparedStatementForExecutionCommand.setString(3, updatedGameInformationObjectToSave.gameName());
                String jsonGameStringForDatabase = objectToJsonTranslatorForDatabase.toJson(updatedGameInformationObjectToSave.game());
                preparedStatementForExecutionCommand.setString(4, jsonGameStringForDatabase);
                preparedStatementForExecutionCommand.setInt(5, updatedGameInformationObjectToSave.gameID());
                preparedStatementForExecutionCommand.executeUpdate();
            }
        } catch (SQLException exceptionFromDatabaseNetworkError) {
            throw new DataAccessException(exceptionFromDatabaseNetworkError.getMessage());
        }
    }

    /**
     * This removes all the games from the table.
     * @throws DataAccessException if connection fails so server can return 500 error code.
     */
    public void clear() throws DataAccessException {
        String deleteCommandStringForDatabase = "TRUNCATE TABLE gameDatabaseTableForStorage";
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