package client;

import java.util.Scanner;

/**
 * This class contain the user interface loop logic to read terminal commands.
 * The system parse string and decide what function to execute next step.
 */
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
                    ); // Post process call
                } else { // If not log in call pre process
                    keepRunningTheLoopBooleanFlag = processPreLoginCommandString(
                            mainCommandStringWordLower, wordsArrayFromCommandStringSplit
                    ); // Pre process call
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
            executeRegisterCommandUserLogic(wordsArrayFullInputArgument); // Call helper logic
        } else if (commandStringMainArgument.equals("login")) { // If login check argument
            executeLoginCommandUserLogic(wordsArrayFullInputArgument); // Call helper logic
        } else { // If command not exist print unknown
            System.out.println("Unknown command typed. Type help to options"); // Print unknown
        }
        return true; // Return true keep loop
    }

    private void executeRegisterCommandUserLogic(
            String[] wordsArrayFullInputArgument
    ) throws Exception { // This function isolate register logic for clean code structure
        if (wordsArrayFullInputArgument.length < 4) { // If argument short print error return
            System.out.println("Please provide username password email"); // Print error
            return; // Early return avoid deep nesting
        }
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataFromRegisterAction =
                this.serverFacadeObjectForHttpConnection.registerUserOnServer(
                        wordsArrayFullInputArgument[1],
                        wordsArrayFullInputArgument[2],
                        wordsArrayFullInputArgument[3]
                ); // Call facade to register
        this.authorizationTokenStringFromServerResponse =
                responseDataFromRegisterAction.authToken(); // Store token session
        this.isUserCurrentlyLoggedInBooleanStateTracker = true; // Set boolean to true
        System.out.println("Registration success"); // Print success register
    }

    private void executeLoginCommandUserLogic(
            String[] wordsArrayFullInputArgument
    ) throws Exception { // This function isolate login logic for clean code structure
        if (wordsArrayFullInputArgument.length < 3) { // If argument short print error return
            System.out.println("Please provide username password"); // Print error
            return; // Early return avoid deep nesting
        }
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataFromLoginAction =
                this.serverFacadeObjectForHttpConnection.loginUserOnServer(
                        wordsArrayFullInputArgument[1],
                        wordsArrayFullInputArgument[2]
                ); // Call facade to login
        this.authorizationTokenStringFromServerResponse =
                responseDataFromLoginAction.authToken(); // Store token session
        this.isUserCurrentlyLoggedInBooleanStateTracker = true; // Set boolean to true
        System.out.println("Login success"); // Print success login
    }

    private boolean processPostLoginCommandString(
            String commandStringMainArgument, String[] wordsArrayFullInputArgument
    ) throws Exception { // Process check string execute post logic decomposition
        if (commandStringMainArgument.equals("help")) { // If help print instructions
            executeHelpCommandToPrintMenuInstructions(); // Call helper print
        } else if (commandStringMainArgument.equals("logout")) { // If logout call facade
            executeLogoutCommandFromServerSession(); // Call helper logout
        } else if (commandStringMainArgument.equals("create")) { // If create game facade
            executeCreateGameCommandToServerLogic(wordsArrayFullInputArgument); // Call helper create
        } else if (commandStringMainArgument.equals("list")) { // If list call facade array
            executeListGamesCommandFromServerDatabase(); // Call helper list
        } else if (commandStringMainArgument.equals("play")) { // If play call facade draw
            executePlayGameCommandToJoinMatch(wordsArrayFullInputArgument); // Call helper play
        } else if (commandStringMainArgument.equals("observe")) { // If observe check draw
            executeObserveGameCommandToWatchMatch(wordsArrayFullInputArgument); // Call helper observe
        } else { // If command not exist print unknown
            System.out.println("Unknown command typed. Type help to options"); // Print unknown
        }
        return true; // Return true keep loop
    }

    private void executeHelpCommandToPrintMenuInstructions() {
        // This function simply print the post login help menu to the console screen safely
        System.out.println("help - Display this text informing actions"); // Print help
        System.out.println("logout - Log out the user"); // Print help
        System.out.println("create <name> - Create a new game"); // Print help
        System.out.println("list - List all the games"); // Print help
        System.out.println("play <num> [WHITE|BLACK] - Join game play"); // Print help
        System.out.println("observe <num> - Observe a game"); // Print help
    }

    private void executeLogoutCommandFromServerSession() throws Exception {
        // This function send logout request and reset the local variables boolean state
        this.serverFacadeObjectForHttpConnection.logoutUserOnServer(
                this.authorizationTokenStringFromServerResponse
        ); // Call facade execution
        this.isUserCurrentlyLoggedInBooleanStateTracker = false; // Reset boolean false
        this.authorizationTokenStringFromServerResponse = null; // Reset token null
        System.out.println("Logout success"); // Print success message
    }

    private void executeCreateGameCommandToServerLogic(
            String[] wordsArrayFullInputArgument
    ) throws Exception { // This function try to create game in server if arguments are correct
        if (wordsArrayFullInputArgument.length < 2) { // If arguments missing print error
            System.out.println("Please provide the game name"); // Print error console
            return; // Early return to prevent deep nested block
        }
        ServerFacadeForHttpCalls.GameResponseDataFormat responseDataFromCreateAction =
                this.serverFacadeObjectForHttpConnection.createGameOnServer(
                        this.authorizationTokenStringFromServerResponse,
                        wordsArrayFullInputArgument[1]
                ); // Call facade creation
        System.out.println("The new game has the id: "
                + responseDataFromCreateAction.gameID()); // Print success creation
    }

    private void executeListGamesCommandFromServerDatabase() throws Exception {
        // This function list the games from server and save to memory array safely
        ServerFacadeForHttpCalls.ListGamesResponseDataFormat responseDataFromListAction =
                this.serverFacadeObjectForHttpConnection.listGamesOnServer(
                        this.authorizationTokenStringFromServerResponse
                ); // Call facade listing
        this.listOfGamesStoredArrayMemory = responseDataFromListAction.games(); // Store array local
        int indexIntegerTrackerForListPrint = 1; // Init index print numbering
        for (ServerFacadeForHttpCalls.GameDataObjectFormat gameLoopObjectItemArray :
                this.listOfGamesStoredArrayMemory) { // Loop games print terminal iteration
            System.out.println(indexIntegerTrackerForListPrint + ". "
                    + gameLoopObjectItemArray.gameName() + " (White: "
                    + gameLoopObjectItemArray.whiteUsername() + ", Black: "
                    + gameLoopObjectItemArray.blackUsername() + ")"); // Print game information row
            indexIntegerTrackerForListPrint++; // Increment index print counter
        }
    }

    private void executePlayGameCommandToJoinMatch(
            String[] wordsArrayFullInputArgument
    ) throws Exception { // This function try to join the game using the array of words
        if (this.listOfGamesStoredArrayMemory == null) { // If list is null user need to list first
            System.out.println("Please run list command first to update array"); // Print error
            return; // Early return guard clause
        }
        if (wordsArrayFullInputArgument.length < 3) { // If length is small argument is missing
            System.out.println("Please provide game number and color parameter"); // Print error
            return; // Early return guard clause
        }
        try { // Try catch the parse int so string do not crash program execution
            int gameIndexIntegerTargetValue =
                    Integer.parseInt(wordsArrayFullInputArgument[1]) - 1; // Parse int text
            if (gameIndexIntegerTargetValue < 0
                    || gameIndexIntegerTargetValue >= this.listOfGamesStoredArrayMemory.length) { // Check bounds
                System.out.println("Invalid game number provide out of bounds"); // Print error
                return; // Early return guard clause bounds
            }
            int actualGameIdIntegerNumberValue =
                    this.listOfGamesStoredArrayMemory[gameIndexIntegerTargetValue].gameID(); // Get id
            String playerColorStringChoiceValue =
                    wordsArrayFullInputArgument[2].toUpperCase(); // Get color uppercase
            this.serverFacadeObjectForHttpConnection.joinGameOnServer(
                    this.authorizationTokenStringFromServerResponse,
                    playerColorStringChoiceValue, actualGameIdIntegerNumberValue
            ); // Call facade connection
            System.out.println("Join game success"); // Print success
            ChessBoardDrawingUtility boardDrawingUtilityObjectInstanceMake =
                    new ChessBoardDrawingUtility(); // Create utility
            if (playerColorStringChoiceValue.equals("BLACK")) { // If color is black draw black
                boardDrawingUtilityObjectInstanceMake.printBoardForBlackPerspectiveMode(); // Call black
            } else { // If color is white draw white board
                boardDrawingUtilityObjectInstanceMake.printBoardForWhitePerspectiveMode(); // Call white
            }
        } catch (NumberFormatException numberExceptionObjectCaught) { // Catch string input
            System.out.println("Invalid game number provide not a numeric string"); // Print error
        }
    }

    private void executeObserveGameCommandToWatchMatch(
            String[] wordsArrayFullInputArgument
    ) { // This function try to observe the game using the array of words terminal
        if (this.listOfGamesStoredArrayMemory == null) { // If list is null user need to list
            System.out.println("Please run list command first to update array"); // Print error
            return; // Early return guard clause
        }
        if (wordsArrayFullInputArgument.length < 2) { // If length is small argument missing
            System.out.println("Please provide game number numeric parameter"); // Print error
            return; // Early return guard clause
        }
        try { // Try catch the parse int so string do not crash program memory
            int gameIndexIntegerTargetObserveValue =
                    Integer.parseInt(wordsArrayFullInputArgument[1]) - 1; // Parse index text
            if (gameIndexIntegerTargetObserveValue < 0
                    || gameIndexIntegerTargetObserveValue >= this.listOfGamesStoredArrayMemory.length) { // Check bounds
                System.out.println("Invalid game number provide out of bounds"); // Print error
                return; // Early return guard clause bounds
            }
            System.out.println("Observe game is starting"); // Print start text successfully
            ChessBoardDrawingUtility boardDrawingUtilityObjectInstanceForObserveAction =
                    new ChessBoardDrawingUtility(); // Create utility
            boardDrawingUtilityObjectInstanceForObserveAction.printBoardForWhitePerspectiveMode(); // Call print
        } catch (NumberFormatException numberExceptionObjectCaught) { // Catch string input
            System.out.println("Invalid game number provide not a numeric string"); // Print error
        }
    }
}