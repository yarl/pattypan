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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import pattypan.Session;
import pattypan.UploadElement;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiPane;
import pattypan.elements.WikiTextField;

public class ValidatePane extends WikiPane {

  Stage stage;
  Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
  
  WikiLabel descLabel;
  WikiTextField browsePath;
  WikiButton browseButton;
  VBox infoContainer = new VBox(2);

  public ValidatePane(Stage stage) {
    super(stage, 1.01);
    this.stage = stage;
  
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
    
    setContent();
  }

  public WikiPane getContent() {
    return this;
  }

  private void addInfo(String text) {
    infoContainer.getChildren().add(new WikiLabel(text).setAlign("left"));
  }
  
  private void selectFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose file");
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls");
    fileChooser.getExtensionFilters().add(extFilter);

    File file = fileChooser.showOpenDialog(stage);
    if (file != null) {
      loadFile(file);
    }
  }
  
  private void loadFile(File file) {
    Session.DIRECTORY = file.getParentFile();
    Session.FILE = file;
    browsePath.setText(file.getAbsolutePath());

    int result = readFile();
    switch (result) {
      case -1:
        addInfo("Something is wrong with file!");
        break;
      case -2:
        addInfo("Errors in template!");
        break;
      case 0:
        addInfo("No files in spreadsheet!");
        break;
      default:
        addInfo(result + " files loaded successfully!");
        nextButton.setDisable(false);
        break;
    }
  }

  private ArrayList<Map<String, String>> readDescriptions(Sheet sheet) {
    ArrayList<Map<String, String>> descriptions = new ArrayList<>();
    int rows = sheet.getRows();
    int columns = sheet.getColumns();

    for (int row = 1; row < rows; row++) {
      Map<String, String> description = new HashMap();
      for (int column = 0; column < columns; column++) {
        String label = sheet.getCell(column, 0).getContents();
        if(label.isEmpty()) continue;
        String value = sheet.getCell(column, row).getContents();
        description.put(label, value);
      }
      descriptions.add(description);
    }
    return descriptions;
  }

  private Template readTemplate(Sheet sheet) throws IOException {
    String text = sheet.getCell(0, 0).getContents();
    return new Template("wikitemplate", new StringReader(text), cfg);
  }
  
  private int addFilesToUpload(ArrayList<Map<String, String>> descriptions, Template template) {
    Session.FILES_TO_UPLOAD = new ArrayList<>();
    
    for (Map<String, String> description : descriptions) {
      try {
        addInfo("Loading '" + description.get("path") + "'");
        
        if(description.get("path").isEmpty() || description.get("name").isEmpty()) {
          //addInfo("Essential parametes are missing!");
          continue;
        }
        
        if (description.containsValue("")) {
          //addInfo("Warning: some parameters are empty!");
        }
        
        StringWriter writer = new StringWriter();
        template.process(description, writer);
        String wikicode = writer.getBuffer().toString();
        Session.FILES_TO_UPLOAD.add(new UploadElement(description, wikicode));
        //addInfo("OK");
      } catch (TemplateException | IOException ex) {
        Logger.getLogger(ValidatePane.class.getName()).log(Level.SEVERE, null, ex);
        return -2;
      }
    }
    
    return Session.FILES_TO_UPLOAD.size();
  }
  
  private int readFile() {
    try {
      WorkbookSettings ws = new WorkbookSettings();
      ws.setEncoding("Cp1252");
      
      Workbook workbook = Workbook.getWorkbook(Session.FILE, ws);
      ArrayList<Map<String, String>> descriptions = readDescriptions(workbook.getSheet(0));
      Template template = readTemplate(workbook.getSheet(1));
      
      return addFilesToUpload(descriptions, template);
    } catch (IOException | BiffException ex) {
      Logger.getLogger(ValidatePane.class.getName()).log(Level.SEVERE, null, ex);
      return -1;
    }
  }

  private WikiPane setContent() {

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
    
    addElement(infoContainer);
    
    prevButton.linkTo("StartPane", stage);
    nextButton.linkTo("LoginPane", stage);
    nextButton.setDisable(true);
    
    return this;
  }

}
