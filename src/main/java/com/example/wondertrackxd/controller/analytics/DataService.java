package com.example.wondertrackxd.controller.analytics;

import com.example.wondertrackxd.controller.model.RecentOrder;
import com.example.wondertrackxd.controller.model.SalesRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * DataService for WonderTrackXd POS System
 * Professional separation of orders and sales data management
 * Follows industry standards for POS data handling
 * 
 * Responsibilities:
 * - Load and parse orders.txt and sales.txt
 * - Create sales records from completed orders
 * - Provide data access methods for analytics
 * - Handle file operations with error handling
 * 
 * @author WonderTrackXd Development Team
 * @version 2.0 - Professional POS Structure
 */
public class DataService {
    
    private static final Logger logger = Logger.getLogger(DataService.class.getName());
    
    // File paths
    private static final String ORDERS_FILE = "src/main/resources/txtFiles/orders.txt";
    private static final String SALES_FILE = "src/main/resources/txtFiles/sales.txt";
    
    // Data storage
    private List<RecentOrder> allOrders = new ArrayList<>();
    private List<SalesRecord> allSales = new ArrayList<>();

    /**
     * Load all data from both orders and sales files
     * @return true if both files loaded successfully
     */
    public boolean loadAllData() {
        logger.info("📂 Loading all POS data...");
        
        boolean ordersLoaded = loadOrderData();
        boolean salesLoaded = loadSalesData();
        
        logger.info("✅ Data loading completed: " + allOrders.size() + " orders, " + allSales.size() + " sales");
        return ordersLoaded && salesLoaded;
    }

    /**
     * Load order data from orders.txt
     * @return true if loading was successful
     */
    public boolean loadOrderData() {
        try {
            if (Files.exists(Paths.get(ORDERS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(ORDERS_FILE));
                
                allOrders = lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .filter(line -> !line.trim().startsWith("#"))
                    .map(this::parseOrderLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                
                logger.info("✅ Loaded " + allOrders.size() + " orders from " + ORDERS_FILE);
                return true;
            } else {
                logger.warning("⚠️ Orders file not found: " + ORDERS_FILE);
                return false;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error loading order data", e);
            return false;
        }
    }

    /**
     * Load sales data from sales.txt
     * @return true if loading was successful
     */
    public boolean loadSalesData() {
        try {
            if (Files.exists(Paths.get(SALES_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(SALES_FILE));
                
                allSales = lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .filter(line -> !line.trim().startsWith("#"))
                    .map(this::parseSalesLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                
                logger.info("✅ Loaded " + allSales.size() + " sales from " + SALES_FILE);
                return true;
            } else {
                logger.warning("⚠️ Sales file not found: " + SALES_FILE);
                return false;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error loading sales data", e);
            return false;
        }
    }

    /**
     * Create a sales record when an order is completed
     * @param order The completed order
     * @return true if sales record was created and saved successfully
     */
    public boolean createSalesRecord(RecentOrder order) {
        if (!"Completed".equals(order.getStatus())) {
            logger.warning("⚠️ Cannot create sales record for non-completed order: " + order.getOrderId());
            return false;
        }
        
        try {
            SalesRecord salesRecord = SalesRecord.fromOrder(order);
            allSales.add(salesRecord);
            
            // Append to sales.txt file
            return appendSalesRecordToFile(salesRecord);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error creating sales record for order: " + order.getOrderId(), e);
            return false;
        }
    }

    /**
     * Append a sales record to the sales.txt file
     * @param salesRecord The sales record to append
     * @return true if append was successful
     */
    private boolean appendSalesRecordToFile(SalesRecord salesRecord) {
        try {
            String csvLine = salesRecord.toCsvString();
            Files.write(Paths.get(SALES_FILE), 
                       Arrays.asList(csvLine), 
                       StandardOpenOption.CREATE, 
                       StandardOpenOption.APPEND);
            
            logger.info("💾 Sales record saved: " + salesRecord.getSaleId());
            return true;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error saving sales record to file", e);
            return false;
        }
    }

    // Data parsing methods
    private RecentOrder parseOrderLine(String line) {
        try {
            List<String> tokens = parseCSVLine(line);
            
            if (tokens.size() >= 11) {
                return new RecentOrder(
                    tokens.get(0), tokens.get(1), tokens.get(2), 
                    tokens.get(3).replace("\"", ""), tokens.get(4), tokens.get(5), 
                    tokens.get(6), tokens.get(7).replace("\"", ""), tokens.get(8),
                    tokens.get(9).replace("\"", ""), tokens.get(10).replace("\"", "")
                );
            }
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing order line: " + line, e);
            return null;
        }
    }

    private SalesRecord parseSalesLine(String line) {
        try {
            List<String> tokens = parseCSVLine(line);
            
            if (tokens.size() >= 11) {
                return new SalesRecord(
                    tokens.get(0), tokens.get(1), tokens.get(2), tokens.get(3),
                    tokens.get(4).replace("\"", ""), tokens.get(5), tokens.get(6), 
                    tokens.get(7), tokens.get(8).replace("\"", ""), 
                    tokens.get(9).replace("\"", ""), tokens.get(10)
                );
            }
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing sales line: " + line, e);
            return null;
        }
    }

    private List<String> parseCSVLine(String line) {
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
        return tokens;
    }

    // Getter methods for data access
    public List<RecentOrder> getAllOrders() {
        return new ArrayList<>(allOrders);
    }

    public List<SalesRecord> getAllSales() {
        return new ArrayList<>(allSales);
    }

    public List<RecentOrder> getOrdersByStatus(String status) {
        return allOrders.stream()
            .filter(order -> status.equals(order.getStatus()))
            .collect(Collectors.toList());
    }

    public long getOrderCountByStatus(String status) {
        return allOrders.stream()
            .filter(order -> status.equals(order.getStatus()))
            .count();
    }

    public double getTotalRevenue() {
        return allSales.stream()
            .mapToDouble(sale -> parseAmount(sale.getSaleAmount()))
            .sum();
    }

    public double getAverageOrderValue() {
        return allSales.size() > 0 ? getTotalRevenue() / allSales.size() : 0.0;
    }

    private double parseAmount(String amountStr) {
        try {
            return Double.parseDouble(amountStr.replace("₱", "").replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Refresh all data by reloading from files
     */
    public void refreshData() {
        allOrders.clear();
        allSales.clear();
        loadAllData();
    }
} 