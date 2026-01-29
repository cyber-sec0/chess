package chess;

import java.util.Collection;

/**
 * Calculates the movement vectors for the Bishop unit.
 * The Bishop is restricted to diagonal pathways.
 */
public class BishopMovesCalculator extends SlidingMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // Defining the 4 diagonal vectors
        int[][] diagonalVectors = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        // Initiating the diagonal scan protocol
        return scanDirectionalPath(board, myPosition, piece, diagonalVectors);
    }
}