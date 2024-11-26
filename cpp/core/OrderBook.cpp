#include "OrderBook.h"
#include <iostream>
using namespace std;

AVLTree* OrderBook::getBuyOrders() {
    return buyOrders;
}

AVLTree* OrderBook::getSellOrders() {
    return sellOrders;
}

double OrderBook::getBestBid() {
    AVLTree* root = this->buyOrders;

    cout << "hello best bid" << endl;
    if (root == NULL) {
        cout << "root null ask" << endl;
        return 0.0;
    }
    //return node with max price in buyOrders
    cout << "hello best bid 2" << endl;
    while (root != NULL && (root->right != NULL)) {
        cout << "hello best bid 3" << endl;
        root = root->getRight();
    }
    cout << "post loop bid" << endl;
    if (root) cout << "not null bid" << endl;
    else cout << "null bid" << endl;
    //print the price
    if (root != NULL) cout << root->getOrder()->getPrice() << endl;
    return root->getOrder()->getPrice();
}

double OrderBook::getBestAsk() {
    AVLTree* root = sellOrders;

    cout << "hello best ask" << endl;
    if (root == NULL) {
        cout << "root null ask" << endl;
        return 0.0;
    }
    cout << "hello best ask 2" << endl;

    while (root != NULL && root->getLeft() != NULL) {
        cout << "hello best ask 3" << endl;
        root = root->getLeft();
    }

    cout << "post loop ask" << endl;
    if (root) cout << "not null ask" << endl;
    else cout << "null ask" << endl;
    if (root != NULL) cout << root->getOrder()->getPrice() << endl;
    return root->getOrder()->getPrice();
    // return 0.32;
}

/*
double OrderBook::getBestBid() {
    AVLTree* root = this->buyOrders;

    if (root == NULL) {
        cout << "root null bid" << endl;
        return 0.0;
    }

    while (root != NULL && root->getRight() != NULL) {
        root = root->getRight();
    }

    if (root == NULL || root->getOrder() == NULL) {
        cout << "Error: Null root or order in bid" << endl;
        return 0.0;
    }

    double price = root->getOrder()->getPrice();
    cout << "Best bid price: " << price << endl;
    return price;
}

double OrderBook::getBestAsk() {
    AVLTree* root = this->sellOrders;

    if (root == NULL) {
        cout << "root null ask" << endl;
        return 0.0;
    }

    while (root != NULL && root->getLeft() != NULL) {
        root = root->getLeft();
    }

    if (root == NULL || root->getOrder() == NULL) {
        cout << "Error: Null root or order in ask" << endl;
        return 0.0;
    }

    double price = root->getOrder()->getPrice();
    cout << "Best ask price: " << price << endl;
    return price;
}
*/

void OrderBook::addOrder(string orderID, string type, double price, int quantity) {
    Order newOrder = Order(orderID, type, price, quantity);
    cout<<type<<endl;
    if (type == "buy") {
        buyOrders = buyOrders->insert_AVLTree(buyOrders,&newOrder);
    } else if (type == "sell") {
        sellOrders = sellOrders->insert_AVLTree(sellOrders,&newOrder);
    }
}

void OrderBook::cancelOrder(string orderID) {
    Order dummyOrder = Order(orderID, "", 0, 0);
    buyOrders = buyOrders->delete_AVLTree(buyOrders,&dummyOrder);
    // buyOrders->delete_AVLTree(buyOrders->findAVLTreeEnabler(dummyOrder)->order);
}

vector<vector<string> > OrderBook::matchBuyOrder(string orderID, string type, double price, int quantity) {
    // Implementation for matching buy orders

    Order* buyOrder = new Order(orderID, type, price, quantity);
    vector<vector<string> > matchedOrders;

    //find min price among the sell orders
    Order* minNode = sellOrders->findMin(sellOrders)->getOrder();

    //if min price is more than buy order, then return
    if ((minNode != NULL && minNode->getPrice() > buyOrder->getPrice()) || minNode == NULL) {
        //buy order is not matched, so add to the buy order book
        if (buyOrder->getQuantity() > 0) {
            buyOrders = buyOrders->insert_AVLTree(buyOrders,buyOrder);
        }
        return matchedOrders;
    }

    //if min price is less than buy order, then match the order, and track all orders with the same price
    while (minNode != NULL && minNode->getPrice() <= buyOrder->getPrice()) {
        if (minNode->getQuantity() >= buyOrder->getQuantity()) {
            //subtract quantity of buy order from sell order, and delete the sell order if quantity 0
            vector<string> temp;
            temp.push_back(minNode->toString());
            temp.push_back(buyOrder->toString()); 
            matchedOrders.push_back(temp);
            
            minNode->setQuantity(minNode->getQuantity() - buyOrder->getQuantity());
            if (minNode->getQuantity() == 0) {
                sellOrders = sellOrders->delete_AVLTree(sellOrders,minNode);
            }
            //if this is the case, no need to do anything else with the buy order, the trade is executed
            //Sell order details are added to a vector
            
            return matchedOrders;
        }
        else if (minNode->getQuantity() < buyOrder->getQuantity()) {
            //delete that sell order, and update the quantity of the buy order
            vector<string> temp;
            temp.push_back(minNode->toString());
            temp.push_back(buyOrder->toString());
            matchedOrders.push_back(temp);

            buyOrder->setQuantity(buyOrder->getQuantity() - minNode->getQuantity());
            sellOrders = sellOrders->delete_AVLTree(sellOrders,minNode);

            minNode = sellOrders->findMin(sellOrders)->getOrder();
        }
    }

    if (buyOrder->getQuantity() > 0) {
        //if the buy order quantity is still greater than 0, add the order to the order book
        buyOrders = buyOrders->insert_AVLTree(buyOrders,buyOrder);
    }

    return matchedOrders;
}

vector<vector<string> > OrderBook::matchSellOrder(string orderID, string type, double price, int quantity) {
    // Implementation for matching sell orders

    //for sell order, all the buy orders with price equal to or more than the sell order (price-wise, it's the inorder successor of this price in buy orders)

    Order* sellOrder = new Order(orderID, type, price, quantity);
    
    vector<vector<string> > matchedOrders;
    //find the min price of buy orders >= sell order's price
    Order* minNode = buyOrders->findJustGreater(*sellOrder, buyOrders)->getOrder();

    if (minNode == NULL) {
        //if no buy order with price >= sell order's price, then return
        if (sellOrder->getQuantity() > 0) {
            sellOrders = sellOrders->insert_AVLTree(buyOrders,sellOrder);
        }
        return matchedOrders;
    }

    while (minNode != NULL && minNode->getPrice() >= sellOrder->getPrice()) {
        if (minNode->getQuantity() >= sellOrder->getQuantity()) {
            //subtract quantity of sell order from buy order, and delete the buy order if quantity 0
            vector<string> temp;
            temp.push_back(minNode->toString());
            temp.push_back(sellOrder->toString()); 
            matchedOrders.push_back(temp);

            minNode->setQuantity(minNode->getQuantity() - sellOrder->getQuantity());
            if (minNode->getQuantity() == 0) {
                sellOrders = sellOrders->delete_AVLTree(sellOrders,minNode);
            }
            //if this is the case, no need to do anything else with the sell order, the trade is executed
            return matchedOrders;
        }
        else if (minNode->getQuantity() < sellOrder->getQuantity()) {
            //delete that buy order, and update the quantity of the sell order
            vector<string> temp;
            temp.push_back(minNode->toString());
            temp.push_back(sellOrder->toString()); 
            matchedOrders.push_back(temp);

            sellOrder->setQuantity(sellOrder->getQuantity() - minNode->getQuantity());
            buyOrders = buyOrders->delete_AVLTree(sellOrders,minNode);
            minNode = buyOrders->findJustGreater(*sellOrder, buyOrders)->getOrder();
        }
    }

    if (sellOrder->getQuantity() > 0) {
        //if the sell order quantity is still greater than 0, add the order to the order book
        sellOrders = sellOrders->insert_AVLTree(buyOrders,sellOrder);
        
    }

    return matchedOrders;
}


