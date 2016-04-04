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

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import pattypan.Session;
import pattypan.Util;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiPane;
import pattypan.elements.WikiPasswordField;
import pattypan.elements.WikiTextField;

public class LoginPane extends WikiPane {

  Stage stage;

  WikiTextField loginText = new WikiTextField("").setPlaceholder("login-login-field").setWidth(300);
  WikiPasswordField passwordText = new WikiPasswordField().setPlaceholder("login-password-field").setWidth(300);
  WikiButton loginButton = new WikiButton("login-login-button").setWidth(300);
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
    addElement("login-intro", 40);

    passwordText.setOnKeyPressed((KeyEvent event) -> {
      if (event.getCode().equals(KeyCode.ENTER)) {
        logIn();
      }
    });
    
    loginButton.setOnAction((ActionEvent event) -> {
      logIn();
    });

    addElement(loginText);
    addElement(passwordText);
    addElement(loginButton);
    addElement(loginStatus);

    prevButton.linkTo("ValidatePane", stage);
    nextButton.linkTo("UploadPane", stage).setDisable(true);

    return this;
  }

  private void setDisableForm(boolean state) {
    loginText.setDisable(state);
    passwordText.setDisable(state);
    loginButton.setDisable(state);
  }
  
  
  
  private void logIn() {
    Task<Integer> task = new Task<Integer>() {

      private void setLoginSuccess() {
        setDisableForm(false);
        loginButton.setText(Util.text("login-login-button"));
        nextButton.setDisable(false);
        nextButton.fire();
      }

      private void setLoginFailed() {
        setDisableForm(false);
        loginButton.setText(Util.text("login-login-button"));
        loginStatus.setText(Util.text("login-failed"));
      }

      @Override
      protected Integer call() throws Exception {
        Session.WIKI.login(loginText.getText(), passwordText.getText());
        return 1;
      }

      @Override
      protected void succeeded() {
        super.succeeded();
        setLoginSuccess();
      }

      @Override
      protected void cancelled() {
        super.cancelled();
      }

      @Override
      protected void failed() {
        super.failed();
        setLoginFailed();
      }
    };
    
    setDisableForm(true);
    loginButton.setText(Util.text("login-load"));
    new Thread(task).start();
  }
}
