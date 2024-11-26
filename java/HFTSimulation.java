import exchange.Exchange;
import strategy.ArbitrageStrategy;
import strategy.MarketMakingStrategy;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class HFTSimulation {

    private Exchange e1;
    private Exchange e2; //For the purpose of Strategy where we place orders based on different markets
    

    public HFTSimulation() {
        e1 = new Exchange();
        e2 = new Exchange();
    }


    public static void main(String[] args) {
        System.out.println("HFT Simulation");
        HFTSimulation hft = new HFTSimulation();

        System.out.println("Starting HFT Simulation");
        
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        RandomOrderGeneration addOrdersE1 = new RandomOrderGeneration(hft.e1, "E1");
        RandomOrderGeneration addOrdersE2 = new RandomOrderGeneration(hft.e2, "E2");


        MarketMakingStrategy mms = new MarketMakingStrategy(hft.e1, 10, 0.01);
        ArbitrageStrategy abs = new ArbitrageStrategy(null, null, 0, 0);
        

        executor.submit(addOrdersE1);
        executor.submit(addOrdersE2);

        executor.submit(mms);
        executor.submit(abs);

        executor.shutdown();

        System.out.println("All tasks completed");
        
    }
}