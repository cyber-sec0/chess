package chess;

import java.util.Collection;

public class BishopMovesCalculator extends SlidingMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}; // Diagonals
        return calculateSlidingMoves(board, myPosition, piece, directions);
    }
}