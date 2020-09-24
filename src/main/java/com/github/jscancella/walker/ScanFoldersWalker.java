package com.github.jscancella.walker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

public class ScanFoldersWalker extends SimpleFileVisitor<Path> {
  private static final Logger logger = LoggerFactory.getLogger(ScanFoldersWalker.class);
  private static final int _64_KB = 1024 * 64;
  private static final int CHUNK_SIZE = _64_KB;
  
  private final ConcurrentHashMap<String, List<Path>> hashes = new ConcurrentHashMap<>();
  private final int totalNumberOfFiles;
  private final ProgressBar totalProgressBar;
  private final ProgressBar currentFileProgressBar;
  private double currentFileProgress;
  private double totalFileProgress;
  
  public ScanFoldersWalker(final int totalNumberOfFiles, final ProgressBar totalProgressBar, final ProgressBar currentFileProgressBar) {
    this.totalNumberOfFiles = totalNumberOfFiles;
    this.totalProgressBar = totalProgressBar;
    this.currentFileProgressBar = currentFileProgressBar;
    this.currentFileProgress = 0D;
    this.totalFileProgress = 0D;
  }
  
  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
    logger.debug("Visiting file [{}]", file);
    currentFileProgress = 0D;
    hash(file, hashes);
    
    totalFileProgress = totalFileProgress + (1/(double)totalNumberOfFiles);
    logger.debug("Setting totalFileProcess to [{}]", totalFileProgress);
    Platform.runLater(() -> { totalProgressBar.setProgress(totalFileProgress); });
    return FileVisitResult.CONTINUE;
  }
  
  private void hash(final Path file, final ConcurrentHashMap<String, List<Path>> hashes) {
    try {
      final MessageDigest md = MessageDigest.getInstance("MD5");
      updateMessageDigest(file, md, currentFileProgressBar);
      final String hash = formatMessageDigest(md);
      logger.debug("Hash of file [{}] is [{}]", file, hash);
      List<Path> hashGroup = hashes.getOrDefault(hash, new ArrayList<Path>());
      hashGroup.add(file);
      hashes.put(hash, hashGroup);
    } catch (NoSuchAlgorithmException | IOException e) {
      logger.error("Error generating hash of file [{}]", file, e);
    }
  }
  
  private void updateMessageDigest(final Path path, final MessageDigest messageDigest, final ProgressBar currentFileProgressBar) throws IOException{
    try(InputStream inputStream = new BufferedInputStream(Files.newInputStream(path, StandardOpenOption.READ))){
      final double size = Files.size(path);
      final byte[] buffer = new byte[CHUNK_SIZE];
      int read = inputStream.read(buffer);
      currentFileProgress = read/size;
      Platform.runLater(() -> { currentFileProgressBar.setProgress(currentFileProgress); });

      while(read != -1){
        messageDigest.update(buffer, 0, read);
        read = inputStream.read(buffer);
        currentFileProgress = currentFileProgress + (read/size);
        logger.debug("Updating current file progress to [{}]", currentFileProgress);
        Platform.runLater(() -> { currentFileProgressBar.setProgress(currentFileProgress); });
      }
      Platform.runLater(() -> { currentFileProgressBar.setProgress(1D); }); //done with current file
    }
  }
  
  
  private static String formatMessageDigest(final MessageDigest messageDigest){
    logger.debug("Formatting messageDigest into hex hash");
    try(Formatter formatter = new Formatter()){
      for (final byte b : messageDigest.digest()) {
        formatter.format("%02x", b);
      }
      
      return formatter.toString();
    }
  }

  public List<List<Path>>getDuplicateGroups() {
    return hashes.values().stream()
        .filter(list -> list.size() > 1)
        .collect(Collectors.toList());
  }
}
