package strategy;
import exchange.Exchange;

import java.util.ArrayList;

public class MarketMakingStrategy{
    private Exchange exchange;
    private int maxOrderSize;
    private double spreadFactor;

    public MarketMakingStrategy(Exchange exchange, int maxOrderSize, double spreadFactor) {
        this.exchange = exchange;
        this.maxOrderSize = maxOrderSize;
        this.spreadFactor = spreadFactor;
    }

    public double calculateEMA(ArrayList<Double> returns, int period) {
        double alpha = 2.0 / (period + 1);
        double ema = returns.get(0); // Start with the first data point

        for (int i = 1; i < returns.size(); i++) {
            ema = alpha * returns.get(i) + (1 - alpha) * ema;
        }
        return ema;
    }

    /*
        Volatility is the measure of how market prices are changing.
        Common way of measuring volatility is to calculate standard deviation.
        The method used here is called EMA, which is closer to what HFT firms implement.
        Volatility is calculated using Exponential Moving Average (EMA)
        EMA is a weighted average of prices where recent prices have more influence than older ones.
        EMA(today) = a * Price (now) + (1 - a) EMA(before)

        Algorithm:
        1. Collect historical prices from the Exchange class.
        2. Compute daily returns from the price list.
        3. Apply EMA to these returns.
        4. Compute squared EMA of returns for volatility.

        volatility = sqrt(EMA of squared returns)
        spread = spreadFactor * volatility
     */

    public double calculateVolatility(ArrayList<Double> priceHistory) {

        ArrayList<Double> returns = new ArrayList<>();
        for (int i = 1; i < priceHistory.size(); i++) {
            returns.add(priceHistory.get(i) - priceHistory.get(i - 1));
        }

        ArrayList<Double> squaredReturns = new ArrayList<>();
        for (double r : returns) {
            squaredReturns.add(r * r);
        }

        double squaredReturnEMA = calculateEMA(squaredReturns, 10);


        return Math.sqrt(squaredReturnEMA);
    }


    // @Override
    public void run(){
        System.out.println("hiiiiiiiiiiii");
        // Order Size is dynamically calculated as follows: Max Order Size/Volatility
        ArrayList<Double> priceHistory = exchange.getPriceHistory(); // get the recent prices
        double volatility = calculateVolatility(priceHistory); // get volatility

        double midPrice = exchange.getMidPrice();
        double spread = spreadFactor * volatility; // dynamic spread
        int orderSize = (int)Math.ceil(maxOrderSize/volatility); // dynamic order size

        double bidPrice = midPrice - (spread / 2);
        double askPrice = midPrice + (spread / 2);
        
        if(bidPrice > 0 && askPrice > 0) {
            // Order o1 = new Order("buy", bidPrice, orderSize);
            // Order o2 = new Order("sell", askPrice, orderSize);
            
            exchange.getOrderBook().matchBuyOrder(exchange.getHFTId(),"buy",bidPrice,orderSize); // place buy order
            
            exchange.getOrderBook().matchSellOrder(exchange.getHFTId(),"sell",askPrice,orderSize); // place sell order

            System.out.println("Placed orders: Buy at " + bidPrice + ", Sell at " + askPrice);
        }
    }


}