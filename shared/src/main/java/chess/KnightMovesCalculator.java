package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> legalMoves = new ArrayList<>();

        // This is the L shape moves logic.
        int[][] jumpPatterns = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] jump : jumpPatterns) {
            int targetRow = myPosition.getRow() + jump[0];
            int targetCol = myPosition.getColumn() + jump[1];

            // Verifying if the jump lands inside the map (board)
            if (targetRow >= 1 && targetRow <= 8 && targetCol >= 1 && targetCol <= 8) {
                ChessPosition landingSpot = new ChessPosition(targetRow, targetCol);
                ChessPiece existingPiece = board.getPiece(landingSpot);

                // Knights can only land if the spot is empty or has an enemy
                if (existingPiece == null || existingPiece.getTeamColor() != piece.getTeamColor()) {
                    legalMoves.add(new ChessMove(myPosition, landingSpot, null));
                }
            }
        }
        return legalMoves;
    }
}