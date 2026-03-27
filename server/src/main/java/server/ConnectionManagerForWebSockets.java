package server;

import io.javalin.websocket.WsContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class manage active connections mapping securely explicitly.
 * It use the javalin context object to track the network users directly.
 */
public class ConnectionManagerForWebSockets { // This class manage active connections mapping securely
    public final Map<Integer, Map<String, WsContext>> gameNetworkConnectionsMapTrackerFlow; // Store connections safely

    public ConnectionManagerForWebSockets() { // Constructor initialization allocation execution
        this.gameNetworkConnectionsMapTrackerFlow = new ConcurrentHashMap<>(); // Init map safely
    }

    public void addConnectionToGamePoolSafely(
            int gameIdParamData, String authTokenParamData, WsContext sessionContextParamData
    ) { // This function add new session to the structure cleanly
        if (!this.gameNetworkConnectionsMapTrackerFlow.containsKey(gameIdParamData)) { // If game not exist safely
            this.gameNetworkConnectionsMapTrackerFlow.put(
                    gameIdParamData, new ConcurrentHashMap<>()
            ); // Create new map inside safely cleanly
        }
        Map<String, WsContext> specificGameConnectionsMapDataFlow =
                this.gameNetworkConnectionsMapTrackerFlow.get(gameIdParamData); // Get map specifically safely
        specificGameConnectionsMapDataFlow.put(authTokenParamData, sessionContextParamData); // Add session memory mapping cleanly
    }

    public void removeConnectionFromGamePoolSafely(
            int gameIdParamData, String authTokenParamData
    ) { // This function remove session from structure safely without crashing
        if (this.gameNetworkConnectionsMapTrackerFlow.containsKey(gameIdParamData)) { // If game exist safely
            Map<String, WsContext> specificGameConnectionsMapDataFlow =
                    this.gameNetworkConnectionsMapTrackerFlow.get(gameIdParamData); // Get map specifically safely
            specificGameConnectionsMapDataFlow.remove(authTokenParamData); // Remove session cleanly safely
        }
    }

    public void broadcastMessageToOtherClientsSafely(
            int gameIdParamData, String excludeAuthTokenParamData, String jsonMessageStringPayloadTextData
    ) { // This function send message to everyone except one token explicitly
        if (!this.gameNetworkConnectionsMapTrackerFlow.containsKey(gameIdParamData)) { // Check guard explicitly
            return; // Early return stop cleanly
        }
        Map<String, WsContext> specificGameConnectionsMapDataFlow =
                this.gameNetworkConnectionsMapTrackerFlow.get(gameIdParamData); // Get map specifically safely
        for (Map.Entry<String, WsContext> connectionEntryObjectDataFlow :
                specificGameConnectionsMapDataFlow.entrySet()) { // Loop every connection safely cleanly
            if (connectionEntryObjectDataFlow.getKey().equals(excludeAuthTokenParamData)) { // If match skip safely
                continue; // Skip execution explicitly cleanly
            }
            try { // Try catch network payload sending
                connectionEntryObjectDataFlow.getValue().send(
                        jsonMessageStringPayloadTextData
                ); // Send string message text reliably safely
            } catch (Exception socketExceptionCaughtFlowData) { // Catch exception network drop
                System.out.println("Socket closed unexpectedly safely"); // Print trace safely explicitly
            }
        }
    }

    public void broadcastMessageToAllClientsSafely(
            int gameIdParamData, String jsonMessageStringPayloadTextData
    ) { // This function send message to absolutely everyone in lobby explicitly
        if (!this.gameNetworkConnectionsMapTrackerFlow.containsKey(gameIdParamData)) { // Check guard explicitly safely
            return; // Early return stop cleanly
        }
        Map<String, WsContext> specificGameConnectionsMapDataFlow =
                this.gameNetworkConnectionsMapTrackerFlow.get(gameIdParamData); // Get map specifically safely
        for (Map.Entry<String, WsContext> connectionEntryObjectDataFlow :
                specificGameConnectionsMapDataFlow.entrySet()) { // Loop every connection safely cleanly
            try { // Try catch network payload sending cleanly
                connectionEntryObjectDataFlow.getValue().send(
                        jsonMessageStringPayloadTextData
                ); // Send string message text explicitly safely
            } catch (Exception socketExceptionCaughtFlowData) { // Catch exception network drop safely
                System.out.println("Socket closed unexpectedly cleanly"); // Print trace safely explicitly
            }
        }
    }
}