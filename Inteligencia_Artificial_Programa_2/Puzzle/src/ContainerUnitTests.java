import org.junit.Test;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.*;
/**
 * Classe ContainerUnitTests: Esta classe tem como principal objetivo a realização de testes da classe ContainersConfiguration e dos métodos
 * solve e reconstructPath da classe BestFirst
 * @author Márcio Felício
 * @version 1.0
 */

public class ContainerUnitTests {

    /**
     * Testa o construtor da classe ContainersConfiguration passando uma string de configuração.
     * Verifica se a saída de toString() é gerada corretamente.
     */
    @Test
    public void testConstructor() {
        ContainersConfiguration config = new ContainersConfiguration("ABC DEF", true);
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        pw.println("[A, B, C]");
        pw.println("[D, E, F]");
        assertEquals(config.toString(), writer.toString());
        pw.close();
    }

    /**
     * Testa outra configuração de contêineres e verifica se a representação textual está correta.
     */
    @Test
    public void testConstructor2() {
        ContainersConfiguration config = new ContainersConfiguration("XYZ PQR", true);
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        pw.println("[P, Q, R]");
        pw.println("[X, Y, Z]");
        assertEquals(config.toString(), writer.toString());
        pw.close();
    }

    /**
     * Testa se o método solve() consegue encontrar o caminho do estado inicial até o objetivo.
     */
    @Test
    public void testSolve() {
        Ilayout initial = new ContainersConfiguration("ABC DEF", true);
        Ilayout goal = new ContainersConfiguration("DEF ABC", true);

        BestFirst bfs = new BestFirst();

        Iterator<BestFirst.State> solution = bfs.solve(initial, goal);

        assertNotNull("A solução não deve ser nula", solution);

        BestFirst.State lastState = null;
        while (solution.hasNext()) {
            lastState = solution.next();
        }
        assertNotNull(lastState);
        assertEquals(goal, lastState.getLayout());
    }

    /**
     * Verifica se dois estados com a mesma configuração são considerados iguais.
     */
    @Test
    public void testStateEquality() {
        Ilayout config1 = new ContainersConfiguration("ABC DEF", true);
        Ilayout config2 = new ContainersConfiguration("ABC DEF", true);

        BestFirst.State state1 = new BestFirst.State(config1, null);
        BestFirst.State state2 = new BestFirst.State(config2, null);

        assertEquals("Os estados com a mesma configuração devem ser iguais", state1, state2);
    }

    /**
     * Verifica se dois estados com configurações diferentes não são considerados iguais.
     */
    @Test
    public void testStateInequality() {
        Ilayout config1 = new ContainersConfiguration("ABC DEF", true);
        Ilayout config2 = new ContainersConfiguration("XYZ PQR", true);

        BestFirst.State state1 = new BestFirst.State(config1, null);
        BestFirst.State state2 = new BestFirst.State(config2, null);

        assertNotEquals("Os estados com diferentes configurações não devem ser iguais", state1, state2);
    }


    /**
     * Testa a reconstrução do caminho usando o método reconstructPath.
     * Garante que o caminho do estado inicial até o objetivo seja correto.
     */
    @Test
    public void testReconstructPath() {
        Ilayout config1 = new ContainersConfiguration("ABC DEF", true);
        Ilayout config2 = new ContainersConfiguration("DEF ABC", true);
        Ilayout config3 = new ContainersConfiguration("FEC DAB", true);

        BestFirst.State state1 = new BestFirst.State(config1, null);
        BestFirst.State state2 = new BestFirst.State(config2, state1);
        BestFirst.State state3 = new BestFirst.State(config3, state2);

        BestFirst bfs = new BestFirst();

        try {
            java.lang.reflect.Method method = BestFirst.class.getDeclaredMethod("reconstructPath", BestFirst.State.class);
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            Iterator<BestFirst.State> path = (Iterator<BestFirst.State>) method.invoke(bfs, state3);

            assertTrue("O caminho deve conter estados", path.hasNext());
            assertEquals("O primeiro estado deve ser o estado inicial", state1, path.next());
            assertEquals("O segundo estado deve ser state2", state2, path.next());
            assertEquals("O terceiro estado deve ser o estado objetivo (state3)", state3, path.next());
        } catch (Exception e) {
            fail("Ocorreu uma exceção: " + e.getMessage());
        }
    }

    /**
     * Testa se o método clone() cria uma cópia correta da configuração.
     */
    @Test
    public void testClone() {
        ContainersConfiguration config1 = new ContainersConfiguration("ABC DEF", true);
        ContainersConfiguration config2 = config1.clone();

        assertEquals("As configurações clonadas devem ser iguais", config1, config2);
        assertNotSame("Os objetos clonados devem ser diferentes instâncias", config1, config2);
    }

    /**
     * Verifica se duas configurações diferentes têm diferentes códigos hash.
     */
    @Test
    public void testHashCode() {
        ContainersConfiguration config1 = new ContainersConfiguration("ABC DEF", true);
        ContainersConfiguration config2 = new ContainersConfiguration("XYZ PQR", true);

        assertNotEquals("Configurações diferentes devem ter diferentes códigos hash", config1.hashCode(), config2.hashCode());
    }

    /**
     * Testa o método children() para garantir que ele gera corretamente os estados filhos.
     */
    @Test
    public void testChildren() {
        ContainersConfiguration config = new ContainersConfiguration("ABC DEF", true);
        List<Ilayout> children = config.children();

        assertFalse("A lista de filhos não deve estar vazia", children.isEmpty());
    }

    /**
     * Testa se um tabuleiro é corretamente identificado como o estado objetivo.
     */
    @Test
    public void testIsGoalTrue() {
        ContainersConfiguration config = new ContainersConfiguration("ABC DEF", true);
        ContainersConfiguration goal = new ContainersConfiguration("ABC DEF", true);
        assertTrue("O estado objetivo deve ser identificado corretamente", config.isGoal(goal));
    }

    /**
     * Testa se um tabuleiro é corretamente identificado como não sendo o estado objetivo.
     */
    @Test
    public void testIsGoalFalse() {
        ContainersConfiguration config = new ContainersConfiguration("ABC DEF", true);
        ContainersConfiguration goal = new ContainersConfiguration("XYZ PQR", true);
        assertFalse("O estado objetivo deve ser identificado corretamente", config.isGoal(goal));
    }

    /**
     * Testa o método getK(), verificando se o custo de movimento é retornado corretamente.
     */
    @Test
    public void testGetK() {
        ContainersConfiguration config = new ContainersConfiguration("ABC DEF", true);
        assertEquals("O valor de K deve estar correto", 0.0, config.getK(), 0.001);
    }
}
