package com.example.wondertrackxd.controller.model;

/**
 * Class representing an individual item in an order
 * Contains category, flavor, quantity, price, and subtotal information
 */
public class OrderItem {
    
    private WaffleCategory category;
    private WaffleFlavor flavor;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    /**
     * Constructor for OrderItem
     * @param category The waffle category
     * @param flavor The specific flavor
     * @param quantity The quantity ordered
     */
    public OrderItem(WaffleCategory category, WaffleFlavor flavor, int quantity) {
        this.category = category;
        this.flavor = flavor;
        this.quantity = quantity;
        this.unitPrice = flavor.getPrice();
        this.subtotal = unitPrice * quantity;
    }

    /**
     * Get the waffle category
     * @return WaffleCategory
     */
    public WaffleCategory getCategory() {
        return category;
    }

    /**
     * Set the waffle category
     * @param category New category
     */
    public void setCategory(WaffleCategory category) {
        this.category = category;
    }

    /**
     * Get the waffle flavor
     * @return WaffleFlavor
     */
    public WaffleFlavor getFlavor() {
        return flavor;
    }

    /**
     * Set the waffle flavor and update unit price
     * @param flavor New flavor
     */
    public void setFlavor(WaffleFlavor flavor) {
        this.flavor = flavor;
        this.unitPrice = flavor.getPrice();
        updateSubtotal();
    }

    /**
     * Get the quantity
     * @return Quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Set the quantity and update subtotal
     * @param quantity New quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateSubtotal();
    }

    /**
     * Get the unit price
     * @return Unit price
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Get the subtotal for this item
     * @return Subtotal (unit price * quantity)
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Update the subtotal when quantity or price changes
     */
    private void updateSubtotal() {
        this.subtotal = unitPrice * quantity;
    }

    /**
     * Get formatted unit price string
     * @return Formatted unit price (e.g., "₱45")
     */
    public String getFormattedUnitPrice() {
        return String.format("₱%.0f", unitPrice);
    }

    /**
     * Get formatted subtotal string
     * @return Formatted subtotal (e.g., "₱90")
     */
    public String getFormattedSubtotal() {
        return String.format("₱%.0f", subtotal);
    }

    /**
     * Get a display string for the item
     * @return Item display string (e.g., "2x Eggmayoza")
     */
    public String getDisplayString() {
        return quantity + "x " + flavor.getName();
    }

    /**
     * Get a detailed display string for the item
     * @return Detailed item string (e.g., "2x Eggmayoza (₱45 each) = ₱90")
     */
    public String getDetailedDisplayString() {
        return String.format("%dx %s (%s each) = %s", 
                quantity, flavor.getName(), getFormattedUnitPrice(), getFormattedSubtotal());
    }

    @Override
    public String toString() {
        return getDetailedDisplayString();
    }
} 