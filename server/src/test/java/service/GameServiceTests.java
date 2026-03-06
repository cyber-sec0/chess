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
 * This class tests the game service functionality to make sure it runs good.
 */
public class GameServiceTests {

    private MemoryGameDao memoryGameDaoToolForTesting;
    private MemoryAuthDao memoryAuthDaoToolForTesting;
    private GameService gameServiceLogicForTesting;
    private String existingAuthTokenStringForGameTest;

    @BeforeEach
    public void setupMethodForTests() throws DataAccessException {
        //Adding throws exception here because clearing database can have problems
        memoryGameDaoToolForTesting = new MemoryGameDao();
        memoryAuthDaoToolForTesting = new MemoryAuthDao();

        //Clear the database tables to prevent bad data clash
        memoryGameDaoToolForTesting.clear();
        memoryAuthDaoToolForTesting.clear();

        gameServiceLogicForTesting = new GameService(memoryGameDaoToolForTesting, memoryAuthDaoToolForTesting);
        AuthData authDataObjectForSetupTesting = new AuthData("valid-token", "player1");
        memoryAuthDaoToolForTesting.createAuth(authDataObjectForSetupTesting);
        existingAuthTokenStringForGameTest = "valid-token";
    }

    //This test is responsible to verify if the creation of a new game returns a positive numeric ID.
    @Test
    public void createGameSuccessTest() throws DataAccessException {
        //Checking if the numeric ID returned by the creation is bigger than zero
        int gameNumericIdResultFromCreation = gameServiceLogicForTesting.createGame(existingAuthTokenStringForGameTest, "My Game");
        Assertions.assertTrue(gameNumericIdResultFromCreation > 0);
    }

    //Validate that a user without a valid session token cannot create a game in the system database.
    @Test
    public void createGameFailBadAuthTest() {
        //Testing if the bad token fails to create the game inside database
        Assertions.assertThrows(DataAccessException.class, () -> gameServiceLogicForTesting.createGame("invalid-token", "My Game"));
    }

    //This function checks if the list of games returns the correct number of games stored in the list.
    @Test
    public void listGamesSuccessTest() throws DataAccessException {
        //Creating two games to verify if the list command returns the exact same number
        gameServiceLogicForTesting.createGame(existingAuthTokenStringForGameTest, "Game 1");
        gameServiceLogicForTesting.createGame(existingAuthTokenStringForGameTest, "Game 2");
        Collection<GameData> gamesCollectionResultFromDatabase = gameServiceLogicForTesting.listGames(existingAuthTokenStringForGameTest);
        Assertions.assertEquals(2, gamesCollectionResultFromDatabase.size());
    }

    //It is necessary to ensure that listing games is a protected action that requires valid authentication.
    @Test
    public void listGamesFailBadAuthTest() {
        //Verifying if the wrong token string blocks the user to see the games list
        Assertions.assertThrows(DataAccessException.class, () -> gameServiceLogicForTesting.listGames("bad-token"));
    }

    //This test validates if a player can successfully join a game that has a free spot for the requested color.
    @Test
    public void joinGameSuccessTest() throws DataAccessException {
        //Checking if the user can join the white color when the slot is empty
        int gameNumericIdFromCreation = gameServiceLogicForTesting.createGame(existingAuthTokenStringForGameTest, "Game 1");
        gameServiceLogicForTesting.joinGame(existingAuthTokenStringForGameTest, "WHITE", gameNumericIdFromCreation);
        GameData gameDataObjectFromDatabaseTable = memoryGameDaoToolForTesting.getGame(gameNumericIdFromCreation);
        Assertions.assertEquals("player1", gameDataObjectFromDatabaseTable.whiteUsername());
    }

    //Verify that the system forbids a user from joining a color that is already taken by another user.
    @Test
    public void joinGameFailColorTakenTest() throws DataAccessException {
        //Making sure that trying to join a taken spot throws the correct exception
        int gameNumericIdFromDatabase = gameServiceLogicForTesting.createGame(existingAuthTokenStringForGameTest, "Game 1");
        gameServiceLogicForTesting.joinGame(existingAuthTokenStringForGameTest, "WHITE", gameNumericIdFromDatabase);
        Assertions.assertThrows(DataAccessException.class, () ->
                gameServiceLogicForTesting.joinGame(existingAuthTokenStringForGameTest, "WHITE", gameNumericIdFromDatabase));
    }
}