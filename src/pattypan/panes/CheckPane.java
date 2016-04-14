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
package pattypan.panes;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pattypan.Session;
import pattypan.UploadElement;
import pattypan.elements.WikiPane;

public class CheckPane extends WikiPane {

  Stage stage;
  VBox fileListContainer = new VBox(4);

  public CheckPane(Stage stage) {
    super(stage, 1.34);
    this.stage = stage;

    setContent();
  }

  public WikiPane getContent() {
    return this;
  }

  private WikiPane setContent() {
    addElement("check-intro", 40);

    for (UploadElement ue : Session.FILES_TO_UPLOAD) {
      Hyperlink label = new Hyperlink(ue.getData("name"));
      label.setOnAction(event -> {
        try {
          URI link = new URI("https://commons.wikimedia.org/wiki/Special:ExpandTemplates"
                  + "?wpRemoveComments=true"
                  + "&wpInput=" + URLEncoder.encode(ue.getWikicode(), "UTF-8")
                  + "&wpContextTitle=" + URLEncoder.encode(ue.getData("name"), "UTF-8"));
          Desktop.getDesktop().browse(link);
        } catch (IOException | URISyntaxException ex) {
        }
      });

      fileListContainer.getChildren().add(label);
    }

    addElement(new ScrollPane(fileListContainer));

    prevButton.linkTo("LoadPane", stage);
    nextButton.linkTo("LoginPane", stage);

    return this;
  }
}
