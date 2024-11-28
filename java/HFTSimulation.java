import exchange.Exchange;
import rml.RiskManagement;
import strategy.ArbitrageStrategy;
import strategy.MarketMakingStrategy;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.io.PrintStream;
import java.io.FileOutputStream;
import gui.MatchedOrdersGUI;

public class HFTSimulation {
    private Exchange e1;
    private Exchange e2;
    
    public HFTSimulation() {
        e1 = new Exchange();
        e2 = new Exchange();
    }

    public static void main(String[] args) {
        try {
            MatchedOrdersGUI gui = new MatchedOrdersGUI();

            // Create file output streams
            PrintStream e1Output = new PrintStream(new FileOutputStream("e1_orders.txt"));
            PrintStream e2Output = new PrintStream(new FileOutputStream("e2_orders.txt"));
            PrintStream mmsOutput = new PrintStream(new FileOutputStream("market_making.txt"));
            PrintStream arbOutput = new PrintStream(new FileOutputStream("arbitrage.txt"));
            PrintStream rmOutput = new PrintStream(new FileOutputStream("rm.txt"));

            System.out.println("HFT Simulation");
            HFTSimulation hft = new HFTSimulation();

            System.out.println("Starting HFT Simulation");
            
            ExecutorService executor = Executors.newFixedThreadPool(4);
            
            RandomOrderGeneration addOrdersE1 = new RandomOrderGeneration(hft.e1, "E1", e1Output, gui);
            RandomOrderGeneration addOrdersE2 = new RandomOrderGeneration(hft.e2, "E2", e2Output, gui);
            RiskManagement rm = new RiskManagement(-1000, 500, 0.02, 10000, rmOutput);

            MarketMakingStrategy mms = new MarketMakingStrategy(hft.e1, 10, 0.01, rm, mmsOutput, gui);
            ArbitrageStrategy abs = new ArbitrageStrategy(hft.e1, hft.e2, 0.01, 10, rm, arbOutput, gui);

            executor.submit(addOrdersE1);
            executor.submit(addOrdersE2);
            executor.submit(mms);
            executor.submit(abs);

            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            executor.shutdown();

            // Add shutdown hook to close files
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                e1Output.close();
                e2Output.close();
                mmsOutput.close();
                arbOutput.close();
                System.out.println("Output files closed successfully");
            }));

        } catch (Exception e) {
            System.err.println("Error setting up output files: " + e.getMessage());
        }
    }
}
