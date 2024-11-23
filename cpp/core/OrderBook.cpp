#include "OrderBook.h"
using namespace std;

AVLTree* OrderBook::getBuyOrders() {
    return &buyOrders;
}

AVLTree* OrderBook::getSellOrders() {
    return &sellOrders;
}

double OrderBook::getBestBid() {
    // return buyOrders.findMax()->order.getPrice();
    return 0.32;
}

double OrderBook::getBestAsk() {
    // return sellOrders.findMin()->order.getPrice();
    return 0.32;
}

void OrderBook::addOrder(string orderID, string type, double price, int quantity) {
    Order newOrder = Order(orderID, type, price, quantity);

    if (type == "buy") {
        buyOrders.insert(newOrder);
    } else if (type == "sell") {
        sellOrders.insert(newOrder);
    }
}

void OrderBook::cancelOrder(string orderID) {
    Order dummyOrder = Order(orderID, "", 0, 0);
    buyOrders.remove(buyOrders.findNodeEnabler(dummyOrder)->order);
}

vector<vector<string>> OrderBook::matchBuyOrder(string orderID, string type, double price, int quantity) {
    // Implementation for matching buy orders

    Order* buyOrder = new Order(orderID, type, price, quantity);
    vector<vector<string>> matchedOrders;

    //find min price among the sell orders
    Order* minNode = &(sellOrders.findMin()->order);

    //if min price is more than buy order, then return
    if ((minNode != NULL && minNode->getPrice() > buyOrder->getPrice()) || minNode == NULL) {
        //buy order is not matched, so add to the buy order book
        if (buyOrder->getQuantity() > 0) {
            buyOrders.insert(*buyOrder);
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
                sellOrders.remove(*minNode);
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
            sellOrders.remove(*minNode);

            minNode = &(sellOrders.findMin()->order);
        }
    }

    if (buyOrder->getQuantity() > 0) {
        //if the buy order quantity is still greater than 0, add the order to the order book
        buyOrders.insert(*buyOrder);
    }

    return matchedOrders;
}

vector<vector<string>> OrderBook::matchSellOrder(string orderID, string type, double price, int quantity) {
    // Implementation for matching sell orders

    //for sell order, all the buy orders with price equal to or more than the sell order (price-wise, it's the inorder successor of this price in buy orders)

    Order* sellOrder = new Order(orderID, type, price, quantity);
    
    vector<vector<string>> matchedOrders;
    //find the min price of buy orders >= sell order's price
    Order* minNode = &(buyOrders.findJustGreater(*sellOrder)->order);

    if ((minNode == NULL)) {
        //if no buy order with price >= sell order's price, then return
        if (sellOrder->getQuantity() > 0) {
            sellOrders.insert(*sellOrder);
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
                buyOrders.remove(*minNode);
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
            buyOrders.remove(*minNode);
            minNode = &(buyOrders.findJustGreater(*sellOrder)->order);
        }
    }

    if (sellOrder->getQuantity() > 0) {
        //if the sell order quantity is still greater than 0, add the order to the order book
        sellOrders.insert(*sellOrder);
    }

    return matchedOrders;
}


