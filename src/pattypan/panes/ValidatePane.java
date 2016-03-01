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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import pattypan.Session;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiPane;
import pattypan.elements.WikiTextField;

public class ValidatePane extends WikiPane {

  Stage stage;

  WikiLabel descLabel;
  WikiTextField browsePath;
  WikiButton browseButton;

  public ValidatePane(Stage stage) {
    super(stage, 1.01);
    this.stage = stage;

    try {
      setContent();
    } catch (IOException ex) {
      Logger.getLogger(ValidatePane.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public WikiPane getContent() {
    return this;
  }

  private void selectFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose file");
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls");
    fileChooser.getExtensionFilters().add(extFilter);

    File file = fileChooser.showOpenDialog(stage);
    if (file != null) {
      Session.DIRECTORY = file.getParentFile();
      Session.FILE = file;
      browsePath.setText(file.getAbsolutePath());
      
      WikiLabel result = loadFile();
      addElement(result);
    }
  }

  private WikiLabel loadFile() {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);

    try {
      Workbook workbook = Workbook.getWorkbook(Session.FILE);
      Sheet dataSheet = workbook.getSheet(0);
      Sheet templateSheet = workbook.getSheet(1);

      ArrayList<Map<String, String>> descriptions = new ArrayList<>();
      
      int rows = dataSheet.getRows();
      int columns = dataSheet.getColumns();

      for (int row = 1; row < rows; row++) {
        Map<String, String> description = new HashMap();
        for (int column = 0; column < columns; column++) {
          String label = dataSheet.getCell(column, 0).getContents();
          String value = dataSheet.getCell(column, row).getContents();
          description.put(label, value);
        }
        descriptions.add(description);
      }
      
      String wikicode = templateSheet.getCell(0, 0).getContents();
      Template template = new Template("name", new StringReader(wikicode), cfg);

      for(Map<String, String> description : descriptions) {
        template.process(description, new OutputStreamWriter(System.out));
      }
      
      if(descriptions.size() > 0) {
        nextButton.setDisable(false);
      }
      return new WikiLabel(descriptions.size() + " files loaded successfully!");
      
    } catch (IOException | BiffException ex) {
      Logger.getLogger(ValidatePane.class.getName()).log(Level.SEVERE, null, ex);
      return new WikiLabel("Something is wrong with file!");
    } catch (TemplateException ex) {
      Logger.getLogger(ValidatePane.class.getName()).log(Level.SEVERE, null, ex);
      return new WikiLabel("Errors in template!");
    }
  }

  private WikiPane setContent() throws IOException {

    descLabel = new WikiLabel("In cursus nunc enim, ac ullamcorper lectus consequat accumsan. Mauris erat sapien, iaculis a quam in, molestie dapibus libero. Morbi mollis mattis porta. Pellentesque at suscipit est, id vestibulum risus.").setWrapped(true);
    descLabel.setTextAlignment(TextAlignment.LEFT);
    addElement(descLabel);

    browsePath = new WikiTextField("");
    browseButton = new WikiButton("Browse", "small").setWidth(100);
    browseButton.setOnAction((ActionEvent e) -> {
      selectFile();
    });
    addElementRow(
            new Node[]{browsePath, browseButton},
            new Priority[]{Priority.ALWAYS, Priority.NEVER}
    );
    
    prevButton.linkTo("StartPane", stage);
    nextButton.linkTo("LoginPane", stage);
    nextButton.setDisable(true);
    
    if (Session.FILE != null) {
      browsePath.setText(Session.FILE.getAbsolutePath());
      WikiLabel result = loadFile();
      addElement(result);
    }
    
    return this;
  }

}
