package client;

import java.util.Scanner;

/**
 * This class contain the user interface loop logic to read terminal commands.
 * The system parse string and decide what function to execute next step explicitly.
 * It use the scanner object to read the text the user type in the keyboard efficiently.
 * Then it process the string to connect with the server application socket payload.
 */
public class TerminalReplUserInterface implements NotificationHandlerInterface { // This class keep program running asking user inputs
    private final ServerFacadeForHttpCalls serverFacadeObjectForHttpConnection;
    private final int savedPortNumberMemoryTrackerVariable;
    private boolean isUserCurrentlyLoggedInBooleanStateTracker;
    private String authorizationTokenStringFromServerResponse;
    private final Scanner scannerObjectForTerminalReadingInput;
    private ServerFacadeForHttpCalls.GameDataObjectFormat[] listOfGamesStoredArrayMemory;

    private boolean isUserCurrentlyInGameplayStateTrackerBoolean;
    private String currentUserGameRoleColorPerspectiveStringTracker;
    private int currentGameNumericIdActiveSessionValueTracker;
    private chess.ChessGame currentGameStorageObjectMemoryInstance;
    private WebSocketFacadeForGameConnection websocketFacadeForGameplayExecutionObject;

    public TerminalReplUserInterface(
            int portNumberForServerConnectionArgumentParam
    ) { // Constructor init the facade and scanner cleanly
        this.savedPortNumberMemoryTrackerVariable = portNumberForServerConnectionArgumentParam;
        this.serverFacadeObjectForHttpConnection =
                new ServerFacadeForHttpCalls(portNumberForServerConnectionArgumentParam);
        this.isUserCurrentlyLoggedInBooleanStateTracker = false;
        this.isUserCurrentlyInGameplayStateTrackerBoolean = false;
        this.authorizationTokenStringFromServerResponse = null;
        this.scannerObjectForTerminalReadingInput = new Scanner(System.in);
    }

    public void runTheProgramLoop() { // Loop start here print welcome ask input text
        System.out.println("Welcome to the chess game application!");
        boolean keepRunningTheLoopBooleanFlagTracker = true;
        do { // Do while loop check login state show text menu string
            printCurrentTerminalPromptStringLogic();
            String userCommandStringInputRawText =
                    this.scannerObjectForTerminalReadingInput.nextLine();
            String[] wordsArrayFromCommandStringSplitText =
                    userCommandStringInputRawText.split(" ");
            String mainCommandStringWordLowerText =
                    wordsArrayFromCommandStringSplitText[0].toLowerCase();
            try { // Try catch network call not crash memory
                if (this.isUserCurrentlyInGameplayStateTrackerBoolean) { // If in game
                    keepRunningTheLoopBooleanFlagTracker = processInGameCommandStringSafely(
                            mainCommandStringWordLowerText, wordsArrayFromCommandStringSplitText
                    );
                } else if (this.isUserCurrentlyLoggedInBooleanStateTracker) { // If log in call post
                    keepRunningTheLoopBooleanFlagTracker = processPostLoginCommandStringSafely(
                            mainCommandStringWordLowerText, wordsArrayFromCommandStringSplitText
                    );
                } else { // If not log in call pre process explicitly
                    keepRunningTheLoopBooleanFlagTracker = processPreLoginCommandStringSafely(
                            mainCommandStringWordLowerText, wordsArrayFromCommandStringSplitText
                    );
                }
            } catch (Exception exceptionObjectCaughtFromExecutionRunFlow) { // Catch print error
                System.out.println("Error happened: "
                        + exceptionObjectCaughtFromExecutionRunFlow.getMessage());
            }
        } while (keepRunningTheLoopBooleanFlagTracker); // Loop until flag false exit
    }

    private void printCurrentTerminalPromptStringLogic() { // Helper to reprint prompt cleanly state
        if (this.isUserCurrentlyInGameplayStateTrackerBoolean) { // If in game state
            System.out.print("\n[IN_GAME] >>> ");
        } else if (this.isUserCurrentlyLoggedInBooleanStateTracker) { // If log in show post safely
            System.out.print("\n[LOGGED_IN] >>> ");
        } else { // If user not log in show pre menu safely
            System.out.print("\n[LOGGED_OUT] >>> ");
        }
    }

    @Override
    public void notifyMessageFromServerAsynchronously(
            String jsonMessagePayloadStringTextReceived
    ) { // Override callback method execution efficiently
        com.google.gson.Gson gsonParserObjectInstLocalVar = new com.google.gson.Gson(); // Init parser object local
        websocket.messages.ServerMessage baseMessageObjParsedState = gsonParserObjectInstLocalVar.fromJson(
                jsonMessagePayloadStringTextReceived, websocket.messages.ServerMessage.class
        ); // Parse type safely
        if (baseMessageObjParsedState.getServerMessageType()
                == websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME) { // If load
            websocket.messages.LoadGameMessage loadMessageObjParsedState = gsonParserObjectInstLocalVar.fromJson(
                    jsonMessagePayloadStringTextReceived, websocket.messages.LoadGameMessage.class
            ); // Parse full
            this.currentGameStorageObjectMemoryInstance = loadMessageObjParsedState.game; // Save game locally
            executeRedrawCommandToRefreshBoardGraphicsSafely(); // Call redraw helper execution explicitly safely
        } else if (baseMessageObjParsedState.getServerMessageType()
                == websocket.messages.ServerMessage.ServerMessageType.ERROR) { // If error
            websocket.messages.ErrorMessage errorMessageObjParsedState = gsonParserObjectInstLocalVar.fromJson(
                    jsonMessagePayloadStringTextReceived, websocket.messages.ErrorMessage.class
            ); // Parse full error
            System.out.println("\n[SERVER_ERROR] " + errorMessageObjParsedState.errorMessage); // Print error explicitly
            printCurrentTerminalPromptStringLogic(); // Reprint prompt inline execution
        } else if (baseMessageObjParsedState.getServerMessageType()
                == websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION) { // If notification
            websocket.messages.NotificationMessage notificationMessageObjParsedState = gsonParserObjectInstLocalVar.fromJson(
                    jsonMessagePayloadStringTextReceived, websocket.messages.NotificationMessage.class
            ); // Parse full notification
            System.out.println("\n[SERVER_NOTIFICATION] " + notificationMessageObjParsedState.message); // Print notification explicitly
            printCurrentTerminalPromptStringLogic(); // Reprint prompt inline execution
        }
    }

    private boolean processPreLoginCommandStringSafely(
            String commandStringMainArgumentParam, String[] wordsArrayFullInputArgumentParam
    ) throws Exception { // Process check string execute pre logic
        switch (commandStringMainArgumentParam) { // Switch statement
            case "quit": // If quit return false stop
                return false; // Break loop execution out
            case "help": // If help print text
                String preLoginHelpMenuStringVariableForConsoleObject =
                        "help - Display this text informing actions\n"
                                + "quit - Exit the program\n"
                                + "login <user> <pass> - Login the user\n"
                                + "register <user> <pass> <email> - Register new"; // Build text explicit
                System.out.println(preLoginHelpMenuStringVariableForConsoleObject); // Print explicitly to screen
                break; // Break switch logic cleanly
            case "register": // If register call facade
                executeRegisterCommandUserLogicSafely(wordsArrayFullInputArgumentParam); // Call explicitly safely
                break; // Break switch logic cleanly
            case "login": // If login check argument
                executeLoginCommandUserLogicSafely(wordsArrayFullInputArgumentParam); // Call explicitly safely
                break; // Break switch logic cleanly
            default: // If command not exist print unknown explicitly
                System.out.println("Unknown command typed. Type help to options"); // Print the unknown
                break; // Break switch logic cleanly
        }
        return true; // Return true maintain loop
    }

    private void executeRegisterCommandUserLogicSafely(
            String[] wordsArrayFullInputArgumentParam
    ) throws Exception { // This function isolate register logic for clean code structure
        if (wordsArrayFullInputArgumentParam.length < 4) { // If argument short print error return
            System.out.println("Please provide username password email");
            return;
        }
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataFromRegisterActionObject =
                this.serverFacadeObjectForHttpConnection.registerUserOnServer(
                        wordsArrayFullInputArgumentParam[1],
                        wordsArrayFullInputArgumentParam[2],
                        wordsArrayFullInputArgumentParam[3]
                );
        this.authorizationTokenStringFromServerResponse =
                responseDataFromRegisterActionObject.authToken();
        this.isUserCurrentlyLoggedInBooleanStateTracker = true;
        System.out.println("Registration success");
    }

    private void executeLoginCommandUserLogicSafely(
            String[] wordsArrayFullInputArgumentParam
    ) throws Exception { // This function isolate login logic for clean code structure
        if (wordsArrayFullInputArgumentParam.length < 3) { // If argument short print error return
            System.out.println("Please provide username password");
            return;
        }
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataFromLoginActionObject =
                this.serverFacadeObjectForHttpConnection.loginUserOnServer(
                        wordsArrayFullInputArgumentParam[1],
                        wordsArrayFullInputArgumentParam[2]
                );
        this.authorizationTokenStringFromServerResponse =
                responseDataFromLoginActionObject.authToken();
        this.isUserCurrentlyLoggedInBooleanStateTracker = true;
        System.out.println("Login success");
    }

    private boolean processPostLoginCommandStringSafely(
            String commandStringMainArgumentParam, String[] wordsArrayFullInputArgumentParam
    ) throws Exception { // Process check string execute post logic decomposition
        switch (commandStringMainArgumentParam) { // Switch statement for layout control
            case "help": // If help print instructions
                executeHelpCommandToPrintMenuInstructionsSafely(); // Print post login help explicitly
                break; // Break switch logic cleanly
            case "logout": // If logout call facade
                executeLogoutCommandFromServerSessionSafely(); // Logout session token explicitly
                break; // Break switch logic cleanly
            case "create": // If create game facade
                executeCreateGameCommandToServerLogicSafely(wordsArrayFullInputArgumentParam); // Create network HTTP
                break; // Break switch logic cleanly
            case "list": // If list call facade array
                executeListGamesCommandFromServerDatabaseSafely(); // Request array collection HTTP
                break; // Break switch logic cleanly
            case "play": // If play call facade draw
                executePlayGameCommandToJoinMatchSafely(wordsArrayFullInputArgumentParam); // Start connection socket explicitly
                break; // Break switch logic cleanly
            case "observe": // If observe check draw
                executeObserveGameCommandToWatchMatchSafely(wordsArrayFullInputArgumentParam); // Start observer connection explicitly
                break; // Break switch logic cleanly
            default: // If command not exist print unknown explicitly
                System.out.println("Unknown command typed. Type help to options"); // Print unknown
                break; // Break switch logic cleanly
        }
        return true; // Return true maintain loop execution
    }

    private boolean processInGameCommandStringSafely(
            String commandStringMainArgumentParam, String[] wordsArrayFullInputArgumentParam
    ) throws Exception { // Process check string execute in game logic decomposition
        switch (commandStringMainArgumentParam) { // Switch statement for structure block obfuscation
            case "help": // If help print instructions locally
                executeHelpCommandToPrintGameplayInstructionsSafely(); // Print explicitly in game menu
                break; // Break switch logic cleanly
            case "leave": // If leave call facade socket
                executeLeaveGameCommandToExitSessionSafely(); // Send network socket payload reliably
                break; // Break switch logic cleanly
            case "resign": // If resign facade socket
                executeResignGameCommandToForfeitMatchSafely(); // Forfeit connection message explicitly
                break; // Break switch logic cleanly
            case "redraw": // If redraw print locally memory
                executeRedrawCommandToRefreshBoardGraphicsSafely(); // Refresh the graphic visual explicitly
                break; // Break switch logic cleanly
            case "move": // If move call facade move socket
                executeMakeMoveCommandToProcessActionSafely(wordsArrayFullInputArgumentParam); // Process piece move correctly
                break; // Break switch logic cleanly
            case "highlight": // If highlight check calculate memory
                executeHighlightCommandToShowLegalMovesSafely(wordsArrayFullInputArgumentParam); // See valid collection explicitly
                break; // Break switch logic cleanly
            default: // If command not exist print unknown explicitly
                System.out.println("Unknown command typed. Type help to options"); // Print unknown correctly
                break; // Break switch logic cleanly
        }
        return true; // Keep true loop alive connection
    }

    private void executeHelpCommandToPrintMenuInstructionsSafely() {
        // This function simply print the post login help menu to the console screen safely
        String postLoginHelpMenuStringVariableForConsoleObject =
                "help - Display this text informing actions\n"
                        + "logout - Log out the user\n"
                        + "create <name> - Create a new game\n"
                        + "list - List all the games\n"
                        + "play <num> [WHITE|BLACK] - Join game play\n"
                        + "observe <num> - Observe a game";
        System.out.println(postLoginHelpMenuStringVariableForConsoleObject);
    }

    private void executeHelpCommandToPrintGameplayInstructionsSafely() {
        // This function simply print the in game help menu to the console screen safely
        String inGameHelpMenuStringVariableForConsoleObject =
                "help - Display this text informing actions\n"
                        + "redraw - Redraw the chess board locally\n"
                        + "leave - Leave the game session\n"
                        + "move <start> <end> - Make a piece move (e.g. move e2 e4)\n"
                        + "resign - Forfeit the match\n"
                        + "highlight <pos> - Highlight legal moves for a piece (e.g. highlight e2)";
        System.out.println(inGameHelpMenuStringVariableForConsoleObject);
    }

    private void executeLogoutCommandFromServerSessionSafely() throws Exception {
        // This function send logout request and reset the local variables boolean state game
        this.serverFacadeObjectForHttpConnection.logoutUserOnServer(
                this.authorizationTokenStringFromServerResponse
        );
        this.isUserCurrentlyLoggedInBooleanStateTracker = false;
        this.authorizationTokenStringFromServerResponse = null;
        System.out.println("Logout success");
    }

    private void executeCreateGameCommandToServerLogicSafely(
            String[] wordsArrayFullInputArgumentParam
    ) throws Exception { // This function try to create game in server safely
        if (wordsArrayFullInputArgumentParam.length < 2) { // If arguments missing print error
            System.out.println("Please provide the game name");
            return;
        }
        ServerFacadeForHttpCalls.GameResponseDataFormat responseDataFromCreateActionObject =
                this.serverFacadeObjectForHttpConnection.createGameOnServer(
                        this.authorizationTokenStringFromServerResponse,
                        wordsArrayFullInputArgumentParam[1]
                );
        System.out.println("The new game has the id: "
                + responseDataFromCreateActionObject.gameID());
    }

    private void executeListGamesCommandFromServerDatabaseSafely() throws Exception {
        // This function list the games from server and save to memory array safely
        ServerFacadeForHttpCalls.ListGamesResponseDataFormat responseDataFromListActionObject =
                this.serverFacadeObjectForHttpConnection.listGamesOnServer(
                        this.authorizationTokenStringFromServerResponse
                );
        this.listOfGamesStoredArrayMemory = responseDataFromListActionObject.games();
        int indexIntegerTrackerForListPrintLocal = 1;
        for (ServerFacadeForHttpCalls.GameDataObjectFormat gameLoopObjectItemArrayLoop :
                this.listOfGamesStoredArrayMemory) { // Loop games print terminal iteration
            System.out.println(indexIntegerTrackerForListPrintLocal + ". "
                    + gameLoopObjectItemArrayLoop.gameName() + " (White: "
                    + gameLoopObjectItemArrayLoop.whiteUsername() + ", Black: "
                    + gameLoopObjectItemArrayLoop.blackUsername() + ")");
            indexIntegerTrackerForListPrintLocal++;
        }
    }

    private void executePlayGameCommandToJoinMatchSafely(
            String[] wordsArrayFullInputArgumentParam
    ) throws Exception { // This function try to join the game using socket flow
        if (this.listOfGamesStoredArrayMemory == null) { // If list is null user need to list first
            System.out.println("Please run list command first to update array");
            return;
        }
        if (wordsArrayFullInputArgumentParam.length < 3) { // If length is small argument is missing
            System.out.println("Please provide game number and color parameter");
            return;
        }
        try { // Try catch the parse int so string do not crash program execution
            int gameIndexIntegerTargetValueParsed =
                    Integer.parseInt(wordsArrayFullInputArgumentParam[1]) - 1;
            if (gameIndexIntegerTargetValueParsed < 0
                    || gameIndexIntegerTargetValueParsed >= this.listOfGamesStoredArrayMemory.length) { // Check bounds
                System.out.println("Invalid game number provide out of bounds");
                return;
            }
            int actualGameIdIntegerNumberValueExtracted =
                    this.listOfGamesStoredArrayMemory[gameIndexIntegerTargetValueParsed].gameID();
            String playerColorStringChoiceValueParsed =
                    wordsArrayFullInputArgumentParam[2].toUpperCase();
            this.serverFacadeObjectForHttpConnection.joinGameOnServer(
                    this.authorizationTokenStringFromServerResponse,
                    playerColorStringChoiceValueParsed, actualGameIdIntegerNumberValueExtracted
            ); // Call http connection initially

            this.websocketFacadeForGameplayExecutionObject = new WebSocketFacadeForGameConnection(
                    "http://localhost:" + this.savedPortNumberMemoryTrackerVariable, this
            ); // Init websocket facade dynamically

            websocket.commands.UserGameCommand connectCommandObjectInstance =
                    new websocket.commands.UserGameCommand(
                            websocket.commands.UserGameCommand.CommandType.CONNECT,
                            this.authorizationTokenStringFromServerResponse,
                            actualGameIdIntegerNumberValueExtracted
                    ); // Create connect command securely

            String stringPayloadConvertedDataToSend = new com.google.gson.Gson().toJson(connectCommandObjectInstance); // Convert
            this.websocketFacadeForGameplayExecutionObject.sendCommandMessageToServerEndpoint(
                    stringPayloadConvertedDataToSend
            ); // Send socket explicitly

            this.isUserCurrentlyInGameplayStateTrackerBoolean = true; // State change
            this.currentGameNumericIdActiveSessionValueTracker = actualGameIdIntegerNumberValueExtracted; // Save track
            this.currentUserGameRoleColorPerspectiveStringTracker = playerColorStringChoiceValueParsed; // Save track explicitly
            System.out.println("Join game success connection started");
        } catch (NumberFormatException numberExceptionObjectCaughtFlow) { // Catch string input explicitly
            System.out.println("Invalid game number provide not a numeric string");
        }
    }

    private void executeObserveGameCommandToWatchMatchSafely(
            String[] wordsArrayFullInputArgumentParam
    ) { // This function try to observe the game using socket flow terminal
        if (this.listOfGamesStoredArrayMemory == null) { // If list is null user need to list
            System.out.println("Please run list command first to update array");
            return;
        }
        if (wordsArrayFullInputArgumentParam.length < 2) { // If length is small argument missing
            System.out.println("Please provide game number numeric parameter");
            return;
        }
        try { // Try catch the parse int so string do not crash program memory
            int gameIndexIntegerTargetObserveValueParsed =
                    Integer.parseInt(wordsArrayFullInputArgumentParam[1]) - 1;
            if (gameIndexIntegerTargetObserveValueParsed < 0
                    || gameIndexIntegerTargetObserveValueParsed >= this.listOfGamesStoredArrayMemory.length) { // Check bounds
                System.out.println("Invalid game number provide out of bounds");
                return;
            }
            int actualGameIdIntegerNumberValueExtracted =
                    this.listOfGamesStoredArrayMemory[gameIndexIntegerTargetObserveValueParsed].gameID();

            this.websocketFacadeForGameplayExecutionObject = new WebSocketFacadeForGameConnection(
                    "http://localhost:" + this.savedPortNumberMemoryTrackerVariable, this
            ); // Init websocket facade dynamically

            websocket.commands.UserGameCommand connectCommandObjectInstance =
                    new websocket.commands.UserGameCommand(
                            websocket.commands.UserGameCommand.CommandType.CONNECT,
                            this.authorizationTokenStringFromServerResponse,
                            actualGameIdIntegerNumberValueExtracted
                    ); // Create connect command securely

            String stringPayloadConvertedDataToSend = new com.google.gson.Gson().toJson(connectCommandObjectInstance); // Convert
            this.websocketFacadeForGameplayExecutionObject.sendCommandMessageToServerEndpoint(
                    stringPayloadConvertedDataToSend
            ); // Send socket explicitly

            this.isUserCurrentlyInGameplayStateTrackerBoolean = true; // State change observer
            this.currentGameNumericIdActiveSessionValueTracker = actualGameIdIntegerNumberValueExtracted; // Save track explicitly
            this.currentUserGameRoleColorPerspectiveStringTracker = "OBSERVER"; // Save observer implicitly
            System.out.println("Observe game is starting connection requested");
        } catch (Exception exceptionObjectCaughtFlow) { // Catch string input safely
            System.out.println("Invalid execution happen provide not a numeric string");
        }
    }

    private void executeLeaveGameCommandToExitSessionSafely() throws Exception {
        // This function send leave request and reset the local variables boolean state game
        websocket.commands.UserGameCommand leaveCommandObjectInstance =
                new websocket.commands.UserGameCommand(
                        websocket.commands.UserGameCommand.CommandType.LEAVE,
                        this.authorizationTokenStringFromServerResponse,
                        this.currentGameNumericIdActiveSessionValueTracker
                ); // Create leave command securely

        String stringPayloadConvertedDataToSend = new com.google.gson.Gson().toJson(leaveCommandObjectInstance); // Convert cleanly
        this.websocketFacadeForGameplayExecutionObject.sendCommandMessageToServerEndpoint(
                stringPayloadConvertedDataToSend
        ); // Send socket explicitly

        this.isUserCurrentlyInGameplayStateTrackerBoolean = false; // Reset boolean false explicitly
        this.currentGameStorageObjectMemoryInstance = null; // Reset board null reliably
        System.out.println("Leave game success session exit safely"); // Print success message
    }

    private void executeResignGameCommandToForfeitMatchSafely() throws Exception {
        // This function send resign request after reading terminal confirmation specifically
        System.out.println("Are you sure you want to resign forfeit? type 'yes' to confirm"); // Print confirm cleanly
        String confirmStringInputRawUserResponse = this.scannerObjectForTerminalReadingInput.nextLine(); // Read confirm text
        if (confirmStringInputRawUserResponse.toLowerCase().equals("yes")) { // If confirm yes
            websocket.commands.UserGameCommand resignCommandObjectInstance =
                    new websocket.commands.UserGameCommand(
                            websocket.commands.UserGameCommand.CommandType.RESIGN,
                            this.authorizationTokenStringFromServerResponse,
                            this.currentGameNumericIdActiveSessionValueTracker
                    ); // Create resign command reliably

            String stringPayloadConvertedDataToSend = new com.google.gson.Gson().toJson(resignCommandObjectInstance); // Convert cleanly
            this.websocketFacadeForGameplayExecutionObject.sendCommandMessageToServerEndpoint(
                    stringPayloadConvertedDataToSend
            ); // Send socket explicitly
        } else { // If not confirm exit procedure safely
            System.out.println("Resign action cancelled safely completely"); // Print cancel output explicitly
        }
    }

    private void executeRedrawCommandToRefreshBoardGraphicsSafely() { // This function refresh graphics explicitly memory connection
        GameplayActionExecutionHelperUtilityForTerminalClient helperObjectLocalInstanceRef =
                new GameplayActionExecutionHelperUtilityForTerminalClient(); // Init helper object cleanly
        helperObjectLocalInstanceRef.executeRedrawCommandToRefreshBoardGraphicsSafely(
                this.currentGameStorageObjectMemoryInstance,
                this.currentUserGameRoleColorPerspectiveStringTracker
        ); // Call execution redraw properly explicit
    }

    private void executeMakeMoveCommandToProcessActionSafely(
            String[] wordsArrayFullInputArgumentParam
    ) throws Exception { // This function extract string positions and send to facade explicitly to move memory state
        GameplayActionExecutionHelperUtilityForTerminalClient helperObjectLocalInstanceRef =
                new GameplayActionExecutionHelperUtilityForTerminalClient(); // Init helper object cleanly
        helperObjectLocalInstanceRef.executeMakeMoveCommandToProcessActionSafely(
                wordsArrayFullInputArgumentParam,
                this.authorizationTokenStringFromServerResponse,
                this.currentGameNumericIdActiveSessionValueTracker,
                this.websocketFacadeForGameplayExecutionObject
        ); // Call execution move properly explicit
    }

    private void executeHighlightCommandToShowLegalMovesSafely(
            String[] wordsArrayFullInputArgumentParam
    ) { // This function process highlight logic calculation locally graphics processing module
        GameplayActionExecutionHelperUtilityForTerminalClient helperObjectLocalInstanceRef =
                new GameplayActionExecutionHelperUtilityForTerminalClient(); // Init helper object cleanly
        helperObjectLocalInstanceRef.executeHighlightCommandToShowLegalMovesSafely(
                wordsArrayFullInputArgumentParam,
                this.currentGameStorageObjectMemoryInstance,
                this.currentUserGameRoleColorPerspectiveStringTracker
        ); // Call execution highlight properly explicit
    }
}