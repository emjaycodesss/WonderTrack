package com.example.wondertrackxd.controller.products;

import com.example.wondertrackxd.controller.model.WaffleCategory;
import com.example.wondertrackxd.controller.model.WaffleFlavor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.example.wondertrackxd.controller.products.ProductManagementController;

/**
 * Controller for the Overview page that handles the recent orders table display
 * Manages MFX TableView population and column configuration with proper styling
 */
public class ProductsController {

    // Logger instance for tracking operations and debugging
    private static final Logger logger = Logger.getLogger(ProductsController.class.getName());

    @FXML private Button manageProductsButton;
    @FXML private VBox categoriesContainer;
    
    private static final String PRODUCTS_FILE = "src/main/resources/txtFiles/products.txt";
    private static final String CATEGORIES_FILE = "src/main/resources/txtFiles/categories.txt";
    
    @FXML
    public void initialize() {
        logger.info("🏗️ Initializing Products Controller...");
        
        if (categoriesContainer == null) {
            logger.severe("❌ categoriesContainer is null - FXML injection failed");
            return;
        }
        
        logger.info("✅ categoriesContainer successfully injected");
        
        try {
            setupEventHandlers();
            refreshProductsView();
            logger.info("✅ Products Controller initialized successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during Products Controller initialization", e);
        }
    }
    
    private void setupEventHandlers() {
        logger.info("🔧 Setting up event handlers...");
        
        if (manageProductsButton != null) {
            manageProductsButton.setOnAction(event -> openProductManagement());
            logger.info("✅ Manage Products button handler set up");
        } else {
            logger.warning("⚠️ manageProductsButton is null - FXML injection failed");
        }
    }
    
    private void openProductManagement() {
        try {
            logger.info("🔄 Opening Product Management window...");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProductManagement.fxml"));
            Scene scene = new Scene(loader.load());
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Manage Products");
            stage.setScene(scene);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setWidth(900);
            stage.setHeight(600);
            
            logger.info("✅ Product Management window created successfully");
            
            stage.showAndWait();
            refreshProductsView();
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "❌ Error opening product management window", e);
        }
    }
    
    private void refreshProductsView() {
        try {
            logger.info("🔄 Starting products view refresh...");
            
            if (categoriesContainer == null) {
                logger.severe("❌ categoriesContainer is null during refresh");
                return;
            }
            
            logger.info("🧹 Clearing existing content...");
            categoriesContainer.getChildren().clear();
            
            // Load categories and products
            logger.info("📂 Loading categories...");
            List<String> categories = loadCategories();
            logger.info("📋 Loaded " + categories.size() + " categories");
            
            logger.info("📂 Loading products...");
            Map<String, List<ProductManagementController.ProductItem>> productsByCategory = loadProducts();
            logger.info("📋 Loaded products for " + productsByCategory.size() + " categories");
            
            // Create category sections
            for (String category : categories) {
                List<ProductManagementController.ProductItem> products = productsByCategory.getOrDefault(category, new ArrayList<>());
                logger.info("📦 Creating section for category '" + category + "' with " + products.size() + " products");
                VBox categorySection = createCategorySection(category, products);
                categoriesContainer.getChildren().add(categorySection);
            }
            
            logger.info("✅ Products view refreshed successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error refreshing products view", e);
        }
    }
    
    private List<String> loadCategories() throws IOException {
        logger.info("📂 Loading categories from file...");
        
        try {
            List<String> categories = Files.readAllLines(Paths.get(CATEGORIES_FILE))
                .stream()
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());
                
            logger.info("✅ Successfully loaded " + categories.size() + " categories");
            return categories;
        } catch (IOException e) {
            logger.warning("⚠️ Categories file not found at " + CATEGORIES_FILE + ", trying fallback...");
            
            // Fallback to resource loading if file doesn't exist
            try (InputStream is = getClass().getResourceAsStream("/txtFiles/categories.txt")) {
                if (is == null) {
                    logger.warning("⚠️ Categories file not found in resources either");
                    return new ArrayList<>();
                }
                
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    List<String> categories = reader.lines()
                        .filter(line -> !line.trim().isEmpty())
                        .collect(Collectors.toList());
                        
                    logger.info("✅ Successfully loaded " + categories.size() + " categories from resources");
                    return categories;
                }
            }
        }
    }
    
    private Map<String, List<ProductManagementController.ProductItem>> loadProducts() throws IOException {
        logger.info("📂 Loading products from file...");
        
        try {
            Map<String, List<ProductManagementController.ProductItem>> productsByCategory = Files.readAllLines(Paths.get(PRODUCTS_FILE))
                .stream()
                .filter(line -> !line.trim().isEmpty())
                .map(line -> {
                    String[] parts = line.split("\\|");
                    if (parts.length == 4) {
                        return new ProductManagementController.ProductItem(
                            parts[0].trim(), // category
                            parts[1].trim(), // name
                            parts[2].trim(), // description
                            parts[3].trim()  // price
                        );
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(ProductManagementController.ProductItem::getCategory));
                
            logger.info("✅ Successfully loaded products for " + productsByCategory.size() + " categories");
            return productsByCategory;
        } catch (IOException e) {
            logger.warning("⚠️ Products file not found at " + PRODUCTS_FILE + ", trying fallback...");
            
            // Fallback to resource loading if file doesn't exist
            try (InputStream is = getClass().getResourceAsStream("/txtFiles/products.txt")) {
                if (is == null) {
                    logger.warning("⚠️ Products file not found in resources either");
                    return new HashMap<>();
                }
                
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    Map<String, List<ProductManagementController.ProductItem>> productsByCategory = reader.lines()
                        .filter(line -> !line.trim().isEmpty())
                        .map(line -> {
                            String[] parts = line.split("\\|");
                            if (parts.length == 4) {
                                return new ProductManagementController.ProductItem(
                                    parts[0].trim(), // category
                                    parts[1].trim(), // name
                                    parts[2].trim(), // description
                                    parts[3].trim()  // price
                                );
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(ProductManagementController.ProductItem::getCategory));
                        
                    logger.info("✅ Successfully loaded products for " + productsByCategory.size() + " categories from resources");
                    return productsByCategory;
                }
            }
        }
    }
    
    private VBox createCategorySection(String categoryName, List<ProductManagementController.ProductItem> products) {
        logger.info("🎨 Creating section for category: " + categoryName);
        
        // Create category section container
        VBox categorySection = new VBox();
        categorySection.setSpacing(15);
        categorySection.setStyle("-fx-background-color: #fffefc; -fx-border-color: #fde998; " +
                               "-fx-background-radius: 6px; -fx-border-radius: 6px; -fx-border-width: 1px;");
        categorySection.setPadding(new Insets(15));
        
        // Create header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);
        
        Label categoryLabel = new Label(categoryName);
        categoryLabel.setTextFill(javafx.scene.paint.Color.valueOf("#8b4513"));
        categoryLabel.setFont(Font.font("Inter Bold", 22));
        
        Label itemCountLabel = new Label(products.size() + " Items");
        itemCountLabel.setStyle("-fx-background-color: #fef3c7; -fx-background-radius: 12px;");
        itemCountLabel.setTextFill(javafx.scene.paint.Color.valueOf("#d97708"));
        itemCountLabel.setFont(Font.font("Inter Medium", 14));
        itemCountLabel.setPadding(new Insets(4, 12, 4, 12));
        
        header.getChildren().addAll(categoryLabel, itemCountLabel);
        
        // Create products grid using FlowPane for wrapping
        javafx.scene.layout.FlowPane productsGrid = new javafx.scene.layout.FlowPane();
        productsGrid.setHgap(15); // Horizontal spacing between cards
        productsGrid.setVgap(15); // Vertical spacing between rows
        // Remove fixed width constraints to allow responsive layout
        productsGrid.setMaxWidth(Double.MAX_VALUE); // Allow full width usage
        productsGrid.setPrefWidth(Region.USE_COMPUTED_SIZE); // Use computed size based on content
        
        // Calculate card width based on number of products
        double cardWidth = calculateCardWidth(products.size());
        
        // Add product cards
        for (ProductManagementController.ProductItem product : products) {
            VBox productCard = createProductCard(product);
            // Set dynamic width based on number of items
            productCard.setPrefWidth(cardWidth);
            productCard.setMinWidth(280); // Minimum card width for readability
            productCard.setMaxWidth(cardWidth);
            productCard.setPrefHeight(120);
            productCard.setMinHeight(120);
            productCard.setMaxHeight(120);
            productsGrid.getChildren().add(productCard);
        }
        
        categorySection.getChildren().addAll(header, productsGrid);
        logger.info("✅ Created section for category: " + categoryName + " with " + products.size() + " products");
        
        return categorySection;
    }
    
    private double calculateCardWidth(int itemCount) {
        // Calculate available width dynamically based on container
        // Main app width (1200) - sidebar (265) - main VBox padding (30) - category section padding (30) = 875
        double availableWidth = 875; 
        double gap = 15;
        
        logger.info("🔧 Calculating card width for " + itemCount + " items, available width: " + availableWidth);
        
        if (itemCount >= 3) {
            // If 3 or more items, calculate width for 3 cards per row
            double cardWidth = (availableWidth - (2 * gap)) / 3; // 3 cards with 2 gaps
            logger.info("✅ Card width for 3+ items: " + cardWidth);
            return cardWidth;
        } else if (itemCount == 2) {
            // If 2 items, split the width minus one gap
            double cardWidth = (availableWidth - gap) / 2;
            logger.info("✅ Card width for 2 items: " + cardWidth);
            return cardWidth;
        } else {
            // If 1 item, use full available width
            logger.info("✅ Card width for 1 item: " + availableWidth);
            return availableWidth;
        }
    }

    private VBox createProductCard(ProductManagementController.ProductItem product) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 6px; " +
                     "-fx-border-radius: 6px; -fx-border-color: #ffe98f;");
        card.setPadding(new Insets(15));
        
        // Product name and price
        HBox namePrice = new HBox();
        namePrice.setAlignment(Pos.CENTER_LEFT);
        namePrice.setPrefHeight(30); // Fixed height for name/price section
        
        Label nameLabel = new Label(product.getName());
        nameLabel.setTextFill(javafx.scene.paint.Color.valueOf("#8b4513"));
        nameLabel.setFont(Font.font("Inter Semi Bold", 16));
        nameLabel.setMaxWidth(200); // Prevent long names from pushing price
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label priceLabel = new Label(product.getPrice());
        priceLabel.setStyle("-fx-background-color: #d97708; -fx-background-radius: 12px;");
        priceLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        priceLabel.setFont(Font.font("Inter Bold", 12));
        priceLabel.setPadding(new Insets(4, 12, 4, 12));
        
        namePrice.getChildren().addAll(nameLabel, spacer, priceLabel);
        
        // Description
        VBox descContainer = new VBox();
        descContainer.setPrefHeight(50); // Fixed height for description
        descContainer.setAlignment(Pos.TOP_LEFT);
        
        Label descLabel = new Label(product.getDescription());
        descLabel.setTextFill(javafx.scene.paint.Color.valueOf("#b4540a"));
        descLabel.setFont(Font.font("Inter Regular", 13));
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(50); // Limit description height
        
        descContainer.getChildren().add(descLabel);
        
        card.getChildren().addAll(namePrice, descContainer);
        return card;
    }
}


