package strategy;
import exchange.Exchange;

public class ArbitrageStrategy implements Runnable {
    private Exchange exchange1;
    private Exchange exchange2;
    // to simulate transaction cost that exchange charges for trade
    // it is kept as fixed for this simulation
    private double transactionCost;
    private int tradeSize;
    private String[][] lastBuyOrders;
    private String[][] lastSellOrders;

    public ArbitrageStrategy(Exchange e1, Exchange e2, double transactionCost, int tradeSize) {
        this.exchange1 = e1;
        this.exchange2 = e2;
        this.transactionCost = transactionCost;
        this.tradeSize = tradeSize;
    }

    public String[][] getLastBuyOrders() {
        return lastBuyOrders;
    }
    
    public String[][] getLastSellOrders() {
        return lastSellOrders;
    }
    
    @Override
    public void run() {
        double bestBid1 = exchange1.getBestBid();
        double bestBid2 = exchange2.getBestBid();

        double bestAsk1 = exchange1.getBestAsk();
        double bestAsk2 = exchange2.getBestAsk();

        // Arbitrage: Buy from Exchange 2 and sell on Exchange 1
        if(bestBid1 > bestAsk2 + transactionCost) {
            double predictedProfit = bestBid1 - bestAsk2 - transactionCost;

            // Order buyAt2 = new Order("buy",bestAsk2, tradeSize);
            lastBuyOrders = exchange2.getOrderBook().matchBuyOrder("1","buy",bestAsk2, tradeSize); // buy order at exchange 2
            // Order sellAt1 = new Order("sell",bestBid1, tradeSize);
            lastSellOrders = exchange1.getOrderBook().matchSellOrder("1","sell",bestBid1, tradeSize);
        }

        // Arbitrage: Buy from Exchange 1 and sell on Exchange 2
        if(bestBid2 > bestAsk1 + transactionCost) {
            double predictedProfit = bestBid2 - bestAsk1 + transactionCost;

            // Order buyAt1 = new Order("buy",bestAsk1, tradeSize);
            lastBuyOrders = exchange1.getOrderBook().matchBuyOrder(exchange1.getHFTId(),"buy",bestAsk1, tradeSize);
            // Order sellAt2 = new Order("sell",bestBid2, tradeSize);
            lastSellOrders = exchange2.getOrderBook().matchSellOrder(exchange2.getHFTId(),"sell",bestBid2, tradeSize);
        }
    }
}