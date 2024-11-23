import exchange.Exchange;
import exchange.OrderBook;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HFTSimulation {

    private Exchange e1;
    private Exchange e2; //For the purpose of Strategy where we place orders based on different markets
    
    public HFTSimulation() {
        e1 = new Exchange();
        e2 = new Exchange();
    }

    public void run(){
        //Add the orders continuously isme threading or specifically ExecutorService which will do two things
        //1. Add orders and 2. Match orders
        //For the add orders we will define a new class(which will contain the random order generation or realistic order generation) and create a new thread for it
        //For the Match orders we will need construct a callable object which will call the matchorder method of the orderbook class and return executed trade with the profit/loss incurred by us
        
        //new instances of arbitrage strategy and market making strategy
        // ArbitrageStrategy arbitrageStrategy = new ArbitrageStrategy(e1, e2);
        // MarketMakingStrategy marketMakingStrategy = new MarketMakingStrategy(e1, 0.01, 100, 2.0);

    }

    public static void main(String[] args) {
        HFTSimulation hft = new HFTSimulation();
        
        hft.run();

        //Create a new instane of the HFT simulation
        //Create instance of orderbook inside it
        //Orderbook ke andar data fill karna hein
        //Call matchorder method of orderbook  (For matching ordders strategies need to be used)
        //The method of matchorder wil return us the trade executed and the profit/loss incurred by us
        
    }

    public static void generateRandomOrders(OrderBook orderBook) {
        Random random = new Random();
        int idCounter = 1;

        while (true) {
            // Generate a random order type
            String type = random.nextBoolean() ? "buy" : "sell";

            // Generate a random price between 200 and 1199
            double price = 500 + Math.round(random.nextDouble() * 2 * 100.0) / 100.0;

            // Generate a random quantity between 1 and 500
            int quantity = 1 + random.nextInt(500);

            // Generate a unique order ID
            String orderID = "Order" + idCounter++;

            // Add the order to the order book
            orderBook.addOrder(orderID, type, price, quantity);

            // Wait for one second before generating the next order
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Order generation interrupted!");
                break;
            }
        }
    }
}