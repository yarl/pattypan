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

import freemarker.core.InvalidReferenceException;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import pattypan.Session;
import pattypan.UploadElement;
import pattypan.Util;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiPane;
import pattypan.elements.WikiTextField;

public class ValidatePane extends WikiPane {

  Stage stage;
  Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

  WikiTextField browsePath;
  WikiButton browseButton;
  VBox infoContainer = new VBox(4);

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

  /**
   * Shows file shooser dialog.
   */
  private void selectFile() {
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(Util.text("validate-file-type"), "*.xls");

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(Util.text("validate-file-select"));
    fileChooser.getExtensionFilters().add(extFilter);

    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile != null) {
      loadSelectedFile(selectedFile);
    }
  }

  private void loadSelectedFile(File file) {
    Session.DIRECTORY = file.getParentFile();
    Session.FILE = file;
    browsePath.setText(file.getAbsolutePath());

    readSelectedFile();
  }

  /**
   * Checks headers of data sheet (first row).
   *
   * @param sheet sheet with data
   * @throws Exception when essential headers are missing
   */
  private void checkHeaders(Sheet sheet) throws Exception {
    int columns = sheet.getColumns();
    ArrayList<String> cols = new ArrayList<>();
    for (int col = 0; col < columns; col++) {
      cols.add(sheet.getCell(col, 0).getContents());
    }

    if (cols.isEmpty() || !cols.contains("path") || !cols.contains("name")) {
      throw new Exception("Headers error!");
    }
  }

  private ArrayList<Map<String, String>> readDescriptions(Sheet sheet) throws Exception {
    ArrayList<Map<String, String>> descriptions = new ArrayList<>();
    int rows = sheet.getRows();
    int columns = sheet.getColumns();

    checkHeaders(sheet);

    for (int row = 1; row < rows; row++) {
      Map<String, String> description = new HashMap();
      for (int column = 0; column < columns; column++) {
        String label = sheet.getCell(column, 0).getContents();
        if (label.isEmpty()) {
          continue;
        }
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

  private void addFilesToUpload(ArrayList<Map<String, String>> descriptions, Template template) throws TemplateException, IOException, Exception {
    Session.FILES_TO_UPLOAD = new ArrayList<>();

    ArrayList<String> errors = new ArrayList<>();
    ArrayList<String> warnings = new ArrayList<>();

    for (Map<String, String> description : descriptions) {
      String namePath = String.format("%s (%s)",
              description.get("name"), description.get("path"));

      if (description.get("path").isEmpty() && description.get("name").isEmpty()) {
        continue;
      }
      
      if (description.get("path").isEmpty() || description.get("name").isEmpty()) {
        errors.add(namePath);
        continue;
      }

      File f = new File(description.get("path"));
      if (!f.isFile()) {
        errors.add(namePath);
        continue;
      }

      if (description.containsValue("")) {
        warnings.add(namePath);
      }

      StringWriter writer = new StringWriter();
      template.process(description, writer);
      String wikicode = writer.getBuffer().toString();

      if (String.valueOf(wikicode.charAt(0)).equals("'")) {
        wikicode = wikicode.substring(1);
      }

      System.out.println(namePath);
      System.out.println(wikicode + '\n');

      if (wikicode.isEmpty()) {
        throw new Exception("Error: empty template!");
      }

      Session.FILES_TO_UPLOAD.add(new UploadElement(description, wikicode));
    }

    infoContainer.getChildren().add(new WikiLabel("Summary").setAlign("left").setClass("header"));
    addInfo(Session.FILES_TO_UPLOAD.size() + " files loaded successfully");
    addInfo(errors.size() + " errors");
    addInfo(warnings.size() + " warnings");

    WikiButton reloadButton = new WikiButton("Reload", "inversed");
    reloadButton.setOnAction(event -> {
      infoContainer.getChildren().clear();
      readSelectedFile();
    });

    infoContainer.getChildren().addAll(new Region(), new Region(), reloadButton);

    if (Session.FILES_TO_UPLOAD.size() > 0) {
      nextButton.setDisable(false);
    }
  }

  private void readSelectedFile() {
    try {
      WorkbookSettings ws = new WorkbookSettings();
      ws.setEncoding("Cp1252");

      Workbook workbook = Workbook.getWorkbook(Session.FILE, ws);
      ArrayList<Map<String, String>> descriptions = readDescriptions(workbook.getSheet(0));
      Sheet s = workbook.getSheet(1);
      Template template = readTemplate(s);

      addFilesToUpload(descriptions, template);
    } catch (IOException ex) {
      addInfo("File error: there are problems opening file. It may be corrupted.");
    } catch (BiffException ex) {
      addInfo("File error: file needs to be saved in binnary format. Please save your file in \"Excel 97-2003 format\"");
    } catch (InvalidReferenceException ex) {
      addInfo("File error: variables mismatch. Column headers variables must match wikitemplate variables.");
    } catch (Exception ex) {
      addInfo(ex.getMessage());
    }
  }

  private WikiPane setContent() {
    addElement("validate-intro", 40);

    browsePath = new WikiTextField("");
    browsePath.setDisable(true);
    browseButton = new WikiButton("generic-browse", "small").setWidth(100);
    browseButton.setOnAction((ActionEvent e) -> {
      selectFile();
    });
    addElementRow(
            new Node[]{browseButton, browsePath},
            new Priority[]{Priority.NEVER, Priority.ALWAYS}
    );

    addElement(new ScrollPane(infoContainer));

    prevButton.linkTo("StartPane", stage);
    nextButton.linkTo("LoginPane", stage);
    nextButton.setDisable(true);

    return this;
  }

}
