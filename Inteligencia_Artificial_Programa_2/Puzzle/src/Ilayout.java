import java.util.List;

public interface Ilayout {
    /**
     @return the children of the receiver.
     */
    List<Ilayout> children();
    /**
     @return true if the receiver equals the argument l; return false otherwise.
     */
    boolean isGoal(Ilayout l);
    /**
     @return the cost from the receiver to a successor
     */
    double getK();
    /**
     * @return the minimun estimated cost from current configuration to goal
     */
    double computeHeuristic(Ilayout goalLayout);
}