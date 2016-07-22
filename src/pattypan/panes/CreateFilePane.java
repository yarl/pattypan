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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.Stage;
import jxl.CellView;
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
import pattypan.elements.WikiPane;
import pattypan.elements.WikiTextField;

public class CreateFilePane extends WikiPane {

  Stage stage;

  WikiLabel descLabel;
  WikiTextField fileName = new WikiTextField("").setPlaceholder("create-file-filename").setWidth(300);
  WikiButton createButton = new WikiButton("create-file-button", "primary").setWidth(300);

  public CreateFilePane(Stage stage) {
    super(stage, 1.0);
    this.stage = stage;

    setContent();
    setActions();
  }

  public WikiPane getContent() {
    return this;
  }

  private WikiPane setContent() {
    addElement("generic-summary", "header");
    addElement(Util.text("create-file-summary", Session.FILES.size(), Session.DIRECTORY.getName()), 40);
    addElement(fileName);
    addElement(createButton);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
    fileName.setText("pattypan " + sdf.format(new Date()));

    prevButton.linkTo("ChooseColumnsPane", stage);
    nextButton.setVisible(false);

    return this;
  }

  private WikiPane setActions() {
    fileName.textProperty().addListener((observable, oldValue, newValue) -> {
      createButton.setDisable(newValue.isEmpty());
    });

    createButton.setOnAction(event -> {
      try {
        createSpreadsheet();
        addElement(new WikiLabel("create-file-success"));
        showOpenFileButton();
      } catch (IOException | BiffException | WriteException ex) {
        addElement(new WikiLabel("create-file-error"));
        Logger.getLogger(CreateFilePane.class.getName()).log(Level.SEVERE, null, ex);
      }
    });

    return this;
  }

  private void showOpenFileButton() {
    nextButton.setText(Util.text("create-file-open"));
    nextButton.setVisible(true);
    nextButton.setOnAction(event -> {
      try {
        Desktop.getDesktop().open(Session.FILE);
        stage.close();
      } catch (IOException ex) {
        Logger.getLogger(CreateFilePane.class.getName()).log(Level.SEVERE, null, ex);
      }
    });
  }

  private void autoSizeColumn(int column, WritableSheet sheet) {
    CellView cell = sheet.getColumnView(column);
    cell.setAutosize(true);
    sheet.setColumnView(column, cell);
  }

  private void createSpreadsheet() throws IOException, BiffException, WriteException {
    File f = new File(Session.DIRECTORY, fileName.getText() + ".xls");
    WritableWorkbook workbook = Workbook.createWorkbook(f);

    WritableSheet sheet = workbook.createSheet("Data", 0);
    int num = 0;
    for (String var : Session.VARIABLES) {
      sheet.addCell(new Label(num++, 0, var));
    }

    num = 1;
    for (File file : Session.FILES) {
      sheet.addCell(new Label(0, num, file.getAbsolutePath()));
      sheet.addCell(new Label(1, num++, Util.getNameFromFilename(file.getName())));
    }

    for (num = 0; num < sheet.getColumns(); num++) {
      autoSizeColumn(num, sheet);
    }

    WritableSheet templateSheet = workbook.createSheet("Template", 1);
    templateSheet.addCell(new Label(0, 0, "'" + Session.WIKICODE));
    //                                    ^^
    // leading apostrophe prevents turning wikitext into formula in Excel

    autoSizeColumn(0, templateSheet);

    workbook.write();
    workbook.close();
    Session.FILE = f;
  }
}
