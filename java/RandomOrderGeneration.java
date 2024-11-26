import java.util.Random;
import java.util.concurrent.TimeUnit;
import exchange.Exchange;
import java.io.PrintStream;

public class RandomOrderGeneration implements Runnable {

    private Exchange exchange;
    private String excNum;
    private PrintStream output;

    public RandomOrderGeneration(Exchange exchange, String exchNum, PrintStream output) {
        this.excNum = exchNum;
        this.exchange = exchange;
        this.output = output;
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
            String orderID = this.excNum + "Order" + idCounter++;

            // Add the order to the order book
            //output.println("Order details: " + orderID + " $" + price + " " + quantity + " :-" + type);
            exchange.addOrder(orderID, type, price, quantity);
            exchange.updatePriceHistory(price);

            output.println("Order added: " + orderID + " $" + price + " " + quantity + " :-" + type);
            String[][] addedAndMatched;
            if (type.equals("buy")) {
                addedAndMatched = exchange.getOrderBook().matchBuyOrder(orderID, type, price, quantity);
            }
            else {
                addedAndMatched = exchange.getOrderBook().matchSellOrder(orderID, type, price, quantity);
            }


            for (String[] s : addedAndMatched) {
                String ord1 = s[0];
                String ord2 = s[1];

                String[] ord1Params = ord1.split(",");
                String[] ord2Params = ord2.split(",");

                double ord1Price = Double.parseDouble(ord1Params[2]);
                double ord2Price = Double.parseDouble(ord2Params[2]);

                int ord1Quantity = Integer.parseInt(ord1Params[3]);
                int ord2Quantity = Integer.parseInt(ord2Params[3]);
                
                //output.println("Order 1: " + ord1);
                //output.println("Order 2: " + ord2);
                //seeing that the matched orders are one between hft and one from orderbook
                //seeing that only one order has hftId and other doesn't
                // if ((ord1Params[0].contains("hftId") && !ord2Params[0].contains("hftId")) ||
                // (!ord1Params[0].contains("hftId") && ord2Params[0].contains("hftId"))) {
                //     if (ord1Params[1].equals("sell")) {
                //         this.profitMM += (ord1Price * ord1Quantity) - (ord2Price * ord2Quantity);
                //     }
                //     else {
                //         this.profitMM += (ord2Price * ord2Quantity) - (ord1Price * ord1Quantity);
                //     }
                // }
            }
            // Wait for one second before generating the next order
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Order generation interrupted!");
                break;
            }

                        
        }
        output.println(" ");
    }
}
