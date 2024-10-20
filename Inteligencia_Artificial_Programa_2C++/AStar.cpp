#ifndef ASTAR_H
#define ASTAR_H

#include <iostream>
#include <queue>
#include <unordered_map>
#include <vector>
#include <functional>
#include <memory>

class Ilayout {
public:
    virtual double computeHeuristic(std::shared_ptr<Ilayout> goalLayout) = 0;
    virtual bool isGoal(std::shared_ptr<Ilayout> l) = 0;
    virtual std::vector<std::shared_ptr<Ilayout>> children() = 0;
    virtual double getK() const = 0;
    virtual std::shared_ptr<Ilayout> clone() const = 0;
    virtual bool operator==(const Ilayout& other) const = 0;
    virtual std::size_t hashCode() const = 0;
    virtual ~Ilayout() {}
};
// Hash function for Ilayout to be used in unordered_map
struct IlayoutHash {
    std::size_t operator()(const std::shared_ptr<Ilayout>& l) const {
        return l->hashCode();
    }
};

// State class for the A* algorithm
class State {
public:
    std::shared_ptr<Ilayout> layout;
    std::shared_ptr<State> father;
    double g;  // Cost to reach this state

    State(std::shared_ptr<Ilayout> l, std::shared_ptr<State> n) 
        : layout(l), father(n) {
        if (father != nullptr) {
            g = father->g + l->getK();
        } else {
            g = 0.0;
        }
    }

    double getF(std::shared_ptr<Ilayout> objective) const {
        return g + layout->computeHeuristic(objective);
    }

    bool operator==(const State& other) const {
        return *layout == *(other.layout);
    }

    std::size_t hashCode() const {
        return layout->hashCode();
    }
};

// Hash function for State to be used in unordered_map
struct StateHash {
    std::size_t operator()(const std::shared_ptr<State>& s) const {
        return s->hashCode();
    }
};

// AStar class
class AStar {
private:
    std::priority_queue<std::shared_ptr<State>, std::vector<std::shared_ptr<State>>, 
        std::function<bool(const std::shared_ptr<State>&, const std::shared_ptr<State>&)>> openSet;
    std::unordered_map<std::shared_ptr<Ilayout>, std::shared_ptr<State>, IlayoutHash> closedSet;
    std::unordered_map<std::shared_ptr<Ilayout>, std::shared_ptr<State>, IlayoutHash> openSetMap;
    std::shared_ptr<Ilayout> objective;

public:
    AStar() : openSet([](const std::shared_ptr<State>& a, const std::shared_ptr<State>& b) {
        return a->getF(nullptr) > b->getF(nullptr);
    }) {}

    std::shared_ptr<State> solve(std::shared_ptr<Ilayout> initial, std::shared_ptr<Ilayout> goal) {
        objective = goal;

        auto initialState = std::make_shared<State>(initial, nullptr);
        openSet.push(initialState);
        openSetMap[initial] = initialState;

        while (!openSet.empty()) {
            auto current = openSet.top();
            openSet.pop();
            openSetMap.erase(current->layout);

            if (current->layout->isGoal(objective)) {
                return current;
            }

            closedSet[current->layout] = current;

            // Generate successors
            for (auto& childLayout : current->layout->children()) {
                auto successor = std::make_shared<State>(childLayout, current);

                if (closedSet.find(successor->layout) != closedSet.end()) {
                    continue;  // Skip if the state is already in the closed set
                }

                if (openSetMap.find(successor->layout) == openSetMap.end()) {
                    openSet.push(successor);
                    openSetMap[successor->layout] = successor;
                } else {
                    auto openState = openSetMap[successor->layout];
                    if (successor->g < openState->g) {
                        openSet.push(successor);
                        openSetMap[successor->layout] = successor;
                    }
                }
            }
        }
        return nullptr;  // No solution found
    }
};

#endif