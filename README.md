# Introduction
## Team Details
### Team name: DigitalDynamos

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
