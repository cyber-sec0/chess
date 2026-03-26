package client;

public interface NotificationHandlerInterface { // This interface act as callback for socket streaming asynchronous
    void notifyMessageFromServerAsynchronously(
            String jsonMessagePayloadStringText
    ); // The method signature logic implementation
}