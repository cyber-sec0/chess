package chess;

import java.util.Collection;

/**
 * Calculates the movement vectors for the Queen unit.
 * The Queen possesses the highest clearance level, combining Rook and Bishop vectors.
 */
public class QueenMovesCalculator extends SlidingMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // Combining all 8 directional vectors (Orthogonal + Diagonal)
        int[][] omniDirectionalVectors = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        // Executing full spectrum scan
        return scanDirectionalPath(board, myPosition, piece, omniDirectionalVectors);
    }
}