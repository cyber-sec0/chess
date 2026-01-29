package chess;

import java.util.Arrays;

/**
 * This class manages the 8x8 grid matrix.
 * It is responsible for adding, retrieving, and resetting the game state.
 */
public class ChessBoard {
    // The matrix that holds the references to all active units
    private ChessPiece[][] gridMatrix = new ChessPiece[8][8];

    public void addPiece(ChessPosition position, ChessPiece piece) {
        // Must subtract 1 because computer arrays start at 0 but chess starts at 1
        boardStorageMatrix[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    public ChessPiece getPiece(ChessPosition position) {
        return gridMatrix[position.getRow() - 1][position.getColumn() - 1];
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
        // Re-initializing the memory for the board
        gridMatrix = new ChessPiece[8][8];

        ChessGame.TeamColor whiteSquad = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor blackSquad = ChessGame.TeamColor.BLACK;

        // Deploying the pawns to the second and seventh ranks
        // Loop iterates through all columns from 1 to 8
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(whiteSquad, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, i), new ChessPiece(blackSquad, ChessPiece.PieceType.PAWN));
        }

        // Defining the order of the noble units for the back ranks
        ChessPiece.PieceType[] nobleUnitOrder = {
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK
        };

        // Deploying the noble units to the first and eighth ranks
        for (int i = 0; i < 8; i++) {
            // Deploying White units at the bottom (Row 1)
            addPiece(new ChessPosition(1, i + 1), new ChessPiece(whiteSquad, nobleUnitOrder[i]));
            // Deploying Black units at the top (Row 8)
            addPiece(new ChessPosition(8, i + 1), new ChessPiece(blackSquad, nobleUnitOrder[i]));
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
        return Arrays.deepEquals(gridMatrix, that.gridMatrix);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(gridMatrix);
    }

    @Override
    public String toString() {
        return "BoardMatrix" + Arrays.deepToString(gridMatrix);
    }
}