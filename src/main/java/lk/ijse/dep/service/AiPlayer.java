package lk.ijse.dep.service;

public class AiPlayer extends Player {

    private Board newBoard;

    public AiPlayer(Board newBoard) {
        this.newBoard = newBoard;
    }

    @Override
    public void movePiece(int col) {

        do {

            col = (int) (Math.random() * 6);

        } while (!(col >= 0 && col < 6) || !this.newBoard.isLegalMove(col));

        if (this.newBoard.isLegalMove(col)) {

            this.newBoard.updateMove(col, Piece.GREEN);
            this.newBoard.getBoardUI().update(col, false);

            if (this.newBoard.findWinner().getWinningPiece() == Piece.EMPTY) {

                if (!this.newBoard.exitsLegalMoves()) {
                    this.newBoard.getBoardUI().notifyWinner(this.newBoard.findWinner());
                }

            } else {
                this.newBoard.getBoardUI().notifyWinner(this.newBoard.findWinner());
            }
        }
    }
}
