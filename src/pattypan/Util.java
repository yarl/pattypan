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

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
  private final static ArrayList<String> allowedExtentionImage
          = new ArrayList<>(Arrays.asList("png", "gif", "jpg", "jpeg", "tiff", "tif", "xcf"));

  public static ArrayList<String> getVariablesFromString(String text) {
    final Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
    Matcher m = pattern.matcher(text);
    ArrayList<String> results = new ArrayList<>();

    while (m.find()) {
      results.add(m.group(1));
    }
    return results;
  }

  public static String getExtFromFilename(String filename) {
    String extension = "";

    int i = filename.lastIndexOf('.');
    if (i >= 0) {
      extension = filename.substring(i + 1).toLowerCase();
    }
    return extension;
  }

  public static File[] getFilesAllowedToUpload(File directory) {
    return directory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File directory, String name) {
        return isFileAllowedToUpload(name);
      }
    });
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
}
