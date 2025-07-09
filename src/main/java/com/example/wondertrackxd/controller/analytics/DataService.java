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
 * DataService - Singleton service for managing sales and analytics data
 */
public class DataService {
    private static final Logger logger = Logger.getLogger(DataService.class.getName());
    
    // Singleton instance
    private static DataService instance;
    
    // File paths
    private static final String SALES_FILE = "src/main/resources/txtFiles/sales.txt";
    private static final String ORDERS_FILE = "src/main/resources/txtFiles/orders.txt";
    
    // Data collections
    private List<SalesRecord> allSales = new ArrayList<>();
    private List<RecentOrder> allOrders = new ArrayList<>();
    
    /**
     * Private constructor to prevent direct instantiation
     */
    public DataService() {
        loadAllData();
    }
    
    /**
     * Get the singleton instance of DataService
     * @return The DataService instance
     */
    public static synchronized DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }
    
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
            logger.info("📂 Loading sales data from: " + SALES_FILE);
            
            if (Files.exists(Paths.get(SALES_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(SALES_FILE));
                logger.info("📝 Read " + lines.size() + " lines from file");
                
                allSales = lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .filter(line -> !line.trim().startsWith("#"))
                    .map(this::parseSalesLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                
                logger.info("✅ Successfully loaded " + allSales.size() + " sales records");
                
                // Debug: Print first record if available
                if (!allSales.isEmpty()) {
                    logger.info("📝 First sales record: " + allSales.get(0).toString());
                }
                
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
     * Remove a sales record when an order status changes from "Completed" to another status
     * @param orderId The order ID to remove from sales records
     * @return true if sales record was removed successfully
     */
    public boolean removeSalesRecord(String orderId) {
        logger.info("🗑️ Removing sales record for order: " + orderId);
        
        try {
            // Find and remove the sales record from in-memory list
            boolean removed = allSales.removeIf(sale -> sale.getOrderId().equals(orderId));
            
            if (removed) {
                // Rewrite the sales.txt file without the removed record
                List<String> lines = Files.readAllLines(Paths.get(SALES_FILE));
                List<String> updatedLines = lines.stream()
                    .filter(line -> !line.contains("," + orderId + ","))
                    .collect(Collectors.toList());
                
                Files.write(Paths.get(SALES_FILE), updatedLines);
                
                logger.info("✅ Sales record removed successfully for order: " + orderId);
                return true;
            } else {
                logger.warning("⚠️ No sales record found for order: " + orderId);
                return false;
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error removing sales record", e);
            return false;
        }
    }

    /**
     * Create a sales record when an order is completed
     * @param order The completed order
     * @return true if sales record was created and saved successfully
     */
    public boolean createSalesRecord(RecentOrder order) {
        logger.info("💰 Creating sales record for order: " + order.getOrderId());
        
        try {
            // Check if a sales record already exists for this order
            boolean recordExists = allSales.stream()
                .anyMatch(sale -> sale.getOrderId().equals(order.getOrderId()));
            
            if (recordExists) {
                logger.warning("⚠️ Sales record already exists for order: " + order.getOrderId() + " - skipping creation");
                return false;
            }
            
            // Generate next sale ID
            String nextSaleId = generateNextSaleId();
            
            // Use current timestamp for the sale date-time with proper format for sorting
            // Format: "MMM d, yyyy h:mm a" (e.g., "Jul 6, 2025 2:45 PM")
            String currentDateTime = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));

            String salesLine = String.format("%s,%s,%s,%s,\"%s\",%s,%s,%s,\"%s\",\"%s\",%s",
                nextSaleId,
                order.getOrderId(),
                order.getName(),
                order.getContactNumber(),
                order.getItemsOrdered(),
                order.getTotalItems(),
                order.getTotalAmount(),
                order.getPaymentMethod(),
                currentDateTime,
                order.getReferenceNumber(),
                "0.00" // Cash received is not tracked in RecentOrder (handled separately in Sales view)
            );
            
            // Append to sales.txt
            Files.write(
                Paths.get(SALES_FILE),
                (salesLine + System.lineSeparator()).getBytes(),
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE
            );
            
            // Parse and add to in-memory list
            SalesRecord newRecord = parseSalesLine(salesLine);
            if (newRecord != null) {
                allSales.add(newRecord);
                logger.info("✅ Sales record created successfully: " + nextSaleId + " for order: " + order.getOrderId());
                return true;
            } else {
                logger.severe("❌ Failed to parse created sales record");
                return false;
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error creating sales record", e);
            return false;
        }
    }
    
    /**
     * Generate the next sale ID based on existing sales records
     */
    private String generateNextSaleId() {
        try {
            if (allSales.isEmpty()) {
                return "S001";
            }
            
            // Get the last sale ID
            String lastSaleId = allSales.get(allSales.size() - 1).getSaleId();
            
            // Extract the number and increment
            int nextNumber = Integer.parseInt(lastSaleId.substring(1)) + 1;
            return String.format("S%03d", nextNumber);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "⚠️ Error generating next sale ID, using timestamp-based ID", e);
            return "S" + System.currentTimeMillis();
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
            logger.info("📝 Parsing sales line: " + line);
            
            List<String> tokens = parseCSVLine(line);
            logger.info("📝 Parsed " + tokens.size() + " tokens");
            
            if (tokens.size() >= 11) {
                SalesRecord record = new SalesRecord(
                    tokens.get(0), tokens.get(1), tokens.get(2), tokens.get(3),
                    tokens.get(4).replace("\"", ""), tokens.get(5), tokens.get(6), 
                    tokens.get(7), tokens.get(8).replace("\"", ""), 
                    tokens.get(9).replace("\"", ""), tokens.get(10)
                );
                logger.info("✅ Successfully created SalesRecord: " + record.toString());
                return record;
            }
            logger.warning("⚠️ Not enough tokens in line: " + tokens.size());
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "❌ Error parsing sales line: " + line, e);
            return null;
        }
    }

    private List<String> parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;
        
        try {
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
            
            logger.info("📝 CSV parsing - tokens: " + tokens.size() + ", first token: " + 
                       (tokens.isEmpty() ? "none" : tokens.get(0)));
            
            return tokens;
        } catch (Exception e) {
            logger.log(Level.WARNING, "❌ Error parsing CSV line: " + line, e);
            return new ArrayList<>();
        }
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