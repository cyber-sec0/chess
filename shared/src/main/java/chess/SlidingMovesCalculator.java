package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class SlidingMovesCalculator implements PieceMovesCalculator {

    /*
     * This method is a helper for pieces that slide (Rook, Bishop, Queen).
     * It prevents code duplication which is bad for security and maintenance.
     * Iterate through the directions until a wall or another piece hits.
     */
    protected Collection<ChessMove> runSlidingLogic(ChessBoard theBoard, ChessPosition myPosition,
                                                    ChessPiece myPiece, int[][] searchDirections) {

        Collection<ChessMove> validMoveList = new ArrayList<>();

        for (int[] currentDirection : searchDirections) {
            int currentRowCheck = myPosition.getRow();
            int currentColCheck = myPosition.getColumn();

            // Loop forever until we explicitly break
            while (true) {
                currentRowCheck += currentDirection[0];
                currentColCheck += currentDirection[1];

                // Boundary Check: If outside the board, must stop immediately
                if (currentRowCheck < 1 || currentRowCheck > 8
                        || currentColCheck < 1 || currentColCheck > 8) {
                    break;
                }

                ChessPosition potentialSpot = new ChessPosition(currentRowCheck, currentColCheck);
                ChessPiece pieceAtDestination = theBoard.getPiece(potentialSpot);

                if (pieceAtDestination == null) {
                    // The path is clear, so add this move
                    validMoveList.add(new ChessMove(myPosition, potentialSpot, null));
                } else {
                    // There is a piece here. Check if it is friend or foe.
                    if (pieceAtDestination.getTeamColor() != myPiece.getTeamColor()) {
                        // It is an enemy, so capture it, but cannot go further
                        validMoveList.add(new ChessMove(myPosition, potentialSpot, null));
                    }
                    // If it's a friend or enemy, we are blocked either way
                    break;
                }
            }
        }
        return validMoveList;
    }
}