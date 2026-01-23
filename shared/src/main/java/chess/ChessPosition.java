package chess;

import java.util.Objects;

public class ChessPosition {
    // I am using these names to be very clear about what variable is what
    private final int rowLocationOnBoard;
    private final int colLocationOnBoard;

    public ChessPosition(int row, int col) {
        this.rowLocationOnBoard = row;
        this.colLocationOnBoard = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return rowLocationOnBoard;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return colLocationOnBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return rowLocationOnBoard == that.rowLocationOnBoard 
                && colLocationOnBoard == that.colLocationOnBoard;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowLocationOnBoard, colLocationOnBoard);
    }
}