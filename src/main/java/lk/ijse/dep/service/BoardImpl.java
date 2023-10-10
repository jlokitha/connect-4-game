package lk.ijse.dep.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardImpl implements Board {

    private Piece[][] pieces;
    private BoardUI boardUI;
    public int player;
    public int col;

    public BoardImpl(BoardUI boardUI) {

        //Initialize the board array.
        pieces = new Piece[NUM_OF_COLS][NUM_OF_ROWS];

        //Child class object is assign to parent class variable.
        this.boardUI = boardUI;

        //Initialize all pieces in array as EMPTY.
        for (int i = 0; i < NUM_OF_COLS; i++) {
            for (int j = 0; j < NUM_OF_ROWS; j++) {

                pieces[i][j] = Piece.EMPTY;
            }
        }
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

        //Check for any EMPTY spots in the provided column.
        return findNextAvailableSpot(col) > -1;

    }

    @Override
    public boolean exitsLegalMoves() {

        //Check whole board to find there is any EMPTY spots.
        for (int i = 0; i < NUM_OF_COLS; i++) {
            if (isLegalMove(i)) {
                return true;
            }
        }
        return false; //Return false if there is not.
    }

    @Override
    public void updateMove(int col, Piece move) {

        this.col = col;
        this.player = move == Piece.BLUE ? 1 : 2;

        //Find the first EMPTY spot in provide colum and assign move if the column has any EMPTY spots.
        if (findNextAvailableSpot(col) > -1) {
            pieces[col][findNextAvailableSpot(col)] = move;
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


    //Method that created for AI.

    public BoardImpl(Piece[][] pieces, BoardUI boardUI) {

        this.pieces=new Piece[6][5];

        for (int i = 0; i < NUM_OF_COLS; i++) {
            for (int j = 0; j < NUM_OF_ROWS; j++) {

                this.pieces[i][j]=pieces[i][j];
            }
        }
        this.boardUI = boardUI;
    }

    public boolean getStatus(){
        return exitsLegalMoves() && findWinner().getWinningPiece() == Piece.EMPTY;
    }

    public BoardImpl getRandomLeagalNextMove() {
        List<BoardImpl> legalMoves = getAllMoves();
        return legalMoves.isEmpty() ? null : legalMoves.get(new Random().nextInt(legalMoves.size()));
    }

    public List<BoardImpl> getAllMoves() {

        Piece nextPiece = player == 1 ? Piece.GREEN:Piece.BLUE;

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
}