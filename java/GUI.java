import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GUI {
    private JFrame frame;
    private JTextArea buyOrderLog;
    private JTextArea sellOrderLog;
    private JLabel profitLabel;
    private JButton startButton;
    private JButton stopButton;
    private boolean isRunning = false;

    public GUI() {
        createGUI();
    }

    private void createGUI() {
        frame = new JFrame("HFT Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        // Main Panel with Padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Split Panel for Buy and Sell Order Logs
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Buy Orders Log
        buyOrderLog = new JTextArea();
        buyOrderLog.setEditable(false);
        buyOrderLog.setBackground(new Color(240, 248, 255)); // Light blue
        buyOrderLog.setForeground(new Color(0, 102, 204)); // Dark blue text
        buyOrderLog.setFont(new Font("Courier New", Font.PLAIN, 14));
        JScrollPane buyScrollPane = new JScrollPane(buyOrderLog);
        buyScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 102, 204)), "Buy Orders"));

        // Sell Orders Log
        sellOrderLog = new JTextArea();
        sellOrderLog.setEditable(false);
        sellOrderLog.setBackground(new Color(255, 240, 245)); // Light pink
        sellOrderLog.setForeground(new Color(204, 0, 51)); // Deep red text
        sellOrderLog.setFont(new Font("Courier New", Font.PLAIN, 14));
        JScrollPane sellScrollPane = new JScrollPane(sellOrderLog);
        sellScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(204, 0, 51)), "Sell Orders"));

        splitPane.setLeftComponent(buyScrollPane);
        splitPane.setRightComponent(sellScrollPane);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Bottom Panel for Controls and Profit
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245)); // Match main panel background

        // Profit Display
        profitLabel = new JLabel("Profit: $0.00", SwingConstants.CENTER);
        profitLabel.setFont(new Font("Arial", Font.BOLD, 18));
        profitLabel.setForeground(new Color(34, 139, 34)); // Forest green
        profitLabel.setOpaque(true);
        profitLabel.setBackground(new Color(240, 255, 240)); // Light green background
        profitLabel.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34)));
        bottomPanel.add(profitLabel, BorderLayout.NORTH);

        Button Panel;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 245, 245)); // Match main panel background

        startButton = new JButton("Start Simulation");
        stopButton = new JButton("Stop Simulation");
        stopButton.setEnabled(false);

        Button Colors;
        startButton.setBackground(new Color(144, 238, 144)); // Light green
        startButton.setForeground(Color.BLACK);
        stopButton.setBackground(new Color(255, 99, 71)); // Tomato red
        stopButton.setForeground(Color.WHITE);

        // Start Button Action
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });

        // Stop Button Action
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

    private void startSimulation() {
        if (!isRunning) {
            isRunning = true;
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            buyOrderLog.append("Simulation started...\n");
            sellOrderLog.append("Simulation started...\n");
            

            // TODO: Call your existing logic to start the simulation
        }
    }

    private void stopSimulation() {
        if (isRunning) {
            isRunning = false;
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            buyOrderLog.append("Simulation stopped.\n");
            sellOrderLog.append("Simulation stopped.\n");

            // TODO: Call your existing logic to stop the simulation
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}