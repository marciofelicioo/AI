import org.junit.Test;
import java.io.PrintWriter;
import java.io.StringWriter;

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
        Platform config = new Platform("ABC DEF", true);
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
        Platform config = new Platform("XYZ PQR", true);
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

//    /**
//     * Verifica se dois estados com a mesma configuração são considerados iguais.
//     */
//    @Test
//    public void testStateEquality() {
//        Ilayout config1 = new ContainersConfiguration("ABC DEF", true);
//        Ilayout config2 = new ContainersConfiguration("ABC DEF", true);
//
//        AStar.State state1 = new AStar.State(config1, null);
//        AStar.State state2 = new AStar.State(config2, null);
//
//        assertEquals("Os estados com a mesma configuração devem ser iguais", state1, state2);
//    }

//    /**
//     * Verifica se dois estados com configurações diferentes não são considerados iguais.
//     */
//    @Test
//    public void testStateInequality() {
//        Ilayout config1 = new ContainersConfiguration("ABC DEF", true);
//        Ilayout config2 = new ContainersConfiguration("XYZ PQR", true);
//
//        AStar.State state1 = new AStar.State(config1, null);
//        AStar.State state2 = new AStar.State(config2, null);
//
//        assertNotEquals("Os estados com diferentes configurações não devem ser iguais", state1, state2);
//    }


    /**
     * Testa se o método clone() cria uma cópia correta da configuração.
     */
    @Test
    public void testClone() {
        Platform config1 = new Platform("ABC DEF", true);
        Platform config2 = config1.clone();

        assertEquals("As configurações clonadas devem ser iguais", config1, config2);
        assertNotSame("Os objetos clonados devem ser diferentes instâncias", config1, config2);
    }

    /**
     * Verifica se duas configurações diferentes têm diferentes códigos hash.
     */
    @Test
    public void testHashCode() {
        Platform config1 = new Platform("ABC DEF", true);
        Platform config2 = new Platform("XYZ PQR", true);

        assertNotEquals("Configurações diferentes devem ter diferentes códigos hash", config1.hashCode(), config2.hashCode());
    }

//    /**
//     * Testa o método children() para garantir que ele gera corretamente os estados filhos.
//     */
//    @Test
//    public void testChildren() {
//        ContainersConfiguration config = new ContainersConfiguration("ABC DEF", true);
//        List<Ilayout> children = config.children();
//
//        assertFalse("A lista de filhos não deve estar vazia", children.isEmpty());
//    }

    /**
     * Testa se um tabuleiro é corretamente identificado como o estado objetivo.
     */
    @Test
    public void testIsGoalTrue() {
        Platform config = new Platform("ABC DEF", true);
        Platform goal = new Platform("ABC DEF", true);
        assertTrue("O estado objetivo deve ser identificado corretamente", config.isGoal(goal));
    }

    /**
     * Testa se um tabuleiro é corretamente identificado como não sendo o estado objetivo.
     */
    @Test
    public void testIsGoalFalse() {
        Platform config = new Platform("ABC DEF", true);
        Platform goal = new Platform("XYZ PQR", true);
        assertFalse("O estado objetivo deve ser identificado corretamente", config.isGoal(goal));
    }

    /**
     * Testa o método getK(), verificando se o custo de movimento é retornado corretamente.
     */
    @Test
    public void testGetK() {
        Platform config = new Platform("ABC DEF", true);
        assertEquals("O valor de K deve estar correto", 0.0, config.getK(), 0.001);
    }
}
