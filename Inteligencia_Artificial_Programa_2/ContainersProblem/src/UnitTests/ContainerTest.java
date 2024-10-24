package UnitTests;

import Exceptions.ContainersException;
import org.junit.Test;
import static org.junit.Assert.*;
import ContainersPP.*;
public class ContainerTest {

    /**
     * Test the constructor and getters.
     */
    @Test
    public void testConstructorAndGetters() throws ContainersException {
        Container container = new Container('A', 2);

        assertEquals("Container ID should be 'A'", 'A', container.getId());
        assertEquals("Container cost should be 2", 2, container.getCost());
    }

    /**
     * Test the equals method.
     */
    @Test
    public void testEquals() throws ContainersException {
        Container container1 = new Container('A', 2);
        Container container2 = new Container('A', 2);
        Container container3 = new Container('B', 3);

        assertEquals("Containers with same ID and cost should be equal", container1, container2);
        assertNotEquals("Containers with different IDs should not be equal", container1, container3);
    }

    /**
     * Test the hashCode method.
     */
    @Test
    public void testHashCode() throws ContainersException {
        Container container1 = new Container('A', 2);
        Container container2 = new Container('A', 2);

        assertEquals("Hash codes should be equal for equal containers", container1.hashCode(), container2.hashCode());
    }

    /**
     * Test the toString method.
     */
    @Test
    public void testToString() throws ContainersException {
        Container container = new Container('A', 2);
        String expectedOutput = "A";

        assertEquals("String representation should match expected output", expectedOutput, container.toString());
    }
}
