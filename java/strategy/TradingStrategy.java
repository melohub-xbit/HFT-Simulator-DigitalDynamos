package strategy;

public abstract class TradingStrategy {

    public abstract void execute();

    protected void log(String message) {
        System.out.println("[TradingStrategy] " + message);
    }
}
