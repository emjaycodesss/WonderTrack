package com.example.wondertrackxd.controller.model;

import com.example.wondertrackxd.controller.analytics.DataService;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SalesRecord Model Class for WonderTrackXd POS System
 * Represents completed sales transactions for revenue analytics
 * Separate from orders to follow professional POS standards
 * 
 * This class handles finalized sales data that feeds into:
 * - Revenue analytics and reporting
 * - Sales trend analysis
 * - Payment method tracking
 * - Customer purchase history
 * - Financial reporting
 * 
 * @author WonderTrackXd Development Team
 * @version 2.0 - Professional POS Structure
 */
public class SalesRecord {
    
    // Sales identification and linking
    private String saleId;          // Unique sales identifier (WPS + YYYYMMDD + sequence)
    private String orderId;         // Reference to original order (WP + YYYYMMDD + sequence)
    
    // Customer information
    private String customerName;
    private String contactNumber;
    
    // Transaction details
    private String itemsSold;       // Semicolon-separated list of items with quantities
    private String totalItems;     // Total quantity of items sold
    private String saleAmount;      // Final transaction amount (₱)
    private String paymentMethod;   // Cash, GCash, Maya
    
    // Transaction timing and references
    private String saleDateTime;    // When the sale was completed
    private String paymentReference; // Digital payment reference (empty for cash)
    private String cashReceived;    // Amount received for cash payments (0.00 for digital)

    /**
     * Default constructor for SalesRecord
     */
    public SalesRecord() {
    }

    /**
     * Full constructor for SalesRecord
     * Used when creating new sales records from completed orders
     * 
     * @param saleId Unique sales identifier
     * @param orderId Reference to original order
     * @param customerName Customer's full name
     * @param contactNumber Customer's phone number
     * @param itemsSold List of items sold with quantities
     * @param totalItems Total number of items sold
     * @param saleAmount Final transaction amount
     * @param paymentMethod Payment method used
     * @param saleDateTime When the sale was completed
     * @param paymentReference Digital payment reference
     * @param cashReceived Amount received for cash payments
     */
    public SalesRecord(String saleId, String orderId, String customerName, String contactNumber,
                      String itemsSold, String totalItems, String saleAmount, String paymentMethod,
                      String saleDateTime, String paymentReference, String cashReceived) {
        this.saleId = saleId;
        this.orderId = orderId;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.itemsSold = itemsSold;
        this.totalItems = totalItems;
        this.saleAmount = saleAmount;
        this.paymentMethod = paymentMethod;
        this.saleDateTime = saleDateTime;
        this.paymentReference = paymentReference;
        this.cashReceived = cashReceived;
    }

    // Getters and Setters with comprehensive documentation

    /**
     * Get the unique sales identifier
     * @return Sale ID (format: WPS + YYYYMMDD + sequence)
     */
    public String getSaleId() {
        return saleId;
    }

    /**
     * Set the unique sales identifier
     * @param saleId Sale ID to set
     */
    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    /**
     * Get the original order ID that this sale references
     * @return Order ID (format: WP + YYYYMMDD + sequence)
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Set the original order ID reference
     * @param orderId Order ID to set
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * Get the customer's full name
     * @return Customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Set the customer's full name
     * @param customerName Customer name to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Get the customer's contact number
     * @return Contact number
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * Set the customer's contact number
     * @param contactNumber Contact number to set
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /**
     * Get the list of items sold with quantities
     * @return Items sold (semicolon-separated format)
     */
    public String getItemsSold() {
        return itemsSold;
    }

    /**
     * Set the list of items sold
     * @param itemsSold Items sold to set
     */
    public void setItemsSold(String itemsSold) {
        this.itemsSold = itemsSold;
    }

    /**
     * Get the total number of items sold
     * @return Total items count as string
     */
    public String getTotalItems() {
        return totalItems;
    }

    /**
     * Set the total number of items sold
     * @param totalItems Total items to set
     */
    public void setTotalItems(String totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * Get the final sale amount
     * @return Sale amount with currency symbol (₱)
     */
    public String getSaleAmount() {
        return saleAmount;
    }

    /**
     * Set the final sale amount
     * @param saleAmount Sale amount to set
     */
    public void setSaleAmount(String saleAmount) {
        this.saleAmount = saleAmount;
    }

    /**
     * Get the payment method used
     * @return Payment method (Cash, GCash, Maya)
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Set the payment method
     * @param paymentMethod Payment method to set
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Get the sale completion date and time
     * @return Sale date/time string
     */
    public String getSaleDateTime() {
        return saleDateTime;
    }

    /**
     * Set the sale completion date and time
     * @param saleDateTime Sale date/time to set
     */
    public void setSaleDateTime(String saleDateTime) {
        this.saleDateTime = saleDateTime;
    }

    /**
     * Get the payment reference for digital payments
     * @return Payment reference (empty for cash payments)
     */
    public String getPaymentReference() {
        return paymentReference;
    }

    /**
     * Set the payment reference
     * @param paymentReference Payment reference to set
     */
    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    /**
     * Get the cash amount received
     * @return Cash received (0.00 for digital payments)
     */
    public String getCashReceived() {
        return cashReceived;
    }

    /**
     * Set the cash amount received
     * @param cashReceived Cash received to set
     */
    public void setCashReceived(String cashReceived) {
        this.cashReceived = cashReceived;
    }

    /**
     * Generate a CSV format string for saving to sales.txt
     * @return Comma-separated values string with proper quoting
     */
    public String toCsvString() {
        return String.format("%s,%s,%s,%s,\"%s\",%s,%s,%s,\"%s\",\"%s\",%s",
                saleId, orderId, customerName, contactNumber, itemsSold, totalItems,
                saleAmount, paymentMethod, saleDateTime, paymentReference, cashReceived);
    }

    /**
     * Create a SalesRecord from a completed RecentOrder
     * @param order The completed order to convert to a sale
     * @return New SalesRecord representing the completed sale
     */
    public static SalesRecord fromOrder(RecentOrder order) {
        // Generate sequential sale ID (S001, S002, etc.)
        String saleId = generateNextSaleId();
        
        // Determine cash received based on payment method
        String cashReceived = "0.00";
        if ("Cash".equalsIgnoreCase(order.getPaymentMethod())) {
            // For cash payments, use the timestamp field which contains cash amount
            cashReceived = order.getTimestamp();
            if (cashReceived == null || cashReceived.trim().isEmpty()) {
                cashReceived = "0.00";
            }
        }
        
        return new SalesRecord(
                saleId,                  // Sale ID (now S001, S002, etc.)
                order.getOrderId(),      // Original order ID (WP...)
                order.getName(),         // Customer name
                order.getContactNumber(),// Contact number
                order.getItemsOrdered(), // Items ordered
                order.getTotalItems(),   // Total items
                order.getTotalAmount(),  // Sale amount
                order.getPaymentMethod(),// Payment method
                order.getOrderDate(),    // Sale date/time
                order.getReferenceNumber() != null ? order.getReferenceNumber() : "", // Payment reference
                cashReceived             // Cash received
        );
    }

    /**
     * Generate the next sequential sale ID
     * Format: S001, S002, etc.
     * @return Next sale ID in sequence
     */
    private static String generateNextSaleId() {
        try {
            DataService dataService = new DataService();
            dataService.loadSalesData(); // Load current sales data
            
            List<String> existingSaleIds = dataService.getAllSales()
                .stream()
                .map(SalesRecord::getSaleId)
                .filter(id -> id.matches("S\\d{3}"))
                .sorted()
                .collect(Collectors.toList());

            if (existingSaleIds.isEmpty()) {
                return "S001";
            }

            // Get the last ID and increment
            String lastId = existingSaleIds.get(existingSaleIds.size() - 1);
            int nextNumber = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("S%03d", nextNumber);
        } catch (Exception e) {
            // If anything goes wrong, return a fallback ID
            return "S" + System.currentTimeMillis();
        }
    }

    /**
     * String representation for debugging and logging
     * @return String representation of the sales record
     */
    @Override
    public String toString() {
        return String.format("SalesRecord{saleId='%s', orderId='%s', customer='%s', amount='%s', method='%s'}",
                saleId, orderId, customerName, saleAmount, paymentMethod);
    }
} 