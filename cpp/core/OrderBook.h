#ifndef ORDERBOOK_H
#define ORDERBOOK_H

#include <string>
#include <bits/stdc++.h>
#include "AVLTree.h"

using namespace std;

class OrderBook {
private:
    AVLTree buyOrders;  
    AVLTree sellOrders; 


public:
    AVLTree* getBuyOrders();

    AVLTree* getSellOrders();
    
    void addOrder(string orderID, string type, double price, int quantity);

    void cancelOrder(string orderID);

    double getBestBid();

    double getBestAsk();

    //matching engine looks at buy or sell orders coming in and matches them and executes the trade
    //1. buy order == sell order -> match the order, execute order at buy order
    //2. buy order > sell order -> match the order, and execute at sell order

    //when buy order is added, remove min of sell order from the orderbook
    //when sell order is added, remove buy orders with price equal to or more than the sell order (price-wise, it's the inorder successor of this price in buy orders)

    //this matching is done until the coming order's qty is 0 or if non zero, add to the tree again

    //matching engine must run everytime an order is added to the orderbook

    //match for buy orders
    vector<vector<string>> matchBuyOrder(Order* buyOrder);

    //match for sell orders
    vector<vector<string>> matchSellOrder(Order* sellOrder);
};

#endif 
