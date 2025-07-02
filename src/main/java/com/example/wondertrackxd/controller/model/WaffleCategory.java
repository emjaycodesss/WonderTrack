package com.example.wondertrackxd.controller.model;

import java.util.Arrays;
import java.util.List;

/**
 * Enum representing waffle categories with their associated flavors and prices
 * Provides structured access to menu items and pricing information
 */
public enum WaffleCategory {
    SAVORY("Savory", Arrays.asList(
            new WaffleFlavor("Eggmayoza", 45.0),
            new WaffleFlavor("Tropiham", 55.0)
    )),
    
    SPICY("Spicy", Arrays.asList(
            new WaffleFlavor("Spicy tunasaur", 45.0),
            new WaffleFlavor("Pimiento", 45.0)
    )),
    
    SWEET("Sweet", Arrays.asList(
            new WaffleFlavor("Berry on top", 45.0),
            new WaffleFlavor("Oreo-verload", 45.0),
            new WaffleFlavor("S'morelicious", 55.0)
    ));

    private final String displayName;
    private final List<WaffleFlavor> flavors;

    /**
     * Constructor for WaffleCategory
     * @param displayName The display name of the category
     * @param flavors List of flavors available in this category
     */
    WaffleCategory(String displayName, List<WaffleFlavor> flavors) {
        this.displayName = displayName;
        this.flavors = flavors;
    }

    /**
     * Get the display name of the category
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get all flavors available in this category
     * @return List of WaffleFlavor objects
     */
    public List<WaffleFlavor> getFlavors() {
        return flavors;
    }

    /**
     * Get flavor names as a list of strings
     * @return List of flavor names
     */
    public List<String> getFlavorNames() {
        return flavors.stream()
                .map(WaffleFlavor::getName)
                .toList();
    }

    /**
     * Find a flavor by name within this category
     * @param flavorName The name of the flavor to find
     * @return WaffleFlavor object if found, null otherwise
     */
    public WaffleFlavor findFlavorByName(String flavorName) {
        return flavors.stream()
                .filter(flavor -> flavor.getName().equals(flavorName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get price for a specific flavor in this category
     * @param flavorName The name of the flavor
     * @return Price of the flavor, or 0.0 if not found
     */
    public double getFlavorPrice(String flavorName) {
        WaffleFlavor flavor = findFlavorByName(flavorName);
        return flavor != null ? flavor.getPrice() : 0.0;
    }

    /**
     * Find category by display name
     * @param categoryName The display name to search for
     * @return WaffleCategory if found, null otherwise
     */
    public static WaffleCategory findByDisplayName(String categoryName) {
        for (WaffleCategory category : values()) {
            if (category.getDisplayName().equals(categoryName)) {
                return category;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 