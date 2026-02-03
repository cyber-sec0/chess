package chess;

import java.util.Collection;

public class RookMovesCalculator extends SlidingMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // Rooks move in straight lines (up, down, left, right)
        int[][] straightWays = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        return runSlidingLogic(board, myPosition, piece, straightWays);
    }
}