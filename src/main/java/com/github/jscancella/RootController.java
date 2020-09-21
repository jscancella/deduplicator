package com.github.jscancella;

import java.io.File;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

public class RootController {
  private static final Logger logger = LoggerFactory.getLogger(RootController.class);
  private static final ResourceBundle messages = ResourceBundle.getBundle("MessageBundle");
  
  @FXML private VBox rootPane;
  @FXML private ProgressBar progressBar;
  @FXML private ListView<File> foldersToScanListView;
  @FXML private VBox duplicateFilesVBox;
  @FXML private Button deleteButton;
  @FXML private Button scanFoldersButton;
  @FXML private Button removeFolderButton;
  
  @FXML
  public void initialize() {
    logger.debug("Running initalize()");
  }
  
  @FXML protected void handleDeleteButtonAction(ActionEvent event) {
    logger.debug("Running handleDeleteButtonAction()");
    
    duplicateFilesVBox.getChildren().stream()
      .filter(n -> n instanceof CheckBox) //remove separators
      .map(n -> (CheckBox)n)
      .filter(cb -> cb.isSelected())
      .map(cb -> Paths.get(cb.getText()))
      .forEach(file -> logger.info("DEBUG: Would have deleted [{}]", file));
  }
  
  @FXML protected void handleScanFoldersButtonAction(ActionEvent event) {
    logger.debug("Running handleScanFoldersButtonAction()");
    progressBar.setProgress(-1D); //show that the application is working
    duplicateFilesVBox.getChildren().clear();
    
    Platform.runLater(new ScanFoldersTask(duplicateFilesVBox, foldersToScanListView, deleteButton, progressBar));
  }
  
  @FXML protected void handleAddFolderButtonAction(ActionEvent event) {
    logger.debug("Running handleAddFolderButtonAction()");    
    final DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("JavaFX Projects");
    final File selectedDirectory = chooser.showDialog(rootPane.getScene().getWindow());
    logger.debug("Selected folder [{}]", selectedDirectory);
    if(selectedDirectory != null) {
      scanFoldersButton.setDisable(false);
      removeFolderButton.setDisable(false);
      foldersToScanListView.getItems().add(selectedDirectory);
    }
  }
  
  @FXML protected void handleQuitAction(ActionEvent event) {
    logger.debug("Running handleQuitAction()");
    Platform.exit();
  }
  
  @FXML protected void handleAboutAction(ActionEvent event) {
    logger.debug("Running handleAboutAction()");
    final Dialog<String> dialog = new Dialog<>();
    dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonData.OK_DONE));
    dialog.setResizable(true);
    dialog.setContentText(messages.getString("version") + "\n" + messages.getString("released"));    
    dialog.showAndWait();
  }
  
  @FXML protected void handleRemoveFolderAction(ActionEvent event) {
    logger.debug("Running handleRemoveFolderAction()");
    foldersToScanListView.getItems().removeAll(foldersToScanListView.getSelectionModel().getSelectedItems());
    scanFoldersButton.setDisable(foldersToScanListView.getItems().size() == 0);
    removeFolderButton.setDisable(foldersToScanListView.getItems().size() == 0);
  }
}
