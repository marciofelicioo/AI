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
     * @param o represents the board to be compared
     * @return Arrays.deepEquals(this.Board, board1.getBoard())
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board1 = (Board) o;
        return Arrays.deepEquals(this.board, board1.getBoard());
    }

    /**
     * This method returns a clone of the receiver, encapsulation purposes
     * @return new Board(this)
     */
    @Override
    public Board clone()
    {
        return new Board(this);
    }

    /**
     * This method returns a hashcode of the Board
     * @return Arrays.hashCode(campos)
     */
    @Override
    public int hashCode() {
        Object[] campos = {Board.dim,this.board};
        return Arrays.hashCode(campos);
    }

    /**
     * This method returns all the possible configurations from a start configuration
     * moving the blank space up down left right and saving the configurations into the data
     * structure List<Ilayout> children
     * @return children
     */
    @Override
    public List<Ilayout> children() {
        List<Ilayout> children = new ArrayList<>();

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
        int[][] moves = {
                { -1, 0 },
                { 1, 0 },
                { 0, -1 },
                { 0, 1 }
        };
        for (int[] move : moves) {
            int newRow = blankRow + move[0];
            int newCol = blankCol + move[1];

            if (newRow >= 0 && newRow < dim && newCol >= 0 && newCol < dim) {
                Board newBoard = this.clone();
                // Swaping the blank space with the new position
                newBoard.getBoard()[blankRow][blankCol] = newBoard.getBoard()[newRow][newCol];
                newBoard.getBoard()[newRow][newCol] = 0;
                children.add(newBoard);
            }
        }
        return children;
    }

    /**
     * This method asserts the equality of a receiver board configuration with a board l
     * configuration
     * @param l is the goal configuration
     * @return true or false
     */
    @Override
    public boolean isGoal(Ilayout l) {
        if (!(l instanceof Board goalBoard)) {
            return false;
        }
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (this.board[i][j] != goalBoard.getBoard()[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * returns the cost
     * @return 1.0
     */
    @Override
    public double getK() {
        return 1.0;
    }
}