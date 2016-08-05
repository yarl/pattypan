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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import pattypan.Session;
import pattypan.Settings;
import pattypan.Util;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiPane;
import pattypan.elements.WikiTextField;

public class ChooseDirectoryPane extends WikiPane {

  Stage stage;

  WikiLabel descLabel;
  WikiTextField browsePath = new WikiTextField("");
  WikiButton browseButton = new WikiButton("generic-browse", "small").setWidth(100);
  CheckBox includeSubdirectoriesCheckbox = new CheckBox(Util.text("choose-directory-include-subdirectories"));
  VBox checkBoxContainer = new VBox();

  public ChooseDirectoryPane(Stage stage) {
    super(stage, 0.0);
    this.stage = stage;

    setContent();
    setActions();
  }

  /*
   * set content and actions
   *****************************************************************************
   */
  public BorderPane getContent() {
    return this;
  }

  private void setActions() {
    browseButton.setOnAction((ActionEvent e) -> {
      chooseAndSetDirectory();
    });
    includeSubdirectoriesCheckbox.setOnAction((ActionEvent e) -> {
      if (Session.DIRECTORY != null) {
        getFileListByDirectory(Session.DIRECTORY);
      }
    });

    prevButton.linkTo("StartPane", stage);
    nextButton.linkTo("ChooseColumnsPane", stage);
  }

  private void setContent() {
    addElement("choose-directory-intro", 40);
    addElementRow(
            new Node[]{browseButton, browsePath},
            new Priority[]{Priority.NEVER, Priority.ALWAYS}
    );
    addElement(new VBox(includeSubdirectoriesCheckbox));
    addElement(checkBoxContainer);

    browsePath.setDisable(true);
    nextButton.setDisable(true);
  }

  /*
   * methods
   *****************************************************************************
   */
  /**
   *
   */
  private void chooseAndSetDirectory() {
    DirectoryChooser fileChooser = new DirectoryChooser();
    fileChooser.setTitle(Util.text("choose-directory-window-name"));
    if (Session.DIRECTORY != null) {
      fileChooser.setInitialDirectory(Session.DIRECTORY);
    }

    File file = fileChooser.showDialog(stage);
    if (file != null) {
      Session.DIRECTORY = file;
      browsePath.setText(file.getAbsolutePath());
      Settings.setSetting("path", Session.DIRECTORY.getAbsolutePath());
      getFileListByDirectory(file);
    }
  }

  /**
   *
   * @param directory
   */
  private void getFileListByDirectory(File directory) {
    checkBoxContainer.getChildren().clear();
    checkBoxContainer.getChildren().add(new WikiLabel("generic-summary").setClass("header"));

    File[] files = Util.getFilesAllowedToUpload(directory, includeSubdirectoriesCheckbox.isSelected());
    Session.FILES = new ArrayList<>(Arrays.asList(files));

    Map<String, Integer> filesByExt = Util.getFilesByExtention(files);
    filesByExt.entrySet().stream()
            .map((e) -> String.format(".%s (%s files)", e.getKey(), e.getValue()))
            .forEach((text) -> {
              CheckBox checkbox = new CheckBox(text);
              checkbox.setSelected(true);
              checkbox.setDisable(true);
              checkBoxContainer.getChildren().add(new WikiLabel(text));
            });

    if (files.length == 0) {
      checkBoxContainer.getChildren().add(new WikiLabel("choose-directory-no-files"));
    }

    nextButton.setDisable(files.length == 0);
  }
}
