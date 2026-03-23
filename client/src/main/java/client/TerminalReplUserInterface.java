package client;

import java.util.Scanner;

/**
 * This class contain the user interface loop logic to read terminal commands.
 * The system parse string and decide what function to execute next step.
 * It use the scanner object to read the text the user type in the keyboard.
 * Then it process the string to connect with the server application.
 */
public class TerminalReplUserInterface { // This class keep program running asking user inputs
    private final ServerFacadeForHttpCalls serverFacadeObjectForHttpConnection;
    private boolean isUserCurrentlyLoggedInBooleanStateTracker;
    private String authorizationTokenStringFromServerResponse;
    private final Scanner scannerObjectForTerminalReadingInput;
    private ServerFacadeForHttpCalls.GameDataObjectFormat[] listOfGamesStoredArrayMemory;

    public TerminalReplUserInterface(int portNumberForServerConnectionArgument) { // Constructor init the facade and scanner
        this.serverFacadeObjectForHttpConnection =
                new ServerFacadeForHttpCalls(portNumberForServerConnectionArgument);
        this.isUserCurrentlyLoggedInBooleanStateTracker = false;
        this.authorizationTokenStringFromServerResponse = null;
        this.scannerObjectForTerminalReadingInput = new Scanner(System.in);
    }

    /**
     * This method is the main loop running the program terminal interface.
     * The loop use do while logic to print the menu and wait the user type something.
     * If the user type quit the loop change flag to false and program stop execution.
     */
    public void runTheProgramLoop() { // Loop start here print welcome ask input
        System.out.println("Welcome to the chess game application!");
        boolean keepRunningTheLoopBooleanFlag = true;
        do { // Do while loop check login state show text menu
            if (this.isUserCurrentlyLoggedInBooleanStateTracker) { // If user log in show post
                System.out.print("[LOGGED_IN] >>> ");
            } else { // If user not log in show pre menu
                System.out.print("[LOGGED_OUT] >>> ");
            }
            String userCommandStringInputRaw =
                    this.scannerObjectForTerminalReadingInput.nextLine();
            String[] wordsArrayFromCommandStringSplit =
                    userCommandStringInputRaw.split(" ");
            String mainCommandStringWordLower =
                    wordsArrayFromCommandStringSplit[0].toLowerCase();
            try { // Try catch network call not crash
                if (this.isUserCurrentlyLoggedInBooleanStateTracker) { // If log in call post
                    keepRunningTheLoopBooleanFlag = processPostLoginCommandString(
                            mainCommandStringWordLower, wordsArrayFromCommandStringSplit
                    );
                } else { // If not log in call pre process
                    keepRunningTheLoopBooleanFlag = processPreLoginCommandString(
                            mainCommandStringWordLower, wordsArrayFromCommandStringSplit
                    );
                }
            } catch (Exception exceptionObjectCaughtFromExecutionRun) { // Catch print error
                System.out.println("Error happened: "
                        + exceptionObjectCaughtFromExecutionRun.getMessage());
            }
        } while (keepRunningTheLoopBooleanFlag); // Loop until flag false
    }

    private boolean processPreLoginCommandString(
            String commandStringMainArgument, String[] wordsArrayFullInputArgument
    ) throws Exception { // Process check string execute pre logic
        if (commandStringMainArgument.equals("quit")) { // If quit return false stop
            return false;
        } else if (commandStringMainArgument.equals("help")) { // If help print text
            String preLoginHelpMenuStringVariableForConsole =
                    "help - Display this text informing actions\n"
                            + "quit - Exit the program\n"
                            + "login <user> <pass> - Login the user\n"
                            + "register <user> <pass> <email> - Register new";
            System.out.println(preLoginHelpMenuStringVariableForConsole);
        } else if (commandStringMainArgument.equals("register")) { // If register call facade
            executeRegisterCommandUserLogic(wordsArrayFullInputArgument);
        } else if (commandStringMainArgument.equals("login")) { // If login check argument
            executeLoginCommandUserLogic(wordsArrayFullInputArgument);
        } else { // If command not exist print unknown
            System.out.println("Unknown command typed. Type help to options");
        }
        return true;
    }

    /**
     * This function extract the register connection out of the main loop.
     * The code isolate the variables to avoid deep nesting structures making readability better.
     */
    private void executeRegisterCommandUserLogic(
            String[] wordsArrayFullInputArgument
    ) throws Exception { // This function isolate register logic for clean code structure
        if (wordsArrayFullInputArgument.length < 4) { // If argument short print error return
            System.out.println("Please provide username password email");
            return;
        }
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataFromRegisterAction =
                this.serverFacadeObjectForHttpConnection.registerUserOnServer(
                        wordsArrayFullInputArgument[1],
                        wordsArrayFullInputArgument[2],
                        wordsArrayFullInputArgument[3]
                );
        this.authorizationTokenStringFromServerResponse =
                responseDataFromRegisterAction.authToken();
        this.isUserCurrentlyLoggedInBooleanStateTracker = true;
        System.out.println("Registration success");
    }

    /**
     * This function extract the login connection out of the main loop.
     * The system need to protect the token response and change the boolean login tracker.
     */
    private void executeLoginCommandUserLogic(
            String[] wordsArrayFullInputArgument
    ) throws Exception { // This function isolate login logic for clean code structure
        if (wordsArrayFullInputArgument.length < 3) { // If argument short print error return
            System.out.println("Please provide username password");
            return;
        }
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataFromLoginAction =
                this.serverFacadeObjectForHttpConnection.loginUserOnServer(
                        wordsArrayFullInputArgument[1],
                        wordsArrayFullInputArgument[2]
                );
        this.authorizationTokenStringFromServerResponse =
                responseDataFromLoginAction.authToken();
        this.isUserCurrentlyLoggedInBooleanStateTracker = true;
        System.out.println("Login success");
    }

    private boolean processPostLoginCommandString(
            String commandStringMainArgument, String[] wordsArrayFullInputArgument
    ) throws Exception { // Process check string execute post logic decomposition
        if (commandStringMainArgument.equals("help")) { // If help print instructions
            executeHelpCommandToPrintMenuInstructions();
        } else if (commandStringMainArgument.equals("logout")) { // If logout call facade
            executeLogoutCommandFromServerSession();
        } else if (commandStringMainArgument.equals("create")) { // If create game facade
            executeCreateGameCommandToServerLogic(wordsArrayFullInputArgument);
        } else if (commandStringMainArgument.equals("list")) { // If list call facade array
            executeListGamesCommandFromServerDatabase();
        } else if (commandStringMainArgument.equals("play")) { // If play call facade draw
            executePlayGameCommandToJoinMatch(wordsArrayFullInputArgument);
        } else if (commandStringMainArgument.equals("observe")) { // If observe check draw
            executeObserveGameCommandToWatchMatch(wordsArrayFullInputArgument);
        } else { // If command not exist print unknown
            System.out.println("Unknown command typed. Type help to options");
        }
        return true;
    }

    /**
     * This function hold the string variable for the menu when user already log inside.
     */
    private void executeHelpCommandToPrintMenuInstructions() { // This function print help
        String postLoginHelpMenuStringVariableForConsole =
                "help - Display this text informing actions\n"
                        + "logout - Log out the user\n"
                        + "create <name> - Create a new game\n"
                        + "list - List all the games\n"
                        + "play <num> [WHITE|BLACK] - Join game play\n"
                        + "observe <num> - Observe a game";
        System.out.println(postLoginHelpMenuStringVariableForConsole);
    }

    /**
     * This function process the logout operation using the authorization token stored in memory.
     */
    private void executeLogoutCommandFromServerSession() throws Exception { // This function execute logout
        this.serverFacadeObjectForHttpConnection.logoutUserOnServer(
                this.authorizationTokenStringFromServerResponse
        );
        this.isUserCurrentlyLoggedInBooleanStateTracker = false;
        this.authorizationTokenStringFromServerResponse = null;
        System.out.println("Logout success");
    }

    /**
     * This function make the request to start a new empty game match in the server database.
     */
    private void executeCreateGameCommandToServerLogic(
            String[] wordsArrayFullInputArgument
    ) throws Exception { // This function try to create game in server
        if (wordsArrayFullInputArgument.length < 2) { // If arguments missing print error
            System.out.println("Please provide the game name");
            return;
        }
        ServerFacadeForHttpCalls.GameResponseDataFormat responseDataFromCreateAction =
                this.serverFacadeObjectForHttpConnection.createGameOnServer(
                        this.authorizationTokenStringFromServerResponse,
                        wordsArrayFullInputArgument[1]
                );
        System.out.println("The new game has the id: "
                + responseDataFromCreateAction.gameID());
    }

    /**
     * This function call the api to list the games and reconstruct the array object locally.
     */
    private void executeListGamesCommandFromServerDatabase() throws Exception { // This function list games
        ServerFacadeForHttpCalls.ListGamesResponseDataFormat responseDataFromListAction =
                this.serverFacadeObjectForHttpConnection.listGamesOnServer(
                        this.authorizationTokenStringFromServerResponse
                );
        this.listOfGamesStoredArrayMemory = responseDataFromListAction.games();
        int indexIntegerTrackerForListPrint = 1;
        for (ServerFacadeForHttpCalls.GameDataObjectFormat gameLoopObjectItemArray :
                this.listOfGamesStoredArrayMemory) { // Loop games print terminal iteration
            System.out.println(indexIntegerTrackerForListPrint + ". "
                    + gameLoopObjectItemArray.gameName() + " (White: "
                    + gameLoopObjectItemArray.whiteUsername() + ", Black: "
                    + gameLoopObjectItemArray.blackUsername() + ")");
            indexIntegerTrackerForListPrint++;
        }
    }

    /**
     * This function try to play the match joining the black or white position slots.
     * The string parsing is handle by a try catch block to avoid java crashes.
     */
    private void executePlayGameCommandToJoinMatch(
            String[] wordsArrayFullInputArgument
    ) throws Exception { // This function try to join the game
        if (this.listOfGamesStoredArrayMemory == null) { // If list is null user need to list first
            System.out.println("Please run list command first to update array");
            return;
        }
        if (wordsArrayFullInputArgument.length < 3) { // If length is small argument is missing
            System.out.println("Please provide game number and color parameter");
            return;
        }
        try { // Try catch the parse int so string do not crash program execution
            int gameIndexIntegerTargetValue =
                    Integer.parseInt(wordsArrayFullInputArgument[1]) - 1;
            if (gameIndexIntegerTargetValue < 0
                    || gameIndexIntegerTargetValue >= this.listOfGamesStoredArrayMemory.length) { // Check bounds
                System.out.println("Invalid game number provide out of bounds");
                return;
            }
            int actualGameIdIntegerNumberValue =
                    this.listOfGamesStoredArrayMemory[gameIndexIntegerTargetValue].gameID();
            String playerColorStringChoiceValue =
                    wordsArrayFullInputArgument[2].toUpperCase();
            this.serverFacadeObjectForHttpConnection.joinGameOnServer(
                    this.authorizationTokenStringFromServerResponse,
                    playerColorStringChoiceValue, actualGameIdIntegerNumberValue
            );
            System.out.println("Join game success");
            ChessBoardDrawingUtility boardDrawingUtilityObjectInstanceMake =
                    new ChessBoardDrawingUtility();
            if (playerColorStringChoiceValue.equals("BLACK")) { // If color is black draw black
                boardDrawingUtilityObjectInstanceMake.printBoardForBlackPerspectiveMode();
            } else { // If color is white draw white board
                boardDrawingUtilityObjectInstanceMake.printBoardForWhitePerspectiveMode();
            }
        } catch (NumberFormatException numberExceptionObjectCaught) { // Catch string input
            System.out.println("Invalid game number provide not a numeric string");
        }
    }

    /**
     * This function process the observe game execution.
     * It render the chess board graphics from the default white orientation layout.
     */
    private void executeObserveGameCommandToWatchMatch(
            String[] wordsArrayFullInputArgument
    ) { // This function try to observe the game terminal
        if (this.listOfGamesStoredArrayMemory == null) { // If list is null user need to list
            System.out.println("Please run list command first to update array");
            return;
        }
        if (wordsArrayFullInputArgument.length < 2) { // If length is small argument missing
            System.out.println("Please provide game number numeric parameter");
            return;
        }
        try { // Try catch the parse int so string do not crash program memory
            int gameIndexIntegerTargetObserveValue =
                    Integer.parseInt(wordsArrayFullInputArgument[1]) - 1;
            if (gameIndexIntegerTargetObserveValue < 0
                    || gameIndexIntegerTargetObserveValue >= this.listOfGamesStoredArrayMemory.length) { // Check bounds
                System.out.println("Invalid game number provide out of bounds");
                return;
            }
            System.out.println("Observe game is starting");
            ChessBoardDrawingUtility boardDrawingUtilityObjectInstanceForObserveAction =
                    new ChessBoardDrawingUtility();
            boardDrawingUtilityObjectInstanceForObserveAction.printBoardForWhitePerspectiveMode();
        } catch (NumberFormatException numberExceptionObjectCaught) { // Catch string input
            System.out.println("Invalid game number provide not a numeric string");
        }
    }
}