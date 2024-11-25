import exchange.Exchange;
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
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        RandomOrderGeneration addOrdersE1 = new RandomOrderGeneration(hft.e1);
        // RandomOrderGeneration addOrdersE2 = new RandomOrderGeneration(hft.e2.getOrderBook(), hft.e2);


        MarketMakingStrategy mms = new MarketMakingStrategy(hft.e1, 10, 0.01);
        

        executor.submit(addOrdersE1);
        // executor.submit(addOrdersE2);
        // executor.submit(mms);
        mms.run();
        
        executor.shutdown();

        System.out.println("All tasks completed");
        
    }
}