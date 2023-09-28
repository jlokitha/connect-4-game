package lk.ijse.dep.service;

public class AiPlayer extends Player {

    private Board newBoard;

    public AiPlayer(Board newBoard) {
        this.newBoard = newBoard;
    }

    @Override
    public void movePiece(int col) {

        int random;

        do {
            random = (int) (Math.random() * 6); // Generate a random integer between 0 and 5
        } while (!(random >= 0 && random < 6));

        if (this.newBoard.isLegalMove(random)) {

            this.newBoard.updateMove(random, Piece.GREEN);
            this.newBoard.getBoardUI().update(random, false);

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
