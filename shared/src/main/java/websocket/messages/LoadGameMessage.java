package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage { // This class extend the server message baseline object
    public ChessGame game; // The game object state serialization wrapper

    public LoadGameMessage(
            ChessGame gameObjParam
    ) { // Constructor initialize the message structure cleanly
        super(ServerMessageType.LOAD_GAME); // Call super constructor setup mapping
        this.game = gameObjParam; // Save the game object cleanly
    }
}