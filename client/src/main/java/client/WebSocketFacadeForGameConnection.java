package client;

import jakarta.websocket.*;
import java.net.URI;

public class WebSocketFacadeForGameConnection extends Endpoint { // This class handle client socket architecture
    public Session networkConnectionSessionObjectClientSide; // The session memory state reference
    public NotificationHandlerInterface notificationCallbackInterfaceReference; // The callback mechanism logic

    public WebSocketFacadeForGameConnection(
            String serverUrlStringPathValue, NotificationHandlerInterface callbackInterfaceParam
    ) throws Exception { // Constructor initialize connection securely
        this.notificationCallbackInterfaceReference = callbackInterfaceParam; // Save callback logic
        URI webSocketUriLocationDestinationObject =
                new URI(serverUrlStringPathValue.replace("http", "ws") + "/ws"); // Parse uri string strictly
        WebSocketContainer containerObjectForSocketsData =
                ContainerProvider.getWebSocketContainer(); // Get container factory
        this.networkConnectionSessionObjectClientSide =
                containerObjectForSocketsData.connectToServer(
                        this, webSocketUriLocationDestinationObject
                ); // Connect dynamically
        this.networkConnectionSessionObjectClientSide.addMessageHandler(
                new MessageHandler.Whole<String>() { // Add handler class inline logic wrapper
                    @Override
                    public void onMessage(String messageStringPayloadReceivedText) { // On message execute callback
                        notificationCallbackInterfaceReference.notifyMessageFromServerAsynchronously(
                                messageStringPayloadReceivedText
                        ); // Trigger callback execution safely
                    }
                }
        );
    }

    @Override
    public void onOpen(Session sessionParam, EndpointConfig configParam) { // On open connection execution routine
        // This function is required by the endpoint inheritance but do nothing currently
    }

    public void sendCommandMessageToServerEndpoint(
            String jsonMessagePayloadStringToSendOut
    ) throws Exception { // This function send text to server socket explicitly
        if (this.networkConnectionSessionObjectClientSide.isOpen()) { // If connection open securely
            this.networkConnectionSessionObjectClientSide.getBasicRemote().sendText(
                    jsonMessagePayloadStringToSendOut
            ); // Send text securely payload logic
        }
    }
}