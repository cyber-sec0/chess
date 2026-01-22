package chess;

import java.util.Collection;
import java.util.Objects;

public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
        return pieceColor;
    }

    public PieceType getPieceType() {
        return type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalculator calculator;

        switch (type) {
            case KING:
                calculator = new KingMovesCalculator();
                break;
            case ROOK:
                calculator = new RookMovesCalculator();
                break;
            case BISHOP:
                calculator = new BishopMovesCalculator();
                break;
            case KNIGHT:
                calculator = new KnightMovesCalculator();
                break;
            case QUEEN:
                calculator = new QueenMovesCalculator();
                break;
            case PAWN:
                calculator = new PawnMovesCalculator();
                break;
            default:
                throw new RuntimeException("Unknown piece type");
        }

        return calculator.calculateMoves(board, myPosition, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + pieceColor +
                ", type=" + type +
                '}';
    }
}