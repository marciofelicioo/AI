#include <iostream>
#include <functional>  // For std::hash
#include <string>      // For std::to_string

class Container {
private:
    char id;         // Container ID (e.g., 'A', 'B', etc.)
    int containerCost;  // Movement cost of the container

public:
    // Constructor to initialize values
    Container(char id, int cost) : id(id), containerCost(cost) {}

    // Copy constructor
    Container(const Container& c) : id(c.getId()), containerCost(c.getCost()) {}

    // Getter for cost
    int getCost() const {
        return containerCost;
    }

    // Getter for ID
    char getId() const {
        return id;
    }

    // Override hash function (used for hashing in unordered_map)
    std::size_t hashCode() const {
        return std::hash<char>{}(id);  // Hash based on the ID
    }

    // Override equality operator
    bool operator==(const Container& other) const {
        return id == other.getId();
    }

    // Clone method (equivalent to Java clone)
    Container clone() const {
        return Container(*this);  // Create a new copy using the copy constructor
    }

    // Override toString equivalent in C++
    std::string toString() const {
        return std::string(1, id);  // Return the container ID as a string
    }
};
