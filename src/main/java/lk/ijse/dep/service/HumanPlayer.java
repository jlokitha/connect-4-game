package lk.ijse.dep.service;

public class HumanPlayer extends Player {

    public HumanPlayer(Board newBoard) {
        super(newBoard);
    }

    @Override
    public void movePiece(int col) {
        //Check if there is any EMPTY spots in the provided column.
        if (board.isLegalMove(col)) {

            //Update the column in the board.
            board.updateMove(col, Piece.BLUE);
            board.getBoardUI().update(col, true);

            /*
                First it check if the winningPiece is EMPTY.If it is that means there is no winner and the game is tied.
                Second it check if there is any legal moves left in the whole board.
                If there is not that means game is over, and we need to check is AI or Human wins or that game is tied.
            */
            if (board.findWinner().getWinningPiece() != Piece.EMPTY || !board.exitsLegalMoves()) {

                //notifyWinner method show who wins according to the returned value of findWinner method.
                board.getBoardUI().notifyWinner(board.findWinner());
            }
        }
    }
}
