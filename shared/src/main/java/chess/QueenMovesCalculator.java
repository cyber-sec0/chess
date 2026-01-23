package chess;

import java.util.Collection;

public class QueenMovesCalculator extends SlidingMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // The Queen combines Bishop and Rook movements
        int[][] allWays = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        return runSlidingLogic(board, myPosition, piece, allWays);
    }
}