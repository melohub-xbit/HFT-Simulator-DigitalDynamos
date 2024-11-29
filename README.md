[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://www.oracle.com/java/)
[![C++](https://img.shields.io/badge/C%2B%2B-17-00599C.svg)](https://en.cppreference.com/w/cpp/17)
[![Algorithms](https://img.shields.io/badge/Algorithms-AVL%20Tree-brightgreen)](https://github.com/melohub-xbit/HFT-Simulator-DigitalDynamos)
[![OOP](https://img.shields.io/badge/Paradigm-OOP-blue)](https://github.com/melohub-xbit/HFT-Simulator-DigitalDynamos)
[![JNI](https://img.shields.io/badge/Bridge-JNI-red)](https://github.com/melohub-xbit/HFT-Simulator-DigitalDynamos)
[![Swing](https://img.shields.io/badge/UI-Swing-orange)](https://github.com/melohub-xbit/HFT-Simulator-DigitalDynamos)


# Introduction
## Team Details
### Team name: DigitalDynamos

### Team Members:
- [Aaryan Antala](https://github.com/AaryanAntala)
- [Chaitya Shah](https://github.com/CShah44)
- [Hitanshu Seth](https://github.com/Hitanshu078)
- [Ramya Parsania](https://github.com/RAMYA-PARSANIA)
- [Satyam Ambi](https://github.com/Satyam137)
- [V Krishna Sai](https://github.com/melohub-xbit/)
## Project Overview:
This project aims to simulate how an HFT (High-Frequency Trading) firm executes its strategies to profit through the stock exchanges. The project also simulates how a stock exchange manages its order book to match and execute orders efficiently using advanced data structures and algorithms to minimize the latency.



### Purpose:
- The project offers a realistic simulation of an HFT system and order book management, helping users understand how the entire system functions.
- The project uses strategies such as arbitrage, market making, etc., and advanced algorithms and data structures to reduce the latency of managing the order book.

### High-Level Functionality:
- Performance analysis: Provides insights into the profits and losses of the firm.
- Simulating strategies of HFT firms. 
- Process incoming buy/sell orders and match them based on price and quantity.
## Scope:
### Included: 
- Live management of order books that keep track of buy/sell prices.
- Simulation of basic HFT strategies.
- Implementation of the classes involved in Java, and C++ implementation of the algorithms being used by the HFT firm.
- Performance analysis tools that simulate different kinds of market conditions.
### Not included:
- Real-time data from actual financial markets.
- Execution of real trades or financial transactions.

# Objectives
The primary objectives of this project are:
## Implement high-performance trading algorithms in C++:
- Establish and initialize data streams to continuously receive order book data from the simulated stock exchange network.
- Develop and implement efficient data structures to determine the most optimal orders for analysis.
- Based on user-defined actions, utilize the appropriate data objects and execute algorithms to identify the most optimal trading options.
## Build a backend and user interface in Java:
- Users can also place orders for buy and sell.
- Users can see their profit/loss and the profit/loss of the HFT.
- Plotting graphs for various data like profit/loss.
## Use multithreading to let the HFT use multiple strategies when trading to maximize profit
- Because there are multiple strategies implemented for the HFT, it is more optimal for the HFT to apply the appropriate strategy to maximize profit.
- Based on the orderbook data, the HFT would use the strategy most fit for the scenario, or even use multiple strategies.
# System Overview
## Technical Specifications:
- Frontend: Java (UI)
- Backend: Java (Server-side logic): For implementing HFT strategies and to simulate different cases
- Core Logic: C++ (Trading algorithms and network monitoring): For order matching and efficient data structures
## Input/Output Requirements:
### Input:
- Buy and sell orders placed by the HFT system.  
- Order details: Order ID, Order type (buy/sell), price and quantity
### Output: 
- Data of Matched orders with executed price, quantity, and timestamp.
- Logs showing how the HFT strategies perform under different market conditions.
- Detailed report on the profits and number of matched orders on the performance of the HFT strategies
# Functional Requirements
## Detailed Features:
### Order Book Management: 
- The system will maintain an order book in real-time containing current bid and ask prices while managing new orders, canceling and modifying existing orders, and sorting current orders on priority (the highest bid will match to the lowest ask).

### Order Matching Engine: 
- Orders are matched according to prices (If a bidding price of an order matches or exceeds the ask price the trade is executed) and quantity then removes the order from the order book and logs the trade.

### Arbitrage Strategy: 
- The arbitrage strategy involves exploiting price differences of the same asset across different markets. HFT systems buy the asset at a lower price in one market and sell it at a higher price in another. Thus profiting from the discrepancy.

### Market-Making Strategy: 
- The market-making strategy involves continuously placing buy and sell limit orders on a stock to profit from the bid-ask spread. HFT earns small, consistent profits by capturing the difference between the buying and selling prices.

## Use Cases:
### Arbitrage Trading: 
- Suppose the HFT system detects a price discrepancy between two stock exchanges. For example:
Stock exchange A lists a stock at ₹10.50, and exchange B lists the stock at ₹10.55. The HFT places a buy order at ₹10.50 in exchange A and a sell order for the same stock in exchange B at ₹10.55. Thus, the order-matching engine executes both trades, profiting the HFT from the arbitrage opportunity.
### Market Making: 
- The HFT places a buy order slightly below the current market price and places a sell order slightly above the current market price.
As the market prices change the system adjusts these orders to stay close to the market prices and then logs all executed trades.

echo '
## Instructions to Run

##### The zip file contains the pre-compiled Java and C++ files, so in order to run the program, just run the following commands from the project root directory (after extracting the zip file, in the root directory of the project):
```java -Djava.library.path="{your_path_to_root_directory_of_project}" -cp java HFTSimulation```
---

##### ```replace the {your_path_to_root_directory_of_project} with the path to the root directory of the project```

##### If any changes are made or the above instructions don't work, please follow the instructions below to run the application:

### Linux/MacOS Systems:
1. From the project root directory, run:
```g++ -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/darwin" -shared -o libOrderBookNative.dylib cpp/jni/OrderBookNative.cpp cpp/core/OrderBook.cpp cpp/core/AVLTree.cpp cpp/core/Order.cpp```

2. Navigate to /java folder and compile Java files:
```cd java```
```javac *.java```

3. Start the program:
```java -Djava.library.path=. HFTSimulation```

### Windows Systems:
1. Compile C++ files:
```g++ -c -fPIC -I"C:\Program Files\Java\jdk-23\include" -I"C:\Program Files\Java\jdk-23\include\win32" cpp/core/OrderBook.cpp cpp/core/AVLTree.cpp cpp/core/Order.cpp cpp/jni/OrderBookNative.cpp```

#### ```Make sure to replace the path with the actual path to your Java installation.```

2. Create shared library:
```g++ -shared OrderBook.o AVLTree.o Order.o OrderBookNative.o -o OrderBookNative.dll```

3. Generate JNI headers:
```javac -h . java/exchange/OrderBook.java```

4. Navigate to java directory:
```cd java```

5. Compile Java files:
```javac *.java```

6. Run the program:
```cd ..```
```java -Djava.library.path="{your_path_to_root_directory_of_project}" -cp java HFTSimulation```

#### OR
In java directory:
```java -Djava.library.path=. HFTSimulation```

Note: Replace the Java paths and versions according to your system configuration.
Also, the documentation for the project is also present in the repository.
