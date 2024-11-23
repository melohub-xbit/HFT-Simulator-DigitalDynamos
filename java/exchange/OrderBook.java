package exchange;

//JNI to bridge the exchange.OrderBook class in C++ with Java
public class OrderBook {
    static {
        System.loadLibrary("OrderBookNative"); // Load the native library
    }

    // Pointer to native C++ exchange.OrderBook instance
    private long nativeHandle;

    // Native methods
    public native void createOrderBook();
    public native void destroyOrderBook();
    public native void addOrder(String orderID, String type, double price, int quantity);
    public native void cancelOrder(String orderID);
    // highest bid
    public native double getBestBid();
    //lowest ask
    public native double getBestAsk();
    
    public native String[][] matchBuyOrder(String orderID, String type, double price, int quantity);
    public native String[][] matchSellOrder(String orderID, String type, double price, int quantity);
}



