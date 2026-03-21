package client;

import java.util.Scanner;

public class TerminalReplUserInterface { // This class keep program running asking user inputs
    private final ServerFacadeForHttpCalls serverFacadeObjectForHttpConnection; // Facade object store
    private boolean isUserCurrentlyLoggedInBooleanStateTracker; // State tracker for login store
    private String authorizationTokenStringFromServerResponse; // Token store here
    private final Scanner scannerObjectForTerminalReadingInput; // Scanner object store string read
    private ServerFacadeForHttpCalls.GameDataObjectFormat[] listOfGamesStoredArrayMemory; // Array store

    public TerminalReplUserInterface(int portNumberForServerConnectionArgument) { // Constructor init
        this.serverFacadeObjectForHttpConnection =
                new ServerFacadeForHttpCalls(portNumberForServerConnectionArgument); // Init facade
        this.isUserCurrentlyLoggedInBooleanStateTracker = false; // Init boolean state
        this.authorizationTokenStringFromServerResponse = null; // Init token string
        this.scannerObjectForTerminalReadingInput = new Scanner(System.in); // Init scanner
    }

    public void runTheProgramLoop() { // Loop start here print welcome ask input
        System.out.println("Welcome to the chess game application!"); // Print welcome
        boolean keepRunningTheLoopBooleanFlag = true; // Init flag tracker
        do { // Do while loop check login state show text menu
            if (this.isUserCurrentlyLoggedInBooleanStateTracker) { // If user log in show post
                System.out.print("[LOGGED_IN] >>> "); // Print post login
            } else { // If user not log in show pre menu
                System.out.print("[LOGGED_OUT] >>> "); // Print pre login
            }
            String userCommandStringInputRaw =
                    this.scannerObjectForTerminalReadingInput.nextLine(); // Read string
            String[] wordsArrayFromCommandStringSplit =
                    userCommandStringInputRaw.split(" "); // Split string
            String mainCommandStringWordLower =
                    wordsArrayFromCommandStringSplit[0].toLowerCase(); // Get command
            try { // Try catch network call not crash
                if (this.isUserCurrentlyLoggedInBooleanStateTracker) { // If log in call post
                    keepRunningTheLoopBooleanFlag = processPostLoginCommandString(
                            mainCommandStringWordLower, wordsArrayFromCommandStringSplit
                    ); // Post process
                } else { // If not log in call pre process
                    keepRunningTheLoopBooleanFlag = processPreLoginCommandString(
                            mainCommandStringWordLower, wordsArrayFromCommandStringSplit
                    ); // Pre process
                }
            } catch (Exception exceptionObjectCaughtFromExecutionRun) { // Catch print error
                System.out.println("Error happened: "
                        + exceptionObjectCaughtFromExecutionRun.getMessage()); // Print error
            }
        } while (keepRunningTheLoopBooleanFlag); // Loop until flag false
    }

    private boolean processPreLoginCommandString(
            String commandStringMainArgument, String[] wordsArrayFullInputArgument
    ) throws Exception { // Process check string execute pre logic
        if (commandStringMainArgument.equals("quit")) { // If quit return false stop
            return false; // Return false
        } else if (commandStringMainArgument.equals("help")) { // If help print text
            System.out.println("help - Display this text informing actions"); // Print help
            System.out.println("quit - Exit the program"); // Print help
            System.out.println("login <user> <pass> - Login the user"); // Print help
            System.out.println("register <user> <pass> <email> - Register new"); // Print help
        } else if (commandStringMainArgument.equals("register")) { // If register call facade
            if (wordsArrayFullInputArgument.length >= 4) { // Check arg size register
                ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataFromRegisterAction =
                        this.serverFacadeObjectForHttpConnection.registerUserOnServer(
                                wordsArrayFullInputArgument[1],
                                wordsArrayFullInputArgument[2],
                                wordsArrayFullInputArgument[3]
                        ); // Call facade
                this.authorizationTokenStringFromServerResponse =
                        responseDataFromRegisterAction.authToken(); // Store token
                this.isUserCurrentlyLoggedInBooleanStateTracker = true; // Set boolean
                System.out.println("Registration is success for the user"); // Print success
            } else { // Print error argument missing register
                System.out.println("Please provide username password email"); // Print error
            }
        } else if (commandStringMainArgument.equals("login")) { // If login check argument
            if (wordsArrayFullInputArgument.length >= 3) { // Check arg size login
                ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataFromLoginAction =
                        this.serverFacadeObjectForHttpConnection.loginUserOnServer(
                                wordsArrayFullInputArgument[1],
                                wordsArrayFullInputArgument[2]
                        ); // Call facade
                this.authorizationTokenStringFromServerResponse =
                        responseDataFromLoginAction.authToken(); // Store token
                this.isUserCurrentlyLoggedInBooleanStateTracker = true; // Set boolean
                System.out.println("Login is success for the user"); // Print success
            } else { // Print error argument missing login
                System.out.println("Please provide username password"); // Print error
            }
        } else { // If command not exist print unknown
            System.out.println("Unknown command typed. Type help to options"); // Print unknown
        }
        return true; // Return true keep loop
    }

    private boolean processPostLoginCommandString(
            String commandStringMainArgument, String[] wordsArrayFullInputArgument
    ) throws Exception { // Process check string execute post logic
        if (commandStringMainArgument.equals("help")) { // If help print instructions
            System.out.println("help - Display this text informing actions"); // Print help
            System.out.println("logout - Log out the user"); // Print help
            System.out.println("create <name> - Create a new game"); // Print help
            System.out.println("list - List all the games"); // Print help
            System.out.println("play <num> [WHITE|BLACK] - Join game play"); // Print help
            System.out.println("observe <num> - Observe a game"); // Print help
        } else if (commandStringMainArgument.equals("logout")) { // If logout call facade
            this.serverFacadeObjectForHttpConnection.logoutUserOnServer(
                    this.authorizationTokenStringFromServerResponse
            ); // Call facade
            this.isUserCurrentlyLoggedInBooleanStateTracker = false; // Reset boolean
            this.authorizationTokenStringFromServerResponse = null; // Reset token
            System.out.println("Logout is success for the user"); // Print success
        } else if (commandStringMainArgument.equals("create")) { // If create game facade
            if (wordsArrayFullInputArgument.length >= 2) { // Check arg size create
                ServerFacadeForHttpCalls.GameResponseDataFormat responseDataFromCreateAction =
                        this.serverFacadeObjectForHttpConnection.createGameOnServer(
                                this.authorizationTokenStringFromServerResponse,
                                wordsArrayFullInputArgument[1]
                        ); // Call facade
                System.out.println("Game create is success with id: "
                        + responseDataFromCreateAction.gameID()); // Print success
            } else { // Print error argument missing create
                System.out.println("Please provide the game name"); // Print error
            }
        } else if (commandStringMainArgument.equals("list")) { // If list call facade array
            ServerFacadeForHttpCalls.ListGamesResponseDataFormat responseDataFromListAction =
                    this.serverFacadeObjectForHttpConnection.listGamesOnServer(
                            this.authorizationTokenStringFromServerResponse
                    ); // Call facade
            this.listOfGamesStoredArrayMemory = responseDataFromListAction.games(); // Store array
            int indexIntegerTrackerForListPrint = 1; // Init index print
            for (ServerFacadeForHttpCalls.GameDataObjectFormat gameLoopObjectItemArray :
                    this.listOfGamesStoredArrayMemory) { // Loop games print terminal
                System.out.println(indexIntegerTrackerForListPrint + ". "
                        + gameLoopObjectItemArray.gameName() + " (White: "
                        + gameLoopObjectItemArray.whiteUsername() + ", Black: "
                        + gameLoopObjectItemArray.blackUsername() + ")"); // Print game info
                indexIntegerTrackerForListPrint++; // Increment index
            }
        } else if (commandStringMainArgument.equals("play")) { // If play call facade draw
            if (wordsArrayFullInputArgument.length >= 3
                    && this.listOfGamesStoredArrayMemory != null) { // Check size array
                int gameIndexIntegerTargetValue =
                        Integer.parseInt(wordsArrayFullInputArgument[1]) - 1; // Parse int
                if (gameIndexIntegerTargetValue >= 0
                        && gameIndexIntegerTargetValue < this.listOfGamesStoredArrayMemory.length) { // Check bounds
                    int actualGameIdIntegerNumberValue =
                            this.listOfGamesStoredArrayMemory[gameIndexIntegerTargetValue].gameID(); // Get id
                    String playerColorStringChoiceValue =
                            wordsArrayFullInputArgument[2].toUpperCase(); // Get color
                    this.serverFacadeObjectForHttpConnection.joinGameOnServer(
                            this.authorizationTokenStringFromServerResponse,
                            playerColorStringChoiceValue,
                            actualGameIdIntegerNumberValue
                    ); // Call facade
                    System.out.println("Join game is success for user"); // Print success
                    ChessBoardDrawingUtility boardDrawingUtilityObjectInstanceMake =
                            new ChessBoardDrawingUtility(); // Create utility
                    if (playerColorStringChoiceValue.equals("BLACK")) { // If color black layout
                        boardDrawingUtilityObjectInstanceMake.printBoardForBlackPerspectiveMode(); // Call black
                    } else { // If color white print white layout
                        boardDrawingUtilityObjectInstanceMake.printBoardForWhitePerspectiveMode(); // Call white
                    }
                } else { // Print error invalid index
                    System.out.println("Invalid game number provide"); // Print error
                }
            } else { // Print error argument missing play
                System.out.println("Please provide game number color list first"); // Print error
            }
        } else if (commandStringMainArgument.equals("observe")) { // If observe check draw
            if (wordsArrayFullInputArgument.length >= 2
                    && this.listOfGamesStoredArrayMemory != null) { // Check argument array
                int gameIndexIntegerTargetObserveValue =
                        Integer.parseInt(wordsArrayFullInputArgument[1]) - 1; // Parse index
                if (gameIndexIntegerTargetObserveValue >= 0
                        && gameIndexIntegerTargetObserveValue < this.listOfGamesStoredArrayMemory.length) { // Check bound
                    System.out.println("Observe game is starting"); // Print start
                    ChessBoardDrawingUtility boardDrawingUtilityObjectInstanceForObserveAction =
                            new ChessBoardDrawingUtility(); // Create utility
                    boardDrawingUtilityObjectInstanceForObserveAction.printBoardForWhitePerspectiveMode(); // Call print
                } else { // Print error invalid index
                    System.out.println("Invalid game number provide"); // Print error
                }
            } else { // Print error missing argument observe
                System.out.println("Please provide game number list games first"); // Print error
            }
        } else { // If command not exist print unknown
            System.out.println("Unknown command typed. Type help to options"); // Print unknown
        }
        return true; // Return true keep loop
    }
}