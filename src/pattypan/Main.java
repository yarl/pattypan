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
package pattypan;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.wikipedia.Wiki;
import pattypan.panes.StartPane;

public class Main extends Application {

  @Override
  public void start(Stage stage) {
    Settings.readProperties();
    Image logo = new Image(getClass().getResourceAsStream("/pattypan/resources/logo.png"));

    Scene scene = new Scene(new StartPane(stage), Settings.getSettingInt("windowWidth"), Settings.getSettingInt("windowHeight"));
    stage.setResizable(true);
    stage.setTitle("pattypan");
    stage.getIcons().add(logo);
    stage.setScene(scene);
    stage.show();

    stage.setOnCloseRequest((WindowEvent we) -> {
      Settings.setSetting("windowWidth", (int) scene.getWidth() + "");
      Settings.setSetting("windowHeight", (int) scene.getHeight() + "");
      Settings.saveProperties();
    });
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    if (args.length > 0) {
      Session.WIKI = new Wiki(args[0].equals("-test") ? "test.wikipedia.org" : args[0]);
    }
    launch(args);
  }

}
