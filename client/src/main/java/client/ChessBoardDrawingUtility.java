package client;

import ui.EscapeSequences;
import chess.ChessPosition;

public class ChessBoardDrawingUtility { // This class is make to draw chess board terminal with color code

    public void printBoardForWhitePerspectiveMode(
            java.util.Collection<ChessPosition> highlightsParamSet, ChessPosition startPosParamPoint
    ) { // This function print white perspective visually
        drawTheBoardSquaresAndPiecesLoopHelperFunction(
                8, 1, -1, 1, 8, 1, highlightsParamSet, startPosParamPoint
        ); // Call helper logic for white view parameters
    }

    public void printBoardForBlackPerspectiveMode(
            java.util.Collection<ChessPosition> highlightsParamSet, ChessPosition startPosParamPoint
    ) { // This function print black perspective visually
        drawTheBoardSquaresAndPiecesLoopHelperFunction(
                1, 8, 1, 8, 1, -1, highlightsParamSet, startPosParamPoint
        ); // Call helper logic for black view parameters
    }

    private void drawTheBoardSquaresAndPiecesLoopHelperFunction(
            int startRowIntegerIndexRange, int endRowIntegerIndexRange,
            int rowDirectionIntegerStepValue, int startColumnIntegerIndexRange,
            int endColumnIntegerIndexRange, int colDirectionIntegerStepValue,
            java.util.Collection<ChessPosition> highlightsParamSet, ChessPosition startPosParamPoint
    ) { // This function is make to loop the rows and columns to print colors terminal gracefully
        printTheLettersHeaderRowHelperFunction(
                startColumnIntegerIndexRange, endColumnIntegerIndexRange, colDirectionIntegerStepValue
        ); // Call header print function
        int currentRowIntegerTrackerLoopingState = startRowIntegerIndexRange; // Init row tracker state
        while (true) { // The while loop will iterate the rows until it reach bounds safely
            if (currentRowIntegerTrackerLoopingState == endRowIntegerIndexRange + rowDirectionIntegerStepValue) { // Check row bounds
                break; // Break loop execution out
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                    + EscapeSequences.SET_TEXT_COLOR_BLACK); // Set border color string formatting
            System.out.print(" " + currentRowIntegerTrackerLoopingState + " "); // Print row number explicitly
            int currentColumnIntegerTrackerLoopingState = startColumnIntegerIndexRange; // Init column state tracking
            while (true) { // The inner loop will iterate the columns until reach bounds cleanly
                if (currentColumnIntegerTrackerLoopingState == endColumnIntegerIndexRange + colDirectionIntegerStepValue) { // Check col bounds
                    break; // Break inner loop safely
                }
                ChessPosition currentGridCoordinatePointPos =
                        new ChessPosition(currentRowIntegerTrackerLoopingState, currentColumnIntegerTrackerLoopingState); // Make pos
                boolean isSquareHighlightedInCollectionFlag = false; // Init highlight false
                if (highlightsParamSet != null) { // If collection is not empty basically
                    if (highlightsParamSet.contains(currentGridCoordinatePointPos)) { // If set contains pos mapping
                        isSquareHighlightedInCollectionFlag = true; // Flag is true completely
                    }
                }
                boolean isSquareStartingPointInCollectionFlag = false; // Init starting false
                if (startPosParamPoint != null) { // If start point not empty
                    if (startPosParamPoint.equals(currentGridCoordinatePointPos)) { // If start point matching
                        isSquareStartingPointInCollectionFlag = true; // Flag is true reliably
                    }
                }
                boolean isLightSquareBooleanFlagCheckRule =
                        (currentRowIntegerTrackerLoopingState + currentColumnIntegerTrackerLoopingState) % 2 != 0; // Calc color flag
                if (isSquareStartingPointInCollectionFlag) { // If starting square logic mapping
                    System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW); // Print yellow
                } else if (isSquareHighlightedInCollectionFlag) { // If highlight logic condition
                    if (isLightSquareBooleanFlagCheckRule) { // If highlight on light square specifically
                        System.out.print(EscapeSequences.SET_BG_COLOR_GREEN); // Print green
                    } else { // If highlight on dark square specifically
                        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN); // Print dark green
                    }
                } else { // If normal square logic rendering block
                    if (isLightSquareBooleanFlagCheckRule) { // If sum is odd then light square color
                        System.out.print(EscapeSequences.SET_BG_COLOR_WHITE); // Set light color background
                    } else { // If sum is even then dark square color code explicitly
                        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK); // Set dark color background
                    }
                }
                System.out.print(getInitialPieceStringForPositionCoordinate(
                        currentRowIntegerTrackerLoopingState, currentColumnIntegerTrackerLoopingState
                )); // Print piece code correctly
                currentColumnIntegerTrackerLoopingState += colDirectionIntegerStepValue; // Increment column cleanly
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                    + EscapeSequences.SET_TEXT_COLOR_BLACK); // Set border color string trailing
            System.out.print(" " + currentRowIntegerTrackerLoopingState + " "); // Print row number Trailing cleanly
            System.out.println(EscapeSequences.RESET_BG_COLOR
                    + EscapeSequences.RESET_TEXT_COLOR); // Reset color string explicitly newline
            currentRowIntegerTrackerLoopingState += rowDirectionIntegerStepValue; // Increment row cleanly
        }
        printTheLettersHeaderRowHelperFunction(
                startColumnIntegerIndexRange, endColumnIntegerIndexRange, colDirectionIntegerStepValue
        ); // Call footer print gracefully
    }

    // Keep the other helper methods (`printTheLettersHeaderRowHelperFunction`, `getInitialPieceStringForPositionCoordinate`) exactly as they were in Phase 5...
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