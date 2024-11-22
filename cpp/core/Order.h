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
        // time_t dateTime;

    public:
        // Constructor
        Order(string id, string type, double price, int quantity);

        // Method to display order details
        void displayOrder() const;

        //getter methods
        string getId() const;
        string getType() const;
        double getPrice() const;
        int getQuantity() const;
        // time_t getDateTime() const;

        void setQuantity(int quantity);
        string toString();
    };

#endif
