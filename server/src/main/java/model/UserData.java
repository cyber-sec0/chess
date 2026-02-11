package model;

/**
 * This record is responsible to hold the information about the user in the system.
 * The usage of records is good because it makes the code smaller and immutable.
 */
public record UserData(String username, String password, String email) {}