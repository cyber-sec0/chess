package client;

import java.util.Scanner;

public class TerminalReplUserInterface { // This class is the loop that keep the program running and asking for the user inputs constantly to not stop execution
    private final ServerFacadeForHttpCalls serverFacadeObjectForHttpConnection; // The facade object is store here for the network requests
    private boolean isUserCurrentlyLoggedInBooleanStateTracker; // The state tracker for the login is store here
    private String authorizationTokenStringFromServerResponse; // The token is store here
    private final Scanner scannerObjectForTerminalReadingInput; // The scanner object is store here for string reading
    private ServerFacadeForHttpCalls.GameDataObjectFormat[] listOfGamesStoredArrayMemory; // The games array is store here

    public TerminalReplUserInterface(int portNumberForServerConnectionArgument) { // The constructor is initialize the facade and the scanner to be ready for the loop function
        this.serverFacadeObjectForHttpConnection = new ServerFacadeForHttpCalls(portNumberForServerConnectionArgument); // Initialize facade object
        this.isUserCurrentlyLoggedInBooleanStateTracker = false; // Initialize state boolean
        this.authorizationTokenStringFromServerResponse = null; // Initialize token string
        this.scannerObjectForTerminalReadingInput = new Scanner(System.in); // Initialize scanner object
    }

    public void runTheProgramLoop() { // The loop start here and it will print the welcome message before asking for the input continuously
        System.out.println("Welcome to the chess game application!"); // Print welcome text
        boolean keepRunningTheLoopBooleanFlag = true; // Initialize flag tracker
        do { // The do while loop will check the login state to show the correct text and menu to the terminal
            if (this.isUserCurrentlyLoggedInBooleanStateTracker) { // If the user is log in then show the post login menu format string
                System.out.print("[LOGGED_IN] >>> "); // Print post login prompt
            } else { // If the user is not log in then show the pre login menu format string
                System.out.print("[LOGGED_OUT] >>> "); // Print pre login prompt
            }
            String userCommandStringInputRaw = this.scannerObjectForTerminalReadingInput.nextLine(); // Read input string
            String[] wordsArrayFromCommandStringSplit = userCommandStringInputRaw.split(" "); // Split the string words
            String mainCommandStringWordLower = wordsArrayFromCommandStringSplit[0].toLowerCase(); // Get the command word
            try { // The try block is to catch any exception that can happen in the network call to not crash app
                if (this.isUserCurrentlyLoggedInBooleanStateTracker) { // If the state is log in then call the post login process method
                    keepRunningTheLoopBooleanFlag = processPostLoginCommandString(mainCommandStringWordLower, wordsArrayFromCommandStringSplit); // Call post login process
                } else { // If the state is not log in then call the pre login process method
                    keepRunningTheLoopBooleanFlag = processPreLoginCommandString(mainCommandStringWordLower, wordsArrayFromCommandStringSplit); // Call pre login process
                }
            } catch (Exception exceptionObjectCaughtFromExecutionRun) { // The catch block will print the error message so the program do not crash and stop execution
                System.out.println("Error happened: " + exceptionObjectCaughtFromExecutionRun.getMessage()); // Print error message
            }
        } while (keepRunningTheLoopBooleanFlag); // Loop until the flag is false
    }

    private boolean processPreLoginCommandString(String commandStringMainArgument, String[] wordsArrayFullInputArgument) throws Exception { // The process function check the string and execute the right action for it logic
        if (commandStringMainArgument.equals("quit")) { // If the user want to quit then return false to stop the loop running
            return false; // Return false
        } else if (commandStringMainArgument.equals("help")) { // If the user want help then print the instructions text
            System.out.println("help - Display this text informing the actions"); // Print help text
            System.out.println("quit - Exit the program"); // Print help text
            System.out.println("login <username> <password> - Login the user"); // Print help text
            System.out.println("register <username> <password> <email> - Register new user"); // Print help text
        } else if (commandStringMainArgument.equals("register")) { // If the user want to register then check the arguments and call the facade properly
            if (wordsArrayFullInputArgument.length >= 4) { // Check if the arguments size is correct before calling the server register logic
                ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataFromRegisterAction = this.serverFacadeObjectForHttpConnection.registerUserOnServer(wordsArrayFullInputArgument[1], wordsArrayFullInputArgument[2], wordsArrayFullInputArgument[3]); // Call facade
                this.authorizationTokenStringFromServerResponse = responseDataFromRegisterAction.authToken(); // Store token string
                this.isUserCurrentlyLoggedInBooleanStateTracker = true; // Set state boolean
                System.out.println("Registration is success for the user"); // Print success
            } else { // Print error if the arguments are missing for register
                System.out.println("Please provide the username password and email"); // Print error
            }
        } else if (commandStringMainArgument.equals("login")) { // If the user want to login then check the arguments and call the facade properly
            if (wordsArrayFullInputArgument.length >= 3) { // Check if the arguments size is correct before calling the server login logic
                ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataFromLoginAction = this.serverFacadeObjectForHttpConnection.loginUserOnServer(wordsArrayFullInputArgument[1], wordsArrayFullInputArgument[2]); // Call facade
                this.authorizationTokenStringFromServerResponse = responseDataFromLoginAction.authToken(); // Store token string
                this.isUserCurrentlyLoggedInBooleanStateTracker = true; // Set state boolean
                System.out.println("Login is success for the user"); // Print success
            } else { // Print error if the arguments are missing for login
                System.out.println("Please provide the username and password"); // Print error
            }
        } else { // If the command is not exist then print the unknown message text
            System.out.println("Unknown command typed. Type help to see the options"); // Print unknown
        }
        return true; // Return true to keep loop
    }

    private boolean processPostLoginCommandString(String commandStringMainArgument, String[] wordsArrayFullInputArgument) throws Exception { // The process function check the string and execute the right action for the logged state logic
        if (commandStringMainArgument.equals("help")) { // If the user want help then print the instructions text lines
            System.out.println("help - Display this text informing the actions"); // Print help text
            System.out.println("logout - Log out the user"); // Print help text
            System.out.println("create <name> - Create a new game"); // Print help text
            System.out.println("list - List all the games"); // Print help text
            System.out.println("play <number> [WHITE|BLACK] - Join a game to play"); // Print help text
            System.out.println("observe <number> - Observe a game"); // Print help text
        } else if (commandStringMainArgument.equals("logout")) { // If the user want to logout then call the facade properly logic
            this.serverFacadeObjectForHttpConnection.logoutUserOnServer(this.authorizationTokenStringFromServerResponse); // Call facade logic
            this.isUserCurrentlyLoggedInBooleanStateTracker = false; // Reset state boolean
            this.authorizationTokenStringFromServerResponse = null; // Reset token string
            System.out.println("Logout is success for the user"); // Print success
        } else if (commandStringMainArgument.equals("create")) { // If the user want to create then check the arguments and call the facade properly logic
            if (wordsArrayFullInputArgument.length >= 2) { // Check if the arguments size is correct before calling the server create logic
                ServerFacadeForHttpCalls.GameResponseDataFormat responseDataFromCreateAction = this.serverFacadeObjectForHttpConnection.createGameOnServer(this.authorizationTokenStringFromServerResponse, wordsArrayFullInputArgument[1]); // Call facade logic
                System.out.println("Game create is success with id: " + responseDataFromCreateAction.gameID()); // Print success
            } else { // Print error if the arguments are missing for create
                System.out.println("Please provide the game name"); // Print error
            }
        } else if (commandStringMainArgument.equals("list")) { // If the user want to list then call the facade and print the games array loop string
            ServerFacadeForHttpCalls.ListGamesResponseDataFormat responseDataFromListAction = this.serverFacadeObjectForHttpConnection.listGamesOnServer(this.authorizationTokenStringFromServerResponse); // Call facade logic
            this.listOfGamesStoredArrayMemory = responseDataFromListAction.games(); // Store array in memory
            int indexIntegerTrackerForListPrint = 1; // Initialize index print
            for (ServerFacadeForHttpCalls.GameDataObjectFormat gameLoopObjectItemArray : this.listOfGamesStoredArrayMemory) { // Loop the games to print the information in the terminal screen view
                System.out.println(indexIntegerTrackerForListPrint + ". " + gameLoopObjectItemArray.gameName() + " (White: " + gameLoopObjectItemArray.whiteUsername() + ", Black: " + gameLoopObjectItemArray.blackUsername() + ")"); // Print game info
                indexIntegerTrackerForListPrint++; // Increment index print
            }
        } else if (commandStringMainArgument.equals("play")) { // If the user want to play then check the arguments and call the facade properly and draw board logic
            if (wordsArrayFullInputArgument.length >= 3 && this.listOfGamesStoredArrayMemory != null) { // Check if the arguments size is correct and array is not null before calling the server logic
                int gameIndexIntegerTargetValue = Integer.parseInt(wordsArrayFullInputArgument[1]) - 1; // Parse integer for index
                if (gameIndexIntegerTargetValue >= 0 && gameIndexIntegerTargetValue < this.listOfGamesStoredArrayMemory.length) { // Check if the index is valid inside the array bounds length
                    int actualGameIdIntegerNumberValue = this.listOfGamesStoredArrayMemory[gameIndexIntegerTargetValue].gameID(); // Get actual id value
                    String playerColorStringChoiceValue = wordsArrayFullInputArgument[2].toUpperCase(); // Get color string
                    this.serverFacadeObjectForHttpConnection.joinGameOnServer(this.authorizationTokenStringFromServerResponse, playerColorStringChoiceValue, actualGameIdIntegerNumberValue); // Call facade logic
                    System.out.println("Join game is success for the user"); // Print success text
                    ChessBoardDrawingUtility boardDrawingUtilityObjectInstanceMake = new ChessBoardDrawingUtility(); // Create utility object
                    if (playerColorStringChoiceValue.equals("BLACK")) { // If the color is black then print the black perspective board layout screen
                        boardDrawingUtilityObjectInstanceMake.printBoardForBlackPerspectiveMode(); // Call print black
                    } else { // If the color is white then print the white perspective board layout screen
                        boardDrawingUtilityObjectInstanceMake.printBoardForWhitePerspectiveMode(); // Call print white
                    }
                } else { // Print error if the index is invalid
                    System.out.println("Invalid game number provide"); // Print error
                }
            } else { // Print error if the arguments are missing
                System.out.println("Please provide the game number and color or list games first"); // Print error
            }
        } else if (commandStringMainArgument.equals("observe")) { // If the user want to observe then check the arguments and draw board properly logic
            if (wordsArrayFullInputArgument.length >= 2 && this.listOfGamesStoredArrayMemory != null) { // Check if the arguments size is correct and array is not null before processing the command logic
                int gameIndexIntegerTargetObserveValue = Integer.parseInt(wordsArrayFullInputArgument[1]) - 1; // Parse integer index
                if (gameIndexIntegerTargetObserveValue >= 0 && gameIndexIntegerTargetObserveValue < this.listOfGamesStoredArrayMemory.length) { // Check if the index is valid inside the array bounds for observing the match
                    System.out.println("Observe game is starting"); // Print start text
                    ChessBoardDrawingUtility boardDrawingUtilityObjectInstanceForObserveAction = new ChessBoardDrawingUtility(); // Create utility object
                    boardDrawingUtilityObjectInstanceForObserveAction.printBoardForWhitePerspectiveMode(); // Call print white
                } else { // Print error if the index is invalid
                    System.out.println("Invalid game number provide"); // Print error
                }
            } else { // Print error if the arguments are missing
                System.out.println("Please provide the game number or list games first"); // Print error
            }
        } else { // If the command is not exist then print the unknown message text string
            System.out.println("Unknown command typed. Type help to see the options"); // Print unknown
        }
        return true; // Return true to keep loop
    }
}