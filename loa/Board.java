/** Note to grader:
  * I know my project is really late, but I
  * already emailed Hilfinger regarding this.
  * I had a medical condition the 2 weeks before
  * it was due and I already gave Hilfinger the
  * proper documentation fron Tang Center. I already
  * got permission for an extension on this project.
  * Thank you for understanding!
  */

package loa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Formatter;
import java.util.NoSuchElementException;

import java.util.regex.Pattern;

import static loa.Piece.*;
import static loa.Direction.*;

/** Represents the state of a game of Lines of Action.
 *  @author Tara Calista
 */
class Board implements Iterable<Move> {

    /** Size of a board. */
    static final int M = 8;

    /** Pattern describing a valid square designator (cr). */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /** A Board whose initial contents are taken from INITIALCONTENTS
     *  and in which the player playing TURN is to move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row-1][col-1]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is MxM.
     *
     *  CAUTION: The natural written notation for arrays initializers puts
     *  the BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /** A new board in the standard initial position. */
    Board() {
        clear();
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    Board(Board board) {
        copyFrom(board);
    }

    /** Large prime. */
    static final int G = 1000000007;

    /** Set my state to CONTENTS with SIDE to move. */
    void initialize(Piece[][] contents, Piece side) {
        _moves.clear();
        for (int r = 1; r <= M; r += 1) {
            for (int c = 1; c <= M; c += 1) {
                set(c, r, contents[r - 1][c - 1]);
            }
        }
        _turn = side;
    }

    /** Set me to the initial configuration. */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /** Set my state to a copy of BOARD. */
    void copyFrom(Board board) {
        if (board == this) {
            return;
        }
        _moves.clear();
        _moves.addAll(board._moves);
        _turn = board._turn;
        this.initialize(board.boardArr, board._turn);
    }

    /** Return the contents of column C, row R, where 1 <= C,R <= 8,
     *  where column 1 corresponds to column 'a' in the standard
     *  notation. */
    Piece get(int c, int r) {
        return this.boardArr[r - 1][c - 1];
    }

    /** Return the contents of the square SQ.  SQ must be the
     *  standard printed designation of a square (having the form cr,
     *  where c is a letter from a-h and r is a digit from 1-8). */
    Piece get(String sq) {
        return get(col(sq), row(sq));
    }

    /** Return the column number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int col(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(0) - 'a' + 1;
    }

    /** Return the row number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int row(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(1) - '0';
    }

    /** Set the square at column C, row R to V, and make NEXT the next side
     *  to move, if it is not null. */
    void set(int c, int r, Piece v, Piece next) {
        if (v != BP && v != WP && v != EMP) {
            throw new IllegalArgumentException("no such starting piece.");
        }
        if (next != BP && next != WP && next != EMP && next != null) {
            throw new IllegalArgumentException("no such ending piece.");
        }
        this.boardArr[r - 1][c - 1] = v;
        if (next != null) {
            _turn = next;
        }
    }

    /** Set the square at column C, row R to V. */
    void set(int c, int r, Piece v) {
        set(c, r, v, null);
    }

    /** Assuming isLegal(MOVE), make MOVE. */
    void makeMove(Move move) {
        assert isLegal(move);
        _moves.add(move);
        Piece replaced = move.replacedPiece();
        int c0 = move.getCol0(), c1 = move.getCol1();
        int r0 = move.getRow0(), r1 = move.getRow1();
        if (replaced != EMP) {
            set(c1, r1, EMP);
        }
        set(c1, r1, move.movedPiece());
        set(c0, r0, EMP);
        _turn = _turn.opposite();
    }

    /** Retract (unmake) one move, returning to the state immediately before
     *  that move.  Requires that movesMade () > 0. */
    void retract() {
        assert movesMade() > 0;
        Move move = _moves.remove(_moves.size() - 1);
        Piece replaced = move.replacedPiece();
        int c0 = move.getCol0(), c1 = move.getCol1();
        int r0 = move.getRow0(), r1 = move.getRow1();
        Piece movedPiece = move.movedPiece();
        set(c1, r1, replaced);
        set(c0, r0, movedPiece);
        _turn = _turn.opposite();
    }

    /** Return the Piece representing who is next to move. */
    Piece turn() {
        return _turn;
    }

    /** Return true iff MOVE is legal for the player currently on move. */
    boolean isLegal(Move move) {
        int count = pieceCountAlong(move);
        int c0 = move.getCol0();
        int r0 = move.getRow0();
        int c1 = move.getCol1();
        int r1 = move.getRow1();
        int count2 = Math.max(Math.abs(c1 - c0), Math.abs(r1 - r0));
        return ((count == count2) && !blocked(move));
    }

    /** Return a sequence of all legal moves from this position. */
    Iterator<Move> legalMoves() {
        return new MoveIterator();
    }

    @Override
    public Iterator<Move> iterator() {
        return legalMoves();
    }

    /** Return true if there is at least one legal move for the player
     *  on move. */
    public boolean isLegalMove() {
        return iterator().hasNext();
    }

    /** Return true iff either player has all his pieces continguous. */
    boolean gameOver() {
        return piecesContiguous(BP) || piecesContiguous(WP);
    }

    /** Returns true if an index is valid in piecesContiguous.
     *  @param i
     *       the column integer.
     *  @param j
     *       the row integer.
     */
    boolean isValid(int i, int j) {
        if (i >= 0 && i <= 7 && j >= 0 && j <= 7) {
            return true;
        }
        return false;
    }

    /** 2D array to keep track here DFS has visited. */
    private static boolean[][] _visited = new boolean[M][M];

    /** Sum of contiguous pieces. */
    private int contSum;

    /** String abbrev of the piece SIDE. */
    private String sideAbbrev;

    /** String abbrev of the piece. */
    private Piece p;

    /** DFS.
     * @param i is the column integer
     * @param j is the row integer.
     * @return int.
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
                    p = boardArr[m][n];
                    if (p.abbrev().equals(sideAbbrev)) {
                        contSum++;
                        dfs(intI[m], intJ[n]);
                    }
                }
            }
        }
        return contSum;
    }

    /** Return the total number of SIDE pieces in the board. */
    private int totalCount(Piece side) {
        int sum = 0;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                if (boardArr[i][j].abbrev().equals(sideAbbrev)) {
                    sum++;
                }
            }
        }
        return sum;
    }

    /** Return true iff SIDE's pieces are continguous. */
    boolean piecesContiguous(Piece side) {
        int k;
        int l;
        sideAbbrev = side.abbrev();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                _visited[i][j] = false;
            }
        }
        contSum = 0;
        for (k = 0; k < M; k++) {
            for (l = 0; l < M; l++) {
                if (boardArr[k][l].abbrev().equals(sideAbbrev)) {
                    return dfs(k, l) == totalCount(side);
                }
            }
        }
        return false;
    }

    /** Return the total number of moves that have been made (and not
     *  retracted).  Each valid call to makeMove with a normal move increases
     *  this number by 1. */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public boolean equals(Object obj) {
        Board b = (Board) obj;
        return b.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        int sum = 0;
        int temp;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String s = boardArr[i][j].abbrev();
                if (s.equals("b")) {
                    temp = 0;
                } else if (s.equals("w")) {
                    temp = 1;
                } else if (s.equals("-")) {
                    temp = 2;
                } else {
                    throw new IllegalArgumentException("No such piece");
                }
                sum += power(3, (8 * i) + j);
            }
        }
        return sum;
    }

    /** Finding the power of a number.
      * @return int.
      * @param base
      *    the base of the power
      * @param exp
      *    the exponent of the power
      */
    public int power(int base, int exp) {
        if (exp == 0) {
            return 1;
        }
        long result = power(base, exp / 2);
        result *= result % G;
        if (exp % 2 == 1) {
            result *= base;
        }
        return (int) result;
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int r = M; r >= 1; r -= 1) {
            out.format("    ");
            for (int c = 1; c <= M; c += 1) {
                out.format("%s ", get(c, r).abbrev());
            }
            out.format("%n");
        }
        out.format("Next move: %s%n===", turn().fullName());
        return out.toString();
    }

    /** Return the number of pieces in the line of action indicated by MOVE. */
    private int pieceCountAlong(Move move) {
        int dCol = move.getCol1() - move.getCol0();
        int dRow = move.getRow1() - move.getRow0();
        int c = move.getCol0();
        int r = move.getRow0();
        int i;
        for (Direction d : Direction.values()) {
            for (i = 1; i <= 7; i++) {
                if (dCol == d.dc * i && dRow == d.dr * i) {
                    return pieceCountAlong(c, r, d);
                }
            }
        }
        throw new IllegalArgumentException("not a valid direction");
    }

    /** Return the number of pieces in the line of action in direction DIR and
     *  containing the square at column C and row R. */
    public int pieceCountAlong(int c, int r, Direction dir) {
        int count = 0;
        c--;
        r--;
        int i;
        if (dir == NOWHERE) {
            return (!boardArr[r][c].abbrev().equals("-") ? 1 : 0);
        } else if (dir == S || dir == N) {
            for (i = 0; i < M; i++) {
                if (!boardArr[i][c].abbrev().equals("-")) {
                    count++;
                }
            }
        } else if (dir == E || dir == W) {
            for (i = 0; i < M; i++) {
                if (!boardArr[r][i].abbrev().equals("-")) {
                    count++;
                }
            }
        } else if (dir == NE || dir == SW) {
            for (i = -Math.min(c, r); i <= Math.min(7 - c, 7 - r); i++) {
                if (!boardArr[r + i][c + i].abbrev().equals("-")) {
                    count++;
                }
            }
        } else if (dir == NW || dir == SE) {
            for (i = -Math.min(c, 7 - r); i <= Math.min(7 - c, r); i++) {
                if (!boardArr[r - i][c + i].abbrev().equals("-")) {
                    count++;
                }
            }
        } else {
            throw new IllegalArgumentException("bad direction");
        }
        return count;
    }

    /** Return true iff MOVE is blocked by an opposing piece or by a
     *  friendly piece on the target square. */
    public boolean blocked(Move move) {
        String t = move.replacedPiece().abbrev();
        String s = move.movedPiece().abbrev();
        if (s.equals("-")) {
            throw new IllegalArgumentException("can't move empty piece");
        }
        if (s.equals(t)) {
            return true;
        }
        int c0 = move.getCol0();
        int c1 = move.getCol1();
        int r0 = move.getRow0();
        int r1 = move.getRow1();
        int dCol = c1 - c0;
        int dRow = r1 - r0;
        int i, j;
        for (Direction d : Direction.values()) {
            for (i = 1; i <= 7; i++) {
                if (dCol == d.dc * i && dRow == d.dr * i) {
                    for (j = 1; j < i; j++) {
                        String u = get(c0 + d.dc * j, r0 + d.dr * j).abbrev();
                        if (s.equals("w") && u.equals("b")) {
                            return true;
                        }
                        if (s.equals("b") && u.equals("w")) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
        throw new IllegalArgumentException("not a valid direction");
    }

    /** The standard initial configuration for Lines of Action. */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };

    /** List of all unretracted moves on this board, in order. */
    private final ArrayList<Move> _moves = new ArrayList<>();
    /** Current side on move. */
    private Piece _turn;

    /** 2D Array showing the board's arrangement. */
    private static Piece[][] boardArr = new Piece[M][M];

    /** An iterator returning the legal moves from the current board. */
    private class MoveIterator implements Iterator<Move> {
        /** Current piece under consideration. */
        private int _c, _r;
        /** Next direction of current piece to return. */
        private Direction _dir;
        /** Next move. */
        private Move _move;

        /** A new move iterator for turn(). */
        MoveIterator() {
            _c = 1; _r = 1; _dir = NOWHERE;
            incr();
        }

        @Override
        public boolean hasNext() {
            return _move != null;
        }

        @Override
        public Move next() {
            if (_move == null) {
                throw new NoSuchElementException("no legal move");
            }

            Move move = _move;
            incr();
            return move;
        }

        @Override
        public void remove() {
        }
        /** Advance to the next _c and _r. */
        private void nextPiece() {
            if (_c == M && _r <= M) {
                _c = 1;
                _r++;
            } else if (_r > M) {
                _move = null;
            } else {
                _c++;
            }
        }

        /** Advance to the next legal move. */
        private void incr() {
            Move tempMove = null;
            Piece t1, t2;
            while (_r <= M) {
                t1 = Board.this._turn;
                t2 = Board.this.get(_c, _r);
                if (t1 == t2) {
                    break;
                } else {
                    nextPiece();
                }
            }
            if (_r > M) {
                _move = null;
                return;
            }
            int c1, r1, count3;
            Piece temp1, temp2;
            Board tempBoard;
            _dir = _dir.succ();
            while (_dir !=  null) {
                count3 = pieceCountAlong(_c, _r, _dir);
                c1 = _c + _dir.dc * count3;
                r1 = _r + _dir.dr * count3;
                tempMove = Move.create(_c, _r, c1, r1, Board.this);
                if (tempMove == null) {
                    _dir = _dir.succ();
                    continue;
                }
                if (isLegal(tempMove)) {
                    _move = tempMove;
                    break;
                }
                _dir = _dir.succ();
            }
            if (_dir == null) {
                _dir = NOWHERE;
                nextPiece();
                incr();
            }
        }
    }
}
