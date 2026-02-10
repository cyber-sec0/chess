package model;

/**
 * This record represents the authentication token data.
 * It links the token string to the username so the server knows who is making the request.
 */
public record AuthData(String authToken, String username) {}