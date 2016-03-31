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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import pattypan.Session;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiPane;

public class CreateFilePane extends WikiPane {

  Stage stage;

  WikiLabel descLabel;
  WikiButton createButton;

  public CreateFilePane(Stage stage) {
    super(stage, 1.0);
    this.stage = stage;

    setContent();
  }

  public WikiPane getContent() {
    return this;
  }
  
  private WikiPane setContent() {
    descLabel = new WikiLabel(String.format(
            "You will create spreadsheet with %s files from directory %s.",
            Session.FILES.size(), Session.DIRECTORY.getName()
    )).setWrapped(true);
    descLabel.setTextAlignment(TextAlignment.LEFT);
    addElement(descLabel);
    
    createButton = new WikiButton("Create file", "primary").setWidth(200);
    createButton.setOnAction(event -> {
      try {
        createSpreadsheet();
        addElement(new WikiLabel("Spreadsheet created successfully."));
        getOpenFileButton();
      } catch (IOException | BiffException | WriteException ex) {
        addElement(new WikiLabel("Error occurred during creation of spreadsheet file!"));
        Logger.getLogger(CreateFilePane.class.getName()).log(Level.SEVERE, null, ex);
      }
    });
    addElement(createButton);

    prevButton.linkTo("ChooseColumnsPane", stage);
    nextButton.setVisible(false);
    
    return this;
  }
  
  private void getOpenFileButton() {
    nextButton.setText("Open file");
    nextButton.setVisible(true);
    nextButton.setOnAction(event -> {
      try {
        Desktop.getDesktop().open(Session.FILE);
      } catch (IOException ex) {
        Logger.getLogger(CreateFilePane.class.getName()).log(Level.SEVERE, null, ex);
      }
    });
  }
  
  private void createSpreadsheet() throws IOException, BiffException, WriteException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
    File f = new File(Session.DIRECTORY, "pattypan " + sdf.format(new Date()) + ".xls");
    WritableWorkbook workbook = Workbook.createWorkbook(f);
    
    WritableSheet sheet = workbook.createSheet("Data", 0);
    int num = 0;
    for(String var : Session.VARIABLES) {
      sheet.addCell(new Label(num++, 0, var));
    }
    
    num = 1;
    for(File file : Session.FILES) {
      sheet.addCell(new Label(0, num, file.getAbsolutePath()));
      sheet.addCell(new Label(1, num++, file.getName()));
    }
    
    WritableSheet templateSheet = workbook.createSheet("Template", 1);
    templateSheet.addCell(new Label(0, 0, Session.WIKICODE));
    
    workbook.write(); 
    workbook.close();
    Session.FILE = f;
  }
}
