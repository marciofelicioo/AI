#include <iostream>
#include <queue>
#include <unordered_map>
#include <vector>
#include <functional>
#include <memory>
// Define the Ilayout interface (equivalent to Java interface)
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