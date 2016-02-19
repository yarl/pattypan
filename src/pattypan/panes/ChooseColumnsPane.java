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

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import pattypan.Session;
import pattypan.Util;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiProgressBar;

public class ChooseColumnsPane extends GridPane {

  String css = getClass().getResource("/pattypan/style/style.css").toExternalForm();
  Stage stage;
  
  WikiLabel descLabel;
  WikiButton templateButton;
  WikiButton wikicodeButton;
  ScrollPane scrollText = new ScrollPane();
  WikiButton prevButton;
  WikiButton nextButton;
  
  public ChooseColumnsPane(Stage stage) {
    this.stage = stage;
    setContent();
    
    if(Session.METHOD.equals("wikicode")) {
      wikicodeButton.fire();
    }
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

    this.getColumnConstraints().add(Util.newColumn(50, "%", HPos.LEFT));
    this.getColumnConstraints().add(Util.newColumn(50, "%", HPos.RIGHT));
    
    WikiProgressBar progressBar = new WikiProgressBar(0.5,
            new String[]{
              "Choose directory",
              "Choose columns",
              "Create file"
            });
    
    HBox progressBarContainer = new HBox();
    progressBarContainer.setAlignment(Pos.CENTER);
    progressBarContainer.getChildren().add(progressBar);
    
    descLabel = new WikiLabel("In cursus nunc enim, ac ullamcorper lectus consequat accumsan. Mauris erat sapien, iaculis a quam in, molestie dapibus libero. Morbi mollis mattis porta. Pellentesque at suscipit est, id vestibulum risus.").setWrapped(true);
    descLabel.setTextAlignment(TextAlignment.LEFT);

    /* buttons */
    
    templateButton = new WikiButton("Use template", "group-left").setWidth(150);
    wikicodeButton = new WikiButton("Write wikicode", "group-right", "inversed").setWidth(150);
    HBox tabs = new HBox(0);
    tabs.getChildren().addAll(templateButton, wikicodeButton);
    tabs.setAlignment(Pos.BOTTOM_LEFT);

    /* templates pane */
    
    final ComboBox templateBox = new ComboBox();
    templateBox.getItems().addAll(
            "Artwork", "Book", "Photograph", "Map"
    );
    templateBox.getSelectionModel().select(Session.TEMPLATE);
    templateBox.setOnAction((Event ev) -> {
      String template = templateBox.getSelectionModel().getSelectedItem().toString();
    });
    
    GridPane templatePane = new GridPane();
    templatePane.add(templateBox, 0, 0);
    
    /* wiki code pane */
    TextArea wikicodeText = new TextArea();
    wikicodeText.getStyleClass().add("mw-ui-input");
    wikicodeText.setText(Session.WIKICODE);
    
    GridPane wikicodePane = new GridPane();
    wikicodePane.add(wikicodeText, 0, 0);
    
    templateButton.setOnAction(event -> {
      templateButton.getStyleClass().remove("mw-ui-inversed");
      wikicodeButton.getStyleClass().add("mw-ui-inversed");
      if(this.getChildren().remove(wikicodePane)) {
        this.add(templatePane, 0, 3, 2, 1);
      }
      Session.METHOD = "template";
    });
    
    wikicodeButton.setOnAction(event -> {
      wikicodeButton.getStyleClass().remove("mw-ui-inversed");
      templateButton.getStyleClass().add("mw-ui-inversed");
      
      if(this.getChildren().remove(templatePane)) {
        this.add(wikicodePane, 0, 3, 2, 1);
      }
      Session.METHOD = "wikicode";
    });
    
    prevButton = new WikiButton("Back", "inversed").linkTo("ChooseDirectoryPane", stage).setWidth(100);
    nextButton = new WikiButton("Next", "inversed").setWidth(100);
    nextButton.setOnAction(event -> {
      if(Session.METHOD.equals("wikicode")) {
        ArrayList<String> vars = Util.getVariablesFromString(wikicodeText.getText());
        Session.VARIABLES.addAll(vars);
      }
      Session.WIKICODE = wikicodeText.getText();
      nextButton.goTo("CreateFilePane", stage);
    });
    
    this.add(progressBarContainer, 0, 0, 2, 1);
    this.add(descLabel, 0, 1, 2, 1);
    this.add(tabs, 0, 2, 2, 1);
    this.addRow(3, templatePane);
    this.addRow(4, prevButton, nextButton);
    
    return this;
  }
}
