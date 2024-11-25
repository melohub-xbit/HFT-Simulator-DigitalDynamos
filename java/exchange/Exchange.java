package exchange;

import java.util.*;

public class Exchange {
    private ArrayList<Double> priceHistory;
    private OrderBook orderBook; // since we're simulating an exchange with only one stock, we'll use a single OrderBook

    private static int hft_id = 1 ;

    public Exchange() {
        priceHistory = new ArrayList<>();
        orderBook = new OrderBook();
        orderBook.createOrderBook();
    }

    //Getter Methods
    public String getHFTId(){
        return "hftId" + hft_id++;
    }

    public ArrayList<Double> getPriceHistory() {
        return priceHistory;
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }

    // Price history is updated every time a new price is added (stock prices)
    // Recent Price history reflects the intra_day behaviour which is often leveraged by HFTs
    public void updatePriceHistory(double price) {
        priceHistory.add(price);

        // Will maintain only 10 recent prices
        if(priceHistory.size() > 10) {
            priceHistory.remove(0);
        }
    }

    public void addOrder(String id, String type, double price, int quantity) {
        //call OrderBook.cpp's addOrder
        orderBook.addOrder(id,type,price,quantity);
    }

    public double getBestBid() {
        return orderBook.getBestBid();
    }

    public double getBestAsk() {
        return orderBook.getBestAsk();
    }

    public double getMidPrice() {
        double bestBid = orderBook.getBestBid();
        double bestAsk = orderBook.getBestAsk();

        return (bestAsk - bestBid) / 2;
    }
}
