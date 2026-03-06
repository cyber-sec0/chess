package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collection;

/**
 * This class runs the tests for the game database tool memory.
 * It verify if the game data is persistent and correct formatted.
 * Data corruption prevention is the main focus of these validations.
 */
public class MemoryGameDaoTests {

    private MemoryGameDao memoryGameDatabaseAccessObjectForTestingTool;

    /**
     * This method formats the table empty for the isolated test cases.
     * @throws DataAccessException If connection drops.
     */
    @BeforeEach
    public void initializeGameDatabaseToolBeforeTesting() throws DataAccessException {
        // If the tool is not loaded, it makes memory instantiation
        memoryGameDatabaseAccessObjectForTestingTool = new MemoryGameDao();
        memoryGameDatabaseAccessObjectForTestingTool.clear();
    }

    /**
     * This test checks if create game inserts the object inside database.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void createGamePositiveSuccessTest() throws DataAccessException {
        // If the game object is complete, it must insert the row in the table
        int newGameNumericIdentifierValue = 777;
        String newGameNameStringValue = "championship_game";
        ChessGame freshChessGameLogicObject = new ChessGame();

        GameData newGameDataObjectForDatabase = new GameData(
                newGameNumericIdentifierValue,
                null,
                null,
                newGameNameStringValue,
                freshChessGameLogicObject
        );

        memoryGameDatabaseAccessObjectForTestingTool.createGame(newGameDataObjectForDatabase);

        GameData retrievedGameDataObjectFromDatabase = memoryGameDatabaseAccessObjectForTestingTool
                .getGame(newGameNumericIdentifierValue);

        Assertions.assertNotNull(retrievedGameDataObjectFromDatabase);
        Assertions.assertEquals(newGameNameStringValue, retrievedGameDataObjectFromDatabase.gameName());
    }

    /**
     * This negative test verify if duplicate game ID throws error in database.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void createGameNegativeDuplicateIdTest() throws DataAccessException {
        // If the game ID exist, the system must throw exception because primary key conflict
        int conflictGameNumericIdentifierValue = 999;
        GameData firstGameDataObjectForConflict = new GameData(
                conflictGameNumericIdentifierValue, null, null, "First Game", new ChessGame()
        );
        GameData secondGameDataObjectForConflict = new GameData(
                conflictGameNumericIdentifierValue, null, null, "Second Game", new ChessGame()
        );

        memoryGameDatabaseAccessObjectForTestingTool.createGame(firstGameDataObjectForConflict);

        Assertions.assertThrows(DataAccessException.class, () -> {
            memoryGameDatabaseAccessObjectForTestingTool.createGame(secondGameDataObjectForConflict);
        });
    }

    /**
     * This test checks if the database gets the exact game requested by ID.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void getGamePositiveSuccessTest() throws DataAccessException {
        // If the ID is valid, the return object must contain the exact game configuration
        int targetGameNumericIdentifierValue = 505;
        String targetWhitePlayerStringName = "kasparov_wannabe";
        GameData targetGameDataObjectToSave = new GameData(
                targetGameNumericIdentifierValue, targetWhitePlayerStringName, null, "match", new ChessGame()
        );

        memoryGameDatabaseAccessObjectForTestingTool.createGame(targetGameDataObjectToSave);

        GameData foundGameDataObjectResult = memoryGameDatabaseAccessObjectForTestingTool
                .getGame(targetGameNumericIdentifierValue);

        Assertions.assertNotNull(foundGameDataObjectResult);
        Assertions.assertEquals(targetWhitePlayerStringName, foundGameDataObjectResult.whiteUsername());
    }

    /**
     * This negative test verify if searching fake game ID returns null.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void getGameNegativeNotFoundTest() throws DataAccessException {
        // If the requested game ID is fake, the database must return null value safe
        int fakeGameNumericIdentifierValue = 404;
        GameData missingGameDataObjectResult = memoryGameDatabaseAccessObjectForTestingTool
                .getGame(fakeGameNumericIdentifierValue);

        Assertions.assertNull(missingGameDataObjectResult);
    }

    /**
     * This test validates if list games method returns all games in table.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void listGamesPositiveMultipleGamesTest() throws DataAccessException {
        // If the table have multiple rows, the list must contain the exact size of items
        memoryGameDatabaseAccessObjectForTestingTool.createGame(
                new GameData(10, null, null, "G1", new ChessGame())
        );
        memoryGameDatabaseAccessObjectForTestingTool.createGame(
                new GameData(20, null, null, "G2", new ChessGame())
        );

        Collection<GameData> collectionOfGameDataObjectsFromDatabaseTable = memoryGameDatabaseAccessObjectForTestingTool
                .listGames();

        Assertions.assertEquals(2, collectionOfGameDataObjectsFromDatabaseTable.size());
    }

    /**
     * This negative test verify if listing an empty database works without crash.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void listGamesNegativeEmptyListTest() throws DataAccessException {
        // If the table is empty, the list collection must be zero size and not null pointer crash
        Collection<GameData> emptyCollectionOfGameDataObjects = memoryGameDatabaseAccessObjectForTestingTool.listGames();
        Assertions.assertTrue(emptyCollectionOfGameDataObjects.isEmpty());
    }

    /**
     * This test checks if update game method modifies the row correct in database.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void updateGamePositiveSuccessTest() throws DataAccessException {
        // If the update object has new data, the database row must reflect the change
        int updateGameNumericIdentifierValue = 333;
        GameData originalGameDataObject = new GameData(
                updateGameNumericIdentifierValue, null, null, "Original Name", new ChessGame()
        );
        memoryGameDatabaseAccessObjectForTestingTool.createGame(originalGameDataObject);

        String updatedBlackPlayerStringName = "carlsen_fan";
        GameData modifiedGameDataObjectForUpdate = new GameData(
                updateGameNumericIdentifierValue, null, updatedBlackPlayerStringName, "Original Name", new ChessGame()
        );

        memoryGameDatabaseAccessObjectForTestingTool.updateGame(modifiedGameDataObjectForUpdate);

        GameData verifiedGameDataObjectFromDatabase = memoryGameDatabaseAccessObjectForTestingTool
                .getGame(updateGameNumericIdentifierValue);
        Assertions.assertEquals(updatedBlackPlayerStringName, verifiedGameDataObjectFromDatabase.blackUsername());
    }

    /**
     * This negative test verify if update fails when required fields are null.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void updateGameNegativeNullNameFailureTest() throws DataAccessException {
        // If the game name is null, the database must reject the update because schema is NOT NULL
        int targetGameNumericIdentifierValue = 111;
        memoryGameDatabaseAccessObjectForTestingTool.createGame(
                new GameData(targetGameNumericIdentifierValue, null, null, "Valid Name", new ChessGame())
        );

        GameData invalidGameDataObjectForUpdate = new GameData(
                targetGameNumericIdentifierValue, null, null, null, new ChessGame()
        );

        Assertions.assertThrows(DataAccessException.class, () -> {
            memoryGameDatabaseAccessObjectForTestingTool.updateGame(invalidGameDataObjectForUpdate);
        });
    }

    /**
     * This test verify if the clear function erase the game table.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void clearGamesPositiveWithDataTest() throws DataAccessException {
        // If the clear command execute, the list of games must become completely empty
        memoryGameDatabaseAccessObjectForTestingTool.createGame(
                new GameData(55, null, null, "Clear Me", new ChessGame())
        );
        memoryGameDatabaseAccessObjectForTestingTool.clear();

        Assertions.assertTrue(memoryGameDatabaseAccessObjectForTestingTool.listGames().isEmpty());
    }

    /**
     * This negative test checks if clearing empty game table is safe.
     * @throws DataAccessException If database fails.
     */
    @Test
    public void clearGamesNegativeEmptyTableTest() throws DataAccessException {
        // If the table is empty already, clear must not trigger exception crash
        memoryGameDatabaseAccessObjectForTestingTool.clear();
        memoryGameDatabaseAccessObjectForTestingTool.clear(); // Verify twice

        Assertions.assertNull(memoryGameDatabaseAccessObjectForTestingTool.getGame(1));
    }
}