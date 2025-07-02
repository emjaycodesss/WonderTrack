package com.example.wondertrackxd.controller.overview;

import com.example.wondertrackxd.controller.model.RecentOrder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

// PDFBox imports for receipt generation
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Enhanced Controller for the Overview page that handles real-time analytics and KPI data
 * Manages TableView, KPI cards, and charts with live data from orders.txt
 * Provides comprehensive analytics dashboard with automatic data updates
 */
public class OverviewController {

    // Logger instance for tracking operations and debugging
    private static final Logger logger = Logger.getLogger(OverviewController.class.getName());

    // FXML injected components
    @FXML private TableView<RecentOrder> recentOrdersTable;
    
    // KPI Cards Labels
    @FXML private Label grossSalesAmount;
    @FXML private Label grossSalesDesc;
    @FXML private Label pendingOrdersCount;
    @FXML private Label pendingOrdersDesc;
    @FXML private Label completedOrdersCount;
    @FXML private Label completedOrdersDesc;
    @FXML private Label totalOrdersCount;
    @FXML private Label totalOrdersDesc;
    @FXML private Label totalOrdersDesc1;
    
    // Charts
    @FXML private LineChart<String, Number> salesTrendChart;
    @FXML private PieChart flavorDistributionChart;

    // Data storage
    private List<RecentOrder> allOrders = new ArrayList<>();
    private ScheduledExecutorService scheduler;
    private final DecimalFormat currencyFormat = new DecimalFormat("₱#,##0.00");
    
    // Receipt generation constant
    private static final String RECEIPT_SEPARATOR = "--------------------------------------";

    /**
     * Initialize the Overview Controller with real-time, responsive components
     * Sets up KPI updates, charts, table, and automatic refresh schedule for TODAY's data
     */
    @FXML
    public void initialize() {
        logger.info("🏗️ Initializing Enhanced Overview Controller with real-time, responsive analytics...");
        
        try {
            // Setup components for real-time data
            setupTable();
            loadAndProcessOrderData();
            updateAllKPIs();
            setupCharts();
            
            // Start real-time updates for responsive KPIs
            startAutoRefresh();
            
            logger.info("✅ Enhanced Overview initialization completed successfully with real-time updates enabled");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during Overview initialization", e);
        }
    }

    /**
     * Load and process all order data from orders.txt file
     * Parses data, calculates analytics, and prepares for display
     */
    private void loadAndProcessOrderData() {
        logger.info("📊 Loading and processing order data for analytics...");
        
        try {
            String filePath = "txtFiles/orders.txt";
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
            
            if (inputStream == null) {
                inputStream = getClass().getResourceAsStream("/" + filePath);
            }
            
            if (inputStream == null) {
                logger.severe("❌ Orders file not found: " + filePath);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                allOrders = reader.lines()
                    .filter(line -> !line.trim().isEmpty())
                    .filter(line -> !line.trim().startsWith("#"))
                    .map(this::parseOrderLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

                // Sort orders by date (newest first) to ensure proper chronological order
                allOrders.sort((o1, o2) -> {
                    // First compare by date (newest first)
                    LocalDate date1 = parseOrderDate(o1.getOrderDate());
                    LocalDate date2 = parseOrderDate(o2.getOrderDate());
                    int dateComparison = date2.compareTo(date1); // Reverse for newest first
                    
                    if (dateComparison != 0) {
                        return dateComparison;
                    }
                    
                    // If same date, sort by order ID sequence (highest number first)
                    String orderId1 = o1.getOrderId();
                    String orderId2 = o2.getOrderId();
                    
                    // Extract sequence numbers from order IDs (e.g., "001" from "WP20250624-001")
                    try {
                        String seq1 = orderId1.substring(orderId1.lastIndexOf('-') + 1);
                        String seq2 = orderId2.substring(orderId2.lastIndexOf('-') + 1);
                        int num1 = Integer.parseInt(seq1);
                        int num2 = Integer.parseInt(seq2);
                        return Integer.compare(num2, num1); // Reverse for highest first
                    } catch (Exception e) {
                        // Fallback to string comparison if parsing fails
                        return orderId2.compareTo(orderId1);
                    }
                });

                logger.info("📈 Successfully loaded " + allOrders.size() + " orders (sorted newest first)");
                
                // Update all components with new data
                updateAllKPIs();
                setupCharts();
                updateRecentOrdersTable();
                
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error loading order data", e);
        }
    }

    /**
     * Parse a single order line from the text file into RecentOrder object
     * @param line Raw line from orders.txt file
     * @return RecentOrder object or null if parsing fails
     */
    private RecentOrder parseOrderLine(String line) {
        try {
            List<String> tokens = new ArrayList<>();
            StringBuilder currentToken = new StringBuilder();
            boolean inQuotes = false;
            
            // Parse CSV with quote handling
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
            
            if (tokens.size() < 8) {
                logger.warning("⚠️ Skipping malformed line with " + tokens.size() + " tokens");
                return null;
            }
            
            // Handle different formats: old (8 fields), with contact (9 fields), and new (11 fields)
            if (tokens.size() >= 11) {
                // New format with contact number and digital payment details
                return new RecentOrder(
                    tokens.get(0).trim(),                           // Order ID
                    tokens.get(1).trim(),                           // Customer Name
                    tokens.get(2).trim(),                           // Contact Number
                    tokens.get(3).replace("\"", "").trim(),         // Items Ordered
                    tokens.get(4).trim(),                           // Total Items
                    tokens.get(5).trim(),                           // Total Amount
                    tokens.get(6).trim(),                           // Payment Method
                    tokens.get(7).replace("\"", "").trim(),         // Date/Time
                    tokens.get(8).trim(),                           // Status
                    tokens.get(9).replace("\"", "").trim(),         // Reference Number
                    tokens.get(10).replace("\"", "").trim()         // Timestamp
                );
            } else if (tokens.size() >= 10) {
                // Old format with digital payment details but no contact number
                return new RecentOrder(
                    tokens.get(0).trim(),                           // Order ID
                    tokens.get(1).trim(),                           // Customer Name
                    "",                                             // Contact Number (empty)
                    tokens.get(2).replace("\"", "").trim(),         // Items Ordered
                    tokens.get(3).trim(),                           // Total Items
                    tokens.get(4).trim(),                           // Total Amount
                    tokens.get(5).trim(),                           // Payment Method
                    tokens.get(6).replace("\"", "").trim(),         // Date/Time
                    tokens.get(7).trim(),                           // Status
                    tokens.get(8).replace("\"", "").trim(),         // Reference Number
                    tokens.get(9).replace("\"", "").trim()          // Timestamp
                );
            } else if (tokens.size() >= 9) {
                // Format with contact number but no digital payment details
                return new RecentOrder(
                    tokens.get(0).trim(),                           // Order ID
                    tokens.get(1).trim(),                           // Customer Name
                    tokens.get(2).trim(),                           // Contact Number
                    tokens.get(3).replace("\"", "").trim(),         // Items Ordered
                    tokens.get(4).trim(),                           // Total Items
                    tokens.get(5).trim(),                           // Total Amount
                    tokens.get(6).trim(),                           // Payment Method
                    tokens.get(7).replace("\"", "").trim(),         // Date/Time
                    tokens.get(8).trim()                            // Status
                );
            } else {
                // Old format without contact number and digital payment details
                return new RecentOrder(
                    tokens.get(0).trim(),                           // Order ID
                    tokens.get(1).trim(),                           // Customer Name
                    tokens.get(2).replace("\"", "").trim(),         // Items Ordered
                    tokens.get(3).trim(),                           // Total Items
                    tokens.get(4).trim(),                           // Total Amount
                    tokens.get(5).trim(),                           // Payment Method
                    tokens.get(6).replace("\"", "").trim(),         // Date/Time
                    tokens.get(7).trim()                            // Status
                );
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "⚠️ Error parsing order line: " + line, e);
            return null;
        }
    }

    /**
     * Update all KPI cards with real-time calculated data for TODAY only
     * Calculates total orders with status breakdown, gross sales with comparison, average order value, and completion rate
     * All calculations are based on TODAY's data and update in real-time
     */
    private void updateAllKPIs() {
        logger.info("📊 Updating all KPI cards with TODAY's real-time data...");
        
        try {
            // Get today's and yesterday's dates
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            
            // Filter orders for today and yesterday
            List<RecentOrder> todaysOrders = allOrders.stream()
                .filter(order -> {
                    LocalDate orderDate = parseOrderDate(order.getOrderDate());
                    return orderDate != null && orderDate.equals(today);
                })
                .collect(Collectors.toList());
            
            List<RecentOrder> yesterdaysOrders = allOrders.stream()
                .filter(order -> {
                    LocalDate orderDate = parseOrderDate(order.getOrderDate());
                    return orderDate != null && orderDate.equals(yesterday);
                })
                .collect(Collectors.toList());
            
            // Calculate status counts for today
            int pendingCount = countOrdersByStatusForOrders(todaysOrders, "Pending");
            int inProgressCount = countOrdersByStatusForOrders(todaysOrders, "In-Progress");
            int completedCount = countOrdersByStatusForOrders(todaysOrders, "Completed");
            int cancelledCount = countOrdersByStatusForOrders(todaysOrders, "Cancelled");
            int totalCount = todaysOrders.size();
            
            // Calculate gross sales for today and yesterday
            double todaysGrossSales = calculateGrossSalesForOrders(todaysOrders);
            double yesterdaysGrossSales = calculateGrossSalesForOrders(yesterdaysOrders);
            
            // Calculate average order value (today's completed orders only)
            List<RecentOrder> todaysCompletedOrders = todaysOrders.stream()
                .filter(order -> "Completed".equalsIgnoreCase(order.getOrderStatus()))
                .collect(Collectors.toList());
            double avgOrderValue = todaysCompletedOrders.size() > 0 ? 
                todaysGrossSales / todaysCompletedOrders.size() : 0.0;
            
            // Calculate completion rate
            double completionRate = totalCount > 0 ? (double) completedCount / totalCount * 100 : 0.0;
            
            // Calculate sales comparison with yesterday
            String salesComparison;
            if (yesterdaysGrossSales == 0 && todaysGrossSales > 0) {
                salesComparison = "↑ New sales today";
            } else if (yesterdaysGrossSales == 0 && todaysGrossSales == 0) {
                salesComparison = "No sales data";
            } else if (todaysGrossSales > yesterdaysGrossSales) {
                double increase = ((todaysGrossSales - yesterdaysGrossSales) / yesterdaysGrossSales) * 100;
                salesComparison = String.format("↑ %.1f%% more than yesterday", increase);
            } else if (todaysGrossSales < yesterdaysGrossSales) {
                double decrease = ((yesterdaysGrossSales - todaysGrossSales) / yesterdaysGrossSales) * 100;
                salesComparison = String.format("↓ %.1f%% less than yesterday", decrease);
            } else {
                salesComparison = "→ Same as yesterday";
            }
            
            // Update KPI labels on JavaFX thread
            Platform.runLater(() -> {
                // Total Orders with status breakdown
                totalOrdersCount.setText(String.valueOf(totalCount));
                totalOrdersDesc.setText(String.format("Pending: %d | In Progress: %d", pendingCount, inProgressCount));
                totalOrdersDesc1.setText(String.format("Completed: %d | Cancelled: %d", completedCount, cancelledCount));
                
                // Gross Sales with comparison
                grossSalesAmount.setText(currencyFormat.format(todaysGrossSales));
                grossSalesDesc.setText(salesComparison);
                
                // Average Order Value
                pendingOrdersCount.setText(currencyFormat.format(avgOrderValue));
                pendingOrdersDesc.setText("Today's avg per transaction");
                
                // Completion Rate
                completedOrdersCount.setText(String.format("%.1f%%", completionRate));
                completedOrdersDesc.setText(String.format("Today: %d out of %d orders", completedCount, totalCount));
            });
            
            logger.info("✅ Real-time KPI cards updated for TODAY (" + today + "): " + totalCount + " total orders " +
                       "(Pending:" + pendingCount + ", In-Progress:" + inProgressCount + ", Completed:" + completedCount + ", Cancelled:" + cancelledCount + "), " +
                       "₱" + String.format("%.2f", todaysGrossSales) + " gross sales, " +
                       "₱" + String.format("%.2f", avgOrderValue) + " avg order value, " +
                       String.format("%.1f%%", completionRate) + " completion rate");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error updating KPI cards", e);
        }
    }

    /**
     * Calculate total gross sales from completed orders in the given list
     * @param orders List of orders to calculate sales from
     * @return Total sales amount as double
     */
    private double calculateGrossSalesForOrders(List<RecentOrder> orders) {
                return orders.stream()
                .filter(order -> "Completed".equalsIgnoreCase(order.getOrderStatus()))
            .mapToDouble(order -> {
                try {
                    String amount = order.getTotalAmount()
                        .replace("₱", "")
                        .replace(",", "")
                        .trim();
                    return Double.parseDouble(amount);
                } catch (NumberFormatException e) {
                    logger.warning("⚠️ Invalid amount format: " + order.getTotalAmount());
                    return 0.0;
                }
            })
            .sum();
    }

    /**
     * Count orders by specific status from the given list
     * @param orders List of orders to count from
     * @param status Status to count (e.g., "Completed", "Pending", "Cancelled")
     * @return Number of orders with the specified status
     */
    private int countOrdersByStatusForOrders(List<RecentOrder> orders, String status) {
                return (int) orders.stream()
                .filter(order -> status.equalsIgnoreCase(order.getOrderStatus()))
            .count();
    }

    /**
     * Calculate total gross sales from all completed orders
     * @return Total sales amount as double
     */
    private double calculateGrossSales() {
        return calculateGrossSalesForOrders(allOrders);
    }

    /**
     * Count orders by specific status
     * @param status Status to count (e.g., "Completed", "Pending", "Cancelled")
     * @return Number of orders with the specified status
     */
    private int countOrdersByStatus(String status) {
        return countOrdersByStatusForOrders(allOrders, status);
    }

    /**
     * Setup and populate charts with real-time data
     * Creates sales trend line chart and flavor distribution pie chart
     */
    private void setupCharts() {
        logger.info("📈 Setting up charts with real-time data...");
        
        try {
            setupSalesTrendChart();
            setupFlavorDistributionChart();
            
            logger.info("✅ Charts setup completed successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error setting up charts", e);
        }
    }

    /**
     * Setup sales trend line chart with real-time daily sales data
     * Shows sales performance over the last 7 days including today (updates in real-time)
     */
    private void setupSalesTrendChart() {
        Platform.runLater(() -> {
            try {
                // Clear existing data
                salesTrendChart.getData().clear();
                salesTrendChart.setTitle("");
                salesTrendChart.setLegendVisible(false);
                salesTrendChart.setAnimated(false); // Disable animation for better axis control
                salesTrendChart.setCreateSymbols(true);
                
                // Style the chart for better appearance
                salesTrendChart.setStyle("-fx-background-color: transparent;");
                
                // Create data series for sales trend
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Daily Sales");
                
                // Group orders by date and calculate daily sales (real-time data)
                Map<LocalDate, Double> dailySales = allOrders.stream()
                .filter(order -> "Completed".equalsIgnoreCase(order.getOrderStatus()))
                    .collect(Collectors.groupingBy(
                        order -> parseOrderDate(order.getOrderDate()),
                        Collectors.summingDouble(order -> {
                            try {
                                return Double.parseDouble(order.getTotalAmount()
                                    .replace("₱", "").replace(",", "").trim());
                            } catch (NumberFormatException e) {
                                return 0.0;
                            }
                        })
                    ));
                
                // Add data points for the last 7 days (including today)
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
                double[] todaysSalesArray = {0.0}; // Use array to make it effectively final
                
                for (int i = 6; i >= 0; i--) {
                    LocalDate date = today.minusDays(i);
                    double sales = dailySales.getOrDefault(date, 0.0);
                    String dateLabel = date.format(formatter);
                    XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(dateLabel, sales);
                    series.getData().add(dataPoint);
                    
                    if (i == 0) { // Today's data
                        todaysSalesArray[0] = sales;
                    }
                }
                
                salesTrendChart.getData().add(series);
                
                // Add hover tooltips to data points after chart is rendered
                Platform.runLater(() -> {
                    // Wait for chart to be fully rendered
                    Platform.runLater(() -> {
                        for (XYChart.Data<String, Number> data : series.getData()) {
                            if (data.getNode() != null) {
                                // Style the data point for better visibility
                                data.getNode().setStyle("-fx-background-color: #8b4513; -fx-background-radius: 5px; -fx-padding: 3px;");
                                
                                // Create tooltip with value
                                Tooltip tooltip = new Tooltip(String.format("₱%.0f", data.getYValue().doubleValue()));
                                tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #f4f1e8; -fx-text-fill: #8b4513; -fx-border-color: #8b4513; -fx-border-width: 1px;");
                                tooltip.setShowDelay(Duration.millis(100)); // Show quickly on hover
                                Tooltip.install(data.getNode(), tooltip);
                            }
                        }
                    });
                });
                
                // Configure axes AFTER adding data for proper label display
                Platform.runLater(() -> {
                    if (salesTrendChart.getXAxis() instanceof CategoryAxis) {
                        CategoryAxis xAxis = (CategoryAxis) salesTrendChart.getXAxis();
                        xAxis.setAutoRanging(true);
                        xAxis.setGapStartAndEnd(false);
                        xAxis.setTickLabelRotation(0);
                        xAxis.setTickLabelGap(5);
                        xAxis.setTickMarkVisible(true);
                        xAxis.setTickLabelsVisible(true);
                        
                        // Force refresh of axis
                        xAxis.requestAxisLayout();
                        
                        logger.info("🎯 X-axis configured with categories: " + String.join(", ", xAxis.getCategories()));
                    }
                    
                    if (salesTrendChart.getYAxis() instanceof NumberAxis) {
                        NumberAxis yAxis = (NumberAxis) salesTrendChart.getYAxis();
                        yAxis.setAutoRanging(false);
                        yAxis.setLowerBound(0);
                        yAxis.setUpperBound(10000);
                        yAxis.setTickUnit(1000);
                        yAxis.setMinorTickVisible(false);
                        yAxis.setTickMarkVisible(true);
                        yAxis.setTickLabelsVisible(true);
                        
                        // Force refresh of axis
                        yAxis.requestAxisLayout();
                        logger.info("📊 Y-axis configured with range 0-10000, increments of 1000");
                    }
                    
                    // Apply final styling and force layout refresh
                    salesTrendChart.applyCss();
                    salesTrendChart.autosize();
                    salesTrendChart.requestLayout();
                    
                    logger.info("📈 Sales trend chart updated with real-time data (last 7 days including TODAY: ₱" + 
                               String.format("%.2f", todaysSalesArray[0]) + ")");
                });
                
            } catch (Exception e) {
                logger.log(Level.WARNING, "⚠️ Error setting up sales trend chart", e);
            }
        });
    }

    /**
     * Setup flavor distribution chart with TODAY's real-time data only
     * Shows pie chart of waffle flavors ordered today with live updates
     */
    private void setupFlavorDistributionChart() {
        Platform.runLater(() -> {
            try {
                flavorDistributionChart.getData().clear();
                flavorDistributionChart.setTitle("");
                flavorDistributionChart.setLegendVisible(true);
                flavorDistributionChart.setLegendSide(Side.BOTTOM);
                flavorDistributionChart.setAnimated(true);
                flavorDistributionChart.setClockwise(true);
                flavorDistributionChart.setLabelLineLength(10);
                flavorDistributionChart.setLabelsVisible(true);
                
                // Style the chart for better appearance
                flavorDistributionChart.setStyle("-fx-background-color: transparent;");
                
                // Get today's date for real-time filtering
                LocalDate today = LocalDate.now();
                
                // Count flavor occurrences from TODAY's completed orders only
                Map<String, Integer> flavorCounts = new HashMap<>();
                
                List<RecentOrder> todaysCompletedOrders = allOrders.stream()
                    .filter(order -> {
                        // Filter for today's orders only
                        LocalDate orderDate = parseOrderDate(order.getOrderDate());
                        return orderDate != null && orderDate.equals(today);
                    })
                    .filter(order -> "Completed".equalsIgnoreCase(order.getOrderStatus()))
                    .collect(Collectors.toList());
                
                todaysCompletedOrders.forEach(order -> {
                        String items = order.getItemsOrdered();
                        // Parse items and extract flavors
                        extractFlavorsFromItems(items, flavorCounts);
                    });
                
                // Create pie chart data
                ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                
                if (flavorCounts.isEmpty()) {
                    // If no completed orders today, show a placeholder
                    pieData.add(new PieChart.Data("No completed orders today", 1));
                    logger.info("📊 Flavor distribution chart: No completed orders for TODAY (" + today + ")");
                } else {
                    flavorCounts.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .limit(6) // Show top 6 flavors
                        .forEach(entry -> {
                            pieData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
                        });
                    
                    logger.info("📊 Flavor distribution chart updated for TODAY (" + today + ") with " + 
                               flavorCounts.size() + " different flavors from " + 
                               todaysCompletedOrders.size() + " completed orders");
                }
                
                flavorDistributionChart.setData(pieData);
                
                // Apply CSS styling and auto-sizing
                flavorDistributionChart.applyCss();
                flavorDistributionChart.autosize();
                
            } catch (Exception e) {
                logger.log(Level.WARNING, "⚠️ Error setting up flavor distribution chart", e);
            }
        });
    }

    /**
     * Extract flavor names from items ordered string and count occurrences
     * @param items Items ordered string (e.g., "Oreo Overload x2; Chocolate x1")
     * @param flavorCounts Map to store flavor counts
     */
    private void extractFlavorsFromItems(String items, Map<String, Integer> flavorCounts) {
        if (items == null || items.trim().isEmpty()) return;
        
        // Split by semicolon and process each item
        String[] itemArray = items.split(";");
        for (String item : itemArray) {
            item = item.trim();
            if (item.isEmpty()) continue;
            
            // Extract flavor name (everything before " x" or just the item name)
            String flavor;
            if (item.contains(" x")) {
                flavor = item.substring(0, item.lastIndexOf(" x")).trim();
            } else {
                flavor = item.trim();
            }
            
            // Extract quantity
            int quantity = 1;
            if (item.contains(" x")) {
                try {
                    String quantityStr = item.substring(item.lastIndexOf(" x") + 2).trim();
                    // Remove any non-numeric characters except the number
                    quantityStr = quantityStr.replaceAll("[^0-9]", "");
                    if (!quantityStr.isEmpty()) {
                        quantity = Integer.parseInt(quantityStr);
                    }
                } catch (NumberFormatException e) {
                    quantity = 1;
                }
            }
            
            flavorCounts.put(flavor, flavorCounts.getOrDefault(flavor, 0) + quantity);
        }
    }

    /**
     * Parse order date from various formats including "Jan 26, 2025 3:45 PM" and new MM/DD/YYYY format
     * @param dateTimeStr The date string to parse
     * @return LocalDate if parsing successful, current date as fallback
     */
    private LocalDate parseOrderDate(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return LocalDate.now();
        
        try {
            String cleanDateStr = dateTimeStr.trim();
            
            // Handle MM/DD/YYYY H:MM AM/PM format (new standard)
            if (cleanDateStr.matches("\\d{2}/\\d{2}/\\d{4} \\d{1,2}:\\d{2} [AP]M")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.ENGLISH);
                return LocalDateTime.parse(cleanDateStr, formatter).toLocalDate();
            }
            
            // Handle different legacy date formats for backward compatibility
            if (cleanDateStr.contains("-")) {
                // Format: 2025-01-26 or 2025-01-26 15:45:00
                return LocalDate.parse(cleanDateStr.substring(0, 10));
            } else if (cleanDateStr.contains(",")) {
                // Format: "Jan 26, 2025 3:45 PM" or "Jan 26, 2025"
                DateTimeFormatter[] formatters = {
                    DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.ENGLISH),  // "Jan 26, 2025 3:45 PM"
                    DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a", Locale.ENGLISH), // "Jan 26, 2025 3:45 PM"
                    DateTimeFormatter.ofPattern("MMM d, yyyy H:mm a", Locale.ENGLISH),  // "Jan 26, 2025 15:45 PM"
                    DateTimeFormatter.ofPattern("MMM dd, yyyy H:mm a", Locale.ENGLISH), // "Jan 26, 2025 15:45 PM"
                    DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH),         // "Jan 26, 2025"
                    DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)         // "Jan 26, 2025"
                };
                
                for (DateTimeFormatter formatter : formatters) {
                    try {
                        if (formatter.toString().contains("h:mm") || formatter.toString().contains("H:mm")) {
                            return LocalDateTime.parse(cleanDateStr, formatter).toLocalDate();
                        } else {
                            return LocalDate.parse(cleanDateStr, formatter);
                        }
                    } catch (Exception ignored) {
                        // Try next formatter
                    }
                }
            }
            
            logger.warning("⚠️ Could not parse date format: " + dateTimeStr);
        } catch (Exception e) {
            logger.warning("⚠️ Error parsing date: " + dateTimeStr + " - " + e.getMessage());
        }
        
        return LocalDate.now(); // Fallback to current date
    }

    /**
     * Update recent orders table with the latest data
     * Shows the 10 most recent orders in the table
     */
    private void updateRecentOrdersTable() {
        Platform.runLater(() -> {
            try {
                // Get first 10 orders (already sorted newest first)
                List<RecentOrder> recentOrders = allOrders.size() > 10 ? 
                    allOrders.subList(0, 10) : allOrders;
                
                recentOrdersTable.setItems(FXCollections.observableArrayList(recentOrders));
                
            } catch (Exception e) {
                logger.log(Level.WARNING, "⚠️ Error updating recent orders table", e);
            }
        });
    }

    /**
     * Setup the TableView with all necessary columns and styling
     */
    private void setupTable() {
        logger.info("📋 Setting up TableView with 10 row limit...");
        try {
            createTableColumns();
            
            // Set fixed row count to show only 10 orders (plus header = 11 total rows visible)
            recentOrdersTable.setFixedCellSize(35); // Set consistent row height
            recentOrdersTable.setPrefHeight(11 * 35 + 2); // 11 rows * height + border
            recentOrdersTable.setMaxHeight(11 * 35 + 2);
            
            // Disable selection, hover, and focus effects
            recentOrdersTable.setSelectionModel(null);
            recentOrdersTable.setFocusTraversable(false);
            
            logger.info("✅ Table setup completed with 10 row limit");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error setting up table", e);
        }
    }

    /**
     * Create table columns with proper styling and cell factories
     */
    private void createTableColumns() {
        logger.info("🔧 Creating table columns with 7 columns including contact number...");

        // Order ID Column
        TableColumn<RecentOrder, String> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty());
        orderIdCol.setMinWidth(120);
        orderIdCol.setPrefWidth(140);

        // Customer Name Column
        TableColumn<RecentOrder, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setMinWidth(80);
        nameCol.setPrefWidth(100);

        // Contact Number Column
        TableColumn<RecentOrder, String> contactCol = new TableColumn<>("Contact Number");
        contactCol.setCellValueFactory(cellData -> cellData.getValue().contactNumberProperty());
        contactCol.setMinWidth(100);
        contactCol.setPrefWidth(120);

        // Total Amount Column
        TableColumn<RecentOrder, String> amountCol = new TableColumn<>("Total Amount");
        amountCol.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty());
        amountCol.setMinWidth(80);
        amountCol.setPrefWidth(100);

        // Date & Time Column
        TableColumn<RecentOrder, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());
        dateCol.setMinWidth(120);
        dateCol.setPrefWidth(140);

        // Order Status Column
        TableColumn<RecentOrder, String> statusCol = new TableColumn<>("Order Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().orderStatusProperty());
        statusCol.setMinWidth(80);
        statusCol.setPrefWidth(100);

        // Actions Column
        TableColumn<RecentOrder, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> {
            TableCell<RecentOrder, Void> cell = new TableCell<>() {
                private final Button viewReceiptBtn = new Button("View Receipt");
                
                {
                    viewReceiptBtn.setOnAction(event -> {
                        RecentOrder order = getTableView().getItems().get(getIndex());
                        handleViewReceipt(order);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(viewReceiptBtn);
                    }
                }
            };
            return cell;
        });
        actionsCol.setMinWidth(100);
        actionsCol.setPrefWidth(120);
        actionsCol.setSortable(false);

        // Add all 7 columns to table
        recentOrdersTable.getColumns().addAll(
            orderIdCol, nameCol, contactCol, amountCol, dateCol, statusCol, actionsCol
        );
            
        // Setup column width management for 7 columns
        recentOrdersTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            if (width > 0) {
                double totalMinWidth = orderIdCol.getMinWidth() + nameCol.getMinWidth() +
                        contactCol.getMinWidth() + amountCol.getMinWidth() + dateCol.getMinWidth() + 
                        statusCol.getMinWidth() + actionsCol.getMinWidth();

                if (width > totalMinWidth) {
                    double remainingWidth = width - totalMinWidth;
                    orderIdCol.setPrefWidth(orderIdCol.getMinWidth() + (remainingWidth * 0.10));
                    nameCol.setPrefWidth(nameCol.getMinWidth() + (remainingWidth * 0.13));
                    contactCol.setPrefWidth(contactCol.getMinWidth() + (remainingWidth * 0.15));
                    amountCol.setPrefWidth(amountCol.getMinWidth() + (remainingWidth * 0.12));
                    dateCol.setPrefWidth(dateCol.getMinWidth() + (remainingWidth * 0.20));
                    statusCol.setPrefWidth(statusCol.getMinWidth() + (remainingWidth * 0.12));
                    actionsCol.setPrefWidth(actionsCol.getMinWidth() + (remainingWidth * 0.18));
                }
            }
        });
        
        logger.info("✅ Table columns created successfully with 7 columns including contact number");
    }

    /**
     * Handle view receipt action - opens a new window with actual PDF image preview
     * @param order The order to view receipt for
     */
    private void handleViewReceipt(RecentOrder order) {
        logger.info("📄 Opening receipt preview for order: " + order.getOrderId());
        
        try {
            // Create new stage for receipt preview
            Stage receiptStage = new Stage();
            receiptStage.initModality(Modality.APPLICATION_MODAL);
            receiptStage.setTitle("Receipt Preview - Order " + order.getOrderId());
            receiptStage.setResizable(true);
            
            // Create main container
            VBox mainContainer = new VBox(15);
            mainContainer.setAlignment(Pos.CENTER);
            mainContainer.setPadding(new Insets(20));
            mainContainer.setStyle("-fx-background-color: #FFFAEC;");
            
            // Create loading indicator initially
            ProgressIndicator loadingIndicator = new ProgressIndicator();
            loadingIndicator.setMaxSize(50, 50);
            
            VBox contentContainer = new VBox(10);
            contentContainer.setAlignment(Pos.CENTER);
            contentContainer.getChildren().add(loadingIndicator);
            
            // Create button bar (disabled initially)
            HBox buttonBar = new HBox(15);
            buttonBar.setAlignment(Pos.CENTER);
            buttonBar.setPadding(new Insets(10, 0, 0, 0));
            
            Button exportButton = new Button("Export");
            exportButton.setStyle("-fx-background-color: #d97708; -fx-text-fill: white; " +
                                 "-fx-font-size: 14px; -fx-padding: 12px 24px; " +
                                 "-fx-border-radius: 6px; -fx-background-radius: 6px; " +
                                 "-fx-font-weight: bold; -fx-cursor: hand;");
            exportButton.setDisable(true); // Initially disabled
            exportButton.setOnAction(e -> {
                generatePDFReceipt(order);
                receiptStage.close();
            });
            
            Button closeButton = new Button("Close");
            closeButton.setStyle("-fx-background-color: #6B7280; -fx-text-fill: white; " +
                                "-fx-font-size: 14px; -fx-padding: 12px 24px; " +
                                "-fx-border-radius: 6px; -fx-background-radius: 6px; " +
                                "-fx-cursor: hand;");
            closeButton.setOnAction(e -> receiptStage.close());
            
            buttonBar.getChildren().addAll(exportButton, closeButton);
            
            // Info label
            Label infoLabel = new Label("This is your receipt preview. Click 'Export' to save as PDF to Downloads folder.");
            infoLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13px; -fx-font-style: italic;");
            infoLabel.setAlignment(Pos.CENTER);
            infoLabel.setWrapText(true);
            
            // Add components to main container
            mainContainer.getChildren().addAll(contentContainer, infoLabel, buttonBar);
            
            // Create and show scene
            Scene scene = new Scene(mainContainer, 400, 600);
            receiptStage.setScene(scene);
            receiptStage.centerOnScreen();
            receiptStage.show();
            
            // Generate PDF image in background thread
            new Thread(() -> {
                try {
                    // Generate PDF document using the same method as invoice preview
                    PDDocument document = generatePDFInMemory(order);
                    if (document != null) {
                        // Convert first page to image
                        PDFRenderer pdfRenderer = new PDFRenderer(document);
                        BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300); // 300 DPI for high quality
                        
                        // Convert to JavaFX image
                        WritableImage fxImage = SwingFXUtils.toFXImage(image, null);
                        
                        // Update UI in JavaFX thread
                        Platform.runLater(() -> {
                            try {
                                // Create image view with the actual PDF image
                                ImageView imageView = new ImageView(fxImage);
                                imageView.setPreserveRatio(true);
                                
                                // Calculate window size based on image dimensions
                                double imageWidth = fxImage.getWidth();
                                double imageHeight = fxImage.getHeight();
                                
                                // Set reasonable window size (max 85% of screen)
                                double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
                                double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();
                                
                                double maxWindowWidth = screenWidth * 0.6; // Smaller than fullscreen viewer
                                double maxWindowHeight = screenHeight * 0.8;
                                
                                // Calculate scale to fit the image properly in window
                                double scaleX = maxWindowWidth / imageWidth;
                                double scaleY = maxWindowHeight / imageHeight;
                                double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio
                                
                                // Calculate final dimensions
                                double finalImageWidth = imageWidth * scale;
                                double finalImageHeight = imageHeight * scale;
                                
                                // Set the image size
                                imageView.setFitWidth(finalImageWidth);
                                imageView.setFitHeight(finalImageHeight);
                                
                                // Create scroll pane for the image
                                ScrollPane scrollPane = new ScrollPane();
                                scrollPane.setContent(imageView);
                                scrollPane.setFitToWidth(true);
                                scrollPane.setFitToHeight(true);
                                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                                scrollPane.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-width: 1px;");
                                scrollPane.setPrefSize(finalImageWidth + 20, finalImageHeight + 20);
                                
                                // Clear loading and add image
                                contentContainer.getChildren().clear();
                                contentContainer.getChildren().add(scrollPane);
                                
                                // Enable export button
                                exportButton.setDisable(false);
                                
                                // Adjust window size to fit content
                                double windowWidth = Math.min(finalImageWidth + 60, maxWindowWidth);
                                double windowHeight = Math.min(finalImageHeight + 140, maxWindowHeight); // Extra space for buttons and info
                                
                                receiptStage.setWidth(windowWidth);
                                receiptStage.setHeight(windowHeight);
                                receiptStage.centerOnScreen();
                                
                                logger.info("✅ Receipt PDF image preview loaded successfully for order: " + order.getOrderId());
                                
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, "❌ Error displaying PDF image in receipt preview", e);
                                Platform.runLater(() -> showReceiptPreviewError(contentContainer));
                            }
                        });
                        
                        // Close the document
                        document.close();
                        
                    } else {
                        Platform.runLater(() -> showReceiptPreviewError(contentContainer));
                    }
                    
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "❌ Error generating PDF image for receipt preview", e);
                    Platform.runLater(() -> showReceiptPreviewError(contentContainer));
                }
            }).start();
            
            logger.info("✅ Receipt preview window opened, generating PDF image...");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error opening receipt preview for order: " + order.getOrderId(), e);
            showAlert(Alert.AlertType.ERROR, "Receipt Preview Error", 
                "Failed to open receipt preview. Please try again.");
        }
    }
    
    /**
     * Show error message when PDF image generation fails in receipt preview
     * @param container The container to add error message to
     */
    private void showReceiptPreviewError(VBox container) {
        container.getChildren().clear();
        Label errorLabel = new Label("Error generating receipt preview");
        errorLabel.setTextFill(Color.web("#EF4444"));
        errorLabel.setFont(Font.font("Inter Regular", 13));
        container.getChildren().add(errorLabel);
        container.setAlignment(Pos.CENTER);
    }

    /**
     * Start auto-refresh timer for real-time updates
     * Refreshes data every 15 seconds to keep information current
     */
    private void startAutoRefresh() {
        logger.info("⏰ Starting real-time auto-refresh timer...");
        
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                logger.info("🔄 Auto-refreshing overview data for real-time updates...");
                loadAndProcessOrderData();
                logger.info("✅ Real-time refresh completed successfully");
            } catch (Exception e) {
                logger.log(Level.WARNING, "⚠️ Error during auto-refresh", e);
            }
        }, 15, 15, TimeUnit.SECONDS);
        
        logger.info("✅ Real-time auto-refresh started (15-second intervals for responsive updates)");
    }

    /**
     * Cleanup method to stop the auto-refresh timer
     * Should be called when the controller is being destroyed
     */
    public void cleanup() {
        logger.info("🧹 Cleaning up OverviewController...");
        
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            logger.info("✅ Auto-refresh timer stopped");
        }
    }
    
    /**
     * Generate PDF receipt for an order and save to Downloads folder
     * @param order The order to generate PDF for
     */
    private void generatePDFReceipt(RecentOrder order) {
        logger.info("📄 Generating receipt for order: " + order.getOrderId());
        
        try {
            // Create new PDF document with custom page size
            PDDocument document = new PDDocument();
            
            // Custom page size: 3.15 inches width (227 points), auto-height
            float pageWidth = 3.15f * 72; // 3.15 inches * 72 points per inch = 227 points
            
            // Initialize fonts
            PDType1Font courierRegular = new PDType1Font(Standard14Fonts.FontName.COURIER);
            PDType1Font courierBold = new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
            
            // Calculate separator dimensions that will be used throughout
            float separatorWidth = courierRegular.getStringWidth(RECEIPT_SEPARATOR) / 1000 * 9f;
            float separatorX = (pageWidth - separatorWidth) / 2;
            
            // Calculate actual height needed based on content
            String[] items = order.getItemsOrdered().split(";");
            int itemCount = items.length;
            float lineHeight = 12f; // Line height for compact receipt
            
            // Calculate total lines needed - updated calculation
            int headerLines = 12; // Updated for new header format
            int orderDetailLines = 4; // Order ID, Date, Customer details
            int itemHeaderLines = 3; // Separator + "Items Ordered" + spacing
            int itemLines = itemCount;
            int totalSectionLines = 3; // First separator + TOTAL line + second separator
            int digitalPaymentLines = 0;
            int cashPaymentLines = 0;
            int thankYouLines = 4; // Bottom separator + thank you message
            
            // Add cash payment lines if applicable
            if ("Cash".equals(order.getPaymentMethod())) {
                cashPaymentLines = 3; // Paid By, Cash amount, Change lines
            }
            
            // Add digital payment lines if applicable
            if ("Maya".equals(order.getPaymentMethod()) || "GCash".equals(order.getPaymentMethod())) {
                digitalPaymentLines = 3; // Paid By, Ref #, Time lines
            }
            
            int totalLines = headerLines + orderDetailLines + itemHeaderLines + itemLines + totalSectionLines + cashPaymentLines + digitalPaymentLines + thankYouLines;
            float pageHeight = totalLines * lineHeight + 100; // Add padding
            
            // Create page with calculated dimensions
            PDRectangle pageSize = new PDRectangle(pageWidth, pageHeight);
            PDPage page = new PDPage(pageSize);
            document.addPage(page);
            
            // Create content stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Set margins
            float leftMargin = 10f;
            float rightMargin = 10f;
            float topMargin = 20f;
            
            // Starting Y position (from top)
            float yPosition = pageHeight - topMargin;
            
            // Header 1 - "WONDERPOFFLES" (18px, centered, Courier Bold)
            contentStream.beginText();
            contentStream.setFont(courierBold, 18f);
            String companyName = "WONDERPOFFLES";
            float companyNameWidth = courierBold.getStringWidth(companyName) / 1000 * 18f;
            float companyNameX = (pageWidth - companyNameWidth) / 2;
            contentStream.newLineAtOffset(companyNameX, yPosition);
            contentStream.showText(companyName);
            yPosition -= 20f; // 20px spacing
            
            // Address line 1
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String address1 = "Blk 23 Lot 15, San Isidro";
            float address1Width = courierRegular.getStringWidth(address1) / 1000 * 10f;
            float address1X = (pageWidth - address1Width) / 2;
            contentStream.newLineAtOffset(address1X, yPosition);
            contentStream.showText(address1);
            yPosition -= 10f; // 10px spacing for address
            
            // Address line 2
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String address2 = "Antipolo City, Rizal 1870";
            float address2Width = courierRegular.getStringWidth(address2) / 1000 * 10f;
            float address2X = (pageWidth - address2Width) / 2;
            contentStream.newLineAtOffset(address2X, yPosition);
            contentStream.showText(address2);
            yPosition -= 10f; // 10px spacing for address
            
            // Contact line
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String contact = "Contact: 09123456789";
            float contactWidth = courierRegular.getStringWidth(contact) / 1000 * 10f;
            float contactX = (pageWidth - contactWidth) / 2;
            contentStream.newLineAtOffset(contactX, yPosition);
            contentStream.showText(contact);
            yPosition -= 20f; // 20px spacing after contact
            
            // Order ID
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 11f);
            String orderIdText = "Order ID: " + order.getOrderId();
            float orderIdWidth = courierBold.getStringWidth(orderIdText) / 1000 * 11f;
            float orderIdX = (pageWidth - orderIdWidth) / 2;
            contentStream.newLineAtOffset(orderIdX, yPosition);
            contentStream.showText(orderIdText);
            yPosition -= 15f; // 15px spacing
            
            // Date and Time
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String dateTimeText = "Date: " + order.getOrderDate();
            float dateTimeWidth = courierRegular.getStringWidth(dateTimeText) / 1000 * 10f;
            float dateTimeX = (pageWidth - dateTimeWidth) / 2;
            contentStream.newLineAtOffset(dateTimeX, yPosition);
            contentStream.showText(dateTimeText);
            yPosition -= 15f; // 15px spacing
            
            // Customer info (if available)
            if (order.getName() != null && !order.getName().isEmpty()) {
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                String customerText = "Customer: " + order.getName();
                float customerWidth = courierRegular.getStringWidth(customerText) / 1000 * 10f;
                float customerX = (pageWidth - customerWidth) / 2;
                contentStream.newLineAtOffset(customerX, yPosition);
                contentStream.showText(customerText);
                yPosition -= 15f;
            }
            
            // Separator before items
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 20f; // 20px spacing after separator
            
            // "Items Ordered" header
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 12f);
            String itemsHeader = "Items Ordered";
            float itemsHeaderWidth = courierBold.getStringWidth(itemsHeader) / 1000 * 12f;
            float itemsHeaderX = (pageWidth - itemsHeaderWidth) / 2;
            contentStream.newLineAtOffset(itemsHeaderX, yPosition);
            contentStream.showText(itemsHeader);
            yPosition -= 20f; // 20px spacing after header
            
            // Items list
            for (String item : items) {
                if (!item.trim().isEmpty()) {
                    Object[] itemData = parseItemWithPrice(item.trim());
                    String qty = itemData[0].toString();
                    String name = itemData[1].toString();
                    double unitPrice = (Double) itemData[2];
                    double totalPrice = (Double) itemData[3];
                    
                    // Format item line: "2x Beef Waffle"
                    String itemLine = qty + "x " + name;
                    
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.setFont(courierRegular, 10f);
                    contentStream.newLineAtOffset(leftMargin, yPosition);
                    contentStream.showText(itemLine);
                    
                    // Amount on the right
                    String amountStr = String.format("PHP %.2f", totalPrice);
                    float amountWidth = courierRegular.getStringWidth(amountStr) / 1000 * 10f;
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.setFont(courierRegular, 10f);
                    contentStream.newLineAtOffset(pageWidth - rightMargin - amountWidth, yPosition);
                    contentStream.showText(amountStr);
                    
                    yPosition -= 12f; // 12px spacing between items
                }
            }
            
            // First separator after table
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 20f; // 20px spacing after separator
            
            // TOTAL section - "TOTAL" left aligned, amount right aligned
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 18f);
            String totalLabel = "TOTAL";
            contentStream.newLineAtOffset(leftMargin, yPosition);
            contentStream.showText(totalLabel);
            
            // Calculate and display total amount
            double totalAmount = calculateOrderTotal(order);
            String totalAmountStr = String.format("PHP %.2f", totalAmount);
            float totalAmountWidth = courierBold.getStringWidth(totalAmountStr) / 1000 * 18f;
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 18f);
            contentStream.newLineAtOffset(pageWidth - rightMargin - totalAmountWidth, yPosition);
            contentStream.showText(totalAmountStr);
            yPosition -= 20f; // 20px spacing after total
            
            // Second separator after total
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 20f; // 20px spacing after separator
            
            // Payment method information
            if ("Cash".equals(order.getPaymentMethod())) {
                // Cash payment details
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 13f);
                String paidByText = "Paid By: Cash";
                float paidByWidth = courierRegular.getStringWidth(paidByText) / 1000 * 13f;
                float paidByX = (pageWidth - paidByWidth) / 2;
                contentStream.newLineAtOffset(paidByX, yPosition);
                contentStream.showText(paidByText);
                yPosition -= 15f;
                
                // Cash amount
                double cashAmount = parseCashAmount(order);
                String cashAmountText = String.format("Cash: PHP %.2f", cashAmount);
                float cashAmountWidth = courierRegular.getStringWidth(cashAmountText) / 1000 * 13f;
                float cashAmountX = (pageWidth - cashAmountWidth) / 2;
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 13f);
                contentStream.newLineAtOffset(cashAmountX, yPosition);
                contentStream.showText(cashAmountText);
                yPosition -= 15f;
                
                // Change amount
                double changeAmount = cashAmount - totalAmount;
                String changeAmountText = String.format("Change: PHP %.2f", changeAmount);
                float changeAmountWidth = courierRegular.getStringWidth(changeAmountText) / 1000 * 13f;
                float changeAmountX = (pageWidth - changeAmountWidth) / 2;
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 13f);
                contentStream.newLineAtOffset(changeAmountX, yPosition);
                contentStream.showText(changeAmountText);
                yPosition -= 20f;
                
            } else if ("Maya".equals(order.getPaymentMethod()) || "GCash".equals(order.getPaymentMethod())) {
                // Digital payment details
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 13f);
                String paidByText = "Paid By: " + order.getPaymentMethod();
                float paidByWidth = courierRegular.getStringWidth(paidByText) / 1000 * 13f;
                float paidByX = (pageWidth - paidByWidth) / 2;
                contentStream.newLineAtOffset(paidByX, yPosition);
                contentStream.showText(paidByText);
                yPosition -= 15f;
                
                // Reference number
                String refNumber = order.getReferenceNumber() != null ? order.getReferenceNumber() : "N/A";
                String refText = "Ref #: " + refNumber;
                float refWidth = courierRegular.getStringWidth(refText) / 1000 * 13f;
                float refX = (pageWidth - refWidth) / 2;
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 13f);
                contentStream.newLineAtOffset(refX, yPosition);
                contentStream.showText(refText);
                yPosition -= 15f;
                
                // Timestamp
                String timestamp = order.getTimestamp() != null ? order.getTimestamp() : "N/A";
                String timestampText = "Time: " + timestamp;
                float timestampWidth = courierRegular.getStringWidth(timestampText) / 1000 * 13f;
                float timestampX = (pageWidth - timestampWidth) / 2;
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 13f);
                contentStream.newLineAtOffset(timestampX, yPosition);
                contentStream.showText(timestampText);
                yPosition -= 20f;
            }
            
            // Final separator
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 20f;
            
            // Thank you message
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 11f);
            String thankYouLine1 = "Thank you for choosing";
            float thankYouLine1Width = courierRegular.getStringWidth(thankYouLine1) / 1000 * 11f;
            float thankYouLine1X = (pageWidth - thankYouLine1Width) / 2;
            contentStream.newLineAtOffset(thankYouLine1X, yPosition);
            contentStream.showText(thankYouLine1);
            yPosition -= 12f;
            
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 11f);
            String thankYouLine2 = "WonderPoffles!";
            float thankYouLine2Width = courierBold.getStringWidth(thankYouLine2) / 1000 * 11f;
            float thankYouLine2X = (pageWidth - thankYouLine2Width) / 2;
            contentStream.newLineAtOffset(thankYouLine2X, yPosition);
            contentStream.showText(thankYouLine2);
            yPosition -= 12f;
            
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 11f);
            String thankYouLine3 = "Come back again!";
            float thankYouLine3Width = courierRegular.getStringWidth(thankYouLine3) / 1000 * 11f;
            float thankYouLine3X = (pageWidth - thankYouLine3Width) / 2;
            contentStream.newLineAtOffset(thankYouLine3X, yPosition);
            contentStream.showText(thankYouLine3);
            
            contentStream.endText();
            contentStream.close();
            
            // Save to Downloads folder
            String userHome = System.getProperty("user.home");
            String downloadsPath = userHome + "/Downloads";
            String fileName = "Order_" + order.getOrderId() + "_Receipt.pdf";
            String fullPath = downloadsPath + "/" + fileName;
            
            document.save(fullPath);
            document.close();
            
            logger.info("✅ Receipt generated successfully: " + fullPath);
            
            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Receipt Generated", 
                "Receipt has been saved to Downloads folder:\n" + fileName);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error generating receipt for order: " + order.getOrderId(), e);
            showAlert(Alert.AlertType.ERROR, "PDF Generation Error", 
                "Failed to generate receipt. Please try again.");
        }
    }
    
    /**
     * Generate PDF document in memory for preview purposes
     * @param order The order to generate PDF for
     * @return PDDocument object or null if generation fails
     */
    private PDDocument generatePDFInMemory(RecentOrder order) {
        try {
            // Create new PDF document with custom page size
            PDDocument document = new PDDocument();
            
            // Custom page size: 3.15 inches width (227 points), auto-height
            float pageWidth = 3.15f * 72; // 3.15 inches * 72 points per inch = 227 points
            
            // Initialize fonts
            PDType1Font courierRegular = new PDType1Font(Standard14Fonts.FontName.COURIER);
            PDType1Font courierBold = new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
            
            // Calculate separator dimensions that will be used throughout
            float separatorWidth = courierRegular.getStringWidth(RECEIPT_SEPARATOR) / 1000 * 9f;
            float separatorX = (pageWidth - separatorWidth) / 2;
            
            // Calculate actual height needed based on content
            String[] items = order.getItemsOrdered().split(";");
            int itemCount = items.length;
            float lineHeight = 12f; // Line height for compact receipt
            
            // Calculate total lines needed - updated calculation without the removed content
            int headerLines = 12; // Updated for new header format
            int orderDetailLines = 4; // Order ID, Date, Customer details
            int itemHeaderLines = 3; // Separator + "Items Ordered" + spacing
            int itemLines = itemCount;
            int totalSectionLines = 3; // First separator + TOTAL line + second separator
            int digitalPaymentLines = 0;
            int cashPaymentLines = 0;
            int thankYouLines = 4; // Bottom separator + thank you message (3 lines) - reduced spacing
            
            // Add cash payment lines if applicable
            if ("Cash".equals(order.getPaymentMethod())) {
                cashPaymentLines = 3; // Paid By, Cash amount, Change lines
            }
            
            // Add digital payment lines if applicable (handled in main payment section now)
            if (("Maya".equals(order.getPaymentMethod()) || "GCash".equals(order.getPaymentMethod()))) {
                digitalPaymentLines = 3; // Paid By, Reference Number, Timestamp lines
            }
            
            // Calculate total height with proper margins - removed the totalLines that included unwanted content
            float totalHeight = (headerLines + orderDetailLines + itemHeaderLines + itemLines + totalSectionLines + cashPaymentLines + digitalPaymentLines + thankYouLines) * lineHeight + 80; // Reduced margin for better spacing balance
            
            // Create page with calculated dimensions
            PDPage page = new PDPage();
            page.setMediaBox(new PDRectangle(pageWidth, totalHeight));
            document.addPage(page);
            
            // Create content stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Start from top with margin
            float topMargin = 20f;
            float bottomMargin = 20f; // Add bottom margin for consistency
            float startY = totalHeight - topMargin;
            float yPosition = startY;
            
            contentStream.beginText();
            
            // Header 1 - "RECEIPT OF SALE" (15px, centered, Courier regular)
            contentStream.setFont(courierRegular, 15f);
            String receiptHeader = "RECEIPT OF SALE";
            float receiptHeaderWidth = courierRegular.getStringWidth(receiptHeader) / 1000 * 15f;
            float receiptHeaderX = (pageWidth - receiptHeaderWidth) / 2;
            contentStream.newLineAtOffset(receiptHeaderX, yPosition);
            contentStream.showText(receiptHeader);
            yPosition -= 15f; // Spacing after RECEIPT OF SALE
            
            // Header 2 - "WONDERPOFFLES" (18px, centered, Courier Bold)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 18f);
            String companyName = "WONDERPOFFLES";
            float companyNameWidth = courierBold.getStringWidth(companyName) / 1000 * 18f;
            float companyNameX = (pageWidth - companyNameWidth) / 2;
            contentStream.newLineAtOffset(companyNameX, yPosition);
            contentStream.showText(companyName);
            yPosition -= 40f; // Increased spacing after company name
            
            // Address Line 1 - "Address: 43 Bassig Street, Ugac" (10px, centered, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String addressLine1 = "Address: 43 Bassig Street, Ugac";
            float addressLine1Width = courierRegular.getStringWidth(addressLine1) / 1000 * 10f;
            float addressLine1X = (pageWidth - addressLine1Width) / 2;
            contentStream.newLineAtOffset(addressLine1X, yPosition);
            contentStream.showText(addressLine1);
            yPosition -= 12f;
            
            // Address Line 2 - "Sur, Tuguegarao City, Cagayan" (10px, centered, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String addressLine2 = "Sur, Tuguegarao City, Cagayan";
            float addressLine2Width = courierRegular.getStringWidth(addressLine2) / 1000 * 10f;
            float addressLine2X = (pageWidth - addressLine2Width) / 2;
            contentStream.newLineAtOffset(addressLine2X, yPosition);
            contentStream.showText(addressLine2);
            yPosition -= 12f;
            
            // Contact - "Contact #: 0975-825-6553" (10px, centered, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String contactInfo = "Contact #: 0975-825-6553";
            float contactInfoWidth = courierRegular.getStringWidth(contactInfo) / 1000 * 10f;
            float contactInfoX = (pageWidth - contactInfoWidth) / 2;
            contentStream.newLineAtOffset(contactInfoX, yPosition);
            contentStream.showText(contactInfo);
            yPosition -= 25f; // Increased spacing after contact info
            
            // Separator line
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= lineHeight * 1.5f;
            
            // Add 10f spacing after separator
            yPosition -= 10f;
            
            // Order ID (centered, 13px, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 13f);
            String orderIdText = "Order ID: " + order.getOrderId();
            float orderIdWidth = courierRegular.getStringWidth(orderIdText) / 1000 * 13f;
            float orderIdX = (pageWidth - orderIdWidth) / 2;
            contentStream.newLineAtOffset(orderIdX, yPosition);
            contentStream.showText(orderIdText);
            yPosition -= 15f; // 15px spacing after Order ID
            
            // Date and time (centered, 13px, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 13f);
            String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy   hh:mm a"));
            float dateTimeWidth = courierRegular.getStringWidth(currentDateTime) / 1000 * 13f;
            float dateTimeX = (pageWidth - dateTimeWidth) / 2;
            contentStream.newLineAtOffset(dateTimeX, yPosition);
            contentStream.showText(currentDateTime);
            yPosition -= 30f; // 30px spacing after date/time
            
            // Table setup - Define column positions for 3.15" width (227 points)
            float leftMargin = 10f;
            float rightMargin = 10f;
            float availableWidth = pageWidth - leftMargin - rightMargin; // 207 points available
            
            // Column widths: QTY (30), NAME (130), AMT (47)
            float qtyColWidth = 30f;
            float nameColWidth = 130f; 
            float amtColWidth = 47f;
            
            // Column positions
            float qtyColX = leftMargin;
            float nameColX = qtyColX + qtyColWidth;
            float amtColX = nameColX + nameColWidth;
            
            // Table header row
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            
            // QTY header (left aligned)
            contentStream.newLineAtOffset(qtyColX, yPosition);
            contentStream.showText("QTY");
            
            // NAME header (left aligned)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            contentStream.newLineAtOffset(nameColX, yPosition);
            contentStream.showText("NAME");
            
            // AMT header (right aligned)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String amtHeader = "AMT";
            float amtHeaderWidth = courierRegular.getStringWidth(amtHeader) / 1000 * 10f;
            contentStream.newLineAtOffset(amtColX + amtColWidth - amtHeaderWidth, yPosition);
            contentStream.showText(amtHeader);
            
            yPosition -= 12f; // Move down after header
            
            // Horizontal separator between header and data
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 10f; // 10px spacing after separator
            
            // Parse and display items in table format with correct pricing
            for (String item : items) {
                // Use helper method to get correct pricing
                Object[] itemDetails = parseItemWithPrice(item);
                String qty = (String) itemDetails[0];
                String name = (String) itemDetails[1];
                double unitPrice = (Double) itemDetails[2];
                double totalPrice = (Double) itemDetails[3];
                
                // Truncate name if too long (max 18 characters for narrow receipt)
                if (name.length() > 18) {
                    name = name.substring(0, 18);
                }
                
                // QTY column (left aligned)
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(qtyColX, yPosition);
                contentStream.showText(qty);
                
                // NAME column (left aligned)
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(nameColX, yPosition);
                contentStream.showText(name);
                
                // AMT column (right aligned)
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                String amtText = String.format("%.2f", totalPrice);
                float amtTextWidth = courierRegular.getStringWidth(amtText) / 1000 * 10f;
                contentStream.newLineAtOffset(amtColX + amtColWidth - amtTextWidth, yPosition);
                contentStream.showText(amtText);
                
                yPosition -= 15f; // 15px spacing per row
            }
            
            yPosition -= 15f; // 15f spacing after table
            
            // First separator after table
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 20f; // 20px spacing after separator
            
            // TOTAL section - "TOTAL" left aligned, amount right aligned
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 18f);
            
            // Calculate total amount from items using actual product prices
            double calculatedTotal = calculateOrderTotal(order);
            
            // "TOTAL" left aligned to left edge
            contentStream.newLineAtOffset(leftMargin, yPosition);
            contentStream.showText("TOTAL");
            
            // Total amount right aligned to right edge
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 18f);
            String totalAmountText = String.format("PHP %.2f", calculatedTotal);
            float totalAmountWidth = courierBold.getStringWidth(totalAmountText) / 1000 * 18f;
            contentStream.newLineAtOffset(pageWidth - rightMargin - totalAmountWidth, yPosition);
            contentStream.showText(totalAmountText);
            yPosition -= 15f; // 15px spacing after TOTAL
            
            // Second separator after TOTAL
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 25f; // 25px spacing after separator
            
            // Payment details section (Cash or Digital payment)
            if ("Cash".equals(order.getPaymentMethod())) {
                // "Paid By:" left aligned, "Cash" right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Paid By:");
                
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                String cashText = "Cash";
                float cashTextWidth = courierRegular.getStringWidth(cashText) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - cashTextWidth, yPosition);
                contentStream.showText(cashText);
                yPosition -= 15f; // 15px spacing
                
                // "Cash:" left aligned, cash amount right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Cash Received:");
                
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                // Use the actual cash received amount from the order data (stored in timestamp field for cash payments)
                double cashAmount;
                try {
                    cashAmount = Double.parseDouble(order.getTimestamp());
                } catch (NumberFormatException e) {
                    cashAmount = 0.0; // Default to 0.0 when empty - no random numbers
                }
                String cashAmountText = String.format("PHP %.2f", cashAmount);
                float cashAmountWidth = courierRegular.getStringWidth(cashAmountText) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - cashAmountWidth, yPosition);
                contentStream.showText(cashAmountText);
                yPosition -= 15f; // 15px spacing
                
                // "Change:" left aligned, change amount right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Change:");
                
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                double changeAmount = cashAmount - calculatedTotal;
                String changeAmountText = String.format("PHP %.2f", changeAmount);
                float changeAmountWidth = courierRegular.getStringWidth(changeAmountText) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - changeAmountWidth, yPosition);
                contentStream.showText(changeAmountText);
                yPosition -= 25f; // 25px spacing after cash details
            } else if ("GCash".equals(order.getPaymentMethod()) || "Maya".equals(order.getPaymentMethod())) {
                // E-wallet payment format (GCash/Maya)
                // "Paid By:" left aligned, "GCash" or "Maya" right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Paid By:");
                
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                String paymentMethodText = order.getPaymentMethod(); // "GCash" or "Maya"
                float paymentMethodWidth = courierRegular.getStringWidth(paymentMethodText) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - paymentMethodWidth, yPosition);
                contentStream.showText(paymentMethodText);
                yPosition -= 15f; // 15px spacing
                
                // "Reference Number:" left aligned, reference number right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Reference Number:");
                
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.setFont(courierRegular, 10f);
                String referenceNumber = order.getReferenceNumber().isEmpty() ? "N/A" : order.getReferenceNumber();
                float referenceNumberWidth = courierRegular.getStringWidth(referenceNumber) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - referenceNumberWidth, yPosition);
                contentStream.showText(referenceNumber);
                yPosition -= 15f; // 15px spacing
                
                // Timestamp left aligned (no right side content)
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.setFont(courierRegular, 10f);
                        contentStream.newLineAtOffset(leftMargin, yPosition);
                String timestamp = order.getTimestamp().isEmpty() ? "N/A" : order.getTimestamp();
                contentStream.showText(timestamp);
                yPosition -= 25f; // 25px spacing after e-wallet details
            } else {
                yPosition -= lineHeight * 1.5f; // Normal spacing for other payment methods
            }
            
            
            // Bottom separator
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 15f; // Reduced spacing after separator for more compact layout
            
            // Thank you message - customized format
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 8f);
            
            // First line: "Thank you for choosing WonderPoffles!"
            String thankYouLine1 = "Thank you for choosing WonderPoffles!";
            float thankYouLine1Width = courierRegular.getStringWidth(thankYouLine1) / 1000 * 8f;
            float thankYouLine1X = (pageWidth - thankYouLine1Width) / 2;
            contentStream.newLineAtOffset(thankYouLine1X, yPosition);
            contentStream.showText(thankYouLine1);
            yPosition -= 12f; // 12px spacing after first line
            
            // Second line: "We hope every bite made your day a"
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 8f);
            String thankYouLine2 = "We hope every bite made your day a";
            float thankYouLine2Width = courierRegular.getStringWidth(thankYouLine2) / 1000 * 8f;
            float thankYouLine2X = (pageWidth - thankYouLine2Width) / 2;
            contentStream.newLineAtOffset(thankYouLine2X, yPosition);
            contentStream.showText(thankYouLine2);
            yPosition -= 12f; // 12px spacing after second line
            
            // Third line: "little brighter."
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 8f);
            String thankYouLine3 = "little brighter.";
            float thankYouLine3Width = courierRegular.getStringWidth(thankYouLine3) / 1000 * 8f;
            float thankYouLine3X = (pageWidth - thankYouLine3Width) / 2;
            contentStream.newLineAtOffset(thankYouLine3X, yPosition);
            contentStream.showText(thankYouLine3);
            // Remove the extra bottom margin - height calculation already accounts for proper spacing
            
            contentStream.close();
            
            return document;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error generating receipt for order: " + order.getOrderId(), e);
            showAlert(Alert.AlertType.ERROR, "PDF Generation Error", 
                "Failed to generate receipt. Please try again.");
            return null;
        }
    }

    /**
     * Parse item string to extract quantity, name, unit price, and total price
     * @param item The item string to parse
     * @return Object array with [quantity, name, unitPrice, totalPrice]
     */
    private Object[] parseItemWithPrice(String item) {
        try {
            // Parse format: "4x Tropiham @ ₱55.00 each"
            if (item.contains("x") && item.contains("@") && item.contains("each")) {
                String[] parts = item.split("x");
                String qty = parts[0].trim();
                
                String remaining = parts[1].trim();
                String[] namePriceParts = remaining.split("@");
                String name = namePriceParts[0].trim();
                
                String priceSection = namePriceParts[1].trim();
                // Extract unit price from "₱55.00 each"
                String unitPriceStr = priceSection.replace("₱", "").replace("each", "").trim();
                double unitPrice = Double.parseDouble(unitPriceStr);
                double totalPrice = Integer.parseInt(qty) * unitPrice;
                
                return new Object[]{qty, name, unitPrice, totalPrice};
            }
            
            // Fallback parsing for simpler format
            if (item.contains("x")) {
                String[] parts = item.split("x");
                String qty = parts[0].trim();
                String name = parts[1].trim();
                
                // Try to find product price from product data
                double unitPrice = findProductPrice(name);
                double totalPrice = Integer.parseInt(qty) * unitPrice;
                
                return new Object[]{qty, name, unitPrice, totalPrice};
            }
            
            // Default fallback
            return new Object[]{"1", item, 45.0, 45.0};
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing item: " + item, e);
            return new Object[]{"1", item, 45.0, 45.0};
        }
    }
    
    /**
     * Find product price by name
     * @param productName The name of the product
     * @return The price of the product, or 45.0 as default
     */
    private double findProductPrice(String productName) {
        // Default prices for common products
        if (productName.toLowerCase().contains("tropiham")) return 55.0;
        if (productName.toLowerCase().contains("chocobam")) return 50.0;
        if (productName.toLowerCase().contains("classic")) return 45.0;
        if (productName.toLowerCase().contains("strawberry")) return 50.0;
        if (productName.toLowerCase().contains("blueberry")) return 50.0;
        
        // Default price
        return 45.0;
    }
    
    /**
     * Calculate total amount for an order
     * @param order The order to calculate total for
     * @return The total amount
     */
    private double calculateOrderTotal(RecentOrder order) {
        try {
            String totalAmountStr = order.getTotalAmount().replace("₱", "").replace(",", "").trim();
            return Double.parseDouble(totalAmountStr);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing total amount: " + order.getTotalAmount(), e);
            return 0.0;
        }
    }
    
    /**
     * Parse cash amount from order
     * @param order The order to parse cash amount for
     * @return The cash amount, or total amount if not available
     */
    private double parseCashAmount(RecentOrder order) {
        try {
            // For cash orders, assume cash amount is at least the total amount
            double totalAmount = calculateOrderTotal(order);
            // Add some change for realistic receipt (typically round up to nearest 50 or 100)
            if (totalAmount <= 100) {
                return Math.ceil(totalAmount / 50) * 50;
            } else {
                return Math.ceil(totalAmount / 100) * 100;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error calculating cash amount for order: " + order.getOrderId(), e);
            return calculateOrderTotal(order);
        }
    }
    
    /**
     * Show alert dialog with the specified message
     * @param alertType Type of alert
     * @param title Title of alert
     * @param message Message content
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}


