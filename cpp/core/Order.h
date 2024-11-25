#ifndef ORDER_H
#define ORDER_H

#include <string>
#include <ctime>

using namespace std;

class Order {
    private:
        string id;
        string type;
        double price;
        int quantity;

    public:
        // Constructor
        Order(string id, string type, double price, int quantity);

        // Method to display order details
        void displayOrder() ;

        //getter methods
        string getId() ;
        string getType() ;
        double getPrice() ;
        int getQuantity() ;

        void setQuantity(int quantity);
        string toString();

        friend class OrderBook;
        friend class AVLTree;
    };

#endif
