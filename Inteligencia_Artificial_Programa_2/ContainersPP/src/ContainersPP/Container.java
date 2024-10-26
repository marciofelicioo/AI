package ContainersPP;
import Exceptions.ContainersException;

/**
 * Classe ContainersPP.Container: Esta classe contém construtores que criam objetos Container
 * e o seu principal objetivo é armazenar os valores primitivos associados aos contentores
 * (character e custo de movimento)
 *
 * @author Márcio Felício, Maria Anjos, Miguel Rosa
 * @version 1.0 19/10/2024
 * @inv Cada Contentor tem de ter um custo associado acima de 0 valores
 */
public final class Container {
    /**
     * Estrutura da classe Container
     */
    private final char id;
    private final int containerCost;

    /**
     * Construtor de inicialização da classe Container
     */
    public Container(char id, int cost) throws ContainersException {
        if (cost < 0) {
            throw new ContainersException("O custo de movimento do contentor não pode ser negativo.");
        }
        this.id = id;
        this.containerCost = cost;
    }
    public Container(Container c)
    {
        this.id = c.getId();
        this.containerCost = c.getCost();
    }

    /**
     * getters da estrutura da classe Container
     */
    public int getCost() {
        return containerCost;
    }

    public char getId() {
        return id;
    }

    /**
     * Código único atribuído a cada instância de Container criada
     */
    @Override
    public int hashCode() {
        return id;
    }

    /**
     * Método que compara se duas instâncias de Containers são iguais
     * @param obj representa o objeto que será comparado com o recetor
     * @return true caso sejam, false caso contrário
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Container)) return false;
        Container other = (Container) obj;
        return this.id == other.id;
    }

    /**
     * @return clone de uma instância Container
     */
    @Override
    public Container clone() {
        return new Container(this);
    }

    /**
     * @return representação textual da classe Container
     */
    @Override
    public String toString() {
        return String.valueOf(id);
    }
}