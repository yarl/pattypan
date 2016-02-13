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

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import pattypan.Util;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiProgressBar;

public class ChooseColumnsPane extends GridPane {

  String css = getClass().getResource("/pattypan/style/style.css").toExternalForm();
  Stage stage;
  
  WikiLabel descLabel;
  WikiButton prevButton;
  WikiButton nextButton;
  
  public ChooseColumnsPane(Stage stage) {
    this.stage = stage;
    setContent();
  }
  
  public GridPane getContent() {
    return this;
  }
  
  private GridPane setContent() {
    this.getStylesheets().add(css);
    this.setAlignment(Pos.TOP_CENTER);
    this.setHgap(20);
    this.setVgap(10);
    this.getStyleClass().add("background");

    this.getColumnConstraints().add(Util.newColumn(200, "px", HPos.LEFT));
    this.getColumnConstraints().add(Util.newColumn(200, "px", HPos.RIGHT));
    
    WikiProgressBar progressBar = new WikiProgressBar(0.5,
            new String[]{
              "Choose directory",
              "Choose columns",
              "Create file"
            });
    
    descLabel = new WikiLabel("In cursus nunc enim, ac ullamcorper lectus consequat accumsan. Mauris erat sapien, iaculis a quam in, molestie dapibus libero. Morbi mollis mattis porta. Pellentesque at suscipit est, id vestibulum risus.").setWrapped(true);
    descLabel.setTextAlignment(TextAlignment.LEFT);
    
    WikiButton a = new WikiButton("Columns", "group-left").setWidth(150);
    WikiButton b = new WikiButton("Wikicode", "group-right", "inversed").setWidth(150);
    b.setDisable(true);
            
    HBox tabs = new HBox(0);
    tabs.getChildren().addAll(a,b);
    tabs.setAlignment(Pos.BOTTOM_LEFT);

    prevButton = new WikiButton("Back", "inversed").linkTo("ChooseDirectoryPane", stage).setWidth(100);
    nextButton = new WikiButton("Next", "inversed").setWidth(100);
    
    this.add(progressBar, 0, 0, 2, 1);
    this.add(descLabel, 0, 1, 2, 1);
    this.add(tabs, 0, 2, 2, 1);
    //this.addRow(1, a, b);
    this.addRow(3, prevButton, nextButton);
    
    return this;
  }
}