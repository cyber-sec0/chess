package chess;

import java.util.*;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};

        for (int[] dir : directions) {
            int newRow = myPosition.getRow() + dir[0];
            int newCol = myPosition.getColumn() + dir[1];
            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtPos = board.getPiece(newPos);
                if (pieceAtPos == null || pieceAtPos.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                }
            }
        }
        return moves;
    }
}