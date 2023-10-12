package lk.ijse.dep.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardImpl implements Board {

    private final Piece[][] pieces;
    private final BoardUI boardUI;
    public int player;
    public int col;

    public BoardImpl(BoardUI boardUI) {

        //Initialize the array that represent the board inside the code.
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

    //Return boardUI address.
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
    public boolean existLegalMoves() {

        //Check whole board to find there is any EMPTY spots.
        for (int i = 0; i < NUM_OF_COLS; i++) {
            if (isLegalMove(i)) {
                return true; //Return true if there is.
            }
        }
        return false; //Return false if there is not.
    }

    @Override
    public void updateMove(int col, Piece move) {
        this.col = col;
        this.player = move == Piece.BLUE ? 1 : 2;

        //Find the first EMPTY spot in provide colum and assign move to the returned EMPTY row.
        pieces[col][findNextAvailableSpot(col)] = move;
    }

    @Override
    public Winner findWinner() {

        //Check if there is any winner in the current board.
        for (int i = 0; i < NUM_OF_COLS; i++) {
            for (int j = 0; j < NUM_OF_ROWS; j++) {

                //Take one Piece at a time to check is it the winningPiece.
                Piece currentPiece = pieces[i][j];

                //Ensure that currentPiece is not EMPTY.
                if (currentPiece != Piece.EMPTY) {

                    /*
                    * First we check if we can take three columns to the right.This ensures it won't exceed the board size.
                    * After that we ensure that all the pieces in that columns are the same if so it is a win.
                    **/
                    if (i + 3 < pieces.length &&
                            currentPiece == pieces[i + 1][j] &&
                            currentPiece == pieces[i + 2][j] &&
                            currentPiece == pieces[i + 3][j]) {
                        return new Winner(currentPiece, i, j, i + 3, j);
                    }

                    /*
                    * First we check if we can take three rows up in the column.This ensures it won't exceed the board size.
                    * Lastly we check and confirm all the pieces are the same type in that rows.
                    * If this all are true then it is a win.
                    **/
                    if (j + 3 < pieces[0].length &&
                            currentPiece == pieces[i][j + 1] &&
                            currentPiece == pieces[i][j + 2] &&
                            currentPiece == pieces[i][j + 3]) {
                        return new Winner(currentPiece, i, j, i, j + 3);
                    }
                }
            }
        }
        //If there is no winner we return EMPTY.
        return new Winner(Piece.EMPTY);
    }

    @Override
    public void updateMove(int col, int row, Piece move) {
        this.col = col;
        this.player = move == Piece.BLUE ? 1 : 2;

        //Update the board by placing move in provided column and row.
        pieces[col][row] = move;
    }

    //Method that created for MCTS algorithm.

    public BoardImpl(Piece[][] pieces, BoardUI boardUI) {
        //Declaration and initialization of the 2D array.
        this.pieces=new Piece[NUM_OF_COLS][NUM_OF_ROWS];

        //Coping the provided array to newly declared array.
        for (int i = 0; i < NUM_OF_COLS; i++) {
            for (int j = 0; j < NUM_OF_ROWS; j++) {
                this.pieces[i][j]=pieces[i][j];
            }
        }
        //Assign the 'boarsUI' instance variable with provided reference.
        this.boardUI = boardUI;
    }

    public boolean status(){
        //This method ensure that there is still left legal moves in the board and human or AI isn't won yet.
        return existLegalMoves() && findWinner().getWinningPiece() == Piece.EMPTY;
    }

    public BoardImpl getRandomNextMove() {
        //Create new ArrayList to catch the ArrayList that return from getAllMoves method.
        List<BoardImpl> legalMoves = getAllMoves();

        /*
        * In here we check if the returned array is empty or not.
        * If it is empty that means there aren't any legal moves left, so we returned null.
        * But if array isn't null we generate a random number between 0 and size of the 'legalMoves' array
        * and return the element of the generated number.
        **/
        return legalMoves.isEmpty() ? null : legalMoves.get(new Random().nextInt(legalMoves.size()));
    }

    public List<BoardImpl> getAllMoves() {

        //Store the next player.
        Piece nextPiece = player == 1 ? Piece.GREEN:Piece.BLUE;
        //To save all the next moves.
        List<BoardImpl> nextMoves = new ArrayList<>();

        for (int i = 0; i < NUM_OF_COLS; i++) {

            /*
            * This for loop iterate through all the columns in the board and find each column has any empty spot
            * using findNextAvailableSpot method.
            * This method return num between 0 and 5 if the provided column has any empty spot if it doesn't
            * it return -1.
            * After we check is there is any empty spot using if condition and if it is we create a new
            * 'BoardImpl' object using current board and current 'BoardUI' as arguments, this copy the current
            * state of the board to the newly created object.
            * After we 'nextPiece' and current column to updateMove method to represent simulation of the move, and
            * we add the potential next move to the 'nextMoves' array.
            * Lastly we return the 'nextMove' array.
            **/

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