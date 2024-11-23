#ifndef AVLTREE_H
#define AVLTREE_H

#include "Order.h"

class AVLTree {
    private:
        Order* order;
        AVLTree* left;
        AVLTree* right;
        int height;

    public:
        AVLTree( Order* order){
            this->order=order;
        }

        //Getter methods
        Order* getOrder() ;
        AVLTree* getLeft() ;
        AVLTree* getRight() ;
        int getHeight() ;

        void set_height(AVLTree* n);
        int get_balance(AVLTree* n);
        AVLTree* right_rotate(AVLTree* z);
        AVLTree* left_rotate(AVLTree* z);
        AVLTree* left_right_rotate(AVLTree* z);
        AVLTree* right_left_rotate(AVLTree* z);
        AVLTree* insert_AVLTree(AVLTree* n, Order* o);
        AVLTree* delete_AVLTree(AVLTree* n, Order* o);

        AVLTree* findMin() ;
        AVLTree* findAVLTree(Order dummyOrder,AVLTree* root);
        AVLTree* findAVLTreeEnabler(Order dummyOrder);
        AVLTree* findJustGreater(Order refOrder);

};


#endif // AVLTREE_H
