package dataaccess;

import model.AuthData;
import java.util.ArrayList;

/**
 * This class manages the authentication tokens in the volatile memory.
 */
public class MemoryAuthDao {

    private static final ArrayList<AuthData> LIST_OF_AUTH_TOKENS = new ArrayList<>();

    public void createAuth(AuthData newAuth) {
        LIST_OF_AUTH_TOKENS.add(newAuth);
    }

    public AuthData getAuth(String tokenString) {
        for (AuthData tokenData : LIST_OF_AUTH_TOKENS) {
            if (tokenData.authToken().equals(tokenString)) {
                return tokenData;
            }
        }
        return null;
    }

    public void deleteAuth(String tokenToDelete) {
        AuthData target = null;
        for (AuthData token : LIST_OF_AUTH_TOKENS) {
            if (token.authToken().equals(tokenToDelete)) {
                target = token;
                break;
            }
        }
        if (target != null) {
            LIST_OF_AUTH_TOKENS.remove(target);
        }
    }

    public void clear() {
        LIST_OF_AUTH_TOKENS.clear();
    }
}