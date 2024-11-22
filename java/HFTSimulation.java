import exchange.Exchange;

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
        

    }

    public void hiAaryan() {
        System.out.println("Hi I am aaryan, I am a good boy");
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

}
