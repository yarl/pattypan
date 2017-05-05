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

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.security.auth.login.LoginException;
import pattypan.Session;
import pattypan.Settings;
import pattypan.UploadElement;
import pattypan.Util;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiPane;
import pattypan.elements.WikiScrollPane;

public class UploadPane extends WikiPane {

  Stage stage;
  volatile boolean stopRq = false;
  WikiLabel fakeLoger = new WikiLabel("");

  WikiButton uploadButton = new WikiButton("upload-button", "primary").setWidth(150);
  WikiButton stopButton = new WikiButton("upload-stop").setWidth(150);

  VBox infoContainer = new VBox(4);

  public UploadPane(Stage stage) {
    super(stage, 2.0);
    this.stage = stage;

    setContent();
    setActions();
  }

  /*
   * set content and actions
   *****************************************************************************
   */
  public WikiPane getContent() {
    return this;
  }

  private void setActions() {
    uploadButton.setOnAction((ActionEvent e) -> {
      stopRq = false;
      uploadButton.setDisable(true);
      stopButton.setDisable(false);
      uploadFiles();
    });

    stopButton.setOnAction((ActionEvent e) -> {
      stopButton.setDisable(true);
      addInfo("upload-log-canceled");
      stopRq = true;
    });

    fakeLoger.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
        if (newValue.equals("_COMPLETE_UPLOAD")) {
          uploadButton.setDisable(false);
          stopButton.setDisable(true);
        } else {
          addInfo(newValue);
        }
      }
    });

    prevButton.linkTo("LoginPane", stage);
  }

  private void setContent() {
    addElement("upload-intro", 40);

    stopButton.setDisable(true);

    addElementRow(
            new Node[]{new Region(), uploadButton, stopButton, new Region()},
            new Priority[]{Priority.ALWAYS, Priority.NEVER, Priority.NEVER, Priority.ALWAYS}
    );
    addElement(new WikiScrollPane(infoContainer));

    nextButton.setVisible(false);
  }

  /*
   * methods
   *****************************************************************************
   */
  private void addInfo(String text) {
    infoContainer.getChildren().add(new WikiLabel(text).setAlign("left"));
  }

  /**
   * Retuns human-readable error from MediaWiki API
   *
   * @url https://www.mediawiki.org/wiki/API:Errors_and_warnings#Errors
   * @param error raw MediaWiki error
   * @return error string
   */
  private String getMediaWikiError(Exception error) {
    final Pattern errorPattern = Pattern.compile("info=\"(.*?)\"");
    Matcher m = errorPattern.matcher(error.getLocalizedMessage());
    while (m.find()) {
      return m.group(1);
    }
    return error.getLocalizedMessage();
  }

  /**
   * Checks if file name is taken on Wikimedia Commons
   *
   * @param name file name (without File: prefix)
   * @return true if name is taken
   */
  private boolean isFileNameTaken(String name) {
    try {
      Map map = Session.WIKI.getPageInfo("File:" + name);
      return (boolean) map.get("exists");
    } catch (IOException ex) {
      Logger.getLogger(UploadPane.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }
  }

  private void uploadFiles() {
    Task task = new Task() {
      @Override
      protected Object call() {
        final String summary = Settings.NAME + " " + Settings.VERSION;
        int current = 0;
        int max = Session.FILES_TO_UPLOAD.size();

        int uploaded = 0;
        int skipped = 0;

        for (UploadElement ue : Session.FILES_TO_UPLOAD) {
          current++;
          if (!stopRq) {
            updateMessage(Util.text("upload-log-uploading", current, max, ue.getData("name")));
            try {
              if (isFileNameTaken(ue.getData("name"))) {
                updateMessage(Util.text("upload-log-error", current, max, Util.text("upload-log-error-name-taken")));
                Thread.sleep(500);
                skipped++;
                continue;
              }
              Session.WIKI.upload(ue.getFile(), ue.getData("name"), ue.getWikicode(), summary);
              Thread.sleep(500);
              updateMessage(Util.text("upload-log-success", current, max));
              uploaded++;
            } catch (InterruptedException | IOException | LoginException ex) {
              updateMessage(Util.text("upload-log-error", current, max, getMediaWikiError(ex)));
              try { Thread.sleep(500); } catch (InterruptedException e) {}
              skipped++;
            }
          }
        }
        updateMessage("_COMPLETE_UPLOAD");
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        updateMessage(Util.text("upload-log-done", uploaded, skipped));
        return true;
      }
    };
    fakeLoger.textProperty().bind(task.messageProperty());
    new Thread(task).start();
  }
}
