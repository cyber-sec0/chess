package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import java.util.Collection;
import java.util.Random;

/**
 * This service handles all the game related operations.
 * It is very important for the gameplay functionality.
 */
public class GameService {

    private final MemoryGameDao gameDatabaseAccess;
    private final MemoryAuthDao authCheckerDao;

    public GameService(MemoryGameDao gameDao, MemoryAuthDao authDao) {
        this.gameDatabaseAccess = gameDao;
        this.authCheckerDao = authDao;
    }

    /**
     * This function creates a new game with a random ID.
     * It tries to find a unique ID by generating random numbers.
     * @param authToken The auth token.
     * @param gameName The name of the game.
     * @return The new game ID.
     * @throws DataAccessException If failed.
     */
    public int createGame(String authToken, String gameName) throws DataAccessException {
        AuthData verifier = authCheckerDao.getAuth(authToken);
        if (verifier == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameName == null) {
            throw new DataAccessException("Error: bad request");
        }

        Random numberGenerator = new Random();
        int newGameIdInteger = Math.abs(numberGenerator.nextInt());

        // This loop makes sure the ID is not already used in the database
        while (gameDatabaseAccess.getGame(newGameIdInteger) != null) {
            newGameIdInteger = Math.abs(numberGenerator.nextInt());
        }

        GameData freshGame = new GameData(newGameIdInteger, null, null, gameName, new ChessGame());
        gameDatabaseAccess.createGame(freshGame);

        return newGameIdInteger;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        AuthData verifier = authCheckerDao.getAuth(authToken);
        if (verifier == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return gameDatabaseAccess.listGames();
    }

    /**
     * This method allows a user to join a game.
     * It checks colors and availability.
     * @param authToken The token.
     * @param playerColor The color to join.
     * @param gameIdTarget The game ID.
     * @throws DataAccessException If joining fails.
     */
    public void joinGame(String authToken, String playerColor, int gameIdTarget) throws DataAccessException {
        AuthData verifier = authCheckerDao.getAuth(authToken);
        if (verifier == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        GameData gameFound = gameDatabaseAccess.getGame(gameIdTarget);
        if (gameFound == null) {
            throw new DataAccessException("Error: bad request");
        }

        if (playerColor == null) {
            throw new DataAccessException("Error: bad request");
        }

        String usernameOfPlayer = verifier.username();
        String newWhiteUser = gameFound.whiteUsername();
        String newBlackUser = gameFound.blackUsername();

        // Used guard clauses to prevent deep nesting code
        if (playerColor.equals("WHITE")) {
            if (newWhiteUser != null) {
                throw new DataAccessException("Error: already taken");
            }
            newWhiteUser = usernameOfPlayer;
        } else if (playerColor.equals("BLACK")) {
            if (newBlackUser != null) {
                throw new DataAccessException("Error: already taken");
            }
            newBlackUser = usernameOfPlayer;
        } else {
            throw new DataAccessException("Error: bad request");
        }

        GameData updatedGameObject = new GameData(
                gameFound.gameID(),
                newWhiteUser,
                newBlackUser,
                gameFound.gameName(),
                gameFound.game()
        );

        gameDatabaseAccess.updateGame(updatedGameObject);
    }
}