package chess;

import java.util.Objects;

public class ChessPosition {
    // I am using these names to be very clear about what variable is what
    private final int row_location_on_board;
    private final int col_location_on_board;

    public ChessPosition(int row, int col) {
        this.row_location_on_board = row;
        this.col_location_on_board = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row_location_on_board;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col_location_on_board;
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
        return row_location_on_board == that.row_location_on_board
                && col_location_on_board == that.col_location_on_board;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row_location_on_board, col_location_on_board);
    }
}