package gui;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.geom.Path2D;

import javax.swing.*;

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
    private Queue<String> buyQueue = new LinkedList<>();
    private Queue<String> sellQueue = new LinkedList<>();
    private GraphPanel graphPanel;
    private List<Double> prices = new ArrayList<>();
    private static final int MAX_GRAPH_POINTS = 50;

    public MatchedOrdersGUI() {
        createGUI();
    }
    private void createGUI() {
        frame = new JFrame("Matched Orders Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));  // Set minimum size
        frame.setPreferredSize(new Dimension(1200, 800));
    
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));
    
        // Create split pane for logs with continuous layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setContinuousLayout(true);
    
        // Left panel for buy orders with size constraints
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
        leftScrollPane.setMinimumSize(new Dimension(200, 400));
    
        // Right panel for sell orders with size constraints
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
        rightScrollPane.setMinimumSize(new Dimension(200, 400));
    
        splitPane.setLeftComponent(leftScrollPane);
        splitPane.setRightComponent(rightScrollPane);
    
        // Create layered pane with proportional layout
        JLayeredPane layeredPane = new JLayeredPane() {
            @Override
            public void doLayout() {
                // Make split pane fill the entire space
                splitPane.setBounds(0, 0, getWidth(), getHeight());
                
                // Calculate graph size proportionally
                int graphWidth = Math.min(400, getWidth() / 3);
                int graphHeight = Math.min(300, getHeight() / 3);
                
                // Center the graph
                graphPanel.setBounds(
                    (getWidth() - graphWidth) / 2,
                    (getHeight() - graphHeight) / 2 - 50,
                    graphWidth,
                    graphHeight
                );
            }
        };
        layeredPane.setLayout(null);
    
        // Add split pane to base layer
        layeredPane.add(splitPane, JLayeredPane.DEFAULT_LAYER);
    
        // Initialize and add graph panel
        prices.add(0.0);
        prices.add(0.0);
        prices.add(0.0);
        prices.add(0.0);
        prices.add(0.0);
    
        graphPanel = new GraphPanel(prices);
        graphPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        graphPanel.setBackground(new Color(255, 255, 255, 240));
        layeredPane.add(graphPanel, JLayeredPane.PALETTE_LAYER);
    
        mainPanel.add(layeredPane, BorderLayout.CENTER);
    
        // Bottom panel with dynamic sizing
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
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
    
        startButton = new JButton("Start Monitoring");
        stopButton = new JButton("Stop Monitoring");
        JButton exitButton = new JButton("EXIT");
        exitButton.setBackground(new Color(255, 69, 0));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.addActionListener(e -> {
            stopMonitoring();
            System.exit(0);
        });
        buttonPanel.add(exitButton);

        startButton.setBackground(new Color(144, 238, 144));
        stopButton.setBackground(new Color(255, 99, 71));
        stopButton.setEnabled(false);
    
        startButton.addActionListener(e -> startMonitoring());
        stopButton.addActionListener(e -> stopMonitoring());
    
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
    
        bottomPanel.add(totalProfitLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
    
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    
        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public void addMatchedOrder(String buyOrder, String sellOrder, double profit) {
        if (!isRunning) return;

        SwingUtilities.invokeLater(() -> {
            totalProfit += profit;
            prices.add(totalProfit);
            if (prices.size() > MAX_GRAPH_POINTS) prices.remove(0);
            graphPanel.repaint();

            String buyEntry = String.format("BUY: %s Profit: Rs %.2f%n", buyOrder, profit / 2);
            String sellEntry = String.format("SELL: %s Profit: Rs %.2f%n", sellOrder, profit / 2);

            buyQueue.offer(buyEntry);
            sellQueue.offer(sellEntry);

            while (buyQueue.size() > MAX_PAIRS) {
                buyQueue.poll();
                sellQueue.poll();
            }

            leftOrdersLog.setText(String.join("", buyQueue));
            rightOrdersLog.setText(String.join("", sellQueue));
            totalProfitLabel.setText("Total Profit: Rs " + df.format(totalProfit));
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

    class GraphPanel extends JPanel {
        private List<Double> data;
        private final int AXIS_PADDING = 50;
        private final int TICK_LENGTH = 5;
        private final DecimalFormat df = new DecimalFormat("#,##0.00");
    
        public GraphPanel(List<Double> data) {
            this.data = data;
            setBackground(Color.WHITE);
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth() - 2 * AXIS_PADDING;
            int height = getHeight() - 2 * AXIS_PADDING;

            // Add title
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String title = "Profits";
            FontMetrics metrics = g2.getFontMetrics();
            int titleWidth = metrics.stringWidth(title);
            g2.drawString(title, (getWidth() - titleWidth) / 2, 25);

            // Draw axes
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(AXIS_PADDING, AXIS_PADDING, AXIS_PADDING, height + AXIS_PADDING);
            g2.drawLine(AXIS_PADDING, height + AXIS_PADDING, width + AXIS_PADDING, height + AXIS_PADDING);

            if (data.isEmpty()) return;

            double maxProfit = data.stream().max(Double::compareTo).orElse(0.0);
            double minProfit = data.stream().min(Double::compareTo).orElse(0.0);
            double range = maxProfit - minProfit;
            double scale = height / (range == 0 ? 1 : range);

            drawYAxisLabels(g2, height, minProfit, range, scale);

            // Draw filled areas and lines
            for (int i = 0; i < data.size() - 1; i++) {
                int x1 = AXIS_PADDING + (i * width / (data.size() - 1));
                int y1 = height + AXIS_PADDING - (int)((data.get(i) - minProfit) * scale);
                int x2 = AXIS_PADDING + ((i + 1) * width / (data.size() - 1));
                int y2 = height + AXIS_PADDING - (int)((data.get(i + 1) - minProfit) * scale);

                // Create and fill area
                Path2D.Double path = new Path2D.Double();
                path.moveTo(x1, height + AXIS_PADDING);
                path.lineTo(x1, y1);
                path.lineTo(x2, y2);
                path.lineTo(x2, height + AXIS_PADDING);
                path.closePath();

                // Set colors based on slope
                Color lineColor;
                Color fillColor;
                if (data.get(i + 1) > data.get(i)) {
                    lineColor = new Color(34, 139, 34);
                    fillColor = new Color(34, 139, 34, 40);
                } else {
                    lineColor = new Color(220, 20, 60);
                    fillColor = new Color(220, 20, 60, 40);
                }

                // Fill area
                g2.setColor(fillColor);
                g2.fill(path);

                // Draw line
                g2.setColor(lineColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x1, y1, x2, y2);

                // Draw points
                g2.fillOval(x1 - 3, y1 - 3, 6, 6);
                if (i == data.size() - 2) {
                    g2.fillOval(x2 - 3, y2 - 3, 6, 6);
                }
            }

            drawTimeLabels(g2, width, height);
        }

        private void drawYAxisLabels(Graphics2D g2, int height, double minProfit, double range, double scale) {
            int numYLabels = 5;
            for (int i = 0; i <= numYLabels; i++) {
                double value = minProfit + (range * i / numYLabels);
                int y = height + AXIS_PADDING - (int)((value - minProfit) * scale);
                
                g2.setColor(new Color(220, 220, 220));
                g2.drawLine(AXIS_PADDING, y, getWidth() - AXIS_PADDING, y);
                
                g2.setColor(Color.BLACK);
                g2.drawString(df.format(value), 5, y + 5);
                g2.drawLine(AXIS_PADDING - TICK_LENGTH, y, AXIS_PADDING, y);
            }
        }

        private void drawTimeLabels(Graphics2D g2, int width, int height) {
            int numXLabels = 10; // Increased number of time labels
            for (int i = 0; i < numXLabels; i++) {
                int x = AXIS_PADDING + (i * width / (numXLabels - 1));
                g2.setColor(Color.BLACK);
                int timePoint = (data.size() * i) / (numXLabels - 1);
                g2.drawString(String.format("T-%d", numXLabels - i - 1), 
                             x - 10, height + AXIS_PADDING + 20);
                g2.drawLine(x, height + AXIS_PADDING, x, height + AXIS_PADDING + TICK_LENGTH);
            }
        }

    }
    
}