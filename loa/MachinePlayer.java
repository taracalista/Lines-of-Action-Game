package loa;

/** An automated Player.
 *  @author Tara Calista. */
class MachinePlayer extends Player {

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
        _side = side;
        _game = game;
    }

    /** Size of the row and column. */
    static final int M = 8;

    @Override
    Move makeMove() {
        testBoard = getBoard();
        for (Move m : testBoard) {
            maxMove = m;
            minMove = m;
            break;
        }
        max = Integer.MAX_VALUE;
        min = Integer.MIN_VALUE;
        maxMove.setMoveScore(max);
        minMove.setMoveScore(min);
        return findBestMove(2, testBoard, _side, min);
    }

    /** Finding the best move that the AI can make.
      * @param depth
      *    how many moves you want to look ahead
      * @param start
      *    the current board state
      * @param player
      *    the side that player is on
      * @param cutoff
      *    the last best value
      * @return Move.
      */
    Move findBestMove(int depth, Board start, Piece player, double cutoff) {
        int tempScore;
        int maxScore = 0;
        if (start.piecesContiguous(player)) {
            return maxMove;
        } else if (start.piecesContiguous(player.opposite())) {
            return minMove;
        } else if (depth == 0) {
            return guessBestMove(player, start, cutoff);
        }
        Move bestSoFar;
        bestSoFar = minMove;
        for (Move m : start) {
            start.makeMove(m);
            tempScore = getScore(start, player);
            Piece opposit = player.opposite();
            Move response = findBestMove(depth - 1, start, opposit, -tempScore);
            start.retract();
            if (-tempScore > maxScore) {
                maxScore = tempScore;
                bestSoFar = m;
                bestSoFar.setMoveScore(maxScore);
                if (bestSoFar.getMoveScore() >= cutoff) {
                    break;
                }
            }
        }
        return bestSoFar;
    }

    /** Guessing the best move for AI.
      * @param player
      *    the side that player is on
      * @param start
      *    the current state of the board
      * @param cutoff
      *    the last best value for cutoff
      * @return Move.
      */
    Move guessBestMove(Piece player, Board start, double cutoff) {
        int tempScore;
        Move bestSoFar;
        bestSoFar = minMove;
        for (Move m : start) {
            start.makeMove(m);
            tempScore = getScore(start, player);
            start.retract();
            if (tempScore > bestSoFar.getMoveScore()) {
                bestSoFar = m;
                bestSoFar.setMoveScore(tempScore);
                if (tempScore >= cutoff) {
                    break;
                }
            }
        }
        return bestSoFar;
    }

    /** Return the score of the board.
      * @param b
      *    the current board b
      * @param pi
      *    the side that player is on
      */
    public int getScore(Board b, Piece pi) {
        int score = 0;
        int temp;
        for (int k = 0; k < M; k++) {
            for (int l = 0; l < M; l++) {
                _visited[k][l] = false;
            }
        }
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                if (b.get(i + 1, j + 1) == pi && !_visited[i][j]) {
                    contSum = 0;
                    temp = dfs(i, j);
                    score += temp * temp;
                }
            }
        }
        return score;
    }

    /** DFS.
      * @param i
      *    the starting column
      * @param j
      *    the starting row
      * @return int
      */
    private int dfs(int i, int j) {
        _visited[i][j] = true;
        int[] intI = {i - 1, i + 1, i};
        int[] intJ = {j - 1, j + 1, j};
        for (int m = 0; m < 3; m++) {
            for (int n = 0; n < 3; n++) {
                if (m == 2 && n == 2) {
                    continue;
                }
                if (isValid(intI[m], intJ[n]) && !_visited[intI[m]][intJ[n]]) {
                    p = getBoard().get(m + 1, n + 1);
                    if (p == _side) {
                        contSum++;
                        dfs(intI[m], intJ[n]);
                    }
                }
            }
        }
        return contSum;
    }

    /** Returns true if an index is valid in getScore.
      * @param i
      *    the column index
      * @param j
      *    the row index
      */
    boolean isValid(int i, int j) {
        if (i >= 0 && i <= 7 && j >= 0 && j <= 7) {
            return true;
        }
        return false;
    }

    /** The side the player is on. */
    private Piece _side;
    /** The current game being played. */
    private Game _game;
    /** For DFS to check if a piece has been visited. */
    private boolean[][] _visited = new boolean[M][M];
    /** The total sum of a piece in a board. */
    private int contSum;
    /** The board being tested for AI. */
    private Board testBoard;
    /** The piece that the player is. */
    private Piece p;
    /** Max and min values. */
    private int max, min;
    /** The max and min moves that gives max and min values. */
    private Move maxMove, minMove;
}
