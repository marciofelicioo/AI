#ifndef PLATFORM_H
#define PLATFORM_H


#include <iostream>
#include <vector>
#include <stack>
#include <string>
#include <unordered_map>
#include <memory>
#include <algorithm> 
 // For std::sort
class Ilayout {
public:
    virtual double computeHeuristic(std::shared_ptr<Ilayout> goalLayout) = 0;
    virtual bool isGoal(std::shared_ptr<Ilayout> l) = 0;
    virtual std::vector<std::shared_ptr<Ilayout>> children() = 0;
    virtual double getK() const = 0;  // Movement cost
    virtual bool operator==(const Ilayout& other) const = 0;  // For equality check
    virtual std::size_t hashCode() const = 0;  // For hashing
    virtual ~Ilayout() {}
};

class Platform : public Ilayout {
private:
    std::vector<std::stack<Container>> stacks;  // Vector de pilhas de containers
    double cost;                                // Custo acumulado de movimentos
    std::unordered_map<char, std::pair<int, int>> cachedGoalPositions;  // Cache das posições objetivo

public:
    // Construtor padrão
    Platform() : cost(0) {}

    // Construtor com uma string de configuração
    Platform(const std::string& config, bool isInitialState) : cost(0) {
        parseInput(config, isInitialState);
    }

    // Construtor de cópia
    Platform(const Platform& other) : stacks(other.stacks), cost(other.cost), cachedGoalPositions(other.cachedGoalPositions) {}

    // Parse de uma configuração de entrada
    void parseInput(const std::string& config, bool isInitialState) {
        stacks.clear();  // Limpar as pilhas anteriores
        std::vector<std::string> stackConfigs;
        std::stringstream ss(config);
        std::string stackStr;

        // Dividir a string de configuração pelos espaços (representando as pilhas)
        while (ss >> stackStr) {
            std::stack<Container> stack;
            for (size_t i = 0; i < stackStr.length(); i++) {
                char containerId = stackStr[i];
                int moveCost = 0;

                // Verifica se há um número de custo de movimento ao lado do contêiner
                if (isInitialState && i + 1 < stackStr.length() && isdigit(stackStr[i + 1])) {
                    moveCost = stackStr[i + 1] - '0';  // Converte o dígito char para int
                    i++;  // Pula o próximo caractere (o número)
                }
                stack.push(Container(containerId, moveCost));
            }
            stacks.push_back(stack);
        }
    }

    // Getter para as pilhas
    const std::vector<std::stack<Container>>& getStacks() const {
        return stacks;
    }

    // Setter para o custo de movimento
    void setCost(double cost) {
        this->cost = cost;
    }

    // Função hash (necessária para armazenar em unordered_map)
    std::size_t hashCode() const override {
        std::size_t hash = 7;
        for (const auto& stack : stacks) {
            if (!stack.empty()) {
                hash = 31 * hash + std::hash<char>{}(stack.top().getId());
            }
        }
        return hash;
    }

    // Clone method
    std::shared_ptr<Ilayout> clone() const override {
        return std::make_shared<Platform>(*this);
    }

    // Verifica se esta configuração é igual a outra
    bool operator==(const Ilayout& other) const override {
        const Platform& otherPlatform = dynamic_cast<const Platform&>(other);
        return stacks == otherPlatform.stacks;
    }

    // Método de heurística para o A* (cálculo do custo estimado)
    double computeHeuristic(std::shared_ptr<Ilayout> goalLayout) override {
        double totalEstimatedCost = 0;
        Platform* goal = dynamic_cast<Platform*>(goalLayout.get());

        // Cache das posições objetivo
        if (cachedGoalPositions.empty()) {
            for (size_t i = 0; i < goal->stacks.size(); i++) {
                const std::stack<Container>& goalStack = goal->stacks[i];
                std::stack<Container> tempStack = goalStack;  // Cópia para iteração
                int level = 0;
                while (!tempStack.empty()) {
                    cachedGoalPositions[tempStack.top().getId()] = {i, level};
                    tempStack.pop();
                    level++;
                }
            }
        }

        // Cálculo do custo estimado
        for (size_t i = 0; i < stacks.size(); i++) {
            std::stack<Container> currentStack = stacks[i];
            int level = 0;
            while (!currentStack.empty()) {
                Container currentContainer = currentStack.top();
                currentStack.pop();
                auto goalPosition = cachedGoalPositions[currentContainer.getId()];
                if (goalPosition.first != i || goalPosition.second != level) {
                    totalEstimatedCost += currentContainer.getCost();
                }
                level++;
            }
        }

        return totalEstimatedCost;
    }

    // Verifica se esta configuração é o objetivo
    bool isGoal(std::shared_ptr<Ilayout> l) override {
        return *this == *l;
    }

    // Gera filhos (sucessores)
    std::vector<std::shared_ptr<Ilayout>> children() override {
        std::vector<std::shared_ptr<Ilayout>> result;

        // Para cada pilha, mover o topo para todas as outras pilhas
        for (size_t i = 0; i < stacks.size(); i++) {
            if (stacks[i].empty()) continue;  // Ignora pilhas vazias
            for (size_t j = 0; j < stacks.size(); j++) {
                if (i == j) continue;  // Não mover para a mesma pilha
                Platform child = *this;  // Clone da plataforma atual
                Container topContainer = child.stacks[i].top();
                child.stacks[i].pop();
                child.stacks[j].push(topContainer);
                child.setCost(child.getK() + topContainer.getCost());
                result.push_back(std::make_shared<Platform>(child));
            }
        }
        return result;
    }

    // Ordena as pilhas com base nos identificadores dos contêineres
    void getSortedStacks() {
        std::sort(stacks.begin(), stacks.end(), [](const std::stack<Container>& a, const std::stack<Container>& b) {
            if (a.empty() || b.empty()) return false;  // Ignora pilhas vazias
            return a.top().getId() < b.top().getId();  // Ordena pelo ID do topo
        });
    }

    // Move o contêiner de uma pilha para uma nova pilha (representando o chão)
    void moveToGround(int fromStack) {
        if (fromStack >= stacks.size() || stacks[fromStack].empty()) return;

        Container container = stacks[fromStack].top();
        stacks[fromStack].pop();

        std::stack<Container> newStack;
        newStack.push(container);
        stacks.push_back(newStack);  // Adiciona uma nova pilha (representando o chão)

        this->setCost(container.getCost());
    }

    // Move o contêiner de uma pilha para outra pilha
    void moveToStack(int fromStack, int toStack) {
        if (fromStack >= stacks.size() || toStack >= stacks.size() || stacks[fromStack].empty()) return;

        Container container = stacks[fromStack].top();
        stacks[fromStack].pop();
        stacks[toStack].push(container);

        this->setCost(container.getCost());
    }

    // Retorna o custo de movimento acumulado (g(n))
    double getK() const override {
        return cost;
    }

    // Representação textual da plataforma
    std::string toString() const {
        std::string result;
        for (const auto& stack : stacks) {
            result += "[";
            std::stack<Container> tempStack = stack;  // Cópia para iteração
            while (!tempStack.empty()) {
                result += tempStack.top().toString();
                tempStack.pop();
                if (!tempStack.empty()) result += ", ";
            }
            result += "]\n";
        }
        return result;
    }
};

#endif