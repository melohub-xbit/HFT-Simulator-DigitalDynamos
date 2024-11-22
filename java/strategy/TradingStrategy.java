package strategy;

public abstract class TradingStrategy {
    private static int idCounter = 1;
    public abstract void execute();

    protected void log(String message) {
        System.out.println("[TradingStrategy] " + message);
    }

    protected String generateOrderId() {
        return "hft_order_" + idCounter++;
    }
}
