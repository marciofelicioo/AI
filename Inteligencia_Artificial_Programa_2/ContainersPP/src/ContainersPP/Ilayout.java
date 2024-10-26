package ContainersPP;
import java.util.Deque;
import java.util.List;
/**
 * Interface ContainersPP.Ilayout: Esta interface define as assinaturas dos métodos
 * que todas as classes que representam configurações de layouts devem implementar.
 *
 * @author Márcio Felício, Maria Anjos, Miguel Rosa
 * @version 1.0 19/10/2024
 * @inv Cada layout deve ter métodos para gerar sucessores, calcular o custo de transição e estimar o custo heurístico até o objetivo.
 */
public interface Ilayout {
    /**
     @return the children of the receiver.
     */
    List<Ilayout> children();
    /**
     @return true if the receiver equals the argument l; return false otherwise.
     */
    boolean isGoal(Ilayout layout);
    /**
     @return the cost from the receiver to a successor
     */
    double getK();
    /**
     * @return the minimun estimated cost from current configuration to goal
     */
    double computeHeuristic(Ilayout goal);
}
