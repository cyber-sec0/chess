package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class to handle the logic for sliding units (Rook, Bishop, Queen).
 * This prevents code duplication which is a security risk for maintenance.
 */
public abstract class SlidingMovesCalculator implements PieceMovesCalculator {

    /**
     * Scans the board in specific directions until a collision occurs.
     *
     * @param gameBoard The current grid state
     * @param myPos The starting coordinates of the unit
     * @param unit The unit itself
     * @param searchVectors The array of directional vectors to scan
     * @return A list of validated moves
     */
    protected Collection<ChessMove> scanDirectionalPath(ChessBoard gameBoard, ChessPosition myPos,
                                                        ChessPiece unit, int[][] searchVectors) {

        Collection<ChessMove> validatedMoves = new ArrayList<>();

        // Iterating through each vector in the search array
        for (int[] vector : searchVectors) {
            int currentVertical = myPos.getRow();
            int currentHorizontal = myPos.getColumn();

            // Infinite loop to simulate the sliding movement
            // Logic breaks when a boundary or obstacle is hit
            while (true) {
                currentVertical += vector[0];
                currentHorizontal += vector[1];

                // Boundary Check: Verifying if coordinates are within the 1-8 grid
                if (currentVertical < 1 || currentVertical > 8
                        || currentHorizontal < 1 || currentHorizontal > 8) {
                    break;
                }

                ChessPosition targetPos = new ChessPosition(currentVertical, currentHorizontal);
                ChessPiece obstacle = gameBoard.getPiece(targetPos);

                if (obstacle == null) {
                    // Sector is clear, authorize movement
                    validatedMoves.add(new ChessMove(myPos, targetPos, null));
                } else {
                    // Collision detected. Checking identification of the obstacle.
                    if (obstacle.getTeamColor() != unit.getTeamColor()) {
                        // Enemy detected. Capture authorized.
                        validatedMoves.add(new ChessMove(myPos, targetPos, null));
                    }
                    // Path blocked by either friend or foe. Terminate scan.
                    break;
                }
            }
        }
        return validatedMoves;
    }
}