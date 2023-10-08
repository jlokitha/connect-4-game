package lk.ijse.dep.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardImpl implements Board {

    private Piece[][] pieces;
    private BoardUI boardUI;

    private Piece player;

    public int col;

    public BoardImpl(BoardUI boardUI) {

        pieces = new Piece[6][5];


        //Child class object is assign to parent class variable.
        this.boardUI = boardUI;

        //Initialize all pieces in array as EMPTY.
        for (int i = 0; i < NUM_OF_COLS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                pieces[i][j] = Piece.EMPTY;
            }
        }
    }

    public BoardImpl(Piece[][] pieces, BoardUI boardUI) {

        this.pieces=new Piece[6][5];

        for (int i = 0; i < NUM_OF_COLS; i++) {
            for (int j = 0; j < NUM_OF_ROWS; j++) {

                this.pieces[i][j]=pieces[i][j];
            }
        }
        this.boardUI = boardUI;
    }

    @Override
    public BoardUI getBoardUI() {
        return boardUI;
    }

    @Override
    public int findNextAvailableSpot(int col) {

        //Check if there is any spot as EMPTY in provide column.
        for (int i = 0; i < NUM_OF_ROWS; i++) {

            if (pieces[col][i] == Piece.EMPTY) {

                return i; //Return row number if there is any.
            }
        }
        return -1; //Return -1 if there is non.
    }

    @Override
    public boolean isLegalMove(int col) {
        int rowNo = findNextAvailableSpot(col);
        //Check if the returned num is -1 or not.
        if (rowNo > -1) {
            return true; //Return true if rowNo is not -1.
        }
        return false; //Return false if rowNo is -1.
    }

    @Override
    public boolean exitsLegalMoves() {
        //Check whole board to find there is any EMPTY spots.
        for (int i = 0; i < NUM_OF_COLS; i++) {
            for (int j = 0; j < NUM_OF_ROWS; j++) {

                if (pieces[i][j] == Piece.EMPTY) {

                    return true; //Return true if there is.
                }
            }
        }
        return false; //Return false if there is not.
    }

    public Piece getPlayer() {
        return player;
    }

    @Override
    public void updateMove(int col, Piece move) {

        this.col = col;
        this.player = move;

        //Find the first EMPTY spot in provide colum and change the value from EMPTY to value of move.
        for (int i = 0; i < NUM_OF_ROWS; i++) {

            if (pieces[col][i] == Piece.EMPTY) {

                pieces[col][i] = move;
                break; //Break the for loop after find and initialize the first EMPTY spot.
            }
        }
    }

    @Override
    public Winner findWinner() {
        //Check if there is any winner.
        for (int i = 0; i < NUM_OF_COLS; i++) {
            for (int j = 0; j < NUM_OF_ROWS; j++) {
                Piece currentPiece = pieces[i][j]; //Take the first piece to check.

                //Ensure that currentPiece is not EMPTY.
                if (currentPiece != Piece.EMPTY) {

                    //Horizontal check.
                    if (i + 3 < pieces.length &&
                            currentPiece == pieces[i + 1][j] &&
                            currentPiece == pieces[i + 2][j] &&
                            currentPiece == pieces[i + 3][j]) {
                        return new Winner(currentPiece, i, j, i + 3, j);
                    }

                    //Vertical check.
                    if (j + 3 < pieces[0].length &&
                            currentPiece == pieces[i][j + 1] &&
                            currentPiece == pieces[i][j + 2] &&
                            currentPiece == pieces[i][j + 3]) {
                        return new Winner(currentPiece, i, j, i, j + 3);
                    }
                }
            }
        }
        //If there is no winner.
        return new Winner(Piece.EMPTY);
    }

    @Override
    public void updateMove(int col, int row, Piece move) {
            pieces[col][row] = move;
    }

    public Piece[][] getPieces() {
        return pieces;
    }

    public boolean getStatus(){
        if (!exitsLegalMoves()){
            return false;
        }

        Winner winner=findWinner();
        if (winner.getWinningPiece() != Piece.EMPTY){

            return false;
        }
        return true;
    }

    public BoardImpl getRandomLeagalNextMove() {
        final List<BoardImpl> legalMoves = getAllLegalNextMoves();

        if (legalMoves.isEmpty()) {
            return null;
        }

        final int random= new Random().nextInt(legalMoves.size());
        return legalMoves.get(random);

    }

    public List<BoardImpl> getAllLegalNextMoves() {

        Piece nextPiece = player == Piece.BLUE?Piece.GREEN:Piece.BLUE;

        List<BoardImpl> nextMoves = new ArrayList<>();

        for (int i = 0; i < NUM_OF_COLS; i++) {

            int raw = findNextAvailableSpot(i);

            if (raw != -1) {

                BoardImpl legalMove = new BoardImpl(this.pieces,this.boardUI);
                legalMove.updateMove(i, nextPiece);
                nextMoves.add(legalMove);
            }
        }
        return nextMoves;
    }

    @Override
    public BoardImpl getBoardImpl() {
        return this;
    }
}
