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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import pattypan.Session;
import pattypan.Util;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiProgressBar;

public class CreateFilePane extends GridPane {

  String css = getClass().getResource("/pattypan/style/style.css").toExternalForm();
  Stage stage;

  WikiLabel descLabel;
  WikiButton prevButton;
  WikiButton createButton;

  public CreateFilePane(Stage stage) {
    this.stage = stage;
    setContent();
  }

  public GridPane getContent() {
    return this;
  }

  private GridPane setContent() {
    this.getStylesheets().add(css);
    this.setAlignment(Pos.TOP_CENTER);
    this.setHgap(20);
    this.setVgap(10);
    this.getStyleClass().add("background");

    this.getColumnConstraints().add(Util.newColumn(50, "%", HPos.LEFT));
    this.getColumnConstraints().add(Util.newColumn(50, "%", HPos.RIGHT));

    WikiProgressBar progressBar = new WikiProgressBar(1.0,
            new String[]{
              "Choose directory",
              "Choose columns",
              "Create file"
            });

    HBox progressBarContainer = new HBox();
    progressBarContainer.setAlignment(Pos.CENTER);
    progressBarContainer.getChildren().add(progressBar);

    descLabel = new WikiLabel("You will create spreadsheet with " + Session.FILES.size()
            + " files from directory " + Session.DIRECTORY.getName() + ".").setWrapped(true);
    descLabel.setTextAlignment(TextAlignment.LEFT);

    prevButton = new WikiButton("Back", "inversed").linkTo("ChooseColumnsPane", stage).setWidth(100);
    createButton = new WikiButton("Create", "inversed").setWidth(100);
    createButton.setOnAction(event -> {
      try {
        createSpreadsheet();
      } catch (IOException | BiffException | WriteException ex) {
        Logger.getLogger(CreateFilePane.class.getName()).log(Level.SEVERE, null, ex);
      }
    });
    
    this.add(progressBarContainer, 0, 0, 2, 1);
    this.add(descLabel, 0, 1, 2, 1);
    this.addRow(2, prevButton, createButton);

    return this;
  }
  
  private void createSpreadsheet() throws IOException, BiffException, WriteException {
    File f = new File(Session.DIRECTORY, "pattypan.xls");
    WritableWorkbook workbook = Workbook.createWorkbook(f);
    
    WritableSheet sheet = workbook.createSheet("Data", 0);
    int num = 0;
    for(String var : Session.VARIABLES) {
      sheet.addCell(new Label(num++, 0, "%" + var + "%"));
    }
    
    num = 1;
    for(File file : Session.FILES) {
      sheet.addCell(new Label(0, num, file.getAbsolutePath()));
      sheet.addCell(new Label(1, num++, file.getName()));
    }
    
    workbook.write(); 
    workbook.close();
  }
}
