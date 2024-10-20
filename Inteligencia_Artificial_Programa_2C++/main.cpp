#include <iostream>
#include <memory>
#include <chrono>  // Para medir o tempo de execução
#include AStar_H
#include Platform_H

// Função principal para testar a solução A*
int main() {
    // Instância de AStar
    AStar astar;

    // Entrada das configurações (usando std::cin como Scanner do Java)
    std::string firstConfiguration;
    std::string secondConfiguration;

    std::cout << "Digite a primeira configuração: ";
    std::getline(std::cin, firstConfiguration);
    
    std::cout << "Digite a segunda configuração (objetivo): ";
    std::getline(std::cin, secondConfiguration);

    // Criação das plataformas com as configurações
    std::shared_ptr<Platform> containerConfiguration1 = std::make_shared<Platform>(firstConfiguration, true);
    std::shared_ptr<Platform> containersConfiguration2 = std::make_shared<Platform>(secondConfiguration, false);

    // Medir o tempo de execução
    auto startTime = std::chrono::high_resolution_clock::now();

    // Resolver o problema
    std::shared_ptr<AStar::State> result = astar.solve(containerConfiguration1, containersConfiguration2);

    auto endTime = std::chrono::high_resolution_clock::now();

    // Calcular o tempo total em segundos
    std::chrono::duration<double> totalTimeInSeconds = endTime - startTime;

    // Verificar se a solução foi encontrada
    if (result != nullptr) {
        std::cout << "Solução encontrada:\n";
        std::cout << result->getLayout()->toString();  // Mostra o layout final
        std::cout << "Custo total: " << (int)result->getG() << "\n";
        std::cout << "Generated Nodes: " << AStar::getGeneratedNodes() << "\n";
        std::cout << "Expanded Nodes: " << AStar::getExpandedNodes() << "\n";
        std::cout << "Tempo total: " << totalTimeInSeconds.count() << " segundos\n";
    } else {
        std::cout << "Nenhuma solução encontrada.\n";
    }

    return 0;
}
