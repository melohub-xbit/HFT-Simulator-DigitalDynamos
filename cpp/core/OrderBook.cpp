#include "OrderBook.h"
#include <iostream>
using namespace std;

AVLTree* OrderBook::getBuyOrders() {
    return buyOrders;
}

AVLTree* OrderBook::getSellOrders() {
    return sellOrders;
}

// double OrderBook::getBestBid() {
//     AVLTree* root = this->buyOrders;

//     cout << "hello best bid" << endl;
//     if (root == NULL) {
//         cout << "root null ask" << endl;
//         return 0.0;
//     }
//     //return node with max price in buyOrders
//     cout << "hello best bid 2" << endl;
//     while (root != NULL && (root->right != NULL)) {
//         cout << "hello best bid 3" << endl;
//         root = root->getRight();
//     }
//     cout << "post loop bid" << endl;
//     if (root) cout << "not null bid" << endl;
//     else cout << "null bid" << endl;
//     //print the price
//     if (root != NULL) cout << root->getOrder()->getPrice() << endl;
//     return root->getOrder()->getPrice();
// }

// double OrderBook::getBestAsk() {
//     AVLTree* root = sellOrders;

//     cout << "hello best ask" << endl;
//     if (root == NULL) {
//         cout << "root null ask" << endl;
//         return 0.0;
//     }
//     cout << "hello best ask 2" << endl;

//     while (root != NULL && root->getLeft() != NULL) {
//         cout << "hello best ask 3" << endl;
//         root = root->getLeft();
//     }

//     cout << "post loop ask" << endl;
//     if (root) cout << "not null ask" << endl;
//     else cout << "null ask" << endl;
//     if (root != NULL) cout << root->getOrder()->getPrice() << endl;
//     return root->getOrder()->getPrice();
//     // return 0.32;
// }

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

void OrderBook::addOrder(string orderID, string type, double price, int quantity) {
    Order* newOrder = new Order(orderID, type, price, quantity);
    if (type == "buy") {
        if (buyOrders != nullptr) {
            buyOrders = buyOrders->insert_AVLTree(buyOrders, newOrder);
        }
    } else if (type == "sell") {
        if (sellOrders != nullptr) {
            sellOrders = sellOrders->insert_AVLTree(sellOrders, newOrder);
        }
    }
}


void OrderBook::cancelOrder(string orderID) {
    if (buyOrders == nullptr) {
        return;
    }
    Order dummyOrder = Order(orderID, "", 0, 0);
    buyOrders = buyOrders->delete_AVLTree(buyOrders,&dummyOrder);
    // buyOrders->delete_AVLTree(buyOrders->findAVLTreeEnabler(dummyOrder)->order);
}

// vector<vector<string> > OrderBook::matchBuyOrder(string orderID, string type, double price, int quantity) {
//     // Implementation for matching buy orders

//     Order* buyOrder = new Order(orderID, type, price, quantity);
//     vector<vector<string> > matchedOrders;

//     //find min price among the sell orders
//     AVLTree* minTree = sellOrders->findMin(sellOrders);
//     if (minTree == nullptr) {
//         // Handle null case
//         return matchedOrders;
//     }
//     Order* minNode = minTree->getOrder();

//     //if min price is more than buy order, then return
//     if ((minNode != NULL && minNode->getPrice() > buyOrder->getPrice()) || minNode == NULL) {
//         //buy order is not matched, so add to the buy order book
//         cout << "buy order not matched" << endl;
//         if (buyOrder->getQuantity() > 0) {
//             buyOrders = buyOrders->insert_AVLTree(buyOrders,buyOrder);
//         }
//         return matchedOrders;
//     }

//     //if min price is less than buy order, then match the order, and track all orders with the same price
//     while (minNode != NULL && minNode->getPrice() <= buyOrder->getPrice()) {
//         if (minNode->getQuantity() >= buyOrder->getQuantity()) {
//             //subtract quantity of buy order from sell order, and delete the sell order if quantity 0
//             vector<string> temp;
//             Order tempOrder = Order(minNode->getId(),minNode->getType(),minNode->getPrice(),buyOrder->getQuantity());
//             temp.push_back(tempOrder.toString());
//             temp.push_back(buyOrder->toString()); 
//             matchedOrders.push_back(temp);

//             cout << "matched orders in buy:" << endl;
//             cout << temp[0] << " " << temp[1]<< endl;
            
//             minNode->setQuantity(minNode->getQuantity() - buyOrder->getQuantity());
//             if (minNode->getQuantity() == 0) {
//                 //delete the sell order
//                 cout << "delete sell order" << endl;

//                 sellOrders = sellOrders->delete_AVLTree(sellOrders,minNode);
//             }
//             //if this is the case, no need to do anything else with the buy order, the trade is executed
//             //Sell order details are added to a vector
            
//             return matchedOrders;
//         }
//         else if (minNode->getQuantity() < buyOrder->getQuantity()) {
//             //delete that sell order, and update the quantity of the buy order
//             vector<string> temp;
//             Order tempOrder = Order(minNode->getId(),minNode->getType(),minNode->getPrice(),buyOrder->getQuantity());
//             temp.push_back(tempOrder.toString());
//             temp.push_back(buyOrder->toString());
//             matchedOrders.push_back(temp);

//             cout << "matched orders (some qty) in buy:" << endl;
//             cout << temp[0] << " " << temp[1] << endl;

//             buyOrder->setQuantity(buyOrder->getQuantity() - minNode->getQuantity());
//             sellOrders = sellOrders->delete_AVLTree(sellOrders,minNode);

//             AVLTree* nextMinTree = sellOrders->findMin(sellOrders);
//             if (nextMinTree == nullptr) {
//                 break;  // Exit the while loop if no more orders
//             }
//             minNode = nextMinTree->getOrder();
//         }
//     }

//     if (buyOrder->getQuantity() > 0) {
//         cout << "buy order not matched completely" << endl;
//         //if the buy order quantity is still greater than 0, add the order to the order book
//         buyOrders = buyOrders->insert_AVLTree(buyOrders,buyOrder);
//     }

//     return matchedOrders;
// }

// vector<vector<string> > OrderBook::matchSellOrder(string orderID, string type, double price, int quantity) {
//     // Implementation for matching sell orders

//     //for sell order, all the buy orders with price equal to or more than the sell order (price-wise, it's the inorder successor of this price in buy orders)

//     Order* sellOrder = new Order(orderID, type, price, quantity);
    
//     vector<vector<string> > matchedOrders;
//     //find the min price of buy orders >= sell order's price
//     AVLTree* node = buyOrders->findJustGreater(*sellOrder, buyOrders);
//     if (node == nullptr) {
//         // Handle null case
//         if (sellOrder->getQuantity() > 0) {
//             sellOrders = sellOrders->insert_AVLTree(sellOrders, sellOrder);
//         }
//         return matchedOrders;
//     }
//     Order* minNode = node->getOrder();

//     if (minNode == NULL) {
//         cout << "sell order not matched" << endl;
//         //if no buy order with price >= sell order's price, then return
//         if (sellOrder->getQuantity() > 0) {
//             sellOrders = sellOrders->insert_AVLTree(sellOrders,sellOrder);
//         }
//         return matchedOrders;
//     }

//     while (minNode != NULL && minNode->getPrice() >= sellOrder->getPrice()) {
//         if (minNode->getQuantity() >= sellOrder->getQuantity()) {
//             //subtract quantity of sell order from buy order, and delete the buy order if quantity 0
//             vector<string> temp;
//             Order tempOrder = Order(minNode->getId(),minNode->getType(),minNode->getPrice(),sellOrder->getQuantity());
//             temp.push_back(tempOrder.toString());
//             temp.push_back(sellOrder->toString()); 
//             matchedOrders.push_back(temp);

//             cout << "matched orders in sell:" << endl;
//             cout << temp[0] << " " << temp[1] << endl;
            

//             minNode->setQuantity(minNode->getQuantity() - sellOrder->getQuantity());
//             if (minNode->getQuantity() == 0) {
//                 cout << "delete buy order" << endl;
//                 buyOrders = buyOrders->delete_AVLTree(buyOrders,minNode);
//             }
//             //if this is the case, no need to do anything else with the sell order, the trade is executed
//             return matchedOrders;
//         }
//         else if (minNode->getQuantity() < sellOrder->getQuantity()) {
//             //delete that buy order, and update the quantity of the sell order
//             vector<string> temp;
//             Order tempOrder = Order(minNode->getId(),minNode->getType(),minNode->getPrice(),sellOrder->getQuantity());
//             temp.push_back(tempOrder.toString());
//             temp.push_back(sellOrder->toString()); 
//             matchedOrders.push_back(temp);

//             cout << "matched orders (some qty) in sell:" << endl;
//             cout << temp[0] << " " << temp[1] << endl;

//             sellOrder->setQuantity(sellOrder->getQuantity() - minNode->getQuantity());
//             buyOrders = buyOrders->delete_AVLTree(buyOrders,minNode);
//             AVLTree* nextNode = buyOrders->findJustGreater(*sellOrder, buyOrders);
//             if (nextNode == nullptr) {
//                 break;  // Exit the while loop if no more matching orders
//             }
//             minNode = nextNode->getOrder();
//         }
//     }

//     if (sellOrder->getQuantity() > 0) {
//         cout << "sell order not matched completely" << endl;
//         //if the sell order quantity is still greater than 0, add the order to the order book
//         sellOrders = sellOrders->insert_AVLTree(sellOrders,sellOrder);
        
//     }

//     return matchedOrders;
// }



vector<vector<string>> OrderBook::matchBuyOrder(string orderID, string type, double price, int quantity) {
    Order* buyOrder = new Order(orderID, type, price, quantity);
    vector<vector<string>> matchedOrders;

    AVLTree* minTree = sellOrders->findMin(sellOrders);
    if (minTree == nullptr) {
        return matchedOrders;
    }
    Order* minNode = minTree->getOrder();

    if ((minNode != NULL && minNode->getPrice() > buyOrder->getPrice()) || minNode == NULL) {
        if (buyOrder->getQuantity() > 0) {
            buyOrders = buyOrders->insert_AVLTree(buyOrders, buyOrder);
        }
        return matchedOrders;
    }

    while (minNode != NULL && minNode->getPrice() <= buyOrder->getPrice()) {
        int fillQuantity = min(minNode->getQuantity(), buyOrder->getQuantity());
        double executionPrice = minNode->getPrice(); // Execute at seller's price

        vector<string> temp;
        Order tempOrder = Order(minNode->getId(), minNode->getType(), executionPrice, fillQuantity);
        Order executedBuyOrder = Order(buyOrder->getId(), buyOrder->getType(), executionPrice, fillQuantity);
        
        temp.push_back(executedBuyOrder.toString());
        temp.push_back(tempOrder.toString());
        matchedOrders.push_back(temp);

        cout << "Matched orders in buy:" << endl;
        cout << "Sell: " << temp[0] << " Buy: " << temp[1] << endl;
        
        minNode->setQuantity(minNode->getQuantity() - fillQuantity);
        buyOrder->setQuantity(buyOrder->getQuantity() - fillQuantity);

        if (minNode->getQuantity() == 0) {
            sellOrders = sellOrders->delete_AVLTree(sellOrders, minNode);
            AVLTree* nextMinTree = sellOrders->findMin(sellOrders);
            if (nextMinTree == nullptr) break;
            minNode = nextMinTree->getOrder();
        }

        if (buyOrder->getQuantity() == 0) break;
    }

    if (buyOrder->getQuantity() > 0) {
        buyOrders = buyOrders->insert_AVLTree(buyOrders, buyOrder);
    }

    return matchedOrders;
}

vector<vector<string>> OrderBook::matchSellOrder(string orderID, string type, double price, int quantity) {
    Order* sellOrder = new Order(orderID, type, price, quantity);
    vector<vector<string>> matchedOrders;

    AVLTree* maxTree = buyOrders->findMax(buyOrders);
    if (maxTree == nullptr) {
        if (sellOrder->getQuantity() > 0) {
            sellOrders = sellOrders->insert_AVLTree(sellOrders, sellOrder);
        }
        return matchedOrders;
    }
    Order* maxNode = maxTree->getOrder();

    if (maxNode == NULL || maxNode->getPrice() < sellOrder->getPrice()) {
        if (sellOrder->getQuantity() > 0) {
            sellOrders = sellOrders->insert_AVLTree(sellOrders, sellOrder);
        }
        return matchedOrders;
    }

    while (maxNode != NULL && maxNode->getPrice() >= sellOrder->getPrice()) {
        int fillQuantity = min(maxNode->getQuantity(), sellOrder->getQuantity());
        double executionPrice = maxNode->getPrice();

        vector<string> temp;
        Order tempOrder = Order(maxNode->getId(), maxNode->getType(), executionPrice, fillQuantity);
        Order executedSellOrder = Order(sellOrder->getId(), sellOrder->getType(), executionPrice, fillQuantity);
        
        temp.push_back(tempOrder.toString());
        temp.push_back(executedSellOrder.toString());
        matchedOrders.push_back(temp);

        cout << "Matched orders in sell:" << endl;
        cout << "Buy: " << temp[0] << " Sell: " << temp[1] << endl;
        
        maxNode->setQuantity(maxNode->getQuantity() - fillQuantity);
        sellOrder->setQuantity(sellOrder->getQuantity() - fillQuantity);

        if (maxNode->getQuantity() == 0) {
            buyOrders = buyOrders->delete_AVLTree(buyOrders, maxNode);
            AVLTree* nextMaxTree = buyOrders->findMax(buyOrders);
            if (nextMaxTree == nullptr) break;
            maxNode = nextMaxTree->getOrder();
        }

        if (sellOrder->getQuantity() == 0) break;
    }

    if (sellOrder->getQuantity() > 0) {
        sellOrders = sellOrders->insert_AVLTree(sellOrders, sellOrder);
    }

    return matchedOrders;
}



