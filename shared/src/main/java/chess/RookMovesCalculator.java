package chess;

import java.util.Collection;

/**
 * Calculates the movement vectors for the Rook unit.
 * The Rook operates on orthogonal axes (vertical and horizontal).
 */
public class RookMovesCalculator extends SlidingMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // Defining the 4 orthogonal vectors: Up, Down, Left, Right
        int[][] straightLineVectors = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        // Delegating the scanning operation to the parent security class
        return scanDirectionalPath(board, myPosition, piece, straightLineVectors);
    }
}