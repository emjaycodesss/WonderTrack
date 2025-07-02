package com.example.wondertrackxd.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.animation.TranslateTransition;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.URL;

/**
 * Main controller for the application shell
 * Manages the main content area and initial page loading
 */
public class AppShellController {

    // Logger for tracking shell operations and debugging
    private static final Logger logger = Logger.getLogger(AppShellController.class.getName());

    @FXML
    private StackPane mainContent;
    
    @FXML
    private BorderPane appShellRoot;
    
    // Static reference for controller communication
    private static AppShellController instance;
    
    // Track sidebar visibility state
    private boolean isSidebarVisible = true;
    
    // Track transition state to prevent overlapping animations
    private boolean isTransitioning = false;
    
    // Store reference to sidebar for toggle functionality
    private Parent sidebarNode;

    private static final Duration ANIMATION_DURATION = Duration.millis(200);
    private final DoubleProperty sidebarWidth = new SimpleDoubleProperty();

    /**
     * Get the singleton instance
     */
    public static AppShellController getInstance() {
        return instance;
    }

    /**
     * Initialize the application shell
     * Loads the Overview page as the default content
     */
    @FXML
    public void initialize() {
        logger.info("🏗️ Initializing Application Shell...");
        
        // Set static instance for controller communication
        instance = this;
        
        // Apply sidebar stability constraints
        setupSidebarStability();
        
        try {
            // Load the Overview page as the initial content
            logger.info("📄 Loading Overview.fxml as default page...");
            
            Parent page = FXMLLoader.load(getClass().getResource("/fxml/Overview.fxml"));
            
            if (page != null) {
                // Clear existing content and set the new page
                mainContent.getChildren().setAll(page);
                logger.info("✅ Overview page loaded successfully into main content");
                
                // Store sidebar reference for toggle functionality
                sidebarNode = (Parent) appShellRoot.getLeft();
                if (sidebarNode instanceof Region) {
                    Region sidebar = (Region) sidebarNode;
                    sidebarWidth.set(sidebar.getPrefWidth());
                    
                    // Ensure sidebar has proper styling with border from the start
                    String sidebarStyle = "-fx-background-color: #FFF9ED; -fx-border-color: #FDE68A; -fx-border-width: 0 1px 0 0; -fx-border-style: solid;";
                    sidebar.setStyle(sidebarStyle);
                    logger.info("🎨 Sidebar styling with border applied explicitly");
                }
                logger.info("📋 Sidebar reference stored for toggle functionality");
            } else {
                logger.severe("❌ Failed to load Overview page - returned null");
            }
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "❌ IOException while loading Overview.fxml", e);
            createErrorFallback();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Unexpected error during AppShell initialization", e);
            createErrorFallback();
        }
        
        logger.info("🏁 Application Shell initialization completed");
    }
    
    /**
     * Setup sidebar stability constraints to prevent shifting during navigation
     */
    private void setupSidebarStability() {
        logger.info("🔧 Setting up sidebar stability constraints...");
        
        try {
            // Ensure BorderPane has fixed constraints
            if (appShellRoot != null) {
                // Set minimum sizes to prevent shrinking
                appShellRoot.setMinWidth(1200.0);
                appShellRoot.setMinHeight(800.0);
                
                // Ensure main content area is responsive
                if (mainContent != null) {
                    // Remove any existing bindings first
                    mainContent.prefWidthProperty().unbind();
                    
                    // Set basic responsive properties
                    mainContent.setMinWidth(0); // Allow shrinking
                    mainContent.setMaxWidth(Double.MAX_VALUE); // Allow expanding
                    
                    logger.info("✅ Main content area configured for responsive sizing");
                }
                
                // Apply constraints after the scene graph is built
                javafx.application.Platform.runLater(() -> {
                    try {
                        Parent leftNode = (Parent) appShellRoot.getLeft();
                        if (leftNode instanceof Region) {
                            Region sidebar = (Region) leftNode;
                            
                            // Store the initial sidebar width
                            sidebarWidth.set(215.0);
                            
                            // Lock sidebar dimensions initially
                            sidebar.setMinWidth(215.0);
                            sidebar.setMaxWidth(215.0);
                            sidebar.setPrefWidth(215.0);
                            
                            // Prevent content from affecting size
                            sidebar.setSnapToPixel(true);
                            
                            // Ensure sidebar doesn't interfere with main content layout
                            sidebar.autosize();
                            
                            logger.info("✅ Sidebar stability constraints applied successfully");
                        }
                        
                        // Ensure the main content area is properly configured for resizing
                        if (mainContent != null) {
                            // Remove any existing bindings first
                            mainContent.prefWidthProperty().unbind();
                            
                            // Set basic responsive properties
                            mainContent.setMinWidth(0); // Allow shrinking
                            mainContent.setMaxWidth(Double.MAX_VALUE); // Allow expanding
                            
                            logger.info("✅ Main content area configured for responsive sizing");
                        }
                        
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "⚠️ Could not apply sidebar constraints", e);
                    }
                });
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "⚠️ Error setting up sidebar stability", e);
        }
    }

    /**
     * Create fallback content when the main page fails to load
     */
    private void createErrorFallback() {
        logger.warning("⚠️ Creating error fallback content...");
        
        try {
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "Error loading content. Please check the logs."
            );
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            
            mainContent.getChildren().setAll(errorLabel);
            logger.info("✅ Error fallback content created");
            
        } catch (Exception fallbackError) {
            logger.log(Level.SEVERE, "❌ Failed to create error fallback", fallbackError);
        }
    }
    
    /**
     * Toggle sidebar visibility with synchronized animations
     */
    public void toggleSidebar() {
        logger.info("🔄 Toggling sidebar visibility...");
        
        try {
            if (sidebarNode == null || !(sidebarNode instanceof Region)) {
                logger.warning("⚠️ Sidebar node not available for toggle");
                return;
            }

            Region sidebar = (Region) sidebarNode;
            
            // Define animation properties
            Duration animationDuration = Duration.millis(200); // Match header animation speed
            double sidebarFullWidth = sidebarWidth.get();
            
            if (isSidebarVisible) {
                // COLLAPSING: Sidebar visible -> hidden
                logger.info("📁 Collapsing sidebar...");
                
                // Get sidebar children for content animation
                javafx.scene.Node[] sidebarChildren = sidebar.getChildrenUnmodifiable().toArray(new javafx.scene.Node[0]);
                
                // Create content fade-out and slide-out animation
                ParallelTransition contentAnimation = new ParallelTransition();
                
                for (javafx.scene.Node child : sidebarChildren) {
                    // Fade out animation
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(150), child);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setInterpolator(javafx.animation.Interpolator.EASE_IN);
                    
                    // Slide out animation (slide completely off-screen to the left)
                    TranslateTransition slideOut = new TranslateTransition(Duration.millis(150), child);
                    slideOut.setFromX(0.0);
                    slideOut.setToX(-sidebarFullWidth); // Slide completely off-screen left
                    slideOut.setInterpolator(javafx.animation.Interpolator.EASE_IN);
                    
                    // Add both animations to parallel transition
                    contentAnimation.getChildren().addAll(fadeOut, slideOut);
                }
                
                // Create sidebar width animation (shrink to 0) - preserve border during animation
                Timeline sidebarAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO,
                        new KeyValue(sidebar.prefWidthProperty(), sidebarFullWidth),
                        new KeyValue(sidebar.minWidthProperty(), sidebarFullWidth),
                        new KeyValue(sidebar.maxWidthProperty(), sidebarFullWidth)
                    ),
                    new KeyFrame(animationDuration,
                        new KeyValue(sidebar.prefWidthProperty(), 0, javafx.animation.Interpolator.EASE_BOTH),
                        new KeyValue(sidebar.minWidthProperty(), 0, javafx.animation.Interpolator.EASE_BOTH),
                        new KeyValue(sidebar.maxWidthProperty(), 0, javafx.animation.Interpolator.EASE_BOTH)
                    )
                );
                
                // Preserve sidebar styling and border during animation
                String originalStyle = "-fx-background-color: #FFF9ED; -fx-border-color: #FDE68A; -fx-border-width: 0 1px 0 0; -fx-border-style: solid;";
                
                // Force layout updates during animation for responsive main content
                sidebarAnimation.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    // Ensure border remains visible during animation
                    sidebar.setStyle(originalStyle);
                    appShellRoot.requestLayout();
                });
                
                // Create parallel animation: content fades AND width shrinks simultaneously
                ParallelTransition collapseAnimation = new ParallelTransition(
                    contentAnimation,
                    sidebarAnimation
                );
                
                collapseAnimation.setOnFinished(e -> {
                    // Ensure styling is preserved even when collapsed
                    sidebar.setStyle(originalStyle);
                    // Completely hide sidebar from layout
                    appShellRoot.setLeft(null);
                    appShellRoot.requestLayout(); // Force final layout update
                    logger.info("✅ Sidebar collapsed and hidden from layout");
                });
                
                collapseAnimation.play();
                
            } else {
                // EXPANDING: Sidebar hidden -> visible
                logger.info("📂 Expanding sidebar...");
                
                // First, add sidebar back to layout (but with 0 width)
                sidebar.setPrefWidth(0);
                sidebar.setMinWidth(0);
                sidebar.setMaxWidth(0);
                
                // Preserve original styling and border
                String originalStyle = "-fx-background-color: #FFF9ED; -fx-border-color: #FDE68A; -fx-border-width: 0 1px 0 0; -fx-border-style: solid;";
                sidebar.setStyle(originalStyle);
                
                appShellRoot.setLeft(sidebarNode);
                appShellRoot.requestLayout(); // Force initial layout update
                
                // Set all children to invisible state initially
                javafx.scene.Node[] sidebarChildren = sidebar.getChildrenUnmodifiable().toArray(new javafx.scene.Node[0]);
                for (javafx.scene.Node child : sidebarChildren) {
                    child.setOpacity(0.0);
                    child.setTranslateX(-sidebarFullWidth); // Start completely off-screen left
            }

                // Create sidebar expansion animation
                Timeline sidebarAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO,
                        new KeyValue(sidebar.prefWidthProperty(), 0),
                        new KeyValue(sidebar.minWidthProperty(), 0),
                        new KeyValue(sidebar.maxWidthProperty(), 0)
                    ),
                    new KeyFrame(animationDuration,
                        new KeyValue(sidebar.prefWidthProperty(), sidebarFullWidth, javafx.animation.Interpolator.EASE_BOTH),
                        new KeyValue(sidebar.minWidthProperty(), sidebarFullWidth, javafx.animation.Interpolator.EASE_BOTH),
                        new KeyValue(sidebar.maxWidthProperty(), sidebarFullWidth, javafx.animation.Interpolator.EASE_BOTH)
                    )
                );
                
                // Create content fade-in and slide-in animation (with slight delay for better visual flow)
                ParallelTransition contentAnimation = new ParallelTransition();
                
                for (javafx.scene.Node child : sidebarChildren) {
                    // Fade in animation
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(150), child);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
                    fadeIn.setDelay(Duration.millis(25)); // Reduced delay for faster feel
                    
                    // Slide in animation (slide from the left)
                    TranslateTransition slideIn = new TranslateTransition(Duration.millis(150), child);
                    slideIn.setFromX(-sidebarFullWidth); // Start completely off-screen left
                    slideIn.setToX(0.0);
                    slideIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
                    slideIn.setDelay(Duration.millis(25)); // Reduced delay for faster feel
                    
                    // Add both animations to parallel transition
                    contentAnimation.getChildren().addAll(fadeIn, slideIn);
                }
                
                // Force layout updates during animation for responsive main content
                sidebarAnimation.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    // Ensure border remains visible during animation
                    sidebar.setStyle(originalStyle);
                    appShellRoot.requestLayout();
                });
                
                // Create parallel animation: width expands AND content fades in simultaneously
                ParallelTransition expandAnimation = new ParallelTransition(
                    sidebarAnimation,
                    contentAnimation
                );
                
                expandAnimation.setOnFinished(e -> {
                    // Ensure all children are in final state
                    for (javafx.scene.Node child : sidebarChildren) {
                        child.setOpacity(1.0);
                        child.setTranslateX(0.0);
                    }
                    // Ensure final styling is preserved with explicit border
                    sidebar.setStyle(originalStyle);
                    appShellRoot.requestLayout(); // Force final layout update
                    logger.info("✅ Sidebar expanded and fully visible");
                });
                
                expandAnimation.play();
            }
            
            // Update state
            isSidebarVisible = !isSidebarVisible;
            logger.info("🔄 Sidebar visibility state updated to: " + isSidebarVisible);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during sidebar toggle", e);
        }
    }
    
    /**
     * Get the current sidebar visibility state
     */
    public boolean isSidebarVisible() {
        return isSidebarVisible;
    }
    
    /**
     * Navigate to a specific page with smooth fade transition
     * @param fxmlFile The FXML file name (e.g., "Overview.fxml")
     * @param pageTitle The page title to display in header
     */
    public void navigateToPage(String fxmlFile, String pageTitle) {
        logger.info("🧭 Navigating to page: " + fxmlFile + " with title: " + pageTitle);
        
        // Prevent overlapping transitions
        if (isTransitioning) {
            logger.info("⏳ Transition already in progress, ignoring navigation request");
            return;
        }
        
        try {
            // Log the resource path being loaded
            String resourcePath = "/fxml/" + fxmlFile;
            logger.info("📂 Attempting to load resource: " + resourcePath);
            
            // Get the resource URL and log it
            URL resourceUrl = getClass().getResource(resourcePath);
            if (resourceUrl == null) {
                logger.severe("❌ Resource not found: " + resourcePath);
                return;
            }
            logger.info("📄 Resource found at: " + resourceUrl);
            
            // Create a new FXMLLoader and log its state
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            logger.info("🔧 Created FXMLLoader for: " + resourceUrl);
            
            try {
                // Load the requested page
                Parent newPage = loader.load();
                logger.info("✅ Successfully loaded FXML content");
                
                // Log the controller instance
                Object controller = loader.getController();
                logger.info("🎮 Controller instance: " + (controller != null ? controller.getClass().getSimpleName() : "null"));
                
                if (newPage != null) {
                    // Perform smooth page transition
                    performPageTransition(newPage, pageTitle);
                } else {
                    logger.severe("❌ Loaded page content is null");
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "❌ Error loading FXML content", e);
                createErrorFallback();
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during page navigation", e);
            createErrorFallback();
        }
    }
    
    /**
     * Perform smooth fade and slide transition between pages
     * @param newPage The new page to display
     * @param pageTitle The page title to update
     */
    private void performPageTransition(Parent newPage, String pageTitle) {
        logger.info("🎬 Starting page transition animation...");
        
        // Set transition state to prevent overlapping
        isTransitioning = true;
        
        try {
            // Get current page if exists
            Parent currentPage = null;
            if (!mainContent.getChildren().isEmpty()) {
                currentPage = (Parent) mainContent.getChildren().get(0);
            }
            
            // Set initial state for new page
            newPage.setOpacity(0.0);
            newPage.setTranslateX(30.0); // Start slightly to the right
            
            if (currentPage != null) {
                // Create fade out transition for current page
                FadeTransition fadeOut = new FadeTransition(Duration.millis(120), currentPage);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setInterpolator(javafx.animation.Interpolator.EASE_IN);
                
                // Create slide out transition for current page
                TranslateTransition slideOut = new TranslateTransition(Duration.millis(120), currentPage);
                slideOut.setFromX(0.0);
                slideOut.setToX(-20.0); // Slide slightly to the left
                slideOut.setInterpolator(javafx.animation.Interpolator.EASE_IN);
                
                // Combine fade out and slide out
                ParallelTransition exitTransition = new ParallelTransition(fadeOut, slideOut);
                
                // Create fade in transition for new page
                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), newPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
                
                // Create slide in transition for new page
                TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), newPage);
                slideIn.setFromX(30.0);
                slideIn.setToX(0.0);
                slideIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
                
                // Combine fade in and slide in
                ParallelTransition enterTransition = new ParallelTransition(fadeIn, slideIn);
                
                // Create sequential transition
                SequentialTransition transition = new SequentialTransition();
                
                // Handle page switch between transitions
                exitTransition.setOnFinished(e -> {
                    // Switch pages during the transition
                    mainContent.getChildren().setAll(newPage);
                    logger.info("✅ Page content switched during transition");
                    
                    // CRITICAL: Reapply sidebar styling after new page with CSS is loaded
                    reinforceSidebarStyling();
                    
                    // Update the page title
                    updatePageTitle(pageTitle);
                    logger.info("✅ Page title updated to: " + pageTitle);
                });
                
                // Combine exit and enter transitions
                transition.getChildren().addAll(exitTransition, enterTransition);
                transition.setOnFinished(e -> {
                    // Ensure final state is clean
                    newPage.setOpacity(1.0);
                    newPage.setTranslateX(0.0);
                    isTransitioning = false; // Reset transition state
                    logger.info("🎉 Page transition completed successfully");
                });
                
                transition.play();
                
            } else {
                // No current page, just fade and slide in the new page
                mainContent.getChildren().setAll(newPage);
                
                // CRITICAL: Reapply sidebar styling after new page with CSS is loaded
                reinforceSidebarStyling();
                
                // Create fade in transition
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
                
                // Create slide in transition
                TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), newPage);
                slideIn.setFromX(30.0);
                slideIn.setToX(0.0);
                slideIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
                
                // Combine fade and slide
                ParallelTransition enterTransition = new ParallelTransition(fadeIn, slideIn);
                
                enterTransition.setOnFinished(e -> {
                    // Ensure final state is clean
                    newPage.setOpacity(1.0);
                    newPage.setTranslateX(0.0);
                    isTransitioning = false; // Reset transition state
                    logger.info("🎉 Initial page transition completed");
                });
                
                // Update the page title
                updatePageTitle(pageTitle);
                logger.info("✅ Page title updated to: " + pageTitle);
                
                enterTransition.play();
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during page transition", e);
            // Fallback to immediate page switch with clean state
            mainContent.getChildren().setAll(newPage);
            newPage.setOpacity(1.0);
            newPage.setTranslateX(0.0);
            isTransitioning = false; // Reset transition state
            
            // CRITICAL: Reapply sidebar styling even in fallback case
            reinforceSidebarStyling();
            
            updatePageTitle(pageTitle);
        }
    }
    
    /**
     * Update the page title in the header
     * @param title The new page title
     */
    private void updatePageTitle(String title) {
        try {
            // Get HeaderController instance and update title
            com.example.wondertrackxd.controller.header.HeaderController headerController = 
                com.example.wondertrackxd.controller.header.HeaderController.getInstance();
            
            if (headerController != null) {
                headerController.updatePageTitle(title);
                logger.info("✅ Page title updated to: " + title);
            } else {
                logger.warning("⚠️ HeaderController instance not available for title update");
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "⚠️ Error updating page title", e);
        }
    }

    /**
     * Forcefully reapply sidebar styling to ensure border visibility
     * Called after page loads to override any CSS conflicts
     */
    private void reinforceSidebarStyling() {
        try {
            if (sidebarNode instanceof Region) {
                Region sidebar = (Region) sidebarNode;
                String sidebarStyle = "-fx-background-color: #FFF9ED; -fx-border-color: #FDE68A; -fx-border-width: 0 1px 0 0; -fx-border-style: solid;";
                sidebar.setStyle(sidebarStyle);
                
                // Force immediate style application and layout update
                sidebar.applyCss();
                sidebar.autosize();
                appShellRoot.requestLayout();
                
                logger.info("🎨 Sidebar styling forcefully reapplied to override CSS conflicts");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "⚠️ Error reinforcing sidebar styling", e);
        }
    }
}