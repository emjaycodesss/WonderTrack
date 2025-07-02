package com.example.wondertrackxd.controller.products;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.nio.file.*;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import java.util.Optional;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Tooltip;
import javafx.application.Platform;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser.ExtensionFilter;

import com.example.wondertrackxd.controller.model.WaffleCategory;
import com.example.wondertrackxd.controller.model.WaffleFlavor;

public class ProductManagementController {
    
    private static final Logger logger = Logger.getLogger(ProductManagementController.class.getName());
    private static final int ROWS_PER_PAGE = 5;
    
    /**
     * Callback to notify other controllers when product data changes
     * This allows real-time updates across the application
     */
    private static List<Runnable> dataChangeCallbacks = new ArrayList<>();
    
    /**
     * Register a callback to be notified when product data changes
     * @param callback The callback to execute when data changes
     */
    public static void registerDataChangeCallback(Runnable callback) {
        dataChangeCallbacks.add(callback);
        logger.info("📡 Data change callback registered");
    }
    
    /**
     * Notify all registered callbacks that product data has changed
     */
    private static void notifyDataChangeCallbacks() {
        logger.info("📢 Notifying " + dataChangeCallbacks.size() + " registered callbacks of data changes");
        for (Runnable callback : dataChangeCallbacks) {
            try {
                Platform.runLater(callback);
            } catch (Exception e) {
                logger.log(Level.WARNING, "⚠️ Error executing data change callback", e);
            }
        }
    }
    
    // Callback for real-time updates
    private Runnable onDataChangedCallback;
    
    @FXML private TextField categoryNameField;
    @FXML private Button addCategoryButton;
    @FXML private TableView<CategoryItem> categoriesTable;
    @FXML private TableColumn<CategoryItem, String> categoryNameColumn;
    @FXML private TableColumn<CategoryItem, Void> categoryActionsColumn;
    
    // Category Pagination Controls
    @FXML private Button categoryPrevPageBtn;
    @FXML private Button categoryNextPageBtn;
    @FXML private Label categoryPageLabel;
    @FXML private Label categoryResultsInfoLabel;
    
    @FXML private ComboBox<String> productCategoryCombo;
    @FXML private TextField productNameField;
    @FXML private TextField productDescriptionField;
    @FXML private TextField productPriceField;
    @FXML private Button addProductButton;
    @FXML private TableView<ProductItem> productsTable;
    @FXML private TableColumn<ProductItem, String> productCategoryColumn;
    @FXML private TableColumn<ProductItem, String> productNameColumn;
    @FXML private TableColumn<ProductItem, String> productDescriptionColumn;
    @FXML private TableColumn<ProductItem, String> productPriceColumn;
    @FXML private TableColumn<ProductItem, Void> productActionsColumn;
    
    // Product Pagination Controls
    @FXML private Button productPrevPageBtn;
    @FXML private Button productNextPageBtn;
    @FXML private Label productPageLabel;
    @FXML private Label productResultsInfoLabel;
    
    private ObservableList<CategoryItem> categories = FXCollections.observableArrayList();
    private ObservableList<ProductItem> products = FXCollections.observableArrayList();
    private ObservableList<CategoryItem> currentCategoryPage = FXCollections.observableArrayList();
    private ObservableList<ProductItem> currentProductPage = FXCollections.observableArrayList();
    
    private int currentCategoryPageIndex = 0;
    private int currentProductPageIndex = 0;
    private static final String PRODUCTS_FILE = "src/main/resources/txtFiles/products.txt";
    // Categories are now dynamically extracted from products.txt - no separate file needed
    
    @FXML
    public void initialize() {
        logger.info("🏗️ Initializing Product Management Controller...");
        setupTables();
        setupPagination();
        setupEventHandlers();
        loadData();
        
        // Set style class for action columns
        categoryActionsColumn.getStyleClass().add("actions-column");
        productActionsColumn.getStyleClass().add("actions-column");
        
        setupCategoryActionsColumn();
        setupProductActionsColumn();
    }
    
    private void setupTables() {
        logger.info("🔧 Setting up table configurations...");
        
        // Setup Categories Table
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        setupCategoryActionsColumn();
        categoriesTable.setItems(currentCategoryPage);
        
        // Setup Products Table
        productCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        setupProductActionsColumn();
        productsTable.setItems(currentProductPage);
        
        // Configure table appearance and scrolling behavior
        configureCategoriesTable();
        configureProductsTable();
        
        logger.info("✅ Tables configured successfully");
    }
    
    /**
     * Configures the categories table for non-scrollable, paginated display
     * Sets fixed cell size and height to show exactly 5 rows
     */
    private void configureCategoriesTable() {
        logger.info("🔧 Configuring categories table for exactly 5 rows...");
        
        // Set exact cell size and table height - this is the key to preventing scrollbars
        categoriesTable.setFixedCellSize(30.0);
        categoriesTable.setPrefHeight(180.0); // 1 header row (30px) + 5 data rows (30px each) = 180px
        categoriesTable.setMinHeight(180.0);
        categoriesTable.setMaxHeight(180.0);
        
        // Simple style to ensure proper column resizing
        categoriesTable.setStyle("-fx-table-column-resize-policy: constrained-resize;");
        
        // Custom row factory to prevent extra rows from appearing
        categoriesTable.setRowFactory(tv -> {
            TableRow<CategoryItem> row = new TableRow<CategoryItem>() {
                @Override
                protected void updateItem(CategoryItem item, boolean empty) {
                    super.updateItem(item, empty);
                    // Ensure we never show more than our page size
                    if (getIndex() >= ROWS_PER_PAGE) {
                        setVisible(false);
                        setManaged(false);
                    } else {
                        setVisible(true);
                        setManaged(true);
                    }
                }
            };
            return row;
        });
        
        logger.info("✅ Categories table configured: fixed height, no scrollbars");
    }
    
    /**
     * Configures the products table for non-scrollable, paginated display
     * Sets fixed cell size and height to show exactly 5 rows
     */
    private void configureProductsTable() {
        logger.info("🔧 Configuring products table for exactly 5 rows...");
        
        // Set exact cell size and table height - this is the key to preventing scrollbars
        productsTable.setFixedCellSize(30.0);
        productsTable.setPrefHeight(180.0); // 1 header row (30px) + 5 data rows (30px each) = 180px
        productsTable.setMinHeight(180.0);
        productsTable.setMaxHeight(180.0);
        
        // Simple style to ensure proper column resizing
        productsTable.setStyle("-fx-table-column-resize-policy: constrained-resize;");
        
        // Custom row factory to prevent extra rows from appearing
        productsTable.setRowFactory(tv -> {
            TableRow<ProductItem> row = new TableRow<ProductItem>() {
                @Override
                protected void updateItem(ProductItem item, boolean empty) {
                    super.updateItem(item, empty);
                    // Ensure we never show more than our page size
                    if (getIndex() >= ROWS_PER_PAGE) {
                        setVisible(false);
                        setManaged(false);
                    } else {
                        setVisible(true);
                        setManaged(true);
                    }
                }
            };
            return row;
        });
        
        logger.info("✅ Products table configured: fixed height, no scrollbars");
    }
    
    private void setupPagination() {
        logger.info("🔧 Setting up pagination controls...");
        
        // Setup category pagination
        categoryPrevPageBtn.setOnAction(e -> handleCategoryPrevPage());
        categoryNextPageBtn.setOnAction(e -> handleCategoryNextPage());
        
        // Setup product pagination
        productPrevPageBtn.setOnAction(e -> handleProductPrevPage());
        productNextPageBtn.setOnAction(e -> handleProductNextPage());
        
        // Add listeners to update pagination when data changes
        categories.addListener((ListChangeListener<CategoryItem>) c -> {
            logger.info("📊 Categories data changed, updating pagination...");
            updateCategoryPagination();
        });
        
        products.addListener((ListChangeListener<ProductItem>) c -> {
            logger.info("📊 Products data changed, updating pagination...");
            updateProductPagination();
        });
        
        logger.info("✅ Pagination controls initialized");
    }
    
    private void updateCategoryPagination() {
        int totalItems = categories.size();
        int pageCount = Math.max(1, (int) Math.ceil((double) totalItems / ROWS_PER_PAGE));
        
        logger.info("📊 Updating category pagination: " + totalItems + " items, " + pageCount + " pages");
        
        // Update navigation buttons state
        categoryPrevPageBtn.setDisable(currentCategoryPageIndex == 0);
        categoryNextPageBtn.setDisable(currentCategoryPageIndex >= pageCount - 1);
        
        // Update page label
        categoryPageLabel.setText(String.format("Page %d of %d", currentCategoryPageIndex + 1, pageCount));
        
        // Update results info
        int fromIndex = currentCategoryPageIndex * ROWS_PER_PAGE + 1;
        int toIndex = Math.min((currentCategoryPageIndex + 1) * ROWS_PER_PAGE, totalItems);
        categoryResultsInfoLabel.setText(String.format("Showing %d-%d of %d results", 
            Math.min(fromIndex, totalItems), toIndex, totalItems));
        
        // Update table for current page
        updateCategoryTableForPage(currentCategoryPageIndex);
        
        logger.info("✅ Category pagination updated - showing page " + (currentCategoryPageIndex + 1) + " of " + pageCount);
    }
    
    private void updateProductPagination() {
        int totalItems = products.size();
        int pageCount = Math.max(1, (int) Math.ceil((double) totalItems / ROWS_PER_PAGE));
        
        logger.info("📊 Updating product pagination: " + totalItems + " items, " + pageCount + " pages");
        
        // Update navigation buttons state
        productPrevPageBtn.setDisable(currentProductPageIndex == 0);
        productNextPageBtn.setDisable(currentProductPageIndex >= pageCount - 1);
        
        // Update page label
        productPageLabel.setText(String.format("Page %d of %d", currentProductPageIndex + 1, pageCount));
        
        // Update results info
        int fromIndex = currentProductPageIndex * ROWS_PER_PAGE + 1;
        int toIndex = Math.min((currentProductPageIndex + 1) * ROWS_PER_PAGE, totalItems);
        productResultsInfoLabel.setText(String.format("Showing %d-%d of %d results", 
            Math.min(fromIndex, totalItems), toIndex, totalItems));
        
        // Update table for current page
        updateProductTableForPage(currentProductPageIndex);
        
        logger.info("✅ Product pagination updated - showing page " + (currentProductPageIndex + 1) + " of " + pageCount);
    }
    
    private void handleCategoryPrevPage() {
        if (currentCategoryPageIndex > 0) {
            currentCategoryPageIndex--;
            updateCategoryPagination();
        }
    }
    
    private void handleCategoryNextPage() {
        int pageCount = (int) Math.ceil((double) categories.size() / ROWS_PER_PAGE);
        if (currentCategoryPageIndex < pageCount - 1) {
            currentCategoryPageIndex++;
            updateCategoryPagination();
        }
    }
    
    private void handleProductPrevPage() {
        if (currentProductPageIndex > 0) {
            currentProductPageIndex--;
            updateProductPagination();
        }
    }
    
    private void handleProductNextPage() {
        int pageCount = (int) Math.ceil((double) products.size() / ROWS_PER_PAGE);
        if (currentProductPageIndex < pageCount - 1) {
            currentProductPageIndex++;
            updateProductPagination();
        }
    }
    
    private void updateCategoryTableForPage(int pageIndex) {
        logger.info("🔄 Updating category table for page: " + pageIndex);
        
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, categories.size());
        
        currentCategoryPage.clear();
        
        if (fromIndex < categories.size() && !categories.isEmpty()) {
            List<CategoryItem> pageItems = categories.subList(fromIndex, toIndex);
            currentCategoryPage.addAll(pageItems);
            logger.info("📋 Category page updated: showing items " + fromIndex + " to " + (toIndex-1) + " (" + pageItems.size() + " items)");
        } else {
            logger.info("📋 No category items to display for page " + pageIndex);
        }
        
        // Ensure the table never shows more than 5 rows
        while (currentCategoryPage.size() > ROWS_PER_PAGE) {
            currentCategoryPage.remove(currentCategoryPage.size() - 1);
        }
        
        // Force table refresh
        categoriesTable.refresh();
    }
    
    private void updateProductTableForPage(int pageIndex) {
        logger.info("🔄 Updating product table for page: " + pageIndex);
        
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, products.size());
        
        currentProductPage.clear();
        
        if (fromIndex < products.size() && !products.isEmpty()) {
            List<ProductItem> pageItems = products.subList(fromIndex, toIndex);
            currentProductPage.addAll(pageItems);
            logger.info("📋 Product page updated: showing items " + fromIndex + " to " + (toIndex-1) + " (" + pageItems.size() + " items)");
        } else {
            logger.info("📋 No product items to display for page " + pageIndex);
        }
        
        // Ensure the table never shows more than 5 rows
        while (currentProductPage.size() > ROWS_PER_PAGE) {
            currentProductPage.remove(currentProductPage.size() - 1);
        }
        
        // Force table refresh
        productsTable.refresh();
    }
    
    private void setupEventHandlers() {
        addCategoryButton.setOnAction(e -> handleAddCategory());
        addProductButton.setOnAction(e -> handleAddProduct());
        
        // Update category combo box when categories change
        categories.addListener((javafx.collections.ListChangeListener.Change<? extends CategoryItem> c) -> {
            updateCategoryComboBox();
        });
    }
    
    private void setupCategoryActionsColumn() {
        categoryActionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button();
            {
                // Set up delete button with icon
                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/delete_icon.png")));
                deleteIcon.setFitWidth(14);
                deleteIcon.setFitHeight(14);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.getStyleClass().addAll("icon-button", "delete-button");
                deleteButton.setTooltip(new Tooltip("Delete Category"));
                
                // Add style class to the cell
                getStyleClass().add("actions-cell");
                
                deleteButton.setOnAction(event -> {
                    CategoryItem category = getTableView().getItems().get(getIndex());
                    handleDeleteCategory(category);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }
    
    private void setupProductActionsColumn() {
        productActionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final HBox container;
            
            {
                // Set up edit button with icon
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/edit_icon.png")));
                editIcon.setFitWidth(14);
                editIcon.setFitHeight(14);
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().addAll("icon-button", "edit-button");
                editButton.setTooltip(new Tooltip("Edit Flavor"));
                
                // Set up delete button with icon
                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/delete_icon.png")));
                deleteIcon.setFitWidth(14);
                deleteIcon.setFitHeight(14);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.getStyleClass().addAll("icon-button", "delete-button");
                deleteButton.setTooltip(new Tooltip("Delete Flavor"));
                
                container = new HBox(4);
                container.setAlignment(Pos.CENTER_LEFT);
                
                // Add style class to the cell
                getStyleClass().add("actions-cell");
                
                container.getChildren().addAll(editButton, deleteButton);
                
                editButton.setOnAction(event -> {
                    ProductItem product = getTableView().getItems().get(getIndex());
                    handleEditProduct(product);
                });
                
                deleteButton.setOnAction(event -> {
                    ProductItem product = getTableView().getItems().get(getIndex());
                    handleDeleteProduct(product);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }
    
    private void handleAddCategory() {
        String categoryName = categoryNameField.getText().trim();
        if (categoryName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Category name cannot be empty");
            return;
        }
        
        // Check for duplicates
        if (categories.stream().anyMatch(c -> c.getName().equalsIgnoreCase(categoryName))) {
            showAlert(Alert.AlertType.WARNING, "Duplicate Category", "A category with this name already exists");
            return;
        }
        
        // Add new category at the beginning (index 0) to show at top
        categories.add(0, new CategoryItem(categoryName));
        categoryNameField.clear();
        saveCategories();
        
        // Navigate to first page to show the new entry
        currentCategoryPageIndex = 0;
        updateCategoryPagination();
        
        logger.info("✅ New category added at top: " + categoryName);
    }
    
    private void handleDeleteCategory(CategoryItem category) {
        // Check if category has products
        if (products.stream().anyMatch(p -> p.getCategory().equals(category.getName()))) {
            showAlert(Alert.AlertType.WARNING, "Cannot Delete", 
                     "This category has products. Delete or move the products first.");
            return;
        }
        
        categories.remove(category);
        saveCategories();
        updateCategoryPagination(); // Update pagination after deleting
    }
    
    private void handleAddProduct() {
        String category = productCategoryCombo.getValue();
        String name = productNameField.getText().trim();
        String description = productDescriptionField.getText().trim();
        String priceText = productPriceField.getText().trim();
        
        if (category == null || name.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "All fields are required");
            return;
        }
        
        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Price", "Price must be greater than 0");
                return;
            }
            
            if (productBeingEdited != null) {
                // We're editing an existing product - keep it at its original position
                int index = products.indexOf(productBeingEdited);
                if (index != -1) {
                    // Replace the product at its original position
                    products.set(index, new ProductItem(category, name, description, String.format("₱%.2f", price)));
                    logger.info("✅ Product edited at original position: " + name);
                }
                productBeingEdited = null;
                addProductButton.setText("Add Flavor");
            } else {
                // We're adding a new product - add at the beginning (index 0) to show at top
                products.add(0, new ProductItem(category, name, description, String.format("₱%.2f", price)));
                
                // Navigate to first page to show the new entry
                currentProductPageIndex = 0;
                logger.info("✅ New product added at top: " + name);
            }
            
            clearProductForm();
            saveProducts();
            updateProductPagination(); // Update pagination after adding
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Price", "Please enter a valid number for price");
        }
    }
    
    private ProductItem productBeingEdited = null;

    private void handleEditProduct(ProductItem product) {
        // Store the product being edited
        productBeingEdited = product;
        
        // Populate the form fields
        productCategoryCombo.setValue(product.getCategory());
        productNameField.setText(product.getName());
        productDescriptionField.setText(product.getDescription());
        productPriceField.setText(product.getPrice().replace("₱", ""));
        
        // Change the Add Product button to Save Changes
        addProductButton.setText("Save Changes");
        
        // Focus on the name field instead of category
        productNameField.requestFocus();
    }
    
    private void handleDeleteProduct(ProductItem product) {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this flavor?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            products.remove(product);
            saveProducts();
            updateProductPagination(); // Update pagination after deleting
        }
    }
    
    private void updateCategoryComboBox() {
        List<String> categoryNames = categories.stream()
                .map(CategoryItem::getName)
                .toList();
        productCategoryCombo.setItems(FXCollections.observableArrayList(categoryNames));
    }
    
    private void clearProductForm() {
        productCategoryCombo.setValue(null);
        productNameField.clear();
        productDescriptionField.clear();
        productPriceField.clear();
        
        // Reset edit mode if we're canceling an edit
        if (productBeingEdited != null) {
            productBeingEdited = null;
            addProductButton.setText("Add Flavor");
        }
    }
    
    private void loadData() {
        logger.info("📂 Loading data from files...");
        loadProducts(); // Load products first
        extractCategoriesFromProducts(); // Extract categories dynamically
        
        // Initialize pagination after data is loaded
        updateCategoryPagination();
        updateProductPagination();
        
        logger.info("✅ Data loaded and pagination initialized");
    }
    
    /**
     * Extract categories dynamically from products - no separate categories.txt file needed
     */
    private void extractCategoriesFromProducts() {
        categories.clear();
        Set<String> uniqueCategories = new LinkedHashSet<>(); // Preserve order, avoid duplicates
        
        for (ProductItem product : products) {
            uniqueCategories.add(product.getCategory());
        }
        
        // Convert to CategoryItem objects
        for (String categoryName : uniqueCategories) {
            categories.add(new CategoryItem(categoryName));
        }
        
        // Update the category combo box
        updateCategoryComboBox();
        
        logger.info("📋 Extracted " + categories.size() + " categories dynamically from products");
    }
    
    private void loadProducts() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(PRODUCTS_FILE));
            products.clear();
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    products.add(new ProductItem(
                        parts[0].trim(), // category
                        parts[1].trim(), // name
                        parts[2].trim(), // description
                        parts[3].trim()  // price
                    ));
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not load products", e);
        }
    }
    
    /**
     * Categories are now extracted dynamically from products - no separate file to save
     * This method now just triggers data change notifications
     */
    private void saveCategories() {
        logger.info("💾 Categories are dynamic - extracted from products automatically");
        
        // Notify all registered controllers of data changes
        notifyDataChangeCallbacks();
        // Keep legacy callback for compatibility
        triggerDataChangedCallback();
    }
    
    private void saveProducts() {
        try {
            List<String> lines = products.stream()
                    .map(p -> String.format("%s|%s|%s|%s",
                            p.getCategory(),
                            p.getName(),
                            p.getDescription(),
                            p.getPrice()))
                    .toList();
            Files.write(Paths.get(PRODUCTS_FILE), lines);
            logger.info("💾 Products saved successfully");
            
            // Re-extract categories from updated products
            extractCategoriesFromProducts();
            
            // Notify all registered controllers of data changes
            notifyDataChangeCallbacks();
            // Keep legacy callback for compatibility
            triggerDataChangedCallback();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not save products", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save products");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Data classes for tables
    public static class CategoryItem {
        private final String name;
        
        public CategoryItem(String name) {
            this.name = name;
        }
        
        public String getName() { return name; }
    }
    
    public static class ProductItem {
        private final String category;
        private final String name;
        private final String description;
        private final String price;
        
        public ProductItem(String category, String name, String description, String price) {
            this.category = category;
            this.name = name;
            this.description = description;
            this.price = price;
        }
        
        public String getCategory() { return category; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getPrice() { return price; }
    }
    
    /**
     * Set callback to be triggered when data changes occur
     * @param callback The callback to run when data changes
     */
    public void setOnDataChangedCallback(Runnable callback) {
        this.onDataChangedCallback = callback;
        logger.info("📡 Real-time update callback registered");
    }
    
    /**
     * Trigger the data changed callback if it exists
     */
    private void triggerDataChangedCallback() {
        if (onDataChangedCallback != null) {
            logger.info("📡 Triggering real-time data change callback...");
            onDataChangedCallback.run();
        }
    }
} 