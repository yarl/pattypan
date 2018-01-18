package pattypan;

import java.io.IOException;
import java.lang.SecurityException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import pattypan.Util;

public class LogManager {
  public Logger logger = Logger.getLogger(Main.class.getName());

  public LogManager() {
    try {
      Path logFileLocation = Paths.get(Util.getApplicationDirectory() + "/logs");
      if (!Files.exists(logFileLocation)) {
        Files.createDirectories(logFileLocation);
      }

      FileHandler fh = new FileHandler(logFileLocation.toString() + "/pattypan.log", true);
      fh.setFormatter(new SimpleFormatter());
      logger.addHandler(fh);
      logger.addHandler(new ConsoleHandler());
      logger.setUseParentHandlers(false); // remove console prefix for privacy
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
