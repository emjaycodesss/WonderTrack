package com.example.wondertrackxd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main application class that initializes and starts the WonderTrack application
 * Loads necessary fonts and begins with the login screen
 */
public class MainApp extends Application {

    // Logger for application startup tracking
    private static final Logger logger = Logger.getLogger(MainApp.class.getName());
    
    // Static reference to primary stage for scene switching
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        logger.info("🚀 Starting WonderTrack Application...");
        
        // Store primary stage reference for later use
        primaryStage = stage;
        
        try {
            // Load Inter font family for consistent UI styling
            logger.info("📝 Loading Inter font family...");
            Font interBold = Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-Bold.ttf"), 10);
            Font interExtraBold = Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-ExtraBold.ttf"), 10);
            Font interMedium = Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-Medium.ttf"), 10);
            Font interSemiBold = Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-SemiBold.ttf"), 10);
            logger.info("✅ Inter fonts loaded successfully");

            // Load the login screen as the initial view
            logger.info("🔐 Loading Login screen...");
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/Login.fxml")));
            
            // Configure the primary stage
            primaryStage.setTitle("WonderTrack - Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false); // Login screen should be fixed size
            primaryStage.centerOnScreen();
            
            // Set up proper window close behavior
            primaryStage.setOnCloseRequest(e -> {
                logger.info("🚪 Application close requested");
                javafx.application.Platform.exit();
                System.exit(0);
            });
            
            primaryStage.show();
            
            logger.info("✅ Application started successfully with Login screen");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Failed to start application", e);
            throw e;
        }
    }

    /**
     * Get reference to the primary stage for scene switching
     * @return Primary stage instance
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Switch to the main application view after successful login
     */
    public static void switchToMainApp() {
        try {
            logger.info("🏠 Switching to main application view...");
            
            Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/AppShell.fxml"));
            primaryStage.setTitle("WonderTrack - Order & Sales Tracking System");
            primaryStage.setScene(new Scene(root, 1200, 800)); // Set specific window size
            primaryStage.setResizable(true); // Main app should be resizable
            primaryStage.setMinWidth(800); // Set minimum width
            primaryStage.setMinHeight(600); // Set minimum height
            primaryStage.centerOnScreen();
            
            // Ensure proper window close behavior for main app
            primaryStage.setOnCloseRequest(e -> {
                logger.info("🚪 Main application close requested");
                javafx.application.Platform.exit();
                System.exit(0);
            });
            
            logger.info("✅ Successfully switched to main application");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Failed to switch to main application", e);
        }
    }

    /**
     * Switch back to login view (for logout functionality)
     */
    public static void switchToLogin() {
        try {
            logger.info("🔐 Switching back to login view...");
            
            Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/Login.fxml"));
            primaryStage.setTitle("WonderTrack - Login");
            
            // Reset window properties to original login state
            Scene loginScene = new Scene(root);
            primaryStage.setScene(loginScene);
            primaryStage.setResizable(false);
            
            // Clear any size constraints from main app
            primaryStage.setMinWidth(0);
            primaryStage.setMinHeight(0);
            primaryStage.setMaxWidth(Double.MAX_VALUE);
            primaryStage.setMaxHeight(Double.MAX_VALUE);
            
            // Let the login window size to its content
            primaryStage.sizeToScene();
            primaryStage.centerOnScreen();
            
            logger.info("✅ Successfully switched to login view with original dimensions");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Failed to switch to login view", e);
        }
    }

    public static void main(String[] args) {
        logger.info("🎯 Launching WonderTrack application...");
        launch(args);
    }
}
