package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Calculates the unique L-shaped movement for the Knight.
 * Unlike other units, the Knight can bypass obstacles (jump).
 */
public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> tacticalJumps = new ArrayList<>();

        // The specific L-shape coordinate offsets
        int[][] jumpCoordinates = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] jumpOffset : jumpCoordinates) {
            int landingRow = myPosition.getRow() + jumpOffset[0];
            int landingCol = myPosition.getColumn() + jumpOffset[1];

            // Verify if the landing zone is within the operational area (board)
            if (landingRow >= 1 && landingRow <= 8 && landingCol >= 1 && landingCol <= 8) {
                ChessPosition landingZone = new ChessPosition(landingRow, landingCol);
                ChessPiece pieceInZone = board.getPiece(landingZone);

                // Knights only care about the destination status, not the path
                // If destination is null (empty) or hostile, the move is valid
                if (pieceInZone == null || pieceInZone.getTeamColor() != piece.getTeamColor()) {
                    tacticalJumps.add(new ChessMove(myPosition, landingZone, null));
                }
            }
        }
        return tacticalJumps;
    }
}