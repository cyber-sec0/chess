package chess;

import java.util.Arrays;

public class ChessBoard {
    // This matrix stores the pieces. 8x8 is standard for chess.
    private ChessPiece[][] boardStorageMatrix = new ChessPiece[8][8];

    public void addPiece(ChessPosition position, ChessPiece piece) {
        // Must subtract 1 because computer arrays start at 0 but chess starts at 1
        boardStorageMatrix[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    public ChessPiece getPiece(ChessPosition position) {
        return boardStorageMatrix[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * This creates a sandbox clone of the board.
     * Need this to simulate moves without corrupting the main game state.
     * It's like a virtual machine for testing threats.
     */
    public ChessBoard makeDeepCopy() {
        ChessBoard clonedBoard = new ChessBoard();
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition scannerPos = new ChessPosition(r, c);
                ChessPiece foundPiece = this.getPiece(scannerPos);
                if (foundPiece != null) {
                    // Create a new piece with same properties to detach references
                    ChessPiece clonedPiece = new ChessPiece(foundPiece.getTeamColor(), 
                                                          foundPiece.getPieceType());
                    if (foundPiece.hasMoved()) {
                        clonedPiece.markAsMoved();
                    }
                    clonedBoard.addPiece(scannerPos, clonedPiece);
                }
            }
        }
        return clonedBoard;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess usually starts)
     */
    public void resetBoard() {
        // Clearing the old board to make sure no artifacts remain
        boardStorageMatrix = new ChessPiece[8][8];

        ChessGame.TeamColor whiteTeam = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor blackTeam = ChessGame.TeamColor.BLACK;

        // Putting the pawns in the front lines (rows 2 and 7)
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(whiteTeam, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, i), new ChessPiece(blackTeam, ChessPiece.PieceType.PAWN));
        }

        // Using an array to organize the powerful pieces in the back
        ChessPiece.PieceType[] noblePieces = {
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK
        };

        // Using a loop to organize the powerful pieces in the back
        for (int i = 0; i < 8; i++) {
            addPiece(new ChessPosition(1, i + 1), new ChessPiece(whiteTeam, noblePieces[i]));
            addPiece(new ChessPosition(8, i + 1), new ChessPiece(blackTeam, noblePieces[i]));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(boardStorageMatrix, that.boardStorageMatrix);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardStorageMatrix);
    }
}