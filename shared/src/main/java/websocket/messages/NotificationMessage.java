package websocket.messages;

public class NotificationMessage extends ServerMessage { // This class represent notification information update
    public String message; // The notification string data payload text

    public NotificationMessage(
            String msgStrParam
    ) { // Constructor initialize the notification parameters cleanly
        super(ServerMessageType.NOTIFICATION); // Call super constructor setup mapping
        this.message = msgStrParam; // Save message string explicitly
    }
}