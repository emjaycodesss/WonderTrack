package com.example.wondertrackxd.controller.analytics;

import com.example.wondertrackxd.controller.model.RecentOrder;
import com.example.wondertrackxd.controller.model.SalesRecord;
import com.example.wondertrackxd.controller.header.HeaderController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.application.Platform;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the Analytics page
 * Manages charts, KPI cards, and time period filtering
 */
public class AnalyticsController implements Initializable {

    // Logger for tracking analytics operations
    private static final Logger logger = Logger.getLogger(AnalyticsController.class.getName());

    // KPI Card Labels - need to be added to FXML
    @FXML private Label totalOrdersLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label avgOrderValueLabel;
    @FXML private Label customerRetentionLabel;
    @FXML private Label completionRateLabel;
    @FXML private Label bestSellingItemLabel;
    @FXML private Label growthRateLabel;

    // Charts
    @FXML private LineChart<String, Number> dailySalesChart;
    @FXML private PieChart revenueFlavorChart;
    @FXML private BarChart<Number, String> topFlavorsChart;
    @FXML private BarChart<String, Number> monthlyRevenueChart;

    // Data storage - Professional POS separation
    private List<RecentOrder> allOrders = new ArrayList<>();      // All orders (pending, completed, cancelled)
    private List<SalesRecord> allSales = new ArrayList<>();       // Only completed sales for analytics
    
    private static final String ORDERS_FILE = "src/main/resources/txtFiles/orders.txt";
    private static final String SALES_FILE = "src/main/resources/txtFiles/sales.txt";
    
    // Data service for professional POS operations
    private DataService dataService = new DataService();
    
    // Current time period filter
    private String currentTimePeriod = "Last 30 Days";

    /**
     * Initialize the Analytics controller
     * Load real data from orders.txt and populate analytics
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("🏗️ Initializing Analytics Controller with real data...");
        
        try {
            // Register this controller with HeaderController for real-time updates
            HeaderController.setAnalyticsController(this);
            
            // Load data using professional DataService
            if (dataService.loadAllData()) {
                allOrders = dataService.getAllOrders();
                allSales = dataService.getAllSales();
                
                // Calculate and display comprehensive analytics
                calculateAndDisplayAnalytics();
                
                logger.info("✅ Analytics initialization completed with " + allOrders.size() + " orders and " + allSales.size() + " sales");
            } else {
                logger.warning("⚠️ Some data files could not be loaded");
                showErrorState();
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during Analytics initialization", e);
            showErrorState();
        }
    }

    /**
     * Load order data from orders.txt file
     * Orders include all statuses: Pending, Completed, Cancelled, In-Progress
     */
    private void loadOrderData() {
        logger.info("📂 Loading order data from orders.txt...");
        
        try {
            if (Files.exists(Paths.get(ORDERS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(ORDERS_FILE));
                
                allOrders = lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .filter(line -> !line.trim().startsWith("#"))
                    .map(this::parseOrderLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                
                logger.info("✅ Loaded " + allOrders.size() + " orders successfully");
            } else {
                logger.warning("⚠️ Orders file not found: " + ORDERS_FILE);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error loading order data", e);
        }
    }

    /**
     * Load sales data from sales.txt file
     * Sales contain only completed transactions for revenue analytics
     */
    private void loadSalesData() {
        logger.info("💰 Loading sales data from sales.txt...");
        
        try {
            if (Files.exists(Paths.get(SALES_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(SALES_FILE));
                
                allSales = lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .filter(line -> !line.trim().startsWith("#"))
                    .map(this::parseSalesLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                
                logger.info("✅ Loaded " + allSales.size() + " sales records successfully");
            } else {
                logger.warning("⚠️ Sales file not found: " + SALES_FILE);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error loading sales data", e);
        }
    }

    /**
     * Parse order line from CSV format to RecentOrder object
     */
    private RecentOrder parseOrderLine(String line) {
        try {
            List<String> tokens = new ArrayList<>();
            StringBuilder currentToken = new StringBuilder();
            boolean inQuotes = false;
            
            for (char c : line.toCharArray()) {
                if (c == '"') {
                    inQuotes = !inQuotes;
                } else if (c == ',' && !inQuotes) {
                    tokens.add(currentToken.toString().trim());
                    currentToken = new StringBuilder();
                } else {
                    currentToken.append(c);
                }
            }
            tokens.add(currentToken.toString().trim());
            
            if (tokens.size() >= 11) {
                return new RecentOrder(
                    tokens.get(0).trim(), tokens.get(1).trim(), tokens.get(2).trim(), 
                    tokens.get(3).replace("\"", "").trim(), tokens.get(4).trim(), tokens.get(5).trim(), 
                    tokens.get(6).trim(), tokens.get(7).replace("\"", "").trim(), tokens.get(8).trim(),
                    tokens.get(9).replace("\"", "").trim(), tokens.get(10).replace("\"", "").trim()
                );
            }
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing order line: " + line, e);
            return null;
        }
    }

    /**
     * Parse sales line from CSV format to SalesRecord object
     */
    private SalesRecord parseSalesLine(String line) {
        try {
            List<String> tokens = new ArrayList<>();
            StringBuilder currentToken = new StringBuilder();
            boolean inQuotes = false;
            
            for (char c : line.toCharArray()) {
                if (c == '"') {
                    inQuotes = !inQuotes;
                } else if (c == ',' && !inQuotes) {
                    tokens.add(currentToken.toString().trim());
                    currentToken = new StringBuilder();
                } else {
                    currentToken.append(c);
                }
            }
            tokens.add(currentToken.toString().trim());
            
            if (tokens.size() >= 11) {
                return new SalesRecord(
                    tokens.get(0).trim(),  // Sale ID
                    tokens.get(1).trim(),  // Order ID
                    tokens.get(2).trim(),  // Customer Name
                    tokens.get(3).trim(),  // Contact Number
                    tokens.get(4).replace("\"", "").trim(),  // Items Sold
                    tokens.get(5).trim(),  // Total Items
                    tokens.get(6).trim(),  // Sale Amount
                    tokens.get(7).trim(),  // Payment Method
                    tokens.get(8).replace("\"", "").trim(),  // Sale Date/Time
                    tokens.get(9).replace("\"", "").trim(),  // Payment Reference
                    tokens.get(10).trim()  // Cash Received
                );
            }
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing sales line: " + line, e);
            return null;
        }
    }

    /**
     * Calculate and display all analytics from real order data
     */
    private void calculateAndDisplayAnalytics() {
        logger.info("📊 Calculating analytics from " + allOrders.size() + " orders...");
        
        Platform.runLater(() -> {
            try {
                updateKPICards();
                updateCharts();
                logger.info("✅ Analytics updated successfully");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "❌ Error updating analytics display", e);
            }
        });
    }

    /**
     * Update KPI cards with real calculated values using professional POS separation
     */
    private void updateKPICards() {
        // Get filtered data for current time period
        List<RecentOrder> filteredOrders = getFilteredOrders();
        List<SalesRecord> filteredSales = getFilteredSales();
        
        // Order management metrics (from filtered orders)
        int totalOrders = filteredOrders.size();
        long completedOrders = filteredOrders.stream()
            .filter(order -> "Completed".equals(order.getStatus()))
            .count();
        double completionRate = totalOrders > 0 ? (completedOrders * 100.0) / totalOrders : 0.0;
        
        // Revenue metrics (from filtered sales - only completed transactions)
        double totalRevenue = filteredSales.stream()
            .mapToDouble(sale -> parseAmount(sale.getSaleAmount()))
            .sum();
        
        // Calculate average order value from filtered sales data
        double avgOrderValue = filteredSales.size() > 0 ? totalRevenue / filteredSales.size() : 0.0;
        
        // Find best selling item from filtered sales data
        String bestSellingItem = findBestSellingItem(filteredSales);
        
        // Calculate customer retention from filtered sales data
        double customerRetention = calculateCustomerRetention(filteredSales);
        
        // Calculate growth rate from filtered sales data
        double growthRate = calculateGrowthRate(filteredSales);
        
        // Update labels (check if they exist in FXML)
        if (totalOrdersLabel != null) {
            totalOrdersLabel.setText(String.format("%,d", totalOrders));
        }
        
        if (totalRevenueLabel != null) {
            totalRevenueLabel.setText(String.format("₱%,.0f", totalRevenue));
        }
        
        if (avgOrderValueLabel != null) {
            avgOrderValueLabel.setText(String.format("₱%.1f", avgOrderValue));
        }
        
        if (completionRateLabel != null) {
            completionRateLabel.setText(String.format("%.1f%%", completionRate));
        }
        
        if (bestSellingItemLabel != null) {
            bestSellingItemLabel.setText(bestSellingItem);
        }
        
        if (customerRetentionLabel != null) {
            customerRetentionLabel.setText(String.format("%.0f%%", customerRetention));
        }
        
        if (growthRateLabel != null) {
            String growthText = growthRate >= 0 ? String.format("+%.1f%%", growthRate) : String.format("%.1f%%", growthRate);
            growthRateLabel.setText(growthText);
        }
        
        logger.info("💰 Analytics: " + totalOrders + " orders, ₱" + String.format("%.0f", totalRevenue) + " revenue, " + String.format("%.1f%%", completionRate) + " completion rate");
    }

    /**
     * Update charts with real data
     */
    private void updateCharts() {
        updateDailySalesChart();
        updateRevenueFlavorChart();
        updateTopFlavorsChart();
        updateMonthlyRevenueChart();
    }

    /**
     * Update daily sales trend chart using filtered sales data (professional POS approach)
     */
    private void updateDailySalesChart() {
        if (dailySalesChart == null) return;
        
        try {
            dailySalesChart.getData().clear();
            
            // Configure Y-axis with intervals of 1000, ranging from 0 to 10,000
            javafx.scene.chart.NumberAxis yAxis = (javafx.scene.chart.NumberAxis) dailySalesChart.getYAxis();
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(10000);
            yAxis.setTickUnit(1000);
            yAxis.setAutoRanging(false);
            
            // Get filtered sales for current time period
            List<SalesRecord> filteredSales = getFilteredSales();
            
            // Group sales by date and calculate daily revenue from filtered sales
            Map<String, Double> dailySales = filteredSales.stream()
                .collect(Collectors.groupingBy(
                    sale -> extractDateFromSale(sale),
                    LinkedHashMap::new,
                    Collectors.summingDouble(sale -> parseAmount(sale.getSaleAmount()))
                ));
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Daily Sales");
            
            // Sort by date and add to chart
            dailySales.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                });
            
            dailySalesChart.getData().add(series);
            dailySalesChart.setLegendVisible(false);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error updating daily sales chart", e);
        }
    }

    /**
     * Update revenue per flavor pie chart using filtered sales data
     */
    private void updateRevenueFlavorChart() {
        if (revenueFlavorChart == null) return;
        
        try {
            revenueFlavorChart.getData().clear();
            
            // Get filtered sales for current time period
            List<SalesRecord> filteredSales = getFilteredSales();
            
            // Calculate revenue by flavor from filtered sales data
            Map<String, Double> flavorRevenue = new HashMap<>();
            
            for (SalesRecord sale : filteredSales) {
                String[] items = sale.getItemsSold().split(";");
                double saleAmount = parseAmount(sale.getSaleAmount());
                double itemValue = saleAmount / items.length; // Approximate per item
                
                for (String item : items) {
                    String flavor = extractFlavorName(item.trim());
                    flavorRevenue.put(flavor, flavorRevenue.getOrDefault(flavor, 0.0) + itemValue);
                }
            }
            
            // Add to pie chart
            flavorRevenue.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(8) // Top 8 flavors
                .forEach(entry -> {
                    PieChart.Data slice = new PieChart.Data(
                        entry.getKey() + " (₱" + String.format("%.0f", entry.getValue()) + ")",
                        entry.getValue()
                    );
                    revenueFlavorChart.getData().add(slice);
                });
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error updating revenue flavor chart", e);
        }
    }

    /**
     * Update top selling flavors bar chart using filtered sales data
     */
    private void updateTopFlavorsChart() {
        if (topFlavorsChart == null) return;
        
        try {
            topFlavorsChart.getData().clear();
            
            // Get filtered sales for current time period
            List<SalesRecord> filteredSales = getFilteredSales();
            
            // Count sales by flavor from filtered sales data
            Map<String, Integer> flavorCounts = new HashMap<>();
            
            for (SalesRecord sale : filteredSales) {
                String[] items = sale.getItemsSold().split(";");
                for (String item : items) {
                    String flavor = extractFlavorName(item.trim());
                    int quantity = extractQuantity(item.trim());
                    flavorCounts.put(flavor, flavorCounts.getOrDefault(flavor, 0) + quantity);
                }
            }
            
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName("Units Sold");
            
            // Add top flavors to chart
            flavorCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getValue(), entry.getKey()));
                });
            
            topFlavorsChart.getData().add(series);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error updating top flavors chart", e);
        }
    }

    /**
     * Update monthly revenue trend chart using filtered sales data
     */
    private void updateMonthlyRevenueChart() {
        if (monthlyRevenueChart == null) return;
        
        try {
            monthlyRevenueChart.getData().clear();
            
            // Get filtered sales for current time period
            List<SalesRecord> filteredSales = getFilteredSales();
            
            // Group by month and calculate revenue from filtered sales data
            Map<String, Double> monthlyRevenue = filteredSales.stream()
                .collect(Collectors.groupingBy(
                    sale -> extractMonthFromSale(sale),
                    LinkedHashMap::new,
                    Collectors.summingDouble(sale -> parseAmount(sale.getSaleAmount()))
                ));
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Monthly Revenue");
            
            monthlyRevenue.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                });
            
            monthlyRevenueChart.getData().add(series);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error updating monthly revenue chart", e);
        }
    }

    // Helper methods
    private double parseAmount(String amountStr) {
        try {
            return Double.parseDouble(amountStr.replace("₱", "").replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String extractDateFromOrder(RecentOrder order) {
        try {
            String dateStr = order.getOrderDate();
            // Convert "Jun 24, 2025 9:09 PM" to "Jun 24"
            if (dateStr.contains(",")) {
                return dateStr.substring(0, dateStr.indexOf(",")).trim();
            }
            return dateStr;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String extractDateFromSale(SalesRecord sale) {
        try {
            String dateStr = sale.getSaleDateTime();
            // Convert "Jun 24, 2025 9:09 PM" to "Jun 24"
            if (dateStr.contains(",")) {
                return dateStr.substring(0, dateStr.indexOf(",")).trim();
            }
            return dateStr;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String extractMonthFromOrder(RecentOrder order) {
        try {
            String dateStr = order.getOrderDate();
            if (dateStr.contains(",")) {
                String[] parts = dateStr.split(",");
                if (parts.length >= 2) {
                    String month = parts[0].trim().split(" ")[0]; // Get month name
                    String year = parts[1].trim().split(" ")[0]; // Get year
                    return month + " " + year;
                }
            }
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String extractMonthFromSale(SalesRecord sale) {
        try {
            String dateStr = sale.getSaleDateTime();
            if (dateStr.contains(",")) {
                String[] parts = dateStr.split(",");
                if (parts.length >= 2) {
                    String month = parts[0].trim().split(" ")[0]; // Get month name
                    String year = parts[1].trim().split(" ")[0]; // Get year
                    return month + " " + year;
                }
            }
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String extractFlavorName(String item) {
        try {
            // Handle formats like "5x Tropiham" or "Tropiham x5"
            String cleaned = item.replaceAll("\\d+x\\s*", "").replaceAll("\\s*x\\s*\\d+", "").trim();
            return cleaned.isEmpty() ? "Unknown" : cleaned;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private int extractQuantity(String item) {
        try {
            if (item.contains("x")) {
                String[] parts = item.split("x");
                for (String part : parts) {
                    String trimmed = part.trim();
                    if (trimmed.matches("\\d+")) {
                        return Integer.parseInt(trimmed);
                    }
                }
            }
            return 1; // Default quantity
        } catch (Exception e) {
            return 1;
        }
    }

    private String findBestSellingItem(List<SalesRecord> salesData) {
        Map<String, Integer> itemCounts = new HashMap<>();
        
        for (SalesRecord sale : salesData) {
            String[] items = sale.getItemsSold().split(";");
            for (String item : items) {
                String flavor = extractFlavorName(item.trim());
                int quantity = extractQuantity(item.trim());
                itemCounts.put(flavor, itemCounts.getOrDefault(flavor, 0) + quantity);
            }
        }
        
        return itemCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("No data");
    }

    private double calculateCustomerRetention(List<SalesRecord> salesData) {
        Set<String> uniqueCustomers = salesData.stream()
            .map(SalesRecord::getContactNumber)
            .collect(Collectors.toSet());
        
        long repeatCustomers = salesData.stream()
            .collect(Collectors.groupingBy(SalesRecord::getContactNumber, Collectors.counting()))
            .values().stream()
            .filter(count -> count > 1)
            .count();
        
        return uniqueCustomers.size() > 0 ? (repeatCustomers * 100.0) / uniqueCustomers.size() : 0.0;
    }

    private double calculateGrowthRate(List<SalesRecord> salesData) {
        try {
            LocalDate now = LocalDate.now();
            LocalDate weekAgo = now.minusDays(7);
            LocalDate twoWeeksAgo = now.minusDays(14);
            
            double lastWeekRevenue = salesData.stream()
                .filter(sale -> isSaleInDateRange(sale, weekAgo, now))
                .mapToDouble(sale -> parseAmount(sale.getSaleAmount()))
                .sum();
            
            double previousWeekRevenue = salesData.stream()
                .filter(sale -> isSaleInDateRange(sale, twoWeeksAgo, weekAgo))
                .mapToDouble(sale -> parseAmount(sale.getSaleAmount()))
                .sum();
            
            if (previousWeekRevenue > 0) {
                return ((lastWeekRevenue - previousWeekRevenue) / previousWeekRevenue) * 100.0;
            }
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private boolean isOrderInDateRange(RecentOrder order, LocalDate start, LocalDate end) {
        try {
            LocalDate orderDate = parseOrderDate(order.getOrderDate());
            if (orderDate == null) return false;
            
            return !orderDate.isBefore(start) && !orderDate.isAfter(end);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error checking order date range: " + order.getOrderDate(), e);
            return false;
        }
    }

    private boolean isSaleInDateRange(SalesRecord sale, LocalDate start, LocalDate end) {
        try {
            LocalDate saleDate = parseOrderDate(sale.getSaleDateTime());
            if (saleDate == null) return false;
            
            return !saleDate.isBefore(start) && !saleDate.isAfter(end);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error checking sale date range: " + sale.getSaleDateTime(), e);
            return false;
        }
    }
    
    /**
     * Parse date string from order/sale data
     * Handles format: "Jun 24, 2025 9:09 PM"
     */
    private LocalDate parseOrderDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return null;
            }
            
            // Extract date part before comma: "Jun 24, 2025 9:09 PM" -> "Jun 24, 2025"
            if (dateStr.contains(",")) {
                String[] parts = dateStr.split(",");
                if (parts.length >= 2) {
                    String datePart = parts[0].trim() + ", " + parts[1].trim().split(" ")[0];
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
                    return LocalDate.parse(datePart, formatter);
                }
            }
            
            return null;
        } catch (DateTimeParseException e) {
            logger.log(Level.WARNING, "Failed to parse date: " + dateStr, e);
            return null;
        }
    }
    
    /**
     * Get date range for the specified time period
     * @param timePeriod The time period string (e.g., "Last 7 Days")
     * @return Array with [startDate, endDate]
     */
    private LocalDate[] getDateRangeForPeriod(String timePeriod) {
        LocalDate now = LocalDate.now();
        LocalDate start, end = now;
        
        switch (timePeriod) {
            case "This Week":
                start = now.minusDays(now.getDayOfWeek().getValue() - 1); // Monday
                break;
            case "Last 7 Days":
                start = now.minusDays(6);
                break;
            case "Last 14 Days":
                start = now.minusDays(13);
                break;
            case "Last 30 Days":
                start = now.minusDays(29);
                break;
            case "Last 90 Days":
                start = now.minusDays(89);
                break;
            case "This Month":
                start = now.withDayOfMonth(1);
                break;
            case "This Year":
                start = now.withDayOfYear(1);
                break;
            default:
                // Default to last 30 days
                start = now.minusDays(29);
                break;
        }
        
        return new LocalDate[]{start, end};
    }
    
    /**
     * Filter orders based on current time period
     */
    private List<RecentOrder> getFilteredOrders() {
        LocalDate[] dateRange = getDateRangeForPeriod(currentTimePeriod);
        return allOrders.stream()
                .filter(order -> isOrderInDateRange(order, dateRange[0], dateRange[1]))
                .collect(Collectors.toList());
    }
    
    /**
     * Filter sales based on current time period
     */
    private List<SalesRecord> getFilteredSales() {
        LocalDate[] dateRange = getDateRangeForPeriod(currentTimePeriod);
        return allSales.stream()
                .filter(sale -> isSaleInDateRange(sale, dateRange[0], dateRange[1]))
                .collect(Collectors.toList());
    }

    private void showErrorState() {
        Platform.runLater(() -> {
            // Show error message in analytics if data can't be loaded
            logger.warning("⚠️ Showing error state for analytics");
        });
    }

    /**
     * Refresh all data - can be called when orders are updated
     */
    public void refreshData(String timePeriod) {
        logger.info("🔄 Refreshing analytics data for period: " + timePeriod);
        
        dataService.refreshData();
        allOrders = dataService.getAllOrders();
        allSales = dataService.getAllSales();
        calculateAndDisplayAnalytics();
        
        logger.info("✅ Analytics data refresh completed");
    }

    /**
     * Public method to refresh analytics when called from other controllers
     */
    public void refreshAnalytics() {
        refreshData("current");
    }
    
    /**
     * Refresh analytics data for a specific time period
     * Called from HeaderController when user changes time filter
     * @param timePeriod The selected time period (e.g., "Last 7 Days", "This Month")
     */
    public void refreshDataForTimePeriod(String timePeriod) {
        logger.info("📅 Refreshing analytics for time period: " + timePeriod);
        
        Platform.runLater(() -> {
            try {
                currentTimePeriod = timePeriod;
                
                // Reload data from files
                dataService.refreshData();
                allOrders = dataService.getAllOrders();
                allSales = dataService.getAllSales();
                
                // Filter data based on time period and recalculate analytics
                calculateAndDisplayAnalytics();
                
                logger.info("✅ Analytics refreshed successfully for period: " + timePeriod);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "❌ Error refreshing analytics for time period: " + timePeriod, e);
                showErrorState();
            }
        });
    }
}
