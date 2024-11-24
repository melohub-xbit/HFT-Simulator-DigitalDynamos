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
    AVLTree* y = z->left;
    AVLTree* t3 = y->right;
    z->left = t3;
    y->right = z;
    set_height(z);
    set_height(y);
    return y;
}

AVLTree* AVLTree::left_rotate(AVLTree* z) {
    AVLTree* y = z->right;
    AVLTree* t2 = y->left;
    z->right = t2;
    y->left = z;
    set_height(z);
    set_height(y);
    return y;
}

AVLTree* AVLTree::left_right_rotate(AVLTree* z) {
    AVLTree* y = z->left;
    z->left = left_rotate(y);
    return right_rotate(z);
}

AVLTree* AVLTree::right_left_rotate(AVLTree* z) {
    z->right = right_rotate(z->right);
    return left_rotate(z);
}

AVLTree* AVLTree::inorder_processor(AVLTree* n) {
    AVLTree* current = n;
    while (current->right != nullptr) {
        current = current->right;
    }
    return current;
}


AVLTree* AVLTree::insert_AVLTree(AVLTree* n, Order* o) {
    if(n == nullptr) {
        return new AVLTree(o);
    }
    
    if(n->order == nullptr) {
        cout<<"1"<<endl;
        return new AVLTree(o);
    }
    
    if (o->getPrice() <= n->order->getPrice()) {
        cout<<"2"<<endl;
        n->left = insert_AVLTree(n->left, o);
    } 
    else if(o->getPrice() > n->order->getPrice()) {
        cout<<"3"<<endl;
        n->right = insert_AVLTree(n->right, o);
    }
    else return (n);
    cout<<"4"<<endl;
    set_height(n);
    cout<<"5"<<endl;
    int balance = get_balance(n);
    cout<<"6"<<endl;
    if(balance>1){
        cout<<"7"<<endl;
        if(o->getPrice() < n->left->order->getPrice()){
            cout<<"8"<<endl;
            return right_rotate(n);
        }
        else{
            cout<<"9"<<endl;
            return left_right_rotate(n);
        }
    }
    cout<<"9.5"<<endl;
    if(balance<-1){
        cout<<"10"<<endl;
        if(o->getPrice() > n->right->order->getPrice()){
            cout<<"11"<<endl;
            return left_rotate(n);
        }
        else{
            cout<<"12"<<endl;
            return right_left_rotate(n);
        }
    }
    cout<<"13"<<endl;
    return n;

}

AVLTree* AVLTree::delete_AVLTree(AVLTree* root, Order* o) {
    if (!root)
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
    AVLTree* current = root;
    while (current && current->left)
        current = current->left;
    return current;
}

AVLTree* AVLTree::findAVLTree(Order dummyOrder,AVLTree* root) {
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
    AVLTree* current = root;
    return findAVLTree(dummyOrder,current);
    
}

AVLTree* AVLTree::findJustGreater(Order refOrder, AVLTree* root) {
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

