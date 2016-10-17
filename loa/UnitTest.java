package loa;

import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;
import static loa.Piece.*;
import static loa.Direction.*;

/** The suite of all JUnit tests for the loa package.
 *  @author Tara Calista
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class, BoardTest.class);
    }

    /** A dummy test to avoid complaint. */
    @Test
    public void placeholderTest() {
    }


}


