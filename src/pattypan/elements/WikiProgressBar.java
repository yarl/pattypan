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
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class WikiProgressBar extends GridPane {
  String[] labels;
  
  public WikiProgressBar(double progress, String... labels) {
    super();
    this.labels = labels;
    createContent(progress);
  }

  private Pane createDot(double number, double progress, int translateX) {
    boolean isActive = progress >= (number / 2.0);

    Circle c = new Circle(5);
    c.getStyleClass().add("mw-ui-progressbar-dot" + (isActive ? "-active" : ""));

    Pane p = new Pane(c);
    p.setTranslateX(translateX);
    p.setTranslateY(-2);
    return p;
  }

  private GridPane createContent(double progress) {
    this.setMinWidth(420);
    this.getStyleClass().add("mw-ui-progressbar-container");

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
            createDot(0.0, progress, 1),
            createDot(1.0, progress, 70),
            createDot(2.0, progress, 140));

    this.addRow(2,
            new WikiLabel(labels[0]).setTranslateByHalf(false),
            new WikiLabel(labels[1]).setAlign("center"),
            new WikiLabel(labels[2]).setAlign("right").setTranslateByHalf(true)
    );
    return this;
  }
}
