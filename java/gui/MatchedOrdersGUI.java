package gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.DecimalFormat;

public class MatchedOrdersGUI {
    private JFrame frame;
    private JTextArea leftOrdersLog;
    private JTextArea rightOrdersLog;
    private JLabel totalProfitLabel;
    private JButton startButton;
    private JButton stopButton;
    private boolean isRunning = false;
    private double totalProfit = 0.0;
    private ExecutorService executor;
    private DecimalFormat df = new DecimalFormat("#,##0.00");
    private static final int MAX_PAIRS = 30;
    private java.util.Queue<String> buyQueue = new java.util.LinkedList<>();
    private java.util.Queue<String> sellQueue = new java.util.LinkedList<>();

    public MatchedOrdersGUI() {
        createGUI();
    }

    private void createGUI() {
        frame = new JFrame("Matched Orders Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Left panel for buy orders
        leftOrdersLog = new JTextArea();
        leftOrdersLog.setEditable(false);
        leftOrdersLog.setBackground(new Color(240, 248, 255));
        leftOrdersLog.setForeground(new Color(0, 102, 204));
        leftOrdersLog.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane leftScrollPane = new JScrollPane(leftOrdersLog);
        leftScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204)), 
            "Buy Orders"
        ));

        // Right panel for sell orders
        rightOrdersLog = new JTextArea();
        rightOrdersLog.setEditable(false);
        rightOrdersLog.setBackground(new Color(255, 240, 245));
        rightOrdersLog.setForeground(new Color(204, 0, 51));
        rightOrdersLog.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane rightScrollPane = new JScrollPane(rightOrdersLog);
        rightScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(204, 0, 51)), 
            "Sell Orders"
        ));

        splitPane.setLeftComponent(leftScrollPane);
        splitPane.setRightComponent(rightScrollPane);

        // Profit Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));

        totalProfitLabel = new JLabel("Total Profit: Rs0.00", SwingConstants.CENTER);
        totalProfitLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalProfitLabel.setForeground(new Color(34, 139, 34));
        totalProfitLabel.setOpaque(true);
        totalProfitLabel.setBackground(new Color(240, 255, 240));
        totalProfitLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 139, 34)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Control Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 245, 245));

        startButton = new JButton("Start Monitoring");
        stopButton = new JButton("Stop Monitoring");
        startButton.setBackground(new Color(144, 238, 144));
        stopButton.setBackground(new Color(255, 99, 71));
        stopButton.setEnabled(false);

        startButton.addActionListener(e -> startMonitoring());
        stopButton.addActionListener(e -> stopMonitoring());

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        bottomPanel.add(totalProfitLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public void addMatchedOrder(String buyOrder, String sellOrder, double profit) {
        if (!isRunning) return;
        
        SwingUtilities.invokeLater(() -> {
            totalProfit += profit;
            System.out.println("*********************************************");
            System.out.println("profit: " + profit + " totalProfit: " + totalProfit);
            
            String buyEntry = String.format(
                "╔═══════════════════════════╗%n" +
                "║ %s%n" +
                "║ Profit: Rs%s%n" +
                "╚═══════════════════════════╝%n%n",
                buyOrder, df.format(profit/2)
            );
            
            String sellEntry = String.format(
                "╔═══════════════════════════╗%n" +
                "║ %s%n" +
                "║ Profit: Rs%s%n" +
                "╚═══════════════════════════╝%n%n",
                sellOrder, df.format(profit/2)
            );
            
            // Add to queues
            if (buyOrder.toLowerCase().contains("buy")) {
                buyQueue.offer(buyEntry);
                sellQueue.offer(sellEntry);
            } else {
                buyQueue.offer(sellEntry);
                sellQueue.offer(buyEntry);
            }
            
            // Remove oldest if over limit
            while (buyQueue.size() > MAX_PAIRS) {
                buyQueue.poll();
                sellQueue.poll();
            }
            
            // Update display
            leftOrdersLog.setText("");
            rightOrdersLog.setText("");
            buyQueue.forEach(entry -> leftOrdersLog.append(entry));
            sellQueue.forEach(entry -> rightOrdersLog.append(entry));
            
            totalProfitLabel.setText("Total Profit: Rs" + df.format(totalProfit));
            
            // Auto-scroll both panels
            leftOrdersLog.setCaretPosition(leftOrdersLog.getDocument().getLength());
            rightOrdersLog.setCaretPosition(rightOrdersLog.getDocument().getLength());
        });
    }
    
    private void startMonitoring() {
        isRunning = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        executor = Executors.newFixedThreadPool(2);
    }

    private void stopMonitoring() {
        isRunning = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        
        if (executor != null) {
            executor.shutdown();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MatchedOrdersGUI::new);
    }
}
