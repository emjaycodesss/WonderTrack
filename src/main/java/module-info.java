module com.example.wondertrackxd {
    // Core JavaFX modules (actually used)
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.logging;

    // PDF generation (actually used)
    requires org.apache.pdfbox;

    // Open packages for FXML injection
    opens com.example.wondertrackxd to javafx.fxml;
    opens com.example.wondertrackxd.controller to javafx.fxml;
    
    // Export packages for module access
    exports com.example.wondertrackxd;
    exports com.example.wondertrackxd.controller;
    exports com.example.wondertrackxd.controller.overview;
    opens com.example.wondertrackxd.controller.overview to javafx.fxml;
    exports com.example.wondertrackxd.controller.orders;
    opens com.example.wondertrackxd.controller.orders to javafx.fxml;
    exports com.example.wondertrackxd.controller.analytics;
    opens com.example.wondertrackxd.controller.analytics to javafx.fxml;
    exports com.example.wondertrackxd.controller.products;
    opens com.example.wondertrackxd.controller.products to javafx.fxml;
    exports com.example.wondertrackxd.controller.header;
    opens com.example.wondertrackxd.controller.header to javafx.fxml;
    exports com.example.wondertrackxd.controller.sidebar;
    opens com.example.wondertrackxd.controller.sidebar to javafx.fxml;
    exports com.example.wondertrackxd.controller.settings;
    opens com.example.wondertrackxd.controller.settings to javafx.fxml;
    exports com.example.wondertrackxd.controller.sales;
    opens com.example.wondertrackxd.controller.sales to javafx.fxml;

    // Model package exports and opens for JavaFX property access
    exports com.example.wondertrackxd.controller.model;
    opens com.example.wondertrackxd.controller.model to javafx.base, javafx.fxml;
}