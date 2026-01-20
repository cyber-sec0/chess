package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}; // Up, Down, Right, Left

        for (int[] dir : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += dir[0];
                col += dir[1];

                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break; // Off board
                }

                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece pieceAtPos = board.getPiece(newPos);

                if (pieceAtPos == null) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                } else {
                    if (pieceAtPos.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPos, null)); // Capture
                    }
                    break; // Blocked by a piece (friend or foe)
                }
            }
        }
        return moves;
    }
}