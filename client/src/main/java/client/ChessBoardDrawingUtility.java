package client;

import ui.EscapeSequences;

public class ChessBoardDrawingUtility { // This class is make for the purpose of draw the chess board in the terminal screen using colors and pieces codes

    public void printBoardForWhitePerspectiveMode() { // This function is make to print the board using the white perspective where a1 is bottom left corner code
        drawTheBoardSquaresAndPiecesLoopHelperFunction(8, 1, -1, 1, 8, 1); // Call helper logic
    }

    public void printBoardForBlackPerspectiveMode() { // This function is make to print the board using the black perspective where a1 is upper right corner code
        drawTheBoardSquaresAndPiecesLoopHelperFunction(1, 8, 1, 8, 1, -1); // Call helper logic
    }

    private void drawTheBoardSquaresAndPiecesLoopHelperFunction(int startRowIntegerIndexRange, int endRowIntegerIndexRange, int rowDirectionIntegerStepValue, int startColumnIntegerIndexRange, int endColumnIntegerIndexRange, int colDirectionIntegerStepValue) { // This function is make to loop the rows and columns and print the correct colors and text in terminal
        printTheLettersHeaderRowHelperFunction(startColumnIntegerIndexRange, endColumnIntegerIndexRange, colDirectionIntegerStepValue); // Call header print
        int currentRowIntegerTrackerLoopingState = startRowIntegerIndexRange; // Initialize row tracker
        while (true) { // The while loop will iterate the rows until it reach the end bound values
            if (currentRowIntegerTrackerLoopingState == endRowIntegerIndexRange + rowDirectionIntegerStepValue) { // Check if the row is finish then break the loop to stop it
                break; // Break loop
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK); // Set border color
            System.out.print(" " + currentRowIntegerTrackerLoopingState + " "); // Print row number text
            int currentColumnIntegerTrackerLoopingState = startColumnIntegerIndexRange; // Initialize column tracker
            while (true) { // The inner loop will iterate the columns until it reach the end bound values
                if (currentColumnIntegerTrackerLoopingState == endColumnIntegerIndexRange + colDirectionIntegerStepValue) { // Check if the column is finish then break the inner loop to stop it
                    break; // Break loop
                }
                boolean isLightSquareBooleanFlagCheckRule = (currentRowIntegerTrackerLoopingState + currentColumnIntegerTrackerLoopingState) % 2 != 0; // Calculate color logic
                if (isLightSquareBooleanFlagCheckRule) { // If the sum is odd then it is a light square color background code
                    System.out.print(EscapeSequences.SET_BG_COLOR_WHITE); // Set light color background
                } else { // If the sum is even then it is a dark square color background code
                    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK); // Set dark color background
                }
                System.out.print(getInitialPieceStringForPositionCoordinate(currentRowIntegerTrackerLoopingState, currentColumnIntegerTrackerLoopingState)); // Print piece code
                currentColumnIntegerTrackerLoopingState += colDirectionIntegerStepValue; // Increment column tracker
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK); // Set border color
            System.out.print(" " + currentRowIntegerTrackerLoopingState + " "); // Print row number text
            System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR); // Reset color string
            currentRowIntegerTrackerLoopingState += rowDirectionIntegerStepValue; // Increment row tracker
        }
        printTheLettersHeaderRowHelperFunction(startColumnIntegerIndexRange, endColumnIntegerIndexRange, colDirectionIntegerStepValue); // Call footer print
    }

    private void printTheLettersHeaderRowHelperFunction(int startColumnIntegerIndexRange, int endColumnIntegerIndexRange, int colDirectionIntegerStepValue) { // This function is make to print the letters row in the top and bottom of the board UI element
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK); // Set border color
        System.out.print("   "); // Print space corner
        int currentColumnIntegerTrackerLoopingState = startColumnIntegerIndexRange; // Initialize column tracker
        while (true) { // The loop break when the column is out of the bounds iteration count
            if (currentColumnIntegerTrackerLoopingState == endColumnIntegerIndexRange + colDirectionIntegerStepValue) { // Break if the tracker pass the end column bound rule
                break; // Break loop
            }
            char letterCharToPrintForColumnValue = (char) ('a' + currentColumnIntegerTrackerLoopingState - 1); // Calculate letter char
            System.out.print(" " + letterCharToPrintForColumnValue + " "); // Print letter char
            currentColumnIntegerTrackerLoopingState += colDirectionIntegerStepValue; // Increment column tracker
        }
        System.out.println("   " + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR); // Reset color and print new line
    }

    private String getInitialPieceStringForPositionCoordinate(int rowPositionIntegerInputRaw, int colPositionIntegerInputRaw) { // This function is make to get the piece string for the start state of the game board setup UI
        if (rowPositionIntegerInputRaw == 2) { // If it is row 2 then return white pawn string code character
            return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_PAWN; // Return white pawn
        } else if (rowPositionIntegerInputRaw == 7) { // If it is row 7 then return black pawn string code character
            return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_PAWN; // Return black pawn
        } else if (rowPositionIntegerInputRaw == 1) { // If it is row 1 then return white back rank pieces characters
            return getBackRankPieceStringByColorAndColumn(colPositionIntegerInputRaw, EscapeSequences.SET_TEXT_COLOR_RED, true); // Return white back rank
        } else if (rowPositionIntegerInputRaw == 8) { // If it is row 8 then return black back rank pieces characters
            return getBackRankPieceStringByColorAndColumn(colPositionIntegerInputRaw, EscapeSequences.SET_TEXT_COLOR_BLUE, false); // Return black back rank
        }
        return EscapeSequences.EMPTY; // Return empty string
    }

    private String getBackRankPieceStringByColorAndColumn(int colPositionIntegerInputRaw, String colorStringCodeInputValue, boolean isWhiteBooleanFlagInputValue) { // This function is make to return the string of the back rank piece base on the column position check logic
        if (colPositionIntegerInputRaw == 1 || colPositionIntegerInputRaw == 8) { // If edge column then return rook piece string character
            return colorStringCodeInputValue + (isWhiteBooleanFlagInputValue ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK); // Return rook character
        } else if (colPositionIntegerInputRaw == 2 || colPositionIntegerInputRaw == 7) { // If beside edge column then return knight piece string character
            return colorStringCodeInputValue + (isWhiteBooleanFlagInputValue ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT); // Return knight character
        } else if (colPositionIntegerInputRaw == 3 || colPositionIntegerInputRaw == 6) { // If beside middle column then return bishop piece string character
            return colorStringCodeInputValue + (isWhiteBooleanFlagInputValue ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP); // Return bishop character
        } else if (colPositionIntegerInputRaw == 4) { // If queen column then return queen piece string character
            return colorStringCodeInputValue + (isWhiteBooleanFlagInputValue ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN); // Return queen character
        } else if (colPositionIntegerInputRaw == 5) { // If king column then return king piece string character
            return colorStringCodeInputValue + (isWhiteBooleanFlagInputValue ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING); // Return king character
        }
        return EscapeSequences.EMPTY; // Return empty string
    }
}