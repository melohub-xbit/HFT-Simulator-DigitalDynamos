import java.util.Random;
import java.util.concurrent.TimeUnit;
import exchange.Exchange;

public class RandomOrderGeneration {

    private Exchange exchange;

    public RandomOrderGeneration(Exchange exchange){
        
        this.exchange = exchange;
    }

    public void run(){
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
            exchange.addOrder(orderID, type, price, quantity);
            exchange.updatePriceHistory(price);


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
