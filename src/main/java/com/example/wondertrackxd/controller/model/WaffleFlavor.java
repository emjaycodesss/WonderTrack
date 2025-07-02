package com.example.wondertrackxd.controller.model;

/**
 * Class representing a waffle flavor with its name and price
 * Used to store menu item information for the ordering system
 */
public class WaffleFlavor {
    
    private final String name;
    private final double price;

    /**
     * Constructor for WaffleFlavor
     * @param name The name of the waffle flavor
     * @param price The price per piece of this flavor
     */
    public WaffleFlavor(String name, double price) {
        this.name = name;
        this.price = price;
    }

    /**
     * Get the name of the waffle flavor
     * @return Flavor name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the price of the waffle flavor
     * @return Price per piece
     */
    public double getPrice() {
        return price;
    }

    /**
     * Get formatted price string with peso sign
     * @return Formatted price (e.g., "₱45")
     */
    public String getFormattedPrice() {
        return String.format("₱%.0f", price);
    }

    @Override
    public String toString() {
        return name + " (" + getFormattedPrice() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WaffleFlavor that = (WaffleFlavor) obj;
        return Double.compare(that.price, price) == 0 && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + (int) (price * 100);
    }
} 