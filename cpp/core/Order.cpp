#include "Order.h"
#include <iostream>

using namespace std;

// Constructor to initialize Order
Order::Order(string id, string type, double price, int quantity)
    : id(id), type(type), price(price), quantity(quantity) {}

// Getter methods
string Order::getId()  {
    return id;
}

string Order::getType()  {
    return type;
}

double Order::getPrice()  {
    return price;
}

int Order::getQuantity()  {
    return quantity;
}



void Order::setQuantity(int quantity) {
    this->quantity = quantity;
}

// Method to display order details
void Order::displayOrder()  {
    cout << "Order ID: " << id << "\n"
         << "Type: " << type << "\n"
         << "Price: $" << price << "\n"
         << "Quantity: " << quantity << "\n";
}

string Order::toString() {
    return id + "," + type + "," + to_string(price) + "," + to_string(quantity);
}

