package com.github.jscancella;

import java.io.File;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

public class RootController {
  private static final Logger logger = LoggerFactory.getLogger(RootController.class);
  private static final ResourceBundle messages = ResourceBundle.getBundle("MessageBundle");
  
  @FXML private VBox rootPane;
  @FXML private ProgressBar progressBar;
  @FXML private ScrollPane foldersToScanScrollPane;
  @FXML private ScrollPane duplicateFilesFoundScrollPane;
  @FXML private Button deleteButton;
  @FXML private Button scanFoldersButton;
  
  @FXML
  public void initialize() {
    //TODO
    logger.info("Running initalize()");
  }
  
  @FXML protected void handleDeleteButtonAction(ActionEvent event) {
    //TODO delete selected files
    logger.info("Running handleDeleteButtonAction()");
  }
  
  @FXML protected void handleScanFoldersButtonAction(ActionEvent event) {
    //TODO
    logger.info("Running handleScanFoldersButtonAction()");
  }
  
  @FXML protected void handleAddFolderButtonAction(ActionEvent event) {
    //TODO
    logger.info("Running handleAddFolderButtonAction()");
    
    final DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("JavaFX Projects");
    final File selectedDirectory = chooser.showDialog(rootPane.getScene().getWindow());
    logger.info("Selected folder [{}]", selectedDirectory);
  }
  
  @FXML protected void handleQuitAction(ActionEvent event) {
    //TODO
    logger.info("Running handleQuitAction()");
    Platform.exit();
  }
  
  @FXML protected void handleAboutAction(ActionEvent event) {
    logger.info("Running handleAboutAction()");
    final Dialog<String> dialog = new Dialog<>();
    dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonData.OK_DONE));
    dialog.setResizable(true);
    dialog.setContentText(messages.getString("version") + "\n" + messages.getString("released"));    
    dialog.showAndWait();
  }
}
