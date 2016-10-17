package loa;
import static loa.Piece.*;
import static loa.Main.*;

/** A Player that prompts for moves and reads them from its Game.
 *  @author Tara Calista. */
class HumanPlayer extends Player {

    /** A HumanPlayer that plays the SIDE pieces in GAME.  It uses
     *  GAME.getMove() as a source of moves.  */
    HumanPlayer(Piece side, Game game) {
        super(side, game);
        _side = side;
        _game = game;
    }

    @Override
    Move makeMove() {
        Move human = _game.getMove();
        return human;
    }

    /** What side player is on. */
    private Piece _side;
    /** The current game being played. */
    private Game _game;

}
