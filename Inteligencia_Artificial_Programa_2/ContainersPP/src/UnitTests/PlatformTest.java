package UnitTests;
import ContainersPP.*;
import Exceptions.ContainersException;
import Exceptions.PlatformException;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Deque;
import java.util.List;

public class PlatformTest {

    /**
     * Test the constructor that takes a configuration string.
     * Verifies that the platform is initialized correctly.
     */
    @Test
    public void testConstructor() throws PlatformException, ContainersException {
        Platform platform = new Platform("A1B2 C3D4",true);
        List<Deque<Container>> stacks = platform.getStacks();

        assertEquals("Number of stacks should be 2", 2, stacks.size());

        Deque<Container> firstStack = stacks.get(0);
        assertEquals("First stack size should be 2", 2, firstStack.size());
        Container firstContainer = firstStack.peekFirst();
        assertEquals("First container in first stack should be A", 'A', firstContainer.getId());
        assertEquals("First container cost should be 1", 1, firstContainer.getCost());

        Deque<Container> secondStack = stacks.get(1);
        assertEquals("Second stack size should be 2", 2, secondStack.size());
        Container thirdContainer = secondStack.peekFirst();
        assertEquals("First container in second stack should be C", 'C', thirdContainer.getId());
        assertEquals("First container cost should be 3", 3, thirdContainer.getCost());
    }

    /**
     * Test the clone method to ensure it creates a deep copy.
     */
    @Test
    public void testClone() throws PlatformException, ContainersException {
        Platform original = new Platform("A1B2 C3D4",true);
        Platform clone = original.clone();

        assertEquals("Cloned platform should be equal to the original", original, clone);
        assertNotSame("Cloned platform should be a different instance", original, clone);

        clone.getStacks().get(0).removeLast();
        assertNotEquals("Platforms should not be equal after modification", original, clone);
    }

    /**
     * Test the equals method.
     */
    @Test
    public void testEquals() throws PlatformException, ContainersException {
        Platform platform1 = new Platform("A1B2 C3D4",true);
        Platform platform2 = new Platform("A1B2 C3D4",true);
        Platform platform3 = new Platform("A1B2D4 C3",true);

        assertEquals("Platforms with same configuration should be equal", platform1, platform2);
        assertNotEquals("Platforms with different configurations should not be equal", platform1, platform3);
    }

    /**
     * Test the hashCode method.
     */
    @Test
    public void testHashCode() throws PlatformException, ContainersException {
        Platform platform1 = new Platform("A1B2 C3D4",true);
        Platform platform2 = new Platform("A1B2 C3D4",true);

        assertEquals("Hash codes should be equal for equal platforms", platform1.hashCode(), platform2.hashCode());
    }

    /**
     * Test the isGoal method.
     */
    @Test
    public void testIsGoal() throws PlatformException, ContainersException {
        Platform initial = new Platform("A1B2 C3D4",true);
        Platform goal = new Platform("A1B2 C3D4",true);

        assertTrue("Initial platform should be goal", initial.isGoal(goal));

        Platform differentGoal = new Platform("A1B2D4 C3",true);
        assertFalse("Initial platform should not be goal", initial.isGoal(differentGoal));
    }

    /**
     * Test the children method to generate successors.
     * Expected moves: Moving top container from each stack to every other stack and to a new stack
     * For two stacks, each with at least one container, we should have:
     * - Move from stack 0 to stack 1
     * - Move from stack 0 to new stack
     * - Move from stack 1 to stack 0
     * - Move from stack 1 to new stack
     */
    @Test
    public void testChildren() throws PlatformException, ContainersException {
        Platform initial = new Platform("A1B1 C1D1",true);
        List<Ilayout> children = initial.children();


        assertEquals("Number of children should be 4", 4, children.size());

        for (Ilayout child : children) {
            assertNotEquals("Child should not be equal to parent", initial, child);
        }
    }

    /**
     * Test the computeHeuristic method.
     */
    @Test
    public void testComputeHeuristic() throws PlatformException, ContainersException {
        Platform initial = new Platform("A2B3 C4D5",true);
        Platform goal = new Platform("AC BD",false);
        HeuristicStrategy heuristic = new HeuristicStrategy(initial.getStacks());
        double heuristicValue = heuristic.computeHeuristic(goal);
        assertTrue("Heuristic value should be positive", heuristicValue > 0);

        Platform sameAsGoal = new Platform("AC BD",false);
        double zeroHeuristic = sameAsGoal.computeHeuristic(goal);
        assertEquals("Heuristic should be zero when at goal", 0.0, zeroHeuristic, 0.001);
    }

    /**
     * Test the toString method for correct representation.
     */
    @Test
    public void testToString() throws PlatformException, ContainersException {
        Platform platform = new Platform("A1B2 C3D4",true);
        String expectedOutput = "[A, B]\n[C, D]\n";
        assertEquals("String representation should match expected output", expectedOutput, platform.toString());
    }
}
