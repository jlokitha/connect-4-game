package lk.ijse.dep.service;

import lk.ijse.dep.controller.BoardController;

public class BoardImpl implements Board {

    private Piece[][] pieces = new Piece[6][5];
    private BoardUI boardUI;

    private BoardController boardController;

    public BoardImpl(BoardController boardController) {
        this.boardController = boardController;
    }

    public BoardImpl (BoardUI boardUI) {

    }

    @Override
    public BoardUI getBoardUI() {
        return null;
    }

    @Override
    public int findNextAvailableSpot(int col) {
        return 0;
    }

    @Override
    public boolean isLegalMove(int col) {
        return false;
    }

    @Override
    public boolean exitsLegalMoves() {
        return false;
    }

    @Override
    public void updateMove(int col, Piece move) {

    }

    @Override
    public Winner findWinner() {
        return null;
    }
}
