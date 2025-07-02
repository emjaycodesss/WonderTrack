module com.example.wondertrackxd {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires java.logging;

    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires MaterialFX;
    requires org.apache.pdfbox;

    opens com.example.wondertrackxd to javafx.fxml;
    opens com.example.wondertrackxd.controller to javafx.fxml;
    
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
}