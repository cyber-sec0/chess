package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] knightMoves = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}}; //All 8 possible knight moves

        for (int[] move : knightMoves) {
            int newRow = myPosition.getRow() + move[0];
            int newCol = myPosition.getColumn() + move[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) { //Check bounds
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtPos = board.getPiece(newPos);

                if (pieceAtPos == null || pieceAtPos.getTeamColor() != piece.getTeamColor()) { //Empty or enemy
                    moves.add(new ChessMove(myPosition, newPos, null));
                }
            }
        }
        return moves;
    }
}