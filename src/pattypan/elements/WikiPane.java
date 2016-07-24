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
package pattypan.elements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import pattypan.Util;

public class WikiPane extends BorderPane {

  String css = getClass().getResource("/pattypan/style/style.css").toExternalForm();
  Stage stage;

  public HBox topContainer = new HBox();
  public VBox centerContainer = new VBox(15);
  public HBox bottomContainer = new HBox();

  public WikiButton prevButton = new WikiButton("generic-back", "inversed").setWidth(150);
  public WikiButton nextButton = new WikiButton("generic-next", "inversed").setWidth(150);

  private final String[] progressBarLabels = {
    Util.text("choose-directory-name"),
    Util.text("choose-columns-name"),
    Util.text("create-file-name")
  };
  private final String[] progressBarLabels2 = {
    Util.text("validate-name"),
    Util.text("check-name"),
    Util.text("login-name"),
    Util.text("upload-name")
  };
  
  public WikiPane(Stage stage, double value) {
    this.stage = stage;
    this.getStylesheets().add(css);
    this.getStyleClass().add("background");

    topContainer.setAlignment(Pos.CENTER);
    topContainer.getChildren().add(new WikiProgressBar(
            value > 1.0 ? value - 1.0 : value,
            value > 1.0 ? progressBarLabels2 : progressBarLabels)
    );

    centerContainer.setAlignment(Pos.TOP_CENTER);

    Region region = new Region();
    HBox.setHgrow(region, Priority.ALWAYS);
    bottomContainer.getChildren().addAll(prevButton, region, nextButton);
    BorderPane.setMargin(bottomContainer, new Insets(10, 0, 0, 0));
    
    this.setTop(topContainer);
    this.setCenter(centerContainer);
    this.setBottom(bottomContainer);
  }

  public boolean removeElement(Node element) {
    return centerContainer.getChildren().remove(element);
  }
  
  public void addElement(Node element) {
    centerContainer.getChildren().add(element);
  }
  
  public void addElement(String text, String cssClass, int height) {
    WikiLabel label = new WikiLabel(text).setWrapped(true);
    label.setTextAlignment(TextAlignment.LEFT);
    label.setClass(cssClass);
    
    VBox box = new VBox();
    box.getChildren().add(label);
    box.setMinHeight(height);
    addElement(box);
  }
  
  public void addElement(String text, int height) {
    addElement(text, "", height);
  }
  
  public void addElement(String text, String cssClass) {
    addElement(text, cssClass, 0);
  }
  
  public void addElement(String text) {
    addElement(text, "", 0);
  }

  public void addElementRow(Pane target, double spacing, Node[] element, Priority[] priority) {
    HBox hb = new HBox(spacing);
    hb.getChildren().addAll(element);

    for (int i = 0; i < priority.length; i++) {
      HBox.setHgrow(element[i], priority[i]);
    }
    target.getChildren().add(hb);
  }
  
  public void addElementRow(double spacing, Node[] element, Priority[] priority) {
    addElementRow(centerContainer, spacing, element, priority);
  }
  
  public void addElementRow(Node[] element, Priority[] priority) {
    addElementRow(8, element, priority);
  }
}
