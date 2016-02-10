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

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class WikiProgressBar extends GridPane {

  public WikiProgressBar(double progress) {
    super();
    createContent(progress);
  }

  private GridPane createContent(double progress) {
    this.setMinWidth(420);

    ColumnConstraints col = new ColumnConstraints();
    col.setPercentWidth(33);
    for (int i = 0; i < 3; i++) {
      this.getColumnConstraints().add(col);
    }

    ProgressBar pb = new ProgressBar(progress);
    pb.getStyleClass().addAll("mw-ui-progressbar");
    pb.setMinWidth(420);
    pb.setMaxHeight(5);

    this.add(pb, 0, 0, 3, 1);
    this.addRow(1,
            new WikiLabel("Choose directory"),
            new WikiLabel("Choose columns").setAlign("center"),
            new WikiLabel("Create file").setAlign("right")
    );
    return this;
  }
}
