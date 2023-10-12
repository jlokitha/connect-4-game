package lk.ijse.dep.service;

import java.util.*;

public class AiPlayer extends Player {

    public AiPlayer(Board board) {
        super(board);
    }

    @Override
    public void movePiece(int col) {

//        do {
//            //Generate Random number between 0 and 6.
//            col =  (int) (Math.random() * 6);
//
//        /*
//        * First we check if the generated number isn't exceed the limit.
//        * After we check if the generated column index isn't full and there is EMPTY space within that column.
//        **/
//        } while (!(col > -1 && col < 6) || !(board.isLegalMove(col)));

        //Create new MCTSAlgorithm object.
        MctsAlgorithm mctsAlgorithm = new MctsAlgorithm((BoardImpl) board);
        //Find the best move that AI can play.
        col = mctsAlgorithm.mctsStart();

        //Check if there is a EMPTY spot in the column.
        if (this.board.isLegalMove(col)) {

            //Update the column in the board.
            this.board.updateMove(col, board.findNextAvailableSpot(col), Piece.GREEN);
            this.board.getBoardUI().update(col, false);

            /*
             * First it check if the winningPiece is EMPTY.If it is that means there is no winner and the game is tied.
             * Second it check if there is any legal moves left in the whole board.
             * If there is not that means game is over, and we need to check is AI or Human wins or that game is tied.
             **/
            if (this.board.findWinner().getWinningPiece() != Piece.EMPTY || !this.board.existLegalMoves()) {
                this.board.getBoardUI().notifyWinner(this.board.findWinner());
            }
        }
    }

    static class MctsAlgorithm {
        static class Node {
            //Save BordImpl object reference that provided through constructor when creating a new Node object.
            private final BoardImpl board;
            //Represent the value(how many wins does AI can have playing the current node) of the current Node.
            private int value;
            //Represent the times that the current node has been simulated.
            private int visit;
            //Save the reference of the parent value that the current node has been expanded from.
            private Node parent;
            //Store the UCT value of the current node.
            private double uct;
            //Save all the possible next moves can be played by next player.
            private final List<Node> childrenList = new ArrayList<>();
            //Constructor of the Node class.
            public Node(BoardImpl board) {
                this.board = board;
            }

            private Node getMaxValueChild() {

                /*
                * This method is used to find element from children array that has highest 'value' attribute.
                * Firstly its take the first element(index 0) of the childrenList array to
                * variable called maxChild.
                * Secondly it's iterates through children array.
                * Inside the if condition its compare 'value' attributes of every element in
                * childrenList and save the Node with max 'value' to maxChild variable.
                * Lastly its return the maxChild reference.
                **/

                Node maxChild = childrenList.get(0);

                for (int i = 1; i < childrenList.size(); i++) {

                    if (childrenList.get(i).uct > maxChild.uct) {

                        maxChild = childrenList.get(i);
                    }
                }
                return maxChild;
            }
        }

        private final BoardImpl board;
        //Constructor for MctsAlgorithm class.
        public MctsAlgorithm(BoardImpl board) {
            this.board = board;
        }

        private int mctsStart(){

            //This represents the current state of the game.
            Node tree= new Node(board);

            for (int i = 0; i < 4000; i++){

                //Selection phase of the MCTS.
                Node promisingNode = selection(tree);

                Node selected = promisingNode;

                //Expansion phase of the MCTS.
                if (selected.board.status()){
                    selected = expansion(promisingNode);

                }
                //Simulation phase of the MCTS.
                Piece resultPiece = rollout(selected);

                //Backpropagation phase of the MCTS.
                backPropagation(resultPiece, selected);
            }
            //Get child with the maximum value.
            Node best= tree.getMaxValueChild();

            System.out.println("Value " + best.value + " / Visits " + best.visit);

            //Return the column index AI need to play.
            return best.board.col;
        }

        private Node selection(Node tree) {

            /*
            * This method preform the selection phase of the MCTS algorithm.
            * First reference of the 'tree' is assigned to 'currentNode'.
            * After we iterate through a while loop until 'childrenList' of the currentNode is empty.
            * Inside the loop we assign Node value that return from maxUctNode method.
            * Lastly we returned the reference of the currentNode.
            **/

            Node currentNode = tree;

            while (!currentNode.childrenList.isEmpty()) {
                currentNode = maxUctNode(currentNode);
            }

            return currentNode;
        }

        private Node expansion(Node node) {

            /*
            * First we assign 'board' attribute to 'boardImpl' reference from provided 'node' object.
            * After we iterated through all possible next move that returned from getAllMoves method.
            * We assign every move to child reference one at a time and address of 'node' object is assign to
            * parent attribute of every child.
            * Next we add every child that returned from getAllMoves method to childrenList in 'node' object.
            * Lastly we generate random number between 0 and size of the childrenList array and return
            * the element of generated random number from childrenList array.
            **/

            BoardImpl boardImpl = node.board;

            for (BoardImpl move : boardImpl.getAllMoves()) {
                Node child = new Node(move);
                child.parent = node;
                node.childrenList.add(child);
            }

            int random = new Random().nextInt(node.childrenList.size());

            return node.childrenList.get(random);
        }

        private Piece rollout(Node promisingNode) {

            /*
            * First we duplicate the state represented by 'promisingNode' by creating a new node with the
            *  board attribute of the 'promisingNode' and assign it to 'node' reference, and we set the
            *  parent attribute of 'node' with the same value of 'promising' node parent attribute value.
            **/

            Node node = new Node(promisingNode.board);
            node.parent = promisingNode.parent;

            //Check if the current board has any winner.
            Winner winner = node.board.findWinner();

            /*
            * This if condition check to find is the winner is BLUE(human).
            * If so that means AI is lost, so we set the value of parent object of the 'node' class to
            * minimum number that integer can contain.
            * This is done to show AI playing this move will be lost of AI.
            * After that we return the winningPiece from the method in this case it is BLUE.
            **/
            if (winner.getWinningPiece() == Piece.BLUE){
                node.parent.value = Integer.MIN_VALUE;

                return node.board.findWinner().getWinningPiece();
            }

            /*
            * This while loop continue until game represented by 'node' meet the terminal state(until game ends).
            * After we find a random move from current board state and assign it to 'nextMove' reference.
            * Now we create a new Node object with the 'nextMove' as argument that we get and assign it to
            * child reference and set the reference of the node to the parent attribute in child.
            * We add the child to the childrenList array in 'node' and assign child to the node.
            * This means game is progressing to the next state.
            **/
            while (node.board.status()) {
                BoardImpl nextMove = node.board.getRandomNextMove();
                Node child = new Node(nextMove);
                child.parent = node;
                node.childrenList.add(child);
                node = child;
            }

            //Lastly we return the winningPiece of the node that represent the terminal state.
            return node.board.findWinner().getWinningPiece();
        }

        private void backPropagation(Piece resultPiece, Node selected) {

            /*
            * First we assign reference of the 'selected' to the node for avoid modifying the selected attribute.
            * After we find who is the current player and assign that to player reference.
            * Now we initiate a while loop that loop until the node is null(until we find the root Node).
            * We increment the attribute 'visit' and we check that current player and the player that won are the same
            * if so we increment the 'value' attribute too.
            * Lastly we assign reference of the 'parent' attribute to the 'node', effectively moving up the tree.
            **/

            Node node = selected;

            Piece player = node.board.player == 1 ? Piece.BLUE : Piece.GREEN;

            while (node != null){
                node.visit++;

                if (player == resultPiece){

                    node.value++;
                }
                node = node.parent;
            }
        }

        private Node maxUctNode(Node node) {
            //Retrieves the visit count of the 'node'.
            int parentVisit = node.visit;
            //To keep track of child with the best UCT value.
            Node bestChild = null;
            //To keep track of the highest UCT value.
            double bestValue = Double.NEGATIVE_INFINITY;

            //This for loop iterates through the childrenList of provided node.
            for (Node child : node.childrenList) {

                //Store calculated UCT value of the current child.
                double uctValue;

                /*
                * This if condition check whether the current child have been simulated at least one time.
                * If it isn't visited at least once we set the uctValue to POSITIVE_INFINITY, indicating that
                * this node isn't visited and prioritized for exploration.
                *
                * But if it is visited we calculated the uct for each child.
                **/
                if (child.visit == 0) {
                    uctValue = Double.POSITIVE_INFINITY;
                    child.uct = uctValue;
                } else {
                    uctValue = ((double) child.value / (double) child.visit)
                            + 1.41 * Math.sqrt(Math.log(parentVisit) / (double) child.visit);
                    child.uct = uctValue;
                }

                /*
                * If the 'uctValue' of current child is greater-than value of 'bestValue' we assign UCT value
                * of current child to the 'bestValue' variable and current child to the 'bestChild' reference.
                **/
                if (uctValue > bestValue) {

                    bestValue = uctValue;
                    bestChild = child;
                }
            }
            //Lastly we return the bestChild reference.
            return bestChild;
        }
    }
}
