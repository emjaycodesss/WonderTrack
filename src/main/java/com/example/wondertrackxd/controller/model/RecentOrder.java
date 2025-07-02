package com.example.wondertrackxd.controller.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class representing a recent order in the system
 * Contains all relevant order information for display in the MFX TableView
 * Uses JavaFX StringProperty for data binding with UI components
 * Updated to support separate Order Status and Payment Status tracking
 */
public class RecentOrder {
    
    // Order properties using StringProperty for JavaFX data binding
    private final StringProperty orderId;        // Unique identifier for the order
    private final StringProperty name;           // Customer name who placed the order
    private final StringProperty contactNumber;  // Customer contact number
    private final StringProperty itemsOrdered;  // Description of items in the order
    private final StringProperty totalItems;    // Total count of items ordered
    private final StringProperty totalAmount;   // Total amount/price of the order
    private final StringProperty paymentMethod; // Payment method used for the order
    private final StringProperty orderDate;     // Date when the order was placed
    private final StringProperty orderStatus;   // Current order workflow status (Pending, In Progress, Completed, Cancelled)
    private final StringProperty paymentStatus; // Current payment status (Unpaid, Paid, Failed, Refunded)
    private final StringProperty referenceNumber; // Reference number for digital payments (Maya/GCash)
    private final StringProperty timestamp;     // Timestamp for digital payments (Maya/GCash)

    /**
     * Main constructor with both order status and payment status
     * @param orderId Unique order identifier (e.g., "WP20250624-001")
     * @param name Customer name who placed the order
     * @param contactNumber Customer contact number
     * @param itemsOrdered Description of items ordered (e.g., "Pizza x2, Coke x1")
     * @param totalItems Total number of items in the order
     * @param totalAmount Total amount/price of the order (e.g., "₱15.99")
     * @param paymentMethod Payment method used (e.g., "Cash", "GCash", "Maya")
     * @param orderDate Date the order was placed (formatted string)
     * @param orderStatus Current order workflow status (e.g., "Pending", "In Progress", "Completed", "Cancelled")
     * @param paymentStatus Current payment status (e.g., "Unpaid", "Paid", "Failed", "Refunded")
     * @param referenceNumber Reference number for digital payments (empty string for cash)
     * @param timestamp Timestamp for digital payments (empty string for cash)
     */
    public RecentOrder(String orderId, String name, String contactNumber, String itemsOrdered, String totalItems, 
                      String totalAmount, String paymentMethod, String orderDate, String orderStatus, String paymentStatus,
                      String referenceNumber, String timestamp) {
        // Initialize all properties with provided values
        this.orderId = new SimpleStringProperty(orderId);
        this.name = new SimpleStringProperty(name);
        this.contactNumber = new SimpleStringProperty(contactNumber != null ? contactNumber : "");
        this.itemsOrdered = new SimpleStringProperty(itemsOrdered);
        this.totalItems = new SimpleStringProperty(totalItems);
        this.totalAmount = new SimpleStringProperty(totalAmount);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
        this.orderDate = new SimpleStringProperty(orderDate);
        this.orderStatus = new SimpleStringProperty(orderStatus);
        this.paymentStatus = new SimpleStringProperty(paymentStatus);
        this.referenceNumber = new SimpleStringProperty(referenceNumber != null ? referenceNumber : "");
        this.timestamp = new SimpleStringProperty(timestamp != null ? timestamp : "");
    }
    
    /**
     * Constructor for backward compatibility with old single status format
     * Automatically determines payment status based on order status
     * @param orderId Unique order identifier (e.g., "WP20250624-001")
     * @param name Customer name who placed the order
     * @param contactNumber Customer contact number
     * @param itemsOrdered Description of items ordered (e.g., "Pizza x2, Coke x1")
     * @param totalItems Total number of items in the order
     * @param totalAmount Total amount/price of the order (e.g., "₱15.99")
     * @param paymentMethod Payment method used (e.g., "Cash", "GCash", "Maya")
     * @param orderDate Date the order was placed (formatted string)
     * @param status Legacy single status field - will be used as order status
     * @param referenceNumber Reference number for digital payments (empty string for cash)
     * @param timestamp Timestamp for digital payments (empty string for cash)
     */
    public RecentOrder(String orderId, String name, String contactNumber, String itemsOrdered, String totalItems, 
                      String totalAmount, String paymentMethod, String orderDate, String status,
                      String referenceNumber, String timestamp) {
        // Call main constructor with determined payment status
        this(orderId, name, contactNumber, itemsOrdered, totalItems, totalAmount, paymentMethod, orderDate, 
             status, determinePaymentStatusFromOrderStatus(status), referenceNumber, timestamp);
    }
    
    /**
     * Constructor for backward compatibility (without contact number and digital payment fields)
     * @param orderId Unique order identifier (e.g., "WP20250624-001")
     * @param name Customer name who placed the order
     * @param itemsOrdered Description of items ordered (e.g., "Pizza x2, Coke x1")
     * @param totalItems Total number of items in the order
     * @param totalAmount Total amount/price of the order (e.g., "₱15.99")
     * @param paymentMethod Payment method used (e.g., "Cash", "GCash", "Maya")
     * @param orderDate Date the order was placed (formatted string)
     * @param status Legacy single status field - will be used as order status
     */
    public RecentOrder(String orderId, String name, String itemsOrdered, String totalItems, 
                      String totalAmount, String paymentMethod, String orderDate, String status) {
        this(orderId, name, "", itemsOrdered, totalItems, totalAmount, paymentMethod, orderDate, status, "", "");
    }

    /**
     * Constructor with contact number but without digital payment fields
     * @param orderId Unique order identifier (e.g., "WP20250624-001")
     * @param name Customer name who placed the order
     * @param contactNumber Customer contact number
     * @param itemsOrdered Description of items ordered (e.g., "Pizza x2, Coke x1")
     * @param totalItems Total number of items in the order
     * @param totalAmount Total amount/price of the order (e.g., "₱15.99")
     * @param paymentMethod Payment method used (e.g., "Cash", "GCash", "Maya")
     * @param orderDate Date the order was placed (formatted string)
     * @param status Legacy single status field - will be used as order status
     */
    public RecentOrder(String orderId, String name, String contactNumber, String itemsOrdered, String totalItems, 
                      String totalAmount, String paymentMethod, String orderDate, String status) {
        this(orderId, name, contactNumber, itemsOrdered, totalItems, totalAmount, paymentMethod, orderDate, status, "", "");
    }

    /**
     * Helper method to determine payment status from legacy order status
     * Used for backward compatibility when loading old data
     * @param orderStatus The legacy order status
     * @return Appropriate payment status
     */
    private static String determinePaymentStatusFromOrderStatus(String orderStatus) {
        return switch (orderStatus) {
            case "Completed" -> "Paid";        // Completed orders are typically paid
            case "Cancelled" -> "Refunded";    // Cancelled orders may be refunded
            case "Pending", "In Progress" -> "Unpaid"; // Active orders may be unpaid
            default -> "Unpaid";               // Default to unpaid for unknown statuses
        };
    }

    // Property getters for JavaFX data binding
    // These methods are used by MFX TableView to bind data to table cells
    
    /**
     * @return StringProperty for order ID - used for table binding
     */
    public StringProperty orderIdProperty() { 
        return orderId; 
    }
    
    /**
     * @return StringProperty for customer name - used for table binding
     */
    public StringProperty nameProperty() { 
        return name; 
    }
    
    /**
     * @return StringProperty for contact number - used for table binding
     */
    public StringProperty contactNumberProperty() { 
        return contactNumber; 
    }
    
    /**
     * @return StringProperty for items ordered description - used for table binding
     */
    public StringProperty itemsOrderedProperty() { 
        return itemsOrdered; 
    }
    
    /**
     * @return StringProperty for total items count - used for table binding
     */
    public StringProperty totalItemsProperty() { 
        return totalItems; 
    }
    
    /**
     * @return StringProperty for total amount - used for table binding
     */
    public StringProperty totalAmountProperty() { 
        return totalAmount; 
    }
    
    /**
     * @return StringProperty for payment method - used for table binding
     */
    public StringProperty paymentMethodProperty() { 
        return paymentMethod; 
    }
    
    /**
     * @return StringProperty for order date - used for table binding
     */
    public StringProperty orderDateProperty() { 
        return orderDate; 
    }
    
    /**
     * @return StringProperty for order status - used for table binding
     */
    public StringProperty orderStatusProperty() { 
        return orderStatus; 
    }
    
    /**
     * @return StringProperty for payment status - used for table binding
     */
    public StringProperty paymentStatusProperty() { 
        return paymentStatus; 
    }
    
    /**
     * @return StringProperty for reference number - used for table binding
     */
    public StringProperty referenceNumberProperty() { 
        return referenceNumber; 
    }
    
    /**
     * @return StringProperty for timestamp - used for table binding
     */
    public StringProperty timestampProperty() { 
        return timestamp; 
    }
    
    // Convenience getters for direct value access (not properties)
    
    /**
     * @return The actual order ID string value
     */
    public String getOrderId() {
        return orderId.get();
    }
    
    /**
     * @return The actual customer name string value
     */
    public String getName() {
        return name.get();
    }
    
    /**
     * @return The actual contact number string value
     */
    public String getContactNumber() {
        return contactNumber.get();
    }
    
    /**
     * @return The actual items ordered string value
     */
    public String getItemsOrdered() {
        return itemsOrdered.get();
    }
    
    /**
     * @return The actual total items string value
     */
    public String getTotalItems() {
        return totalItems.get();
    }
    
    /**
     * @return The actual total amount string value
     */
    public String getTotalAmount() {
        return totalAmount.get();
    }
    
    /**
     * @return The actual payment method string value
     */
    public String getPaymentMethod() {
        return paymentMethod.get();
    }
    
    /**
     * @return The actual order date string value
     */
    public String getOrderDate() {
        return orderDate.get();
    }
    
    /**
     * @return The actual order status string value
     */
    public String getOrderStatus() {
        return orderStatus.get();
    }
    
    /**
     * @return The actual payment status string value
     */
    public String getPaymentStatus() {
        return paymentStatus.get();
    }
    
    /**
     * @return The actual reference number string value
     */
    public String getReferenceNumber() {
        return referenceNumber.get();
    }
    
    /**
     * @return The actual timestamp string value
     */
    public String getTimestamp() {
        return timestamp.get();
    }
    
    // Legacy getter for backward compatibility
    /**
     * Legacy method for backward compatibility
     * @return The order status (same as getOrderStatus())
     */
    public String getStatus() {
        return getOrderStatus();
    }
    
    /**
     * Set the order status to a new value
     * @param newOrderStatus The new order status to set
     */
    public void setOrderStatus(String newOrderStatus) {
        orderStatus.set(newOrderStatus);
    }
    
    /**
     * Set the payment status to a new value
     * @param newPaymentStatus The new payment status to set
     */
    public void setPaymentStatus(String newPaymentStatus) {
        paymentStatus.set(newPaymentStatus);
    }
    
    /**
     * Legacy setter for backward compatibility
     * @param newStatus The new status to set (will update order status)
     */
    public void setStatus(String newStatus) {
        setOrderStatus(newStatus);
    }
    
    /**
     * String representation of the RecentOrder for debugging purposes
     * @return Formatted string containing all order information
     */
    @Override
    public String toString() {
        return String.format(
            "RecentOrder{orderId='%s', name='%s', contactNumber='%s', itemsOrdered='%s', totalItems='%s', totalAmount='%s', paymentMethod='%s', orderDate='%s', orderStatus='%s', paymentStatus='%s', referenceNumber='%s', timestamp='%s'}",
            getOrderId(), getName(), getContactNumber(), getItemsOrdered(), getTotalItems(), getTotalAmount(), 
            getPaymentMethod(), getOrderDate(), getOrderStatus(), getPaymentStatus(), getReferenceNumber(), getTimestamp()
        );
    }
}
