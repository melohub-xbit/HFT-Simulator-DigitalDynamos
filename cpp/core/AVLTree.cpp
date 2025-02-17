#include "AVLTree.h"
#include <iostream>
#include <algorithm>
#include "Order.h"
using namespace std;

//Getter methods
Order* AVLTree::getOrder()  {
    return order;
}
AVLTree* AVLTree::getLeft()  {
    return left;
}
AVLTree* AVLTree::getRight()  {
    return right;
}
int AVLTree::getHeight()  {
    return height;
}

void AVLTree::set_height(AVLTree* n) {
    if (n == nullptr) return;
    if (n->left) set_height(n->left);
    if (n->right) set_height(n->right);
    if (n->left && n->right) {
        n->height = 1 + std::max(n->left->height, n->right->height);
    } else if (n->left) {
        n->height = 1 + n->left->height;
    } else if (n->right) {
        n->height = 1 + n->right->height;
    } else {
        n->height = 0;
    }
}

int AVLTree::get_balance(AVLTree* n) {
    if (n == nullptr) return 0;
    else if(n->left==nullptr && n->right==nullptr) return 0;
    else if(n->left==nullptr && n->right!=nullptr) return -n->right->height;
    else if(n->right==nullptr && n->left!=nullptr) return n->left->height;
    else return (n->left->height - n->right->height);
}

AVLTree* AVLTree::right_rotate(AVLTree* z) {
    if (z == nullptr || z->left == nullptr) return z;
    AVLTree* y = z->left;
    AVLTree* t3 = y->right;
    z->left = t3;
    y->right = z;
    set_height(z);
    set_height(y);
    return y;
}

AVLTree* AVLTree::left_rotate(AVLTree* z) {
    if (z == nullptr || z->right == nullptr) return z;
    AVLTree* y = z->right;
    AVLTree* t2 = y->left;
    z->right = t2;
    y->left = z;
    set_height(z);
    set_height(y);
    return y;
}

AVLTree* AVLTree::left_right_rotate(AVLTree* z) {
    if (z == nullptr || z->left == nullptr) return z;
    AVLTree* y = z->left;
    z->left = left_rotate(y);
    return right_rotate(z);
}

AVLTree* AVLTree::right_left_rotate(AVLTree* z) {
    if (z == nullptr || z->right == nullptr) return z;
    z->right = right_rotate(z->right);
    return left_rotate(z);
}

AVLTree* AVLTree::inorder_processor(AVLTree* n) {
    if (n == nullptr) return nullptr;
    AVLTree* current = n;
    while (current->right != nullptr) {
        current = current->right;
    }
    return current;
}


AVLTree* AVLTree::insert_AVLTree(AVLTree* n, Order* o) {
    if (o == nullptr) return n;
    if(n == nullptr) {
        return new AVLTree(o);
    }
    
    if(n->order == nullptr) {
        return new AVLTree(o);
    }
    
    if (o->getPrice() <= n->order->getPrice()) {
        n->left = insert_AVLTree(n->left, o);
    } 
    else if(o->getPrice() > n->order->getPrice()) {
        n->right = insert_AVLTree(n->right, o);
    }
    else return (n);
    set_height(n);
    int balance = get_balance(n);
    if(balance>1){
        if(o->getPrice() < n->left->order->getPrice()){
            return right_rotate(n);
        }
        else{
            return left_right_rotate(n);
        }
    }

    if(balance<-1){
        if(o->getPrice() > n->right->order->getPrice()){
            return left_rotate(n);
        }
        else{
            return right_left_rotate(n);
        }
    }
    return n;

}

AVLTree* AVLTree::delete_AVLTree(AVLTree* root, Order* o) {
    if (!root || !o)
        return root;

    if (o->getPrice() < root->order->getPrice()){
        root->left = delete_AVLTree(root->left, o);
    }

    else if (o->getPrice() > root->order->getPrice()){
        root->right = delete_AVLTree(root->right, o);
    }

    else {
        if (!root->left || !root->right) {
            AVLTree* temp = root->left ? root->left : root->right;
            if (!temp) {
                temp = root;
                root = nullptr;
            } 
            else{
                *root = *temp;
                delete temp;
            }
        } 
        else {
            AVLTree* temp = inorder_processor(root->left);
            root->order = temp->order;
            root->left = delete_AVLTree(root->left, temp->order);
        }
    }

    if(root==nullptr){
        return root;
    }
    set_height(root);
    int balance = get_balance(root);
    if(balance>1){
        if(get_balance(root->left)>=0){
            return right_rotate(root);
        }
        else{
            return left_right_rotate(root);
        }
    }
    else if(balance<-1){
        if(get_balance(root->right)<=0){
            return left_rotate(root);
        }
        else{
            return right_left_rotate(root);
        }
    }
    return root;
}

AVLTree* AVLTree::findMin(AVLTree* root)  {
    if (!root) return nullptr;
    AVLTree* current = root;
    while (current && current->left)
        current = current->left;
    return current;
}

AVLTree* AVLTree::findAVLTree(Order dummyOrder,AVLTree* root) {
    if (!root || !root->order) return nullptr;
    AVLTree* current = root;
    if (current) {
        while (current) {
            if (current->order->getId() == dummyOrder.getId()) {
                return current;
            }
            if(current && current->right){
                return(findAVLTree(dummyOrder,current->right));
            }
            if(current && current->left){
                return(findAVLTree(dummyOrder,current->left));
            }
        }
    }
    return nullptr;
}

AVLTree* AVLTree::findAVLTreeEnabler(Order dummyOrder, AVLTree* root) {
    if (!root) return nullptr;
    AVLTree* current = root;
    return findAVLTree(dummyOrder,current);
    
}

AVLTree* AVLTree::findMax(AVLTree* root) {
    if (root == NULL) {
        return NULL;
    }
    
    while (root->getRight() != NULL) {
        root = root->getRight();
    }
    
    return root;
}


AVLTree* AVLTree::findJustGreater(Order refOrder, AVLTree* root) {
    if (!root || !root->order) return nullptr;
    AVLTree* current = root;
    while (current) {
        if (current->order->getPrice() >= refOrder.getPrice()) {
            if (!current->left || current->left->order->getPrice() < refOrder.getPrice()) {
                return current;
            }
            current = current->left;
        } else {
            current = current->right;
        }
    }
    return nullptr;
}