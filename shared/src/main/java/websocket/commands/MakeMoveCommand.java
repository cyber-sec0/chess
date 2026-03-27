package websocket.commands;

import chess.ChessMove;

/**
 * This class represent the specific command to move a piece over the network socket.
 * It inherit the base command class to respect the server logic architecture properly.
 */
public class MakeMoveCommand extends UserGameCommand { // This class inherit the user game command structure
    public ChessMove move; // The move object variable store the start and end position of piece

    public MakeMoveCommand(
            String authTokenStrParam, Integer gameIdNumParam, ChessMove moveObjParam
    ) { // Constructor initialize the variables memory block securely using parent
        super(CommandType.MAKE_MOVE, authTokenStrParam, gameIdNumParam); // Call the parent constructor securely
        this.move = moveObjParam; // Save the move inside local scope securely for json
    }
}