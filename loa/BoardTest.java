package loa;

import org.junit.Test;
import static org.junit.Assert.*;
import static loa.Piece.*;
import static loa.Direction.*;

public class BoardTest {
    static final int M = 8;
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
    Board _board = new Board(INITIAL_PIECES, BP);

    @Test
    public void setTest() {
        _board.set(1, 1, BP);
        assertEquals(BP, _board.get(1, 1));
    }

    @Test
    public void isLegalTest() {
        Move m = Move.create("b1-b3", _board);
        assertTrue(_board.isLegal(m));
    }

    @Test
    public void piecesContiguousTest() {
        assertTrue(!_board.piecesContiguous(BP));
    }

    @Test
    public void pieceCountAlongTest() {
        assertEquals(6, _board.pieceCountAlong(1, 2, N));
    }

    @Test
    public void blockedTest() {
        _board.set(2, 2, BP);
        Move m = Move.create("a2-d2", _board);
        assertTrue(_board.blocked(m));

    }
}
