package chess;

import java.util.Objects;

/**
 * This class defines the protocol for moving a unit from one sector to another.
 * It also handles the special promotion authorization if the pawn reaches the end.
 */
public class ChessMove {

    // The starting sector of the unit
    private final ChessPosition originCoordinate;
    // The target sector for the operation
    private final ChessPosition destinationCoordinate;
    // The type of unit to promote to, if applicable
    private final ChessPiece.PieceType promotionAuth;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.originCoordinate = startPosition;
        this.destinationCoordinate = endPosition;
        this.promotionAuth = promotionPiece;
    }

    public ChessPosition getStartPosition() {
        return originCoordinate;
    }

    public ChessPosition getEndPosition() {
        return destinationCoordinate;
    }

    public ChessPiece.PieceType getPromotionPiece() {
        return promotionAuth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(originCoordinate, chessMove.originCoordinate)
                && Objects.equals(destinationCoordinate, chessMove.destinationCoordinate)
                && promotionAuth == chessMove.promotionAuth;
    }

    @Override
    public int hashCode() {
        return Objects.hash(originCoordinate, destinationCoordinate, promotionAuth);
    }

    @Override
    public String toString() {
        return "Move{" + originCoordinate + " -> " + destinationCoordinate + "}";
    }
}