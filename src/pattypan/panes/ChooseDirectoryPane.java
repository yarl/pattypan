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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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

public class ChooseDirectoryPane extends BorderPane {

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
  
  public BorderPane getContent() {
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
  
  private BorderPane setContent() {
    this.getStylesheets().add(css);
    this.getStyleClass().add("background");
    this.setHeight(Settings.WINDOW_HEIGHT);

    WikiProgressBar progressBar = new WikiProgressBar(0.0,
            new String[]{
              "Choose directory",
              "Choose columns",
              "Create file"
            });
    
    HBox topContainer = new HBox();
    topContainer.setAlignment(Pos.CENTER);
    topContainer.getChildren().add(progressBar);
    
    VBox centerContainer = new VBox(15);
    centerContainer.setAlignment(Pos.TOP_CENTER);
    
    descLabel = new WikiLabel("In cursus nunc enim, ac ullamcorper lectus consequat accumsan. Mauris erat sapien, iaculis a quam in, molestie dapibus libero. Morbi mollis mattis porta. Pellentesque at suscipit est, id vestibulum risus.").setWrapped(true);
    descLabel.setTextAlignment(TextAlignment.LEFT);
    centerContainer.getChildren().add(descLabel);
    
    browsePath = new WikiTextField("");
    browseButton = new WikiButton("Browse", "small").setWidth(100);
    browseButton.setOnAction((ActionEvent e) -> {
      chooseAndSetDirectory();
    });
    
    HBox hb = new HBox(8);
    HBox.setHgrow(browsePath, Priority.ALWAYS);
    hb.getChildren().addAll(browsePath, browseButton);
    centerContainer.getChildren().add(hb);
    
    scrollText.getStyleClass().add("mw-ui-scrollpane");
    scrollText.setMaxHeight(100);
    scrollText.setMinHeight(100);
    centerContainer.getChildren().add(scrollText);
    
    HBox bottomContainer = new HBox();
    bottomContainer.setAlignment(Pos.CENTER_RIGHT);
    bottomContainer.getChildren().add(new WikiButton("Next", "inversed").linkTo("ChooseColumnsPane", stage).setWidth(100));
    
    this.setTop(topContainer);
    this.setCenter(centerContainer);
    this.setBottom(bottomContainer);
    return this;
  }
}
