package chess;

import java.util.Collection;

public class BishopMovesCalculator extends SlidingMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // Bishops only move in diagonals
        int[][] diagonalWays = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        return runSlidingLogic(board, myPosition, piece, diagonalWays);
    }
}