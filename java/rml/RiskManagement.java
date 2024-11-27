package rml;
import java.io.PrintStream;
import java.util.*;

public class RiskManagement {
    private double maxLossThreshold;  // e.g., -Rs1000
    private double maxVaRThreshold;  // e.g., Rs500
    private double riskFreeRate;     // e.g., 0.02 (2%)
    private double totalCapital;     // e.g., Rs100,000

    private double cumulativePnL = 0.0;
    private List<Double> returnsHistory = new ArrayList<>();
    private PrintStream output;

    public RiskManagement(double maxLossThreshold, double maxVaRThreshold, double riskFreeRate, double totalCapital, PrintStream output) {
        this.maxLossThreshold = maxLossThreshold;
        this.maxVaRThreshold = maxVaRThreshold;
        this.riskFreeRate = riskFreeRate;
        this.totalCapital = totalCapital;
        this.output = output;
    }

    // function to update the cumulative PnL and returns history
    public void updatePnL(double tradePnL) {
        cumulativePnL += tradePnL;
        returnsHistory.add(tradePnL / totalCapital);  // Normalize return
    }

    // controls the execution of trades based on risk management protocols
    public boolean isTradingAllowed() {
        if (cumulativePnL < maxLossThreshold) {
            output.println("Trading halted: Loss threshold exceeded.");
            return false;
        }
        if (calculateVaR(returnsHistory) > maxVaRThreshold) {
            output.println("Trading halted: VaR exceeded.");
            return false;
        }
        return true;
    }

    public double calculateVaR(List<Double> returns) {
        double sum = 0.0;
        for (double r : returns) {
            sum += r;
        }
        double mean = !returns.isEmpty() ? sum / returns.size() : 0.0;

        double squaredDiffSum = 0.0;
        for (double r : returns) {
            squaredDiffSum += Math.pow(r - mean, 2);
        }
        double stdDev = !returns.isEmpty() ? Math.sqrt(squaredDiffSum / returns.size()) : 0.0;

        double zScore = 1.65;
        double totalCapital = 100000;
        return Math.abs(mean - zScore * stdDev) * totalCapital;
    }

    public double calculateSharpeRatio() {
        // Step 1: Calculate the mean return
        double total = 0.0;
        for (double r : returnsHistory) {
            total += r;
        }
        double meanReturn = total / returnsHistory.size();

        // Step 2: Calculate the standard deviation
        double squaredDifferenceSum = 0.0;
        for (double r : returnsHistory) {
            squaredDifferenceSum += (r - meanReturn) * (r - meanReturn);
        }
        double stdDev = Math.sqrt(squaredDifferenceSum / returnsHistory.size());

        // Step 3: Calculate and return the Sharpe Ratio
        return (meanReturn - riskFreeRate) / stdDev;
    }

    public void printMetrics() {
        output.println("Cumulative P&L: Rs " + cumulativePnL);
        output.println("Value at Risk (VaR): Rs " + calculateVaR(returnsHistory));
        output.println("Sharpe Ratio: " + calculateSharpeRatio());
    }

    public double getCumulativePnL() {
        return cumulativePnL;
    }

    public double getVar() {
        return calculateVaR(returnsHistory);
    }

    public double getSharpeRatio() {
        return calculateSharpeRatio();
    }
}