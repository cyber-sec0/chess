package dataaccess;

import model.UserData;
import java.util.ArrayList;

/**
 * This class is the implementation of the data access for users in the memory of the computer.
 * It uses a list to store the users which is very simple to understand.
 */
public class MemoryUserDao {

    /**
     * The list where all the users are going to be stored during the execution of the program.
     */
    private static final ArrayList<UserData> DATABASE_OF_USERS_LIST = new ArrayList<>();

    /**
     * This function has the responsibility to find a user inside the list by using the name.
     * It iterates over every single user to check if the name matches the parameter.
     * @param usernameToFind The name of the user that needs to be found.
     * @return The UserData object if found, otherwise it returns null.
     */
    public UserData getUser(String usernameToFind) {
        for (UserData currentUserInLoop : DATABASE_OF_USERS_LIST) {
            if (currentUserInLoop.username().equals(usernameToFind)) {
                return currentUserInLoop;
            }
        }
        return null;
    }

    /**
     * This function is responsible for adding a new user to the internal list storage.
     * @param newUserData The object containing the user information.
     */
    public void createUser(UserData newUserData) {
        DATABASE_OF_USERS_LIST.add(newUserData);
    }

    /**
     * This function clears all the data from the list to reset the database.
     */
    public void clear() {
        DATABASE_OF_USERS_LIST.clear();
    }
}