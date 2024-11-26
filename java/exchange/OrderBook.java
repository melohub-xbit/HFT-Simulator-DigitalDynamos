package exchange;

//JNI to bridge the exchange.OrderBook class in C++ with Java
public class OrderBook {
    static {
        System.loadLibrary("OrderBookNative"); // Load the native library
    }
    
    // Pointer to native C++ exchange.OrderBook instance
    private long nativeHandle;

    // Native methods
    public synchronized native void createOrderBook();
    public synchronized native void destroyOrderBook();
    public synchronized native void addOrder(String orderID, String type, double price, int quantity);
    public synchronized native void cancelOrder(String orderID);
    // highest bid
    public synchronized native double getBestBid();
    //lowest ask
    public synchronized native double getBestAsk();
    
    public synchronized native String[][] matchBuyOrder(String orderID, String type, double price, int quantity);
    public synchronized native String[][] matchSellOrder(String orderID, String type, double price, int quantity);
}



