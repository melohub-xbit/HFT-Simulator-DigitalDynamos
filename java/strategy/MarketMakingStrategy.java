package strategy;
import exchange.Exchange;
import rml.RiskManagement;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import gui.MatchedOrdersGUI;

public class MarketMakingStrategy implements Runnable {
    private Exchange exchange;
    private int maxOrderSize;
    private double spreadFactor;
    private String[][] lastBuyOrders;
    private String[][] lastSellOrders;
    private RiskManagement rm;
    private double profitMM;
    private PrintStream output;
    private MatchedOrdersGUI gui;

    public MarketMakingStrategy(Exchange exchange, int maxOrderSize, double spreadFactor, RiskManagement rm, PrintStream output, MatchedOrdersGUI gui) {
        this.exchange = exchange;
        this.maxOrderSize = maxOrderSize;
        this.spreadFactor = spreadFactor;
        this.rm = rm;
        this.profitMM = 0;
        this.output = output;
        this.gui = gui;
    }


    public String[][] getLastBuyOrders() {
        return lastBuyOrders;
    }
    
    public String[][] getLastSellOrders() {
        return lastSellOrders;
    }

    public double getProfitMM() {
        return profitMM;
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
        while(true){
            output.println("Running Market Making.");
            // Order Size is dynamically calculated as follows: Max Order Size/Volatility
            ArrayList<Double> priceHistory = exchange.getPriceHistory(); // get the recent prices
            double volatility = calculateVolatility(priceHistory); // get volatility
            
            double midPrice = exchange.getMidPrice();
            if (midPrice < 0) {
                output.println("Mid price is negative. Skipping order placement.");
            } else if (!rm.isTradingAllowed()) {
                output.println("Trading halted due to risk. (Market Making Strategy)");
            }
             else {

                double spread = spreadFactor * volatility; // dynamic spread
                int orderSize = (int)Math.ceil(maxOrderSize/volatility); // dynamic order size

                // Check if the order size is greater than the maximum order size, since initialy the price history is set to 0.
                if(orderSize > maxOrderSize) {
                    orderSize = maxOrderSize;
                }

                double bidPrice = midPrice - (spread / 2);
                double askPrice = midPrice + (spread / 2);

                output.println("Mid Price: " + midPrice + ", Spread: " + spread + ", Order Size: " + orderSize);
                
                if(bidPrice > 0 && askPrice > 0) {
                    output.println("Cooking order.");
                    // exchange.getOrderBook().matchBuyOrder(exchange.getHFTId(),"buy",bidPrice,orderSize); // place buy order
                    
                    // exchange.getOrderBook().matchSellOrder(exchange.getHFTId(),"sell",askPrice,orderSize); // place sell order
                    
                    lastBuyOrders = exchange.getOrderBook().matchBuyOrder(exchange.getHFTId(),"buy",bidPrice,orderSize); // place buy order
                    lastSellOrders = exchange.getOrderBook().matchSellOrder(exchange.getHFTId(),"sell",askPrice,orderSize); // place sell order

                    //net profitMM from all the matched orders
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
                                this.profitMM -= Math.abs((ord2Price * ord2Quantity));
                            }
                            else {
                                this.profitMM -= Math.abs((ord1Price * ord1Quantity));
                            }
                        }
                        this.gui.addMatchedOrder(
                            ord1,
                            ord2,
                            this.profitMM
                        );
                        
                    
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
                                this.profitMM += Math.abs((ord1Price * ord1Quantity));
                            }
                            else {
                                this.profitMM += Math.abs((ord2Price * ord2Quantity));
                            }
                        }
                        this.gui.addMatchedOrder(
                            ord1,
                            ord2,
                            this.profitMM
                        );
                    }

                    output.println("Net profitMM: " + this.profitMM);
                    rm.updatePnL((askPrice - bidPrice) * orderSize);
                    output.println("Placed orders: Buy at " + bidPrice + ", Sell at " + askPrice);
                } else {
                    output.println("Invalid prices. Skipping order.");
                }
                
                output.println("Done.");
                rm.printMetrics();
            }
            
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Order generation interrupted!");
                break;
            }
        }
        output.println(" ");
    }
}