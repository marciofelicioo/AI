import java.util.Objects;

/**
 * Classe container: Esta classe serve para armazenar o identificador e o custo de movimentação
 * de um contêiner
 * @author Márcio Felício
 * @version 1.0
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
     * Construtor de cópia para questões de encapsulamento e auxílio do método clone()
     */
    public Container(Container c) {
        this.id = c.getId();
        this.containerCost = c.getcost();
    }

    /**
     * getters
     */
    public int getcost() {
        return this.containerCost;
    }

    public char getId() {
        return this.id;
    }

    /**
     * @return uma chave única para a instância criada
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(),getcost());
    }

    /**
     * Verifica se os contentores são iguais
     * @param obj representa um contentor que será testado com o this (instância receptora)
     * @return true caso sejam iguais e false caso contrário
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;

        Container other = (Container) obj;
        return this.getId() == other.getId();
    }

    /**
     * método clone que retorna um objeto novo com os mesmos valores de estrutura da classe
     * para questões de encapsulamento
     * @return new Container(this);
     */
    @Override
    public Container clone()
    {
        return new Container(this);
    }

    /**
     * Retorna a representação textual da classe container
     */
    @Override
    public String toString() {
        return String.valueOf(getId());
    }
}