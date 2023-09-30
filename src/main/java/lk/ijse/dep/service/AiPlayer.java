package lk.ijse.dep.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AiPlayer extends Player {

    Board newBoard;

    static AiPlayer aiPlayer;

    public AiPlayer(Board newBoard) {
        this.newBoard = newBoard;
        aiPlayer = this;
    }

    @Override
    public void movePiece(int col) {
        MctsAlgorithm mcts = new MctsAlgorithm();

        // Assuming you have an initial Node to start with
        MctsAlgorithm.Node rootNode = new MctsAlgorithm.Node(BoardImpl.getPieces(), 2); // Assuming AI starts

        int iterations = 1000; // Choose the number of iterations

        for (int i = 0; i < iterations; i++) {
            MctsAlgorithm.Node selectedNode = mcts.selection(rootNode);

            if (!selectedNode.children.isEmpty()) {
                List<MctsAlgorithm.Node> expandedNodes = rootNode.expand(selectedNode);
                MctsAlgorithm.Node expandedNode = expandedNodes.get(0); // Assuming you're selecting the first expanded node
                int outcome = rootNode.simulation(expandedNode);
                rootNode.backpropagation(expandedNode, outcome);
            }
        }

        // After the iterations, choose the best move based on the MCTS statistics
        int bestMove = mcts.getBestMove(rootNode);

        // Make the best move on the board
        newBoard.updateMove(bestMove, Piece.GREEN);
        newBoard.getBoardUI().update(bestMove, false);

        if (newBoard.findWinner().getWinningPiece() == Piece.EMPTY) {
            if (!newBoard.exitsLegalMoves()) {
                newBoard.getBoardUI().notifyWinner(newBoard.findWinner());
            }
        } else {
            newBoard.getBoardUI().notifyWinner(newBoard.findWinner());
        }
    }

    static class MctsAlgorithm {

        static class Node {
            //2D array to represent the current state of the game.
            static Piece[][] board;
            //Current playing player(Human = 1, Ai = 2).
            static int currentPlayer;
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

            public List<Node> expand(Node node) {
                List<Node> legalMoves = new ArrayList<>();

                // Generate child nodes for legal moves
                for (int i = 0; i < 6; i++) {
                    if (isValidMove(i)) {
                        Piece[][] newBoard = makeMove(i);
                        int newPlayer = 3 - node.currentPlayer; // Switch player
                        Node newNode = new Node(newBoard, newPlayer);
                        newNode.parent = this;
                        legalMoves.add(newNode);
                    }
                }

                this.children = legalMoves;
                return legalMoves;
            }

            private static Piece[][] makeMove(int column) {
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

            private static boolean isValidMove(int column) {
                return board[column][4] == Piece.EMPTY;
            }

            public int simulation(Node node) {
                Piece winner = aiPlayer.newBoard.findWinner().getWinningPiece();

                // If there is a winner, return the result
                if (winner != Piece.EMPTY) {
                    return winner == Piece.BLUE ? 1 : -1; // Player 1 wins: 1, Player 2 wins: -1
                }

                // If the game is a draw, return 0
                if (aiPlayer.newBoard.exitsLegalMoves()) {
                    return 0;
                }

                // Otherwise, continue the simulation with a random move
                Random rand = new Random();
                int randomColumn;
                do {
                    randomColumn = rand.nextInt(7);
                } while (!isValidMove(randomColumn));

                Piece[][] newBoard = makeMove(randomColumn);
                int newPlayer = 3 - currentPlayer; // Switch player
                Node newNode = new Node(newBoard, newPlayer);

                return newNode.simulation(node);
            }

            public void backpropagation(Node node, int outcome) {
                visits++;
                totalValue += outcome;

                if (parent != null) {
                    parent.backpropagation(node, outcome);
                }
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

        public int getBestMove(Node node) {
            double maxChildValue = Double.MIN_VALUE;
            int bestMove = -1;

            for (Node child : node.children) {
                double childValue = child.totalValue / child.visits;
                if (childValue > maxChildValue) {
                    maxChildValue = childValue;
                    bestMove = findColumn(node.board, child.board);
                }
            }

            return bestMove;
        }

        private int findColumn(Piece[][] parentBoard, Piece[][] childBoard) {
            // Find the column where the boards differ (i.e., where the move was made)
            for (int col = 0; col < parentBoard.length; col++) {
                for (int row = 0; row < parentBoard[0].length; row++) {
                    if (parentBoard[col][row] != childBoard[col][row]) {
                        return col;
                    }
                }
            }
            return -1; // No move found
        }
    }
}
