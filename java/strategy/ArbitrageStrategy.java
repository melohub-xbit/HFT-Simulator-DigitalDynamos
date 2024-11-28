package strategy;
import java.io.PrintStream;

import exchange.Exchange;
import rml.RiskManagement;
import gui.MatchedOrdersGUI;

public class ArbitrageStrategy implements Runnable, TradingStrategy {
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
    private MatchedOrdersGUI gui;

    public ArbitrageStrategy(Exchange e1, Exchange e2, double transactionCost, int tradeSize, RiskManagement rm, PrintStream output, MatchedOrdersGUI gui) {
        this.exchange1 = e1;
        this.exchange2 = e2;
        this.transactionCost = transactionCost;
        this.tradeSize = tradeSize;
        this.rm = rm;
        this.profitArb = 0;
        this.output = output;
        this.gui = gui;
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
        while (true) {
            try {
                output.println("Checking market conditions...");
                double bestBid1 = exchange1.getBestBid();
                double bestBid2 = exchange2.getBestBid();
                double bestAsk1 = exchange1.getBestAsk();
                double bestAsk2 = exchange2.getBestAsk();
    
                // check if they are not 0
                if(bestBid1 <= 0 || bestBid2 <= 0 || bestAsk1 <= 0 || bestAsk2 <= 0) {
                    Thread.sleep(1000);
                    continue;
                }
    
                // check if the bids and asks are valid
                if(bestBid1 < bestAsk1 || bestBid2 < bestAsk2) {
                    Thread.sleep(1000);
                    continue;
                }
    
                if(rm.isTradingAllowed()) {   
                    // Arbitrage: Buy from Exchange 2 and sell on Exchange 1
                    if(bestBid1 > bestAsk2 + transactionCost) {
                        double predictedProfit = bestBid1 - bestAsk2 - transactionCost;
                        
                        lastBuyOrders = exchange2.getOrderBook().matchBuyOrder(exchange2.getHFTId(),"buy",bestAsk2, tradeSize);
                        lastSellOrders = exchange1.getOrderBook().matchSellOrder(exchange1.getHFTId(),"sell",bestBid1, tradeSize);
                        
                        //net profitArb from all the matched orders
                        double totalbuy = 0;
                        double totalsell = 0;
                        for (String[] s : lastBuyOrders) {
                            String ord1 = s[0];
                            String ord2 = s[1];
    
                            String[] ord1Params = ord1.split(",");
                            String[] ord2Params = ord2.split(",");
    
                            double ord1Price = Double.parseDouble(ord1Params[2]);
                            double ord2Price = Double.parseDouble(ord2Params[2]);
    
                            int ord1Quantity = Integer.parseInt(ord1Params[3]);
                            int ord2Quantity = Integer.parseInt(ord2Params[3]);
                            

                            if (ord1Params[1].equals("sell")) {
                                totalbuy -= Math.abs((ord2Price * ord2Quantity));
                            }
                            else {
                                totalbuy -= Math.abs((ord1Price * ord1Quantity));
                            }

                            this.gui.addMatchedOrder(ord1, ord2, totalbuy);
                            
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
                            
                            if (ord1Params[1].equals("sell")) {
                                totalsell += Math.abs((ord1Price * ord1Quantity));
                            }
                            else {
                                totalsell += Math.abs((ord2Price * ord2Quantity));
                            }

                            this.gui.addMatchedOrder(ord1, ord2, totalsell);

                        }
                        this.profitArb += totalsell + totalbuy;

                        rm.updatePnL(predictedProfit * tradeSize);
                    }
                    
                    // Arbitrage: Buy from Exchange 1 and sell on Exchange 2
                    if(bestBid2 > bestAsk1 + transactionCost) {
                        double predictedProfit = bestBid2 - bestAsk1 + transactionCost;
                        
                        lastBuyOrders = exchange1.getOrderBook().matchBuyOrder(exchange1.getHFTId(),"buy",bestAsk1, tradeSize);
                        lastSellOrders = exchange2.getOrderBook().matchSellOrder(exchange2.getHFTId(),"sell",bestBid2, tradeSize);
                        
                        double totalbuy2 = 0;
                        double totalsell2 = 0;
                        for (String[] s : lastBuyOrders) {
                            String ord1 = s[0];
                            String ord2 = s[1];
    
                            String[] ord1Params = ord1.split(",");
                            String[] ord2Params = ord2.split(",");
    
                            double ord1Price = Double.parseDouble(ord1Params[2]);
                            double ord2Price = Double.parseDouble(ord2Params[2]);
    
                            int ord1Quantity = Integer.parseInt(ord1Params[3]);
                            int ord2Quantity = Integer.parseInt(ord2Params[3]);
                            

                            if (ord1Params[1].equals("sell")) {
                                totalbuy2 -= Math.abs((ord2Price * ord2Quantity));
                            }
                            else {
                                totalbuy2 -= Math.abs((ord1Price * ord1Quantity));
                            }
                            
                            this.gui.addMatchedOrder(ord1, ord2, totalbuy);
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
                            

                            if (ord1Params[1].equals("sell")) {
                                totalsell += Math.abs((ord1Price * ord1Quantity));
                            }
                            else {
                                totalsell += Math.abs((ord2Price * ord2Quantity));
                            }

                            this.gui.addMatchedOrder(ord1, ord2, totalsell);
                        }
                        
                        this.profitArb += totalsell + totalbuy;
                        System.out.println("----------------------------------------");
                        rm.updatePnL(predictedProfit * tradeSize);
                    }
                    
                    output.println("Arbitrage profit: " + this.profitArb);
                }
    
                rm.printMetrics();
                output.println(" ");
                Thread.sleep(1000);
    
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    }