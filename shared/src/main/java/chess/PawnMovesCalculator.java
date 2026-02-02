package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Calculates the complex movement logic for Pawns.
 * Includes directional movement, initial double-step, captures, and promotion protocols.
 */
public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> infantryMoves = new ArrayList<>();

        int currentRowIndex = myPosition.getRow();
        int currentColIndex = myPosition.getColumn();

        // Determining forward direction vector based on team allegiance
        int forwardVector;
        int startingRank;
        int promotionThresholdRank;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            forwardVector = 1; // White advances positively
            startingRank = 2;
            promotionThresholdRank = 8;
        } else {
            forwardVector = -1; // Black advances negatively
            startingRank = 7;
            promotionThresholdRank = 1;
        }

        // Logic Block 1: Standard Advance (1 square forward)
        int nextRow = currentRowIndex + forwardVector;
        // Check bounds before proceeding
        if (nextRow >= 1 && nextRow <= 8) {
            ChessPosition singleStepPosition = new ChessPosition(nextRow, currentColIndex);

            // Pawns cannot capture forward, the path must be null
            if (board.getPiece(singleStepPosition) == null) {
                boolean promotionTriggered = (nextRow == promotionThresholdRank);
                processPawnMove(myPosition, singleStepPosition, promotionTriggered, infantryMoves);

                // Logic Block 2: Initial Charge (2 squares forward)
                // Only allowed if currently at starting rank
                if (currentRowIndex == startingRank) {
                    int doubleStepRow = currentRowIndex + (forwardVector * 2);
                    ChessPosition doubleStepPosition = new ChessPosition(doubleStepRow, currentColIndex);

                    // Both the first and second square must be clear
                    if (board.getPiece(doubleStepPosition) == null) {
                        infantryMoves.add(new ChessMove(myPosition, doubleStepPosition, null));
                    }
                }
            }
        }

        // Logic Block 3: Diagonal Engagement (Capturing)
        int[] attackOffsets = {-1, 1}; // Checking left and right diagonals
        for (int offset : attackOffsets) {
            int targetCol = currentColIndex + offset;

            // Boundary validation for column and row
            if (targetCol >= 1 && targetCol <= 8 && nextRow >= 1 && nextRow <= 8) {
                ChessPosition attackTarget = new ChessPosition(nextRow, targetCol);
                ChessPiece targetUnit = board.getPiece(attackTarget);

                // Attack is only valid if a unit exists and is hostile
                if (targetUnit != null && targetUnit.getTeamColor() != piece.getTeamColor()) {
                    boolean promotionTriggered = (nextRow == promotionThresholdRank);
                    processPawnMove(myPosition, attackTarget, promotionTriggered, infantryMoves);
                }
            }
        }

        return infantryMoves;
    }

    /**
     * Helper to handle the verbosity of adding promotion variations.
     * If promotion is active, we must generate 4 distinct moves.
     */
    private void processPawnMove(ChessPosition start, ChessPosition end,
                                 boolean isPromoting, Collection<ChessMove> moveCollection) {
        if (isPromoting) {
            // Generating all possible promotion outcomes
            moveCollection.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            moveCollection.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moveCollection.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moveCollection.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        } else {
            // Standard move with no promotion
            moveCollection.add(new ChessMove(start, end, null));
        }
    }
}