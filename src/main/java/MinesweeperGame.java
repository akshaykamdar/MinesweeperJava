import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MinesweeperGame extends JFrame {

    private static final int ROWS = 9;
    private static final int COLS = 9;
    private static final int NUM_MINES = 10;

    private GameBoard gameBoard;  // New logic handler

    private JButton[][] buttons = new JButton[ROWS][COLS];
    private boolean gameOver = false;

    private JLabel statusLabel = new JLabel("Game in progress");
    private JLabel timerLabel = new JLabel("Time: 0");
    private Timer timer;
    private int elapsedSeconds = 0;
    private boolean timerStarted = false;

    public MinesweeperGame() {
        setTitle("Minesweeper");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize game logic
        gameBoard = new GameBoard(ROWS, COLS, NUM_MINES);

        JPanel topPanel = new JPanel(new BorderLayout());

        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(statusLabel, BorderLayout.CENTER);

        timerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(timerLabel, BorderLayout.WEST);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGame());
        topPanel.add(resetButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(ROWS, COLS));
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JButton button = new JButton();
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setPreferredSize(new Dimension(50, 50));
                button.setFont(new Font("Arial", Font.BOLD, 16));
                button.setFocusPainted(false);
                button.setBackground(new Color(192, 192, 192));
                button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                int r = row;
                int c = col;
                button.addMouseListener(new MouseAdapter() {
                    private boolean rightClickPressed = false;

                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (!gameOver && !gameBoard.isCellRevealed(r, c)) {
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                            }
                            if (SwingUtilities.isRightMouseButton(e)) {
                                rightClickPressed = true;
                            }
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (!gameOver && !gameBoard.isCellRevealed(r, c)) {
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                                revealCell(r, c);
                            } else if (SwingUtilities.isRightMouseButton(e) && rightClickPressed) {
                                toggleFlag(r, c);
                            }
                        }
                        rightClickPressed = false;
                    }
                });
                buttons[row][col] = button;
                panel.add(button);
            }
        }

        add(panel, BorderLayout.CENTER);

        timer = new Timer(1000, e -> {
            elapsedSeconds++;
            timerLabel.setText("Time: " + elapsedSeconds);
        });

        setVisible(true);
    }

    private void resetGame() {
        gameOver = false;
        timer.stop();
        timerStarted = false;
        elapsedSeconds = 0;
        timerLabel.setText("Time: 0");
        statusLabel.setText("Game in progress");

        gameBoard.initializeGame();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JButton button = buttons[row][col];
                button.setText("");
                button.setEnabled(true);
                button.setBackground(new Color(192, 192, 192));
                button.setForeground(Color.BLACK);
                button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            }
        }
    }

    private void revealCell(int row, int col) {
        if (!timerStarted) {
            timerStarted = true;
            timer.start();
        }

        boolean safe = gameBoard.revealCell(row, col);
        updateCellUI(row, col);

        if (!safe) {
            gameOver = true;
            timer.stop();
            revealAllMines();
            statusLabel.setText("Game Over! You clicked a mine.");
            JOptionPane.showMessageDialog(this, "Game Over! You clicked a mine.");
        } else if (gameBoard.checkWin()) {
            gameOver = true;
            timer.stop();
            statusLabel.setText("Congratulations! You won the game!");
            JOptionPane.showMessageDialog(this, "Congratulations! You won the game!");
        }
    }

    private void toggleFlag(int row, int col) {
        gameBoard.toggleFlag(row, col);
        updateCellUI(row, col);
    }

    // Update UI button based on underlying gameBoard cell state
    private void updateCellUI(int row, int col) {
        JButton button = buttons[row][col];

        if (gameBoard.isCellRevealed(row, col)) {
            button.setEnabled(false);
            button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            button.setBackground(new Color(224, 224, 224));

            if (gameBoard.isCellMine(row, col)) {
                button.setText("X");
                button.setForeground(Color.RED);
            } else {
                int count = gameBoard.getAdjacentMineCount(row, col);
                if (count > 0) {
                    button.setText(String.valueOf(count));
                    button.setForeground(getColorForNumber(count));
                } else {
                    button.setText("");
                }
            }
        } else if (gameBoard.isCellFlagged(row, col)) {
            button.setEnabled(true);
            button.setText("F");
            button.setForeground(Color.BLUE);
            button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            button.setBackground(new Color(192, 192, 192));
        } else {
            button.setEnabled(true);
            button.setText("");
            button.setForeground(Color.BLACK);
            button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            button.setBackground(new Color(192, 192, 192));
        }
    }

    private void revealAllMines() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (gameBoard.isCellMine(row, col)) {
                    JButton button = buttons[row][col];
                    button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                    button.setBackground(new Color(224, 224, 224));
                    button.setEnabled(false);
                    button.setText("X");
                    button.setForeground(Color.RED);
                }
            }
        }
    }

    private Color getColorForNumber(int num) {
        switch (num) {
            case 1: return Color.BLUE;
            case 2: return new Color(0, 128, 0);
            case 3: return Color.RED;
            case 4: return new Color(0, 0, 128);
            case 5: return new Color(128, 0, 0);
            case 6: return new Color(64, 224, 208);
            case 7: return Color.BLACK;
            case 8: return Color.GRAY;
            default: return Color.BLACK;
        }
    }

    // Main method stays same
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MinesweeperGame::new);
    }
}
