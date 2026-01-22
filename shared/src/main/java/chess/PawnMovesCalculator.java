package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;

        // Forward 1
        int nextRow = row + direction;
        if (nextRow >= 1 && nextRow <= 8) {
            ChessPosition nextPos = new ChessPosition(nextRow, col);
            if (board.getPiece(nextPos) == null) {
                addPawnMove(myPosition, nextPos, nextRow == promotionRow, moves);

                // Forward 2 (only if starting and path is clear)
                if (row == startRow) {
                    int twoStepRow = row + (direction * 2);
                    ChessPosition twoStepPos = new ChessPosition(twoStepRow, col);
                    if (board.getPiece(twoStepPos) == null) {
                        moves.add(new ChessMove(myPosition, twoStepPos, null));
                    }
                }
            }
        }

        // Captures
        int[] captureCols = {col - 1, col + 1};
        for (int captureCol : captureCols) {
            if (captureCol >= 1 && captureCol <= 8) {
                ChessPosition capturePos = new ChessPosition(nextRow, captureCol);
                ChessPiece target = board.getPiece(capturePos);
                if (target != null && target.getTeamColor() != piece.getTeamColor()) {
                    addPawnMove(myPosition, capturePos, nextRow == promotionRow, moves);
                }
            }
        }

        return moves;
    }

    private void addPawnMove(ChessPosition start, ChessPosition end, boolean promotion, Collection<ChessMove> moves) {
        if (promotion) {
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }
}