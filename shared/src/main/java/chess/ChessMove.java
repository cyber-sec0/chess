package chess;

import java.util.Objects;

public class ChessMove {
    // Saving the start and end to know the trajectory of the piece
    private final ChessPosition starting_location;
    private final ChessPosition ending_location;
    private final ChessPiece.PieceType type_of_promotion;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.starting_location = startPosition;
        this.ending_location = endPosition;
        this.type_of_promotion = promotionPiece;
    }

    public ChessPosition getStartPosition() {
        return starting_location;
    }

    public ChessPosition getEndPosition() {
        return ending_location;
    }

    public ChessPiece.PieceType getPromotionPiece() {
        return type_of_promotion;
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
        return Objects.equals(starting_location, chessMove.starting_location)
                && Objects.equals(ending_location, chessMove.ending_location)
                && type_of_promotion == chessMove.type_of_promotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(starting_location, ending_location, type_of_promotion);
    }
}