package lk.ijse.dep.service;

public interface Board {
    int NUM_OF_ROWS = 5;
    int NUM_OF_COLS = 6;

    BoardUI getBoardUI ();

    int findNextAvailableSpot (int col);
    boolean isLegalMove (int col);

    boolean exitsLegalMoves ();

    void updateMove (int col, Piece move);

    Winner findWinner ();

    void updateMove (int col, int row, Piece move);
}
