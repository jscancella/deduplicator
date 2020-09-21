package com.github.jscancella;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashWalker extends SimpleFileVisitor<Path> {
  private static final Logger logger = LoggerFactory.getLogger(HashWalker.class);
  
  private final ConcurrentHashMap<String, List<Path>> hashes = new ConcurrentHashMap<>();
  
  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
    try {
      final String hash = Hasher.INSTANCE.hash(file);
      logger.debug("Hash of file [{}] is [{}]", file, hash);
      List<Path> hashGroup = hashes.getOrDefault(hash, new ArrayList<Path>());
      hashGroup.add(file);
      hashes.put(hash, hashGroup);
    } catch (NoSuchAlgorithmException | IOException e) {
      logger.error("Error generating hash of file [{}]", file, e);
    }
    return FileVisitResult.CONTINUE;
  }

  public List<List<Path>>getDuplicateGroups() {
    return hashes.values().stream()
        .filter(list -> list.size() > 1)
        .collect(Collectors.toList());
  }
}
