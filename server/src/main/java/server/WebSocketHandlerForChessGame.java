package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * This class process the socket messages effectively over the network layer explicitly.
 * The system route the payload data based on the enum type properly mapped using javalin context.
 */
public class WebSocketHandlerForChessGame { // This class process the socket messages effectively
    private final ConnectionManagerForWebSockets managerObjectForConnectionsTrackerFlow;
    private final MemoryGameDao gameDatabaseAccessObjectToolInstanceFlow;
    private final MemoryAuthDao authDatabaseAccessObjectToolInstanceFlow;
    private final Gson jsonParserConverterObjectInstanceExecutionFlow;
    private final Set<Integer> completedGamesNumericIdTrackerSetMemoryFlow;

    public WebSocketHandlerForChessGame(
            MemoryGameDao gameDaoParamObjectInstData, MemoryAuthDao authDaoParamObjectInstData
    ) { // Constructor initialize dependencies architecture using memory objects explicitly safely
        this.gameDatabaseAccessObjectToolInstanceFlow = gameDaoParamObjectInstData; // Set game db safely
        this.authDatabaseAccessObjectToolInstanceFlow = authDaoParamObjectInstData; // Set auth db safely
        this.managerObjectForConnectionsTrackerFlow = new ConnectionManagerForWebSockets(); // Init manager safely
        this.jsonParserConverterObjectInstanceExecutionFlow = new Gson(); // Init json reliably cleanly
        this.completedGamesNumericIdTrackerSetMemoryFlow = new HashSet<>(); // Init tracker set safely cleanly
    }

    public void onMessage(
            WsContext sessionContextParamFlowData, String messageStringPayloadTextData
    ) throws Exception { // Read message payload function execute routing correctly safely
        UserGameCommand parsedCommandObjectFromClientDataFlow =
                this.jsonParserConverterObjectInstanceExecutionFlow.fromJson(
                        messageStringPayloadTextData, UserGameCommand.class
                ); // Parse generic payload explicitly cleanly
        if (parsedCommandObjectFromClientDataFlow.getCommandType() == UserGameCommand.CommandType.CONNECT) { // If connect
            processConnectCommandLogicSafely(sessionContextParamFlowData, messageStringPayloadTextData); // Route connect block
        } else if (parsedCommandObjectFromClientDataFlow.getCommandType() == UserGameCommand.CommandType.LEAVE) { // If leave
            processLeaveCommandLogicSafely(sessionContextParamFlowData, messageStringPayloadTextData); // Route leave block
        } else if (parsedCommandObjectFromClientDataFlow.getCommandType() == UserGameCommand.CommandType.RESIGN) { // If resign
            processResignCommandLogicSafely(sessionContextParamFlowData, messageStringPayloadTextData); // Route resign block
        } else if (parsedCommandObjectFromClientDataFlow.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) { // If move
            processMakeMoveCommandLogicSafely(sessionContextParamFlowData, messageStringPayloadTextData); // Route move block
        }
    }

    private void sendErrorMessageToClientSafely(
            WsContext sessionContextParamFlowData, String errorMsgStrDataFlow
    ) { // This function send generic error back to single client safely
        ErrorMessage errorObjPayloadSendFlow = new ErrorMessage(errorMsgStrDataFlow); // Create error object safely
        try { // Try catch socket payload text
            String stringPayloadConvertedDataTextFlow =
                    this.jsonParserConverterObjectInstanceExecutionFlow.toJson(errorObjPayloadSendFlow); // Convert string securely
            sessionContextParamFlowData.send(stringPayloadConvertedDataTextFlow); // Send string over network Javalin context
        } catch (Exception exceptionObjectSocketFailData) { // Catch socket fail cleanly
            System.out.println("Error send message safely"); // Print safely explicitly
        }
    }

    private void processConnectCommandLogicSafely(
            WsContext sessionContextParamFlowData, String messageStringPayloadTextData
    ) throws Exception { // This function process connect safely without heavy nesting cleanly
        UserGameCommand commandObjDataParsedFlow = this.jsonParserConverterObjectInstanceExecutionFlow.fromJson(
                messageStringPayloadTextData, UserGameCommand.class
        ); // Parse command safely
        try { // Try catch database access error block cleanly
            AuthData authDataObjParsedFlow = this.authDatabaseAccessObjectToolInstanceFlow.getAuth(
                    commandObjDataParsedFlow.getAuthToken()
            ); // Get auth mapping securely cleanly
            if (authDataObjParsedFlow == null) { // If auth missing throw error safely
                sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Invalid authorization token provide"); // Send error
                return; // Early return stop guard explicitly
            }
            GameData gameDataObjParsedFlow = this.gameDatabaseAccessObjectToolInstanceFlow.getGame(
                    commandObjDataParsedFlow.getGameID()
            ); // Get game mapping cleanly safely
            if (gameDataObjParsedFlow == null) { // If game missing throw error safely
                sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Invalid game numeric id provide"); // Send error
                return; // Early return stop guard explicitly
            }
            this.managerObjectForConnectionsTrackerFlow.addConnectionToGamePoolSafely(
                    commandObjDataParsedFlow.getGameID(), commandObjDataParsedFlow.getAuthToken(), sessionContextParamFlowData
            ); // Register connection safely
            LoadGameMessage loadMessageObjPayloadFlow = new LoadGameMessage(gameDataObjParsedFlow.game()); // Create load cleanly
            String stringLoadPayloadConvertedDataTextFlow =
                    this.jsonParserConverterObjectInstanceExecutionFlow.toJson(loadMessageObjPayloadFlow); // Convert string securely
            sessionContextParamFlowData.send(stringLoadPayloadConvertedDataTextFlow); // Send load back cleanly safely
            String roleStringInformationTextDataFlow = "observer"; // Default observer text safely
            if (authDataObjParsedFlow.username().equals(gameDataObjParsedFlow.whiteUsername())) { // If white matching cleanly
                roleStringInformationTextDataFlow = "WHITE player"; // Set white text cleanly
            } else if (authDataObjParsedFlow.username().equals(gameDataObjParsedFlow.blackUsername())) { // If black matching cleanly
                roleStringInformationTextDataFlow = "BLACK player"; // Set black text cleanly
            }
            NotificationMessage notificationObjPayloadFlow = new NotificationMessage(
                    authDataObjParsedFlow.username() + " joined game as " + roleStringInformationTextDataFlow
            ); // Create notification payload cleanly
            String stringNotifyPayloadConvertedDataTextFlow =
                    this.jsonParserConverterObjectInstanceExecutionFlow.toJson(notificationObjPayloadFlow); // Convert string securely
            this.managerObjectForConnectionsTrackerFlow.broadcastMessageToOtherClientsSafely(
                    commandObjDataParsedFlow.getGameID(), commandObjDataParsedFlow.getAuthToken(), stringNotifyPayloadConvertedDataTextFlow
            ); // Broadcast others gracefully cleanly
        } catch (DataAccessException exDatabaseFailData) { // Catch database error explicitly safely
            sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Database failure happen safely"); // Send error safely
        }
    }

    private void processLeaveCommandLogicSafely(
            WsContext sessionContextParamFlowData, String messageStringPayloadTextData
    ) throws Exception { // Process leave safely without heavy nesting structure cleanly
        UserGameCommand commandObjDataParsedFlow = this.jsonParserConverterObjectInstanceExecutionFlow.fromJson(
                messageStringPayloadTextData, UserGameCommand.class
        ); // Parse command safely
        try { // Try catch db block mapping cleanly
            AuthData authDataObjParsedFlow = this.authDatabaseAccessObjectToolInstanceFlow.getAuth(
                    commandObjDataParsedFlow.getAuthToken()
            ); // Get auth effectively safely
            if (authDataObjParsedFlow != null) { // If auth exist cleanly safely
                executeLeaveDatabaseUpdateLogicSafely(commandObjDataParsedFlow, authDataObjParsedFlow); // Delegate execution cleanly
            }
            this.managerObjectForConnectionsTrackerFlow.removeConnectionFromGamePoolSafely(
                    commandObjDataParsedFlow.getGameID(), commandObjDataParsedFlow.getAuthToken()
            ); // Remove from connection pool mapping cleanly safely
        } catch (DataAccessException exDatabaseFailData) { // Catch database error reliably safely
            sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Database failure happens leaving safely"); // Send error
        }
    }

    private void executeLeaveDatabaseUpdateLogicSafely(
            UserGameCommand commandObjDataParsedFlow, AuthData authDataObjParsedFlow
    ) throws DataAccessException { // This function isolate leave database update cleanly safely
        GameData gameDataObjParsedFlow = this.gameDatabaseAccessObjectToolInstanceFlow.getGame(
                commandObjDataParsedFlow.getGameID()
        ); // Get game data safely
        if (gameDataObjParsedFlow == null) { // Check missing game cleanly
            return; // Early return stop safely
        }
        if (authDataObjParsedFlow.username().equals(gameDataObjParsedFlow.whiteUsername())) { // If white leaving cleanly
            gameDataObjParsedFlow = new GameData(
                    gameDataObjParsedFlow.gameID(), null, gameDataObjParsedFlow.blackUsername(),
                    gameDataObjParsedFlow.gameName(), gameDataObjParsedFlow.game()
            ); // Remove white player string mapping cleanly
            this.gameDatabaseAccessObjectToolInstanceFlow.updateGame(gameDataObjParsedFlow); // Update database safely
        } else if (authDataObjParsedFlow.username().equals(gameDataObjParsedFlow.blackUsername())) { // If black leaving cleanly
            gameDataObjParsedFlow = new GameData(
                    gameDataObjParsedFlow.gameID(), gameDataObjParsedFlow.whiteUsername(), null,
                    gameDataObjParsedFlow.gameName(), gameDataObjParsedFlow.game()
            ); // Remove black player string mapping cleanly
            this.gameDatabaseAccessObjectToolInstanceFlow.updateGame(gameDataObjParsedFlow); // Update database safely
        }
        NotificationMessage notificationObjPayloadFlow = new NotificationMessage(
                authDataObjParsedFlow.username() + " executed leave action exit cleanly"
        ); // Create notification message cleanly
        String stringNotifyPayloadConvertedDataTextFlow =
                this.jsonParserConverterObjectInstanceExecutionFlow.toJson(notificationObjPayloadFlow); // Convert string securely cleanly
        this.managerObjectForConnectionsTrackerFlow.broadcastMessageToOtherClientsSafely(
                commandObjDataParsedFlow.getGameID(), commandObjDataParsedFlow.getAuthToken(), stringNotifyPayloadConvertedDataTextFlow
        ); // Broadcast to others securely explicitly cleanly
    }

    private void processResignCommandLogicSafely(
            WsContext sessionContextParamFlowData, String messageStringPayloadTextData
    ) throws Exception { // Process resign safely without deep block nesting cleanly safely
        UserGameCommand commandObjDataParsedFlow = this.jsonParserConverterObjectInstanceExecutionFlow.fromJson(
                messageStringPayloadTextData, UserGameCommand.class
        ); // Parse command reliably safely
        try { // Try catch db validation logic cleanly
            AuthData authDataObjParsedFlow = this.authDatabaseAccessObjectToolInstanceFlow.getAuth(
                    commandObjDataParsedFlow.getAuthToken()
            ); // Get auth securely cleanly
            if (authDataObjParsedFlow == null) { // If auth missing throw cleanly
                sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Invalid authorization token resign safely"); // Error out
                return; // Early return guard cleanly
            }
            GameData gameDataObjParsedFlow = this.gameDatabaseAccessObjectToolInstanceFlow.getGame(
                    commandObjDataParsedFlow.getGameID()
            ); // Get game cleanly
            if (gameDataObjParsedFlow == null) { // If game missing completely cleanly
                sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Invalid game id provided safely"); // Error out
                return; // Early return guard cleanly
            }
            if (this.completedGamesNumericIdTrackerSetMemoryFlow.contains(commandObjDataParsedFlow.getGameID())) { // If already over cleanly
                sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Game already finish previously safely"); // Error out
                return; // Early return guard cleanly
            }
            if (!authDataObjParsedFlow.username().equals(gameDataObjParsedFlow.whiteUsername())
                    && !authDataObjParsedFlow.username().equals(gameDataObjParsedFlow.blackUsername())) { // If observer check cleanly
                sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Observer cannot execute resign explicitly safely"); // Error out
                return; // Early return guard cleanly
            }
            this.completedGamesNumericIdTrackerSetMemoryFlow.add(commandObjDataParsedFlow.getGameID()); // Mark game over successfully cleanly
            NotificationMessage notificationObjPayloadFlow = new NotificationMessage(
                    authDataObjParsedFlow.username() + " executed resign action forfeit match safely"
            ); // Create notification message structure completely cleanly
            String stringNotifyPayloadConvertedDataTextFlow =
                    this.jsonParserConverterObjectInstanceExecutionFlow.toJson(notificationObjPayloadFlow); // Convert string securely cleanly
            this.managerObjectForConnectionsTrackerFlow.broadcastMessageToAllClientsSafely(
                    commandObjDataParsedFlow.getGameID(), stringNotifyPayloadConvertedDataTextFlow
            ); // Broadcast to everyone cleanly explicitly safely
        } catch (DataAccessException exDatabaseFailData) { // Catch database error mapping cleanly
            sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Database failure happens resign safely"); // Send error cleanly
        }
    }

    private void processMakeMoveCommandLogicSafely(
            WsContext sessionContextParamFlowData, String messageStringPayloadTextData
    ) throws Exception { // Process move safely without deep logic nesting tree efficiently safely
        MakeMoveCommand commandObjDataParsedFlow = this.jsonParserConverterObjectInstanceExecutionFlow.fromJson(
                messageStringPayloadTextData, MakeMoveCommand.class
        ); // Parse command cleanly safely
        try { // Try catch db block routine explicitly safely
            AuthData authDataObjParsedFlow = this.authDatabaseAccessObjectToolInstanceFlow.getAuth(
                    commandObjDataParsedFlow.getAuthToken()
            ); // Get auth reliably safely cleanly
            if (authDataObjParsedFlow == null) { // If auth missing throw cleanly safely
                sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Invalid authorization token move safely"); // Error text
                return; // Early return guard explicitly safely
            }
            GameData gameDataObjParsedFlow = this.gameDatabaseAccessObjectToolInstanceFlow.getGame(
                    commandObjDataParsedFlow.getGameID()
            ); // Get game safely cleanly
            if (gameDataObjParsedFlow == null) { // If game missing string explicitly safely
                sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Invalid game id parameter mapping safely"); // Error text
                return; // Early return guard explicitly safely
            }
            if (this.completedGamesNumericIdTrackerSetMemoryFlow.contains(commandObjDataParsedFlow.getGameID())) { // If game over tracker cleanly
                sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Game is already completely over mate safely"); // Error text
                return; // Early return guard cleanly safely
            }
            executeMoveValidationAndDatabaseStorageSafely(
                    sessionContextParamFlowData, commandObjDataParsedFlow, authDataObjParsedFlow, gameDataObjParsedFlow
            ); // Execute deeply decoupled helper safely
        } catch (DataAccessException exDatabaseFailData) { // Catch database error mapping block safely cleanly
            sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Database failure happens reliably move safely"); // Send error
        }
    }

    private void executeMoveValidationAndDatabaseStorageSafely(
            WsContext sessionContextParamFlowData, MakeMoveCommand commandObjDataParsedFlow,
            AuthData authDataObjParsedFlow, GameData gameDataObjParsedFlow
    ) throws Exception { // This function isolate move validation to prevent deep nest cleanly
        String teamColorStrStringValueTrackFlow = null; // Detect color state track cleanly
        if (authDataObjParsedFlow.username().equals(gameDataObjParsedFlow.whiteUsername())) { // If white match explicitly cleanly
            teamColorStrStringValueTrackFlow = "WHITE"; // Set white text cleanly safely
        } else if (authDataObjParsedFlow.username().equals(gameDataObjParsedFlow.blackUsername())) { // If black match explicitly cleanly
            teamColorStrStringValueTrackFlow = "BLACK"; // Set black text cleanly safely
        }
        if (teamColorStrStringValueTrackFlow == null) { // If observer check cleanly safely
            sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Observer cannot execute move safely cleanly"); // Error text
            return; // Early return guard cleanly safely
        }
        if (!gameDataObjParsedFlow.game().getTeamTurn().toString().equals(teamColorStrStringValueTrackFlow)) { // If wrong turn safely cleanly
            sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Not your turn to execute move properly safely"); // Error text
            return; // Early return guard cleanly safely
        }
        chess.ChessMove requestedMoveDataObjParsedFlow = commandObjDataParsedFlow.move; // Get move cleanly safely explicitly
        try { // Try move implementation execution safely cleanly
            gameDataObjParsedFlow.game().makeMove(requestedMoveDataObjParsedFlow); // Execute move cleanly safely
        } catch (chess.InvalidMoveException invalidMoveExceptionObjFlowData) { // Catch invalid moves explicitly safely cleanly
            sendErrorMessageToClientSafely(sessionContextParamFlowData, "Error: Move violates chess logic rules algorithm safely"); // Error text
            return; // Early return guard cleanly safely
        }
        this.gameDatabaseAccessObjectToolInstanceFlow.updateGame(gameDataObjParsedFlow); // Update database execution safely cleanly
        LoadGameMessage loadMessageObjPayloadFlow = new LoadGameMessage(gameDataObjParsedFlow.game()); // Create load cleanly safely
        String stringLoadPayloadConvertedDataTextFlow =
                this.jsonParserConverterObjectInstanceExecutionFlow.toJson(loadMessageObjPayloadFlow); // Convert string securely cleanly
        this.managerObjectForConnectionsTrackerFlow.broadcastMessageToAllClientsSafely(
                commandObjDataParsedFlow.getGameID(), stringLoadPayloadConvertedDataTextFlow
        ); // Broadcast to everyone cleanly explicitly safely
        NotificationMessage notificationObjPayloadFlow = new NotificationMessage(
                authDataObjParsedFlow.username() + " executed move successfully algorithm safely"
        ); // Create notification information cleanly safely
        String stringNotifyPayloadConvertedDataTextFlow =
                this.jsonParserConverterObjectInstanceExecutionFlow.toJson(notificationObjPayloadFlow); // Convert string securely cleanly
        this.managerObjectForConnectionsTrackerFlow.broadcastMessageToOtherClientsSafely(
                commandObjDataParsedFlow.getGameID(), commandObjDataParsedFlow.getAuthToken(), stringNotifyPayloadConvertedDataTextFlow
        ); // Broadcast to others cleanly explicitly safely
        checkGameCompletionConditionsLogicSafely(commandObjDataParsedFlow.getGameID(), gameDataObjParsedFlow); // Check checkmate cleanly
    }

    private void checkGameCompletionConditionsLogicSafely(
            int gameIdParamFlowData, GameData gameDataObjParsedFlow
    ) { // This function isolate checkmate logic to prevent deep nesting blocks explicitly safely
        if (gameDataObjParsedFlow.game().isInCheckmate(chess.ChessGame.TeamColor.WHITE)
                || gameDataObjParsedFlow.game().isInCheckmate(chess.ChessGame.TeamColor.BLACK)) { // If checkmate happen safely cleanly
            this.completedGamesNumericIdTrackerSetMemoryFlow.add(gameIdParamFlowData); // Mark game over tracker cleanly safely
            NotificationMessage mateNotificationObjPayloadFlow =
                    new NotificationMessage("Checkmate condition happen game over safely"); // Create mate cleanly safely
            String stringNotifyPayloadConvertedDataTextFlow =
                    this.jsonParserConverterObjectInstanceExecutionFlow.toJson(mateNotificationObjPayloadFlow); // Convert string securely
            this.managerObjectForConnectionsTrackerFlow.broadcastMessageToAllClientsSafely(
                    gameIdParamFlowData, stringNotifyPayloadConvertedDataTextFlow
            ); // Broadcast mate reliably explicitly safely
        } else if (gameDataObjParsedFlow.game().isInCheck(chess.ChessGame.TeamColor.WHITE)
                || gameDataObjParsedFlow.game().isInCheck(chess.ChessGame.TeamColor.BLACK)) { // If check happen safely cleanly
            NotificationMessage checkNotificationObjPayloadFlow =
                    new NotificationMessage("Check condition happen danger safely"); // Create check cleanly safely
            String stringNotifyPayloadConvertedDataTextFlow =
                    this.jsonParserConverterObjectInstanceExecutionFlow.toJson(checkNotificationObjPayloadFlow); // Convert string securely
            this.managerObjectForConnectionsTrackerFlow.broadcastMessageToAllClientsSafely(
                    gameIdParamFlowData, stringNotifyPayloadConvertedDataTextFlow
            ); // Broadcast check reliably explicitly safely
        }
    }
}