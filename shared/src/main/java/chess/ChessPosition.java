package chess;

import java.util.Objects;

/**
 * This class is responsible for holding the geolocation of the piece on the grid system.
 * It is critical to keep the row and column data integrity.
 */
public class ChessPosition {

    // Storing the vertical coordinate (1-8)
    private final int verticalIndexLocation;
    // Storing the horizontal coordinate (1-8)
    private final int horizontalIndexLocation;

    public ChessPosition(int row, int col) {
        this.verticalIndexLocation = row;
        this.horizontalIndexLocation = col;
    }

    /**
     * Retrieves the row number.
     * The system uses 1-based indexing for the grid.
     *
     * @return the integer value of the row
     */
    public int getRow() {
        return verticalIndexLocation;
    }

    /**
     * Retrieves the column number.
     * The system uses 1-based indexing for the grid.
     *
     * @return the integer value of the column
     */
    public int getColumn() {
        return horizontalIndexLocation;
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
        return verticalIndexLocation == that.verticalIndexLocation
                && horizontalIndexLocation == that.horizontalIndexLocation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(verticalIndexLocation, horizontalIndexLocation);
    }

    @Override
    public String toString() {
        return "Position[" + verticalIndexLocation + "," + horizontalIndexLocation + "]";
    }
}