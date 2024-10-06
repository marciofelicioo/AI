import java.util.Objects;

/**
 * Classe container para armazenar o identificador e o custo de movimentação
 */
public class Container {
    /**
     * Estrutura da classe Container
     */
    private char id;
    private int containerCost;

    /**
     * Construtor de inicialização de valores de estrutura
     */
    public Container(char id, int cost) {
        this.id = id;
        this.containerCost = cost;
    }

    /**
     * Construtor de cópia
     */
    public Container(Container c) {
        this.id = c.getId();
        this.containerCost = c.getcost();
    }

    public int getcost() {
        return this.containerCost;
    }

    public char getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),getcost());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;

        Container other = (Container) obj;
        return this.getId() == other.getId();
    }

    @Override
    public Container clone()
    {
        return new Container(this);
    }

    /**
     * Retorna a representação textual da classe interna container
     */
    @Override
    public String toString() {
        return String.valueOf(getId());
    }
}