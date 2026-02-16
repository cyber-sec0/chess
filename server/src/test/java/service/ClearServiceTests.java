package service;

import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import dataaccess.MemoryUserDao;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearServiceTests {

    @Test
    public void clearData() {
        MemoryUserDao u = new MemoryUserDao();
        MemoryGameDao g = new MemoryGameDao();
        MemoryAuthDao a = new MemoryAuthDao();
        ClearService service = new ClearService(u, g, a);

        // Add some garbage data
        u.createUser(new UserData("user", "pass", "email"));
        
        // Clear
        service.clearEverything();
        
        // Verify empty
        Assertions.assertNull(u.getUser("user"));
    }
}