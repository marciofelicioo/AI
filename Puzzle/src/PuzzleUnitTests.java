import org.junit.Test;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.*;
public class PuzzleUnitTests {
    /**
     * Testa o construtor da classe Board ao passar uma string representando a
     * configuração do tabuleiro.
     * Verifica se a saída de toString() é gerada corretamente.
     */
    @Test
    public void testConstructor() {
        Board b = new Board("023145678");
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter ( writer ) ;
        pw.println(" 23");
        pw.println("145");
        pw.println("678");
        assertEquals(b.toString(), writer.toString());
        pw.close();
    }

    /**
     * Testa outra configuração
     * do tabuleiro no construtor e verifica se a representação textual está correta.
     */
    @Test
    public void testConstructor2() {
        Board b = new Board("123485670");
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter (writer) ;
        pw.println("123");
        pw.println("485");
        pw.println("67 ");
        assertEquals(b.toString(), writer.toString());
        pw.close();
    }

    /**
     * Verifica se o algoritmo BestFirst.solve() consegue encontrar uma solução do estado inicial para o estado objetivo.
     * Testa se a solução encontrada corresponde ao estado objetivo.
     */
    @Test
    public void testSolve() {
        Ilayout initial = new Board("023145678");
        Ilayout goal = new Board("123456780"); // The solved configuration

        BestFirst bfs = new BestFirst();

        Iterator<BestFirst.State> solution = bfs.solve(initial, goal);

        assertNotNull("Solution should not be null", solution);

        BestFirst.State lastState = null;
        while (solution.hasNext()) {
            lastState = solution.next();
        }
        assertNotNull(lastState);
        assertEquals(goal, lastState.getLayout());
    }

    /**
     * Verifica se dois estados com a mesma configuração de tabuleiro são considerados iguais
     */
    @Test
    public void testStateEquality() {
        Ilayout board1 = new Board("123456780");
        Ilayout board2 = new Board("123456780");

        BestFirst.State state1 = new BestFirst.State(board1, null);
        BestFirst.State state2 = new BestFirst.State(board2, null);

        assertEquals("States with the same layout should be equal", state1, state2);
    }

    /**
     * Verifica se dois estados com configurações de tabuleiro diferentes não são considerados iguais.
     */
    @Test
    public void testStateInequality() {
        Ilayout board1 = new Board("123456780");
        Ilayout board2 = new Board("876543210");

        BestFirst.State state1 = new BestFirst.State(board1, null);
        BestFirst.State state2 = new BestFirst.State(board2, null);

        assertNotEquals("States with different layouts should not be equal", state1, state2);
    }

    /**
     * Testa o método privado sucessores da classe BestFirst,
     * que gera os sucessores (estados filhos) de um dado estado.
     */
    @Test
    public void testSuccessors() {
        Ilayout board = new Board("123456780");
        BestFirst.State state = new BestFirst.State(board, null);

        BestFirst bfs = new BestFirst();

        try {
            java.lang.reflect.Method method = BestFirst.class.getDeclaredMethod("sucessores", BestFirst.State.class);
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<BestFirst.State> successors = (List<BestFirst.State>) method.invoke(bfs, state);

            assertNotNull("Successors should not be null", successors);

            assertFalse("Successors list should not be empty", successors.isEmpty());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Testa a reconstrução do caminho da solução utilizando o método reconstructPath,
     * que retorna o caminho do estado inicial ao objetivo.
     */
    @Test
    public void testReconstructPath() {

        Ilayout board1 = new Board("123456780");
        Ilayout board2 = new Board("123456078");
        Ilayout board3 = new Board("123456708");

        BestFirst.State state1 = new BestFirst.State(board1, null);
        BestFirst.State state2 = new BestFirst.State(board2, state1);
        BestFirst.State state3 = new BestFirst.State(board3, state2);

        BestFirst bfs = new BestFirst();

        try {
            java.lang.reflect.Method method = BestFirst.class.getDeclaredMethod("reconstructPath", BestFirst.State.class);
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            Iterator<BestFirst.State> path = (Iterator<BestFirst.State>) method.invoke(bfs, state3);

            assertTrue("Path should contain states", path.hasNext());
            assertEquals("First state should be the initial state", state1, path.next());
            assertEquals("Second state should be state2", state2, path.next());
            assertEquals("Third state should be the goal state (state3)", state3, path.next());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Testa o construtor padrão de Board e verifica se a representação textual vazia está correta.
     */
    @Test
    public void testDefaultConstructor() {
        Board b = new Board();
        String expected = "   \n   \n   \n";
        assertEquals(expected, b.toString());
    }

    /**
     * Testa o construtor de Board com uma string válida e verifica se a representação textual está correta.
     */
    @Test
    public void testStringConstructorValid() {
        Board b = new Board("123456780");
        String expected = "123\n456\n78 \n";
        assertEquals(expected, b.toString());
    }

    /**
     * Testa se o construtor lança uma exceção ao receber uma string de comprimento inválido.
     */
    @Test(expected = IllegalStateException.class)
    public void testStringConstructorInvalidLength() {
        new Board("12345678");
    }

    /**
     * Testa o construtor de cópia para garantir que os objetos Board copiados são iguais,
     * mas não são a mesma instância.
     */
    @Test
    public void testCopyConstructor() {
        Board b1 = new Board("123456780");
        Board b2 = new Board(b1);
        assertEquals(b1, b2);
        assertNotSame(b1, b2);
    }

    /**
     * Verifica se a função toString() de Board retorna a representação correta do tabuleiro.
     */
    @Test
    public void testToString() {
        Board b = new Board("123456780");
        String expected = "123\n456\n78 \n";
        assertEquals(expected, b.toString());
    }

    /**
     * Testa se dois objetos Board com a mesma configuração são considerados iguais.
     */
    @Test
    public void testEqualsTrue() {
        Board b1 = new Board("123456780");
        Board b2 = new Board("123456780");
        assertEquals(b1, b2);
    }

    /**
     * Testa se dois objetos Board com configurações diferentes são considerados diferentes.
     */
    @Test
    public void testEqualsFalse() {
        Board b1 = new Board("123456780");
        Board b2 = new Board("876543210");
        assertNotEquals(b1, b2);
    }

    /**
     * Verifica se o método clone() cria uma cópia correta do objeto Board.
     */
    @Test
    public void testClone() {
        Board b1 = new Board("123456780");
        Board b2 = b1.clone();
        assertEquals(b1, b2);
        assertNotSame(b1, b2);
    }

    /**
     * Testa se dois objetos Board diferentes têm diferentes códigos hash.
     */
    @Test
    public void testHashCode() {
        Board b1 = new Board("123456780");
        Board b2 = new Board("123450786");
        assertNotEquals(b1.hashCode(), b2.hashCode());
    }

    /**
     * Testa o método children() de Board para garantir que ele gera os estados filhos corretamente.
     */
    @Test
    public void testChildren() {
        Board b = new Board("123405678");
        List<Ilayout> children = b.children();


        assertEquals(4, children.size());


        Board firstChild = (Board) children.getFirst();
        assertEquals("1 3\n425\n678\n", firstChild.toString());

        Board secondChild = (Board) children.get(1);
        assertEquals("123\n475\n6 8\n", secondChild.toString());

        Board thirdChild = (Board) children.get(2);
        assertEquals("123\n 45\n678\n", thirdChild.toString());

        Board fourthChild = (Board) children.get(3);
        assertEquals("123\n45 \n678\n", fourthChild.toString());
    }

    /**
     * Testa se um tabuleiro é corretamente identificado como o estado objetivo.
     */
    @Test
    public void testIsGoalTrue() {
        Board b = new Board("123456780");
        Board goal = new Board("123456780");
        assertTrue(b.isGoal(goal));
    }

    /**
     * Testa se um tabuleiro é corretamente identificado como não sendo o estado objetivo.
     */
    @Test
    public void testIsGoalFalse() {
        Board b = new Board("123456780");
        Board goal = new Board("876543210");
        assertFalse(b.isGoal(goal));
    }

    /**
     * Testa o método getK() da classe Board, verificando se o valor retornado está correto.
     */
    @Test
    public void testGetK() {
        Board b = new Board("123456780");
        assertEquals(1.0, b.getK(), 0.001);
    }
}