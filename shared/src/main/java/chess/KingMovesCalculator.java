package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> safeMoves = new ArrayList<>();

        // The king can move 1 step in any direction
        int[][] kingWalkOptions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] step : kingWalkOptions) {
            int stepRow = myPosition.getRow() + step[0];
            int stepCol = myPosition.getColumn() + step[1];

            // Strictly check bounds to ensure king safety from falling off board
            if (stepRow >= 1 && stepRow <= 8 && stepCol >= 1 && stepCol <= 8) {
                ChessPosition newPos = new ChessPosition(stepRow, stepCol);
                ChessPiece occupant = board.getPiece(newPos);

                // If space is null (empty) or has enemy, go!
                if (occupant == null || occupant.getTeamColor() != piece.getTeamColor()) {
                    safeMoves.add(new ChessMove(myPosition, newPos, null));
                }
            }
        }

        return safeMoves;
    }
}