package com.example.wondertrackxd.controller.header;

import com.example.wondertrackxd.controller.AppShellController;
import com.example.wondertrackxd.controller.analytics.AnalyticsController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Controller for the application header
 * Handles burger menu toggle and other header interactions
 */
public class HeaderController {

    // Logger for tracking header operations
    private static final Logger logger = Logger.getLogger(HeaderController.class.getName());

    @FXML
    private Button sidebarToggleButton;

    @FXML
    private HBox logoSection;

    @FXML
    private HBox navSection;

    @FXML
    private ImageView burgerIcon;
    
    @FXML
    private javafx.scene.control.Label pageTitle;
    
    @FXML
    private ComboBox<String> analyticsTimeFilter;
    
    @FXML
    private Button manageProductsButton;

    // Static instance for controller communication
    private static HeaderController instance;
    
    // Reference to AnalyticsController for real-time updates
    private static AnalyticsController analyticsController;
    
    // Reference to SalesController for real-time updates
    private static com.example.wondertrackxd.controller.sales.SalesController salesController;

    private static final double EXPANDED_WIDTH = 215.0;
    private static final Duration ANIMATION_DURATION = Duration.millis(200);
    private static final Duration HOVER_DURATION = Duration.millis(150);

    /**
     * Initialize the header controller
     * Sets up event handlers and initial state
     */
    @FXML
    public void initialize() {
        logger.info("🏠 Header controller initialized successfully");
        
        // Set static instance for controller communication
        instance = this;
        
        setupButtonAnimations();
        initializeAnalyticsFilter();
        setupManageProductsButton();
    }
    
    /**
     * Get the singleton instance
     * @return HeaderController instance
     */
    public static HeaderController getInstance() {
        return instance;
    }
    
    /**
     * Set the AnalyticsController reference for real-time updates
     * @param controller The AnalyticsController instance
     */
    public static void setAnalyticsController(AnalyticsController controller) {
        analyticsController = controller;
        logger.info("🔗 AnalyticsController reference set for real-time updates");
    }
    
    /**
     * Set the SalesController reference for real-time updates
     * @param controller The SalesController instance
     */
    public static void setSalesController(com.example.wondertrackxd.controller.sales.SalesController controller) {
        salesController = controller;
        logger.info("🔗 SalesController reference set for real-time updates");
    }
    
    /**
     * Update the page title displayed in the header
     * @param title The new page title
     */
    public void updatePageTitle(String title) {
        logger.info("📝 Updating page title to: " + title);
        
        if (pageTitle != null) {
            pageTitle.setText(title);
            
            // Show/hide analytics filter based on page
            if (analyticsTimeFilter != null) {
                boolean isAnalyticsPage = "Analytics".equals(title);
                analyticsTimeFilter.setVisible(isAnalyticsPage);
                analyticsTimeFilter.setManaged(isAnalyticsPage);
                logger.info("📊 Analytics filter visibility: " + isAnalyticsPage);
            }
            
            // Show/hide manage products button based on page
            if (manageProductsButton != null) {
                boolean isProductsPage = "Products".equals(title);
                manageProductsButton.setVisible(isProductsPage);
                manageProductsButton.setManaged(isProductsPage);
                logger.info("🛠️ Manage Products button visibility: " + isProductsPage);
            }
            
            logger.info("✅ Page title updated successfully");
        } else {
            logger.warning("⚠️ Page title label is null - cannot update title");
        }
    }
    
    /**
     * Initialize the analytics time filter ComboBox
     */
    private void initializeAnalyticsFilter() {
        logger.info("📅 Initializing analytics time filter...");
        
        if (analyticsTimeFilter != null) {
            analyticsTimeFilter.setItems(FXCollections.observableArrayList(
                "This Week",
                "This Month", 
                "Last 7 Days",
                "Last 14 Days",
                "Last 30 Days",
                "Last 90 Days",
                "This Year"
            ));
            
            // Set default selection
            analyticsTimeFilter.setValue("Last 30 Days");
            
            // Add change listener
            analyticsTimeFilter.setOnAction(e -> handleTimeFilterChange());
            
            // Initially hidden
            analyticsTimeFilter.setVisible(false);
            analyticsTimeFilter.setManaged(false);
            
            logger.info("✅ Analytics time filter initialized");
        } else {
            logger.warning("⚠️ analyticsTimeFilter is null - check FXML fx:id");
        }
    }
    
    /**
     * Handle time filter changes in analytics
     */
    private void handleTimeFilterChange() {
        String selectedPeriod = analyticsTimeFilter.getValue();
        logger.info("⏰ Analytics time filter changed to: " + selectedPeriod);
        
        // Communicate with AnalyticsController for real-time data updates
        if (analyticsController != null) {
            analyticsController.refreshDataForTimePeriod(selectedPeriod);
            logger.info("✅ Analytics data refreshed for period: " + selectedPeriod);
        } else {
            logger.warning("⚠️ AnalyticsController reference not set - cannot refresh data");
        }
    }

    private void setupButtonAnimations() {
        // Create hover effects
        ScaleTransition scaleUpTransition = new ScaleTransition(HOVER_DURATION, sidebarToggleButton);
        scaleUpTransition.setToX(1.1);
        scaleUpTransition.setToY(1.1);

        ScaleTransition scaleDownTransition = new ScaleTransition(HOVER_DURATION, sidebarToggleButton);
        scaleDownTransition.setToX(1.0);
        scaleDownTransition.setToY(1.0);

        FadeTransition fadeInTransition = new FadeTransition(HOVER_DURATION, burgerIcon);
        fadeInTransition.setToValue(0.8);

        FadeTransition fadeOutTransition = new FadeTransition(HOVER_DURATION, burgerIcon);
        fadeOutTransition.setToValue(1.0);

        // Create shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        shadow.setRadius(4);
        shadow.setSpread(0.2);

        // Add hover listeners
        sidebarToggleButton.setOnMouseEntered(e -> {
            sidebarToggleButton.setStyle("-fx-background-color: #FEF3C7; -fx-background-radius: 4;");
            sidebarToggleButton.setEffect(shadow);
            scaleUpTransition.play();
            fadeInTransition.play();
        });

        sidebarToggleButton.setOnMouseExited(e -> {
            sidebarToggleButton.setStyle("-fx-background-color: transparent;");
            sidebarToggleButton.setEffect(null);
            scaleDownTransition.play();
            fadeOutTransition.play();
        });

        sidebarToggleButton.setOnMousePressed(e -> {
            sidebarToggleButton.setStyle("-fx-background-color: #FDE68A; -fx-background-radius: 4;");
            scaleDownTransition.setToX(0.95);
            scaleDownTransition.setToY(0.95);
            scaleDownTransition.play();
        });

        sidebarToggleButton.setOnMouseReleased(e -> {
            if (sidebarToggleButton.isHover()) {
                sidebarToggleButton.setStyle("-fx-background-color: #FEF3C7; -fx-background-radius: 4;");
                scaleUpTransition.play();
            } else {
                sidebarToggleButton.setStyle("-fx-background-color: transparent;");
                scaleDownTransition.setToX(1.0);
                scaleDownTransition.setToY(1.0);
                scaleDownTransition.play();
            }
        });
    }

    private void setupManageProductsButton() {
        if (manageProductsButton != null) {
            manageProductsButton.setOnAction(event -> handleManageProducts());
            
            // Initially hidden
            manageProductsButton.setVisible(false);
            manageProductsButton.setManaged(false);
            
            logger.info("✅ Manage Products button initialized");
        } else {
            logger.warning("⚠️ manageProductsButton is null - check FXML fx:id");
        }
    }
    
    private void handleManageProducts() {
        logger.info("🛠️ Opening Product Management window...");
        
        try {
            // Load the Product Management FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProductManagement.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Get the controller and set up real-time refresh callback
            Object controllerObj = loader.getController();
            if (controllerObj instanceof com.example.wondertrackxd.controller.products.ProductManagementController) {
                com.example.wondertrackxd.controller.products.ProductManagementController controller = 
                    (com.example.wondertrackxd.controller.products.ProductManagementController) controllerObj;
                
                // Set up callback for real-time updates
                controller.setOnDataChangedCallback(() -> {
                    AppShellController appShellController = AppShellController.getInstance();
                    if (appShellController != null && "Products".equals(pageTitle.getText())) {
                        logger.info("🔄 Real-time refresh triggered for Products page...");
                        javafx.application.Platform.runLater(() -> {
                            appShellController.navigateToPage("Products.fxml", "Products");
                        });
                    }
                });
            }
            
            // Create and configure the stage
            Stage stage = new Stage();
            stage.setTitle("Manage Products");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMinWidth(700);
            stage.setMinHeight(700);
            stage.setWidth(700);
            stage.setHeight(700);
            
            // Show the window and wait for it to close
            stage.showAndWait();
            
            // After the window closes, do a final refresh to ensure everything is up to date
            AppShellController appShellController = AppShellController.getInstance();
            if (appShellController != null && "Products".equals(pageTitle.getText())) {
                logger.info("🔄 Final refresh after ProductManagement window closed...");
                appShellController.navigateToPage("Products.fxml", "Products");
            }
            
            logger.info("✅ Product Management window closed and Products page refreshed");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error opening Product Management window", e);
        }
    }

    /**
     * Handle sidebar toggle button click
     * Uses static reference to communicate with AppShellController
     */
    @FXML
    public void handleSidebarToggle() {
        logger.info("🔄 Sidebar toggle button clicked");
        
        try {
            // Get the AppShellController instance
            AppShellController appShellController = AppShellController.getInstance();
            
            if (appShellController != null) {
                boolean isCollapsing = appShellController.isSidebarVisible();
                // Start header animation first
                animateHeader(isCollapsing);
                // Then trigger sidebar toggle
                appShellController.toggleSidebar();
                logger.info("✅ Sidebar toggle request sent to AppShellController");
            } else {
                logger.warning("⚠️ AppShellController instance not available - sidebar toggle failed");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during sidebar toggle", e);
        }
    }

    /**
     * Animate the header components
     * @param isCollapsing true if sidebar is being hidden, false if being shown
     */
    private void animateHeader(boolean isCollapsing) {
        try {
            double targetX = isCollapsing ? -EXPANDED_WIDTH : 0;
            
            // Translate the logo section
            TranslateTransition logoTransition = new TranslateTransition(ANIMATION_DURATION, logoSection);
            logoTransition.setToX(targetX);
            
            // Translate the navigation section
            TranslateTransition navTransition = new TranslateTransition(ANIMATION_DURATION, navSection);
            navTransition.setToX(targetX);

            // Rotate burger icon
            RotateTransition rotateTransition = new RotateTransition(ANIMATION_DURATION, burgerIcon);
            rotateTransition.setByAngle(isCollapsing ? 180 : -180);
            
            // Combine all animations
            ParallelTransition parallelTransition = new ParallelTransition(
                logoTransition,
                navTransition,
                rotateTransition
            );
            
            // Handle visibility before animation starts
            if (!isCollapsing) {
                logoSection.setVisible(true);
            }
            
            // Handle visibility after animation ends
            parallelTransition.setOnFinished(e -> {
                if (isCollapsing) {
                    logoSection.setVisible(false);
                }
                burgerIcon.setRotate(0); // Reset rotation for next animation
            });
            
            parallelTransition.play();
            logger.info("✅ Header animation started: " + (isCollapsing ? "collapsing" : "expanding"));
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during header animation", e);
        }
    }

    /**
     * Get the SalesController instance
     * @return The current SalesController instance
     */
    public static com.example.wondertrackxd.controller.sales.SalesController getSalesController() {
        return salesController;
    }

    /**
     * Get the AnalyticsController instance
     * @return The current AnalyticsController instance
     */
    public static AnalyticsController getAnalyticsController() {
        return analyticsController;
    }
}
