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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pattypan.Session;
import pattypan.UploadElement;
import pattypan.Util;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiPane;
import pattypan.elements.WikiScrollPane;

public class CheckPane extends WikiPane {

  Stage stage;
  VBox fileListContainer = new VBox(4);
  VBox detailsContainer = new VBox(4);
  Hyperlink prevLabel = new Hyperlink();

  public CheckPane(Stage stage) {
    super(stage, 1.34);
    this.stage = stage;

    setContent();
  }

  /*
   * set content and actions
   *****************************************************************************
   */
  public WikiPane getContent() {
    return this;
  }

  private void setContent() {
    addElement("check-intro", 40);

    Session.FILES_TO_UPLOAD.stream().map(uploadElement -> {
      String name = uploadElement.getData("name");
      Hyperlink label = new Hyperlink(name);

      label.setTooltip(new Tooltip(name));
      label.setOnAction(event -> {
        setDetails(uploadElement, label);
      });
      return label;
    }).forEach(label -> {
      fileListContainer.getChildren().add(label);
    });

    addElementRow(10,
            new Node[]{new WikiScrollPane(fileListContainer).setWidth(250), new WikiScrollPane(detailsContainer)},
            new Priority[]{Priority.SOMETIMES, Priority.SOMETIMES}
    );

    setDetails(Session.FILES_TO_UPLOAD.get(0), (Hyperlink) fileListContainer.getChildren().get(0));

    prevButton.linkTo("LoadPane", stage);
    nextButton.linkTo("LoginPane", stage);
  }

  /*
   * methods
   *****************************************************************************
   */
  /**
   * Gets thumbnail of file
   *
   * @param file
   * @return
   */
  private ImageView getScaledThumbnail(File file) {
    Image img = new Image(file.toURI().toString());
    ImageView view = new ImageView(img);

    int originalWidth = (int) img.getWidth();
    int originalHeight = (int) img.getHeight();
    int boundWidth = 400;
    int boundHeight = 400;
    int width = originalWidth;
    int height = originalHeight;

    if (originalWidth > boundWidth) {
      width = boundWidth;
      height = (width * originalHeight) / originalWidth;
    }
    if (height > boundHeight) {
      height = boundHeight;
      width = (height * originalWidth) / originalHeight;
    }

    view.setFitHeight(height);
    view.setFitWidth(width);
    return view;
  }

  /**
   * Shows details of selected file
   *
   * @param ue
   */
  private void setDetails(UploadElement ue, Hyperlink label) {

    WikiLabel title = new WikiLabel(ue.getData("name")).setClass("header").setAlign("left");
    WikiLabel path = new WikiLabel(ue.getData("path")).setAlign("left");
    Hyperlink pathURL = new Hyperlink(ue.getData("path"));
    Hyperlink preview = new Hyperlink(Util.text("check-preview"));
    WikiLabel wikitext = new WikiLabel(ue.getWikicode()).setClass("monospace").setAlign("left");

    prevLabel.getStyleClass().remove("bold");
    prevLabel = label;
    prevLabel.getStyleClass().add("bold");

    preview.setOnAction(event -> {
      try {
        Util.openUrl(Session.WIKI.getProtocol() + Session.WIKI.getDomain() + "/wiki/Special:ExpandTemplates"
                + "?wpRemoveComments=true"
                + "&wpInput=" + URLEncoder.encode(ue.getWikicode(), "UTF-8")
                + "&wpContextTitle=" + URLEncoder.encode(ue.getData("name"), "UTF-8"));
      } catch (UnsupportedEncodingException ex) {
        Session.LOGGER.log(Level.SEVERE, null, ex);
      }
    });
    
    pathURL.setOnAction(event -> {
      Util.openUrl(ue.getData("path"));
    });

    detailsContainer.getChildren().clear();
    
    if (ue.getData("path").startsWith("https://") || ue.getData("path").startsWith("http://")) {
      detailsContainer.getChildren().addAll(title, pathURL, preview, wikitext);
    } else {
      detailsContainer.getChildren().addAll(title, path, getScaledThumbnail(ue.getFile()), preview, wikitext);
    }
  }
}
