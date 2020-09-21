package com.github.jscancella;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class ScanFoldersTask extends Task<Void>{
  private static final Logger logger = LoggerFactory.getLogger(ScanFoldersTask.class);
  
  private final VBox duplicateFilesVBox;
  private final ListView<File> foldersToScanListView;
  private final Button deleteButton;
  private final ProgressBar progressBar;
  
  public ScanFoldersTask(final VBox duplicateFilesVBox, 
          final ListView<File> foldersToScanListView, 
          final Button deleteButton, 
          final ProgressBar progressBar) {
    this.duplicateFilesVBox = duplicateFilesVBox;
    this.foldersToScanListView = foldersToScanListView;
    this.deleteButton = deleteButton;
    this.progressBar = progressBar;
  }

  @Override
  protected Void call() throws Exception {
    duplicateFilesVBox.getChildren().clear();
    try {
      for(File folder : foldersToScanListView.getItems()) {
        logger.debug("Walking folder [{}] to generate checksums.", folder);
        final HashWalker walker = new HashWalker();
        Files.walkFileTree(folder.toPath(), walker);
        final List<List<Path>> duplicates = walker.getDuplicateGroups();
        
        logger.debug("Number of duplicate groups [{}]", duplicates.size());
        deleteButton.setDisable(duplicates.size() == 0);
        
        addDuplicatesToUI(duplicates);
      }
    }
    catch(IOException e) {
      logger.error("Failed to scan folder!", e);
    }
    progressBar.setProgress(1); //show that the application is done
    
    return null;
  }
  
  private void addDuplicatesToUI(final List<List<Path>> duplicates) {
    for(List<Path> duplicateGroup : duplicates) {
      logger.debug("Processing duplicate group");
      for(Path file : duplicateGroup) {
        logger.debug("Adding duplicate file [{}]  to UI as checkbox.", file);
        duplicateFilesVBox.getChildren().add(new CheckBox(file.toAbsolutePath().toString()));
      }
      logger.debug("Adding separator to UI.");
      duplicateFilesVBox.getChildren().add(new Separator(Orientation.HORIZONTAL));
    }
  }
}
