package com.example.wondertrackxd.controller.sidebar;

import com.example.wondertrackxd.MainApp;
import com.example.wondertrackxd.controller.AppShellController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Controller for the sidebar navigation
 * Handles page navigation and user interactions
 */
public class SidebarController {

    // Logger for sidebar operations tracking
    private static final Logger logger = Logger.getLogger(SidebarController.class.getName());

    @FXML
    private ToggleButton dashboardButton;
    
    @FXML
    private ToggleButton ordersButton;
    
    @FXML
    private ToggleButton salesButton;
    
    @FXML
    private ToggleButton productsButton;
    
    @FXML
    private ToggleButton analyticsButton;
    
    @FXML
    private Button logoutButton;

    // Track current pulsing animation
    private Timeline pulseAnimation;

    /**
     * Initialize the sidebar controller
     * Sets up initial button states and event handlers
     */
    @FXML
    public void initialize() {
        logger.info("🔧 Sidebar controller initialized");
        
        // Set Overview as the initially selected page
        dashboardButton.setSelected(true);
        updateButtonStates(dashboardButton);
        
        // Set up button styling for all navigation buttons
        setupButtonStyling();
        
        // Set up hover effects for navigation buttons
        setupNavigationButtonHoverEffects();
        
        // Set up logout button hover effects
        setupLogoutButtonHoverEffects();
    }

    /**
     * Handle Overview/Dashboard button click
     */
    @FXML
    public void handleDashboard() {
        logger.info("📊 Overview page requested");
        
        // Navigate to Overview page
        navigateToPage("Overview.fxml", "Overview", dashboardButton);
    }

    /**
     * Handle Orders button click
     */
    @FXML
    public void handleOrders() {
        logger.info("📦 Orders page requested");
        
        // Navigate to Orders page
        navigateToPage("Orders.fxml", "Orders", ordersButton);
    }

    /**
     * Handle Sales button click
     */
    @FXML
    public void handleSales() {
        logger.info("💰 Sales page requested");
        
        // Navigate to Sales page
        navigateToPage("Sales.fxml", "Sales", salesButton);
    }

    /**
     * Handle Products button click
     */
    @FXML
    public void handleProducts() {
        logger.info("🏷️ Products page requested");
        
        // Navigate to Products page
        navigateToPage("Products.fxml", "Products", productsButton);
    }

    /**
     * Handle Analytics button click
     */
    @FXML
    public void handleAnalytics() {
        logger.info("📈 Analytics page requested");
        
        // Navigate to Analytics page
        navigateToPage("Analytics.fxml", "Analytics", analyticsButton);
    }

    // Settings functionality removed as per requirements

    /**
     * Handle logout button click
     * Shows confirmation dialog and logs out user
     */
    @FXML
    public void handleLogout() {
        logger.info("👋 Logout requested");
        
        try {
            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Logout");
            confirmAlert.setHeaderText("Are you sure you want to log out?");
            confirmAlert.setContentText("You will be redirected to the login screen.");
            
            // Handle user response
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    logger.info("✅ User confirmed logout");
                    
                    // Switch back to login screen
                    MainApp.switchToLogin();
                    logger.info("🔐 Successfully logged out and returned to login screen");
                } else {
                    logger.info("❌ User cancelled logout");
                }
            });
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during logout process", e);
        }
    }

    /**
     * Navigate to the specified page
     * @param fxmlFile The FXML file name
     * @param pageTitle The page title
     * @param selectedButton The button that was clicked
     */
    private void navigateToPage(String fxmlFile, String pageTitle, ToggleButton selectedButton) {
        try {
            // Get AppShellController instance
            AppShellController appShellController = AppShellController.getInstance();
            
            if (appShellController != null) {
                // Navigate to the requested page
                appShellController.navigateToPage(fxmlFile, pageTitle);
                
                // Update button states
                updateButtonStates(selectedButton);
                
                logger.info("✅ Successfully navigated to " + pageTitle + " page");
            } else {
                logger.warning("⚠️ AppShellController instance not available for navigation");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during navigation to " + pageTitle, e);
        }
    }

    /**
     * Update the visual state of navigation buttons
     * @param selectedButton The currently selected button
     */
    private void updateButtonStates(ToggleButton selectedButton) {
        logger.info("🎨 Updating button states - selected: " + getButtonName(selectedButton));
        
        // Stop any existing pulse animation
        if (pulseAnimation != null) {
            pulseAnimation.stop();
        }
        
        // Reset all buttons
        dashboardButton.setSelected(false);
        ordersButton.setSelected(false);
        salesButton.setSelected(false);
        productsButton.setSelected(false);
        analyticsButton.setSelected(false);
        
        // Set the selected button
        selectedButton.setSelected(true);
        
        // Apply appropriate styling
        applyButtonStyling();
        
        // Add subtle pulse animation to selected button
        addPulseAnimation(selectedButton);
    }

    /**
     * Set up initial button styling
     */
    private void setupButtonStyling() {
        logger.info("🎨 Setting up button styling");
        applyButtonStyling();
    }

    /**
     * Apply styling to all navigation buttons based on their selected state
     * Works in conjunction with hover effects
     */
    private void applyButtonStyling() {
        // Style for selected buttons
        String selectedStyle = "-fx-background-color: #FEF3C7; -fx-background-radius: 4; -fx-text-fill: #78350f;";
        
        // Style for unselected buttons
        String unselectedStyle = "-fx-background-color: transparent; -fx-text-fill: #78350f;";
        
        // Apply styles based on selection state, but only if not hovering
        applyButtonStyle(dashboardButton, selectedStyle, unselectedStyle);
        applyButtonStyle(ordersButton, selectedStyle, unselectedStyle);
        applyButtonStyle(salesButton, selectedStyle, unselectedStyle);
        applyButtonStyle(productsButton, selectedStyle, unselectedStyle);
        applyButtonStyle(analyticsButton, selectedStyle, unselectedStyle);
    }
    
    /**
     * Apply appropriate style to a button based on its state
     * @param button The button to style
     * @param selectedStyle Style for selected state
     * @param unselectedStyle Style for unselected state
     */
    private void applyButtonStyle(ToggleButton button, String selectedStyle, String unselectedStyle) {
        // Only apply style if button is not currently being hovered
        // (hover effects will handle styling during hover)
        if (!button.isHover()) {
            button.setStyle(button.isSelected() ? selectedStyle : unselectedStyle);
            
            // Reset scale to normal if button is not selected and not hovered
            if (!button.isSelected()) {
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            }
        }
    }

    /**
     * Get button name for logging purposes
     * @param button The button to get name for
     * @return Button name as string
     */
    private String getButtonName(ToggleButton button) {
        if (button == dashboardButton) return "Overview";
        if (button == ordersButton) return "Orders";
        if (button == salesButton) return "Sales";
        if (button == productsButton) return "Products";
        if (button == analyticsButton) return "Analytics";
        return "Unknown";
    }
    
    /**
     * Set up hover effects for the logout button
     */
    private void setupLogoutButtonHoverEffects() {
        logger.info("✨ Setting up logout button hover effects");
        
        if (logoutButton != null) {
            // Store original style
            String originalStyle = "-fx-background-color: fef2f2; -fx-border-color: fecaca; -fx-background-radius: 4; -fx-border-radius: 4;";
            String hoverStyle = "-fx-background-color: fca5a5; -fx-border-color: f87171; -fx-background-radius: 4; -fx-border-radius: 4; -fx-scale-x: 1.02; -fx-scale-y: 1.02;";
            String pressedStyle = "-fx-background-color: ef4444; -fx-border-color: dc2626; -fx-background-radius: 4; -fx-border-radius: 4; -fx-scale-x: 0.98; -fx-scale-y: 0.98;";
            
            // Hover enter effect
            logoutButton.setOnMouseEntered(e -> {
                logoutButton.setStyle(hoverStyle);
            });
            
            // Hover exit effect
            logoutButton.setOnMouseExited(e -> {
                logoutButton.setStyle(originalStyle);
            });
            
            // Press effect
            logoutButton.setOnMousePressed(e -> {
                logoutButton.setStyle(pressedStyle);
            });
            
            // Release effect
            logoutButton.setOnMouseReleased(e -> {
                if (logoutButton.isHover()) {
                    logoutButton.setStyle(hoverStyle);
                } else {
                    logoutButton.setStyle(originalStyle);
                }
            });
            
            logger.info("✅ Logout button hover effects setup completed");
        }
    }

    /**
     * Set up hover effects for navigation buttons with smooth transitions
     */
    private void setupNavigationButtonHoverEffects() {
        logger.info("✨ Setting up navigation button hover effects");
        
        // Setup hover effects for each navigation button
        setupButtonHover(dashboardButton);
        setupButtonHover(ordersButton);
        setupButtonHover(salesButton);
        setupButtonHover(productsButton);
        setupButtonHover(analyticsButton);
        
        logger.info("✅ Navigation button hover effects setup completed");
    }
    
    /**
     * Setup hover effects for a specific toggle button
     * @param button The toggle button to setup hover effects for
     */
    private void setupButtonHover(ToggleButton button) {
        // Define styles
        String selectedStyle = "-fx-background-color: #FEF3C7; -fx-background-radius: 4; -fx-text-fill: #78350f;";
        String unselectedStyle = "-fx-background-color: transparent; -fx-text-fill: #78350f;";
        String hoverStyle = "-fx-background-color: #FFFBEB; -fx-background-radius: 4; -fx-text-fill: #92400d;";
        String selectedHoverStyle = "-fx-background-color: #FDE68A; -fx-background-radius: 4; -fx-text-fill: #92400d;";
        
        // Hover enter effect with scale animation
        button.setOnMouseEntered(e -> {
            // Create scale transition
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(100), button);
            scaleIn.setToX(1.02);
            scaleIn.setToY(1.02);
            
            // Apply appropriate hover style based on selection state
            if (button.isSelected()) {
                button.setStyle(selectedHoverStyle);
            } else {
                button.setStyle(hoverStyle);
            }
            
            scaleIn.play();
        });
        
        // Hover exit effect with scale animation
        button.setOnMouseExited(e -> {
            // Create scale transition
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(100), button);
            scaleOut.setToX(1.0);
            scaleOut.setToY(1.0);
            
            // Apply appropriate normal style based on selection state
            if (button.isSelected()) {
                button.setStyle(selectedStyle);
            } else {
                button.setStyle(unselectedStyle);
            }
            
            scaleOut.play();
        });
        
        // Press effect
        button.setOnMousePressed(e -> {
            ScaleTransition scalePress = new ScaleTransition(Duration.millis(50), button);
            scalePress.setToX(0.98);
            scalePress.setToY(0.98);
            scalePress.play();
        });
        
        // Release effect
        button.setOnMouseReleased(e -> {
            ScaleTransition scaleRelease = new ScaleTransition(Duration.millis(50), button);
            if (button.isHover()) {
                scaleRelease.setToX(1.02);
                scaleRelease.setToY(1.02);
            } else {
                scaleRelease.setToX(1.0);
                scaleRelease.setToY(1.0);
            }
            scaleRelease.play();
        });
    }

    /**
     * Add a subtle pulse animation to the selected button
     * @param button The button to animate
     */
    private void addPulseAnimation(ToggleButton button) {
        // Create a subtle pulsing effect for the selected button
        pulseAnimation = new Timeline(
            new KeyFrame(Duration.seconds(0), new KeyValue(button.opacityProperty(), 1.0)),
            new KeyFrame(Duration.seconds(1.5), new KeyValue(button.opacityProperty(), 0.85)),
            new KeyFrame(Duration.seconds(3.0), new KeyValue(button.opacityProperty(), 1.0))
        );
        
        pulseAnimation.setCycleCount(Timeline.INDEFINITE);
        pulseAnimation.play();
        
        // Stop pulse animation when button is no longer selected or when hovering
        button.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (!isSelected && pulseAnimation != null) {
                pulseAnimation.stop();
                button.setOpacity(1.0);
            }
        });
        
        button.hoverProperty().addListener((obs, wasHovered, isHovered) -> {
            if (isHovered && pulseAnimation != null) {
                pulseAnimation.pause();
                button.setOpacity(1.0);
            } else if (!isHovered && button.isSelected() && pulseAnimation != null) {
                pulseAnimation.play();
            }
        });
    }
}
