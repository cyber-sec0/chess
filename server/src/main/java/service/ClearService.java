package service;

import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import dataaccess.MemoryUserDao;
import dataaccess.DataAccessException;

/**
 * This class handles the operation to clear the whole system database.
 * It connects the handler requests to the database access objects.
 */
public class ClearService {

    private final MemoryUserDao userDatabaseAccessObjectTool;
    private final MemoryGameDao gameDatabaseAccessObjectTool;
    private final MemoryAuthDao authDatabaseAccessObjectTool;

    // This constructor initialize the tools for the clear service
    public ClearService(MemoryUserDao userDaoObjectParameter, MemoryGameDao gameDaoObjectParameter, MemoryAuthDao authDaoObjectParameter) {
        this.userDatabaseAccessObjectTool = userDaoObjectParameter;
        this.gameDatabaseAccessObjectTool = gameDaoObjectParameter;
        this.authDatabaseAccessObjectTool = authDaoObjectParameter;
    }

    /**
     * This function erases everything from the database tables.
     * @throws DataAccessException If the database fails to execute.
     */
    public void clearEverything() throws DataAccessException {
        // Erasing the user data from table so we have empty space
        userDatabaseAccessObjectTool.clear();
        // Erasing the game data from table so we have empty space
        gameDatabaseAccessObjectTool.clear();
        // Erasing the auth data from table so we have empty space
        authDatabaseAccessObjectTool.clear();
    }
}