package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> securityZones = new ArrayList<>();

        // The King unit has limited mobility (1 step radius)
        int[][] possibleSteps = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] step : possibleSteps) {
            int nextRow = myPosition.getRow() + step[0];
            int nextCol = myPosition.getColumn() + step[1];

            // Validation of the board boundaries is mandatory
            if (nextRow >= 1 && nextRow <= 8 && nextCol >= 1 && nextCol <= 8) {
                ChessPosition targetPosition = new ChessPosition(nextRow, nextCol);
                ChessPiece occupiedSpace = board.getPiece(targetPosition);

                // Checking if the space is empty or occupied by a hostile unit
                if (occupiedSpace == null || occupiedSpace.getTeamColor() != piece.getTeamColor()) {
                    securityZones.add(new ChessMove(myPosition, targetPosition, null));
                }
            }
        }

        return securityZones;
    }
}