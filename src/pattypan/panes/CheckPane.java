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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
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
        setDetails(uploadElement);
      });
      return label;
    }).forEach(label -> {
      fileListContainer.getChildren().add(label);
    });

    addElementRow(10,
            new Node[]{new WikiScrollPane(fileListContainer).setWidth(250), new WikiScrollPane(detailsContainer)},
            new Priority[]{Priority.SOMETIMES, Priority.SOMETIMES}
    );

    setDetails(Session.FILES_TO_UPLOAD.get(0));

    prevButton.linkTo("LoadPane", stage);
    nextButton.linkTo("LoginPane", stage);
  }

  /*
   * methods
   *****************************************************************************
   */
  
  /**
   * Shows details of selected file
   * 
   * @param ue 
   */
  private void setDetails(UploadElement ue) {
    
    WikiLabel title = new WikiLabel(ue.getData("name")).setClass("header").setAlign("left");
    Hyperlink preview = new Hyperlink(Util.text("check-preview"));
    WikiLabel wikitext = new WikiLabel(ue.getWikicode()).setAlign("left");
    
    preview.setOnAction(event -> {
      try {
        Util.openUrl("https://commons.wikimedia.org/wiki/Special:ExpandTemplates"
                + "?wpRemoveComments=true"
                + "&wpInput=" + URLEncoder.encode(ue.getWikicode(), "UTF-8")
                + "&wpContextTitle=" + URLEncoder.encode(ue.getData("name"), "UTF-8"));
      } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(CheckPane.class.getName()).log(Level.SEVERE, null, ex);
      }
    });

    detailsContainer.getChildren().clear();
    detailsContainer.getChildren().addAll(title, preview, wikitext);
  }
}
