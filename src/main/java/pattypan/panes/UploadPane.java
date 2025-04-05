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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.logging.Level;
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
import org.wikipedia.Wiki;
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

  int current = 0;
  int uploaded = 0;
  int skipped = 0;
  ArrayList<UploadElement> failedElements = new ArrayList<>();

  WikiLabel fakeLoger = new WikiLabel("");

  WikiButton uploadButton = new WikiButton("upload-button", "primary").setWidth(150);
  WikiButton stopButton = new WikiButton("upload-stop").setWidth(150);

  VBox infoContainer = new VBox(4);
  WikiScrollPane infoContainerScroll = new WikiScrollPane(infoContainer);
  WikiLabel statusLabel = new WikiLabel("").setClass("bold");

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

  private void resetInfo() {
    infoContainer.getChildren().clear();
    current = 0;
    uploaded = 0;
    skipped = 0;
  }

  private void setActions() {
    uploadButton.setOnAction((ActionEvent e) -> {
      stopRq = false;
      uploadButton.setDisable(true);
      stopButton.setDisable(false);
      nextButton.setVisible(false);
      uploadFiles(Session.FILES_TO_UPLOAD);
    });

    stopButton.setOnAction((ActionEvent e) -> {
      stopButton.setDisable(true);
      statusLabel.setText(Util.text("upload-log-canceled"));
      stopRq = true;
    });

    fakeLoger.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> ov, String oldValue, String value) {
        String[] data = value.split(" \\| ");
        String max = String.valueOf(Session.FILES_TO_UPLOAD.size());

        if (value.contains("UPLOAD_START")) {
          String text = String.format("[%s/%s] ", data[1], max) + Util.text("upload-log-uploading", data[2]);
          WikiLabel label = new WikiLabel(text).setAlign("left");
          addInfo(label);
          statusLabel.setText(Util.text("upload-log-uploading", "") + String.format(" (%s/%s) ", data[1], max));
        } else if (value.contains("UPLOAD_NAME_TAKEN")) {
          String text = String.format("[%s/%s] ", data[1], max) + Util.text("upload-log-error", Util.text("upload-log-error-name-taken"));
          WikiLabel label = new WikiLabel(text).setAlign("left");
          addInfo(label);
          addInfo(new WikiLabel(""));
        } else if (value.contains("FILE_DUPLICATE")) {
          String text = String.format("[%s/%s] ", data[1], max) + Util.text("upload-log-error", Util.text("upload-log-error-file-duplicate", data[3]));
          WikiLabel label = new WikiLabel(text).setAlign("left");
          addInfo(label);
          addInfo(new WikiLabel(""));
        } else if (value.contains("UPLOAD_SUCCESS")) {
          String text = String.format("[%s/%s] ", data[1], max) + Util.text("upload-log-success");
          WikiLabel label = new WikiLabel(text).setAlign("left");
          addInfo(label);
          addInfo(new WikiLabel(""));
        } else if (value.contains("UPLOAD_ERROR")) {
          String text = String.format("[%s/%s] ", data[1], max) + Util.text("upload-log-error", data[3]);
          WikiLabel label = new WikiLabel(text).setAlign("left").setClass("bold");
          addInfo(label);
          addInfo(new WikiLabel(""));
        } else if (value.contains("UPLOAD_COMPLETED")) {
          int uploaded = Integer.parseInt(data[1]);
          int skipped = Integer.parseInt(data[2]);
          int total = Session.FILES_TO_UPLOAD.size();

          uploadButton.setDisable(uploaded == total);
          if (uploaded + skipped == total && skipped > 0) {
            uploadButton.setText(Util.text("upload-retry"));
            uploadButton.setOnAction((ActionEvent e) -> {
              stopRq = false;
              uploadButton.setDisable(true);
              stopButton.setDisable(false);
              resetInfo();
              uploadFiles(failedElements);
            });
          } else if (uploaded + skipped < total) {
            uploadButton.setText(Util.text("upload-continue"));
          } else if (uploaded == total) {
            uploadButton.setText(Util.text("upload-button"));
          }

          stopButton.setDisable(true);
          String text = Util.text("upload-log-done", data[1], data[2]);
          WikiLabel label = new WikiLabel(text).setAlign("left").setClass("bold");
          addInfo(label);
          addInfo(new WikiLabel(""));
          statusLabel.setText(text);

          nextButton.linkTo("StartPane", stage, true).setText(Util.text("upload-next-sheet"));
          nextButton.setVisible(true);
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
    addElement(infoContainerScroll);
    addElement(statusLabel);

    nextButton.setVisible(false);
  }

  /*
   * methods
   *****************************************************************************
   */
  private void addInfo(WikiLabel label) {
    infoContainer.getChildren().add(label);
    infoContainerScroll.setVvalue(1.0);
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
      Map map = Session.WIKI.getPageInfo(List.of("File:" + name)).get(0);
      return (boolean) map.get("exists");
    } catch (UnknownHostException ex) {
      Session.LOGGER.log(Level.WARNING,
              "Error occurred during file name check: {0}",
              new String[]{"no internet connection"}
      );
      return false;
    } catch (IOException ex) {
      Session.LOGGER.log(Level.WARNING,
              "Error occurred during file name check: {0}",
              new String[]{ex.getLocalizedMessage()}
      );
      return false;
    }
  }

  private void uploadFiles(ArrayList<UploadElement> fileList) {
    Task task = new Task() {
      @Override
      protected Object call() {
        final String summary = Settings.NAME + " " + Settings.VERSION;

        // for (UploadElement ue : Session.FILES_TO_UPLOAD) {
        for (; current < fileList.size(); current++) {
          UploadElement ue = fileList.get(current);
          String name = Util.getNormalizedName(ue.getData("name"));
          
          if (!stopRq) {
            updateMessage(String.format(
                    "UPLOAD_START | %s | %s",
                    current + 1, name
            ));
            try {
              if (isFileNameTaken(name)) {
                updateMessage(String.format(
                        "UPLOAD_NAME_TAKEN | %s | %s",
                        current + 1, name
                ));
                Thread.sleep(500);
                skipped++;
                continue;
              }

              if (ue.getData("path").startsWith("https://") || ue.getData("path").startsWith("http://")) {
                Session.WIKI.upload(ue.getUrl(), name, ue.getWikicode(), summary);
              } else {
                Session.WIKI.upload(ue.getFile(), name, ue.getWikicode(), summary);
              }

              Thread.sleep(500);
              updateMessage(String.format(
                      "UPLOAD_SUCCESS | %s | %s",
                      current + 1, name
              ));
              uploaded++;
            } catch (InterruptedException | IOException | LoginException ex) {
              updateMessage(String.format(
                      "UPLOAD_ERROR | %s | %s | %s",
                      current + 1, name, getMediaWikiError(ex)
              ));
              try {
                Thread.sleep(500);
              } catch (InterruptedException e) {
              }
              if (!failedElements.contains(ue)) {
                failedElements.add(ue);
              }
              skipped++;
            }
          } else {
            break;
          }
        }
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        updateMessage(String.format(
                "UPLOAD_COMPLETED | %s | %s",
                uploaded, skipped
        ));
        return true;
      }
    };
    fakeLoger.textProperty().bind(task.messageProperty());
    new Thread(task).start();
  }
}
