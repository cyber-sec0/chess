package chess;

import java.util.Objects;

public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public ChessPosition getStartPosition() {return startPosition;}
    public ChessPosition getEndPosition() {return endPosition;}
    public ChessPiece.PieceType getPromotionPiece() {return promotionPiece;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove that = (ChessMove) o;
        return Objects.equals(startPosition, that.startPosition) && Objects.equals(endPosition, that.endPosition) && promotionPiece == that.promotionPiece;
    }

    @Override
    public int hashCode() {return Objects.hash(startPosition, endPosition, promotionPiece);}
}