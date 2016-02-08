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
package pattypan.stages;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pattypan.Util;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;

public class StartStage {
  
  String css = getClass().getResource("/pattypan/style/style.css").toExternalForm();
  Stage stage = new Stage();
  
  public StartStage() {
    Scene scene = new Scene(createScene(), Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT);
    stage.setTitle("Hello World!");
    stage.setScene(scene);
  }
  
  public Stage getStage() {
    return stage;
  };

  private GridPane createScene() {
    GridPane grid = new GridPane();
    grid.getStylesheets().add(css);
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(20);
    grid.setVgap(10);
    grid.getStyleClass().add("background");

    grid.addColumn(0,
            new WikiButton("Generate Spreadsheet", "primary"),
            new WikiLabel("Generate Spreadsheet by selecting a directory on your hard drive.")
    );
    grid.addColumn(1,
            new WikiButton("Validate & Upload"),
            new WikiLabel("Check correctness of your spreadsheet and upload files.")
    );
    
    return grid;
  }
}
