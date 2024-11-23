#ifndef AVLTREE_H
#define AVLTREE_H

#include "Order.h"

class Node {
public:
    Order order;
    Node* left;
    Node* right;
    int height;

    Node(const Order& order); // Constructor
};

class AVLTree {
private:
    Node* root;

    int height(Node* n);
    int getBalance(Node* n);
    Node* rightRotate(Node* y);
    Node* leftRotate(Node* x);
    Node* insertNode(Node* node, const Order& order);
    Node* deleteNode(Node* root, const Order& order);
    Node* inorderProcessor(Node* node);
    void display(Node* node) const;

public:
    AVLTree(); // Constructor

    void insert(Order order);
    void remove(Order order);
    void display() const;
    Node* findMin() const;
    Node* findNode(Order dummyOrder,Node* root);
    Node* findNodeEnabler(Order dummyOrder);
    Node* findJustGreater(Order refOrder);
};

#endif // AVLTREE_H
