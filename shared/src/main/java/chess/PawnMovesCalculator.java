package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> pawnMoves = new ArrayList<>();

        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        // Determining direction based on team color
        int walkDirection;
        int startingRow;
        int promotionZoneRow;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            walkDirection = 1; // White goes up
            startingRow = 2;
            promotionZoneRow = 8;
        } else {
            walkDirection = -1; // Black goes down
            startingRow = 7;
            promotionZoneRow = 1;
        }

        // Logic 1: Walking Forward 1 Step
        int nextRow = currentRow + walkDirection;
        if (nextRow >= 1 && nextRow <= 8) {
            ChessPosition oneStepPos = new ChessPosition(nextRow, currentCol);
            // Only walk forward if nobody is blocking
            if (board.getPiece(oneStepPos) == null) {
                boolean isPromoting = (nextRow == promotionZoneRow);
                registerPawnMove(myPosition, oneStepPos, isPromoting, pawnMoves);

                // Logic 2: Walking Forward 2 Steps (only from start)
                if (currentRow == startingRow) {
                    int doubleStepRow = currentRow + (walkDirection * 2);
                    ChessPosition doubleStepPos = new ChessPosition(doubleStepRow, currentCol);
                    // Both steps must be clear
                    if (board.getPiece(doubleStepPos) == null) {
                        pawnMoves.add(new ChessMove(myPosition, doubleStepPos, null));
                    }
                }
            }
        }

        // Logic 3: Capturing Enemies (Diagonals)
        int[] captureOffsets = {-1, 1}; // Left and Right columns
        for (int offset : captureOffsets) {
            int attackCol = currentCol + offset;
            if (attackCol >= 1 && attackCol <= 8 && nextRow >= 1 && nextRow <= 8) {
                ChessPosition attackPos = new ChessPosition(nextRow, attackCol);
                ChessPiece enemy = board.getPiece(attackPos);

                // Verify if there is a target and if it is an enemy
                if (enemy != null && enemy.getTeamColor() != piece.getTeamColor()) {
                    boolean isPromoting = (nextRow == promotionZoneRow);
                    registerPawnMove(myPosition, attackPos, isPromoting, pawnMoves);
                }
            }
        }

        return pawnMoves;
    }

    // Helper method to handle the promotion verbosity
    private void registerPawnMove(ChessPosition start, ChessPosition end,
                                  boolean promote, Collection<ChessMove> moves) {
        if (promote) {
            // If promoting, become any of the following pieces
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }
}