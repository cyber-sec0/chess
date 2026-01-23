package chess;

import java.util.Objects;

public class ChessMove {
    // Saving the start and end to know the trajectory of the piece
    private final ChessPosition startingLocation;
    private final ChessPosition endingLocation;
    private final ChessPiece.PieceType typeOfPromotion;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startingLocation = startPosition;
        this.endingLocation = endPosition;
        this.typeOfPromotion = promotionPiece;
    }

    public ChessPosition getStartPosition() {
        return startingLocation;
    }

    public ChessPosition getEndPosition() {
        return endingLocation;
    }

    public ChessPiece.PieceType getPromotionPiece() {
        return typeOfPromotion;
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
        return Objects.equals(startingLocation, chessMove.startingLocation)
                && Objects.equals(endingLocation, chessMove.endingLocation)
                && typeOfPromotion == chessMove.typeOfPromotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingLocation, endingLocation, typeOfPromotion);
    }
}