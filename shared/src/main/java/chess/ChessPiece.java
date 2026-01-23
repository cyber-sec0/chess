package chess;

import java.util.Collection;
import java.util.Objects;

public class ChessPiece {

    private final ChessGame.TeamColor myTeamColor;
    private final PieceType myPieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.myTeamColor = pieceColor;
        this.myPieceType = type;
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
        return myTeamColor;
    }

    public PieceType getPieceType() {
        return myPieceType;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // Using an interface here to allow polymorphism for the movement logic
        PieceMovesCalculator movementCalculator;

        switch (myPieceType) {
            case KING:
                movementCalculator = new KingMovesCalculator();
                break;
            case ROOK:
                movementCalculator = new RookMovesCalculator();
                break;
            case BISHOP:
                movementCalculator = new BishopMovesCalculator();
                break;
            case KNIGHT:
                movementCalculator = new KnightMovesCalculator();
                break;
            case QUEEN:
                movementCalculator = new QueenMovesCalculator();
                break;
            case PAWN:
                movementCalculator = new PawnMovesCalculator();
                break;
            default:
                throw new RuntimeException("Error: Unknown piece type detected!");
        }

        return movementCalculator.calculateMoves(board, myPosition, this);
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
        return myTeamColor == that.myTeamColor && myPieceType == that.myPieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(myTeamColor, myPieceType);
    }

    @Override
    public String toString() {
        return "ChessPiece{" + "color=" + myTeamColor + ", type=" + myPieceType + '}';
    }
}