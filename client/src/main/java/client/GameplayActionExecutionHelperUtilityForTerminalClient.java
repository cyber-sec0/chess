package client;

/**
 * This class is make to help execute the game action for the terminal client successfully.
 * The logic parse the position and send the move to the socket payload destination explicitly.
 * The implementation is make very carefully to not crash the terminal memory state allocation.
 * The graphics and the move command are processing isolated here for code quality improvement.
 */
public class GameplayActionExecutionHelperUtilityForTerminalClient {

    /**
     * This function extract string positions and send to facade explicitly to move piece.
     * The string parameter is split and parse to coordinate properly without crash memory.
     * The socket connection must be open to send the payload byte transmission over network.
     */
    public void executeMakeMoveCommandToProcessActionSafely(
            String[] wordsArrayFullInputArgumentParam,
            String authorizationTokenStringFromServerResponse,
            int currentGameNumericIdActiveSessionValueTracker,
            WebSocketFacadeForGameConnection websocketFacadeForGameplayExecutionObject
    ) throws Exception { // This function isolate the move logic gracefully without failure
        if (wordsArrayFullInputArgumentParam.length < 3) { // If missing params print error explicit
            System.out.println("Please provide move start and end coordinate parameter"); // Print error safely correctly
            return; // Early return stop cleanly safely
        }
        chess.ChessPosition startPosCoordinatePointParsedVariable =
                parsePositionStringFromInputTextSafely(wordsArrayFullInputArgumentParam[1]); // Parse start explicitly safely
        chess.ChessPosition endPosCoordinatePointParsedVariable =
                parsePositionStringFromInputTextSafely(wordsArrayFullInputArgumentParam[2]); // Parse end explicitly safely

        if (startPosCoordinatePointParsedVariable == null) { // If start fail validation format properly
            System.out.println("Invalid position format try notation explicitly"); // Print error safely correctly
            return; // Early return stop logic completely safely
        }
        if (endPosCoordinatePointParsedVariable == null) { // If end fail validation format properly
            System.out.println("Invalid position format try notation explicitly"); // Print error safely correctly
            return; // Early return stop logic completely safely
        }

        chess.ChessMove newMoveObjectPayloadActionToProcess = new chess.ChessMove(
                startPosCoordinatePointParsedVariable, endPosCoordinatePointParsedVariable, null
        ); // Set move cleanly logic reliably
        websocket.commands.MakeMoveCommand makeMoveCommandObjectInstanceMessage =
                new websocket.commands.MakeMoveCommand(
                        authorizationTokenStringFromServerResponse,
                        currentGameNumericIdActiveSessionValueTracker,
                        newMoveObjectPayloadActionToProcess
                ); // Init move structure explicitly safely
        com.google.gson.Gson parserGsonObjectInstanceLocal = new com.google.gson.Gson(); // Init parser object locally
        String stringPayloadConvertedDataToSendSocket =
                parserGsonObjectInstanceLocal.toJson(makeMoveCommandObjectInstanceMessage); // Convert to json correctly safely
        websocketFacadeForGameplayExecutionObject.sendCommandMessageToServerEndpoint(
                stringPayloadConvertedDataToSendSocket
        ); // Send socket explicitly reliably
    }

    /**
     * This function process highlight logic calculation locally graphics terminal execution.
     * The moves are check in the memory and the board is draw with colors explicitly terminal.
     * The user perspective color is use to reverse the board rendering drawing procedure.
     */
    public void executeHighlightCommandToShowLegalMovesSafely(
            String[] wordsArrayFullInputArgumentParam,
            chess.ChessGame currentGameStorageObjectMemoryInstance,
            String currentUserGameRoleColorPerspectiveStringTracker
    ) { // This function process highlight logic calculation locally graphics
        if (wordsArrayFullInputArgumentParam.length < 2) { // If argument missing flow completely
            System.out.println("Please provide position coordinate parameter explicitly"); // Print error safely correctly
            return; // Early return logic safely
        }
        chess.ChessPosition selectedPosObjCoordinatePoint = parsePositionStringFromInputTextSafely(
                wordsArrayFullInputArgumentParam[1]
        ); // Parse string coordinate properly safely
        if (selectedPosObjCoordinatePoint == null) { // If parse fail completely logic
            System.out.println("Invalid position format try text notation explicitly"); // Print error safely correctly
            return; // Early return logic completely safely
        }
        if (currentGameStorageObjectMemoryInstance == null) { // If game not loaded yet locally
            System.out.println("Game state not loaded yet please wait explicitly"); // Print error safely correctly
            return; // Early return logic completely safely
        }
        java.util.Collection<chess.ChessMove> validMovesCollectionArrayStorage =
                currentGameStorageObjectMemoryInstance.validMoves(selectedPosObjCoordinatePoint); // Get moves efficiently safely
        java.util.Collection<chess.ChessPosition> endPositionsHighlightCollectionArray =
                new java.util.HashSet<>(); // Init hash set safely correctly
        if (validMovesCollectionArrayStorage != null) { // If moves exist iterate mapping safely
            for (chess.ChessMove chessMoveObjectItemTrackerFromCollectionLocal :
                    validMovesCollectionArrayStorage) { // Loop moves array effectively safely
                endPositionsHighlightCollectionArray.add(
                        chessMoveObjectItemTrackerFromCollectionLocal.getEndPosition()
                ); // Add to set explicitly safely
            }
        }
        ChessBoardDrawingUtility boardDrawingUtilityObjectInstanceMakeRendering =
                new ChessBoardDrawingUtility(); // Create utility safely
        if (currentUserGameRoleColorPerspectiveStringTracker.equals("BLACK")) { // If color is black draw black cleanly safely
            boardDrawingUtilityObjectInstanceMakeRendering.printBoardForBlackPerspectiveMode(
                    endPositionsHighlightCollectionArray, selectedPosObjCoordinatePoint
            ); // Call black highlight cleanly explicitly safely
        } else { // If color is white or observer draw white board efficiently safely
            boardDrawingUtilityObjectInstanceMakeRendering.printBoardForWhitePerspectiveMode(
                    endPositionsHighlightCollectionArray, selectedPosObjCoordinatePoint
            ); // Call white highlight cleanly explicitly safely
        }
    }

    /**
     * This function parse coordinate safely algorithm string to the object properly memory.
     * The user type text string and this make the memory object row and column.
     * The implementation use the char arithmetic calculation explicitly correctly.
     */
    public chess.ChessPosition parsePositionStringFromInputTextSafely(
            String stringTerminalInputForPositionCoordinateToParseValue
    ) { // Parse coordinate safely algorithm
        if (stringTerminalInputForPositionCoordinateToParseValue.length() < 2) { // If small throw fail explicit safely
            return null; // Return null safely
        }
        char columnCharacterAlphaTextExtractedValue = Character.toLowerCase(
                stringTerminalInputForPositionCoordinateToParseValue.charAt(0)
        ); // Get col alpha safely
        char rowCharacterNumericTextExtractedValue =
                stringTerminalInputForPositionCoordinateToParseValue.charAt(1); // Get row num text safely
        int columnNumericIndexCalculatedCleanlyValue =
                columnCharacterAlphaTextExtractedValue - 'a' + 1; // Calc col index cleanly safely
        int rowNumericIndexCalculatedCleanlyValue =
                rowCharacterNumericTextExtractedValue - '0'; // Calc row index cleanly safely
        return new chess.ChessPosition(
                rowNumericIndexCalculatedCleanlyValue, columnNumericIndexCalculatedCleanlyValue
        ); // Return position cleanly setup safely
    }

    /**
     * This function refresh graphics explicitly memory the terminal state output properly console.
     * The board is redraw based on the perspective string color tracker explicitly.
     * The utility helper is call to make the color string codes execution rendering.
     */
    public void executeRedrawCommandToRefreshBoardGraphicsSafely(
            chess.ChessGame currentGameStorageObjectMemoryInstance,
            String currentUserGameRoleColorPerspectiveStringTracker
    ) { // This function refresh graphics explicitly
        if (currentGameStorageObjectMemoryInstance == null) { // If game not loaded fail explicitly
            System.out.println("Game state not loaded yet please wait"); // Print error safely
            return; // Early return stop flow completely
        }
        ChessBoardDrawingUtility boardDrawingUtilityObjectInstanceMakeRendering =
                new ChessBoardDrawingUtility(); // Create utility safely
        if (currentUserGameRoleColorPerspectiveStringTracker.equals("BLACK")) { // If color is black draw black cleanly
            boardDrawingUtilityObjectInstanceMakeRendering.printBoardForBlackPerspectiveMode(null, null); // Call black no highlights
        } else { // If color is white draw white board cleanly
            boardDrawingUtilityObjectInstanceMakeRendering.printBoardForWhitePerspectiveMode(null, null); // Call white no highlights
        }
    }
}