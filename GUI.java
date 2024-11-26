import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import exchange.Exchange;
import rml.RiskManagement;
import strategy.MarketMakingStrategy;
import strategy.ArbitrageStrategy;

public class GUI {
    private JFrame frame;
    private JTextArea buyOrderLog;
    private JTextArea sellOrderLog;
    private JLabel profitLabel;
    private JButton startButton;
    private JButton stopButton;
    private boolean isRunning = false;
    
    private Exchange exchange1;
    private Exchange exchange2;
    private MarketMakingStrategy mmStrategy;
    private ArbitrageStrategy arbStrategy;
    private RiskManagement riskManagement;
    private RandomOrderGeneration addOrdersE1;
    private RandomOrderGeneration addOrdersE2;
    private ExecutorService executor;

    public GUI() {
        // Initialize exchanges
        exchange1 = new Exchange();
        exchange2 = new Exchange();
        
        // Initialize strategies and order generators
        mmStrategy = new MarketMakingStrategy(exchange1, 10, 0.01, riskManagement);
        arbStrategy = new ArbitrageStrategy(exchange1, exchange2, 0.01, 50, riskManagement);
        addOrdersE1 = new RandomOrderGeneration(exchange1, "E1");
        addOrdersE2 = new RandomOrderGeneration(exchange2, "E2");
        
        createGUI();
    }

    private void createGUI() {
        frame = new JFrame("HFT Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        buyOrderLog = new JTextArea();
        buyOrderLog.setEditable(false);
        buyOrderLog.setBackground(new Color(240, 248, 255));
        buyOrderLog.setForeground(new Color(0, 102, 204));
        buyOrderLog.setFont(new Font("Courier New", Font.PLAIN, 14));
        JScrollPane buyScrollPane = new JScrollPane(buyOrderLog);
        buyScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 102, 204)), "Buy Orders"));

        sellOrderLog = new JTextArea();
        sellOrderLog.setEditable(false);
        sellOrderLog.setBackground(new Color(255, 240, 245));
        sellOrderLog.setForeground(new Color(204, 0, 51));
        sellOrderLog.setFont(new Font("Courier New", Font.PLAIN, 14));
        JScrollPane sellScrollPane = new JScrollPane(sellOrderLog);
        sellScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(204, 0, 51)), "Sell Orders"));

        splitPane.setLeftComponent(buyScrollPane);
        splitPane.setRightComponent(sellScrollPane);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));

        profitLabel = new JLabel("Profit: $0.00", SwingConstants.CENTER);
        profitLabel.setFont(new Font("Arial", Font.BOLD, 18));
        profitLabel.setForeground(new Color(34, 139, 34));
        profitLabel.setOpaque(true);
        profitLabel.setBackground(new Color(240, 255, 240));
        profitLabel.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34)));
        bottomPanel.add(profitLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 245, 245));

        startButton = new JButton("Start Simulation");
        stopButton = new JButton("Stop Simulation");
        stopButton.setEnabled(false);

        startButton.setBackground(new Color(144, 238, 144));
        startButton.setForeground(Color.BLACK);
        stopButton.setBackground(new Color(255, 99, 71));
        stopButton.setForeground(Color.WHITE);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSimulation();
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public void updateOrderLogs(String[][] buyOrders, String[][] sellOrders) {
        if (buyOrders != null) {
            buyOrderLog.setText("");
            for (String[] order : buyOrders) {
                buyOrderLog.append(String.format("Price: %s, Size: %s%n", order[0], order[1]));
            }
        }
        
        if (sellOrders != null) {
            sellOrderLog.setText("");
            for (String[] order : sellOrders) {
                sellOrderLog.append(String.format("Price: %s, Size: %s%n", order[0], order[1]));
            }
        }
    }

    private void startSimulation() {
        if (!isRunning) {
            isRunning = true;
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            
            executor = Executors.newFixedThreadPool(4);
            
            executor.submit(addOrdersE1);
            executor.submit(addOrdersE2);
            
            new Thread(() -> {
                while (isRunning) {
                    executor.submit(() -> {
                        mmStrategy.run();
                        updateOrderLogs(mmStrategy.getLastBuyOrders(), mmStrategy.getLastSellOrders());
                    });
                    
                    executor.submit(() -> {
                        arbStrategy.run();
                        updateOrderLogs(arbStrategy.getLastBuyOrders(), arbStrategy.getLastSellOrders());
                    });
                    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }).start();
        }
    }

    private void stopSimulation() {
        if (isRunning) {
            isRunning = false;
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            buyOrderLog.append("Simulation stopped.\n");
            sellOrderLog.append("Simulation stopped.\n");
            
            if (executor != null) {
                executor.shutdown();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
