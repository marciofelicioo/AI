import java.util.*;

public class Board implements Ilayout, Cloneable {
    /**
     * Estrutura da classe Board
     */
    private static final int dim = 3;
    private int[][] board;
    /**
     * Construtor por omissão
     */
    public Board() {
        this.board = new int[dim][dim];
    }
    /**
     * Construtor de inicialização
     */
    public Board(String str) throws IllegalStateException
    {
        if (str.length() != dim * dim) throw new
                IllegalStateException("Invalid arg in Board constructor");
        this.board = new int[dim][dim];
        int si = 0;
        for (int i = 0; i < dim; i++)
            for (int j = 0; j < dim; j++)
                this.board[i][j] = Character.getNumericValue(str.charAt(si++));
    }
    /**
     * Construtor de cópia
     */
    public Board(Board b)
    {
        this.board = new int[Board.dim][Board.dim];
        for (int i = 0; i < Board.dim; i++)
            for (int j = 0; j < Board.dim; j++)
                this.board[i][j] = b.getBoard()[i][j];
    }
    /**
     * Getter for Board
     */
    public int[][] getBoard()
    {
        return this.board;
    }
    /**
     * This method returns the textual representation of the class Board
     */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if(this.board[i][j] == 0) stringBuilder.append(" ");
                else stringBuilder.append(this.board[i][j]);
                count++;
                if (count % dim == 0) {
                    stringBuilder.append("\n");
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * This method returns true if two boards are equal and false otherwise
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board1 = (Board) o;
        return Arrays.deepEquals(this.board, board1.getBoard());
    }
    @Override
    public Board clone()
    {
        return new Board(this);
    }
    @Override
    public int hashCode() {
        Object[] campos = {Board.dim,this.board};
        return Arrays.hashCode(campos);
    }

    @Override
    public List<Ilayout> children() {
        List<Ilayout> children = new ArrayList<>();

        // Position of the blank space (0)
        int blankRow = -1, blankCol = -1;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (board[i][j] == 0) {
                    blankRow = i;
                    blankCol = j;
                    break;
                }
            }
        }

        // All possible moves (up, down, left, right)
        int[][] moves = {
                { -1, 0 }, // Up
                { 1, 0 },  // Down
                { 0, -1 }, // Left
                { 0, 1 }   // Right
        };

        // Trying all possible moves
        for (int[] move : moves) {
            int newRow = blankRow + move[0];
            int newCol = blankCol + move[1];

            // Check if the new position is within bounds
            if (newRow >= 0 && newRow < dim && newCol >= 0 && newCol < dim) {
                Board newBoard = this.clone(); // Clone the current board
                // Swap the blank space with the new position
                newBoard.board[blankRow][blankCol] = newBoard.board[newRow][newCol];
                newBoard.board[newRow][newCol] = 0;
                children.add(newBoard); // Add the new board configuration to the list
            }
        }

        return children;
    }


    @Override
    public boolean isGoal(Ilayout l) {
        if (!(l instanceof Board goalBoard)) {
            return false;
        }
        // Check if the current board matches the goal board
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (this.board[i][j] != goalBoard.getBoard()[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public double getK() {
        return 1.0;
    }
}