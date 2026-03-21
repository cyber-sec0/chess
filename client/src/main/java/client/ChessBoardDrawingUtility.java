package client;

import ui.EscapeSequences;

public class ChessBoardDrawingUtility { // This class is make to draw chess board terminal

    public void printBoardForWhitePerspectiveMode() { // This function print white perspective
        drawTheBoardSquaresAndPiecesLoopHelperFunction(
                8, 1, -1, 1, 8, 1
        ); // Call helper logic for white view
    }

    public void printBoardForBlackPerspectiveMode() { // This function print black perspective
        drawTheBoardSquaresAndPiecesLoopHelperFunction(
                1, 8, 1, 8, 1, -1
        ); // Call helper logic for black view
    }

    private void drawTheBoardSquaresAndPiecesLoopHelperFunction(
            int startRowIntegerIndexRange, int endRowIntegerIndexRange,
            int rowDirectionIntegerStepValue, int startColumnIntegerIndexRange,
            int endColumnIntegerIndexRange, int colDirectionIntegerStepValue
    ) { // This function is make to loop the rows and columns to print colors terminal
        printTheLettersHeaderRowHelperFunction(
                startColumnIntegerIndexRange, endColumnIntegerIndexRange,
                colDirectionIntegerStepValue
        ); // Call header print function
        int currentRowIntegerTrackerLoopingState = startRowIntegerIndexRange; // Init row tracker
        while (true) { // The while loop will iterate the rows until it reach bounds
            if (currentRowIntegerTrackerLoopingState == endRowIntegerIndexRange + rowDirectionIntegerStepValue) { // Check row bounds
                break; // Break loop
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                    + EscapeSequences.SET_TEXT_COLOR_BLACK); // Set border color string
            System.out.print(" " + currentRowIntegerTrackerLoopingState + " "); // Print row number
            int currentColumnIntegerTrackerLoopingState = startColumnIntegerIndexRange; // Init column
            while (true) { // The inner loop will iterate the columns until reach bounds
                if (currentColumnIntegerTrackerLoopingState == endColumnIntegerIndexRange + colDirectionIntegerStepValue) { // Check col bounds
                    break; // Break inner loop
                }
                boolean isLightSquareBooleanFlagCheckRule =
                        (currentRowIntegerTrackerLoopingState + currentColumnIntegerTrackerLoopingState) % 2 != 0; // Calc color flag
                if (isLightSquareBooleanFlagCheckRule) { // If sum is odd then light square color
                    System.out.print(EscapeSequences.SET_BG_COLOR_WHITE); // Set light color background
                } else { // If sum is even then dark square color code
                    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK); // Set dark color background
                }
                System.out.print(getInitialPieceStringForPositionCoordinate(
                        currentRowIntegerTrackerLoopingState, currentColumnIntegerTrackerLoopingState
                )); // Print piece code
                currentColumnIntegerTrackerLoopingState += colDirectionIntegerStepValue; // Increment
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                    + EscapeSequences.SET_TEXT_COLOR_BLACK); // Set border color
            System.out.print(" " + currentRowIntegerTrackerLoopingState + " "); // Print row number
            System.out.println(EscapeSequences.RESET_BG_COLOR
                    + EscapeSequences.RESET_TEXT_COLOR); // Reset color string
            currentRowIntegerTrackerLoopingState += rowDirectionIntegerStepValue; // Increment row
        }
        printTheLettersHeaderRowHelperFunction(
                startColumnIntegerIndexRange, endColumnIntegerIndexRange,
                colDirectionIntegerStepValue
        ); // Call footer print
    }

    private void printTheLettersHeaderRowHelperFunction(
            int startColumnIntegerIndexRange, int endColumnIntegerIndexRange,
            int colDirectionIntegerStepValue
    ) { // This function is make to print the letters row top bottom
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                + EscapeSequences.SET_TEXT_COLOR_BLACK); // Set border color
        System.out.print("   "); // Print space corner
        int currentColumnIntegerTrackerLoopingState = startColumnIntegerIndexRange; // Init tracker
        while (true) { // Loop break when column out of bounds
            if (currentColumnIntegerTrackerLoopingState == endColumnIntegerIndexRange + colDirectionIntegerStepValue) { // Break if bound pass
                break; // Break loop
            }
            char letterCharToPrintForColumnValue =
                    (char) ('a' + currentColumnIntegerTrackerLoopingState - 1); // Calc letter
            System.out.print(" " + letterCharToPrintForColumnValue + " "); // Print letter
            currentColumnIntegerTrackerLoopingState += colDirectionIntegerStepValue; // Increment
        }
        System.out.println("   " + EscapeSequences.RESET_BG_COLOR
                + EscapeSequences.RESET_TEXT_COLOR); // Reset color print
    }

    private String getInitialPieceStringForPositionCoordinate(
            int rowPositionIntegerInputRaw, int colPositionIntegerInputRaw
    ) { // This function get piece string for start state board setup
        if (rowPositionIntegerInputRaw == 2) { // If row 2 return white pawn
            return EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_PAWN; // Return
        } else if (rowPositionIntegerInputRaw == 7) { // If row 7 return black pawn
            return EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_PAWN; // Return
        } else if (rowPositionIntegerInputRaw == 1) { // If row 1 return white back
            return getBackRankPieceStringByColorAndColumn(
                    colPositionIntegerInputRaw, EscapeSequences.SET_TEXT_COLOR_RED, true
            ); // Return white back rank
        } else if (rowPositionIntegerInputRaw == 8) { // If row 8 return black back
            return getBackRankPieceStringByColorAndColumn(
                    colPositionIntegerInputRaw, EscapeSequences.SET_TEXT_COLOR_BLUE, false
            ); // Return black back rank
        }
        return EscapeSequences.EMPTY; // Return empty string
    }

    private String getBackRankPieceStringByColorAndColumn(
            int colPositionIntegerInputRaw, String colorStringCodeInputValue,
            boolean isWhiteBooleanFlagInputValue
    ) { // This function return string back rank piece base on col position
        if (colPositionIntegerInputRaw == 1 || colPositionIntegerInputRaw == 8) { // If edge
            return colorStringCodeInputValue +
                    (isWhiteBooleanFlagInputValue ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK); // Return rook
        } else if (colPositionIntegerInputRaw == 2 || colPositionIntegerInputRaw == 7) { // If knight
            return colorStringCodeInputValue +
                    (isWhiteBooleanFlagInputValue ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT); // Return knight
        } else if (colPositionIntegerInputRaw == 3 || colPositionIntegerInputRaw == 6) { // If bishop
            return colorStringCodeInputValue +
                    (isWhiteBooleanFlagInputValue ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP); // Return bishop
        } else if (colPositionIntegerInputRaw == 4) { // If queen
            return colorStringCodeInputValue +
                    (isWhiteBooleanFlagInputValue ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN); // Return queen
        } else if (colPositionIntegerInputRaw == 5) { // If king
            return colorStringCodeInputValue +
                    (isWhiteBooleanFlagInputValue ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING); // Return king
        }
        return EscapeSequences.EMPTY; // Return empty
    }
}