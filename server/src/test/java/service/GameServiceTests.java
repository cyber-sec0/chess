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

    @Test
    public void createGameSuccessTest() throws DataAccessException {
        int gameNumericId = gameServiceLogic.createGame(existingAuthTokenString, "My Game");
        Assertions.assertTrue(gameNumericId > 0);
    }

    @Test
    public void createGameFailBadAuthTest() {
        Assertions.assertThrows(DataAccessException.class, () -> gameServiceLogic.createGame("invalid-token", "My Game"));
    }

    @Test
    public void listGamesSuccessTest() throws DataAccessException {
        gameServiceLogic.createGame(existingAuthTokenString, "Game 1");
        gameServiceLogic.createGame(existingAuthTokenString, "Game 2");
        Collection<GameData> gamesCollection = gameServiceLogic.listGames(existingAuthTokenString);
        Assertions.assertEquals(2, gamesCollection.size());
    }

    @Test
    public void listGamesFailBadAuthTest() {
        Assertions.assertThrows(DataAccessException.class, () -> gameServiceLogic.listGames("bad-token"));
    }

    @Test
    public void joinGameSuccessTest() throws DataAccessException {
        int gameNumericId = gameServiceLogic.createGame(existingAuthTokenString, "Game 1");
        gameServiceLogic.joinGame(existingAuthTokenString, "WHITE", gameNumericId);
        GameData gameData = memoryGameDaoTool.getGame(gameNumericId);
        Assertions.assertEquals("player1", gameData.whiteUsername());
    }

    @Test
    public void joinGameFailColorTakenTest() throws DataAccessException {
        int gameNumericId = gameServiceLogic.createGame(existingAuthTokenString, "Game 1");
        gameServiceLogic.joinGame(existingAuthTokenString, "WHITE", gameNumericId);
        Assertions.assertThrows(DataAccessException.class, () -> gameServiceLogic.joinGame(existingAuthTokenString, "WHITE", gameNumericId));
    }
}