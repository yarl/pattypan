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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.Scene;
import org.wikipedia.Wiki;
import pattypan.LogManager;
import java.util.logging.Logger;

public final class Session {
  
  private Session() {};
  
  public static Map<String, Scene> SCENES = new HashMap<>();
  
  public static File DIRECTORY;
  public static File FILE;
  
  public static ArrayList<File> FILES = new ArrayList<>();

  public static Logger LOGGER = new LogManager().logger;

  public static String METHOD = "template";
  public static String TEMPLATE = "Artwork";
  public static String WIKICODE = "";
  public static ArrayList<String> VARIABLES = new ArrayList<>(Arrays.asList("path", "name"));
  
  public static Wiki WIKI = new Wiki("commons.wikimedia.org");
  public static ArrayList<UploadElement> FILES_TO_UPLOAD = new ArrayList<>();
  
  static {
    WIKI.setUserAgent(Settings.USERAGENT);
  }
}
