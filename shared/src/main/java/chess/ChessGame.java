package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ChessGame {

    private TeamColor currentTeamTurn;
    private ChessBoard currentGameBoard;
    // Track the last move to validate En Passant (it expires after 1 turn)
    private ChessMove lastCommittedMove;

    public ChessGame() {
        // Defaulting to White starting, as per standard protocol
        this.currentTeamTurn = TeamColor.WHITE;
        this.currentGameBoard = new ChessBoard();
        this.currentGameBoard.resetBoard();
    }

    public TeamColor getTeamTurn() {
        return currentTeamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        this.currentTeamTurn = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Validates and executes a move on the board.
     * This acts like a firewall: if the move is illegal, it rejects it with an exception.
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece movingPiece = currentGameBoard.getPiece(move.getStartPosition());

        // Security Check 1: Is there actually a piece there?
        if (movingPiece == null) {
            throw new InvalidMoveException("Error: No piece at starting coordinates.");
        }

        // Security Check 2: Is it your turn?
        if (movingPiece.getTeamColor() != currentTeamTurn) {
            throw new InvalidMoveException("Error: You cannot move enemy assets.");
        }

        // Security Check 3: Is the move strictly valid?
        Collection<ChessMove> allowedMoves = validMoves(move.getStartPosition());
        if (allowedMoves == null || !allowedMoves.contains(move)) {
            throw new InvalidMoveException("Error: Move violates physics or safety protocols.");
        }

        // EXECUTION PHASE
        // Getting here, the move is valid. Commit it to the database (board).

        // Handling En Passant Kill (removing the enemy pawn that was passed)
        if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            handleEnPassantExecution(move, movingPiece);
        }

        // Handling Castling Move (moving the Rook along with the King)
        if (movingPiece.getPieceType() == ChessPiece.PieceType.KING) {
            handleCastlingExecution(move);
        }

        // Moving the actual piece
        ChessPiece finalPiece = movingPiece;
        // If promotion happening, swap the pawn for the new upgraded unit
        if (move.getPromotionPiece() != null) {
            finalPiece = new ChessPiece(currentTeamTurn, move.getPromotionPiece());
        }

        currentGameBoard.addPiece(move.getEndPosition(), finalPiece);
        currentGameBoard.addPiece(move.getStartPosition(), null); // Delete from old spot

        // Flagging the piece as having moved (important for future Castling checks)
        movingPiece.markAsMoved();

        // Recording this move for history (needed for En Passant logic next turn)
        lastCommittedMove = move;

        // Switching control to the other team
        currentTeamTurn = (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Calculates all valid moves for a piece at the given position.
     * It filters out moves that would cause self-destruction (Check).
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece targetPiece = currentGameBoard.getPiece(startPosition);
        if (targetPiece == null) {
            return null;
        }

        Collection<ChessMove> rawMoves = targetPiece.pieceMoves(currentGameBoard, startPosition);
        Collection<ChessMove> safeMoves = new ArrayList<>();

        // Injecting Special Moves (En Passant and Castling)
        calculateEnPassantMoves(startPosition, rawMoves);
        calculateCastlingMoves(startPosition, rawMoves);

        // Simulation Loop: Try every move in a sandbox to see if King dies
        for (ChessMove candidateMove : rawMoves) {
            // Create a temporary reality (deep copy)
            ChessBoard sandboxBoard = currentGameBoard.makeDeepCopy();

            // Execute move in sandbox
            doSimulatedMove(sandboxBoard, candidateMove);

            // If the King is safe in this new reality, the move is valid
            ChessGame simulatorGame = new ChessGame();
            simulatorGame.setBoard(sandboxBoard);

            if (!simulatorGame.isInCheck(targetPiece.getTeamColor())) {
                safeMoves.add(candidateMove);
            }
        }
        return safeMoves;
    }

    public boolean isInCheck(TeamColor teamColor) {
        // Find the King's coordinates
        ChessPosition kingPos = findKingCoordinates(currentGameBoard, teamColor);
        if (kingPos == null) {
            return false; // Should not happen in a valid game
        }

        // Scan all enemy pieces to see if any of them can hit the King
        TeamColor enemyColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                if (checkPieceThreat(r, c, enemyColor, kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        // Checkmate = In Check + No Valid Moves
        if (!isInCheck(teamColor)) {
            return false;
        }
        return hasNoValidMoves(teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        // Stalemate = Not in Check + No Valid Moves
        if (isInCheck(teamColor)) {
            return false;
        }
        return hasNoValidMoves(teamColor);
    }

    public void setBoard(ChessBoard board) {
        this.currentGameBoard = board;
    }

    public ChessBoard getBoard() {
        return currentGameBoard;
    }

    // --- Private Helper Methods (The internal "private" logic) ---

    private boolean checkPieceThreat(int row, int col, TeamColor enemyColor, ChessPosition kingPos) {
        ChessPosition scannerPos = new ChessPosition(row, col);
        ChessPiece foundPiece = currentGameBoard.getPiece(scannerPos);

        if (foundPiece != null && foundPiece.getTeamColor() == enemyColor) {
            Collection<ChessMove> threats = foundPiece.pieceMoves(currentGameBoard, scannerPos);
            for (ChessMove threat : threats) {
                if (threat.getEndPosition().equals(kingPos)) {
                    return true; // Threat detected!
                }
            }
        }
        return false;
    }

    private boolean hasNoValidMoves(TeamColor teamColor) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = currentGameBoard.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (moves != null && !moves.isEmpty()) {
                        return false; // Found an escape route
                    }
                }
            }
        }
        return true; // No escape found
    }

    private ChessPosition findKingCoordinates(ChessBoard board, TeamColor color) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null
                        && piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == color) {
                    return pos;
                }
            }
        }
        return null;
    }

    private void doSimulatedMove(ChessBoard board, ChessMove move) {
        // This is a "dumb" move just for simulation (ignoring castling/en passant nuances)
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
    }

    private void calculateEnPassantMoves(ChessPosition startPos, Collection<ChessMove> moves) {
        // En Passant only applies to Pawns
        ChessPiece myPiece = currentGameBoard.getPiece(startPos);
        if (myPiece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return;
        }

        // En Passant requires a previous move to have happened
        if (lastCommittedMove == null) {
            return;
        }

        // Logic: Did the enemy pawn just double jump?
        ChessPosition enemyStart = lastCommittedMove.getStartPosition();
        ChessPosition enemyEnd = lastCommittedMove.getEndPosition();
        ChessPiece enemyPiece = currentGameBoard.getPiece(enemyEnd);

        if (enemyPiece != null && enemyPiece.getPieceType() == ChessPiece.PieceType.PAWN
                && Math.abs(enemyStart.getRow() - enemyEnd.getRow()) == 2) {

            // Check if enemy is directly to my left or right
            int colDiff = enemyEnd.getColumn() - startPos.getColumn();
            if (startPos.getRow() == enemyEnd.getRow() && Math.abs(colDiff) == 1) {
                // Determine direction of kill (based on my color)
                int direction = (myPiece.getTeamColor() == TeamColor.WHITE) ? 1 : -1;
                ChessPosition targetSpot = new ChessPosition(startPos.getRow() + direction,
                        enemyEnd.getColumn());
                moves.add(new ChessMove(startPos, targetSpot, null));
            }
        }
    }

    private void handleEnPassantExecution(ChessMove move, ChessPiece pawn) {
        // If moving diagonally to an empty square, it's En Passant
        if (move.getStartPosition().getColumn() != move.getEndPosition().getColumn()
                && currentGameBoard.getPiece(move.getEndPosition()) == null) {

            // The enemy pawn is "behind" the landing spot
            int direction = (pawn.getTeamColor() == TeamColor.WHITE) ? 1 : -1;
            ChessPosition enemyPawnPos = new ChessPosition(move.getEndPosition().getRow() - direction,
                    move.getEndPosition().getColumn());
            currentGameBoard.addPiece(enemyPawnPos, null); // Eliminate the target
        }
    }

    private void calculateCastlingMoves(ChessPosition startPos, Collection<ChessMove> moves) {
        ChessPiece king = currentGameBoard.getPiece(startPos);
        if (king.getPieceType() != ChessPiece.PieceType.KING || king.hasMoved()) {
            return;
        }

        // King cannot castle out of check
        if (isInCheck(king.getTeamColor())) {
            return;
        }

        int row = startPos.getRow();

        // Kingside Check (Right)
        checkCastlingDirection(startPos, new int[]{row, 8}, new int[]{row, 6}, new int[]{row, 7}, moves);

        // Queenside Check (Left)
        checkCastlingDirection(startPos, new int[]{row, 1}, new int[]{row, 2}, new int[]{row, 3}, moves);
    }

    private void checkCastlingDirection(ChessPosition kingPos, int[] rookCoords,
                                        int[] mid1Coords, int[] mid2Coords,
                                        Collection<ChessMove> moves) {
        ChessPosition rookPos = new ChessPosition(rookCoords[0], rookCoords[1]);
        ChessPiece rook = currentGameBoard.getPiece(rookPos);

        if (rook != null && !rook.hasMoved() && rook.getPieceType() == ChessPiece.PieceType.ROOK) {
            ChessPosition p1 = new ChessPosition(mid1Coords[0], mid1Coords[1]);
            ChessPosition p2 = new ChessPosition(mid2Coords[0], mid2Coords[1]);

            // Need empty path
            if (currentGameBoard.getPiece(p1) == null && currentGameBoard.getPiece(p2) == null) {
                // Checking if the step-over square is safe:
                ChessPosition stepOver = (kingPos.getColumn() < rookPos.getColumn())
                        ? new ChessPosition(kingPos.getRow(), kingPos.getColumn() + 1) // Kingside
                        : new ChessPosition(kingPos.getRow(), kingPos.getColumn() - 1); // Queenside

                // Simulate step over
                ChessBoard stepBoard = currentGameBoard.makeDeepCopy();
                stepBoard.addPiece(stepOver, stepBoard.getPiece(kingPos));
                stepBoard.addPiece(kingPos, null);
                ChessGame stepGame = new ChessGame();
                stepGame.setBoard(stepBoard);

                if (!stepGame.isInCheck(rook.getTeamColor())) {
                    // Path is safe
                    ChessPosition target = (kingPos.getColumn() < rookPos.getColumn())
                            ? new ChessPosition(kingPos.getRow(), kingPos.getColumn() + 2)
                            : new ChessPosition(kingPos.getRow(), kingPos.getColumn() - 2);
                    moves.add(new ChessMove(kingPos, target, null));
                }
            }
        }
    }

    private void handleCastlingExecution(ChessMove move) {
        // If King moved 2 squares, move the Rook
        if (Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 2) {
            int row = move.getStartPosition().getRow();
            int colDiff = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();

            // Determine Rook positions
            int rookStartCol = (colDiff > 0) ? 8 : 1;
            int rookEndCol = (colDiff > 0) ? 6 : 4; // Rook lands next to King

            ChessPosition rookStart = new ChessPosition(row, rookStartCol);
            ChessPosition rookEnd = new ChessPosition(row, rookEndCol);

            ChessPiece rook = currentGameBoard.getPiece(rookStart);
            currentGameBoard.addPiece(rookEnd, rook);
            currentGameBoard.addPiece(rookStart, null);
            rook.markAsMoved();
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
        ChessGame chessGame = (ChessGame) o;
        // Comparing the board state and the turn.
        // Verify the turn first because it's a cheaper operation.
        return currentTeamTurn == chessGame.currentTeamTurn
                && Objects.equals(currentGameBoard, chessGame.currentGameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeamTurn, currentGameBoard);
    }
}