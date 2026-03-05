package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import dataaccess.MemoryUserDao;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearServiceTests {

    //This test performs a check to ensure that all data is completely nuked from the memory database storage.
    @Test
    public void clearDataTest() throws DataAccessException {
        //Adding the exception throw because the clear commands touch the database and can crash
        MemoryUserDao userDaoToolForClearingTest = new MemoryUserDao();
        MemoryGameDao gameDaoToolForClearingTest = new MemoryGameDao();
        MemoryAuthDao authDaoToolForClearingTest = new MemoryAuthDao();
        ClearService clearServiceLogicForTesting = new ClearService(userDaoToolForClearingTest, gameDaoToolForClearingTest, authDaoToolForClearingTest);

        userDaoToolForClearingTest.createUser(new UserData("user", "pass", "email"));
        clearServiceLogicForTesting.clearEverything();
        Assertions.assertNull(userDaoToolForClearingTest.getUser("user"));
    }
}