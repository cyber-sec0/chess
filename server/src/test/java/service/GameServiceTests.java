package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collection;

/**
 * This class tests the game service functionality.
 */
public class GameServiceTests {

    private MemoryGameDao memoryGameDaoTool;
    private MemoryAuthDao memoryAuthDaoTool;
    private GameService gameServiceLogic;
    private String existingAuthTokenString;

    @BeforeEach
    public void setupMethodForTests() {
        memoryGameDaoTool = new MemoryGameDao();
        memoryAuthDaoTool = new MemoryAuthDao();
        
        // Clear the static lists
        memoryGameDaoTool.clear();
        memoryAuthDaoTool.clear();
        
        gameServiceLogic = new GameService(memoryGameDaoTool, memoryAuthDaoTool);
        AuthData authData = new AuthData("valid-token", "player1");
        memoryAuthDaoTool.createAuth(authData);
        existingAuthTokenString = "valid-token";
    }

    // This test is responsible to verify if the creation of a new game returns a positive numeric ID.
    @Test
    public void createGameSuccessTest() throws DataAccessException {
        int gameNumericId = gameServiceLogic.createGame(existingAuthTokenString, "My Game");
        Assertions.assertTrue(gameNumericId > 0);
    }

    // Validate that a user without a valid session token cannot create a game in the system.
    @Test
    public void createGameFailBadAuthTest() {
        Assertions.assertThrows(DataAccessException.class, () -> gameServiceLogic.createGame("invalid-token", "My Game"));
    }

    // This function checks if the list of games returns the correct number of games stored in the list.
    @Test
    public void listGamesSuccessTest() throws DataAccessException {
        gameServiceLogic.createGame(existingAuthTokenString, "Game 1");
        gameServiceLogic.createGame(existingAuthTokenString, "Game 2");
        Collection<GameData> gamesCollection = gameServiceLogic.listGames(existingAuthTokenString);
        Assertions.assertEquals(2, gamesCollection.size());
    }

    // It is necessary to ensure that listing games is a protected action that requires valid authentication.
    @Test
    public void listGamesFailBadAuthTest() {
        Assertions.assertThrows(DataAccessException.class, () -> gameServiceLogic.listGames("bad-token"));
    }

    // This test validates if a player can successfully join a game that has a free spot for the requested color.
    @Test
    public void joinGameSuccessTest() throws DataAccessException {
        int gameNumericId = gameServiceLogic.createGame(existingAuthTokenString, "Game 1");
        gameServiceLogic.joinGame(existingAuthTokenString, "WHITE", gameNumericId);
        GameData gameData = memoryGameDaoTool.getGame(gameNumericId);
        Assertions.assertEquals("player1", gameData.whiteUsername());
    }

    // Verify that the system forbids a user from joining a color that is already taken by another user.
    @Test
    public void joinGameFailColorTakenTest() throws DataAccessException {
        int gameNumericId = gameServiceLogic.createGame(existingAuthTokenString, "Game 1");
        gameServiceLogic.joinGame(existingAuthTokenString, "WHITE", gameNumericId);
        Assertions.assertThrows(DataAccessException.class, () -> gameServiceLogic.joinGame(existingAuthTokenString, "WHITE", gameNumericId));
    }
}