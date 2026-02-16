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

public class GameServiceTests {

    private MemoryGameDao gameDao;
    private MemoryAuthDao authDao;
    private GameService service;
    private String existingAuth;

    @BeforeEach
    public void setup() {
        gameDao = new MemoryGameDao();
        authDao = new MemoryAuthDao();
        service = new GameService(gameDao, authDao);
        
        // We need a valid auth token to do anything
        AuthData auth = new AuthData("valid-token", "player1");
        authDao.createAuth(auth);
        existingAuth = "valid-token";
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        int gameId = service.createGame(existingAuth, "My Game");
        Assertions.assertTrue(gameId > 0);
    }

    @Test
    public void createGameFailBadAuth() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            service.createGame("invalid-token", "My Game");
        });
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        service.createGame(existingAuth, "Game 1");
        service.createGame(existingAuth, "Game 2");
        
        Collection<GameData> games = service.listGames(existingAuth);
        Assertions.assertEquals(2, games.size());
    }

    @Test
    public void listGamesFailBadAuth() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            service.listGames("bad-token");
        });
    }

    @Test
    public void joinGameSuccess() throws DataAccessException {
        int gameId = service.createGame(existingAuth, "Game 1");
        
        service.joinGame(existingAuth, "WHITE", gameId);
        
        // Verify user is in the game
        GameData game = gameDao.getGame(gameId);
        Assertions.assertEquals("player1", game.whiteUsername());
    }

    @Test
    public void joinGameFailColorTaken() throws DataAccessException {
        int gameId = service.createGame(existingAuth, "Game 1");
        service.joinGame(existingAuth, "WHITE", gameId);
        
        // Try to join as white again (with a different user token ideally, but logic checks null)
        // Even same user shouldn't be able to overwrite
        Assertions.assertThrows(DataAccessException.class, () -> {
            service.joinGame(existingAuth, "WHITE", gameId);
        });
    }
}