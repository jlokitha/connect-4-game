package lk.ijse.dep.service;

import java.util.*;

public class AiPlayer extends Player {

    public AiPlayer(Board board) {
        super(board);
    }

    @Override
    public void movePiece(int col) {

//        do {
//
//            col =  (int) (Math.random() * 6);
//
//        } while (!(col > -1 && col < 6) || !(board.isLegalMove(col)));

        MctsAlgorithm mcts = new MctsAlgorithm((BoardImpl) board);

        col = mcts.doMcts();

        if (this.board.isLegalMove(col)) {

            this.board.updateMove(col, Piece.GREEN);
            this.board.getBoardUI().update(col, false);

            if (this.board.findWinner().getWinningPiece() == Piece.EMPTY) {

                if (!this.board.exitsLegalMoves()) {

                    this.board.getBoardUI().notifyWinner(this.board.findWinner());
                }

            } else {

                this.board.getBoardUI().notifyWinner(this.board.findWinner());
            }
        }
    }

    static class MctsAlgorithm {
        static class Node {
            BoardImpl board;
            int value;
            int visit;
            Node parent;
            List<Node> children = new ArrayList<>();

            public Node(BoardImpl board) {
                this.board = board;
            }

            private Node getMaxValueChild() {
                Node result = children.get(0);

                for (int i = 1; i < children.size(); i++) {

                    if (children.get(i).value > result.value) {

                        result = children.get(i);
                    }
                }
                return result;
            }

            void addChild(Node child) {
                children.add(child);
            }
        }

        BoardImpl board;

        public MctsAlgorithm(BoardImpl board) {
            this.board = board;
        }

        private int doMcts(){

            int count=0;

            Node tree= new Node(board);

            while (count<4000){
                count++;

                //Select Node
                Node promisingNode = select(tree);

                //Expand Node
                Node selected = promisingNode;

                if (selected.board.getStatus()){
                    selected = expand(promisingNode);

                }

                Piece resultPiece = simulate(selected);

                backPropagation(resultPiece,selected);
            }

            Node best= tree.getMaxValueChild();

            System.out.println("Value " + best.value + " / Visits " + best.visit);

            return best.board.col;
        }

        private Node select(Node tree) {
            Node currentNode = tree;

            while (!currentNode.children.isEmpty()) {
                currentNode = maxUctNode(currentNode);
            }

            return currentNode;
        }

        private Node expand(Node node) {
            BoardImpl boardImpl = node.board;

            for (BoardImpl move : getAllLegalMoves(boardImpl)) {
                Node child = new Node(move);
                child.parent = node;
                node.addChild(child);
            }

            int random = new Random().nextInt(node.children.size());

            return node.children.get(random);
        }

        private Piece simulate(Node promisingNode) {

            Node node = new Node(promisingNode.board);
            node.parent = promisingNode.parent;

            Winner winner = node.board.findWinner();

            if (winner.getWinningPiece() == Piece.BLUE){
                node.parent.value = Integer.MIN_VALUE;

                return node.board.findWinner().getWinningPiece();
            }
            while (node.board.getStatus()){
                BoardImpl nextMove=node.board.getRandomLeagalNextMove();
                Node child = new Node(nextMove);
                child.parent=node;
                node.addChild(child);
                node=child;
            }

            return node.board.findWinner().getWinningPiece();
        }

        private void backPropagation(Piece resultPiece, Node selected) {

            Node node = selected;

            Piece player = node.board.getPlayer() == 1 ? Piece.BLUE : Piece.GREEN;

            while (node != null){
                node.visit++;

                if (player == resultPiece){
                    node.value++;
                }
                node = node.parent;
            }
        }

        private List<BoardImpl> getAllLegalMoves(BoardImpl board) {

            Piece nextPlayer = board.getPlayer() == 1 ? Piece.GREEN : Piece.BLUE;

            List<BoardImpl> moves = new ArrayList<>();

            outerLoop:
            for (int i = 0; i < 6; i++) {

                int raw = board.findNextAvailableSpot(i);

                if (raw != -1){
                    BoardImpl legalMove = new BoardImpl(board.getPieces(), board.getBoardUI());
                    legalMove.updateMove(i, nextPlayer);
                    moves.add(legalMove);
                }
            }
            return moves;
        }

        private Node maxUctNode(Node node) {
            int parentVisit = node.visit;
            Node bestChild = null;
            double bestValue = Double.NEGATIVE_INFINITY;

            for (Node child : node.children) {
                double uctValue;
                if (child.visit == 0) {
                    uctValue = Double.POSITIVE_INFINITY;
                } else {
                    uctValue = ((double) child.value / (double) child.visit)
                            + 1.41 * Math.sqrt(Math.log(parentVisit) / (double) child.visit);
                }

                if (uctValue > bestValue) {
                    bestValue = uctValue;
                    bestChild = child;
                }
            }

            return bestChild;
        }
    }
}
