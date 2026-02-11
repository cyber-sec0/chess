package model;

import chess.ChessGame;

/**
 * This record contains all the critical information about a specific game of chess.
 * It connects the IDs of the players to the actual game logic object.
 */
public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {}