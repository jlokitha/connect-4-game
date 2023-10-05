package lk.ijse.dep.service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AiPlayer extends Player {

    Board newBoard;

    AiPlayer aiPlayer;

    public AiPlayer(Board newBoard) {
        this.newBoard = newBoard;
        aiPlayer = this;
    }

    @Override
    public void movePiece(int col) {
        do {
            col = (int) (Math.random() * 6); // Generate a random integer between 0 and 5
        } while (!(col >= 0 && col < 6));

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

    static class MctsAlgorithm {
        static class Node {
            Board board;
            int value;
            int visit;
            Node parent;
            List<Node> children = new ArrayList<>();

            public Node(Board board) {
                this.board = board;
            }

            Node getMaxValueChild() {
                Node result = children.get(0);

                for (int i = 1; i < children.size(); i++) {
                    if (children.get(i).value > result.value) {
                        result = children.get(i);
                    }
                }
                return result;
            }

            void addChild (Node child) {
                children.add(child);
            }
        }

        Board board;
        int playerId;
        int oppositePlayerId;

        public MctsAlgorithm (Board board, int playerId) {
            this.board = board;
            this.playerId = playerId;
            oppositePlayerId = 3 - playerId;
        }

        Node Expand (Node node) {
            Board board = node.board;

            for (Board move : getAllLegalMoves(board)) {
                Node child = new Node(move);
                child.parent = node;
                node.addChild(child);
            }

            Random rand = new Random();

            int random = rand.nextInt(node.children.size());

            return node.children.get(random);
        }

        private Board[] getAllLegalMoves(Board board) {

            List<Board> moves = new ArrayList<>();
            Piece[][] pieces = BoardImpl.getPieces();

            outerLoop:
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    if (pieces[i][j] == Piece.EMPTY) {
                        Board temp = new BoardImpl();
                        moves.add(((BoardImpl) temp).setBoard(pieces[i][j]));
                    }
                }
            }
        }
    }
}
