package lk.ijse.dep.service;

import java.util.*;

public class AiPlayer extends Player {

    AiPlayer aiPlayer;

    public AiPlayer(Board board) {
        super(board);
        aiPlayer = this;
    }

    @Override
    public void movePiece(int col) {
        do {
            col = (int) (Math.random() * 6); // Generate a random integer between 0 and 5
        } while (!(col >= 0 && col < 6));

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

        BoardImpl board;
        int playerId;
        int oppositePlayerId;

        public MctsAlgorithm (BoardImpl board, int playerId) {
            this.board = board;
            this.playerId = playerId;
            oppositePlayerId = 3 - playerId;
        }

        private Node select(Node tree) {
            Node node=tree;
            while (node.children.size()!=0){
                node = findBestNodeWithUCT(node);
            }
            return node;
        }

        Node Expand (Node node) {
            BoardImpl board = node.board;

            for (BoardImpl move : getAllLegalMoves(board)) {
                Node child = new Node(move);
                child.parent = node;
                node.addChild(child);
            }

            Random rand = new Random();

            int random = rand.nextInt(node.children.size());

            return node.children.get(random);
        }

//        private Piece simulate(Node promisingNode) {
//
//
//            Node node = new Node(promisingNode.board);
//            node.parent = promisingNode.parent;
//
//            Winner winner = node.board.findWinner();
//
//            if (winner.getWinningPiece() == Piece.BLUE){
//                node.parent.value = Integer.MIN_VALUE;
//
//                return node.board.findWinner().getWinningPiece();
//            }
//
//
//            while (node.board.getStatus()){
//                BoardImpl nextMove=node.board.getRandomLeagalNextMove();
//                Node child = new Node(nextMove);
//                child.parent=node;
//                node.addChild(child);
//                node=child;
//            }
//
//            return node.board.findWinner().getWinningPiece();
//        }

        private void backPropagation(Piece resultPiece, Node selected) {

            Node node=selected;

            while (node!=null){
                node.visit++;

                if (node.board.getPlayer() == resultPiece){
                    node.value++;
                }
                node = node.parent;
            }
        }

        private List<BoardImpl> getAllLegalMoves(BoardImpl board) {

            Piece nextPlayer = board.getPlayer() == Piece.BLUE ? Piece.GREEN : Piece.BLUE;

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

        private Node findBestNodeWithUCT(Node node) {
            int parentVisit = node.visit;
            return Collections.max(
                    node.children,
                    Comparator.comparing(c -> uctValue(parentVisit,
                            c.value, c.visit)));
        }

        private double uctValue(
                int totalVisit, double nodeWinScore, int nodeVisit) {
            if (nodeVisit == 0) {
                return Integer.MAX_VALUE;
            }
            return ((double) nodeWinScore / (double) nodeVisit)
                    + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
        }
    }
}
