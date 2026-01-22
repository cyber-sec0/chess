package chess;

import java.util.Arrays;

public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    public void resetBoard() {
        squares = new ChessPiece[8][8];
        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;

        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(white, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, i), new ChessPiece(black, ChessPiece.PieceType.PAWN));
        }

        ChessPiece.PieceType[] backRow = {ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK};
        for (int i = 0; i < 8; i++) {
            addPiece(new ChessPosition(1, i + 1), new ChessPiece(white, backRow[i]));
            addPiece(new ChessPosition(8, i + 1), new ChessPiece(black, backRow[i]));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Arrays.deepEquals(squares, ((ChessBoard) o).squares);
    }

    @Override
    public int hashCode() {return Arrays.deepHashCode(squares);}
}