package com.example.wondertrackxd.controller.sales;

import com.example.wondertrackxd.controller.analytics.DataService;
import com.example.wondertrackxd.controller.model.SalesRecord;
import com.example.wondertrackxd.controller.header.HeaderController;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Sales Controller
 * Professional POS Sales Management System
 * Handles completed transaction history, analytics, and reporting
 */
public class SalesController implements Initializable {

    private static final Logger logger = Logger.getLogger(SalesController.class.getName());

    // KPI Cards for Sales Dashboard
    @FXML private Label todaySalesAmount;
    @FXML private Label todaySalesDesc;
    @FXML private Label transactionsCount;
    @FXML private Label transactionsDesc;
    @FXML private Label averageTicketAmount;
    @FXML private Label averageTicketDesc;
    @FXML private Label digitalPaymentRatio;
    @FXML private Label digitalPaymentDesc;

    // Sales Table and Controls
    @FXML private TableView<SalesRecord> salesTable;

    // Filters and Search
    @FXML private ComboBox<String> paymentMethodFilter;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortOrderCombo;

    // Pagination
    @FXML private Button prevPageBtn;
    @FXML private Button nextPageBtn;
    @FXML private Label pageLabel;
    @FXML private Label resultsInfoLabel;
    @FXML private Button clearFiltersButton;

    // Data Management
    private List<SalesRecord> allSales = new ArrayList<>();
    private ObservableList<SalesRecord> filteredSales = FXCollections.observableArrayList();
    private DataService dataService = DataService.getInstance();
    private int currentPage = 1;
    private final int itemsPerPage = 15;

    // Currency formatter
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-PH"));

    /**
     * Initialize the Sales Controller
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("🏗️ Initializing Sales Controller...");
        
        try {
            // Register with HeaderController for real-time updates
            HeaderController.setSalesController(this);
            
            // Initialize data service and load sales data
            loadSalesData();
            
            // Setup UI components in correct order
            setupFilters();    // First setup filters
            setupTable();      // Then setup table
            setupPagination(); // Then setup pagination
            setupKPICards();   // Finally setup KPI cards
            
            // Initial data display
            updateKPICards();
            
            // Setup filter event handlers
            setupEventHandlers();
            
            logger.info("✅ Sales Controller initialized successfully with " + allSales.size() + " sales records");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during Sales Controller initialization", e);
            showErrorState();
        }
    }

    /**
     * Load sales data from the data service
     */
    private void loadSalesData() {
        logger.info("📂 Loading sales data from data service...");
        
        try {
            if (dataService.loadSalesData()) {
                allSales = dataService.getAllSales();
                logger.info("✅ Loaded " + allSales.size() + " sales records successfully");
            } else {
                logger.warning("⚠️ Failed to load sales data");
                allSales = new ArrayList<>();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error loading sales data", e);
            allSales = new ArrayList<>();
        }
    }

    /**
     * Setup KPI cards with default values
     */
    private void setupKPICards() {
        logger.info("🔧 Setting up KPI cards...");
        
        // Initialize KPI cards with default values
        if (todaySalesAmount != null) {
            todaySalesAmount.setText("₱0.00");
            todaySalesDesc.setText("From completed transactions");
        }
        
        if (transactionsCount != null) {
            transactionsCount.setText("0");
            transactionsDesc.setText("Completed sales today");
        }
        
        if (averageTicketAmount != null) {
            averageTicketAmount.setText("₱0.00");
            averageTicketDesc.setText("Per transaction value");
        }
        
        if (digitalPaymentRatio != null) {
            digitalPaymentRatio.setText("0%");
            digitalPaymentDesc.setText("GCash, Card, Online");
        }
        
        logger.info("✅ KPI cards setup completed");
    }

    /**
     * Setup the sales table
     */
    private void setupTable() {
        logger.info("🔧 Setting up sales table...");
        
        try {
            // Bind table columns to SalesRecord properties
            if (salesTable != null) {
                logger.info("📊 Table found in FXML, setting up columns...");
                
                // Initialize the filtered sales list
                filteredSales = FXCollections.observableArrayList();
                salesTable.setItems(FXCollections.observableArrayList());
                
                // Set table selection mode to single selection
                salesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                
                // Ensure table uses constrained resize policy
                salesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                
                // Setup custom cell factory for payment details column
                TableColumn<SalesRecord, String> paymentDetailsColumn = 
                    (TableColumn<SalesRecord, String>) salesTable.getColumns().stream()
                        .filter(col -> col.getId().equals("paymentDetailsColumn"))
                        .findFirst()
                        .orElse(null);
                
                if (paymentDetailsColumn != null) {
                    paymentDetailsColumn.setCellFactory(column -> new TableCell<SalesRecord, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            
                            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                                setText(null);
                                return;
                            }
                            
                            SalesRecord sale = (SalesRecord) getTableRow().getItem();
                            String paymentMethod = sale.getPaymentMethod();
                            
                            if ("Cash".equals(paymentMethod)) {
                                // For cash payments, show the cash received amount
                                setText("₱" + sale.getCashReceived());
                            } else {
                                // For GCash/Maya, show the reference number
                                setText(sale.getPaymentReference());
                            }
                        }
                    });
                }
                
                // Setup actions column with view receipt button
                TableColumn<SalesRecord, Void> actionsColumn = 
                    (TableColumn<SalesRecord, Void>) salesTable.getColumns().stream()
                        .filter(col -> col.getId().equals("actionsColumn"))
                        .findFirst()
                        .orElse(null);
                
                if (actionsColumn != null) {
                    actionsColumn.setCellFactory(column -> new TableCell<SalesRecord, Void>() {
                        private final Button viewReceiptBtn = new Button("View Receipt");
                        
                        {
                            // Style the button to match overview page
                            viewReceiptBtn.setStyle("-fx-background-color: #fef7da; -fx-border-color: #fdf1bb; " +
                                                  "-fx-cursor: hand; -fx-font-family: 'Inter Regular'; " +
                                                  "-fx-font-size: 12px;");
                            
                            viewReceiptBtn.setOnAction(event -> {
                                SalesRecord sale = getTableView().getItems().get(getIndex());
                                showReceipt(sale);
                            });
                        }
                        
                        @Override
                        protected void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(viewReceiptBtn);
                            }
                        }
                    });
                }
                
                // Update filtered sales list with all sales
                filteredSales.addAll(allSales);
                
                // Initial update of table display
                updateTableDisplay();
                
                logger.info("✅ Table setup completed with " + filteredSales.size() + " records");
            } else {
                logger.warning("⚠️ Sales table not found in FXML");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error setting up sales table", e);
            e.printStackTrace();
        }
    }

    /**
     * Setup filter controls
     */
    private void setupFilters() {
        logger.info("🔧 Setting up filter controls...");
        
        // Payment method filter
        if (paymentMethodFilter != null) {
            paymentMethodFilter.setItems(FXCollections.observableArrayList(
                "All Methods", "Cash", "GCash", "Maya", "Card"
            ));
            paymentMethodFilter.setValue("All Methods");
        }
        
        // Sort order filter
        if (sortOrderCombo != null) {
            sortOrderCombo.setItems(FXCollections.observableArrayList(
                "Newest First", "Oldest First", "Amount High to Low", "Amount Low to High"
            ));
            sortOrderCombo.setValue("Newest First");
        }
        
        logger.info("✅ Filter controls setup completed");
    }

    /**
     * Setup pagination controls
     */
    private void setupPagination() {
        logger.info("🔧 Setting up pagination controls...");
        
        if (prevPageBtn != null) {
            prevPageBtn.setOnAction(e -> {
                logger.info("⬅️ Previous button clicked - current page: " + currentPage);
                try {
                    if (currentPage > 1) {
                        currentPage--;
                        logger.info("✅ Moving to previous page: " + currentPage);
                        updateTableDisplay();
                    } else {
                        logger.info("⚠️ Already on first page, cannot go previous");
                    }
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "❌ Error handling previous button click", ex);
                }
            });
            
            // Set initial state
            prevPageBtn.setDisable(true);
        } else {
            logger.warning("⚠️ Previous button not found in FXML");
        }
        
        if (nextPageBtn != null) {
            nextPageBtn.setOnAction(e -> {
                logger.info("➡️ Next button clicked - current page: " + currentPage);
                try {
                    int totalPages = (int) Math.ceil((double) filteredSales.size() / itemsPerPage);
                    if (currentPage < totalPages) {
                        currentPage++;
                        logger.info("✅ Moving to next page: " + currentPage);
                        updateTableDisplay();
                    } else {
                        logger.info("⚠️ Already on last page, cannot go next");
                    }
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "❌ Error handling next button click", ex);
                }
            });
        } else {
            logger.warning("⚠️ Next button not found in FXML");
        }
        
        logger.info("✅ Pagination controls setup completed");
    }

    /**
     * Update KPI cards with real-time sales data
     */
    private void updateKPICards() {
        logger.info("📊 Updating sales KPI cards...");
        
        try {
            LocalDate today = LocalDate.now();
            
            // Filter today's sales
            List<SalesRecord> todaysSales = allSales.stream()
                .filter(sale -> isSaleFromToday(sale, today))
                .collect(Collectors.toList());
            
            // Calculate metrics
            double todaysTotalSales = todaysSales.stream()
                .mapToDouble(this::parseSaleAmount)
                .sum();
            
            int todaysTransactionCount = todaysSales.size();
            double averageTicket = todaysTransactionCount > 0 ? todaysTotalSales / todaysTransactionCount : 0.0;
            
            // Calculate payment method distribution
            long digitalPayments = todaysSales.stream()
                .filter(sale -> !"Cash".equals(sale.getPaymentMethod()))
                .count();
            
            double digitalRatio = todaysTransactionCount > 0 ? (digitalPayments * 100.0) / todaysTransactionCount : 0.0;
            
            // Update KPI cards
            Platform.runLater(() -> {
                if (todaySalesAmount != null) {
                    todaySalesAmount.setText(String.format("₱%,.2f", todaysTotalSales));
                }
                
                if (transactionsCount != null) {
                    transactionsCount.setText(String.valueOf(todaysTransactionCount));
                }
                
                if (averageTicketAmount != null) {
                    averageTicketAmount.setText(String.format("₱%,.2f", averageTicket));
                }
                
                if (digitalPaymentRatio != null) {
                    digitalPaymentRatio.setText(String.format("%.1f%%", digitalRatio));
                }
            });
            
            logger.info("✅ KPI cards updated successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error updating KPI cards", e);
        }
    }

    /**
     * Update table display with pagination
     */
    private void updateTableDisplay() {
        if (salesTable == null) return;
        
        Platform.runLater(() -> {
            try {
                // Create a new filtered list from all sales
                List<SalesRecord> tempFiltered = new ArrayList<>(allSales);
                
                // Apply filters if set
                if (paymentMethodFilter != null && paymentMethodFilter.getValue() != null && 
                    !paymentMethodFilter.getValue().equals("All Methods")) {
                    String method = paymentMethodFilter.getValue();
                    tempFiltered = tempFiltered.stream()
                        .filter(sale -> sale.getPaymentMethod().equals(method))
                        .collect(Collectors.toList());
                }
                
                // Apply date range filter
                if (fromDatePicker != null && fromDatePicker.getValue() != null) {
                    LocalDate fromDate = fromDatePicker.getValue();
                    tempFiltered = tempFiltered.stream()
                        .filter(sale -> {
                            LocalDate saleDate = parseSaleDate(sale.getSaleDateTime());
                            return saleDate != null && !saleDate.isBefore(fromDate);
                        })
                        .collect(Collectors.toList());
                }
                
                if (toDatePicker != null && toDatePicker.getValue() != null) {
                    LocalDate toDate = toDatePicker.getValue();
                    tempFiltered = tempFiltered.stream()
                        .filter(sale -> {
                            LocalDate saleDate = parseSaleDate(sale.getSaleDateTime());
                            return saleDate != null && !saleDate.isAfter(toDate);
                        })
                        .collect(Collectors.toList());
                }
                
                // Apply search filter
                if (searchField != null && searchField.getText() != null && !searchField.getText().trim().isEmpty()) {
                    String searchTerm = searchField.getText().toLowerCase().trim();
                    tempFiltered = tempFiltered.stream()
                        .filter(sale -> 
                            sale.getSaleId().toLowerCase().contains(searchTerm) ||
                            sale.getOrderId().toLowerCase().contains(searchTerm) ||
                            sale.getCustomerName().toLowerCase().contains(searchTerm) ||
                            sale.getContactNumber().contains(searchTerm)
                        )
                        .collect(Collectors.toList());
                }
                
                // Apply sorting
                if (sortOrderCombo != null && sortOrderCombo.getValue() != null) {
                    String sortOrder = sortOrderCombo.getValue();
                    switch (sortOrder) {
                        case "Newest First":
                            tempFiltered.sort((a, b) -> {
                                // Parse dates for proper comparison
                                LocalDateTime dateA = parseSaleDateTime(a.getSaleDateTime());
                                LocalDateTime dateB = parseSaleDateTime(b.getSaleDateTime());
                                if (dateA != null && dateB != null) {
                                    return dateB.compareTo(dateA); // Newest first
                                }
                                // Fallback to string comparison if parsing fails
                                return b.getSaleDateTime().compareTo(a.getSaleDateTime());
                            });
                            break;
                        case "Oldest First":
                            tempFiltered.sort((a, b) -> {
                                // Parse dates for proper comparison
                                LocalDateTime dateA = parseSaleDateTime(a.getSaleDateTime());
                                LocalDateTime dateB = parseSaleDateTime(b.getSaleDateTime());
                                if (dateA != null && dateB != null) {
                                    return dateA.compareTo(dateB); // Oldest first
                                }
                                // Fallback to string comparison if parsing fails
                                return a.getSaleDateTime().compareTo(b.getSaleDateTime());
                            });
                            break;
                        case "Amount High to Low":
                            tempFiltered.sort((a, b) -> {
                                double amountA = parseSaleAmount(a);
                                double amountB = parseSaleAmount(b);
                                return Double.compare(amountB, amountA);
                            });
                            break;
                        case "Amount Low to High":
                            tempFiltered.sort((a, b) -> {
                                double amountA = parseSaleAmount(a);
                                double amountB = parseSaleAmount(b);
                                return Double.compare(amountA, amountB);
                            });
                            break;
                    }
                }
                
                // Calculate pagination
                int totalItems = tempFiltered.size();
                int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / itemsPerPage));
                
                // Ensure current page is within bounds
                if (currentPage > totalPages) {
                    currentPage = totalPages;
                }
                
                int startIndex = (currentPage - 1) * itemsPerPage;
                int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
                
                // Get items for current page
                List<SalesRecord> pageItems = tempFiltered.subList(startIndex, endIndex);
                
                // Update table items
                salesTable.getItems().clear();
                salesTable.getItems().addAll(pageItems);
                
                // Update pagination controls
                if (pageLabel != null) {
                    pageLabel.setText(String.format("Page %d of %d", currentPage, totalPages));
                }
                
                if (resultsInfoLabel != null) {
                    resultsInfoLabel.setText(String.format("Showing %d to %d of %d sales", 
                        totalItems > 0 ? startIndex + 1 : 0, endIndex, totalItems));
                }
                
                // Update pagination button states with proper null checks and logging
                if (prevPageBtn != null) {
                    boolean shouldDisablePrev = currentPage <= 1;
                    prevPageBtn.setDisable(shouldDisablePrev);
                    logger.info("🔄 Previous button state: " + (shouldDisablePrev ? "disabled" : "enabled") + " (page " + currentPage + ")");
                } else {
                    logger.warning("⚠️ Previous button is null during table update");
                }
                
                if (nextPageBtn != null) {
                    boolean shouldDisableNext = currentPage >= totalPages;
                    nextPageBtn.setDisable(shouldDisableNext);
                    logger.info("🔄 Next button state: " + (shouldDisableNext ? "disabled" : "enabled") + " (page " + currentPage + " of " + totalPages + ")");
                } else {
                    logger.warning("⚠️ Next button is null during table update");
                }
                
                // Update filtered sales list for other operations
                filteredSales.clear();
                filteredSales.addAll(tempFiltered);
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "❌ Error updating table display", e);
            }
        });
    }

    // Helper methods

    /**
     * Check if a sale is from today
     */
    private boolean isSaleFromToday(SalesRecord sale, LocalDate targetDate) {
        try {
            LocalDate saleDate = parseSaleDate(sale.getSaleDateTime());
            return saleDate != null && saleDate.equals(targetDate);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parse sale date from string format
     */
    private LocalDate parseSaleDate(String dateTimeStr) {
        try {
            if (dateTimeStr != null && dateTimeStr.contains(",")) {
                String[] parts = dateTimeStr.split(",");
                if (parts.length >= 2) {
                    String datePart = parts[0].trim() + ", " + parts[1].trim().split(" ")[0];
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
                    return LocalDate.parse(datePart, formatter);
                }
            }
        } catch (DateTimeParseException e) {
            logger.log(Level.WARNING, "Failed to parse sale date: " + dateTimeStr, e);
        }
        return null;
    }

    /**
     * Parse sale date-time from string format for sorting
     */
    private LocalDateTime parseSaleDateTime(String dateTimeStr) {
        try {
            if (dateTimeStr != null && dateTimeStr.contains(",")) {
                // Format: "MMM d, yyyy h:mm a" (e.g., "Jul 6, 2025 2:45 PM")
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
                return LocalDateTime.parse(dateTimeStr, formatter);
            }
        } catch (DateTimeParseException e) {
            logger.log(Level.WARNING, "Failed to parse sale date-time: " + dateTimeStr, e);
        }
        return null;
    }

    /**
     * Parse sale amount from string
     */
    private double parseSaleAmount(SalesRecord sale) {
        try {
            String amountStr = sale.getSaleAmount().replace("₱", "").replace(",", "").trim();
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Failed to parse sale amount: " + sale.getSaleAmount(), e);
            return 0.0;
        }
    }

    /**
     * Show error state when initialization fails
     */
    private void showErrorState() {
        Platform.runLater(() -> {
            logger.warning("⚠️ Showing error state for sales page");
            
            if (todaySalesAmount != null) {
                todaySalesAmount.setText("Error");
                todaySalesDesc.setText("Failed to load sales data");
            }
        });
    }

    /**
     * Show receipt dialog for a sale
     * @param sale The sale record to show receipt for
     */
    private void showReceipt(SalesRecord sale) {
        logger.info("📄 Opening receipt preview for sale: " + sale.getSaleId());
        
        try {
            // Create new stage for receipt preview
            Stage receiptStage = new Stage();
            receiptStage.initModality(Modality.APPLICATION_MODAL);
            receiptStage.setTitle("Receipt Preview - Sale " + sale.getSaleId());
            receiptStage.setResizable(true);
            
            // Create main container
            VBox mainContainer = new VBox(15);
            mainContainer.setAlignment(Pos.CENTER);
            mainContainer.setPadding(new Insets(20));
            mainContainer.setStyle("-fx-background-color: #FFFAEC;");
            
            // Create loading indicator initially
            ProgressIndicator loadingIndicator = new ProgressIndicator();
            loadingIndicator.setMaxSize(50, 50);
            
            VBox contentContainer = new VBox(10);
            contentContainer.setAlignment(Pos.CENTER);
            contentContainer.getChildren().add(loadingIndicator);
            
            // Create button bar (disabled initially)
            HBox buttonBar = new HBox(15);
            buttonBar.setAlignment(Pos.CENTER);
            buttonBar.setPadding(new Insets(10, 0, 0, 0));
            
            Button exportButton = new Button("Export");
            exportButton.setStyle("-fx-background-color: #d97708; -fx-text-fill: white; " +
                                "-fx-font-size: 14px; -fx-padding: 12px 24px; " +
                                "-fx-border-radius: 6px; -fx-background-radius: 6px; " +
                                "-fx-font-weight: bold; -fx-cursor: hand;");
            exportButton.setDisable(true); // Initially disabled
            
            Button closeButton = new Button("Close");
            closeButton.setStyle("-fx-background-color: #6B7280; -fx-text-fill: white; " +
                                "-fx-font-size: 14px; -fx-padding: 12px 24px; " +
                                "-fx-border-radius: 6px; -fx-background-radius: 6px; " +
                                "-fx-cursor: hand;");
            closeButton.setOnAction(e -> receiptStage.close());
            
            buttonBar.getChildren().addAll(exportButton, closeButton);
            
            // Info label
            Label infoLabel = new Label("This is your receipt preview. Click 'Export' to save as PDF to Downloads folder.");
            infoLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13px; -fx-font-style: italic;");
            infoLabel.setAlignment(Pos.CENTER);
            infoLabel.setWrapText(true);
            
            // Add components to main container
            mainContainer.getChildren().addAll(contentContainer, infoLabel, buttonBar);
            
            // Create and show scene
            Scene scene = new Scene(mainContainer, 400, 600);
            receiptStage.setScene(scene);
            receiptStage.centerOnScreen();
            receiptStage.show();
            
            // Generate PDF image in background thread
            new Thread(() -> {
                try {
                    // Generate PDF document
                    PDDocument document = generatePDFInMemory(sale);
                    if (document != null) {
                        // Convert first page to image
                        PDFRenderer pdfRenderer = new PDFRenderer(document);
                        BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300); // 300 DPI for high quality
                        
                        // Convert to JavaFX image
                        WritableImage fxImage = SwingFXUtils.toFXImage(image, null);
                        
                        // Update UI in JavaFX thread
                        Platform.runLater(() -> {
                            try {
                                // Create image view with the actual PDF image
                                ImageView imageView = new ImageView(fxImage);
                                imageView.setPreserveRatio(true);
                                
                                // Calculate window size based on image dimensions
                                double imageWidth = fxImage.getWidth();
                                double imageHeight = fxImage.getHeight();
                                
                                // Set reasonable window size (max 85% of screen)
                                double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
                                double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();
                                
                                double maxWindowWidth = screenWidth * 0.6; // Smaller than fullscreen viewer
                                double maxWindowHeight = screenHeight * 0.8;
                                
                                // Calculate scale to fit the image properly in window
                                double scaleX = maxWindowWidth / imageWidth;
                                double scaleY = maxWindowHeight / imageHeight;
                                double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio
                                
                                // Calculate final dimensions
                                double finalImageWidth = imageWidth * scale;
                                double finalImageHeight = imageHeight * scale;
                                
                                // Set the image size
                                imageView.setFitWidth(finalImageWidth);
                                imageView.setFitHeight(finalImageHeight);
                                
                                // Create scroll pane for the image
                                ScrollPane scrollPane = new ScrollPane();
                                scrollPane.setContent(imageView);
                                scrollPane.setFitToWidth(true);
                                scrollPane.setFitToHeight(true);
                                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                                scrollPane.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-width: 1px;");
                                scrollPane.setPrefSize(finalImageWidth + 20, finalImageHeight + 20);
                                
                                // Clear loading and add image
                                contentContainer.getChildren().clear();
                                contentContainer.getChildren().add(scrollPane);
                                
                                // Enable export button
                                exportButton.setDisable(false);
                                exportButton.setOnAction(e -> {
                                    generatePDFReceipt(sale);
                                    receiptStage.close();
                                });
                                
                                // Adjust window size to fit content
                                double windowWidth = Math.min(finalImageWidth + 60, maxWindowWidth);
                                double windowHeight = Math.min(finalImageHeight + 140, maxWindowHeight); // Extra space for buttons and info
                                
                                receiptStage.setWidth(windowWidth);
                                receiptStage.setHeight(windowHeight);
                                receiptStage.centerOnScreen();
                                
                                logger.info("✅ Receipt PDF image preview loaded successfully for sale: " + sale.getSaleId());
                                
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, "❌ Error displaying PDF image in receipt preview", e);
                                Platform.runLater(() -> showReceiptPreviewError(contentContainer));
                            }
                        });
                        
                    } else {
                        Platform.runLater(() -> showReceiptPreviewError(contentContainer));
                    }
                    
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "❌ Error generating receipt preview", e);
                    Platform.runLater(() -> showReceiptPreviewError(contentContainer));
                }
            }).start();
            
            logger.info("✅ Receipt preview window opened successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error showing receipt", e);
            e.printStackTrace();
        }
    }
    
    /**
     * Show error message when receipt preview fails
     */
    private void showReceiptPreviewError(VBox container) {
        container.getChildren().clear();
        Label errorLabel = new Label("Error loading receipt preview");
        errorLabel.setStyle("-fx-text-fill: red;");
        container.getChildren().add(errorLabel);
    }
    
    /**
     * Generate PDF receipt and save to downloads folder
     */
    private void generatePDFReceipt(SalesRecord sale) {
        try {
            PDDocument document = generatePDFInMemory(sale);
            if (document != null) {
                String downloadsPath = System.getProperty("user.home") + "/Downloads";
                String fileName = String.format("receipt_%s_%s.pdf", 
                    sale.getSaleId().toLowerCase(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                );
                String filePath = downloadsPath + "/" + fileName;
                
                document.save(filePath);
                document.close();
                
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Receipt Exported");
                    alert.setHeaderText(null);
                    alert.setContentText("Receipt has been saved to your Downloads folder.");
                    alert.showAndWait();
                });
                
                logger.info("✅ Receipt PDF saved successfully: " + filePath);
                
            } else {
                throw new Exception("Failed to generate PDF document");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error saving receipt PDF", e);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Failed");
                alert.setHeaderText(null);
                alert.setContentText("Failed to export receipt. Please try again.");
                alert.showAndWait();
            });
        }
    }
    
    /**
     * Generate PDF document in memory
     */
    private PDDocument generatePDFInMemory(SalesRecord sale) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Load fonts
            PDType0Font regularFont = PDType0Font.load(document, getClass().getResourceAsStream("/fonts/Inter-Regular.ttf"));
            PDType0Font boldFont = PDType0Font.load(document, getClass().getResourceAsStream("/fonts/Inter-Bold.ttf"));
            PDType0Font mediumFont = PDType0Font.load(document, getClass().getResourceAsStream("/fonts/Inter-Medium.ttf"));
            
            // Set initial position
            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float xStart = margin;
            float yPosition = yStart;
            
            // Add logo
            try (InputStream logoStream = getClass().getResourceAsStream("/images/wonderpoffles_logo.png")) {
                if (logoStream != null) {
                    BufferedImage logoImage = ImageIO.read(logoStream);
                    PDImageXObject logo = LosslessFactory.createFromImage(document, logoImage);
                    float logoWidth = 120;
                    float logoHeight = logoWidth * logo.getHeight() / logo.getWidth();
                    contentStream.drawImage(logo, xStart, yPosition - logoHeight, logoWidth, logoHeight);
                }
            }
            
            // Add header text
            yPosition -= 80;
            contentStream.beginText();
            contentStream.setFont(boldFont, 24);
            contentStream.newLineAtOffset(xStart, yPosition);
            contentStream.showText("SALES RECEIPT");
            contentStream.endText();
            
            // Add receipt details
            yPosition -= 40;
            addText(contentStream, "Sale ID: " + sale.getSaleId(), regularFont, 11, xStart, yPosition);
            yPosition -= 20;
            addText(contentStream, "Order ID: " + sale.getOrderId(), regularFont, 11, xStart, yPosition);
            yPosition -= 20;
            addText(contentStream, "Date: " + sale.getSaleDateTime(), regularFont, 11, xStart, yPosition);
            
            // Add customer info
            yPosition -= 40;
            addText(contentStream, "Customer Information", boldFont, 14, xStart, yPosition);
            yPosition -= 25;
            addText(contentStream, "Name: " + sale.getCustomerName(), regularFont, 11, xStart, yPosition);
            yPosition -= 20;
            addText(contentStream, "Contact: " + sale.getContactNumber(), regularFont, 11, xStart, yPosition);
            
            // Add items section
            yPosition -= 40;
            addText(contentStream, "Items Purchased", boldFont, 14, xStart, yPosition);
            yPosition -= 25;
            
            // Parse and add items
            String[] items = sale.getItemsSold().split(";");
            for (String item : items) {
                addText(contentStream, "• " + item.trim(), regularFont, 11, xStart, yPosition);
                yPosition -= 20;
            }
            
            // Add payment details
            yPosition -= 20;
            addText(contentStream, "Payment Details", boldFont, 14, xStart, yPosition);
            yPosition -= 25;
            addText(contentStream, "Payment Method: " + sale.getPaymentMethod(), regularFont, 11, xStart, yPosition);
            yPosition -= 20;
            
            if ("Cash".equals(sale.getPaymentMethod())) {
                addText(contentStream, "Amount Received: ₱" + sale.getCashReceived(), regularFont, 11, xStart, yPosition);
                yPosition -= 20;
                double change = Double.parseDouble(sale.getCashReceived()) - 
                              Double.parseDouble(sale.getSaleAmount().replace("₱", "").replace(",", ""));
                addText(contentStream, "Change: ₱" + String.format("%.2f", change), regularFont, 11, xStart, yPosition);
            } else {
                addText(contentStream, "Reference Number: " + sale.getPaymentReference(), regularFont, 11, xStart, yPosition);
            }
            
            // Add total amount
            yPosition -= 40;
            addText(contentStream, "Total Amount: " + sale.getSaleAmount(), boldFont, 16, xStart, yPosition);
            
            // Add footer
            yPosition -= 60;
            addText(contentStream, "Thank you for your purchase!", mediumFont, 12, xStart, yPosition);
            yPosition -= 20;
            addText(contentStream, "Please come again!", regularFont, 11, xStart, yPosition);
            
            contentStream.close();
            return document;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error generating PDF in memory", e);
            return null;
        }
    }
    
    /**
     * Helper method to add text to PDF
     */
    private void addText(PDPageContentStream contentStream, String text, PDFont font, float fontSize, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    /**
     * Reset pagination to first page and update display
     */
    private void resetPagination() {
        logger.info("🔄 Resetting pagination to first page");
        currentPage = 1;
        updateTableDisplay();
    }

    /**
     * Setup event handlers for filters and controls
     */
    private void setupEventHandlers() {
        if (paymentMethodFilter != null) {
            paymentMethodFilter.setOnAction(e -> {
                logger.info("🔍 Payment method filter changed to: " + paymentMethodFilter.getValue());
                resetPagination();
            });
        }
        
        if (fromDatePicker != null) {
            fromDatePicker.setOnAction(e -> {
                logger.info("📅 From date filter changed to: " + fromDatePicker.getValue());
                resetPagination();
            });
        }
        
        if (toDatePicker != null) {
            toDatePicker.setOnAction(e -> {
                logger.info("📅 To date filter changed to: " + toDatePicker.getValue());
                resetPagination();
            });
        }
        
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                logger.info("🔍 Search filter changed to: " + newValue);
                resetPagination();
            });
        }
        
        if (sortOrderCombo != null) {
            sortOrderCombo.setOnAction(e -> {
                logger.info("📊 Sort order changed to: " + sortOrderCombo.getValue());
                resetPagination();
            });
        }
        
        if (clearFiltersButton != null) {
            clearFiltersButton.setOnAction(e -> {
                logger.info("🧹 Clearing all filters");
                // Reset all filters
                if (paymentMethodFilter != null) paymentMethodFilter.setValue("All Methods");
                if (fromDatePicker != null) fromDatePicker.setValue(null);
                if (toDatePicker != null) toDatePicker.setValue(null);
                if (searchField != null) searchField.clear();
                if (sortOrderCombo != null) sortOrderCombo.setValue("Newest First");
                resetPagination();
            });
        }
    }

    /**
     * Force refresh pagination state - useful when buttons become unresponsive
     */
    private void forceRefreshPagination() {
        logger.info("🔄 Force refreshing pagination state");
        
        try {
            // Recalculate total pages
            int totalItems = filteredSales.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / itemsPerPage));
            
            // Ensure current page is within bounds
            if (currentPage > totalPages) {
                currentPage = totalPages;
                logger.info("⚠️ Current page was out of bounds, reset to: " + currentPage);
            }
            
            // Force update button states
            if (prevPageBtn != null) {
                prevPageBtn.setDisable(currentPage <= 1);
                logger.info("🔄 Previous button refreshed - disabled: " + (currentPage <= 1));
            }
            
            if (nextPageBtn != null) {
                nextPageBtn.setDisable(currentPage >= totalPages);
                logger.info("🔄 Next button refreshed - disabled: " + (currentPage >= totalPages));
            }
            
            if (pageLabel != null) {
                pageLabel.setText(String.format("Page %d of %d", currentPage, totalPages));
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error force refreshing pagination", e);
        }
    }

    /**
     * Refresh the sales data and update the display
     */
    public void refreshSalesData() {
        logger.info("🔄 Refreshing sales data...");
        
        try {
            // Reload data from files
            loadSalesData();
            
            // Reset pagination to first page
            currentPage = 1;
            
            // Update UI
            updateKPICards();
            updateTableDisplay();
            
            // Force refresh pagination state
            forceRefreshPagination();
            
            logger.info("✅ Sales data refreshed successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error refreshing sales data", e);
            showErrorState();
        }
    }
}