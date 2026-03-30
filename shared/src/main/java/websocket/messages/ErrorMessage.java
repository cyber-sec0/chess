package websocket.messages;

public class ErrorMessage extends ServerMessage { // This class represent error notification payload packet
    public String errorMessage; // The error string data payload text

    public ErrorMessage(
            String errorStrParam
    ) { // Constructor initialize the error parameters cleanly
        super(ServerMessageType.ERROR); // Call super constructor setup mapping
        this.errorMessage = errorStrParam; // Save error string explicitly
    }
}