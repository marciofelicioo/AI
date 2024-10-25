package UnitTests;

import ContainersPP.*;
import Exceptions.ContainersException;
import Exceptions.PlatformException;
import org.junit.Test;
import static org.junit.Assert.*;

public class AStarTest {

    /**
     * Test solving a simple problem where the initial state is the same as the goal.
     */
    @Test
    public void testSolveSameInitialAndGoal() throws PlatformException, ContainersException {
        Platform initial = new Platform("A1B2 C3D4",true);
        Platform goal = new Platform("AB CD",false);

        AStar aStar = new AStar();
        AStar.generatedNodes = 0;
        AStar.expandedNodes = 0;

        AStar.State solution = aStar.solve(initial, goal);

        assertNotNull("Solution should not be null", solution);
        assertEquals("Solution should be the initial state", initial, solution.getLayout());
        assertEquals("Path length should be zero", 0, solution.getPathLength());
        assertEquals("Generated nodes should be zero", 0, AStar.getGeneratedNodes());
        assertEquals("Expanded nodes should be one", 1, AStar.getExpandedNodes());
    }

    /**
     * Test solving a problem where a simple move is required.
     */
    @Test
    public void testSolveSimpleMove() throws PlatformException, ContainersException {
        Platform initial = new Platform("A1B1 C1",true);
        Platform goal = new Platform("A BC",false);

        AStar aStar = new AStar();
        AStar.generatedNodes = 0;
        AStar.expandedNodes = 0;

        AStar.State solution = aStar.solve(initial, goal);

        assertNotNull("Solution should not be null", solution);
        assertEquals("Solution path length should be 2", 2, solution.getPathLength());
        assertTrue("Generated nodes should be greater than zero", AStar.getGeneratedNodes() > 0);
        assertTrue("Expanded nodes should be greater than zero", AStar.getExpandedNodes() > 0);

        double penetrance = (double) solution.getPathLength() / AStar.getExpandedNodes();
        assertTrue("Penetrance should be less than or equal to 1", penetrance <= 1.0);
    }

    /**
     * Test that the algorithm finds the optimal solution in a more complex scenario.
     */
    @Test
    public void testSolveComplexScenario() throws PlatformException, ContainersException {
        Platform initial = new Platform("A1B2 C3D4 E5",true);
        Platform goal = new Platform("ABCDE",false);

        AStar aStar = new AStar();
        AStar.generatedNodes = 0;
        AStar.expandedNodes = 0;

        AStar.State solution = aStar.solve(initial, goal);

        assertNotNull("Solution should not be null", solution);
        assertEquals("Goal state should be achieved", goal, solution.getLayout());
        assertTrue("Path length should be greater than zero", solution.getPathLength() > 0);

        double expectedCost = solution.getG();
        assertTrue("Total cost should be greater than zero", expectedCost > 0);
    }

    /**
     * Test the generated and expanded nodes counters.
     */
    @Test
    public void testNodeCounters() throws PlatformException, ContainersException {
        Platform initial = new Platform("A1B1 C1D1",true);
        Platform goal = new Platform("AC BD",false);

        AStar aStar = new AStar();
        AStar.generatedNodes = 0;
        AStar.expandedNodes = 0;

        aStar.solve(initial, goal);

        int generatedNodes = AStar.getGeneratedNodes();
        int expandedNodes = AStar.getExpandedNodes();

        assertTrue("Generated nodes should be greater than zero", generatedNodes > 0);
        assertTrue("Expanded nodes should be greater than zero", expandedNodes > 0);
        assertTrue("Generated nodes should be greater than or equal to expanded nodes", generatedNodes >= expandedNodes);
    }

    /**
     * Test the penetrance calculation.
     */
    @Test
    public void testPenetranceCalculation() throws PlatformException, ContainersException {
        Platform initial = new Platform("A1B1 C1D1",true);
        Platform goal = new Platform("AC BD",false);

        AStar aStar = new AStar();
        AStar.generatedNodes = 0;
        AStar.expandedNodes = 0;

        AStar.State solution = aStar.solve(initial, goal);

        int pathLength = solution.getPathLength();
        int expandedNodes = AStar.getExpandedNodes();
        double penetrance = (double) pathLength / expandedNodes;

        assertTrue("Penetrance should be less than or equal to 1", penetrance <= 1.0);
        assertTrue("Penetrance should be greater than zero", penetrance > 0);
    }
}
