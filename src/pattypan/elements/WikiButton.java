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
package pattypan.elements;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pattypan.Util;

public class WikiButton extends Button {

  public WikiButton(String name) {
    this(name, "");
  }

  public WikiButton(String name, String type) {
    super(name);
    this.getStyleClass().addAll("mw-ui-button", type.isEmpty() ? "" : "mw-ui-" + type);

    this.wrapTextProperty().setValue(true);
    this.setMaxWidth(200);
  }

  private Pane getPaneByPaneName(String name, Stage stage) {
    try {
      Class<?> paneClass = Class.forName("pattypan.panes." + name);
      Constructor<?> constructor = paneClass.getConstructor(Stage.class);
      Object instance = constructor.newInstance(stage);
      Method content = instance.getClass().getMethod("getContent");

      return (Pane) content.invoke(instance);
    } catch (ClassNotFoundException | NoSuchMethodException | SecurityException |InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      Logger.getLogger(WikiButton.class.getName()).log(Level.SEVERE, null, ex);
    }
    return new Pane();
  }

  public WikiButton linkTo(String paneName, Stage stage) {
    this.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        Scene scene = new Scene(getPaneByPaneName(paneName, stage), Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT);
        stage.setScene(scene);
      }
    });
    return this;
  }
}
