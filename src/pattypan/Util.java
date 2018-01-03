/*
 * The MIT License
 *
 * Copyright 2016 Pawel Marynowski.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pattypan;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;

public final class Util {

  private Util() {}

  public static int WINDOW_WIDTH = 750;
  public static int WINDOW_HEIGHT = 550;
  static ResourceBundle bundle = ResourceBundle.getBundle("pattypan/text/messages");

  public static String text(String key) {
    try {
      return bundle.getString(key);
    } catch (final MissingResourceException ex) {
      return "";
    }
  }

  public static String text(String key, Object... vars) {
    String text = text(key);
    return String.format(text, vars);
  }

  /**
   * Open URL in browser
   * 
   * @source http://stackoverflow.com/a/28807079
   * @param url website URL
   */
  public static void openUrl(String url) {
    String os = System.getProperty("os.name").toLowerCase();

    try {
      if (os.contains("win")) {
        Desktop.getDesktop().browse(new URI(url));
      } else if (os.contains("mac")) {
        Runtime rt = Runtime.getRuntime();
        rt.exec("open " + url);
      } else {
        new BrowserLauncher().openURLinBrowser(url);
      }
    } catch (BrowserLaunchingInitializingException | UnsupportedOperatingSystemException ex) {
      Session.LOGGER.log(Level.WARNING, null, ex);
    } catch (URISyntaxException | IOException ex) {
      Session.LOGGER.log(Level.WARNING, null, ex);
    }
  }

  /* row and column utils */
  public static ColumnConstraints newColumn(int value) {
    return newColumn(value, "%", HPos.CENTER);
  }

  public static ColumnConstraints newColumn(int value, String unit) {
    return newColumn(value, unit, HPos.CENTER);
  }

  public static ColumnConstraints newColumn(int value, String unit, HPos position) {
    ColumnConstraints col = new ColumnConstraints();
    if (unit.equals("%")) {
      col.setPercentWidth(value);
    }
    if (unit.equals("px")) {
      col.setMaxWidth(value);
      col.setMinWidth(value);
    }

    if (position != null) {
      col.setHalignment(position);
    }
    return col;
  }

  /* file utils */
  private final static ArrayList<String> allowedExtentionImage = new ArrayList<>(
          Arrays.asList("djvu", "flac", "gif", "jpg", "jpeg", "mid",
                  "oga", "ogg","ogv", "opus", "pdf", "png", "svg", "tiff",
                  "tif", "wav", "webm", "webp", "xcf")
  );

  // https://commons.wikimedia.org/wiki/MediaWiki:Filename-prefix-blacklist
  private final static ArrayList<String> filenamePrefixBlacklist = new ArrayList<>(
    Arrays.asList("CIMG", "DSC_", "DSCF", "DSCN", "DUW", "GEDC",
            "IMG", "JD", "MGP", "PICT", "Imagen", "FOTO", "DSC",
            "SANY", "SAM")
  );

  public static boolean stringHasValidFileExtension(String string) {
    return allowedExtentionImage.parallelStream().anyMatch(string::endsWith);
  }

  public static boolean isPossibleBadFilename(String name) {
    return filenamePrefixBlacklist.parallelStream().anyMatch(name::startsWith);
  }

  public static String getNameFromFilename(String filename) {
    int pos = filename.lastIndexOf(".");
    if (pos > 0) {
      filename = filename.substring(0, pos);
    }
    return filename;
  }

  public static String getExtFromFilename(String filename) {
    String extension = "";

    int i = filename.lastIndexOf('.');
    if (i >= 0) {
      extension = filename.substring(i + 1).toLowerCase();
    }
    return extension;
  }

  public static File[] getFilesAllowedToUpload(File directory, boolean includeSubdirectories) {
    ArrayList<File> files = new ArrayList<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory.toPath())) {
      for (Path path : stream) {
        if (path.toFile().isDirectory() && includeSubdirectories) {
          File[] dirFiles = getFilesAllowedToUpload(path.toFile(), includeSubdirectories);
          ArrayList<File> dirFilesList = new ArrayList<>(Arrays.asList(dirFiles));
          files.addAll(dirFilesList);
        } else if (isFileAllowedToUpload(path.toFile().getName())) {
          files.add(path.toFile());
        }
      }
    } catch (IOException e) {
    }
    return files.stream().toArray(File[]::new);
  }

  public static File[] getFilesAllowedToUpload(File directory, String ext) {
    return directory.listFiles((File dir, String name)
            -> name.toLowerCase().endsWith(ext)
    );
  }

  public static Map<String, Integer> getFilesByExtention(File[] files) {
    Map<String, Integer> map = new HashMap<>();

    for (File file : files) {
      String ext = getExtFromFilename(file.getName());
      if (map.containsKey(ext)) {
        int count = map.get(ext);
        map.replace(ext, ++count);
      } else {
        map.put(ext, 1);
      }
    }
    return map;
  }

  public static boolean isFileAllowedToUpload(String name) {
    return allowedExtentionImage.indexOf(getExtFromFilename(name)) > -1;
  }

  public static String readUrl(String urlString) throws Exception {
    BufferedReader reader = null;
    try {
      URL url = new URL(urlString);
      reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuilder buffer = new StringBuilder();
      int read;
      char[] chars = new char[1024];
      while ((read = reader.read(chars)) != -1) {
        buffer.append(chars, 0, read);
      }

      return buffer.toString();
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  public static boolean validUrl(String path) {
    try {
      URL url = new URL(path);
      url.toURI();
      return true;
    } catch(Exception e) {
      return false;
    }
  }

  /**
   * Compares two version strings.
   *
   * Use this instead of String.compareTo() for a non-lexicographical comparison
   * that works for version strings. e.g. "1.10".compareTo("1.6").
   *
   * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
   *
   * @param str1 a string of ordinal numbers separated by decimal points.
   * @param str2 a string of ordinal numbers separated by decimal points.
   * @return The result is a negative integer if str1 is _numerically_ less than
   * str2. The result is a positive integer if str1 is _numerically_ greater
   * than str2. The result is zero if the strings are _numerically_ equal.
   * @source http://stackoverflow.com/a/6702029/1418878
   */
  public static Integer versionCompare(String str1, String str2) {
    String[] vals1 = str1.split("\\.");
    String[] vals2 = str2.split("\\.");
    int i = 0;
    // set index to first non-equal ordinal or length of shortest version string
    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
      i++;
    }
    // compare first non-equal ordinal number
    if (i < vals1.length && i < vals2.length) {
      int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
      return Integer.signum(diff);
    } // the strings are equal or one string is a substring of the other
    // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
    else {
      return Integer.signum(vals1.length - vals2.length);
    }
  }

  /**
   * Returns keys by value
   *
   * @param map map with data
   * @param value searched value
   * @return list of keys that has searched value
   * @source http://stackoverflow.com/a/2904266/1418878
   */
  public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
    return map.entrySet()
            .stream()
            .filter(entry -> Objects.equals(entry.getValue(), value))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
  }
}
