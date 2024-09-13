import java.util.Arrays;
import java.util.List;

public class Board implements Ilayout, Cloneable {
    /**
     * Estrutura da classe Board
     */
    private static final int dim = 3;
    private int[][] board;
    /**
    Construtor por omissão
     */
    public Board() {
        this.board = new int[dim][dim];
    }
    /**
    Construtor de inicialização
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
    Construtor de cópia
     */
    public Board(Board b)
    {
        this.board = new int[Board.dim][Board.dim];
        for (int i = 0; i < Board.dim; i++)
            for (int j = 0; j < Board.dim; j++)
                this.board[i][j] = b.getBoard()[i][j];
    }
    /**
    Getter for Board
     */
    public int[][] getBoard()
    {
        return this.board;
    }
    /**
    This method returns the textual representation of the class Board
     */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                stringBuilder.append(this.board[i][j]);
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
        return List.of();
    }

    @Override
    public boolean isGoal(Ilayout l) {
        return false;
    }

    @Override
    public double getK() {
        return 0;
    }
}