package strategy;
import java.io.PrintStream;

import exchange.Exchange;
import rml.RiskManagement;

public class ArbitrageStrategy implements Runnable {
    private Exchange exchange1;
    private Exchange exchange2;
    // to simulate transaction cost that exchange charges for trade
    // it is kept as fixed for this simulation
    private double transactionCost;
    private int tradeSize;
    private String[][] lastBuyOrders;
    private String[][] lastSellOrders;
    private RiskManagement rm;
    private double profitArb;
    private PrintStream output;

    public ArbitrageStrategy(Exchange e1, Exchange e2, double transactionCost, int tradeSize, RiskManagement rm, PrintStream output) {
        this.exchange1 = e1;
        this.exchange2 = e2;
        this.transactionCost = transactionCost;
        this.tradeSize = tradeSize;
        this.rm = rm;
        this.profitArb = 0;
        this.output = output;
    }

    public String[][] getLastBuyOrders() {
        return lastBuyOrders;
    }
    
    public String[][] getLastSellOrders() {
        return lastSellOrders;
    }
    
    public double getProfitArb() {
        return profitArb;
    }

    @Override
    public void run() {
        output.println("Hiiii");
        double bestBid1 = exchange1.getBestBid();
        output.println("1.1");
        double bestBid2 = exchange2.getBestBid();
        output.println("1");
        double bestAsk1 = exchange1.getBestAsk();
        double bestAsk2 = exchange2.getBestAsk();
        output.println("2");
        // check if the bids and asks are valid
        if(bestBid1 < bestAsk1 || bestBid2 < bestAsk2) {
            output.println("Invalid bids or asks 1");
            return;
        }
        output.println("3");

        // check if they are not 0
        if(bestBid1 <= 0 || bestBid2 <= 0 || bestAsk1 <= 0 || bestAsk2 <= 0) {
            output.println("Invalid bids or asks 2");
            return;
        }
        output.println("4");

        if(rm.isTradingAllowed()) {   
            output.println("5");
            // Arbitrage: Buy from Exchange 2 and sell on Exchange 1
            if(bestBid1 > bestAsk2 + transactionCost) {
                double predictedProfit = bestBid1 - bestAsk2 - transactionCost;
                output.println("5");
                // Order buyAt2 = new Order("buy",bestAsk2, tradeSize);
                lastBuyOrders = exchange2.getOrderBook().matchBuyOrder("1","buy",bestAsk2, tradeSize); // buy order at exchange 2
                // Order sellAt1 = new Order("sell",bestBid1, tradeSize);
                lastSellOrders = exchange1.getOrderBook().matchSellOrder("1","sell",bestBid1, tradeSize);
                output.println("6");

                //net profitArb from all the matched orders
                for (String[] s : lastBuyOrders) {
                    String ord1 = s[0];
                    String ord2 = s[1];

                    String[] ord1Params = ord1.split(",");
                    String[] ord2Params = ord2.split(",");

                    double ord1Price = Double.parseDouble(ord1Params[2]);
                    double ord2Price = Double.parseDouble(ord2Params[2]);

                    int ord1Quantity = Integer.parseInt(ord1Params[3]);
                    int ord2Quantity = Integer.parseInt(ord2Params[3]);

                    //output.println("Order 1: " + ord1);
                    //output.println("Order 2: " + ord2);
                    
                    //seeing that the matched orders are one between hft and one from orderbook
                    //seeing that only one order has hftId and other doesn't
                    if ((ord1Params[0].contains("hftId") && !ord2Params[0].contains("hftId")) ||
                    (!ord1Params[0].contains("hftId") && ord2Params[0].contains("hftId"))) {
                        if (ord1Params[1].equals("sell")) {
                            this.profitArb -= Math.abs((ord2Price * ord2Quantity));
                        }
                        else {
                            this.profitArb -= Math.abs((ord1Price * ord1Quantity));
                        }
                    }
                    
                
                }

                for (String[] s : lastSellOrders) {
                    String ord1 = s[0];
                    String ord2 = s[1];

                    String[] ord1Params = ord1.split(",");
                    String[] ord2Params = ord2.split(",");
                    

                    double ord1Price = Double.parseDouble(ord1Params[2]);
                    double ord2Price = Double.parseDouble(ord2Params[2]);

                    int ord1Quantity = Integer.parseInt(ord1Params[3]);
                    int ord2Quantity = Integer.parseInt(ord2Params[3]);
                    
                    //output.println("Order 1: " + ord1);
                    //output.println("Order 2: " + ord2);

                    //seeing that the matched orders are one between hft and one from orderbook
                    //seeing that only one order has hftId and other doesn't
                    if ((ord1Params[0].contains("hftId") && !ord2Params[0].contains("hftId")) ||
                    (!ord1Params[0].contains("hftId") && ord2Params[0].contains("hftId"))) {
                        if (ord1Params[1].equals("sell")) {
                            this.profitArb += Math.abs((ord1Price * ord1Quantity));
                        }
                        else {
                            this.profitArb += Math.abs((ord2Price * ord2Quantity));
                        }
                    }
                    
                
                }
                output.println("7");


                output.println("Arbitrage profit: " + this.profitArb);

                rm.updatePnL(predictedProfit * tradeSize);
            }
            
            // Arbitrage: Buy from Exchange 1 and sell on Exchange 2
            if(bestBid2 > bestAsk1 + transactionCost) {
                double predictedProfit = bestBid2 - bestAsk1 + transactionCost;
                
                // Order buyAt1 = new Order("buy",bestAsk1, tradeSize);
                lastBuyOrders = exchange1.getOrderBook().matchBuyOrder(exchange1.getHFTId(),"buy",bestAsk1, tradeSize);
                // Order sellAt2 = new Order("sell",bestBid2, tradeSize);
                lastSellOrders = exchange2.getOrderBook().matchSellOrder(exchange2.getHFTId(),"sell",bestBid2, tradeSize);
                
                //net profitArb from all the matched orders
                for (String[] s : lastBuyOrders) {
                    String ord1 = s[0];
                    String ord2 = s[1];

                    String[] ord1Params = ord1.split(",");
                    String[] ord2Params = ord2.split(",");

                    double ord1Price = Double.parseDouble(ord1Params[2]);
                    double ord2Price = Double.parseDouble(ord2Params[2]);

                    int ord1Quantity = Integer.parseInt(ord1Params[3]);
                    int ord2Quantity = Integer.parseInt(ord2Params[3]);
                    
                    //output.println("Order 1: " + ord1);
                    //output.println("Order 2: " + ord2);
                    //seeing that the matched orders are one between hft and one from orderbook
                    //seeing that only one order has hftId and other doesn't
                    if ((ord1Params[0].contains("hftId") && !ord2Params[0].contains("hftId")) ||
                    (!ord1Params[0].contains("hftId") && ord2Params[0].contains("hftId"))) {
                        if (ord1Params[1].equals("sell")) {
                            this.profitArb -= Math.abs((ord2Price * ord2Quantity));
                        }
                        else {
                            this.profitArb -= Math.abs((ord1Price * ord1Quantity));
                        }
                    }
                    
                
                }

                for (String[] s : lastSellOrders) {
                    String ord1 = s[0];
                    String ord2 = s[1];

                    String[] ord1Params = ord1.split(",");
                    String[] ord2Params = ord2.split(",");

                    double ord1Price = Double.parseDouble(ord1Params[2]);
                    double ord2Price = Double.parseDouble(ord2Params[2]);

                    int ord1Quantity = Integer.parseInt(ord1Params[3]);
                    int ord2Quantity = Integer.parseInt(ord2Params[3]);

                    //output.println("Order 1: " + ord1);
                    //output.println("Order 2: " + ord2);
                    
                    //seeing that the matched orders are one between hft and one from orderbook
                    //seeing that only one order has hftId and other doesn't
                    if ((ord1Params[0].contains("hftId") && !ord2Params[0].contains("hftId")) ||
                    (!ord1Params[0].contains("hftId") && ord2Params[0].contains("hftId"))) {
                        if (ord1Params[1].equals("sell")) {
                            this.profitArb += Math.abs((ord1Price * ord1Quantity));
                        }
                        else {
                            this.profitArb += Math.abs((ord2Price * ord2Quantity));
                        }
                    }
                }

                output.println("Arbitrage profit: " + this.profitArb);
                
                
                rm.updatePnL(predictedProfit * tradeSize);
            }
        }

        rm.printMetrics();
        output.println(" ");
    }
}