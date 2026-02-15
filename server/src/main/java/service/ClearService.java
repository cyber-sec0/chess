package service;

import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import dataaccess.MemoryUserDao;

/**
 * This service is responsible for the nuking of the database.
 * It calls the clear method on every data access object.
 */
public class ClearService {

    private final MemoryUserDao uDao;
    private final MemoryGameDao gDao;
    private final MemoryAuthDao aDao;

    public ClearService(MemoryUserDao u, MemoryGameDao g, MemoryAuthDao a) {
        this.uDao = u;
        this.gDao = g;
        this.aDao = a;
    }

    public void clearEverything() {
        uDao.clear();
        gDao.clear();
        aDao.clear();
    }
}