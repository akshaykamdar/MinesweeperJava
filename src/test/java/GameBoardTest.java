import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {

    private GameBoard gameBoard;

    @BeforeEach
    public void setup() {
        gameBoard = new GameBoard(9, 9, 10);
        gameBoard.initializeGame();
    }

    @Test
    public void testMineCount() {
        int count = 0;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (gameBoard.isCellMine(r, c)) {
                    count++;
                }
            }
        }
        assertEquals(10, count, "There should be exactly 10 mines");
    }

    @Test
    public void testFlagging() {
        gameBoard.toggleFlag(0, 0);
        assertTrue(gameBoard.isCellFlagged(0, 0));
        gameBoard.toggleFlag(0, 0);
        assertFalse(gameBoard.isCellFlagged(0, 0));
    }

    @Test
    public void testRevealSafeCell() {
        // Find a safe cell that is not a mine and reveal it
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (!gameBoard.isCellMine(r, c)) {
                    boolean safe = gameBoard.revealCell(r, c);
                    assertTrue(safe);
                    assertTrue(gameBoard.isCellRevealed(r, c));
                    return; // Test one safe cell only
                }
            }
        }
    }

    @Test
    public void testRevealMineCell() {
        // Reveal a mine cell should cause game over
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (gameBoard.isCellMine(r, c)) {
                    boolean safe = gameBoard.revealCell(r, c);
                    assertFalse(safe);
                    assertTrue(gameBoard.isGameOver());
                    return; // Test one mine cell only
                }
            }
        }
    }

    @Test
    public void testWinCondition() {
        // Reveal all non-mine cells
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (!gameBoard.isCellMine(r, c)) {
                    gameBoard.revealCell(r, c);
                }
            }
        }
        assertTrue(gameBoard.checkWin());
        assertFalse(gameBoard.isGameOver());
    }
}
