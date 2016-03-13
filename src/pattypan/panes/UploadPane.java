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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javax.security.auth.login.LoginException;
import pattypan.Session;
import pattypan.Settings;
import pattypan.UploadElement;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiPane;

public class UploadPane extends WikiPane {

  Stage stage;
  volatile boolean stopRq = false;
  WikiLabel fakeLoger = new WikiLabel("");

  WikiLabel descLabel;
  WikiButton uploadButton = new WikiButton("Upload", "primary").setWidth(300);
  WikiButton stopButton = new WikiButton("Stop");

  VBox infoContainer = new VBox(4);

  /**
   * UploadPane constructor
   * @param stage program stage
   */
  public UploadPane(Stage stage) {
    super(stage, 1.0);
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
    descLabel = new WikiLabel("Click below to start upload.").setWrapped(true);
    descLabel.setTextAlignment(TextAlignment.LEFT);
    addElement(descLabel);

    addElement(uploadButton);
    addElement(stopButton);
    addElement(new ScrollPane(infoContainer));

    nextButton.setVisible(false);
    return this;
  }
  
  private WikiPane setActions() {
    uploadButton.setOnAction((ActionEvent e) -> {
      stopRq = false;
      uploadFiles();
    });
    
    stopButton.setOnAction((ActionEvent e) -> {
      addInfo("Upload cancelled!");
      stopRq = true;
    });
    
    fakeLoger.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
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
        int i = 0;
        for (UploadElement ue : Session.FILES_TO_UPLOAD) {
          if (!stopRq) {
            updateMessage("Uploading " + ue.getData("name") + "...");
            try {
              Session.WIKI.upload(ue.getFile(), ue.getData("name"), ue.getWikicode(), Settings.NAME + " " + Settings.VERSION);
            } catch (IOException ex) {
              updateMessage("Upload error: " + ex.getLocalizedMessage());
              Logger.getLogger(UploadPane.class.getName()).log(Level.SEVERE, null, ex);
            } catch (LoginException ex) {
              updateMessage("Login error: " + ex.getLocalizedMessage());
              Logger.getLogger(UploadPane.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
        return true;
      }
    };
    Thread t = new Thread(task);
    fakeLoger.textProperty().bind(task.messageProperty());
    t.start();
  }
}
