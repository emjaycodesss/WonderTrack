package com.example.wondertrackxd.controller;

import com.example.wondertrackxd.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Controller for the login screen
 * Handles user authentication and navigation to main application
 */
public class LoginController {

    // Logger for authentication tracking
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    // Hardcoded credentials for demonstration
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "WonderAdmin123!";

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private TextField visiblePasswordField;
    
    @FXML
    private Button togglePasswordVisibility;
    
    @FXML
    private javafx.scene.image.ImageView eyeIcon;

    @FXML
    private Button signInButton;
    
    // Track password visibility state
    private boolean isPasswordVisible = false;

    /**
     * Initialize the login controller
     * Sets up event handlers and UI animations
     */
    @FXML
    public void initialize() {
        logger.info("🔐 Login controller initialized");
        
        // Set up button hover animations
        setupButtonAnimations();
        
        // Set up Enter key functionality
        setupKeyboardShortcuts();
        
        // Set focus to username field for better UX
        if (usernameField != null) {
            usernameField.requestFocus();
        }
        
        // Initialize password fields
        setupPasswordFields();
    }

    /**
     * Handle sign in button click
     * Validates credentials and navigates to main app if successful
     */
    @FXML
    public void handleSignIn() {
        logger.info("🔑 Sign in attempt initiated");
        
        try {
            // Get input values
            String username = usernameField.getText().trim();
            String password = getCurrentPassword();
            
            // Log authentication attempt (without password for security)
            logger.info("👤 Authentication attempt for username: " + username);
            
            // Validate input fields
            if (username.isEmpty() || password.isEmpty()) {
                logger.warning("⚠️ Empty username or password field");
                showErrorAlert("Invalid Input", "Please enter both username and password.");
                return;
            }
            
            // Authenticate credentials
            if (authenticateUser(username, password)) {
                logger.info("✅ Authentication successful for user: " + username);
                
                // Show success feedback with animation
                showSuccessAnimation();
                
                // Navigate to main application after short delay
                javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(500));
                delay.setOnFinished(e -> {
                    logger.info("🏠 Redirecting to main application...");
                    MainApp.switchToMainApp();
                });
                delay.play();
                
            } else {
                logger.warning("❌ Authentication failed for user: " + username);
                showErrorAlert("Authentication Failed", 
                    "Invalid username or password.\n\nValid credentials:\nUsername: admin\nPassword: WonderAdmin123!");
                
                // Clear password fields for security
                clearPasswordFields();
                usernameField.requestFocus();
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during sign in process", e);
            showErrorAlert("Login Error", "An unexpected error occurred. Please try again.");
        }
    }

    /**
     * Authenticate user credentials
     * @param username The entered username
     * @param password The entered password
     * @return true if credentials are valid, false otherwise
     */
    private boolean authenticateUser(String username, String password) {
        // Simple credential validation (in production, this would connect to a database)
        boolean isValid = VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
        
        logger.info("🔍 Credential validation result: " + (isValid ? "VALID" : "INVALID"));
        return isValid;
    }

    /**
     * Show error alert dialog
     * @param title Alert title
     * @param message Alert message
     */
    private void showErrorAlert(String title, String message) {
        logger.warning("⚠️ Showing error alert: " + title);
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show success animation on login button
     */
    private void showSuccessAnimation() {
        logger.info("✨ Playing success animation");
        
        // Change button appearance temporarily
        signInButton.setText("Success!");
        signInButton.setStyle("-fx-background-color: #059669; -fx-text-fill: white;");
        
        // Scale animation
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), signInButton);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.05);
        scaleTransition.setToY(1.05);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.play();
    }

    /**
     * Set up button hover animations
     */
    private void setupButtonAnimations() {
        logger.info("🎨 Setting up button animations");
        
        // Hover effect for sign in button
        signInButton.setOnMouseEntered(e -> {
            signInButton.setStyle("-fx-background-color: #B45309; -fx-text-fill: #fffef9;");
        });
        
        signInButton.setOnMouseExited(e -> {
            signInButton.setStyle("-fx-background-color: #CF8032; -fx-text-fill: #fffef9;");
        });
        
        // Press effect
        signInButton.setOnMousePressed(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), signInButton);
            scaleDown.setToX(0.95);
            scaleDown.setToY(0.95);
            scaleDown.play();
        });
        
        signInButton.setOnMouseReleased(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), signInButton);
            scaleUp.setToX(1.0);
            scaleUp.setToY(1.0);
            scaleUp.play();
        });
    }

    /**
     * Set up keyboard shortcuts for better UX
     */
    private void setupKeyboardShortcuts() {
        logger.info("⌨️ Setting up keyboard shortcuts");
        
        // Enter key triggers sign in from any field
        usernameField.setOnAction(e -> {
            if (isPasswordVisible) {
                visiblePasswordField.requestFocus();
            } else {
                passwordField.requestFocus();
            }
        });
        passwordField.setOnAction(e -> handleSignIn());
        visiblePasswordField.setOnAction(e -> handleSignIn());
    }
    
    /**
     * Set up password field synchronization and initial state
     */
    private void setupPasswordFields() {
        logger.info("🔒 Setting up password fields");
        
        // Initially show password field (hidden), hide visible field
        passwordField.setVisible(true);
        passwordField.setManaged(true);
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);
        
        // Synchronize text between fields
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isPasswordVisible) {
                visiblePasswordField.setText(newValue);
            }
        });
        
        visiblePasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isPasswordVisible) {
                passwordField.setText(newValue);
            }
        });
        
        // Set initial eye icon
        updateEyeIcon();
    }
    
    /**
     * Toggle password visibility between hidden and visible
     */
    @FXML
    public void togglePasswordVisibility() {
        logger.info("👁 Toggling password visibility");
        
        try {
            isPasswordVisible = !isPasswordVisible;
            
            if (isPasswordVisible) {
                // Show password as text
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                visiblePasswordField.requestFocus();
                visiblePasswordField.positionCaret(visiblePasswordField.getText().length());
            } else {
                // Hide password
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
                passwordField.requestFocus();
                passwordField.positionCaret(passwordField.getText().length());
            }
            
            updateEyeIcon();
            logger.info("✅ Password visibility toggled to: " + (isPasswordVisible ? "visible" : "hidden"));
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error toggling password visibility", e);
        }
    }
    
    /**
     * Update the eye icon based on current visibility state
     */
    private void updateEyeIcon() {
        if (eyeIcon != null) {
            try {
                if (isPasswordVisible) {
                    // Show closed eye when password is visible (clicking will hide it)
                    javafx.scene.image.Image closedEyeImage = new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/icons/closed_eye_icon.png"));
                    eyeIcon.setImage(closedEyeImage);
                } else {
                    // Show open eye when password is hidden (clicking will reveal it)
                    javafx.scene.image.Image openEyeImage = new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/icons/open_eye_icon.png"));
                    eyeIcon.setImage(openEyeImage);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "⚠️ Failed to load eye icon images", e);
            }
        }
    }
    
    /**
     * Get the current password from whichever field is active
     * @return Current password text
     */
    private String getCurrentPassword() {
        if (isPasswordVisible) {
            return visiblePasswordField.getText();
        } else {
            return passwordField.getText();
        }
    }
    
    /**
     * Clear password fields for security
     */
    private void clearPasswordFields() {
        passwordField.clear();
        visiblePasswordField.clear();
    }
}
