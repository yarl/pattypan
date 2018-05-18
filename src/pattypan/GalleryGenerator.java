/*
 * The MIT License
 *
 * Copyright 2016-2018 Pawel Marynowski.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.security.auth.login.LoginException;

public class GalleryGenerator {

  private ArrayList<String> fileNames = new ArrayList<>();

  public GalleryGenerator() {
    fileNames.add("== " + Session.FILE.getName() + " ==");
    fileNames.add("<gallery>");
    Session.FILES_TO_UPLOAD.forEach((file) -> {
      fileNames.add(file.getName());
    });
    fileNames.add("</gallery>");
}
  
  public String getGallery() {
    return String.join("\n", fileNames);
  }
  
  public void publish() {
    String text = getGallery();
    String title = Settings.getSetting("user") + "/gallery";
    
    try {
      Session.WIKI.edit(title, text, "add gallery", -1);
    } catch (IOException ex) {
      Session.LOGGER.log(Level.WARNING,
        "Error occurred during gallery upload: {0}",
        new String[]{"no internet connection"}
      );
    } catch (LoginException ex) {
      Session.LOGGER.log(Level.WARNING,
        "Error occurred during gallery upload: {0}",
        new String[]{"not logged in"}
      );
    }
  }
}
