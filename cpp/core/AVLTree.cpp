#include "AVLTree.h"
#include <iostream>
#include <algorithm>

//Node constructor
Node::Node(const Order& order) : order(order), left(nullptr), right(nullptr), height(1) {}

//AVLTree constructor
AVLTree::AVLTree() : root(nullptr) {}

//Get height of a node
int AVLTree::height(Node* n) {
    return n ? n->height : 0;
}

//Get balance factor of a node
int AVLTree::getBalance(Node* n) {
    return n ? height(n->left) - height(n->right) : 0;
}

//Right rotation
Node* AVLTree::rightRotate(Node* y) {
    Node* x = y->left;
    Node* T2 = x->right;

    x->right = y;
    y->left = T2;

    y->height = std::max(height(y->left), height(y->right)) + 1;
    x->height = std::max(height(x->left), height(x->right)) + 1;

    return x;
}

//Left rotation
Node* AVLTree::leftRotate(Node* x) {
    Node* y = x->right;
    Node* T2 = y->left;

    y->left = x;
    x->right = T2;

    x->height = std::max(height(x->left), height(x->right)) + 1;
    y->height = std::max(height(y->left), height(y->right)) + 1;

    return y;
}

//Insert a node
Node* AVLTree::insertNode(Node* node, const Order& order) {
    if (!node)
        return new Node(order);

    if (order.getPrice() < node->order.getPrice())
        node->left = insertNode(node->left, order);
    else if (order.getPrice() > node->order.getPrice())
        node->right = insertNode(node->right, order);
    else
        return node;

    node->height = 1 + std::max(height(node->left), height(node->right));

    int balance = getBalance(node);

    if (balance > 1 && order.getPrice() < node->left->order.getPrice())
        return rightRotate(node);
    if (balance < -1 && order.getPrice() > node->right->order.getPrice())
        return leftRotate(node);
    if (balance > 1 && order.getPrice() > node->left->order.getPrice()) {
        node->left = leftRotate(node->left);
        return rightRotate(node);
    }
    if (balance < -1 && order.getPrice() < node->right->order.getPrice()) {
        node->right = rightRotate(node->right);
        return leftRotate(node);
    }

    return node;
}

//Delete a node
Node* AVLTree::deleteNode(Node* root, const Order& order) {
    if (!root)
        return root;

    if (order.getPrice() < root->order.getPrice())
        root->left = deleteNode(root->left, order);
    else if (order.getPrice() > root->order.getPrice())
        root->right = deleteNode(root->right, order);
    else {
        if (!root->left || !root->right) {
            Node* temp = root->left ? root->left : root->right;
            if (!temp) {
                temp = root;
                root = nullptr;
            } else
                *root = *temp;
            delete temp;
        } else {
            Node* temp = inorderProcessor(root->left);
            root->order = temp->order;
            root->left = deleteNode(root->left, temp->order);
        }
    }

    if (!root)
        return root;

    root->height = std::max(height(root->left), height(root->right)) + 1;

    int balance = getBalance(root);

    if (balance > 1 && getBalance(root->left) >= 0)
        return rightRotate(root);
    if (balance > 1 && getBalance(root->left) < 0) {
        root->left = leftRotate(root->left);
        return rightRotate(root);
    }
    if (balance < -1 && getBalance(root->right) <= 0)
        return leftRotate(root);
    if (balance < -1 && getBalance(root->right) > 0) {
        root->right = rightRotate(root->right);
        return leftRotate(root);
    }

    return root;
}

//Inorder processor to find the predecessor
Node* AVLTree::inorderProcessor(Node* node) {
    Node* current = node;
    while (current->right)
        current = current->right;
    return current;
}

//Public insert method
void AVLTree::insert(const Order& order) {
    root = insertNode(root, order);
}

//Public remove method
void AVLTree::remove(const Order& order) {
    root = deleteNode(root, order);
}

//Display AVLTree contents
void AVLTree::display(Node* node) const {
    if (!node)
        return;
    display(node->left);
    std::cout << "Order ID: " << node->order.getId()
              << ", Price: " << node->order.getPrice()
              << ", Quantity: " << node->order.getQuantity()
              << std::endl;
    display(node->right);
}

void AVLTree::display() const {
    display(root);
}

Node* AVLTree::findMin() const {
    Node* current = root;
    while (current && current->left)
        current = current->left;
    return current;
}

Node* AVLTree::findNode(Order dummyOrder,Node* root) {
    Node* current = root;
    if (current) {
        while (current) {
            if (current->order.getId() == dummyOrder.getId()) {
                return current;
            }
            if(current && current->right){
                return(findNode(dummyOrder,current->right));
            }
            if(current && current->left){
                return(findNode(dummyOrder,current->left));
            }
        }
    }
    return nullptr;
}

Node* AVLTree::findNodeEnabler(Order dummyOrder) {
    Node* current = root;
    return findNode(dummyOrder,current);
    
}

Node* AVLTree::findJustGreater(Order refOrder) {
    Node* current = root;
    while (current) {
        if (current->order.getPrice() >= refOrder.getPrice()) {
            if (!current->left || current->left->order.getPrice() < refOrder.getPrice()) {
                return current;
            }
            current = current->left;
        } else {
            current = current->right;
        }
    }
}

