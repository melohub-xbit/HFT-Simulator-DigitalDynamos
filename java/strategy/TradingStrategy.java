package strategy;

import java.io.PrintStream;
import gui.MatchedOrdersGUI;
import rml.RiskManagement;

public abstract class TradingStrategy implements Runnable {
    protected RiskManagement rm;
    protected PrintStream output;
    protected MatchedOrdersGUI gui;

    public TradingStrategy(RiskManagement rm, PrintStream output, MatchedOrdersGUI gui) {
        this.rm = rm;
        this.output = output;
        this.gui = gui;
    }

    public abstract void run();
}
