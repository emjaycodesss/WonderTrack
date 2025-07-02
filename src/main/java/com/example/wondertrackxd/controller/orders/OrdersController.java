package com.example.wondertrackxd.controller.orders;

import com.example.wondertrackxd.controller.model.RecentOrder;
import com.example.wondertrackxd.controller.model.WaffleCategory;
import com.example.wondertrackxd.controller.model.WaffleFlavor;
import com.example.wondertrackxd.controller.model.OrderItem;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.Locale;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.Color;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.WritableImage;
import javafx.application.Platform;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.Objects;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * OrdersController manages the complete order workflow including form, table, and filtering
 * Now includes real-time PDF preview generation and rendering as images
 */
public class OrdersController {

    private static final Logger logger = Logger.getLogger(OrdersController.class.getName());

    // Form Controls
    @FXML private TextField customerNameField;
    @FXML private TextField contactNumberField;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private VBox digitalPaymentContainer;
    @FXML private TextField referenceNumberField;
    @FXML private TextField timestampField;
    @FXML private VBox cashPaymentContainer;
    @FXML private TextField cashReceivedField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> flavorCombo;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private TextField priceField;
    @FXML private Button addItemButton;
    @FXML private Button clearButton;
    @FXML private Button createOrderButton;
    @FXML private Label totalLabel;
    @FXML private VBox itemInputContainer;
    
    // Container references for responsive layout
    @FXML private VBox orderFormContainer;
    @FXML private VBox receiptPreviewContainer;

    // Invoice Preview - now using ImageView for PDF rendering
    @FXML private VBox invoicePreviewContent;
    @FXML private ScrollPane invoiceScrollPane;
    @FXML private HBox invoiceControlButtons;
    @FXML private Button zoomInButton;
    @FXML private Button zoomOutButton;
    @FXML private Button pageViewButton;
    @FXML private Button viewFullImageButton;
    @FXML private Label previewPlaceholderLabel;

    // Zoom and pan variables for invoice preview
    private double currentZoomLevel = 1.0;
    private double originalImageWidth;
    private double originalImageHeight;
    private ImageView currentPreviewImageView;
    private boolean isPanning = false;
    private double lastPanX, lastPanY;

    // Table and Filters
    @FXML private TableView<RecentOrder> ordersTable;
    @FXML private TableColumn<RecentOrder, String> orderIdColumn;
    @FXML private TableColumn<RecentOrder, String> nameColumn;
    @FXML private TableColumn<RecentOrder, String> contactNumberColumn;
    @FXML private TableColumn<RecentOrder, String> totalAmountColumn;
    @FXML private TableColumn<RecentOrder, String> dateTimeColumn;
    @FXML private TableColumn<RecentOrder, String> orderStatusColumn;
    @FXML private TableColumn<RecentOrder, Void> actionsColumn;

    @FXML private ComboBox<String> orderStatusFilter;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortFilterCombo;
    @FXML private Button clearFiltersButton;
    
    // Pagination
    @FXML private Button prevPageBtn;
    @FXML private Button nextPageBtn;
    @FXML private Label pageLabel;
    @FXML private Label resultsInfoLabel;
    
    // Data
    private List<RecentOrder> allOrders = new ArrayList<>();
    private ObservableList<RecentOrder> filteredOrders = FXCollections.observableArrayList();
    private List<DynamicOrderItem> currentDynamicOrderItems = new ArrayList<>();
    private int totalQuantity = 0;
    private int currentPage = 1;
    private final int itemsPerPage = 10;

    private static final String RECEIPT_SEPARATOR = "--------------------------------------";
    
    // Dynamic product data loaded from files
    private Map<String, List<ProductData>> categoryToFlavors = new HashMap<>();
    private List<String> availableCategories = new ArrayList<>();
    
    // Filter debouncing
    private PauseTransition searchDebouncer;
    private final AtomicReference<String> lastSearchText = new AtomicReference<>("");
    private volatile boolean isFilteringInProgress = false;
    
    // Product data structure to hold flavor information from txt files
    public static class ProductData {
        private final String category;
        private final String name;
        private final String description;
        private final String price;
        
        public ProductData(String category, String name, String description, String price) {
            this.category = category;
            this.name = name;
            this.description = description;
            this.price = price;
        }
        
        public String getCategory() { return category; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getPrice() { return price; }
        
        public double getPriceValue() {
            try {
                return Double.parseDouble(price.replace("₱", "").replace(",", "").trim());
            } catch (NumberFormatException e) {
                return 45.0; // Default price
            }
        }
        
        public String getFormattedPrice() {
            return String.format("₱%.2f", getPriceValue());
        }
    }

    @FXML
    public void initialize() {
        logger.info("🔧 Initializing Orders Controller with enhanced scroll support...");
        loadDynamicProductData(); // Load categories and flavors from txt files
        
        // Register for product data change notifications
        try {
            Class<?> productMgmtClass = Class.forName("com.example.wondertrackxd.controller.products.ProductManagementController");
            java.lang.reflect.Method registerMethod = productMgmtClass.getMethod("registerDataChangeCallback", Runnable.class);
            registerMethod.invoke(null, (Runnable) this::refreshProductData);
            logger.info("📡 Registered for product data change notifications");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not register for product data changes", e);
        }
        
        setupFormControls();
        setupTable();
        setupFilters();
        setupScrollFunctionality();
        loadOrderData();
        setupEventHandlers();
        initializeInvoicePreview(); // Initialize invoice preview
        setupInvoicePreviewControls(); // Set up always-visible controls
        updateItemsDisplay(); // Initialize items display
        setupResponsiveLayout(); // Setup responsive height binding between containers
        
        // Initialize placeholder label visibility
        if (previewPlaceholderLabel != null) {
            previewPlaceholderLabel.setVisible(currentDynamicOrderItems.isEmpty());
            previewPlaceholderLabel.setManaged(currentDynamicOrderItems.isEmpty());
        }
        
        logger.info("✅ Orders Controller initialized successfully with scroll support");
    }
    
    /**
     * Load dynamic product data from products.txt file only
     * Categories are extracted dynamically from products - no separate categories.txt needed
     */
    private void loadDynamicProductData() {
        logger.info("📂 Loading dynamic product data from products.txt...");
        
        try {
            // Clear existing data
            categoryToFlavors.clear();
            availableCategories.clear();
            
            // Load products from products.txt and extract categories dynamically
            loadProductsFromFile();
            
            logger.info("✅ Dynamic product data loaded successfully: " + 
                       availableCategories.size() + " categories, " + 
                       categoryToFlavors.values().stream().mapToInt(List::size).sum() + " flavors");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error loading dynamic product data", e);
            // Fallback to basic categories if file loading fails
            setupFallbackProductData();
        }
    }
    
    // Categories are now extracted dynamically from products.txt - no separate file needed
    
    /**
     * Load products from products.txt file and extract categories dynamically
     * This eliminates the need for a separate categories.txt file
     */
    private void loadProductsFromFile() {
        try {
            Path productsPath = Paths.get("src/main/resources/txtFiles/products.txt");
            if (Files.exists(productsPath)) {
                List<String> lines = Files.readAllLines(productsPath);
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("\\|");
                        if (parts.length == 4) {
                            String category = parts[0].trim();
                            String name = parts[1].trim();
                            String description = parts[2].trim();
                            String price = parts[3].trim();
                            
                            ProductData product = new ProductData(category, name, description, price);
                            
                            // Add to category mapping
                            categoryToFlavors.computeIfAbsent(category, k -> new ArrayList<>()).add(product);
                            
                            // Add category to available categories list (dynamically extracted)
                            if (!availableCategories.contains(category)) {
                                availableCategories.add(category);
                            }
                        }
                    }
                }
                
                // Sort categories alphabetically for better UX
                availableCategories.sort(String.CASE_INSENSITIVE_ORDER);
                
                logger.info("🍽️ Loaded " + categoryToFlavors.values().stream().mapToInt(List::size).sum() + 
                           " products and extracted " + availableCategories.size() + " categories dynamically");
            } else {
                logger.warning("⚠️ Products file not found: " + productsPath);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "⚠️ Error loading products from file", e);
        }
    }
    
    /**
     * Setup fallback product data if file loading fails
     */
    private void setupFallbackProductData() {
        logger.info("🔄 Setting up fallback product data...");
        
        availableCategories.clear();
        categoryToFlavors.clear();
        
        // Basic fallback categories and flavors
        availableCategories.add("Savory Waffles");
        availableCategories.add("Spicy Waffles");
        availableCategories.add("Sweet Waffles");
        
        // Basic fallback flavors
        categoryToFlavors.put("Savory Waffles", Arrays.asList(
            new ProductData("Savory Waffles", "Eggmayoza", "Mashed boiled eggs with mayonnaise", "₱45.00"),
            new ProductData("Savory Waffles", "Tropiham", "Ham and pineapple with cheese", "₱55.00")
        ));
        
        categoryToFlavors.put("Spicy Waffles", Arrays.asList(
            new ProductData("Spicy Waffles", "Spicy tunasaur", "Spicy tuna mix with mayonnaise", "₱45.00"),
            new ProductData("Spicy Waffles", "Pimiento", "Bell pepper mix with cheese", "₱45.00")
        ));
        
        categoryToFlavors.put("Sweet Waffles", Arrays.asList(
            new ProductData("Sweet Waffles", "Berry on top", "Sweet berries topping", "₱45.00"),
            new ProductData("Sweet Waffles", "Oreo-verload", "Oreo cookies and cream", "₱45.00")
        ));
        
        logger.info("✅ Fallback product data configured");
    }

    private void setupFormControls() {
        paymentMethodCombo.setItems(FXCollections.observableArrayList(
            "Cash", "GCash", "Maya"
        ));

        // Use dynamic categories loaded from files instead of hardcoded enums
        categoryCombo.setItems(FXCollections.observableArrayList(availableCategories));

        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        
        // Setup price field with proper styling
        priceField.setEditable(false);
        priceField.setFocusTraversable(false);
        priceField.setText("₱0.00");
        
        // Ensure the price field maintains the correct text color
        priceField.getStyleClass().add("price-field");
        ensurePriceFieldStyling();
        
        // Setup digital payment fields with helpful tooltips
        setupDigitalPaymentFieldsGuidance();
        
        // Add listener to maintain text color when price changes
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Ensure text color is maintained when price updates
            if (!priceField.getStyle().contains("-fx-text-fill: #83827b")) {
                String currentStyle = priceField.getStyle();
                if (currentStyle.contains("-fx-text-fill:")) {
                    // Replace existing text-fill with the correct color
                    currentStyle = currentStyle.replaceAll("-fx-text-fill:[^;]*;?", "");
                }
                priceField.setStyle(currentStyle + " -fx-text-fill: #83827b;");
            }
        });
    }

    private void setupTable() {
        logger.info("📋 Setting up TableView with 7 columns (Order Status + Payment Status)...");
        try {
            // Disable table editing and selection to prevent row disappearing issues
            ordersTable.setEditable(false);
            ordersTable.setFocusTraversable(false);
            
            // Completely disable selection model to prevent visual selection effects
            ordersTable.setSelectionModel(null);
            
            // Set up cell value factories for the 6 columns
            orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty());
            nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            contactNumberColumn.setCellValueFactory(cellData -> cellData.getValue().contactNumberProperty());
            totalAmountColumn.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty());
            dateTimeColumn.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());
        orderStatusColumn.setCellValueFactory(cellData -> cellData.getValue().orderStatusProperty());
            
        // Set up comprehensive editable status columns with styled ComboBox
        setupOrderStatusColumn();

            // Set up actions column with edit and view receipt buttons
            actionsColumn.setCellFactory(col -> {
                TableCell<RecentOrder, Void> cell = new TableCell<>() {
                    private final Button editButton = new Button();
                    private final Button viewReceiptBtn = new Button("View Receipt");
                    private final HBox container;
                    
                    {
                        // Set up edit button with icon (matching ProductManagement design)
                        ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/edit_icon.png")));
                        editIcon.setFitWidth(14);
                        editIcon.setFitHeight(14);
                        editButton.setGraphic(editIcon);
                        editButton.setTooltip(new Tooltip("Edit Order"));
                        // Custom styling for edit button to make it appear as icon button
                        editButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                                          "-fx-padding: 3px; -fx-min-width: 24px; -fx-min-height: 24px; " +
                                          "-fx-pref-width: 24px; -fx-pref-height: 24px;");
                        
                        // Set up view receipt button
                        viewReceiptBtn.setOnAction(event -> {
                            RecentOrder order = getTableView().getItems().get(getIndex());
                            handleViewReceipt(order);
                        });
                        // Match overview styling - simple button without custom styling
                        
                        // Create container for both buttons with proper spacing
                        container = new HBox(8);
                        container.setAlignment(Pos.CENTER_LEFT);
                        container.getChildren().addAll(editButton, viewReceiptBtn);
                        
                        // Set up edit button action
                        editButton.setOnAction(event -> {
                            RecentOrder order = getTableView().getItems().get(getIndex());
                            handleEditOrder(order);
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : container);
                    }
                };
                return cell;
            });
            actionsColumn.setSortable(false);
            
            // Set table as editable and bind to filtered data
            ordersTable.setItems(filteredOrders);
            
            // Setup responsive column width management for exactly 6 columns
            ordersTable.widthProperty().addListener((obs, oldVal, newVal) -> {
                double width = newVal.doubleValue();
                if (width > 0) {
                    double totalMinWidth = orderIdColumn.getMinWidth() + nameColumn.getMinWidth() +
                            contactNumberColumn.getMinWidth() + totalAmountColumn.getMinWidth() + 
                            dateTimeColumn.getMinWidth() + orderStatusColumn.getMinWidth() + 
                            actionsColumn.getMinWidth();

                    if (width > totalMinWidth) {
                        double remainingWidth = width - totalMinWidth;
                        orderIdColumn.setPrefWidth(orderIdColumn.getMinWidth() + (remainingWidth * 0.12));
                        nameColumn.setPrefWidth(nameColumn.getMinWidth() + (remainingWidth * 0.15));
                        contactNumberColumn.setPrefWidth(contactNumberColumn.getMinWidth() + (remainingWidth * 0.15));
                        totalAmountColumn.setPrefWidth(totalAmountColumn.getMinWidth() + (remainingWidth * 0.13));
                        dateTimeColumn.setPrefWidth(dateTimeColumn.getMinWidth() + (remainingWidth * 0.20));
                        orderStatusColumn.setPrefWidth(orderStatusColumn.getMinWidth() + (remainingWidth * 0.15));
                        actionsColumn.setPrefWidth(actionsColumn.getMinWidth() + (remainingWidth * 0.20)); // Increased for two buttons
                    }
                }
            });
            
            logger.info("✅ Table setup completed with 6 columns and responsive width management");
            
            // Add comprehensive keyboard support for editing
            setupTableKeyboardHandling();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error setting up table", e);
        }
    }

    private void setupFilters() {
        // Setup order status filter
        orderStatusFilter.setItems(FXCollections.observableArrayList(
            "All", "Pending", "In Progress", "Completed", "Cancelled"
        ));
        orderStatusFilter.setValue("All");
        


        sortFilterCombo.setItems(FXCollections.observableArrayList(
            "Date (Newest First)", "Date (Oldest First)", "Name (A-Z)", "Name (Z-A)", 
            "Amount (High to Low)", "Amount (Low to High)", "Status"
        ));
        sortFilterCombo.setValue("Date (Newest First)");
    }

    /**
     * Setup order status column with always-visible ComboBox dropdown and color-coded text
     */
    private void setupOrderStatusColumn() {
        logger.info("🎨 Setting up always-visible ComboBox order status column...");
        
        try {
            ObservableList<String> orderStatusOptions = FXCollections.observableArrayList(
                "Pending", "In Progress", "Completed", "Cancelled"
            );
            
            orderStatusColumn.setCellFactory(column -> {
                TableCell<RecentOrder, String> cell = new TableCell<RecentOrder, String>() {
                    private final ComboBox<String> comboBox = new ComboBox<>(orderStatusOptions);
                    private RecentOrder currentOrder;
                    private boolean isUpdating = false;
                    
                    {
                        comboBox.setMaxWidth(Double.MAX_VALUE);
                        comboBox.setPrefWidth(115);
                        comboBox.setMinWidth(110);
                        comboBox.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: white; " +
                                         "-fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 3px; " +
                                         "-fx-background-radius: 3px; -fx-padding: 3px 6px;");
                        
                        comboBox.setButtonCell(new ListCell<String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText("");
                                    setStyle("");
                                } else {
                                    setText(item);
                                    String textColor = switch (item) {
                                        case "Pending" -> "#92400E";      // Brown
                                        case "In Progress" -> "#1E40AF";  // Blue
                                        case "Completed" -> "#065F46";    // Green
                                        case "Cancelled" -> "#991B1B";    // Red
                                        default -> "#6B7280";             // Gray
                                    };
                                    setStyle("-fx-text-fill: " + textColor + "; -fx-font-weight: bold; " +
                                           "-fx-font-size: 11px; -fx-padding: 2px 4px; " +
                                           "-fx-text-alignment: left; -fx-alignment: center-left;");
                                }
                            }
                        });
                        
                        comboBox.setOnAction(event -> {
                            if (isUpdating || currentOrder == null || comboBox.getValue() == null) {
                                return;
                            }
                            
                            String oldStatus = currentOrder.getOrderStatus();
                            String newStatus = comboBox.getValue();
                            
                            if (!newStatus.equals(oldStatus)) {
                                currentOrder.setOrderStatus(newStatus);
                                handleOrderStatusChangeDirectly(currentOrder, oldStatus, newStatus);
                                Platform.runLater(() -> ordersTable.refresh());
                                logger.info("🎯 Order status updated: " + currentOrder.getOrderId() + 
                                          " changed from " + oldStatus + " to " + newStatus);
                            }
                        });
                    }
                    
                    @Override
                    protected void updateItem(String status, boolean empty) {
                        super.updateItem(status, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                            currentOrder = null;
                        } else {
                            currentOrder = (RecentOrder) getTableRow().getItem();
                            isUpdating = true;
                            comboBox.setValue(status);
                            isUpdating = false;
                            setGraphic(comboBox);
                        }
                    }
                };
                return cell;
            });
            
            orderStatusColumn.setEditable(false);
            orderStatusColumn.setSortable(true);
            orderStatusColumn.setMinWidth(110);
            orderStatusColumn.setPrefWidth(120);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error setting up order status column", e);
        }
    }
    
    // Payment status functionality removed as per requirements

    // Legacy method removed - replaced with setupOrderStatusColumn() and setupPaymentStatusColumn()

    /**
     * Setup basic keyboard handling for table navigation (no editing needed)
     */
    private void setupTableKeyboardHandling() {
        logger.info("⌨️ Setting up basic keyboard handling...");
        
        try {
            // Since we removed selection model and editing, minimal keyboard handling
            ordersTable.setOnKeyPressed(event -> {
                // Allow basic keyboard navigation for accessibility
                switch (event.getCode()) {
                    case UP:
                    case DOWN:
                    case PAGE_UP:
                    case PAGE_DOWN:
                        // Let default behavior handle scrolling
                        break;
                    default:
                        // Consume other keys to prevent unwanted behavior
                        event.consume();
                        break;
                }
            });
            
            logger.info("✅ Basic keyboard handling configured successfully");
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "⚠️ Error setting up keyboard handling", e);
        }
    }
    
    /**
     * Handle status change events with comprehensive validation, feedback, and file persistence
     * @param event The edit commit event containing old and new values
     */
    private void handleStatusChange(TableColumn.CellEditEvent<RecentOrder, String> event) {
        try {
            RecentOrder order = event.getRowValue();
            String oldStatus = event.getOldValue();
            String newStatus = event.getNewValue();
            
            if (order == null) {
                logger.warning("⚠️ Cannot update status: Order is null");
            return;
        }
        
            if (newStatus == null || newStatus.trim().isEmpty()) {
                logger.warning("⚠️ Cannot update status: New status is null or empty");
                // Revert to old status
                order.setStatus(oldStatus);
                return;
            }
            
            // Validate new status is in allowed list
            List<String> allowedStatuses = Arrays.asList("Pending", "In Progress", "Completed", "Cancelled");
            if (!allowedStatuses.contains(newStatus)) {
                logger.warning("⚠️ Invalid status attempted: " + newStatus + ". Reverting to: " + oldStatus);
                order.setStatus(oldStatus);
                showStatusChangeAlert("Invalid Status", 
                    "The status '" + newStatus + "' is not allowed. Please select from: " + allowedStatuses);
                return;
            }
            
            // Update the order status in memory
            order.setStatus(newStatus);
            
            // Save the updated status to the orders.txt file
            boolean saveSuccess = saveOrdersToFile();
            
            if (saveSuccess) {
                // Log successful change with details
                logger.info("🎯 Order " + order.getOrderId() + " status successfully updated: " + 
                           oldStatus + " → " + newStatus + " (Customer: " + order.getName() + ")");
                logger.info("💾 Status change saved to orders.txt file");
            } else {
                // Revert the change if saving failed
                order.setStatus(oldStatus);
                logger.severe("❌ Failed to save status change to file. Reverting to: " + oldStatus);
                showStatusChangeAlert("Save Error", 
                    "Failed to save the status change to file. The change has been reverted.");
                return;
            }
            
            // Refresh the table to ensure UI consistency
            Platform.runLater(() -> {
                ordersTable.refresh();
                
                // Optionally refresh filters if status changed
                if (!Objects.equals(oldStatus, newStatus)) {
                    // Update any status-based filters or statistics
                    updateStatusStatistics();
                }
            });
            
            // Show success feedback (optional - can be commented out if too verbose)
            // showStatusChangeAlert("Status Updated", 
            //     "Order " + order.getOrderId() + " status changed to: " + newStatus);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error handling status change", e);
            showStatusChangeAlert("Error", "Failed to update order status. Please try again.");
        }
    }
    
    /**
     * Apply visual styling based on the current status (text color only, no background)
     * @param cell The table cell to style
     * @param status The current status value
     */
    private void applyStatusStyling(TableCell<?, ?> cell, String status) {
        if (status == null) return;
        
        // Base styling without background colors - only padding, font styling
        String baseStyle = "-fx-padding: 5px 8px; -fx-font-weight: bold; -fx-font-size: 11px; -fx-alignment: center;";
        
        // Status-specific text colors only (no background or border colors)
        String statusStyle = switch (status) {
            case "Pending" -> 
                "-fx-text-fill: #92400E;"; // Brown text for pending
            case "In Progress" -> 
                "-fx-text-fill: #1E40AF;"; // Blue text for in progress
            case "Completed" -> 
                "-fx-text-fill: #065F46;"; // Green text for completed
            case "Cancelled" -> 
                "-fx-text-fill: #991B1B;"; // Red text for cancelled
            default -> 
                "-fx-text-fill: #6B7280;"; // Gray text for unknown status
        };
        
        cell.setStyle(baseStyle + statusStyle);
    }

    /**
     * Apply color styling to ComboBox based on selected status (text color only)
     * @param comboBox The ComboBox to style
     * @param status The current status value
     */
    private void applyComboBoxStatusStyling(ComboBox<String> comboBox, String status) {
        if (status == null) return;
        
        // Base styling for ComboBox
        String baseStyle = "-fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 5px 8px;";
        
        // Status-specific text colors only (no background colors)
        String statusStyle = switch (status) {
            case "Pending" -> 
                "-fx-text-fill: #92400E;"; // Brown text for pending
            case "In Progress" -> 
                "-fx-text-fill: #1E40AF;"; // Blue text for in progress
            case "Completed" -> 
                "-fx-text-fill: #065F46;"; // Green text for completed
            case "Cancelled" -> 
                "-fx-text-fill: #991B1B;"; // Red text for cancelled
            default -> 
                "-fx-text-fill: #6B7280;"; // Gray text for unknown status
        };
        
        comboBox.setStyle(baseStyle + statusStyle);
    }

    /**
     * Handle status changes directly from ComboBox selection
     * @param order The order being updated
     * @param oldStatus The previous status
     * @param newStatus The new status
     */
    private void handleStatusChangeDirectly(RecentOrder order, String oldStatus, String newStatus) {
        try {
            // Validate new status is in allowed list
            List<String> allowedStatuses = Arrays.asList("Pending", "In Progress", "Completed", "Cancelled");
            if (!allowedStatuses.contains(newStatus)) {
                logger.warning("⚠️ Invalid status attempted: " + newStatus + ". Reverting to: " + oldStatus);
                order.setStatus(oldStatus);
                showStatusChangeAlert("Invalid Status", 
                    "The status '" + newStatus + "' is not allowed. Please select from: " + allowedStatuses);
                return;
            }
            
            // Save the updated status to the orders.txt file
            boolean saveSuccess = saveOrdersToFile();
            
            if (saveSuccess) {
                // Log successful change with details
                logger.info("🎯 Order " + order.getOrderId() + " status successfully updated: " + 
                           oldStatus + " → " + newStatus + " (Customer: " + order.getName() + ")");
                logger.info("💾 Status change saved to orders.txt file");
                
                // Update statistics without redundant table refresh (already handled by caller)
                Platform.runLater(() -> {
                    updateStatusStatistics();
                });
            } else {
                // Revert the change if saving failed
                order.setStatus(oldStatus);
                logger.severe("❌ Failed to save status change to file. Reverting to: " + oldStatus);
                showStatusChangeAlert("Save Error", 
                    "Failed to save the status change to file. The change has been reverted.");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error handling direct status change", e);
            showStatusChangeAlert("Error", "Failed to update order status. Please try again.");
        }
    }
    
    /**
     * Update status-related statistics (optional enhancement)
     * Can be used to update counters, refresh dashboards, etc.
     */
    private void updateStatusStatistics() {
        try {
            // Count orders by status
            Map<String, Long> statusCounts = allOrders.stream()
                .collect(Collectors.groupingBy(RecentOrder::getStatus, Collectors.counting()));
            
            logger.info("📊 Status distribution updated: " + statusCounts);
            
            // Could emit events or update other UI components here
            // For example: notify dashboard, update counters, etc.
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "⚠️ Error updating status statistics", e);
        }
    }
    
    /**
     * Show alert for status change feedback
     * @param title Alert title
     * @param message Alert message
     */
    private void showStatusChangeAlert(String title, String message) {
        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            } catch (Exception e) {
                logger.log(Level.WARNING, "⚠️ Error showing status change alert", e);
            }
        });
    }
    
    /**
     * Handle order status change directly from ComboBox
     */
    private void handleOrderStatusChangeDirectly(RecentOrder order, String oldStatus, String newStatus) {
        try {
            // Save changes to file
            if (saveOrdersToFile()) {
                logger.info("💾 Order status change saved for: " + order.getOrderId());
                updateStatusStatistics();
            } else {
                logger.warning("⚠️ Failed to save order status change");
                // Revert the change
                order.setOrderStatus(oldStatus);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save order status change");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error handling order status change", e);
            order.setOrderStatus(oldStatus); // Revert
            showAlert(Alert.AlertType.ERROR, "Error", "Error updating order status: " + e.getMessage());
        }
    }
    
    /**
     * Handle payment status change directly from ComboBox
     */
    private void handlePaymentStatusChangeDirectly(RecentOrder order, String oldStatus, String newStatus) {
        try {
            // Save changes to file
            if (saveOrdersToFile()) {
                logger.info("💾 Payment status change saved for: " + order.getOrderId());
                updateStatusStatistics();
            } else {
                logger.warning("⚠️ Failed to save payment status change");
                // Revert the change
                order.setPaymentStatus(oldStatus);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save payment status change");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error handling payment status change", e);
            order.setPaymentStatus(oldStatus); // Revert
            showAlert(Alert.AlertType.ERROR, "Error", "Error updating payment status: " + e.getMessage());
        }
    }
    
    /**
     * Handle edit order action - populate form fields with order data for editing
     * @param order The order to edit
     */
    private void handleEditOrder(RecentOrder order) {
        logger.info("✏️ Editing order: " + order.getOrderId());
        
        try {
            // Clear existing form data first
            handleClearForm();
            handleClearItems();
            
            // Populate customer information
            customerNameField.setText(order.getName());
            contactNumberField.setText(order.getContactNumber());
            
            // Set payment method and related fields
            paymentMethodCombo.setValue(order.getPaymentMethod());
            handlePaymentMethodChange(); // Trigger UI updates for payment method
            
            // Handle payment-specific fields
            if ("Cash".equals(order.getPaymentMethod())) {
                // For cash payments, set the cash received amount
                double cashReceived = parseCashAmount(order);
                if (cashReceived > 0) {
                    cashReceivedField.setText(String.valueOf(cashReceived));
                }
            } else {
                // For digital payments (GCash/Maya), set reference number
                String referenceNumber = order.getReferenceNumber();
                if (referenceNumber != null && !referenceNumber.isEmpty() && !referenceNumber.equals("0.00")) {
                    referenceNumberField.setText(referenceNumber);
                }
                
                // Set timestamp if available
                String timestamp = order.getTimestamp();
                if (timestamp != null && !timestamp.isEmpty() && !timestamp.equals("\"\"")) {
                    timestampField.setText(timestamp.replace("\"", ""));
                }
            }
            
            // Parse and populate ordered items
            parseAndPopulateOrderItems(order.getItemsOrdered());
            
            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Edit Mode", 
                     "Order data loaded into form. Make your changes and click 'Create Order' to update.");
            
            logger.info("✅ Order data successfully loaded into form for editing");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error loading order for editing", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load order data for editing: " + e.getMessage());
        }
    }
    
    /**
     * Parse items ordered string and populate the form with individual items
     * @param itemsOrdered String like "Oreo Overload x2; Chocolate x1"
     */
    private void parseAndPopulateOrderItems(String itemsOrdered) {
        if (itemsOrdered == null || itemsOrdered.trim().isEmpty()) {
            return;
        }
        
        try {
            // Split items by semicolon
            String[] items = itemsOrdered.split(";");
            
            for (String item : items) {
                item = item.trim();
                if (item.isEmpty()) continue;
                
                // Parse item format: "Flavor Name x Quantity"
                String flavorName;
                int quantity = 1;
                
                if (item.contains(" x")) {
                    int xIndex = item.lastIndexOf(" x");
                    flavorName = item.substring(0, xIndex).trim();
                    
                    try {
                        String quantityStr = item.substring(xIndex + 2).trim();
                        quantity = Integer.parseInt(quantityStr);
                    } catch (NumberFormatException e) {
                        logger.warning("⚠️ Could not parse quantity from: " + item);
                        quantity = 1;
                    }
                } else {
                    flavorName = item.trim();
                }
                
                // Find the product data for this flavor
                ProductData productData = findProductByName(flavorName);
                if (productData != null) {
                    // Set category and flavor in combo boxes
                    categoryCombo.setValue(productData.getCategory());
                    handleCategorySelection(); // Load flavors for this category
                    flavorCombo.setValue(productData.getName());
                    handleFlavorSelection(); // Load price
                    
                    // Set quantity
                    quantitySpinner.getValueFactory().setValue(quantity);
                    
                    // Add the item
                    handleAddItem();
                } else {
                    logger.warning("⚠️ Could not find product data for flavor: " + flavorName);
                }
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "⚠️ Error parsing order items", e);
        }
    }

    /**
     * Setup scroll functionality to ensure scrolling works even with hidden scroll bars
     * This enables mouse wheel and touch scrolling throughout the orders page
     */
    private void setupScrollFunctionality() {
        logger.info("📜 Setting up enhanced scroll functionality...");
        
        Platform.runLater(() -> {
            try {
                // Find the ScrollPane in the scene graph
                javafx.scene.control.ScrollPane scrollPane = findScrollPaneInScene();
                
                if (scrollPane != null) {
                    // Enable mouse wheel scrolling
                    scrollPane.setOnScroll(event -> {
                        double deltaY = event.getDeltaY() * 2; // Increase scroll sensitivity
                        double currentVValue = scrollPane.getVvalue();
                        double newVValue = currentVValue - (deltaY / scrollPane.getContent().getBoundsInLocal().getHeight());
                        
                        // Clamp the value between 0 and 1
                        newVValue = Math.max(0, Math.min(1, newVValue));
                        scrollPane.setVvalue(newVValue);
                        
                        event.consume(); // Prevent event bubbling
                    });
                    
                    // Enable keyboard scrolling
                    scrollPane.setOnKeyPressed(event -> {
                        double scrollAmount = 0.1;
                        switch (event.getCode()) {
                            case UP:
                            case PAGE_UP:
                                scrollPane.setVvalue(Math.max(0, scrollPane.getVvalue() - scrollAmount));
                                event.consume();
                break;
                            case DOWN:
                            case PAGE_DOWN:
                                scrollPane.setVvalue(Math.min(1, scrollPane.getVvalue() + scrollAmount));
                                event.consume();
                break;
                            case HOME:
                                scrollPane.setVvalue(0);
                                event.consume();
                                break;
                            case END:
                                scrollPane.setVvalue(1);
                                event.consume();
                break;
        }
                    });
                    
                    // Ensure the ScrollPane can receive focus for keyboard events
                    scrollPane.setFocusTraversable(true);
                    
                    logger.info("✅ Enhanced scroll functionality configured successfully");
                } else {
                    logger.warning("⚠️ ScrollPane not found in scene graph");
                }
                
            } catch (Exception e) {
                logger.log(Level.WARNING, "⚠️ Error setting up scroll functionality", e);
            }
        });
    }

    /**
     * Find the ScrollPane in the current scene graph
     * @return ScrollPane if found, null otherwise
     */
    private javafx.scene.control.ScrollPane findScrollPaneInScene() {
        try {
            // Start from a known FXML control and traverse up to find ScrollPane
            javafx.scene.Node currentNode = ordersTable;
            
            while (currentNode != null) {
                if (currentNode instanceof javafx.scene.control.ScrollPane) {
                    return (javafx.scene.control.ScrollPane) currentNode;
                }
                currentNode = currentNode.getParent();
            }
            
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "⚠️ Error finding ScrollPane in scene", e);
            return null;
        }
    }

    private void loadOrderData() {
        try {
            String filePath = "txtFiles/orders.txt";
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
            
            if (inputStream == null) {
                inputStream = getClass().getResourceAsStream("/" + filePath);
            }
            
            if (inputStream == null) {
                logger.severe("Orders file not found: " + filePath);
                return;
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                allOrders = reader.lines()
                    .filter(line -> !line.trim().isEmpty())
                    .filter(line -> !line.trim().startsWith("#"))
                    .map(this::parseOrderLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

                // Sort orders by date (newest first) to ensure proper chronological order
                allOrders.sort((o1, o2) -> {
                    // First compare by date (newest first)
                    LocalDate date1 = parseOrderDate(o1.getOrderDate());
                    LocalDate date2 = parseOrderDate(o2.getOrderDate());
                    int dateComparison = date2.compareTo(date1); // Reverse for newest first
                    
                    if (dateComparison != 0) {
                        return dateComparison;
                    }
                    
                    // If same date, sort by order ID sequence (highest number first)
                    String orderId1 = o1.getOrderId();
                    String orderId2 = o2.getOrderId();
                    
                    // Extract sequence numbers from order IDs (e.g., "001" from "WP20250624-001")
                    try {
                        String seq1 = orderId1.substring(orderId1.lastIndexOf('-') + 1);
                        String seq2 = orderId2.substring(orderId2.lastIndexOf('-') + 1);
                        int num1 = Integer.parseInt(seq1);
                        int num2 = Integer.parseInt(seq2);
                        return Integer.compare(num2, num1); // Reverse for highest first
                    } catch (Exception e) {
                        // Fallback to string comparison if parsing fails
                        return orderId2.compareTo(orderId1);
                    }
                });

                logger.info("Successfully loaded " + allOrders.size() + " orders (sorted newest first)");
                applyFiltersAsync();
                updatePagination();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading order data", e);
        }
    }

    private RecentOrder parseOrderLine(String line) {
        try {
            List<String> tokens = new ArrayList<>();
            StringBuilder currentToken = new StringBuilder();
            boolean inQuotes = false;
            
            for (char c : line.toCharArray()) {
                if (c == '"') {
                    inQuotes = !inQuotes;
                } else if (c == ',' && !inQuotes) {
                    tokens.add(currentToken.toString().trim());
                    currentToken = new StringBuilder();
                } else {
                    currentToken.append(c);
                }
            }
            tokens.add(currentToken.toString().trim());
            
            // Handle different formats: old (8 fields), with contact (9 fields), and new (11 fields)
            if (tokens.size() < 8) return null;
            
            if (tokens.size() >= 11) {
                // New format with contact number and digital payment details
                return new RecentOrder(
                    tokens.get(0).trim(), tokens.get(1).trim(), tokens.get(2).trim(), 
                    tokens.get(3).replace("\"", "").trim(), tokens.get(4).trim(), tokens.get(5).trim(), 
                    tokens.get(6).trim(), tokens.get(7).replace("\"", "").trim(), tokens.get(8).trim(),
                    tokens.get(9).replace("\"", "").trim(), tokens.get(10).replace("\"", "").trim()
                );
            } else if (tokens.size() >= 10) {
                // Old format with digital payment details but no contact number
                return new RecentOrder(
                    tokens.get(0).trim(), tokens.get(1).trim(), "", tokens.get(2).replace("\"", "").trim(), 
                    tokens.get(3).trim(), tokens.get(4).trim(), tokens.get(5).trim(), 
                    tokens.get(6).replace("\"", "").trim(), tokens.get(7).trim(),
                    tokens.get(8).replace("\"", "").trim(), tokens.get(9).replace("\"", "").trim()
                );
            } else if (tokens.size() >= 9) {
                // Format with contact number but no digital payment details
                return new RecentOrder(
                    tokens.get(0).trim(), tokens.get(1).trim(), tokens.get(2).trim(),
                    tokens.get(3).replace("\"", "").trim(), tokens.get(4).trim(), tokens.get(5).trim(), 
                    tokens.get(6).trim(), tokens.get(7).replace("\"", "").trim(), tokens.get(8).trim()
                );
            } else {
                // Old format without contact number and digital payment details
                return new RecentOrder(
                    tokens.get(0).trim(), tokens.get(1).trim(), tokens.get(2).replace("\"", "").trim(), 
                    tokens.get(3).trim(), tokens.get(4).trim(), tokens.get(5).trim(), 
                    tokens.get(6).replace("\"", "").trim(), tokens.get(7).trim()
                );
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing order line: " + line, e);
            return null;
        }
    }
    
    /**
     * Save all orders data back to the orders.txt file with comprehensive error handling
     * @return true if save was successful, false otherwise
     */
    private boolean saveOrdersToFile() {
        try {
            // Get the path to the orders.txt file in resources
            String resourcePath = "src/main/resources/txtFiles/orders.txt";
            Path filePath = Paths.get(resourcePath);
            
            // Create the directory if it doesn't exist
            Files.createDirectories(filePath.getParent());
            
            // Prepare the content to write
            List<String> lines = new ArrayList<>();
            
            // Add the header comment
            lines.add("# Format: Order ID, Name, Contact Number, Items Ordered, Total Items, Total Amount, Payment Method, Date and Time, Status, Reference Number, Timestamp");
            
            // Convert all orders back to the file format
            for (RecentOrder order : allOrders) {
                String line = formatOrderForFile(order);
                lines.add(line);
            }
            
            // Write all lines to the file, replacing the existing content
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            logger.info("💾 Successfully saved " + allOrders.size() + " orders to file: " + resourcePath);
            return true;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error saving orders to file", e);
            return false;
        }
    }

    /**
     * Format a RecentOrder object back to the original file format
     * @param order The order to format
     * @return Formatted string ready for the file
     */
    private String formatOrderForFile(RecentOrder order) {
        // Format: Order ID, Name, Contact Number, Items Ordered, Total Items, Total Amount, Payment Method, Date and Time, Status, Reference Number, Timestamp
        return String.format("%s,%s,%s,\"%s\",%s,%s,%s,\"%s\",%s,\"%s\",\"%s\"",
            order.getOrderId(),
            order.getName(),
            order.getContactNumber(),
            order.getItemsOrdered(),
            order.getTotalItems(),
            order.getTotalAmount(),
            order.getPaymentMethod(),
            order.getOrderDate(),
            order.getStatus(),
            order.getReferenceNumber(),
            order.getTimestamp()
        );
    }

    private void setupEventHandlers() {
        categoryCombo.setOnAction(event -> {
            handleCategorySelection();
            // No preview update - only selection change, not order change
        });
        flavorCombo.setOnAction(event -> {
            handleFlavorSelection();
            // No preview update - only selection change, not order change
        });
        addItemButton.setOnAction(event -> handleAddItem());
        clearButton.setOnAction(event -> handleClearForm());
        createOrderButton.setOnAction(event -> handleCreateOrder());
        
        // Payment method change listener (triggers preview update)
        paymentMethodCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            handlePaymentMethodChange();
            updateInvoicePreview(); // Only update when payment method changes
        });
        
        // Table filters with optimized handling
        orderStatusFilter.setOnAction(event -> applyFiltersAsync());
        setupDebouncedSearch(); // Setup debounced search filtering
        sortFilterCombo.setOnAction(event -> applyFiltersAsync());
        clearFiltersButton.setOnAction(event -> clearAllFilters());
        
        // Payment detail fields (only update on focus lost to avoid constant refreshing)
        referenceNumberField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && ("Maya".equals(paymentMethodCombo.getValue()) || "GCash".equals(paymentMethodCombo.getValue()))) {
                updateInvoicePreview(); // Update when field loses focus for digital payments
            }
        });
        timestampField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && ("Maya".equals(paymentMethodCombo.getValue()) || "GCash".equals(paymentMethodCombo.getValue()))) {
                updateInvoicePreview(); // Update when field loses focus for digital payments
            }
        });
        cashReceivedField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && "Cash".equals(paymentMethodCombo.getValue())) {
                updateInvoicePreview(); // Update when field loses focus for cash payments
            }
        });
    }

    private void handleCategorySelection() {
        String selectedCategory = categoryCombo.getValue();
        if (selectedCategory != null) {
            // Use dynamic data instead of enum lookup
            List<ProductData> flavors = categoryToFlavors.get(selectedCategory);
            if (flavors != null && !flavors.isEmpty()) {
                List<String> flavorNames = flavors.stream()
                    .map(ProductData::getName)
                    .collect(Collectors.toList());
                flavorCombo.setItems(FXCollections.observableArrayList(flavorNames));
                flavorCombo.setValue(null);
                priceField.setText("₱0.00");
                logger.info("📋 Category '" + selectedCategory + "' selected with " + flavorNames.size() + " flavors");
            } else {
                flavorCombo.setItems(FXCollections.observableArrayList());
                flavorCombo.setValue(null);
                priceField.setText("₱0.00");
                logger.warning("⚠️ No flavors found for category: " + selectedCategory);
            }
        }
    }

    private void handleFlavorSelection() {
        String selectedFlavor = flavorCombo.getValue();
        String selectedCategory = categoryCombo.getValue();
        
        if (selectedFlavor != null && selectedCategory != null) {
            // Use dynamic data instead of enum lookup
            List<ProductData> flavors = categoryToFlavors.get(selectedCategory);
            if (flavors != null) {
                ProductData selectedProduct = flavors.stream()
                    .filter(product -> product.getName().equals(selectedFlavor))
                    .findFirst()
                    .orElse(null);
                
                if (selectedProduct != null) {
                    priceField.setText(selectedProduct.getFormattedPrice());
                    // Ensure the text color remains correct after setting the price
                    ensurePriceFieldStyling();
                    logger.info("🏷️ Flavor '" + selectedFlavor + "' selected with price: " + selectedProduct.getFormattedPrice());
                } else {
                    priceField.setText("₱0.00");
                    logger.warning("⚠️ Flavor not found: " + selectedFlavor);
                }
            }
        }
    }
    
    /**
     * Handle payment method selection changes - show/hide payment-specific fields
     */
    private void handlePaymentMethodChange() {
        String selectedPaymentMethod = paymentMethodCombo.getValue();
        
        // Show digital payment fields for Maya and GCash
        boolean showDigitalFields = "Maya".equals(selectedPaymentMethod) || "GCash".equals(selectedPaymentMethod);
        // Show cash payment fields for Cash
        boolean showCashFields = "Cash".equals(selectedPaymentMethod);
        
        digitalPaymentContainer.setVisible(showDigitalFields);
        digitalPaymentContainer.setManaged(showDigitalFields);
        
        cashPaymentContainer.setVisible(showCashFields);
        cashPaymentContainer.setManaged(showCashFields);
        
        // ScrollPane size is now fixed in FXML - no dynamic adjustment needed
        
        // Clear the fields when hiding them
        if (!showDigitalFields) {
            referenceNumberField.clear();
            timestampField.clear();
        }
        
        if (!showCashFields) {
            cashReceivedField.clear();
        }
        
        // **Update the invoice preview to refresh the image size if needed**
        Platform.runLater(() -> {
            if (currentPreviewImageView != null) {
                // Trigger a page view reset to fit the new container size
                resetToPageView();
            }
        });
        
        logger.info("💳 Payment method changed to: " + selectedPaymentMethod + 
                   " | Digital fields visible: " + showDigitalFields + 
                   " | Cash fields visible: " + showCashFields);
    }
    
    /**
     * Ensures the price field maintains the correct text color styling
     */
    private void ensurePriceFieldStyling() {
        // Apply the correct text color to ensure automatic pricing displays properly
        String currentStyle = priceField.getStyle();
        if (!currentStyle.contains("-fx-text-fill: #83827b")) {
            if (currentStyle.contains("-fx-text-fill:")) {
                // Replace existing text-fill with the correct color
                currentStyle = currentStyle.replaceAll("-fx-text-fill:[^;]*;?", "");
            }
            priceField.setStyle(currentStyle + " -fx-text-fill: #83827b;");
        }
    }
    
    /**
     * Setup guidance and tooltips for digital payment fields
     */
    private void setupDigitalPaymentFieldsGuidance() {
        // Add tooltip for reference number field
        Tooltip referenceTooltip = new Tooltip("Enter the transaction reference number from Maya/GCash");
        referenceTooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #fffbe9; -fx-text-fill: #92400d;");
        referenceNumberField.setTooltip(referenceTooltip);
        
        // Add tooltip for timestamp field with format examples
        Tooltip timestampTooltip = new Tooltip(
            "Enter the transaction timestamp\n\n" +
            "Required format:\n" +
            "MM/DD/YYYY H:MM AM/PM\n\n" +
            "Example: 01/26/2025 2:30 PM"
        );
        timestampTooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #fffbe9; -fx-text-fill: #92400d;");
        timestampField.setTooltip(timestampTooltip);
        
        logger.info("📋 Digital payment fields guidance configured with tooltips");
    }
    
    /**
     * Validate timestamp format - only accepts MM/DD/YYYY H:MM AM/PM format
     * @param timestamp The timestamp string to validate
     * @return true if format is valid, false otherwise
     */
    private boolean isValidTimestampFormat(String timestamp) {
        if (timestamp == null || timestamp.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Define the exact required format: MM/DD/YYYY H:MM AM/PM
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.ENGLISH);
            
            // Try to parse the timestamp with the exact format
            LocalDateTime.parse(timestamp.trim(), formatter);
            
            logger.info("✅ Timestamp format validation passed: " + timestamp);
            return true;
            
        } catch (Exception e) {
            logger.warning("❌ Timestamp format validation failed for: " + timestamp + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Dynamic order item class that works with file-based product data
     * Replaces dependency on WaffleCategory and WaffleFlavor enums
     */
    public static class DynamicOrderItem {
        private String categoryName;
        private ProductData product;
        private int quantity;
        private double subtotal;
        
        public DynamicOrderItem(String categoryName, ProductData product, int quantity) {
            this.categoryName = categoryName;
            this.product = product;
            this.quantity = quantity;
            this.subtotal = product.getPriceValue() * quantity;
        }
        
        public String getCategoryName() { return categoryName; }
        public ProductData getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public double getSubtotal() { return subtotal; }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
            this.subtotal = product.getPriceValue() * quantity;
        }
        
        public String getDisplayString() {
            return quantity + "x " + product.getName();
        }
        
        public String getDetailedDisplayString() {
            return String.format("%dx %s (%s each) = ₱%.2f", 
                    quantity, product.getName(), product.getFormattedPrice(), subtotal);
        }
    }

    private void handleAddItem() {
        String category = categoryCombo.getValue();
        String flavor = flavorCombo.getValue();
        Integer quantity = quantitySpinner.getValue();
        
        if (category == null || flavor == null || quantity == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please select category, flavor, and quantity before adding item.");
                return;
            }
            
        // Use dynamic data instead of enum lookup
        List<ProductData> flavors = categoryToFlavors.get(category);
        if (flavors != null) {
            ProductData selectedProduct = flavors.stream()
                .filter(product -> product.getName().equals(flavor))
                .findFirst()
                .orElse(null);
            
            if (selectedProduct != null) {
                // Check if item already exists and stack quantities
                DynamicOrderItem existingItem = currentDynamicOrderItems.stream()
                    .filter(item -> item.getProduct().getName().equals(flavor) && 
                                   item.getCategoryName().equals(category))
                    .findFirst()
                    .orElse(null);
                
                if (existingItem != null) {
                    // Stack the quantities for existing item
                    int newQuantity = existingItem.getQuantity() + quantity;
                    existingItem.setQuantity(newQuantity);
                totalQuantity += quantity;
                    
                    logger.info("📦 Stacked item quantities: " + existingItem.getDetailedDisplayString());
                } else {
                    // Add new item
                    DynamicOrderItem item = new DynamicOrderItem(category, selectedProduct, quantity);
                    currentDynamicOrderItems.add(item);
                    totalQuantity += quantity;
                    
                    logger.info("📦 Added new item to order: " + item.getDetailedDisplayString());
                }
                
                totalLabel.setText("Total Items: " + totalQuantity);
                
                // Reset item form fields for next item
            categoryCombo.setValue(null);
            flavorCombo.setValue(null);
                quantitySpinner.getValueFactory().setValue(1);
                priceField.setText("₱0.00");
                ensurePriceFieldStyling();
                
                // Update invoice preview and item display
                updateInvoicePreview();
                updateItemsDisplay();
                
            } else {
                showAlert(Alert.AlertType.ERROR, "Product Not Found", "Selected flavor was not found in the system.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Category Not Found", "Selected category was not found in the system.");
        }
    }

    private void handleClearForm() {
        customerNameField.clear();
        contactNumberField.clear();
        paymentMethodCombo.setValue(null);
        referenceNumberField.clear();
        timestampField.clear();
        cashReceivedField.clear();
        digitalPaymentContainer.setVisible(false);
        digitalPaymentContainer.setManaged(false);
        cashPaymentContainer.setVisible(false);
        cashPaymentContainer.setManaged(false);
        
        // ScrollPane size is fixed in FXML - no reset needed
        
        categoryCombo.setValue(null);
        flavorCombo.setValue(null);
        quantitySpinner.getValueFactory().setValue(1);
        priceField.setText("₱0.00");
        ensurePriceFieldStyling();
        currentDynamicOrderItems.clear();
        totalQuantity = 0;
        totalLabel.setText("Total Items: 0");
        
        // Update invoice preview and items display after clearing
        updateInvoicePreview();
        updateItemsDisplay();
        
        logger.info("🧹 Form cleared, digital payment fields hidden, ScrollPane height reset, and invoice preview reset");
    }
    
    /**
     * Clear only the items from the order, keeping customer information
     */
    private void handleClearItems() {
        currentDynamicOrderItems.clear();
        totalQuantity = 0;
        totalLabel.setText("Total Items: 0");
        
        // Reset item form fields
        categoryCombo.setValue(null);
        flavorCombo.setValue(null);
        quantitySpinner.getValueFactory().setValue(1);
        priceField.setText("₱0.00");
        ensurePriceFieldStyling();
        
        // Update displays
        updateInvoicePreview();
        updateItemsDisplay();
        
        logger.info("🧹 Items cleared, customer information preserved");
    }
    
    /**
     * Update the visual display of current order items
     */
    private void updateItemsDisplay() {
        // Clear existing item displays
        itemInputContainer.getChildren().clear();
        
        // Always show the main input form at the top
        VBox mainInputForm = createMainItemInputForm();
        itemInputContainer.getChildren().add(mainInputForm);
        
        // Show current items if any exist
        if (!currentDynamicOrderItems.isEmpty()) {
            VBox itemsListContainer = createItemsListContainer();
            itemInputContainer.getChildren().add(itemsListContainer);
        }
    }
    
    /**
     * Create the main item input form
     */
    private VBox createMainItemInputForm() {
        VBox mainForm = new VBox(8.0);
        mainForm.setStyle("-fx-background-color: FFFBE9; -fx-background-radius: 4; -fx-padding: 10;");
        
        // Category and Flavor row
        GridPane topGrid = new GridPane();
        topGrid.setHgap(10.0);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50.0);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50.0);
        topGrid.getColumnConstraints().addAll(col1, col2);
        
        // Category section
        VBox categorySection = new VBox(3.0);
        Label categoryLabel = new Label("Category");
        categoryLabel.setTextFill(Color.web("#c24c00"));
        categoryLabel.setFont(Font.font("Inter Medium", 13.0));
        categorySection.getChildren().addAll(categoryLabel, categoryCombo);
        topGrid.add(categorySection, 0, 0);
        
        // Flavor section
        VBox flavorSection = new VBox(3.0);
        Label flavorLabel = new Label("Flavor");
        flavorLabel.setTextFill(Color.web("#c24c00"));
        flavorLabel.setFont(Font.font("Inter Medium", 13.0));
        flavorSection.getChildren().addAll(flavorLabel, flavorCombo);
        topGrid.add(flavorSection, 1, 0);
        
        // Quantity and Price row
        GridPane bottomGrid = new GridPane();
        bottomGrid.setHgap(10.0);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25.0);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25.0);
        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPercentWidth(50.0);
        bottomGrid.getColumnConstraints().addAll(col3, col4, col5);
        
        // Quantity section
        VBox quantitySection = new VBox(3.0);
        Label quantityLabel = new Label("Quantity");
        quantityLabel.setTextFill(Color.web("#c24c00"));
        quantityLabel.setFont(Font.font("Inter Medium", 13.0));
        quantitySection.getChildren().addAll(quantityLabel, quantitySpinner);
        bottomGrid.add(quantitySection, 0, 0);
        
        // Price section
        VBox priceSection = new VBox(3.0);
        Label priceLabel = new Label("Price");
        priceLabel.setTextFill(Color.web("#c24c00"));
        priceLabel.setFont(Font.font("Inter Medium", 13.0));
        priceSection.getChildren().addAll(priceLabel, priceField);
        bottomGrid.add(priceSection, 1, 0);
        
        mainForm.getChildren().addAll(topGrid, bottomGrid);
        return mainForm;
    }
    
    /**
     * Create container showing current items with remove buttons
     */
    private VBox createItemsListContainer() {
        VBox container = new VBox(5.0);
        container.setStyle("-fx-background-color:rgba(194, 78, 0, 0.16); -fx-background-radius: 4; -fx-padding: 10;");
        
        // Header
        HBox header = new HBox(10.0);
        header.setAlignment(Pos.CENTER_LEFT);
        Label headerLabel = new Label("Current Order Items:");
        headerLabel.setTextFill(Color.web("#c24c00"));
        headerLabel.setFont(Font.font("Inter Bold", 14.0));
        
        Button clearItemsButton = new Button("Clear Items");
        clearItemsButton.setStyle("-fx-background-color: #FFE1E1; -fx-text-fill: #A7000F; " +
                                 "-fx-font-size: 11px; -fx-padding: 4px 8px; " +
                                 "-fx-border-radius: 4px; -fx-background-radius: 4px; -fx-border-color: #FF3441");
        clearItemsButton.setOnAction(e -> handleClearItems());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(headerLabel, spacer, clearItemsButton);
        container.getChildren().add(header);
        
        // Items list
        for (int i = 0; i < currentDynamicOrderItems.size(); i++) {
            DynamicOrderItem item = currentDynamicOrderItems.get(i);
            HBox itemRow = createItemRow(item, i);
            container.getChildren().add(itemRow);
        }
        
        return container;
    }
    
    /**
     * Create a row for displaying an individual item with remove button
     */
    private HBox createItemRow(DynamicOrderItem item, int index) {
        HBox row = new HBox(10.0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-background-radius: 4; -fx-padding: 8;");
        
        // Item info
        VBox itemInfo = new VBox(2.0);
        Label nameLabel = new Label(item.getProduct().getName());
        nameLabel.setFont(Font.font("Inter Bold", 13.0));
        nameLabel.setTextFill(Color.web("rgba(194, 78, 0, 0.7)"));
        
        Label detailsLabel = new Label(String.format("Quantity: %d | Price: %s each | Subtotal: ₱%.2f", 
            item.getQuantity(), item.getProduct().getFormattedPrice(), item.getSubtotal()));
        detailsLabel.setFont(Font.font("Inter Regular", 11.0));
        detailsLabel.setTextFill(Color.web("#6B7280"));
        
        itemInfo.getChildren().addAll(nameLabel, detailsLabel);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Remove button
        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #FFE1E1; -fx-text-fill: #A7000F; " +
                             "-fx-font-size: 10px; -fx-padding: 4px 8px; " +
                             "-fx-border-radius: 3px; -fx-background-radius: 3px; -fx-border-color: #FF3441");
        removeButton.setOnAction(e -> handleRemoveItem(index));
        
        row.getChildren().addAll(itemInfo, spacer, removeButton);
        return row;
    }
    
    /**
     * Remove an item from the order
     */
    private void handleRemoveItem(int index) {
        if (index >= 0 && index < currentDynamicOrderItems.size()) {
            DynamicOrderItem removedItem = currentDynamicOrderItems.remove(index);
            totalQuantity -= removedItem.getQuantity();
            totalLabel.setText("Total Items: " + totalQuantity);
            
            // Update displays
            updateInvoicePreview();
            updateItemsDisplay();
            
            logger.info("🗑️ Removed item: " + removedItem.getDetailedDisplayString());
        }
    }

    private void handleCreateOrder() {
        String customerName = customerNameField.getText();
        String contactNumber = contactNumberField.getText();
        String paymentMethod = paymentMethodCombo.getValue();
        String referenceNumber = referenceNumberField.getText();
        String timestamp = timestampField.getText();
        String cashReceivedAmount = cashReceivedField.getText();
        
        // Validate basic fields
        if (customerName.trim().isEmpty() || contactNumber.trim().isEmpty() || paymentMethod == null || currentDynamicOrderItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Order", "Please fill in customer name, contact number, payment method, and add at least one item.");
                return;
            }
        
        // Validate digital payment fields if Maya or GCash is selected
        if (("Maya".equals(paymentMethod) || "GCash".equals(paymentMethod))) {
            if (referenceNumber.trim().isEmpty() || timestamp.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Payment Details", 
                    "Please enter both Reference Number and Timestamp for " + paymentMethod + " payment.\n\n" +
                    "Required timestamp format:\n" +
                    "MM/DD/YYYY H:MM AM/PM\n\n" +
                    "Example: 01/26/2025 2:30 PM");
                return;
            }
            
            // Validate timestamp format
            if (!isValidTimestampFormat(timestamp.trim())) {
                showAlert(Alert.AlertType.WARNING, "Invalid Timestamp Format", 
                    "Please enter the timestamp in the correct format:\n\n" +
                    "Required format: MM/DD/YYYY H:MM AM/PM\n" +
                    "Example: 01/26/2025 2:30 PM\n\n" +
                    "Your input: " + timestamp.trim());
                return;
            }
        }
            
        // Validate cash payment field if Cash is selected
        if ("Cash".equals(paymentMethod)) {
            String cashReceived = cashReceivedField.getText().trim();
            if (cashReceived.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Cash Amount", 
                    "Please enter the cash amount received for Cash payment.");
                return;
            }
            
            // Validate cash amount is a valid number
            try {
                double cashAmount = Double.parseDouble(cashReceived);
                if (cashAmount <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Cash Amount", 
                        "Cash amount must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Invalid Cash Amount", 
                    "Please enter a valid number for cash amount.\n\n" +
                    "Example: 100.00");
                return;
            }
        }
            
        double totalAmount = currentDynamicOrderItems.stream().mapToDouble(DynamicOrderItem::getSubtotal).sum();
        
        // Generate new order ID format: WPYYYYMMDD-00X starting from 001 for each day
        LocalDateTime now = LocalDateTime.now();
        String dateString = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Count orders for today to determine sequence number (start from 1, not 0)
        String todayDatePrefix = "WP" + dateString;
        long todayOrderCount = allOrders.stream()
            .filter(order -> order.getOrderId().startsWith(todayDatePrefix))
            .count();
        
        // Generate order ID starting from 001 for the first order of the day
        String orderId = String.format("%s-%03d", todayDatePrefix, todayOrderCount + 1);
        String itemsOrdered = currentDynamicOrderItems.stream().map(DynamicOrderItem::getDisplayString).collect(Collectors.joining("; "));
        String currentDateTime = now.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.ENGLISH));
        
        // Create order with payment-specific details
        String finalReferenceNumber = referenceNumber.trim();
        String finalTimestampOrCash = timestamp.trim();
        
        // For cash payments, store cash received amount in the timestamp field
        if ("Cash".equals(paymentMethod)) {
            finalReferenceNumber = ""; // No reference number for cash
            finalTimestampOrCash = cashReceivedAmount.trim(); // Store cash received amount
        }
        
        RecentOrder newOrder = new RecentOrder(orderId, customerName, contactNumber, itemsOrdered, String.valueOf(totalQuantity),
            String.format("₱%.2f", totalAmount), paymentMethod, currentDateTime, "Pending", 
            finalReferenceNumber, finalTimestampOrCash);
        
        // Add the new order to the beginning of the in-memory list (most recent first)
        allOrders.add(0, newOrder);
        
        // Save the updated orders list to the file
        boolean saveSuccess = saveOrdersToFile();
        
        if (saveSuccess) {
            logger.info("💾 New order " + orderId + " created and saved to file successfully with status: Pending");
            handleClearForm();
            applyFiltersAsync();
            updatePagination();
            
            // Force table refresh to display the new order immediately
            Platform.runLater(() -> {
                ordersTable.refresh();
            });
            
            showAlert(Alert.AlertType.INFORMATION, "Order Created", 
                "Order " + orderId + " has been created and saved successfully with Pending status!");
        } else {
            // Remove the order from memory if saving failed
            allOrders.remove(newOrder);
            logger.severe("❌ Failed to save new order to file. Order creation cancelled.");
            showAlert(Alert.AlertType.ERROR, "Save Error", 
                "Failed to save the new order to file. Please try again.");
        }
    }
    
    /**
     * Handle view receipt action - opens a new window with actual PDF image preview
     * @param order The order to view receipt for
     */
    private void handleViewReceipt(RecentOrder order) {
        logger.info("📄 Opening receipt preview for order: " + order.getOrderId());
        
        try {
            // Create new stage for receipt preview
            Stage receiptStage = new Stage();
            receiptStage.initModality(Modality.APPLICATION_MODAL);
            receiptStage.setTitle("Receipt Preview - Order " + order.getOrderId());
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
            exportButton.setOnAction(e -> {
                    generatePDFReceipt(order);
                    receiptStage.close();
                });
                
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
                    // Generate PDF document using the same method as invoice preview
                    PDDocument document = generatePDFInMemory(order);
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
                                
                                // Adjust window size to fit content
                                double windowWidth = Math.min(finalImageWidth + 60, maxWindowWidth);
                                double windowHeight = Math.min(finalImageHeight + 140, maxWindowHeight); // Extra space for buttons and info
                                
                                receiptStage.setWidth(windowWidth);
                                receiptStage.setHeight(windowHeight);
                                receiptStage.centerOnScreen();
                                
                                logger.info("✅ Receipt PDF image preview loaded successfully for order: " + order.getOrderId());
                                
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, "❌ Error displaying PDF image in receipt preview", e);
                                Platform.runLater(() -> showReceiptPreviewError(contentContainer));
                            }
                        });
                        
                        // Close the document
                        document.close();
                        
                    } else {
                        Platform.runLater(() -> showReceiptPreviewError(contentContainer));
                    }
                    
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "❌ Error generating PDF image for receipt preview", e);
                    Platform.runLater(() -> showReceiptPreviewError(contentContainer));
                }
            }).start();
                
            logger.info("✅ Receipt preview window opened, generating PDF image...");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error opening receipt preview for order: " + order.getOrderId(), e);
            showAlert(Alert.AlertType.ERROR, "Receipt Preview Error", 
                "Failed to open receipt preview. Please try again.");
        }
    }
    
    /**
     * Show error message when PDF image generation fails in receipt preview
     * @param container The container to add error message to
     */
    private void showReceiptPreviewError(VBox container) {
        container.getChildren().clear();
        Label errorLabel = new Label("Error generating receipt preview");
        errorLabel.setTextFill(Color.web("#EF4444"));
        errorLabel.setFont(Font.font("Inter Regular", 13));
        container.getChildren().add(errorLabel);
        container.setAlignment(Pos.CENTER);
    }

    private void applyFilters() {
        if (isFilteringInProgress) {
            logger.info("🔄 Filtering already in progress, skipping duplicate request");
            return;
        }
        
        try {
            isFilteringInProgress = true;
            logger.info("🔍 Applying filters to " + allOrders.size() + " orders");
            
            // Ensure we're on the JavaFX Application Thread
            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(this::applyFilters);
                return;
            }
            
            filteredOrders.clear();
            List<RecentOrder> tempFilteredList = allOrders.stream()
                .filter(this::matchesStatusFilter)
                .filter(this::matchesSearchFilter)
                .sorted(this::compareOrders)
                .collect(Collectors.toList());
            
            filteredOrders.addAll(tempFilteredList);
            currentPage = 1;
            updateTableView();
            updatePagination();
            
            logger.info("✅ Filtering completed: " + filteredOrders.size() + " orders match criteria");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error during filtering", e);
        } finally {
            isFilteringInProgress = false;
        }
    }
    
    /**
     * Asynchronous filter application to prevent UI freezing
     */
    private void applyFiltersAsync() {
        if (isFilteringInProgress) {
            return;
        }
        
        Platform.runLater(() -> {
            try {
                applyFilters();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "❌ Error in async filtering", e);
                isFilteringInProgress = false;
            }
        });
    }
    
    /**
     * Setup debounced search to prevent filter triggering on every keystroke
     */
    private void setupDebouncedSearch() {
        // Initialize debouncer with 300ms delay
        searchDebouncer = new PauseTransition(Duration.millis(300));
        searchDebouncer.setOnFinished(event -> {
            String currentSearchText = searchField.getText();
            if (!Objects.equals(lastSearchText.get(), currentSearchText)) {
                lastSearchText.set(currentSearchText);
                applyFiltersAsync();
            }
        });
        
        // Add listener to search field with debouncing
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Stop any pending filter operation
            searchDebouncer.stop();
            // Start new debounced filter operation
            searchDebouncer.play();
        });
        
        logger.info("🔍 Debounced search filtering configured with 300ms delay");
    }

    private boolean matchesStatusFilter(RecentOrder order) {
        if (order == null) return false;
        
        try {
            // Check order status filter
            String selectedOrderStatus = orderStatusFilter.getValue();
            if (selectedOrderStatus == null || "All".equals(selectedOrderStatus)) {
                return true;
            }
            
            String orderStatus = order.getOrderStatus();
            return orderStatus != null && orderStatus.equals(selectedOrderStatus);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error matching status filter for order: " + 
                      (order != null ? order.getOrderId() : "null"), e);
            return false;
        }
    }

    // Date filter functionality removed

    private boolean matchesSearchFilter(RecentOrder order) {
        if (order == null) return false;
        
        try {
            String searchText = searchField.getText();
            if (searchText == null || searchText.trim().isEmpty()) return true;
            
            String searchLower = searchText.toLowerCase().trim();
            
            // Check order ID
            String orderId = order.getOrderId();
            if (orderId != null && orderId.toLowerCase().contains(searchLower)) {
                return true;
            }
            
            // Check customer name
            String name = order.getName();
            if (name != null && name.toLowerCase().contains(searchLower)) {
                return true;
            }
            
            // Check items ordered
            String itemsOrdered = order.getItemsOrdered();
            if (itemsOrdered != null && itemsOrdered.toLowerCase().contains(searchLower)) {
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error matching search filter for order: " + 
                      (order != null ? order.getOrderId() : "null"), e);
            return false;
        }
    }

    private int compareOrders(RecentOrder o1, RecentOrder o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return 1;
        if (o2 == null) return -1;
        
        try {
            String sortBy = sortFilterCombo.getValue();
            if (sortBy == null) return 0;
            
            switch (sortBy) {
                case "Date (Newest First)": 
                    return parseOrderDate(o2.getOrderDate()).compareTo(parseOrderDate(o1.getOrderDate()));
                case "Date (Oldest First)": 
                    return parseOrderDate(o1.getOrderDate()).compareTo(parseOrderDate(o2.getOrderDate()));
                case "Name (A-Z)": 
                    String name1 = o1.getName();
                    String name2 = o2.getName();
                    if (name1 == null && name2 == null) return 0;
                    if (name1 == null) return 1;
                    if (name2 == null) return -1;
                    return name1.compareToIgnoreCase(name2);
                case "Name (Z-A)": 
                    String nameZ1 = o1.getName();
                    String nameZ2 = o2.getName();
                    if (nameZ1 == null && nameZ2 == null) return 0;
                    if (nameZ1 == null) return 1;
                    if (nameZ2 == null) return -1;
                    return nameZ2.compareToIgnoreCase(nameZ1);
                case "Amount (High to Low)": 
                    return Double.compare(parseAmount(o2.getTotalAmount()), parseAmount(o1.getTotalAmount()));
                case "Amount (Low to High)": 
                    return Double.compare(parseAmount(o1.getTotalAmount()), parseAmount(o2.getTotalAmount()));
                case "Status": 
                    String status1 = o1.getStatus();
                    String status2 = o2.getStatus();
                    if (status1 == null && status2 == null) return 0;
                    if (status1 == null) return 1;
                    if (status2 == null) return -1;
                    return status1.compareToIgnoreCase(status2);
                default: return 0;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error comparing orders: " + 
                      (o1 != null ? o1.getOrderId() : "null") + " vs " + 
                      (o2 != null ? o2.getOrderId() : "null"), e);
            return 0;
        }
    }
    
    /**
     * Parse order date from various formats including "Jan 26, 2025 3:45 PM" and new MM/DD/YYYY format
     * @param dateTimeStr The date string to parse
     * @return LocalDate if parsing successful, current date as fallback
     */
    private LocalDate parseOrderDate(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return LocalDate.now();
        
        try {
            String cleanDateStr = dateTimeStr.trim();
            
            // Handle MM/DD/YYYY H:MM AM/PM format (new standard)
            if (cleanDateStr.matches("\\d{2}/\\d{2}/\\d{4} \\d{1,2}:\\d{2} [AP]M")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.ENGLISH);
                return LocalDateTime.parse(cleanDateStr, formatter).toLocalDate();
            }
            
            // Handle different legacy date formats for backward compatibility
            if (cleanDateStr.contains("-")) {
                // Format: 2025-01-26 or 2025-01-26 15:45:00
                return LocalDate.parse(cleanDateStr.substring(0, 10));
            } else if (cleanDateStr.contains(",")) {
                // Format: "Jan 26, 2025 3:45 PM" or "Jan 26, 2025"
                DateTimeFormatter[] formatters = {
                    DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.ENGLISH),  // "Jan 26, 2025 3:45 PM"
                    DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a", Locale.ENGLISH), // "Jan 26, 2025 3:45 PM"
                    DateTimeFormatter.ofPattern("MMM d, yyyy H:mm a", Locale.ENGLISH),  // "Jan 26, 2025 15:45 PM"
                    DateTimeFormatter.ofPattern("MMM dd, yyyy H:mm a", Locale.ENGLISH), // "Jan 26, 2025 15:45 PM"
                    DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH),         // "Jan 26, 2025"
                    DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)         // "Jan 26, 2025"
                };
                
                for (DateTimeFormatter formatter : formatters) {
                    try {
                        if (formatter.toString().contains("h:mm") || formatter.toString().contains("H:mm")) {
                            return LocalDateTime.parse(cleanDateStr, formatter).toLocalDate();
                        } else {
                            return LocalDate.parse(cleanDateStr, formatter);
                        }
                    } catch (Exception ignored) {
                        // Try next formatter
                    }
                }
            }
            
            logger.warning("⚠️ Could not parse date format: " + dateTimeStr);
        } catch (Exception e) {
            logger.warning("⚠️ Error parsing date: " + dateTimeStr + " - " + e.getMessage());
        }
        
        return LocalDate.now(); // Fallback to current date
    }

    private double parseAmount(String amountStr) {
        try {
            return Double.parseDouble(amountStr.replace("₱", "").replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void clearAllFilters() {
        try {
            logger.info("🧹 Clearing all filters");
            
            // Stop any pending search debouncer
            if (searchDebouncer != null) {
                searchDebouncer.stop();
            }
            
            // Reset filter values
            orderStatusFilter.setValue("All");
            searchField.clear();
            sortFilterCombo.setValue("Date (Newest First)");
            lastSearchText.set("");
            
            // Apply filters asynchronously
            applyFiltersAsync();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error clearing filters", e);
        }
    }
    
    private void updateTableView() {
        try {
            // Ensure we're on the JavaFX Application Thread
            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(this::updateTableView);
                return;
            }
            
            int startIndex = (currentPage - 1) * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, filteredOrders.size());
            
            if (startIndex < filteredOrders.size() && startIndex >= 0) {
                List<RecentOrder> pageData = new ArrayList<>(
                    filteredOrders.subList(startIndex, endIndex)
                );
                ordersTable.setItems(FXCollections.observableArrayList(pageData));
            } else {
                ordersTable.setItems(FXCollections.observableArrayList());
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "❌ Error updating table view", e);
            // Fallback to empty table
            Platform.runLater(() -> ordersTable.setItems(FXCollections.observableArrayList()));
        }
    }

    private void updatePagination() {
        try {
            // Ensure we're on the JavaFX Application Thread
            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(this::updatePagination);
                return;
            }
            
            int totalPages = (int) Math.ceil((double) filteredOrders.size() / itemsPerPage);
            totalPages = Math.max(1, totalPages);
            
            // Ensure current page is within bounds
            if (currentPage > totalPages) {
                currentPage = totalPages;
            }
            if (currentPage < 1) {
                currentPage = 1;
            }
            
            pageLabel.setText("Page " + currentPage + " of " + totalPages);
            prevPageBtn.setDisable(currentPage <= 1);
            nextPageBtn.setDisable(currentPage >= totalPages);
            
            int startIndex = (currentPage - 1) * itemsPerPage + 1;
            int endIndex = Math.min(currentPage * itemsPerPage, filteredOrders.size());
            
            if (filteredOrders.isEmpty()) {
                resultsInfoLabel.setText("No orders found");
            } else {
                resultsInfoLabel.setText("Showing " + startIndex + "-" + endIndex + " of " + filteredOrders.size() + " orders");
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "❌ Error updating pagination", e);
        }
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            updateTableView();
            updatePagination();
        }
    }
    
    @FXML
    private void handleNextPage() {
        int totalPages = (int) Math.ceil((double) filteredOrders.size() / itemsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            updateTableView();
            updatePagination();
        }
    }
    
    /**
     * Generate PDF receipt for an order and save to Downloads folder
     * @param order The order to generate PDF for
     */
    private void generatePDFReceipt(RecentOrder order) {
        logger.info("📄 Generating receipt for order: " + order.getOrderId());
        
        try {
            // Create new PDF document with custom page size
            PDDocument document = new PDDocument();
            
            // Custom page size: 3.15 inches width (227 points), auto-height
            float pageWidth = 3.15f * 72; // 3.15 inches * 72 points per inch = 227 points
            
            // Initialize fonts
            PDType1Font courierRegular = new PDType1Font(Standard14Fonts.FontName.COURIER);
            PDType1Font courierBold = new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
            
            // Calculate separator dimensions that will be used throughout
            float separatorWidth = courierRegular.getStringWidth(RECEIPT_SEPARATOR) / 1000 * 9f;
            float separatorX = (pageWidth - separatorWidth) / 2;
            
            // Calculate actual height needed based on content
        String[] items = order.getItemsOrdered().split(";");
            int itemCount = items.length;
            float lineHeight = 12f; // Line height for compact receipt
            
            // Calculate total lines needed - updated calculation without the removed content
            int headerLines = 12; // Updated for new header format
            int orderDetailLines = 4; // Order ID, Date, Customer details
            int itemHeaderLines = 3; // Separator + "Items Ordered" + spacing
            int itemLines = itemCount;
            int totalSectionLines = 3; // First separator + TOTAL line + second separator
            int digitalPaymentLines = 0;
            int cashPaymentLines = 0;
            int thankYouLines = 4; // Bottom separator + thank you message (3 lines) - reduced spacing
            
            // Add cash payment lines if applicable
            if ("Cash".equals(order.getPaymentMethod())) {
                cashPaymentLines = 3; // Paid By, Cash amount, Change lines
            }
            
            // Add digital payment lines if applicable (handled in main payment section now)
            if (("Maya".equals(order.getPaymentMethod()) || "GCash".equals(order.getPaymentMethod()))) {
                digitalPaymentLines = 3; // Paid By, Reference Number, Timestamp lines
            }
            
            // Calculate total height with proper margins - removed the totalLines that included unwanted content
            float totalHeight = (headerLines + orderDetailLines + itemHeaderLines + itemLines + totalSectionLines + cashPaymentLines + digitalPaymentLines + thankYouLines) * lineHeight + 80; // Reduced margin for better spacing balance
            
            // Create page with calculated dimensions
            PDPage page = new PDPage();
            page.setMediaBox(new PDRectangle(pageWidth, totalHeight));
            document.addPage(page);
            
            // Create content stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Start from top with margin
            float topMargin = 20f;
            float bottomMargin = 20f; // Add bottom margin for consistency
            float startY = totalHeight - topMargin;
            float yPosition = startY;
            
            contentStream.beginText();
            
            // Header 1 - "RECEIPT OF SALE" (15px, centered, Courier regular)
            contentStream.setFont(courierRegular, 15f);
            String receiptHeader = "RECEIPT OF SALE";
            float receiptHeaderWidth = courierRegular.getStringWidth(receiptHeader) / 1000 * 15f;
            float receiptHeaderX = (pageWidth - receiptHeaderWidth) / 2;
            contentStream.newLineAtOffset(receiptHeaderX, yPosition);
            contentStream.showText(receiptHeader);
            yPosition -= 15f; // Spacing after RECEIPT OF SALE
            
            // Header 2 - "WONDERPOFFLES" (18px, centered, Courier Bold)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 18f);
            String companyName = "WONDERPOFFLES";
            float companyNameWidth = courierBold.getStringWidth(companyName) / 1000 * 18f;
            float companyNameX = (pageWidth - companyNameWidth) / 2;
            contentStream.newLineAtOffset(companyNameX, yPosition);
            contentStream.showText(companyName);
            yPosition -= 40f; // Increased spacing after company name
            
            // Address Line 1 - "Address: 43 Bassig Street, Ugac" (10px, centered, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String addressLine1 = "Address: 43 Bassig Street, Ugac";
            float addressLine1Width = courierRegular.getStringWidth(addressLine1) / 1000 * 10f;
            float addressLine1X = (pageWidth - addressLine1Width) / 2;
            contentStream.newLineAtOffset(addressLine1X, yPosition);
            contentStream.showText(addressLine1);
            yPosition -= 12f;
            
            // Address Line 2 - "Sur, Tuguegarao City, Cagayan" (10px, centered, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String addressLine2 = "Sur, Tuguegarao City, Cagayan";
            float addressLine2Width = courierRegular.getStringWidth(addressLine2) / 1000 * 10f;
            float addressLine2X = (pageWidth - addressLine2Width) / 2;
            contentStream.newLineAtOffset(addressLine2X, yPosition);
            contentStream.showText(addressLine2);
            yPosition -= 12f;
            
            // Contact - "Contact #: 0975-825-6553" (10px, centered, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String contactInfo = "Contact #: 0975-825-6553";
            float contactInfoWidth = courierRegular.getStringWidth(contactInfo) / 1000 * 10f;
            float contactInfoX = (pageWidth - contactInfoWidth) / 2;
            contentStream.newLineAtOffset(contactInfoX, yPosition);
            contentStream.showText(contactInfo);
            yPosition -= 25f; // Increased spacing after contact info
            
            // Separator line
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= lineHeight * 1.5f;
            
            // Add 10f spacing after separator
            yPosition -= 10f;
            
            // Order ID (centered, 13px, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 13f);
            String orderIdText = "Order ID: " + order.getOrderId();
            float orderIdWidth = courierRegular.getStringWidth(orderIdText) / 1000 * 13f;
            float orderIdX = (pageWidth - orderIdWidth) / 2;
            contentStream.newLineAtOffset(orderIdX, yPosition);
            contentStream.showText(orderIdText);
            yPosition -= 15f; // 15px spacing after Order ID
            
            // Date and time (centered, 13px, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 13f);
            String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy   hh:mm a"));
            float dateTimeWidth = courierRegular.getStringWidth(currentDateTime) / 1000 * 13f;
            float dateTimeX = (pageWidth - dateTimeWidth) / 2;
            contentStream.newLineAtOffset(dateTimeX, yPosition);
            contentStream.showText(currentDateTime);
            yPosition -= 30f; // 30px spacing after date/time
            
            // Table setup - Define column positions for 3.15" width (227 points)
            float leftMargin = 10f;
            float rightMargin = 10f;
            float availableWidth = pageWidth - leftMargin - rightMargin; // 207 points available
            
            // Column widths: QTY (30), NAME (130), AMT (47)
            float qtyColWidth = 30f;
            float nameColWidth = 130f; 
            float amtColWidth = 47f;
            
            // Column positions
            float qtyColX = leftMargin;
            float nameColX = qtyColX + qtyColWidth;
            float amtColX = nameColX + nameColWidth;
            
            // Table header row
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            
            // QTY header (left aligned)
            contentStream.newLineAtOffset(qtyColX, yPosition);
            contentStream.showText("QTY");
            
            // NAME header (left aligned)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            contentStream.newLineAtOffset(nameColX, yPosition);
            contentStream.showText("NAME");
            
            // AMT header (right aligned)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String amtHeader = "AMT";
            float amtHeaderWidth = courierRegular.getStringWidth(amtHeader) / 1000 * 10f;
            contentStream.newLineAtOffset(amtColX + amtColWidth - amtHeaderWidth, yPosition);
            contentStream.showText(amtHeader);
            
            yPosition -= 12f; // Move down after header
            
            // Horizontal separator between header and data
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 10f; // 10px spacing after separator
            
            // Parse and display items in table format with correct pricing
            for (String item : items) {
                // Use helper method to get correct pricing
                Object[] itemDetails = parseItemWithPrice(item);
                String qty = (String) itemDetails[0];
                String name = (String) itemDetails[1];
                double unitPrice = (Double) itemDetails[2];
                double totalPrice = (Double) itemDetails[3];
                
                // Truncate name if too long (max 18 characters for narrow receipt)
                if (name.length() > 18) {
                    name = name.substring(0, 18);
                }
                
                // QTY column (left aligned)
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(qtyColX, yPosition);
                contentStream.showText(qty);
                
                // NAME column (left aligned)
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(nameColX, yPosition);
                contentStream.showText(name);
                
                // AMT column (right aligned)
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                String amtText = String.format("%.2f", totalPrice);
                float amtTextWidth = courierRegular.getStringWidth(amtText) / 1000 * 10f;
                contentStream.newLineAtOffset(amtColX + amtColWidth - amtTextWidth, yPosition);
                contentStream.showText(amtText);
                
                yPosition -= 15f; // 15px spacing per row
            }
            
            yPosition -= 15f; // 15f spacing after table
            
            // First separator after table
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 20f; // 20px spacing after separator
            
            // TOTAL section - "TOTAL" left aligned, amount right aligned
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 18f);
            
            // Calculate total amount from items using actual product prices
            double calculatedTotal = calculateOrderTotal(order);
            
            // "TOTAL" left aligned to left edge
            contentStream.newLineAtOffset(leftMargin, yPosition);
            contentStream.showText("TOTAL");
            
            // Total amount right aligned to right edge
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 18f);
            String totalAmountText = String.format("PHP %.2f", calculatedTotal);
            float totalAmountWidth = courierBold.getStringWidth(totalAmountText) / 1000 * 18f;
            contentStream.newLineAtOffset(pageWidth - rightMargin - totalAmountWidth, yPosition);
            contentStream.showText(totalAmountText);
            yPosition -= 15f; // 15px spacing after TOTAL
            
            // Second separator after TOTAL
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 25f; // 25px spacing after separator
            
            // Payment details section (Cash or Digital payment)
            if ("Cash".equals(order.getPaymentMethod())) {
                // "Paid By:" left aligned, "Cash" right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Paid By:");
                
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                String cashText = "Cash";
                float cashTextWidth = courierRegular.getStringWidth(cashText) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - cashTextWidth, yPosition);
                contentStream.showText(cashText);
                yPosition -= 15f; // 15px spacing
                
                // "Cash:" left aligned, cash amount right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Cash Received:");
                
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                // Use the actual cash received amount from the order data (stored in timestamp field for cash payments)
                double cashAmount;
                try {
                    cashAmount = Double.parseDouble(order.getTimestamp());
                } catch (NumberFormatException e) {
                    cashAmount = 0.0; // Default to 0.0 when empty - no random numbers
                }
                String cashAmountText = String.format("PHP %.2f", cashAmount);
                float cashAmountWidth = courierRegular.getStringWidth(cashAmountText) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - cashAmountWidth, yPosition);
                contentStream.showText(cashAmountText);
                yPosition -= 15f; // 15px spacing
                
                // "Change:" left aligned, change amount right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Change:");
                
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                double changeAmount = cashAmount - calculatedTotal;
                String changeAmountText = String.format("PHP %.2f", changeAmount);
                float changeAmountWidth = courierRegular.getStringWidth(changeAmountText) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - changeAmountWidth, yPosition);
                contentStream.showText(changeAmountText);
                yPosition -= 25f; // 25px spacing after cash details
            } else if ("GCash".equals(order.getPaymentMethod()) || "Maya".equals(order.getPaymentMethod())) {
                // E-wallet payment format (GCash/Maya)
                // "Paid By:" left aligned, "GCash" or "Maya" right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Paid By:");
                
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                String paymentMethodText = order.getPaymentMethod(); // "GCash" or "Maya"
                float paymentMethodWidth = courierRegular.getStringWidth(paymentMethodText) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - paymentMethodWidth, yPosition);
                contentStream.showText(paymentMethodText);
                yPosition -= 15f; // 15px spacing
                
                // "Reference Number:" left aligned, reference number right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Reference Number:");
                
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.setFont(courierRegular, 10f);
                String referenceNumber = order.getReferenceNumber().isEmpty() ? "N/A" : order.getReferenceNumber();
                float referenceNumberWidth = courierRegular.getStringWidth(referenceNumber) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - referenceNumberWidth, yPosition);
                contentStream.showText(referenceNumber);
                yPosition -= 15f; // 15px spacing
                
                // Timestamp left aligned (no right side content)
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.setFont(courierRegular, 10f);
                        contentStream.newLineAtOffset(leftMargin, yPosition);
                String timestamp = order.getTimestamp().isEmpty() ? "N/A" : order.getTimestamp();
                contentStream.showText(timestamp);
                yPosition -= 25f; // 25px spacing after e-wallet details
            } else {
                yPosition -= lineHeight * 1.5f; // Normal spacing for other payment methods
            }
            
            
            // Bottom separator
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 15f; // Reduced spacing after separator for more compact layout
            
            // Thank you message - customized format
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 8f);
            
            // First line: "Thank you for choosing WonderPoffles!"
            String thankYouLine1 = "Thank you for choosing WonderPoffles!";
            float thankYouLine1Width = courierRegular.getStringWidth(thankYouLine1) / 1000 * 8f;
            float thankYouLine1X = (pageWidth - thankYouLine1Width) / 2;
            contentStream.newLineAtOffset(thankYouLine1X, yPosition);
            contentStream.showText(thankYouLine1);
            yPosition -= 12f; // 12px spacing after first line
            
            // Second line: "We hope every bite made your day a"
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 8f);
            String thankYouLine2 = "We hope every bite made your day a";
            float thankYouLine2Width = courierRegular.getStringWidth(thankYouLine2) / 1000 * 8f;
            float thankYouLine2X = (pageWidth - thankYouLine2Width) / 2;
            contentStream.newLineAtOffset(thankYouLine2X, yPosition);
            contentStream.showText(thankYouLine2);
            yPosition -= 12f; // 12px spacing after second line
            
            // Third line: "little brighter."
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 8f);
            String thankYouLine3 = "little brighter.";
            float thankYouLine3Width = courierRegular.getStringWidth(thankYouLine3) / 1000 * 8f;
            float thankYouLine3X = (pageWidth - thankYouLine3Width) / 2;
            contentStream.newLineAtOffset(thankYouLine3X, yPosition);
            contentStream.showText(thankYouLine3);
            // Remove the extra bottom margin - height calculation already accounts for proper spacing
            
            contentStream.close();
            
            // Save to Downloads folder
            String userHome = System.getProperty("user.home");
            String downloadsPath = userHome + "/Downloads";
            String fileName = "Order_" + order.getOrderId() + "_Receipt.pdf";
            String fullPath = downloadsPath + "/" + fileName;
            
            document.save(fullPath);
            document.close();
            
            logger.info("✅ Receipt generated successfully: " + fullPath);
            
            // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Receipt Generated", 
                "Receipt has been saved to Downloads folder:\n" + fileName);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error generating receipt for order: " + order.getOrderId(), e);
                showAlert(Alert.AlertType.ERROR, "PDF Generation Error", 
                "Failed to generate receipt. Please try again.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        });
    }

    private void showPDFPreviewError() {
        Platform.runLater(() -> {
            invoicePreviewContent.getChildren().clear();
            Label errorLabel = new Label("Error generating PDF preview");
            errorLabel.setTextFill(Color.web("#EF4444"));
            errorLabel.setFont(Font.font("Inter Regular", 13));
            invoicePreviewContent.getChildren().add(errorLabel);
            invoicePreviewContent.setAlignment(Pos.CENTER);
        });
    }

    /**
     * Setup control buttons for invoice preview
     */
    private void setupInvoicePreviewControls() {
        // **Ensure buttons are always visible and managed**
        invoiceControlButtons.setVisible(true);
        invoiceControlButtons.setManaged(true);
        
        // Configure button actions - buttons are now always visible
        zoomInButton.setOnAction(e -> zoomInvoicePreview(1.25));
        zoomOutButton.setOnAction(e -> zoomInvoicePreview(0.8));
        pageViewButton.setOnAction(e -> resetToPageView());
        viewFullImageButton.setOnAction(e -> showInvoiceImageViewer());
        
        // **Initialize button states based on current zoom level**
        if (currentPreviewImageView != null) {
            double scrollPaneWidth = invoiceScrollPane.getWidth();
            double exactMinZoom = scrollPaneWidth > 0 ? scrollPaneWidth / originalImageWidth : 1.0;
            updateZoomButtonStates(exactMinZoom);
        }
        
        logger.info("🔧 Invoice preview controls configured: zoom in/out, page view, view full image");
    }
    
    /**
     * Setup panning functionality for the image view
     * @param imageView The ImageView to enable panning on
     */
    private void setupImageViewPanning(ImageView imageView) {
        logger.info("🖱️ Setting up panning functionality for image view");
        
        // **Mouse pressed - start panning**
        imageView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                isPanning = true;
                lastPanX = event.getSceneX();
                lastPanY = event.getSceneY();
                imageView.setCursor(javafx.scene.Cursor.CLOSED_HAND);
                logger.info("🖱️ Started panning at: " + lastPanX + ", " + lastPanY);
            }
        });
        
        // **Mouse dragged - perform panning**
        imageView.setOnMouseDragged(event -> {
            if (isPanning && event.isPrimaryButtonDown()) {
                double deltaX = event.getSceneX() - lastPanX;
                double deltaY = event.getSceneY() - lastPanY;
                
                // Update scroll pane position for panning effect
                double newHValue = invoiceScrollPane.getHvalue() - (deltaX / invoiceScrollPane.getContent().getBoundsInLocal().getWidth());
                double newVValue = invoiceScrollPane.getVvalue() - (deltaY / invoiceScrollPane.getContent().getBoundsInLocal().getHeight());
                
                // Clamp values between 0 and 1
                newHValue = Math.max(0, Math.min(1, newHValue));
                newVValue = Math.max(0, Math.min(1, newVValue));
                
                invoiceScrollPane.setHvalue(newHValue);
                invoiceScrollPane.setVvalue(newVValue);
                
                lastPanX = event.getSceneX();
                lastPanY = event.getSceneY();
            }
        });
        
        // **Mouse released - stop panning**
        imageView.setOnMouseReleased(event -> {
            if (isPanning) {
                isPanning = false;
                imageView.setCursor(javafx.scene.Cursor.DEFAULT);
                logger.info("🖱️ Stopped panning");
            }
        });
        
        // **Mouse entered - show hand cursor when hovering**
        imageView.setOnMouseEntered(event -> {
            if (!isPanning) {
                imageView.setCursor(javafx.scene.Cursor.OPEN_HAND);
            }
        });
        
        // **Mouse exited - restore default cursor**
        imageView.setOnMouseExited(event -> {
            if (!isPanning) {
                imageView.setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });
        
        logger.info("✅ Panning functionality enabled for image view");
    }
    
    /**
     * Zoom the invoice preview by the specified factor
     * @param zoomFactor The zoom factor (>1 to zoom in, <1 to zoom out)
     */
    private void zoomInvoicePreview(double zoomFactor) {
        if (currentPreviewImageView == null) {
            logger.info("🔍 No image to zoom - buttons are still functional");
            return;
        }
        
        // Calculate new zoom level
        double newZoomLevel = currentZoomLevel * zoomFactor;
        
        // **Calculate exact minimum zoom where image width equals container width**
        double scrollPaneWidth = invoiceScrollPane.getWidth();
        double exactMinZoom = scrollPaneWidth > 0 ? scrollPaneWidth / originalImageWidth : 1.0;
        
        // **Limit zoom range - cannot go below container width size**
        newZoomLevel = Math.max(exactMinZoom, Math.min(5.0, newZoomLevel));
        
        if (newZoomLevel != currentZoomLevel) {
            currentZoomLevel = newZoomLevel;
            
            // Apply new size to the image view only
            double newWidth = originalImageWidth * currentZoomLevel;
            double newHeight = originalImageHeight * currentZoomLevel;
            
            currentPreviewImageView.setFitWidth(newWidth);
            currentPreviewImageView.setFitHeight(newHeight);
            
            // Keep parent containers stable - don't resize them based on image size
            // This prevents the UI from moving around when zooming
            
            // **Update button states based on zoom level**
            updateZoomButtonStates(exactMinZoom);
            
            logger.info("🔍 Invoice preview zoomed to " + String.format("%.1f", currentZoomLevel * 100) + "% (min: " + String.format("%.1f", exactMinZoom * 100) + "%)");
        } else {
            logger.info("🔍 Zoom level limited - cannot zoom out beyond page width");
        }
    }
    
    /**
     * Reset invoice preview zoom to original size
     */
    private void resetInvoicePreviewZoom() {
        if (currentPreviewImageView == null) {
            logger.info("🔄 No image to reset - buttons are still functional");
            return;
        }
        
        currentZoomLevel = 1.0;
        
        // Reset to original size
        currentPreviewImageView.setFitWidth(originalImageWidth);
        currentPreviewImageView.setFitHeight(originalImageHeight);
        
        // Keep parent containers stable - don't resize them based on image size
        
        // Reset scroll position to center
        Platform.runLater(() -> {
            invoiceScrollPane.setHvalue(0.5);
            invoiceScrollPane.setVvalue(0.5);
        });
        
        logger.info("🔄 Invoice preview zoom reset to 100%");
    }
    
    /**
     * Show invoice image in a separate viewer window
     */
    private void showInvoiceImageViewer() {
        if (currentPreviewImageView == null) {
            logger.info("🖼️ No image to view - button is still functional");
            showAlert(Alert.AlertType.INFORMATION, "No Preview Available", 
                "Add items to your order to see the invoice preview.");
            return;
        }
        
        try {
            // Create image viewer stage
            Stage imageViewerStage = new Stage();
            imageViewerStage.initModality(Modality.APPLICATION_MODAL);
            imageViewerStage.setTitle("Invoice Preview - Full Image");
            
            // Clone the current image
            Image currentImage = currentPreviewImageView.getImage();
            ImageView imageView = new ImageView(currentImage);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            
            // Calculate window size based on screen dimensions
            double imageWidth = currentImage.getWidth();
            double imageHeight = currentImage.getHeight();
            
            // Set reasonable window size (max 85% of screen)
            double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();
            
            double maxWindowWidth = screenWidth * 0.85;
            double maxWindowHeight = screenHeight * 0.85;
            
            // **Calculate scale to fit the image perfectly in window without scroll bars**
            double scaleX = maxWindowWidth / imageWidth;
            double scaleY = maxWindowHeight / imageHeight;
            double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio
            
            // Calculate final dimensions
            double finalImageWidth = imageWidth * scale;
            double finalImageHeight = imageHeight * scale;
            
            // Set the image size to fit perfectly
            imageView.setFitWidth(finalImageWidth);
            imageView.setFitHeight(finalImageHeight);
            
            // **Create a simple container without scroll bars**
            StackPane imageContainer = new StackPane();
            imageContainer.setAlignment(Pos.CENTER);
            imageContainer.getChildren().add(imageView);
            imageContainer.setStyle("-fx-background-color: white; -fx-padding: 20;");
            
            // Set container size to exactly fit the image plus padding
            double windowWidth = finalImageWidth + 40; // 20px padding on each side
            double windowHeight = finalImageHeight + 40; // 20px padding on top and bottom
            
            // Create scene without scroll pane - direct display
            Scene imageScene = new Scene(imageContainer, windowWidth, windowHeight);
            imageScene.setFill(Color.WHITE);
            
            imageViewerStage.setScene(imageScene);
            imageViewerStage.setResizable(true);
            imageViewerStage.show();
            
            logger.info("🖼️ Full image viewer opened - " + 
                String.format("%.0fx%.0f", finalImageWidth, finalImageHeight) + 
                " (scale: " + String.format("%.1f", scale * 100) + "%)");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error opening image viewer", e);
            showAlert(Alert.AlertType.ERROR, "Image Viewer Error", 
                "Failed to open image viewer. Please try again.");
        }
    }

    /**
     * Reset to page view - fit image width exactly to container width and center-top position
     */
    private void resetToPageView() {
        if (currentPreviewImageView == null) {
            logger.info("🔄 No image to reset - button is still functional");
            return;
        }
        
        // **Set zoom level to exactly match container width**
        double scrollPaneWidth = invoiceScrollPane.getWidth();
        if (scrollPaneWidth > 0) {
            // Calculate exact zoom to match container width
            double exactWidthZoom = scrollPaneWidth / originalImageWidth;
            currentZoomLevel = exactWidthZoom;
            
            // Apply the exact width sizing
            double newWidth = originalImageWidth * currentZoomLevel;
            double newHeight = originalImageHeight * currentZoomLevel;
            
            currentPreviewImageView.setFitWidth(newWidth);
            currentPreviewImageView.setFitHeight(newHeight);
            
            // Keep parent containers stable - don't resize them based on image size
            
            // **Position scroll to center horizontally and top vertically**
            Platform.runLater(() -> {
                // Center horizontally (0.5 = center), top vertically (0.0 = top)
                invoiceScrollPane.setHvalue(0.5); // Center horizontally
                invoiceScrollPane.setVvalue(0.0); // Top vertically
            });
            
            // Update button states
            updateZoomButtonStates(exactWidthZoom);
        } else {
            // Fallback to 1.0 zoom if container width not available
            currentZoomLevel = 1.0;
            currentPreviewImageView.setFitWidth(originalImageWidth);
            currentPreviewImageView.setFitHeight(originalImageHeight);
        }
        
        logger.info("🔄 Page view applied - exact width zoom: " + String.format("%.1f", currentZoomLevel * 100) + "%, positioned at center-top");
    }

    /**
     * Update zoom button states based on current zoom level
     * @param minZoom The minimum allowed zoom level
     */
    private void updateZoomButtonStates(double minZoom) {
        if (zoomOutButton != null) {
            // **Disable zoom out button when at minimum zoom level**
            boolean canZoomOut = currentZoomLevel > minZoom + 0.01; // Small tolerance for floating point comparison
            zoomOutButton.setDisable(!canZoomOut);
            
            if (!canZoomOut) {
                zoomOutButton.setOpacity(0.5); // Visual indication that it's disabled
                logger.info("🔒 Zoom out button disabled - at minimum zoom level");
            } else {
                zoomOutButton.setOpacity(1.0); // Fully visible when enabled
            }
        }
        
        // Zoom in button should always be enabled (unless at max zoom)
        if (zoomInButton != null) {
            boolean canZoomIn = currentZoomLevel < 5.0 - 0.01; // Small tolerance
            zoomInButton.setDisable(!canZoomIn);
            zoomInButton.setOpacity(canZoomIn ? 1.0 : 0.5);
        }
    }

    /**
     * Initialize the invoice preview with PDF rendering capability
     */
    private void initializeInvoicePreview() {
        logger.info("📄 Initializing invoice preview...");
        
        // Configure the VBox styling without forced growth and no extra padding
        invoicePreviewContent.setAlignment(Pos.CENTER);
        invoicePreviewContent.setStyle("-fx-background-color: white; -fx-padding: 0; -fx-spacing: 0;");
        
        // Set the preview content to match available space without creating empty areas
        invoicePreviewContent.setMinWidth(0);
        invoicePreviewContent.setMinHeight(0);
        invoicePreviewContent.setPrefWidth(Region.USE_COMPUTED_SIZE);
        invoicePreviewContent.setPrefHeight(Region.USE_COMPUTED_SIZE);
        invoicePreviewContent.setMaxWidth(Region.USE_PREF_SIZE);
        invoicePreviewContent.setMaxHeight(Region.USE_PREF_SIZE);
        
        // Configure ScrollPane for smooth panning/scrolling without visible scrollbars
        setupReceiptPreviewScrolling();
        
        logger.info("✅ Invoice preview initialized with fixed sizing constraints and scrolling");
    }
    
    /**
     * Setup responsive layout that binds receipt preview height to order form height
     * This ensures both containers grow together when items are added
     */
    private void setupResponsiveLayout() {
        logger.info("📐 Setting up responsive layout for equal container heights...");
        
        try {
            // Bind the receipt preview container height to order form container height
            if (orderFormContainer != null && receiptPreviewContainer != null) {
                // Use Platform.runLater to ensure all layout calculations are complete
                Platform.runLater(() -> {
                    // Create height binding between containers
                    receiptPreviewContainer.prefHeightProperty().bind(orderFormContainer.heightProperty());
                    receiptPreviewContainer.minHeightProperty().bind(orderFormContainer.heightProperty());
                    
                    logger.info("✅ Responsive layout binding established between containers");
                });
            } else {
                logger.warning("⚠️ Container references not found - responsive layout not setup");
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "⚠️ Error setting up responsive layout", e);
        }
    }
    
    /**
     * Setup receipt preview scrolling and panning functionality without scrollbars
     */
    private void setupReceiptPreviewScrolling() {
        // Ensure no scrollbars are ever shown
        invoiceScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        invoiceScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Enable manual panning without visible scrollbars
        invoiceScrollPane.setPannable(false); // Disable default panning to implement custom
        
        // Custom mouse wheel scrolling
        invoiceScrollPane.setOnScroll(event -> {
            if (currentPreviewImageView != null) {
                double deltaY = event.getDeltaY();
                double sensitivity = 0.002; // Scroll sensitivity
                
                // Get current scroll position
                double currentVValue = invoiceScrollPane.getVvalue();
                double newVValue = currentVValue - (deltaY * sensitivity);
                
                // Clamp the value between 0 and 1
                newVValue = Math.max(0, Math.min(1, newVValue));
                invoiceScrollPane.setVvalue(newVValue);
                
                event.consume();
            }
        });
        
        // Custom drag panning for both the ScrollPane and its content
        setupCustomPanning(invoiceScrollPane);
        
        // Keyboard navigation
        invoiceScrollPane.setOnKeyPressed(event -> {
            if (currentPreviewImageView != null) {
                double scrollAmount = 0.1;
                switch (event.getCode()) {
                    case UP:
                    case PAGE_UP:
                        invoiceScrollPane.setVvalue(Math.max(0, invoiceScrollPane.getVvalue() - scrollAmount));
                        event.consume();
                        break;
                    case DOWN:
                    case PAGE_DOWN:
                        invoiceScrollPane.setVvalue(Math.min(1, invoiceScrollPane.getVvalue() + scrollAmount));
                        event.consume();
                        break;
                    case LEFT:
                        invoiceScrollPane.setHvalue(Math.max(0, invoiceScrollPane.getHvalue() - scrollAmount));
                        event.consume();
                        break;
                    case RIGHT:
                        invoiceScrollPane.setHvalue(Math.min(1, invoiceScrollPane.getHvalue() + scrollAmount));
                        event.consume();
                        break;
                    case HOME:
                        invoiceScrollPane.setVvalue(0);
                        invoiceScrollPane.setHvalue(0);
                        event.consume();
                        break;
                    case END:
                        invoiceScrollPane.setVvalue(1);
                        event.consume();
                        break;
                }
            }
        });
        
        invoiceScrollPane.setFocusTraversable(true);
        
        logger.info("🖱️ Receipt preview custom scrolling and panning configured (no scrollbars)");
    }
    
    /**
     * Setup custom panning functionality for a node
     */
    private void setupCustomPanning(javafx.scene.Node node) {
        node.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && currentPreviewImageView != null) {
                isPanning = true;
                lastPanX = event.getSceneX();
                lastPanY = event.getSceneY();
                invoiceScrollPane.setCursor(javafx.scene.Cursor.CLOSED_HAND);
                event.consume();
            }
        });
        
        node.setOnMouseDragged(event -> {
            if (isPanning && event.isPrimaryButtonDown() && currentPreviewImageView != null) {
                double deltaX = event.getSceneX() - lastPanX;
                double deltaY = event.getSceneY() - lastPanY;
                
                // Calculate pan sensitivity based on content size vs viewport size
                double contentWidth = invoicePreviewContent.getBoundsInLocal().getWidth();
                double contentHeight = invoicePreviewContent.getBoundsInLocal().getHeight();
                double viewportWidth = invoiceScrollPane.getViewportBounds().getWidth();
                double viewportHeight = invoiceScrollPane.getViewportBounds().getHeight();
                
                if (contentWidth > viewportWidth) {
                    double hSensitivity = 1.0 / (contentWidth - viewportWidth);
                    double newHValue = invoiceScrollPane.getHvalue() - (deltaX * hSensitivity);
                    newHValue = Math.max(0, Math.min(1, newHValue));
                    invoiceScrollPane.setHvalue(newHValue);
                }
                
                if (contentHeight > viewportHeight) {
                    double vSensitivity = 1.0 / (contentHeight - viewportHeight);
                    double newVValue = invoiceScrollPane.getVvalue() - (deltaY * vSensitivity);
                    newVValue = Math.max(0, Math.min(1, newVValue));
                    invoiceScrollPane.setVvalue(newVValue);
                }
                
                lastPanX = event.getSceneX();
                lastPanY = event.getSceneY();
                event.consume();
            }
        });
        
        node.setOnMouseReleased(event -> {
            if (isPanning) {
                isPanning = false;
                invoiceScrollPane.setCursor(javafx.scene.Cursor.DEFAULT);
                event.consume();
            }
        });
        
        node.setOnMouseEntered(event -> {
            if (!isPanning && currentPreviewImageView != null) {
                invoiceScrollPane.setCursor(javafx.scene.Cursor.OPEN_HAND);
            }
        });
        
        node.setOnMouseExited(event -> {
            if (!isPanning) {
                invoiceScrollPane.setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });
    }

    /**
     * Update the invoice preview - called only when meaningful changes occur:
     * - Payment method changes
     * - Items added/removed/cleared
     * - Payment details changed (on focus lost)
     * - Form cleared
     */
    private void updateInvoicePreview() {
        logger.info("🔄 Updating invoice preview...");
        
        Platform.runLater(() -> {
            try {
                // Store current focus state to prevent jumping
                javafx.scene.Node focusedNode = invoicePreviewContent.getScene() != null ? 
                    invoicePreviewContent.getScene().getFocusOwner() : null;
                
                invoicePreviewContent.getChildren().clear();
                
                                    if (currentDynamicOrderItems.isEmpty()) {
                    // Show stable FXML placeholder label and hide preview content
                    previewPlaceholderLabel.setVisible(true);
                    previewPlaceholderLabel.setManaged(true);
                    invoicePreviewContent.getChildren().clear();
                } else {
                    // Hide stable FXML placeholder label and generate PDF preview
                    previewPlaceholderLabel.setVisible(false);
                    previewPlaceholderLabel.setManaged(false);
                    generatePDFPreviewImage();
                }
                
                // Restore focus to prevent page jumping
                if (focusedNode != null) {
                    Platform.runLater(() -> {
                        try {
                            focusedNode.requestFocus();
                        } catch (Exception ex) {
                            // Ignore focus restore errors
                        }
                    });
                }
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error updating invoice preview", e);
                showPDFPreviewError();
            }
        });
    }

    private void generatePDFPreviewImage() {
        // Store current focus and scroll position to prevent jumping during PDF generation
        javafx.scene.Node focusedNode = invoicePreviewContent.getScene() != null ? 
            invoicePreviewContent.getScene().getFocusOwner() : null;
        double currentHValue = invoiceScrollPane.getHvalue();
        double currentVValue = invoiceScrollPane.getVvalue();
            
        // Show loading indicator first
        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(50, 50);
        invoicePreviewContent.getChildren().clear();
        invoicePreviewContent.getChildren().add(progress);
        
        // Generate PDF in background thread
        new Thread(() -> {
            try {
                // Create preview order from current form data
                String customerName = customerNameField.getText().trim();
                if (customerName.isEmpty()) customerName = "Customer";
                
                String contactNumber = contactNumberField.getText().trim();
                if (contactNumber.isEmpty()) contactNumber = "Contact Number";
                
                String paymentMethod = paymentMethodCombo.getValue();
                if (paymentMethod == null) paymentMethod = "Cash";
                
                // Build items string
                StringBuilder itemsBuilder = new StringBuilder();
                for (DynamicOrderItem item : currentDynamicOrderItems) {
                    if (itemsBuilder.length() > 0) itemsBuilder.append(";");
                    itemsBuilder.append(item.getDisplayString());
                }
                
                // Handle payment-specific details for preview
                String referenceNumber = referenceNumberField.getText().trim();
                String timestampOrCash = timestampField.getText().trim();
                
                // For cash payments, use the cash received amount in the timestamp field
                if ("Cash".equals(paymentMethod)) {
                    referenceNumber = ""; // No reference number for cash
                    timestampOrCash = cashReceivedField.getText().trim().isEmpty() ? "0.0" : cashReceivedField.getText().trim(); // Store cash received amount, default to 0.0
                }
                
                // Calculate correct total from actual order items
                double actualTotal = currentDynamicOrderItems.stream().mapToDouble(DynamicOrderItem::getSubtotal).sum();
                
                // Create preview order with new ID format
                RecentOrder previewOrder = new RecentOrder(
                    "WP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-001",
                    customerName,
                    contactNumber,
                    itemsBuilder.toString(),
                    String.valueOf(totalQuantity),
                    String.format("₱%.2f", actualTotal),
                    paymentMethod,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
                    "Pending",
                    referenceNumber,
                    timestampOrCash
                );
                
                // Generate PDF document
                PDDocument document = generatePDFInMemory(previewOrder);
                if (document != null) {
                    // Convert first page to image
                    PDFRenderer pdfRenderer = new PDFRenderer(document);
                    BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300); // 300 DPI for high quality
                    
                    // Convert to JavaFX image
                    WritableImage fxImage = SwingFXUtils.toFXImage(image, null);
                    
                    // Update UI in JavaFX thread
                    Platform.runLater(() -> {
                        try {
                            // Create image view
                            ImageView imageView = new ImageView(fxImage);
                            imageView.setPreserveRatio(true);
                            
                            // Store original dimensions
                            originalImageWidth = fxImage.getWidth();
                            originalImageHeight = fxImage.getHeight();
                            
                            // Create container for the image view with no extra space
                            StackPane container = new StackPane(imageView);
                            container.setStyle("-fx-background-color: white; -fx-padding: 0; -fx-border-width: 0;");
                            container.setAlignment(Pos.CENTER);
                            
                            // Set container to exactly match image dimensions to prevent empty space
                            container.setMinWidth(0);
                            container.setMinHeight(0);
                            container.setMaxWidth(Region.USE_PREF_SIZE);
                            container.setMaxHeight(Region.USE_PREF_SIZE);
                            
                            // Clear previous content and add new image
                            invoicePreviewContent.getChildren().clear();
                            invoicePreviewContent.getChildren().add(container);
                            
                            // Store reference to current preview
                            currentPreviewImageView = imageView;
                            
                            // Setup panning functionality for the image and container
                            setupImageViewPanning(imageView);
                            setupCustomPanning(container);
                            setupCustomPanning(imageView);
                            
                            // Reset zoom to fit width
                            resetToPageView();
                            
                            // Restore scroll position and focus to prevent page jumping
                            Platform.runLater(() -> {
                                try {
                                    // Restore scroll position first
                                    invoiceScrollPane.setHvalue(currentHValue);
                                    invoiceScrollPane.setVvalue(currentVValue);
                                    
                                    // Then restore focus
                                    if (focusedNode != null) {
                                        focusedNode.requestFocus();
                                    }
                                } catch (Exception ex) {
                                    // Ignore restore errors
                                }
                            });
                            
                            logger.info("✅ PDF preview generated and displayed");
                            
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error displaying PDF preview", e);
                            showPDFPreviewError();
                            
                            // Restore focus even on error
                            if (focusedNode != null) {
                                Platform.runLater(() -> {
                                    try {
                                        focusedNode.requestFocus();
                                    } catch (Exception ex) {
                                        // Ignore focus restore errors
                                    }
                                });
                            }
                        }
                    });
                    
                    // Close the document
                    document.close();
                    
                } else {
                    Platform.runLater(() -> {
                        showPDFPreviewError();
                        // Restore focus on error
                        if (focusedNode != null) {
                            try {
                                focusedNode.requestFocus();
                            } catch (Exception ex) {
                                // Ignore focus restore errors
                            }
                        }
                    });
                }
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error generating PDF preview", e);
                Platform.runLater(() -> {
                    showPDFPreviewError();
                    // Restore focus on error
                    if (focusedNode != null) {
                        try {
                            focusedNode.requestFocus();
                        } catch (Exception ex) {
                            // Ignore focus restore errors
                        }
                    }
                });
            }
        }).start();
    }

    private PDDocument generatePDFInMemory(RecentOrder order) {
        try {
            // Create new PDF document with custom page size
            PDDocument document = new PDDocument();
            
            // Custom page size: 3.15 inches width (227 points), auto-height
            float pageWidth = 3.15f * 72; // 3.15 inches * 72 points per inch = 227 points
            
            // Initialize fonts
            PDType1Font courierRegular = new PDType1Font(Standard14Fonts.FontName.COURIER);
            PDType1Font courierBold = new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
            
            // Calculate separator dimensions that will be used throughout
            float separatorWidth = courierRegular.getStringWidth(RECEIPT_SEPARATOR) / 1000 * 9f;
            float separatorX = (pageWidth - separatorWidth) / 2;
            
            // Calculate actual height needed based on content
            String[] items = order.getItemsOrdered().split(";");
            int itemCount = items.length;
            float lineHeight = 12f; // Line height for compact receipt
            
            // Calculate total lines needed - updated calculation without the removed content
            int headerLines = 12; // Updated for new header format
            int orderDetailLines = 4; // Order ID, Date, Customer details
            int itemHeaderLines = 3; // Separator + "Items Ordered" + spacing
            int itemLines = itemCount;
            int totalSectionLines = 3; // First separator + TOTAL line + second separator
            int digitalPaymentLines = 0;
            int cashPaymentLines = 0;
            int thankYouLines = 4; // Bottom separator + thank you message (3 lines) - reduced spacing
            
            // Add cash payment lines if applicable
            if ("Cash".equals(order.getPaymentMethod())) {
                cashPaymentLines = 3; // Paid By, Cash amount, Change lines
            }
            
            // Add digital payment lines if applicable (handled in main payment section now)
            if (("Maya".equals(order.getPaymentMethod()) || "GCash".equals(order.getPaymentMethod()))) {
                digitalPaymentLines = 3; // Paid By, Reference Number, Timestamp lines
            }
            
            // Calculate total height with proper margins - removed the totalLines that included unwanted content
            float totalHeight = (headerLines + orderDetailLines + itemHeaderLines + itemLines + totalSectionLines + cashPaymentLines + digitalPaymentLines + thankYouLines) * lineHeight + 80; // Reduced margin for better spacing balance
            
            // Create page with calculated dimensions
            PDPage page = new PDPage();
            page.setMediaBox(new PDRectangle(pageWidth, totalHeight));
            document.addPage(page);
            
            // Create content stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Start from top with margin
            float topMargin = 20f;
            float bottomMargin = 20f; // Add bottom margin for consistency
            float startY = totalHeight - topMargin;
            float yPosition = startY;
            
            contentStream.beginText();
            
            // Header 1 - "RECEIPT OF SALE" (15px, centered, Courier regular)
            contentStream.setFont(courierRegular, 15f);
            String receiptHeader = "RECEIPT OF SALE";
            float receiptHeaderWidth = courierRegular.getStringWidth(receiptHeader) / 1000 * 15f;
            float receiptHeaderX = (pageWidth - receiptHeaderWidth) / 2;
            contentStream.newLineAtOffset(receiptHeaderX, yPosition);
            contentStream.showText(receiptHeader);
            yPosition -= 15f; // Spacing after RECEIPT OF SALE
            
            // Header 2 - "WONDERPOFFLES" (18px, centered, Courier Bold)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 18f);
            String companyName = "WONDERPOFFLES";
            float companyNameWidth = courierBold.getStringWidth(companyName) / 1000 * 18f;
            float companyNameX = (pageWidth - companyNameWidth) / 2;
            contentStream.newLineAtOffset(companyNameX, yPosition);
            contentStream.showText(companyName);
            yPosition -= 40f; // Increased spacing after company name
            
            // Address Line 1 - "Address: 43 Bassig Street, Ugac" (10px, centered, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String addressLine1 = "Address: 43 Bassig Street, Ugac";
            float addressLine1Width = courierRegular.getStringWidth(addressLine1) / 1000 * 10f;
            float addressLine1X = (pageWidth - addressLine1Width) / 2;
            contentStream.newLineAtOffset(addressLine1X, yPosition);
            contentStream.showText(addressLine1);
            yPosition -= 12f;
            
            // Address Line 2 - "Sur, Tuguegarao City, Cagayan" (10px, centered, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String addressLine2 = "Sur, Tuguegarao City, Cagayan";
            float addressLine2Width = courierRegular.getStringWidth(addressLine2) / 1000 * 10f;
            float addressLine2X = (pageWidth - addressLine2Width) / 2;
            contentStream.newLineAtOffset(addressLine2X, yPosition);
            contentStream.showText(addressLine2);
            yPosition -= 12f;
            
            // Contact - "Contact #: 0975-825-6553" (10px, centered, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String contactInfo = "Contact #: 0975-825-6553";
            float contactInfoWidth = courierRegular.getStringWidth(contactInfo) / 1000 * 10f;
            float contactInfoX = (pageWidth - contactInfoWidth) / 2;
            contentStream.newLineAtOffset(contactInfoX, yPosition);
            contentStream.showText(contactInfo);
            yPosition -= 25f; // Increased spacing after contact info
            
            // Separator line
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= lineHeight * 1.5f;
            
            // Add 10f spacing after separator
            yPosition -= 10f;
            
            // Order ID (centered, 13px, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 13f);
            String orderIdText = "Order ID: " + order.getOrderId();
            float orderIdWidth = courierRegular.getStringWidth(orderIdText) / 1000 * 13f;
            float orderIdX = (pageWidth - orderIdWidth) / 2;
            contentStream.newLineAtOffset(orderIdX, yPosition);
            contentStream.showText(orderIdText);
            yPosition -= 15f; // 15px spacing after Order ID
            
            // Date and time (centered, 13px, Courier regular)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 13f);
            String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy   hh:mm a"));
            float dateTimeWidth = courierRegular.getStringWidth(currentDateTime) / 1000 * 13f;
            float dateTimeX = (pageWidth - dateTimeWidth) / 2;
            contentStream.newLineAtOffset(dateTimeX, yPosition);
            contentStream.showText(currentDateTime);
            yPosition -= 30f; // 30px spacing after date/time
            
            // Table setup - Define column positions for 3.15" width (227 points)
            float leftMargin = 10f;
            float rightMargin = 10f;
            float availableWidth = pageWidth - leftMargin - rightMargin; // 207 points available
            
            // Column widths: QTY (30), NAME (130), AMT (47)
            float qtyColWidth = 30f;
            float nameColWidth = 130f; 
            float amtColWidth = 47f;
            
            // Column positions
            float qtyColX = leftMargin;
            float nameColX = qtyColX + qtyColWidth;
            float amtColX = nameColX + nameColWidth;
            
            // Table header row
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            
            // QTY header (left aligned)
            contentStream.newLineAtOffset(qtyColX, yPosition);
            contentStream.showText("QTY");
            
            // NAME header (left aligned)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            contentStream.newLineAtOffset(nameColX, yPosition);
            contentStream.showText("NAME");
            
            // AMT header (right aligned)
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 10f);
            String amtHeader = "AMT";
            float amtHeaderWidth = courierRegular.getStringWidth(amtHeader) / 1000 * 10f;
            contentStream.newLineAtOffset(amtColX + amtColWidth - amtHeaderWidth, yPosition);
            contentStream.showText(amtHeader);
            
            yPosition -= 12f; // Move down after header
            
            // Horizontal separator between header and data
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 10f; // 10px spacing after separator
            
            // Parse and display items in table format with correct pricing
            for (String item : items) {
                // Use helper method to get correct pricing
                Object[] itemDetails = parseItemWithPrice(item);
                String qty = (String) itemDetails[0];
                String name = (String) itemDetails[1];
                double unitPrice = (Double) itemDetails[2];
                double totalPrice = (Double) itemDetails[3];
                
                // Truncate name if too long (max 18 characters for narrow receipt)
                if (name.length() > 18) {
                    name = name.substring(0, 18);
                }
                
                // QTY column (left aligned)
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(qtyColX, yPosition);
                contentStream.showText(qty);
                
                // NAME column (left aligned)
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(nameColX, yPosition);
                contentStream.showText(name);
                
                // AMT column (right aligned)
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                String amtText = String.format("%.2f", totalPrice);
                float amtTextWidth = courierRegular.getStringWidth(amtText) / 1000 * 10f;
                contentStream.newLineAtOffset(amtColX + amtColWidth - amtTextWidth, yPosition);
                contentStream.showText(amtText);
                
                yPosition -= 15f; // 15px spacing per row
            }
            
            yPosition -= 15f; // 15f spacing after table
            
            // First separator after table
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 20f; // 20px spacing after separator
            
            // TOTAL section - "TOTAL" left aligned, amount right aligned
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 18f);
            
            // Calculate total amount from items using actual product prices
            double calculatedTotal = calculateOrderTotal(order);
            
            // "TOTAL" left aligned to left edge
            contentStream.newLineAtOffset(leftMargin, yPosition);
            contentStream.showText("TOTAL");
            
            // Total amount right aligned to right edge
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierBold, 18f);
            String totalAmountText = String.format("PHP %.2f", calculatedTotal);
            float totalAmountWidth = courierBold.getStringWidth(totalAmountText) / 1000 * 18f;
            contentStream.newLineAtOffset(pageWidth - rightMargin - totalAmountWidth, yPosition);
            contentStream.showText(totalAmountText);
            yPosition -= 15f; // 15px spacing after TOTAL
            
            // Second separator after TOTAL
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 25f; // 25px spacing after separator
            
            // Payment details section (Cash or Digital payment)
            if ("Cash".equals(order.getPaymentMethod())) {
                // "Paid By:" left aligned, "Cash" right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Paid By:");
                
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                String cashText = "Cash";
                float cashTextWidth = courierRegular.getStringWidth(cashText) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - cashTextWidth, yPosition);
                contentStream.showText(cashText);
                yPosition -= 15f; // 15px spacing
                
                // "Cash:" left aligned, cash amount right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Cash Received:");
                
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                // Use the actual cash received amount from the order data (stored in timestamp field for cash payments)
                double cashAmount;
                try {
                    cashAmount = Double.parseDouble(order.getTimestamp());
                } catch (NumberFormatException e) {
                    cashAmount = 0.0; // Default to 0.0 when empty - no random numbers
                }
                String cashAmountText = String.format("PHP %.2f", cashAmount);
                float cashAmountWidth = courierRegular.getStringWidth(cashAmountText) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - cashAmountWidth, yPosition);
                contentStream.showText(cashAmountText);
                yPosition -= 15f; // 15px spacing
                
                // "Change:" left aligned, change amount right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Change:");
                
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                double changeAmount = cashAmount - calculatedTotal;
                String changeAmountText = String.format("PHP %.2f", changeAmount);
                float changeAmountWidth = courierRegular.getStringWidth(changeAmountText) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - changeAmountWidth, yPosition);
                contentStream.showText(changeAmountText);
                yPosition -= 25f; // 25px spacing after cash details
            } else if ("GCash".equals(order.getPaymentMethod()) || "Maya".equals(order.getPaymentMethod())) {
                // E-wallet payment format (GCash/Maya)
                // "Paid By:" left aligned, "GCash" or "Maya" right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Paid By:");
                
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                String paymentMethodText = order.getPaymentMethod(); // "GCash" or "Maya"
                float paymentMethodWidth = courierRegular.getStringWidth(paymentMethodText) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - paymentMethodWidth, yPosition);
                contentStream.showText(paymentMethodText);
                yPosition -= 15f; // 15px spacing
                
                // "Reference Number:" left aligned, reference number right aligned
                contentStream.endText();
                contentStream.beginText();
                contentStream.setFont(courierRegular, 10f);
                contentStream.newLineAtOffset(leftMargin, yPosition);
                contentStream.showText("Reference Number:");
                
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.setFont(courierRegular, 10f);
                String referenceNumber = order.getReferenceNumber().isEmpty() ? "N/A" : order.getReferenceNumber();
                float referenceNumberWidth = courierRegular.getStringWidth(referenceNumber) / 1000 * 10f;
                contentStream.newLineAtOffset(pageWidth - rightMargin - referenceNumberWidth, yPosition);
                contentStream.showText(referenceNumber);
                yPosition -= 15f; // 15px spacing
                
                // Timestamp left aligned (no right side content)
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.setFont(courierRegular, 10f);
                        contentStream.newLineAtOffset(leftMargin, yPosition);
                String timestamp = order.getTimestamp().isEmpty() ? "N/A" : order.getTimestamp();
                contentStream.showText(timestamp);
                yPosition -= 25f; // 25px spacing after e-wallet details
            } else {
                yPosition -= lineHeight * 1.5f; // Normal spacing for other payment methods
            }
            
            
            // Bottom separator
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 9f);
            contentStream.newLineAtOffset(separatorX, yPosition);
            contentStream.showText(RECEIPT_SEPARATOR);
            yPosition -= 15f; // Reduced spacing after separator for more compact layout
            
            // Thank you message - customized format
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 8f);
            
            // First line: "Thank you for choosing WonderPoffles!"
            String thankYouLine1 = "Thank you for choosing WonderPoffles!";
            float thankYouLine1Width = courierRegular.getStringWidth(thankYouLine1) / 1000 * 8f;
            float thankYouLine1X = (pageWidth - thankYouLine1Width) / 2;
            contentStream.newLineAtOffset(thankYouLine1X, yPosition);
            contentStream.showText(thankYouLine1);
            yPosition -= 12f; // 12px spacing after first line
            
            // Second line: "We hope every bite made your day a"
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 8f);
            String thankYouLine2 = "We hope every bite made your day a";
            float thankYouLine2Width = courierRegular.getStringWidth(thankYouLine2) / 1000 * 8f;
            float thankYouLine2X = (pageWidth - thankYouLine2Width) / 2;
            contentStream.newLineAtOffset(thankYouLine2X, yPosition);
            contentStream.showText(thankYouLine2);
            yPosition -= 12f; // 12px spacing after second line
            
            // Third line: "little brighter."
            contentStream.endText();
            contentStream.beginText();
            contentStream.setFont(courierRegular, 8f);
            String thankYouLine3 = "little brighter.";
            float thankYouLine3Width = courierRegular.getStringWidth(thankYouLine3) / 1000 * 8f;
            float thankYouLine3X = (pageWidth - thankYouLine3Width) / 2;
            contentStream.newLineAtOffset(thankYouLine3X, yPosition);
            contentStream.showText(thankYouLine3);
            // Remove the extra bottom margin - height calculation already accounts for proper spacing
            
            contentStream.close();
            
            return document;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error generating receipt for order: " + order.getOrderId(), e);
            showAlert(Alert.AlertType.ERROR, "PDF Generation Error", 
                "Failed to generate receipt. Please try again.");
            return null;
        }
    }

    /**
     * Public method to refresh product data from files
     * This can be called by ProductManagementController when data changes
     */
    public void refreshProductData() {
        logger.info("🔄 Refreshing product data from files...");
        loadDynamicProductData();
        
        // Update form controls with new data
        Platform.runLater(() -> {
            // Save current selections
            String selectedCategory = categoryCombo.getValue();
            String selectedFlavor = flavorCombo.getValue();
            
            // Update category combo box
            categoryCombo.setItems(FXCollections.observableArrayList(availableCategories));
            
            // Restore category selection if it still exists
            if (selectedCategory != null && availableCategories.contains(selectedCategory)) {
                categoryCombo.setValue(selectedCategory);
                // Trigger category selection to update flavors
                handleCategorySelection();
                
                // Restore flavor selection if it still exists
                if (selectedFlavor != null) {
                    List<ProductData> flavors = categoryToFlavors.get(selectedCategory);
                    if (flavors != null && flavors.stream().anyMatch(p -> p.getName().equals(selectedFlavor))) {
                        flavorCombo.setValue(selectedFlavor);
                        handleFlavorSelection();
                    }
                }
            } else {
                // Clear selections if category no longer exists
                categoryCombo.setValue(null);
                flavorCombo.setValue(null);
                priceField.setText("₱0.00");
            }
            
            logger.info("✅ Product data refreshed and form controls updated - " + availableCategories.size() + " categories loaded");
            
            // Optional: Show a brief visual indicator that data was updated
            if (availableCategories.size() > 0) {
                logger.info("📋 Available categories: " + String.join(", ", availableCategories));
            }
        });
    }

    /**
     * Helper method to get actual product data by name from loaded data
     * @param productName The name of the product to find
     * @return ProductData if found, null otherwise
     */
    private ProductData findProductByName(String productName) {
        for (List<ProductData> categoryProducts : categoryToFlavors.values()) {
            for (ProductData product : categoryProducts) {
                if (product.getName().equals(productName)) {
                    return product;
                }
            }
        }
        return null;
    }
    
    /**
     * Helper method to safely parse cash amount from order data
     * @param order The order containing cash data
     * @return Cash amount as double, 0.0 if empty or invalid
     */
    private double parseCashAmount(RecentOrder order) {
        try {
            String cashStr = order.getTimestamp();
            if (cashStr == null || cashStr.trim().isEmpty()) {
                return 0.0; // Default to 0.0 when empty, no random numbers
            }
            return Double.parseDouble(cashStr.trim());
        } catch (NumberFormatException e) {
            logger.warning("⚠️ Invalid cash amount format: " + order.getTimestamp() + ", defaulting to 0.0");
            return 0.0; // Default to 0.0 when invalid, no random numbers
        }
    }
    
    /**
     * Helper method to calculate total from order items using actual product prices
     * @param order The order to calculate total for
     * @return Calculated total amount
     */
    private double calculateOrderTotal(RecentOrder order) {
        String[] items = order.getItemsOrdered().split(";");
        double total = 0.0;
        
        for (String item : items) {
            item = item.trim();
            if (item.isEmpty()) continue;
            
            int quantity = 1;
            String productName = item;
            
            // Parse quantity and product name from item string
            if (item.contains(" x ")) {
                String[] parts = item.split(" x ");
                if (parts.length == 2) {
                    productName = parts[0].trim();
                    try {
                        quantity = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {
                        quantity = 1;
                    }
                }
            } else if (item.contains("x ")) {
                String[] parts = item.split("x ");
                if (parts.length == 2) {
                    try {
                        quantity = Integer.parseInt(parts[0].trim());
                        productName = parts[1].trim();
                    } catch (NumberFormatException e) {
                        quantity = 1;
                        productName = item;
                    }
                }
            }
            
            // Find actual product data and use real price
            ProductData productData = findProductByName(productName);
            double unitPrice = productData != null ? productData.getPriceValue() : 45.0; // Fallback only
            total += quantity * unitPrice;
        }
        
        return total;
    }
    
    /**
     * Helper method to get item details with correct pricing for receipt generation
     * @param item The item string to parse
     * @return Object array containing [quantity, name, unitPrice, totalPrice]
     */
    private Object[] parseItemWithPrice(String item) {
        item = item.trim();
        String qty = "1";
        String name = item;
        double unitPrice = 45.0; // Default fallback
        double totalPrice = unitPrice;
        
        // Parse quantity and name from item string
        if (item.contains(" x ")) {
            String[] parts = item.split(" x ");
            if (parts.length == 2) {
                name = parts[0].trim();
                try {
                    qty = parts[1].trim();
                    int qtyInt = Integer.parseInt(qty);
                    
                    // Look up actual product price
                    ProductData productData = findProductByName(name);
                    unitPrice = productData != null ? productData.getPriceValue() : 45.0;
                    totalPrice = qtyInt * unitPrice;
                } catch (NumberFormatException e) {
                    qty = "1";
                    // Look up actual product price for single item
                    ProductData productData = findProductByName(name);
                    unitPrice = productData != null ? productData.getPriceValue() : 45.0;
                    totalPrice = unitPrice;
                }
            }
        } else if (item.contains("x ")) {
            String[] parts = item.split("x ");
            if (parts.length == 2) {
                try {
                    qty = parts[0].trim();
                    name = parts[1].trim();
                    int qtyInt = Integer.parseInt(qty);
                    
                    // Look up actual product price
                    ProductData productData = findProductByName(name);
                    unitPrice = productData != null ? productData.getPriceValue() : 45.0;
                    totalPrice = qtyInt * unitPrice;
                } catch (NumberFormatException e) {
                    qty = "1";
                    name = item;
                    // Look up actual product price
                    ProductData productData = findProductByName(name);
                    unitPrice = productData != null ? productData.getPriceValue() : 45.0;
                    totalPrice = unitPrice;
                }
            }
        } else {
            // Single item without explicit quantity
            // Look up actual product price
            ProductData productData = findProductByName(name);
            unitPrice = productData != null ? productData.getPriceValue() : 45.0;
            totalPrice = unitPrice;
        }
        
        return new Object[]{qty, name, unitPrice, totalPrice};
    }
}

