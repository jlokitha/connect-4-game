package lk.ijse.dep.service;

public class HumanPlayer extends Player {

    private Board board;

    public HumanPlayer(Board newBoard) {
        board = newBoard;
    }

    @Override
    public void movePiece(int col) {

        if (board.isLegalMove(col)) {

            board.updateMove(col, Piece.BLUE);
            board.getBoardUI().update(col, true);

            if (board.findWinner().getWinningPiece() == Piece.EMPTY) {

                if (!board.exitsLegalMoves()) {
                    board.getBoardUI().notifyWinner(board.findWinner());
                }

            } else {
                board.getBoardUI().notifyWinner(board.findWinner());
            }
        }
    }
}
