package chess;

import java.util.Collection;

public class RookMovesCalculator extends SlidingMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}; // Up, Down, Right, Left
        return calculateSlidingMoves(board, myPosition, piece, directions);
    }
}