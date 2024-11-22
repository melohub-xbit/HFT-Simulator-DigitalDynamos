#include "Order.h"
#include <iostream>

using namespace std;

// Constructor to initialize Order
Order::Order(string id, string type, double price, int quantity)
    : id(id), type(type), price(price), quantity(quantity) {}

// Getter methods
string Order::getId() const {
    return id;
}

string Order::getType() const {
    return type;
}

double Order::getPrice() const {
    return price;
}

int Order::getQuantity() const {
    return quantity;
}

// time_t Order::getDateTime() const {
//     return dateTime;
// }

void Order::setQuantity(int quantity) {
    this->quantity = quantity;
}

// Method to display order details
void Order::displayOrder() const {
    cout << "Order ID: " << id << "\n"
         << "Type: " << type << "\n"
         << "Price: $" << price << "\n"
         << "Quantity: " << quantity << "\n";
        //  << "Date & Time: " << ctime(&dateTime) << endl;
}

string Order::toString() {
    return id + "," + type + "," + to_string(price) + "," + to_string(quantity);
}

