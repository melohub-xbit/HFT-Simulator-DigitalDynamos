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

    public MatchedOrdersGUI() {
        createGUI();
    }
    private void createGUI() {
        frame = new JFrame("Matched Orders Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Create main split pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setResizeWeight(0.3);

        // Left panel with buy orders
        JPanel leftPanel = new JPanel(new BorderLayout());
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
        leftPanel.add(leftScrollPane, BorderLayout.CENTER);

        // Right split pane for graph and sell orders
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplitPane.setResizeWeight(0.7);

        // Center graph panel
        prices.add(100.0);
        prices.add(105.0);
        prices.add(103.0);
        prices.add(107.0);
        prices.add(104.0);
        graphPanel = new GraphPanel(prices);
        JPanel graphContainer = new JPanel(new BorderLayout());
        graphContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            "Profit Timeline"
        ));
        graphContainer.add(graphPanel, BorderLayout.CENTER);

        // Right panel with sell orders
        JPanel rightPanel = new JPanel(new BorderLayout());
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
        rightPanel.add(rightScrollPane, BorderLayout.CENTER);

        // Combine panels
        rightSplitPane.setLeftComponent(graphContainer);
        rightSplitPane.setRightComponent(rightPanel);
        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightSplitPane);

        mainPanel.add(mainSplitPane, BorderLayout.CENTER);

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

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }
    
    public void addMatchedOrder(String buyOrder, String sellOrder, double profit) {
        if (!isRunning) return;

        SwingUtilities.invokeLater(() -> {
            totalProfit += profit;
            prices.add(totalProfit);
            if (prices.size() > 20) prices.remove(0);
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
    
            // Draw axes
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            // Y-axis
            g2.drawLine(AXIS_PADDING, AXIS_PADDING, AXIS_PADDING, height + AXIS_PADDING);
            // X-axis
            g2.drawLine(AXIS_PADDING, height + AXIS_PADDING, width + AXIS_PADDING, height + AXIS_PADDING);
    
            if (data.isEmpty()) return;
    
            double maxProfit = data.stream().max(Double::compareTo).orElse(0.0);
            double minProfit = data.stream().min(Double::compareTo).orElse(0.0);
            double range = maxProfit - minProfit;
            double scale = height / (range == 0 ? 1 : range);
    
            // Draw Y-axis labels and grid lines
            g2.setStroke(new BasicStroke(1));
            int numYLabels = 5;
            for (int i = 0; i <= numYLabels; i++) {
                double value = minProfit + (range * i / numYLabels);
                int y = height + AXIS_PADDING - (int)((value - minProfit) * scale);
                // Grid lines
                g2.setColor(new Color(220, 220, 220));
                g2.drawLine(AXIS_PADDING, y, width + AXIS_PADDING, y);
                // Labels
                g2.setColor(Color.BLACK);
                g2.drawString(df.format(value), 5, y + 5);
                g2.drawLine(AXIS_PADDING - TICK_LENGTH, y, AXIS_PADDING, y);
            }
    
            // Draw profit line
            g2.setStroke(new BasicStroke(2));
            Path2D.Double path = new Path2D.Double();
            boolean first = true;
    
            for (int i = 0; i < data.size(); i++) {
                int x = AXIS_PADDING + (i * width / (data.size() - 1));
                int y = height + AXIS_PADDING - (int)((data.get(i) - minProfit) * scale);
                
                if (first) {
                    path.moveTo(x, y);
                    first = false;
                } else {
                    path.lineTo(x, y);
                }
                
                // Draw points
                g2.setColor(Color.BLUE);
                g2.fillOval(x - 3, y - 3, 6, 6);
            }
    
            // Draw gradient under the line
            GradientPaint gradient = new GradientPaint(
                0, AXIS_PADDING, new Color(0, 150, 255, 50),
                0, height + AXIS_PADDING, new Color(0, 150, 255, 10)
            );
            g2.setPaint(gradient);
            path.lineTo(width + AXIS_PADDING, height + AXIS_PADDING);
            path.lineTo(AXIS_PADDING, height + AXIS_PADDING);
            path.closePath();
            g2.fill(path);
    
            // Draw the line
            g2.setColor(new Color(0, 120, 255));
            g2.draw(path);
    
            // Draw time labels
            int numXLabels = Math.min(data.size(), 5);
            for (int i = 0; i < numXLabels; i++) {
                int x = AXIS_PADDING + (i * width / (numXLabels - 1));
                g2.setColor(Color.BLACK);
                g2.drawString(String.format("T-%d", numXLabels - i - 1), 
                             x - 10, height + AXIS_PADDING + 20);
                g2.drawLine(x, height + AXIS_PADDING, x, height + AXIS_PADDING + TICK_LENGTH);
            }
        }
    }
    
}