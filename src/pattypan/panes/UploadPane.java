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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
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

public class UploadPane extends WikiPane {

  Stage stage;
  volatile boolean stopRq = false;
  WikiLabel fakeLoger = new WikiLabel("");

  WikiButton uploadButton = new WikiButton("upload-button", "primary").setWidth(150);
  WikiButton stopButton = new WikiButton("upload-stop").setWidth(150);

  VBox infoContainer = new VBox(4);

  /**
   * UploadPane constructor
   *
   * @param stage program stage
   */
  public UploadPane(Stage stage) {
    super(stage, 2.0);
    this.stage = stage;

    setContent();
    setActions();
  }

  public WikiPane getContent() {
    return this;
  }

  private void addInfo(String text) {
    infoContainer.getChildren().add(new WikiLabel(text).setAlign("left"));
  }

  private WikiPane setContent() {
    addElement("upload-intro", 40);

    stopButton.setDisable(true);

    addElementRow(
            new Node[]{new Region(), uploadButton, stopButton, new Region()},
            new Priority[]{Priority.ALWAYS, Priority.NEVER, Priority.NEVER, Priority.ALWAYS}
    );

    addElement(new ScrollPane(infoContainer));

    nextButton.setVisible(false);
    return this;
  }

  private WikiPane setActions() {
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
        if (newValue.contains("Upload completed")) {
          uploadButton.setDisable(false);
          stopButton.setDisable(true);
        }
        addInfo(newValue);
      }
    });

    prevButton.linkTo("LoginPane", stage);

    return this;
  }

  private void uploadFiles() {
    Task task = new Task() {
      @Override
      protected Object call() {
        final String summary = Settings.NAME + " " + Settings.VERSION;

        int i = 0;
        int uploaded = 0;
        int skipped = 0;

        for (UploadElement ue : Session.FILES_TO_UPLOAD) {
          i++;
          if (!stopRq) {
            updateMessage(String.format("[%s/%s] Uploading %s",
                    i, Session.FILES_TO_UPLOAD.size(), ue.getData("name")));
            try {
              Map m = Session.WIKI.getPageInfo("File:" + ue.getData("name"));
              if ((boolean) m.get("exists")) {
                skipped++;
                continue;
              }
              Session.WIKI.upload(ue.getFile(), ue.getData("name"), ue.getWikicode(), summary);
              uploaded++;
            } catch (IOException | LoginException ex) {
              updateMessage(Util.text("upload-log-error", ex.getLocalizedMessage()));
              Logger.getLogger(UploadPane.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
        updateMessage(Util.text("upload-log-done", uploaded, skipped));
        return true;
      }
    };
    Thread t = new Thread(task);
    fakeLoger.textProperty().bind(task.messageProperty());
    t.start();
  }
}
