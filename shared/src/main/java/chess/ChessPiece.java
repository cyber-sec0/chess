package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single unit on the board.
 * It holds the team affiliation and the classification of the piece.
 */
public class ChessPiece {

    private final ChessGame.TeamColor myTeamColor;
    private final PieceType myPieceType;
    // Tracking if this unit has been deployed/moved to determine castling eligibility
    private boolean hasExecutedMove = false;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.squadIdentifier = pieceColor;
        this.unitClassification = type;
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return squadIdentifier;
    }

    public PieceType getPieceType() {
        return unitClassification;
    }

    // Checking the logs to see if this piece has moved before
    public boolean hasMoved() {
        return hasExecutedMove;
    }

    // Marking the piece as deployed so we don't accidentally castle with it later
    public void markAsMoved() {
        this.hasExecutedMove = true;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // Using an interface here to allow polymorphism for the movement logic
        PieceMovesCalculator movementCalculator;

    /**
     * Marks the unit as having executed a maneuver.
     * Once marked, this cannot be undone (logs are immutable).
     */
    public void markAsMoved() {
        this.hasExecutedMove = true;
    }

    /**
     * This method delegates the movement logic to a specialized calculator.
     * It uses polymorphism to determine the correct algorithm for the piece type.
     *
     * @param board The current state of the game grid
     * @param myPosition The current coordinates of this unit
     * @return A collection of valid moves authorized for this unit
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalculator securityCalculator;

        // Switching the logic based on the unit classification
        switch (unitClassification) {
            case KING:
                securityCalculator = new KingMovesCalculator();
                break;
            case ROOK:
                securityCalculator = new RookMovesCalculator();
                break;
            case BISHOP:
                securityCalculator = new BishopMovesCalculator();
                break;
            case KNIGHT:
                securityCalculator = new KnightMovesCalculator();
                break;
            case QUEEN:
                securityCalculator = new QueenMovesCalculator();
                break;
            case PAWN:
                securityCalculator = new PawnMovesCalculator();
                break;
            default:
                throw new RuntimeException("Error: Unauthorized unit type detected in system.");
        }

        return securityCalculator.calculateMoves(board, myPosition, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        // We verify the movement history flag too, just to be safe
        return myTeamColor == that.myTeamColor 
                && myPieceType == that.myPieceType 
                && hasExecutedMove == that.hasExecutedMove;
    }

    @Override
    public int hashCode() {
        return Objects.hash(myTeamColor, myPieceType, hasExecutedMove);
    }

    @Override
    public String toString() {
        return "Unit[" + squadIdentifier + ":" + unitClassification + "]";
    }
}