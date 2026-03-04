package service;

import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import chess.ChessGame;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;

/**
 * The GameService is the class that contains logic for game operations.
 * It uses the data access objects to save things in database.
 */
public class GameService {

    private final MemoryGameDao gameDatabaseAccessObjectTool;
    private final MemoryAuthDao authDatabaseAccessObjectTool;

    // This constructor initialize the tools for the game service
    public GameService(MemoryGameDao gameDaoObjectParameter, MemoryAuthDao authDaoObjectParameter) {
        this.gameDatabaseAccessObjectTool = gameDaoObjectParameter;
        this.authDatabaseAccessObjectTool = authDaoObjectParameter;
    }

    /**
     * This method lists all the games from the database.
     * @param authTokenStringValue The token string to check authorization.
     * @return Collection of games.
     * @throws DataAccessException If token is bad or database fails.
     */
    public Collection<GameData> listGames(String authTokenStringValue) throws DataAccessException {
        AuthData existingTokenFromDatabaseTable = authDatabaseAccessObjectTool.getAuth(authTokenStringValue);

        // Checking if the token from the user is fake or null so we block
        if (existingTokenFromDatabaseTable == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        return gameDatabaseAccessObjectTool.listGames();
    }

    /**
     * This method creates a new game in the database.
     * @param authTokenStringValue The token string.
     * @param newGameNameStringValue The name of game.
     * @return The numeric ID of the new game.
     * @throws DataAccessException If token is bad or database fails.
     */
    public int createGame(String authTokenStringValue, String newGameNameStringValue) throws DataAccessException {
        AuthData existingTokenFromDatabaseTable = authDatabaseAccessObjectTool.getAuth(authTokenStringValue);

        // Checking if the token from the user is fake or null so we block
        if (existingTokenFromDatabaseTable == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        int randomNumericIdForNewGame = Math.abs(new Random().nextInt());
        GameData newlyCreatedGameObject = new GameData(randomNumericIdForNewGame, null, null, newGameNameStringValue, new ChessGame());

        gameDatabaseAccessObjectTool.createGame(newlyCreatedGameObject);
        return randomNumericIdForNewGame;
    }

    /**
     * This method adds a player to an existing game in the database.
     * @param authTokenStringValue The token string.
     * @param colorStringValue The color chosen.
     * @param gameNumericIdValue The game ID.
     * @throws DataAccessException If validations fail or database fails.
     */
    public void joinGame(String authTokenStringValue, String colorStringValue, int gameNumericIdValue) throws DataAccessException {
        AuthData existingTokenFromDatabaseTable = authDatabaseAccessObjectTool.getAuth(authTokenStringValue);

        // Checking if the token from the user is fake or null so we block
        if (existingTokenFromDatabaseTable == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        GameData existingGameFromDatabaseTable = gameDatabaseAccessObjectTool.getGame(gameNumericIdValue);

        // Checking if the game does not exist in database so we give error
        if (existingGameFromDatabaseTable == null) {
            throw new DataAccessException("Error: bad request");
        }

        String usernameFromTokenString = existingTokenFromDatabaseTable.username();
        String currentWhiteUsernameString = existingGameFromDatabaseTable.whiteUsername();
        String currentBlackUsernameString = existingGameFromDatabaseTable.blackUsername();

        // Checking if the color is white so we assign user to white
        if (Objects.equals(colorStringValue, "WHITE")) {
            // Checking if the white player is already taken
            if (currentWhiteUsernameString != null) {
                throw new DataAccessException("Error: already taken");
            }
            currentWhiteUsernameString = usernameFromTokenString;
        } else if (Objects.equals(colorStringValue, "BLACK")) {
            // Checking if the black player is already taken
            if (currentBlackUsernameString != null) {
                throw new DataAccessException("Error: already taken");
            }
            currentBlackUsernameString = usernameFromTokenString;
        } else {
            throw new DataAccessException("Error: bad request");
        }

        GameData updatedGameObjectForDatabase = new GameData(
                existingGameFromDatabaseTable.gameID(),
                currentWhiteUsernameString,
                currentBlackUsernameString,
                existingGameFromDatabaseTable.gameName(),
                existingGameFromDatabaseTable.game()
        );

        gameDatabaseAccessObjectTool.updateGame(updatedGameObjectForDatabase);
    }
}