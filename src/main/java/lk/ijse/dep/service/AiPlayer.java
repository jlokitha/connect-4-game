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
//        do {
//            col = (int) (Math.random() * 6); // Generate a random integer between 0 and 5
//        } while (!(col >= 0 && col < 6) || !(board.isLegalMove(col)));

        MctsAlgorithm mcts = new MctsAlgorithm(board.getBoardImpl());

        col=mcts.doMcts();

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

        public MctsAlgorithm(BoardImpl board) {
            this.board = board;
        }

        public int doMcts(){
            System.out.println("MCTS working.");
            int count=0;

            Node tree= new Node(board);

            while (count<4000){
                count++;


                //System.out.println(count);

                //Select Node
                Node promisingNode = select(tree);

                //Expand Node
                Node selected=promisingNode;

                if (selected.board.getStatus()){
                    selected= expand(promisingNode);

                }


                //Simulate
                Piece resultPiece=simulate(selected);

                //Propagate
                backPropagation(resultPiece,selected);


            }

            Node best= tree.getMaxValueChild();

            System.out.println("Best move scored " + best.value + " and was visited " + best.visit + " times");

            return best.board.col;

        }

        private Node select(Node tree) {
            Node node=tree;
            while (node.children.size()!=0){
                node = findBestNodeWithUCT(node);
            }
            return node;
        }

        Node expand (Node node) {
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
