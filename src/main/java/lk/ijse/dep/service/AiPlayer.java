package lk.ijse.dep.service;

import java.util.ArrayList;
import java.util.List;

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

    static class MctsAlgorithm {

        static class Node {
            //2D array to represent the current state of the game.
            Piece[][] board;
            //Current playing player(Human = 1, Ai = 2).
            int currentPlayer;
            //Calculate visits.
            int visits;
            //Calculate the total value of the node.
            double totalValue;
            //List of child nodes.
            List<Node> children;
            //Reference to the parent node.
            Node parent;

            Node (Piece[][] board, int currentPlayer) {
                this.board = board;
                this.currentPlayer = currentPlayer;
                this.children = new ArrayList<>();
            }

            public List<Node> expand() {
                List<Node> legalMoves = new ArrayList<>();

                // Generate child nodes for legal moves
                for (int i = 0; i < 6; i++) {
                    if (isValidMove(i)) {
                        Piece[][] newBoard = makeMove(i);
                        int newPlayer = 3 - currentPlayer; // Switch player
                        Node newNode = new Node(newBoard, newPlayer);
                        newNode.parent = this;
                        legalMoves.add(newNode);
                    }
                }

                this.children = legalMoves;
                return legalMoves;
            }

            private Piece[][] makeMove(int column) {
                Piece[][] newBoard = new Piece[6][5];
                for (int i = 0; i < 6; i++) {
                    System.arraycopy(board[i], 0, newBoard[i], 0, 5);
                }

                // Find the lowest available row in the specified column
                int row = 4;
                while (row >= 0 && newBoard[column][row] != Piece.EMPTY) {
                    row--;
                }

                // Apply the move
                newBoard[column][row] = currentPlayer == 1 ? Piece.BLUE : Piece.GREEN;

                return newBoard;
            }

            private boolean isValidMove(int column) {
                return board[column][4] == Piece.EMPTY;
            }
        }

        public Node selection (Node node) {
            while (!node.children.isEmpty()) {
                node = uctSelect(node);
            }
            return node;
        }

        private Node uctSelect(Node node) {
            double maxUctValue = Double.MIN_VALUE;
            Node selectedChild = null;

            for (Node child : node.children) {
                double uctValue = calculateValue(child);

                if (uctValue > maxUctValue) {
                    maxUctValue = uctValue;
                    selectedChild = child;
                }
            }
            return selectedChild;
        }

        private double calculateValue(Node child) {
            double explorationTerm = Math.sqrt(Math.log(child.parent.visits) / child.visits);
            return child.totalValue / child.visits + 2 * explorationTerm;
        }
    }
}
