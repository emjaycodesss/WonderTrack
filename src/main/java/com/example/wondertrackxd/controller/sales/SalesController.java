package com.example.wondertrackxd.controller.sales;

import com.example.wondertrackxd.controller.analytics.DataService;
import com.example.wondertrackxd.controller.model.SalesRecord;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
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
    private DataService dataService = new DataService();
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
            // Initialize data service and load sales data
            loadSalesData();
            
            // Setup UI components
            setupKPICards();
            setupTable();
            setupFilters();
            setupPagination();
            
            // Initial data display
            updateKPICards();
            updateTableDisplay();
            
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
        
        // Create table columns programmatically to avoid FXML property issues
        TableColumn<SalesRecord, String> saleIdCol = new TableColumn<>("Sale ID");
        saleIdCol.setCellValueFactory(new PropertyValueFactory<>("saleId"));
        saleIdCol.setPrefWidth(120);
        
        TableColumn<SalesRecord, String> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderIdCol.setPrefWidth(120);
        
        TableColumn<SalesRecord, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerCol.setPrefWidth(150);
        
        TableColumn<SalesRecord, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        contactCol.setPrefWidth(120);
        
        TableColumn<SalesRecord, String> itemsCol = new TableColumn<>("Items Sold");
        itemsCol.setCellValueFactory(new PropertyValueFactory<>("itemsSold"));
        itemsCol.setPrefWidth(200);
        
        TableColumn<SalesRecord, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("saleAmount"));
        amountCol.setPrefWidth(100);
        
        TableColumn<SalesRecord, String> paymentCol = new TableColumn<>("Payment");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentCol.setPrefWidth(100);
        
        TableColumn<SalesRecord, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("saleDateTime"));
        dateCol.setPrefWidth(150);
        
        // Add columns to table
        salesTable.getColumns().clear();
        salesTable.getColumns().addAll(saleIdCol, orderIdCol, customerCol, contactCol, 
                                      itemsCol, amountCol, paymentCol, dateCol);
        
        // Set table data
        salesTable.setItems(filteredSales);
        
        logger.info("✅ Sales table setup completed");
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
        if (prevPageBtn != null) {
            prevPageBtn.setOnAction(e -> {
                if (currentPage > 1) {
                    currentPage--;
                    updateTableDisplay();
                }
            });
        }
        
        if (nextPageBtn != null) {
            nextPageBtn.setOnAction(e -> {
                int totalPages = (int) Math.ceil((double) filteredSales.size() / itemsPerPage);
                if (currentPage < totalPages) {
                    currentPage++;
                    updateTableDisplay();
                }
            });
        }
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
        
        // Apply basic filtering if any filters are set
        filteredSales.clear();
        filteredSales.addAll(allSales);
        
        // Calculate pagination
        int totalItems = filteredSales.size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
        
        // Get items for current page
        List<SalesRecord> pageItems = filteredSales.subList(startIndex, endIndex);
        
        // Update table
        salesTable.getItems().clear();
        salesTable.getItems().addAll(pageItems);
        
        // Update pagination controls
        if (pageLabel != null) {
            pageLabel.setText("Page " + currentPage + " of " + Math.max(1, totalPages));
        }
        
        if (resultsInfoLabel != null) {
            resultsInfoLabel.setText("Showing " + Math.max(0, totalItems > 0 ? startIndex + 1 : 0) + 
                                  " to " + endIndex + " of " + totalItems + " sales");
        }
        
        if (prevPageBtn != null) {
            prevPageBtn.setDisable(currentPage <= 1);
        }
        
        if (nextPageBtn != null) {
            nextPageBtn.setDisable(currentPage >= totalPages);
        }
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
} 