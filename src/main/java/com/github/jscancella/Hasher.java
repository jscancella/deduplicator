package com.github.jscancella;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Hasher {
  INSTANCE; //enforce singleton
  
  private static final int _64_KB = 1024 * 64;
  private static final int CHUNK_SIZE = _64_KB;
  private static final Logger logger = LoggerFactory.getLogger(ScanFoldersTask.class);
  
  public String hash(final Path path) throws NoSuchAlgorithmException, IOException {
    logger.debug("Hashing file [{}]", path);
    final  MessageDigest md = MessageDigest.getInstance("MD5");
    updateMessageDigest(path, md);
    return formatMessageDigest(md);
  }
  
  private static void updateMessageDigest(final Path path, final MessageDigest messageDigest) throws IOException{
    try(InputStream inputStream = new BufferedInputStream(Files.newInputStream(path, StandardOpenOption.READ))){
      final byte[] buffer = new byte[CHUNK_SIZE];
      int read = inputStream.read(buffer);

      while(read != -1){
        messageDigest.update(buffer, 0, read);
        read = inputStream.read(buffer);
      }
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

}
