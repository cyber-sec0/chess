package client;

import java.util.Scanner;

public class TerminalReplUserInterface { // This class keep program running asking user inputs execution
    private final ServerFacadeForHttpCalls serverFacadeObjectForHttpConnection; // Facade object is store here
    private boolean isUserCurrentlyLoggedInBooleanStateTracker; // State tracker for login is store here
    private String authorizationTokenStringFromServerResponse; // Token is store here
    private final Scanner scannerObjectForTerminalReadingInput; // Scanner object is store string read
    private ServerFacadeForHttpCalls.GameDataObjectFormat[] listOfGamesStoredArrayMemory; // Games array store here

    public TerminalReplUserInterface(int portNumberForServerConnectionArgument) { // Constructor init facade scanner
        this.serverFacadeObjectForHttpConnection =
                new ServerFacadeForHttpCalls(portNumberForServerConnectionArgument); // Init facade
        this.isUserCurrentlyLoggedInBooleanStateTracker = false; // Init boolean state
        this.authorizationTokenStringFromServerResponse = null; // Init token string
        this.scannerObjectForTerminalReadingInput = new Scanner(System.in); // Init scanner
    }

    public void runTheProgramLoop() { // Loop start here print welcome ask input continuously
        System.out.println("Welcome to the chess game application!"); // Print welcome
        boolean keepRunningTheLoopBooleanFlag = true; // Init flag tracker
        do { // Do while loop check login state show text menu terminal
            if (this.isUserCurrentlyLoggedInBooleanStateTracker) { // If user log in show post menu
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
            try { // Try catch network call not crash app
                if (this.isUserCurrentlyLoggedInBooleanStateTracker) { // If state log in call post process
                    keepRunningTheLoopBooleanFlag = processPostLoginCommandString(
                            mainCommandStringWordLower, wordsArrayFromCommandStringSplit
                    ); // Post process
                } else { // If state not log in call pre process
                    keepRunningTheLoopBooleanFlag = processPreLoginCommandString(
                            mainCommandStringWordLower, wordsArrayFromCommandStringSplit
                    ); // Pre process
                }
            } catch (Exception exceptionObjectCaughtFromExecutionRun) { // Catch print error program not stop
                System.out.println("Error happened: "
                        + exceptionObjectCaughtFromExecutionRun.getMessage()); // Print error
            }
        } while (keepRunningTheLoopBooleanFlag); // Loop until flag false
    }

    private boolean processPreLoginCommandString(
            String commandStringMainArgument, String[] wordsArrayFullInputArgument
    ) throws Exception { // Process function check string execute action logic pre
        if (commandStringMainArgument.equals("quit")) { // If user want quit return false stop loop
            return false; // Return false
        } else if (commandStringMainArgument.equals("help")) { // If user want help print text
            System.out.println("help - Display this text informing the actions"); // Print help
            System.out.println("quit - Exit the program"); // Print help
            System.out.println("login <user> <pass> - Login the user"); // Print help
            System.out.println("register <user> <pass> <email> - Register new"); // Print help
        } else if (commandStringMainArgument.equals("register")) { // If user register call facade
            if (wordsArrayFullInputArgument.length >= 4) { // Check argument size call register
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
                System.out.println("Please provide the username password and email"); // Print error
            }
        } else if (commandStringMainArgument.equals("login")) { // If user login check argument
            if (wordsArrayFullInputArgument.length >= 3) { // Check argument size call login
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
                System.out.println("Please provide the username and password"); // Print error
            }
        } else { // If command not exist print unknown
            System.out.println("Unknown command typed. Type help to see options"); // Print unknown
        }
        return true; // Return true keep loop
    }

    private boolean processPostLoginCommandString(
            String commandStringMainArgument, String[] wordsArrayFullInputArgument
    ) throws Exception { // Process function check string execute action logic post
        if (commandStringMainArgument.equals("help")) { // If user want help print instructions
            System.out.println("help - Display this text informing the actions"); // Print help
            System.out.println("logout - Log out the user"); // Print help
            System.out.println("create <name> - Create a new game"); // Print help
            System.out.println("list - List all the games"); // Print help
            System.out.println("play <num> [WHITE|BLACK] - Join a game to play"); // Print help
            System.out.println("observe <num> - Observe a game"); // Print help
        } else if (commandStringMainArgument.equals("logout")) { // If user logout call facade
            this.serverFacadeObjectForHttpConnection.logoutUserOnServer(
                    this.authorizationTokenStringFromServerResponse
            ); // Call facade
            this.isUserCurrentlyLoggedInBooleanStateTracker = false; // Reset boolean
            this.authorizationTokenStringFromServerResponse = null; // Reset token
            System.out.println("Logout is success for the user"); // Print success
        } else if (commandStringMainArgument.equals("create")) { // If user create game facade logic
            if (wordsArrayFullInputArgument.length >= 2) { // Check argument size create
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
        } else if (commandStringMainArgument.equals("list")) { // If user list call facade print array
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
                indexIntegerTrackerForListPrint++; // Increment index print
            }
        } else if (commandStringMainArgument.equals("play")) { // If user play call facade draw logic
            if (wordsArrayFullInputArgument.length >= 3 && this.listOfGamesStoredArrayMemory != null) { // Check size array
                int gameIndexIntegerTargetValue =
                        Integer.parseInt(wordsArrayFullInputArgument[1]) - 1; // Parse int index
                if (gameIndexIntegerTargetValue >= 0 && gameIndexIntegerTargetValue < this.listOfGamesStoredArrayMemory.length) { // Check index bounds
                    int actualGameIdIntegerNumberValue =
                            this.listOfGamesStoredArrayMemory[gameIndexIntegerTargetValue].gameID(); // Get actual id
                    String playerColorStringChoiceValue =
                            wordsArrayFullInputArgument[2].toUpperCase(); // Get color string
                    this.serverFacadeObjectForHttpConnection.joinGameOnServer(
                            this.authorizationTokenStringFromServerResponse,
                            playerColorStringChoiceValue,
                            actualGameIdIntegerNumberValue
                    ); // Call facade logic
                    System.out.println("Join game is success for the user"); // Print success text
                    ChessBoardDrawingUtility boardDrawingUtilityObjectInstanceMake =
                            new ChessBoardDrawingUtility(); // Create utility object
                    if (playerColorStringChoiceValue.equals("BLACK")) { // If color black print layout
                        boardDrawingUtilityObjectInstanceMake.printBoardForBlackPerspectiveMode(); // Call print black
                    } else { // If color white print white layout
                        boardDrawingUtilityObjectInstanceMake.printBoardForWhitePerspectiveMode(); // Call print white
                    }
                } else { // Print error invalid index
                    System.out.println("Invalid game number provide"); // Print error
                }
            } else { // Print error argument missing play
                System.out.println("Please provide game number and color or list games"); // Print error
            }
        } else if (commandStringMainArgument.equals("observe")) { // If user observe check draw logic
            if (wordsArrayFullInputArgument.length >= 2 && this.listOfGamesStoredArrayMemory != null) { // Check argument array
                int gameIndexIntegerTargetObserveValue =
                        Integer.parseInt(wordsArrayFullInputArgument[1]) - 1; // Parse integer index
                if (gameIndexIntegerTargetObserveValue >= 0 && gameIndexIntegerTargetObserveValue < this.listOfGamesStoredArrayMemory.length) { // Check index bound observe
                    System.out.println("Observe game is starting"); // Print start text
                    ChessBoardDrawingUtility boardDrawingUtilityObjectInstanceForObserveAction =
                            new ChessBoardDrawingUtility(); // Create utility
                    boardDrawingUtilityObjectInstanceForObserveAction.printBoardForWhitePerspectiveMode(); // Call print
                } else { // Print error invalid index
                    System.out.println("Invalid game number provide"); // Print error
                }
            } else { // Print error missing argument observe
                System.out.println("Please provide game number or list games first"); // Print error
            }
        } else { // If command not exist print unknown string
            System.out.println("Unknown command typed. Type help to see options"); // Print unknown
        }
        return true; // Return true keep loop
    }
}