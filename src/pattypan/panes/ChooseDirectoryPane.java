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
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import pattypan.Session;
import pattypan.Settings;
import pattypan.Util;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiProgressBar;
import pattypan.elements.WikiTextField;

public class ChooseDirectoryPane extends GridPane {

  String css = getClass().getResource("/pattypan/style/style.css").toExternalForm();
  Stage stage;
  
  WikiLabel descLabel;
  WikiTextField browsePath;
  WikiButton browseButton;
  ScrollPane scrollText = new ScrollPane();
  WikiButton nextButton;

  public ChooseDirectoryPane(Stage stage) {
    this.stage = stage;
    setContent();
    
    if(Session.DIRECTORY != null) {
      browsePath.setText(Session.DIRECTORY.getAbsolutePath());
      getFileListByDirectory(Session.DIRECTORY);
    }
  }

  private void chooseAndSetDirectory() {
    DirectoryChooser fileChooser = new DirectoryChooser();
    fileChooser.setTitle("Choose directory");
    if(Session.DIRECTORY != null) {
      fileChooser.setInitialDirectory(Session.DIRECTORY);
    }

    File file = fileChooser.showDialog(stage);
    if (file != null) {
      Session.DIRECTORY = file;
      browsePath.setText(file.getAbsolutePath());
      getFileListByDirectory(file);
    }
  }
  
  public GridPane getContent() {
    return this;
  }
  
  private void getFileListByDirectory(File directory) {
    File[] files = directory.listFiles();
    VBox content = new VBox();
    
    for (File f : files) {
      if (f.isFile()) {
        content.getChildren().add(new WikiLabel(f.getName()));
        Session.FILES.add(f);
      }
    }
    scrollText.setContent(content);
    nextButton.setDisable(files.length == 0);
  }
  
  private GridPane setContent() {
    this.getStylesheets().add(css);
    this.setAlignment(Pos.TOP_CENTER);
    this.setHgap(20);
    this.setVgap(10);
    this.getStyleClass().add("background");
    
    this.setHeight(Settings.WINDOW_HEIGHT);
    this.getColumnConstraints().add(Util.newColumn(80, "%", HPos.LEFT));
    this.getColumnConstraints().add(Util.newColumn(20, "%", HPos.RIGHT));

    WikiProgressBar progressBar = new WikiProgressBar(0.0,
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
        
    browsePath = new WikiTextField("");
    browseButton = new WikiButton("Browse", "small").setWidth(100);
    browseButton.setOnAction((ActionEvent e) -> {
      chooseAndSetDirectory();
    });
    
    scrollText.getStyleClass().add("mw-ui-scrollpane");
    scrollText.setMaxHeight(100);
    scrollText.setMinHeight(100);
    
    nextButton = new WikiButton("Next", "inversed").linkTo("ChooseColumnsPane", stage).setWidth(100);
    //nextButton.setDisable(true);
    
    this.add(progressBarContainer, 0, 0, 2, 1);
    this.add(descLabel, 0, 1, 2, 1);
    this.addRow(2, browsePath, browseButton);
    this.add(scrollText, 0, 3, 2, 1);
    this.add(nextButton, 1, 4, 1, 1);

    return this;
  }
}
