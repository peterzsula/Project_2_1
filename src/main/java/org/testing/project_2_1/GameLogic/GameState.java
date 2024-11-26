package org.testing.project_2_1.GameLogic;
<<<<<<< Updated upstream
=======

import static org.testing.project_2_1.GameLogic.GameLogic.calculateCaptureValue;
>>>>>>> Stashed changes
import static org.testing.project_2_1.UI.CheckersApp.SIZE;

import java.util.ArrayList;
import java.util.List;

import org.testing.project_2_1.Moves.Capture;
import org.testing.project_2_1.Moves.InvalidMove;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.NormalMove;
import org.testing.project_2_1.Moves.Turn;
import org.testing.project_2_1.UI.CheckersApp;

public class GameState {
    protected Tile[][] board;
    protected boolean isWhiteTurn;
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;
    private ArrayList<Turn> turnsPlayed;
    private Turn currentTurn;
    private List<Turn> possibleTurns;
    private boolean isGameOver;

    public GameState(){
        isWhiteTurn = true;
        whitePieces = new ArrayList<Piece>();
        blackPieces = new ArrayList<Piece>();
        turnsPlayed = new ArrayList<Turn>();
        currentTurn = new Turn();
        possibleTurns = new ArrayList<Turn>();
        isGameOver = false;
        board = new Tile[SIZE][SIZE];

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Tile tile = new Tile(x, y);
                board[x][y] = tile;
                if (y <= ((SIZE-2)/2)-1 && tile.isBlack()) {
                    Piece piece = new Piece(PieceType.BLACK, x, y);
                    tile.setPiece(piece);
                    blackPieces.add(piece);
                } else if (y >= SIZE- ((SIZE-2)/2) && tile.isBlack()) {
                    Piece piece = new Piece(PieceType.WHITE, x, y);
                    tile.setPiece(piece);
                    whitePieces.add(piece);
                }
            }
        }
    }

    public GameState(GameState originalB) {
        this.whitePieces = new ArrayList<>();
        this.blackPieces = new ArrayList<>();
        this.turnsPlayed = new ArrayList<>();
        this.currentTurn = new Turn(originalB.getCurrentTurn());
        this.possibleTurns = new ArrayList<>(originalB.getPossibleTurns());
        this.isWhiteTurn = originalB.getIsWhiteTurn();
        this.board = new Tile[SIZE][SIZE];
        this.isGameOver = false;

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                this.board[x][y] = new Tile(x, y);
                if (originalB.board[x][y].hasPiece()) {
                    Piece originalPiece = originalB.board[x][y].getPiece();
                    Piece piece = new Piece(originalPiece.type, x, y);
                    this.board[x][y].setPiece(piece);
                    if (piece.type == PieceType.WHITE || piece.type == PieceType.WHITEKING) {
                        this.whitePieces.add(this.board[x][y].getPiece());
                    } 
                    else {
                        this.blackPieces.add(this.board[x][y].getPiece());
                    }
                }
            }
        }
        for (Turn turn : originalB.getTurnsPlayed()) {
            Turn turnCopy = new Turn(turn);
            turnsPlayed.add(turnCopy);
        }
        for (Turn turn : originalB.possibleTurns) {
            possibleTurns.add(new Turn(turn));
        }
    }

    public Tile[][] getBoard() {
        return board;
    }

    public ArrayList<Piece> getWhitePieces() {
        return whitePieces;
    }

    public ArrayList<Piece> getBlackPieces() {
        return blackPieces;
    }

    public ArrayList<Turn> getTurnsPlayed() {
        return turnsPlayed;
    }

    public void switchTurn() {
        isWhiteTurn = !isWhiteTurn;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
        // TODO: Implement game over logic
    }

    public boolean isGameOver() {
        if (whitePieces.isEmpty() || blackPieces.isEmpty()) {
            isGameOver = true;
            System.out.println("Game Over");
        }
        return isGameOver;
    }

    public void setPossibleTurns(List<Turn> possibleTurns) {
        this.possibleTurns = possibleTurns;
    }

    public List<Turn> getPossibleTurns() {
        return possibleTurns;
    }

    public List<Piece> getAllPieces(){
        List<Piece> pieces = new ArrayList<>();
        pieces.addAll(whitePieces);
        pieces.addAll(blackPieces);
        return pieces;
    }

    public boolean move(Move move) {
        currentTurn.addMove(move);
        Piece piece = board[move.getFromX()][move.getFromY()].getPiece();
        piece.movePiece(move);
        board[move.getToX()][move.getToY()].setPiece(piece);
        board[move.getFromX()][move.getFromY()].setPiece(null);
        if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = board[capture.getCaptureAtX()][capture.getCaptureAtY()].getPiece();
            board[capturedPiece.getX()][capturedPiece.getY()].setPiece(null);
            removeCapturedPieceFromLists(capturedPiece);
        }
        if (move.isTurnEnding()) {
            turnsPlayed.add(currentTurn);
            currentTurn = new Turn();
            // possibleTurns = GameLogic.getLegalTurns(this);
            switchTurn();
        }
        /* else if (!possibleTurns.getFirst().getMoves().isEmpty()) {
            for (Turn turn : possibleTurns) {
                turn.getMoves().removeFirst();
            }
        } */
        return true;
    }
    
    public boolean undoMove(Move move){
        Piece piece = board[move.getToX()][move.getToY()].getPiece();
        if (piece == null) {
            throw new IllegalStateException("Piece to undo does not exist at the target position.");
        }
        piece.undoMove(move);
        board[move.getFromX()][move.getFromY()].setPiece(piece);
        board[move.getToX()][move.getToY()].setPiece(null);
        if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = new Piece(capture.getCapturedPiece().getType(), capture.getCaptureAtX(), capture.getCaptureAtY());
            board[capturedPiece.getX()][capturedPiece.getY()].setPiece(capturedPiece);
            addCapturedPieceToLists(capturedPiece);
        }
        /* (Turn turn : possibleTurns) {
            turn.getMoves().addFirst(move);
        } */
        if (move.isTurnEnding() && !turnsPlayed.isEmpty()) {
            currentTurn = turnsPlayed.removeLast();
            currentTurn.getMoves().removeLast();
            // possibleTurns = GameLogic.getLegalTurns(this);
            switchTurn();
        }
        return true;
    }


    private void removeCapturedPieceFromLists(Piece piece){
        if (piece.type.color.equals("white")) {
            whitePieces.remove(piece);
        }
        else {
            blackPieces.remove(piece);
        }
    }

    private void addCapturedPieceToLists(Piece piece){
        if (piece.type.color.equals("white")) {
            whitePieces.add(piece);
        }
        else {
            blackPieces.add(piece);
        }
    }

    public Piece getPieceAt(int x, int y) {
        if (x >= 0 && x < board.length && y >= 0 && y < board[0].length) {
            return board[x][y].getPiece();  // Assuming each tile has a method getPiece()
        }
        return null;
    }

    public boolean hasPieceAt(int x, int y) {
        if (x >= 0 && x < board.length && y >= 0 && y < board[0].length) {
            return board[x][y].hasPiece();
        }
        return false;
    }

    public boolean getIsWhiteTurn() {
        return isWhiteTurn;
    }
    
    public Move determineMoveType(int x0, int y0, int newX, int newY) {
        // Check for out-of-bounds coordinates
        if (!isValidPosition(x0, y0) || !isValidPosition(newX, newY)) {
            System.out.println("Invalid board coordinates: (" + x0 + ", " + y0 + ") to (" + newX + ", " + newY + ")");
            return new InvalidMove(x0, y0, null, newX, newY);
        }
    
        Tile startTile = board[x0][y0];
        Tile targetTile = board[newX][newY];
    
        // Check if start tile or piece is null
        if (startTile == null || !startTile.hasPiece()) {
            System.out.println("No piece at starting position: (" + x0 + ", " + y0 + ")");
            return new InvalidMove(x0, y0, null, newX, newY);
        }
    
        Piece piece = startTile.getPiece();
    
        // Check if it's the correct player's turn
        if (isWhiteTurn != piece.getType().color.equals("white")) {
            //System.out.println("It's not the correct player's turn for piece at: (" + x0 + ", " + y0 + ")");
            return new InvalidMove(x0, y0, piece, newX, newY);
        }
    
        // Check if the target tile is empty
        if (targetTile == null || targetTile.hasPiece()) {
            //System.out.println("Target tile is not empty: (" + newX + ", " + newY + ")");
            return new InvalidMove(x0, y0, piece, newX, newY);
        }
    
        // Check if the target tile is black
        if (!targetTile.isBlack()) {
            //System.out.println("Target tile is not black: (" + newX + ", " + newY + ")");
            return new InvalidMove(x0, y0, piece, newX, newY);
        }
    
        // King logic
        if (piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING) {
            if (isMoveforKing(x0, y0, newX, newY) && isPathClearforKing(x0, y0, newX, newY)) {
                return new NormalMove(x0, y0, piece, newX, newY);
            }
            if (isCapturePathforKing(x0, y0, newX, newY)) {
                Piece capturedPiece = getCapturedPieceOnPathforKing(x0, y0, newX, newY);
                return new Capture(x0, y0, piece, capturedPiece, newX, newY); 
            }
        } else {
            // Normal piece logic
            if (isMoveDiagonalNormal(x0, y0, newX, newY) && piece.getType().moveDir == (newY - y0)) {
                return new NormalMove(x0, y0, piece, newX, newY);
            }
            if (newY == y0 && Math.abs(newX - x0) == 4) {
                return checkHorizontalCapture(x0, y0, newX, newY, piece);
            }
            if (newX == x0 && Math.abs(newY - y0) == 4) {
                return checkVerticalCapture(x0, y0, newX, newY, piece);
            }
            if (Math.abs(newX - x0) == 2 && Math.abs(newY - y0) == 2) {
                return checkDiagonalCapture(x0, y0, newX, newY, piece);
            }
        }
    
        return new InvalidMove(x0, y0, piece, newX, newY);
    }
    
    // Helper methods for capture logic
    private Move checkHorizontalCapture(int x0, int y0, int newX, int newY, Piece piece) {
        int x1 = (newX + x0) / 2;
        Tile halfWay = board[x1][y0];
        if (halfWay != null && halfWay.hasPiece()) {
            Piece capturedPiece = halfWay.getPiece();
            if (!capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }
        return new InvalidMove(x0, y0, piece, newX, newY);
    }
    
    private Move checkVerticalCapture(int x0, int y0, int newX, int newY, Piece piece) {
        int y1 = (newY + y0) / 2;
        Tile halfWay = board[x0][y1];
        if (halfWay != null && halfWay.hasPiece()) {
            Piece capturedPiece = halfWay.getPiece();
            if (!capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }
        return new InvalidMove(x0, y0, piece, newX, newY);
    }
    
    private Move checkDiagonalCapture(int x0, int y0, int newX, int newY, Piece piece) {
        int x1 = (newX + x0) / 2;
        int y1 = (newY + y0) / 2;
        Tile halfWay = board[x1][y1];
        if (halfWay != null && halfWay.hasPiece()) {
            Piece capturedPiece = halfWay.getPiece();
            if (!capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }
        return new InvalidMove(x0, y0, piece, newX, newY);
    }
    
    // Helper method to validate positions
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < board.length && y >= 0 && y < board[0].length;
    }
    

    // Helper method to check if move is available for king
    private boolean isMoveforKing(int x0, int y0, int newX, int newY) {
        return ( Math.abs(newX - x0) == Math.abs(newY - y0)); //Adjusted for only normal diagonal moves
    }

    // Helper method to check if move is diagonal for normal pieces
    private boolean isMoveDiagonalNormal(int x0, int y0, int newX, int newY) {
        return Math.abs(newX - x0) == 1 && Math.abs(newY - y0) == 1;
    }

    private boolean isMoveACaptureForKing(int x0, int y0, int newX, int newY) {
        return ( x0==newX || y0==newY || Math.abs(newX - x0) == Math.abs(newY - y0)); //Adjusted for only normal diagonal moves
    }

    // Check if the path for king movement (diagonal, horizontal, vertical) is clear
    private boolean isPathClearforKing(int x0, int y0, int newX, int newY) {
        int dx = Integer.compare(newX, x0);
        int dy = Integer.compare(newY, y0);
    
        int x = x0 + dx;
        int y = y0 + dy;
    
        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                return false;  // Path is blocked
            }
            x += dx;
            y += dy;
        }
        return true;
    } 

    // Check if there is a capturable piece on the path
    private boolean isCapturePathforKing(int x0, int y0, int newX, int newY) {
        if (!isMoveACaptureForKing(x0, y0, newX, newY)) {
            return false;  // Not a move for the burger king
        }

        int dx = Integer.compare(newX , x0);
        int dy = Integer.compare(newY , y0);

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;
        int capturedX = -1;
        int capturedY = -1;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null) {
                    // Check if it's an opponent's piece
                    if (!board[x][y].getPiece().getType().color.equals(board[x0][y0].getPiece().getType().color)) {
                        capturedPiece = board[x][y].getPiece();  // Found an opponent's piece to capture
                        capturedX=x;
                        capturedY=y;
                    } else {
                        return false;  // Path is blocked by a friendly piece
                    }
                } else {
                    return false;  // More than one piece on the path
                }
            }
            x += dx;
            y += dy;
        }
        // Ensure capturing piece can only land in any tile behind the captured one
        if (capturedPiece != null) {
            int landingX = capturedX + dx;
            int landingY = capturedY + dy;

            if (Math.abs(newX - landingX) <= board.length && Math.abs(newY - landingY) <= board.length) {
                return true;
            } else {
                return false;
            }
        }

        return false;  // No capturable piece was found
    }

    // Return the piece to capture along the path
    private Piece getCapturedPieceOnPathforKing(int x0, int y0, int newX, int newY) {
        if (!isMoveACaptureForKing(x0, y0, newX, newY)) {
            return null;  // Not a capture-move for the burger king
        }
        int dx = Integer.compare(newX , x0);
        int dy = Integer.compare(newY , y0);

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;
        int capturedX = -1;
        int capturedY = -1;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null) {
                    // Check if it's an opponent's piece
                    if (!board[x][y].getPiece().getType().color.equals(board[x0][y0].getPiece().getType().color)) {
                        capturedPiece = board[x][y].getPiece();  //Found an opponent's piece to capture
                        capturedX = x;
                        capturedY = y;
                    } else {
                        return null;  // Path is blocked by a friendly piece
                    }
                } else {
                    return null;  // More than one piece on the path
                }
            }
            x += dx;
            y += dy;
        }
        // Ensure capturing piece can only land in any tile behind the captured one
        if (capturedPiece != null) {
            int landingX = capturedX + dx;
            int landingY = capturedY + dy;

            if (Math.abs(newX - landingX) <= board.length && Math.abs(newY - landingY) <= board.length) {
                return capturedPiece;  // Valid capture
            } else {
                return null;
            }
        }

        return null; // No valid capture is found
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (!board[x][y].hasPiece())  {
                    sb.append("0 ");
                }
                else {
                    Piece piece = board[x][y].getPiece();
                    if (piece.getType() == PieceType.BLACK) {
                        sb.append("b ");
                    } else if (piece.getType() == PieceType.WHITE) {
                        sb.append("w ");
                    } else if (piece.getType() == PieceType.BLACKKING) {
                        sb.append("B ");
                    } else if (piece.getType() == PieceType.WHITEKING) {
                        sb.append("W ");
                    }
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean equals(GameState b){
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (board[x][y].hasPiece() != b.getBoard()[x][y].hasPiece()) {
                    return false;
                }
                if (board[x][y].hasPiece() && b.getBoard()[x][y].hasPiece()) {
                    if (!board[x][y].getPiece().equals(b.getBoard()[x][y].getPiece())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Piece getPiece(int x, int y) {
        return board[x][y].getPiece();
    }

    public Turn getCurrentTurn() {
        return currentTurn;
    }


<<<<<<< Updated upstream
}
=======
    /**
     * Determines the winner of the game.
     * @return 1 if White wins, -1 if Black wins, 100 if the game is a draw, 0 if the game is not over.
     */
    public int getWinner() {
        if (winner != 0) {
            return winner;
        }
        if (isDraw()) {
            winner = 100; // Game is a draw
            return winner;
        }
        if (blackPieces.isEmpty()) {
            winner = 1; // White wins
            return winner;
        }
        if (whitePieces.isEmpty()) {
            winner = -1; // Black wins
            return winner;
        }
        return 0; // Game is not over
    }

    // Old GameLogic Methods

    /**
     * Retrieves all legal turns for the current player.
     * If no legal moves are available, the game is set to over.
     * @return A list of legal turns for the current player.
     */
    public List<Turn> getLegalTurns() {
        ArrayList<Move> availableMoves = getPossibleMoves();
        if (availableMoves.isEmpty()) {
            setGameOver(true, isWhiteTurn ? -1 : 1); // Set game over if no moves are available
            return new ArrayList<>();
        }
        if (availableMoves.get(0).isNormal()) {
            return Turn.copyMovesToTurns(availableMoves); // Convert moves to turns if no captures are available
        }
        ArrayList<Turn> availableTurns = new ArrayList<>();
        Set<Piece> pieces = Piece.movesToPieces(availableMoves);
        int maxCaptures = 0;
        for (Piece piece : pieces) {
            ArrayList<Turn> pieceTurns = getLegalTurns(piece);
            for (Turn turn : pieceTurns) {
                // TODO: Add 2 kings rule
                if (turn.getMoves().size() > maxCaptures) {
                    availableTurns.clear();
                    availableTurns.add(turn); // Add turns with the highest number of captures
                    maxCaptures = turn.getMoves().size();
                } else if (turn.getMoves().size() == maxCaptures) {
                    availableTurns.add(turn); // Add turns with equal maximum captures
                }
            }
        }
        return availableTurns;
    }

    /**
     * Retrieves all legal turns for a specific piece.
     * Uses depth-first search (DFS) to explore potential capture sequences.
     * @param piece The piece for which legal turns are being determined.
     * @return A list of legal turns for the specified piece.
     */
    public ArrayList<Turn> getLegalTurns(Piece piece) {
        GameState g = new GameState(this); // Create a copy of the current game state
        ArrayList<Move> availableMoves = g.getPossibleMoves(piece);
        if (availableMoves.get(0).isNormal()) {
            return Turn.copyMovesToTurns(availableMoves); // Convert normal moves to turns
        }
        DepthFirstSearch.resetMaxCaptures(); // Reset DFS variables
        DepthFirstSearch.resetResult();
        Turn initialTurn = new Turn();
        DepthFirstSearch.dfs(g, piece, initialTurn, 0); // Perform DFS to find capture sequences
        ArrayList<Turn> result = DepthFirstSearch.getResult();
        return result;
    }

    /**
     * Retrieves all capture moves available for the current player's pieces.
     * @return A list of all available capture moves.
     */
    public ArrayList<Move> getCaptures() {
        ArrayList<Move> availableCaptures = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces();
        for (Piece piece : pieces) {
            availableCaptures.addAll(getCaptures(piece));
        }
        return availableCaptures;
    }

    /**
     * Retrieves all capture moves available for a specific piece.
     * @param piece The piece for which capture moves are being determined.
     * @return A list of all available capture moves for the specified piece.
     */
    public ArrayList<Move> getCaptures(Piece piece) {
        ArrayList<Move> availableCaptures = new ArrayList<>();
        // TODO: Instead of iterating over all black tiles, iterate over all tiles where the piece can move.
        for (int row = 0; row < SIZE; row++) {
            int startCol = (row % 2 == 0) ? 1 : 0;
            for (int col = startCol; col < SIZE; col += 2) {
                Move move = determineMoveType(piece.getX(), piece.getY(), row, col);
                if (move.isCapture()) {
                    availableCaptures.add(move);
                }
            }
        }
        return availableCaptures;
    }

    /**
     * Retrieves all possible moves for the current player's pieces.
     * Prioritizes capture moves if available.
     * @return A list of all possible moves for the current player.
     */
    private ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> availableMoves = new ArrayList<>();
        ArrayList<Move> availableCaptures = new ArrayList<>();
        ArrayList<Move> currentMoves = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces();
        for (Piece piece : pieces) {
            // TODO: Instead of iterating over all black tiles, iterate over all tiles where the piece can move.
            currentMoves = getPossibleMoves(piece);
            if (currentMoves.isEmpty()) {
                continue;
            } else if (currentMoves.get(0).isCapture()) {
                availableCaptures.addAll(currentMoves);
            } else {
                availableMoves.addAll(currentMoves);
            }
        }
        if (!availableCaptures.isEmpty()) {
            return availableCaptures; // Return captures if available
        }
        return availableMoves; // Return normal moves otherwise
    }

    /**
     * Retrieves all possible moves for a specific piece.
     * Prioritizes capture moves if available.
     * @param piece The piece for which possible moves are being determined.
     * @return A list of all possible moves for the specified piece.
     */
    public ArrayList<Move> getPossibleMoves(Piece piece) {
        ArrayList<Move> availableMoves = new ArrayList<>();
        ArrayList<Move> availableCaptures = new ArrayList<>();
        // TODO: Instead of iterating over all black tiles, iterate over all tiles where the piece can move.
        for (int row = 0; row < SIZE; row++) {
            int startCol = (row % 2 == 0) ? 1 : 0;
            for (int col = startCol; col < SIZE; col += 2) {
                Move move = determineMoveType(piece.getX(), piece.getY(), row, col);
                if (move.isNormal() && availableCaptures.isEmpty()) {
                    availableMoves.add(move); // Add normal move if no captures are available
                } else if (move.isCapture()) {
                    availableCaptures.add(move); // Add capture move
                }
            }
        }
        if (!availableCaptures.isEmpty()) {
            return availableCaptures; // Return captures if available
        }
        return availableMoves; // Return normal moves otherwise
    }

    /**
     * Determines all pieces that can be captured by the given player's pieces.
     * @param pieces The player's pieces to evaluate.
     * @return A set of pieces that are threatened (can be captured).
     */
    public Set<Piece> getPiecesTheathenedBy(ArrayList<Piece> pieces) {
        // TODO: Instead of getLegalMoves, iterate over all legal Turns.
        Set<Piece> threatenedPieces = new HashSet<>();
        for (Piece piece : pieces) {
            for (Move move : getPossibleMoves(piece)) {
                if (move.isCapture()) {
                    Capture captureMove = (Capture) move;
                    threatenedPieces.add(captureMove.getCapturedPiece());
                }
            }
        }
        return threatenedPieces;
    }

    /**
     * Retrieves all legal moves for a specific piece.
     * @param piece The piece for which legal moves are being determined.
     * @return A list of legal moves for the specified piece.
     */
    public List<Move> getLegalMoves(Piece piece) {
        List<Turn> availableTurns = getLegalTurns(piece); // Fetch all legal turns for the piece
        List<Move> availableMoves = new ArrayList<>();

        for (Turn turn : availableTurns) {
            availableMoves.add(turn.getMoves().get(0)); // Add the first move of each turn
        }

        return availableMoves;
    }

    /**
     * Retrieves all legal moves for the current player.
     * @return A list of legal moves for the current player.
     */
    public List<Move> getLegalMoves() {
        List<Turn> availableTurns = getLegalTurns(); // Fetch all legal turns for the current player
        List<Move> availableMoves = new ArrayList<>();
        for (Turn turn : availableTurns) {
            availableMoves.add(turn.getMoves().get(0)); // Add the first move of each turn
        }
        return availableMoves;
    }

    /**
     * Retrieves a list of all pieces for the current player.
     * @return A list of all pieces for the current player.
     */
    public ArrayList<Piece> getListOfPieces() {
        if (isWhiteTurn) {
            return getWhitePieces(); // Return white pieces if it's White's turn
        } else {
            return getBlackPieces(); // Return black pieces if it's Black's turn
        }
    }

    /**
     * Evaluates the board using weighted features to determine favorability for White or Black.
     * 
     * The evaluation function is based on the following features:
     * - x1: Number of black pieces on the board.
     * - x2: Number of white pieces on the board.
     * - x3: Number of black kings on the board.
     * - x4: Number of white kings on the board.
     * - x5: Number of black pieces threatened by white (i.e., can be captured on White's next turn).
     * - x6: Number of white pieces threatened by black.
     * 
     * Weights for the features:
     * - w1 = -1 (negative weight for black pieces)
     * - w2 = 1 (positive weight for white pieces)
     * - w3 = -3 (heavier negative weight for black kings)
     * - w4 = 3 (heavier positive weight for white kings)
     * - w5 = 1 (positive weight for black pieces threatened by White)
     * - w6 = -1 (negative weight for white pieces threatened by Black)
     * 
     * @return A positive score for White's advantage, negative for Black's advantage.
     */
    /**
     * Evaluates the board using weighted features to determine favorability for White or Black.
     *
     * Incorporates `calculateCaptureValue` and applies a penalty for pieces in corners.
     *
     * @return A positive score for White's advantage, negative for Black's advantage.
     */
    public int evaluateBoard() {
        if (isGameOver()) {
            if ((isWhiteTurn && whitePieces.isEmpty()) || (!isWhiteTurn && blackPieces.isEmpty())) {
                return -1; // Losing state for the current player
            } else {
                return 1; // Winning state for the current player
            }
        }

        int score = 0;

        // Calculates score for white pieces
        for (Piece piece : whitePieces) {
            int menCount = (piece.getType() == PieceType.WHITE) ? 1 : 0;
            int kingCount = (piece.getType() == PieceType.WHITEKING) ? 1 : 0;
            score += GameLogic.calculateCaptureValue(menCount, kingCount);
        }

        // Calculates score for black pieces
        for (Piece piece : blackPieces) {
            int menCount = (piece.getType() == PieceType.BLACK) ? 1 : 0;
            int kingCount = (piece.getType() == PieceType.BLACKKING) ? 1 : 0;
            score -= GameLogic.calculateCaptureValue(menCount, kingCount);
        }

        // Adjust score based on whose turn it is
        if (isWhiteTurn) {
            if (score > 0) {
                return 1; // Favorable position for white
            } else if (score < 0) {
                return -1; // Unfavorable position for white
            } else {
                return 0; // Neutral position
            }
        } else {
            if (score < 0) {
                return 1; // Favorable position for black
            } else if (score > 0) {
                return -1; // Unfavorable position for black
            } else {
                return 0; // Neutral position
            }
        }
    }

    // end of old GameLogic methods
}
>>>>>>> Stashed changes
