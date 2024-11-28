package strategy;

abstract public interface TradingStrategy {
    // Get last executed buy orders
    public String[][] getLastBuyOrders();

    // Get last executed sell orders
    public String[][] getLastSellOrders();
 
    // Start executing the strategy
     
    void run();
 
    
}
