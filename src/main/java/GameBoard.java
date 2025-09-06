import java.util.Random;

public class GameBoard {

    private int rows;
    private int cols;
    private int numMines;
    private Cell[][] cells;
    private boolean gameOver = false;

    public GameBoard(int rows, int cols, int numMines) {
        this.rows = rows;
        this.cols = cols;
        this.numMines = numMines;
        cells = new Cell[rows][cols];
        initializeGame();
    }

    public void initializeGame() {
        gameOver = false;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell();
            }
        }
        placeMines();
        calculateAdjacentMines();
    }

    private void placeMines() {
        // Place mines randomly
        Random random = new Random();
        int placed = 0;
        while (placed < numMines) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            if (!cells[r][c].isMine()) {
                cells[r][c].setMine(true);
                placed++;
            }
        }
    }

    private void calculateAdjacentMines() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!cells[r][c].isMine()) {
                    int count = 0;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int nr = r + i;
                            int nc = c + j;
                            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                                if (cells[nr][nc].isMine())
                                    count++;
                            }
                        }
                    }
                    cells[r][c].setAdjacentMines(count);
                }
            }
        }
    }

    public boolean revealCell(int row, int col) {
        if (outOfBounds(row, col) || gameOver || cells[row][col].isFlagged() || cells[row][col].isRevealed()) {
            return true; // safe or ignore invalids
        }

        cells[row][col].setRevealed(true);

        if (cells[row][col].isMine()) {
            gameOver = true;
            return false; // mine triggered - game over
        }

        if (cells[row][col].getAdjacentMines() == 0) {
            // Recursively reveal neighbors
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) {
                        revealCell(row + i, col + j);
                    }
                }
            }
        }

        return true;
    }

    public void toggleFlag(int row, int col) {
        if (outOfBounds(row, col) || gameOver || cells[row][col].isRevealed()) return;
        cells[row][col].setFlagged(!cells[row][col].isFlagged());
    }

    public boolean checkWin() {
        int revealedCount = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c].isRevealed()) revealedCount++;
            }
        }
        return revealedCount == (rows * cols - numMines);
    }

    public int getAdjacentMineCount(int row, int col) {
        if (outOfBounds(row, col)) return -1;
        return cells[row][col].getAdjacentMines();
    }

    public boolean isCellMine(int row, int col) {
        if (outOfBounds(row, col)) return false;
        return cells[row][col].isMine();
    }

    public boolean isCellRevealed(int row, int col) {
        if (outOfBounds(row, col)) return false;
        return cells[row][col].isRevealed();
    }

    public boolean isCellFlagged(int row, int col) {
        if (outOfBounds(row, col)) return false;
        return cells[row][col].isFlagged();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    private boolean outOfBounds(int row, int col) {
        return row < 0 || row >= rows || col < 0 || col >= cols;
    }

    private static class Cell {
        private boolean mine = false;
        private int adjacentMines = 0;
        private boolean revealed = false;
        private boolean flagged = false;

        public boolean isMine() { return mine; }
        public void setMine(boolean val) { mine = val; }

        public int getAdjacentMines() { return adjacentMines; }
        public void setAdjacentMines(int val) { adjacentMines = val; }

        public boolean isRevealed() { return revealed; }
        public void setRevealed(boolean val) { revealed = val; }

        public boolean isFlagged() { return flagged; }
        public void setFlagged(boolean val) { flagged = val; }
    }
}
