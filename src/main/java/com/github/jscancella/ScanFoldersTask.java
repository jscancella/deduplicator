package com.github.jscancella;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jscancella.walker.FileCountWalker;
import com.github.jscancella.walker.ScanFoldersWalker;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ScanFoldersTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(ScanFoldersTask.class);
  
  private final VBox duplicateFilesVBox;
  private final ListView<File> foldersToScanListView;
  private final Button deleteButton;
  private final ProgressBar totalProgressBar;
  private final ProgressBar currentFileProgressBar;
  
  public ScanFoldersTask(final VBox duplicateFilesVBox, 
          final ListView<File> foldersToScanListView, 
          final Button deleteButton, 
          final ProgressBar progressBar,
          final ProgressBar currentFileProgressBar) {
    this.duplicateFilesVBox = duplicateFilesVBox;
    this.foldersToScanListView = foldersToScanListView;
    this.deleteButton = deleteButton;
    this.totalProgressBar = progressBar;
    this.currentFileProgressBar = currentFileProgressBar;
  }

  @Override
  public void run() {
    totalProgressBar.setProgress(0); //show that the application is working
    Platform.runLater(() -> {duplicateFilesVBox.getChildren().clear();});
    try {
      final ScanFoldersWalker scanFoldersWalker = new ScanFoldersWalker(getFileCount(), totalProgressBar, currentFileProgressBar);
      
      for(File folder : foldersToScanListView.getItems()) {
        logger.debug("Walking folder [{}] to generate checksums.", folder);  
        Files.walkFileTree(folder.toPath(), scanFoldersWalker);
      }
      
      List<List<Path>> duplicates =  scanFoldersWalker.getDuplicateGroups();
      logger.debug("Number of duplicate groups [{}]", duplicates.size());
      Platform.runLater(() -> {deleteButton.setDisable(duplicates.size() == 0);});
      Platform.runLater(() -> {addDuplicatesToUI(duplicates);});
    }
    catch(IOException e) {
      logger.error("Failed to scan folder!", e);
    }
    Platform.runLater(() -> {totalProgressBar.setProgress(1);}); //show that the application is done
  }
  
  private int getFileCount() {
    try {
      FileCountWalker countWalker = new FileCountWalker();
      for(File folder : foldersToScanListView.getItems()) {
        logger.debug("Counting files in folder [{}]", folder);
        Files.walkFileTree(folder.toPath(), countWalker);
      }
      return countWalker.getCount();
    } catch (IOException e) {
      logger.error("Unable to get count of files!");
      return 1; //so no divide by zero error
    }
  }
  
  private void addDuplicatesToUI(final List<List<Path>> duplicates) {
    for(List<Path> duplicateGroup : duplicates) {
      logger.debug("Processing duplicate group");
      for(Path file : duplicateGroup) {
        logger.debug("Adding duplicate file [{}]  to UI as checkbox.", file);
        final CheckBox checkbox = new CheckBox(file.toAbsolutePath().toString());
        checkbox.setMinWidth(Region.USE_PREF_SIZE);
        duplicateFilesVBox.getChildren().add(checkbox);
      }
      logger.debug("Adding separator to UI.");
      duplicateFilesVBox.getChildren().add(new Separator(Orientation.HORIZONTAL));
    }
  }
}
