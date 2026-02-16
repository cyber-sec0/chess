package service;

import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import dataaccess.MemoryUserDao;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearServiceTests {

    // This test performs a check to ensure that all data is completely nuked from the memory storage.
    @Test
    public void clearDataTest() {
        MemoryUserDao userDaoTool = new MemoryUserDao();
        MemoryGameDao gameDaoTool = new MemoryGameDao();
        MemoryAuthDao authDaoTool = new MemoryAuthDao();
        ClearService clearServiceLogic = new ClearService(userDaoTool, gameDaoTool, authDaoTool);

        userDaoTool.createUser(new UserData("user", "pass", "email"));
        clearServiceLogic.clearEverything();
        Assertions.assertNull(userDaoTool.getUser("user"));
    }
}