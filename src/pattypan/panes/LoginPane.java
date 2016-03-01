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
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javax.security.auth.login.FailedLoginException;
import org.wikipedia.Wiki;
import pattypan.Session;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiPane;
import pattypan.elements.WikiPasswordField;
import pattypan.elements.WikiTextField;

public class LoginPane extends WikiPane {

  Stage stage;

  WikiLabel descLabel;
  WikiTextField loginText = new WikiTextField("").setPlaceholder("Login").setWidth(300);
  WikiPasswordField passwordText = new WikiPasswordField().setPlaceholder("Password").setWidth(300);
  WikiButton loginButton = new WikiButton("Login").setWidth(300);
  WikiLabel loginStatus = new WikiLabel("");

  public LoginPane(Stage stage) {
    super(stage, 1.5);
    this.stage = stage;

    setContent();
  }

  public WikiPane getContent() {
    return this;
  }

  private WikiPane setContent() {
    descLabel = new WikiLabel("In cursus nunc enim, ac ullamcorper lectus consequat accumsan. Mauris erat sapien, iaculis a quam in, molestie dapibus libero. Morbi mollis mattis porta. Pellentesque at suscipit est, id vestibulum risus.").setWrapped(true);
    descLabel.setTextAlignment(TextAlignment.LEFT);
    addElement(descLabel);

    loginButton.setOnAction((ActionEvent e) -> {
      logIn();
    });

    addElement(loginText);
    addElement(passwordText);
    addElement(loginButton);
    addElement(loginStatus);

    prevButton.linkTo("ValidatePane", stage);
    nextButton.setVisible(false);

    return this;
  }

  private void logIn() {
    loginStatus.setText("Logging in...");
    Platform.runLater(() -> {
      try {
        Session.WIKI.login(loginText.getText(), passwordText.getText());
        //loginStatus.setText("Logged in");
      } catch (IOException | FailedLoginException ex) {
        loginStatus.setText("Login error!");
      }
    });
  }
}
