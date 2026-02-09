package dataaccess;

import model.GameData;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class handles the operations for the games data using the memory.
 * It is important to keep track of the games so players can play.
 */
public class MemoryGameDao {

    /**
     * This is the storage list for the games.
     */
    private static final ArrayList<GameData> STORAGE_FOR_ALL_GAMES = new ArrayList<>();

    /**
     * This method adds a new game to the list.
     * @param gameToAdd The game object to add.
     */
    public void createGame(GameData gameToAdd) {
        STORAGE_FOR_ALL_GAMES.add(gameToAdd);
    }

    /**
     * This function searches for a game using the unique numeric identifier.
     * It goes through the list one by one.
     * @param gameNumericId The ID to search for.
     * @return The game data if found.
     */
    public GameData getGame(int gameNumericId) {
        for (GameData individualGame : STORAGE_FOR_ALL_GAMES) {
            if (individualGame.gameID() == gameNumericId) {
                return individualGame;
            }
        }
        return null;
    }

    /**
     * This returns all the games currently in the system.
     * @return A collection of games.
     */
    public Collection<GameData> listGames() {
        return STORAGE_FOR_ALL_GAMES;
    }

    /**
     * This function updates a game by removing the old version and adding the new version.
     * It is a simple way to do an update operation.
     * @param updatedGameInformation The new state of the game.
     */
    public void updateGame(GameData updatedGameInformation) {
        GameData oldGameToRemove = null;
        for (GameData g : STORAGE_FOR_ALL_GAMES) {
            if (g.gameID() == updatedGameInformation.gameID()) {
                oldGameToRemove = g;
                break;
            }
        }

        if (oldGameToRemove != null) {
            STORAGE_FOR_ALL_GAMES.remove(oldGameToRemove);
            STORAGE_FOR_ALL_GAMES.add(updatedGameInformation);
        }
    }

    public void clear() {
        STORAGE_FOR_ALL_GAMES.clear();
    }
}