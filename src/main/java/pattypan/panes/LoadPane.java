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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import pattypan.Session;
import pattypan.Settings;
import pattypan.UploadElement;
import pattypan.Util;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiPane;
import pattypan.elements.WikiScrollPane;
import pattypan.elements.WikiTextField;

public class LoadPane extends WikiPane {

  Stage stage;
  Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

  WikiTextField browsePath = new WikiTextField("");
  WikiButton browseButton = new WikiButton("generic-browse", "small").setWidth(100);
  WikiButton reloadButton = new WikiButton("", "small", "inversed").setWidth(40).setIcon("refresh.png");
  VBox infoContainer = new VBox(6);

  public LoadPane(Stage stage) {
    super(stage, 1.01);
    this.stage = stage;

    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);

    setContent();
    setActions();
  }

  /*
   * set content and actions
   *****************************************************************************
   */
  public WikiPane getContent() {
    return this;
  }

  private void setActions() {
    browseButton.setOnAction(event -> {
      selectSpreadSheetFile();
    });
    reloadButton.setOnAction(event -> {
      loadSpreadsheet(Session.FILE);
    });
  }

  private void setContent() {
    addElement("validate-intro", 40);

    browsePath.setDisable(true);
    reloadButton.setDisable(true);

    addElementRow(
            new Node[]{browseButton, browsePath, reloadButton},
            new Priority[]{Priority.NEVER, Priority.ALWAYS, Priority.NEVER}
    );
    addElement(new WikiScrollPane(infoContainer));

    prevButton.linkTo("StartPane", stage);
    nextButton.linkTo("CheckPane", stage);
    nextButton.setDisable(true);
  }

  /*
   * methods
   *****************************************************************************
   */
  private boolean checkIfFileExist(String path) {
    String fixedPath = path.trim()
            .replace("/", File.separator)
            .replace("\\", File.separator);

      File file = new File(fixedPath);
      return file.isFile();
  }

  /**
   *
   * @param descriptions
   * @param template
   * @throws TemplateException
   * @throws IOException
   * @throws Exception
   */
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

      try {
        if (description.get("path").isEmpty()) {
          throw new Exception("empty path");
        }
        if (description.get("name").isEmpty()) {
          throw new Exception("empty name");
        }
        if (description.get("path").startsWith("https://") || description.get("path").startsWith("http://")) {
          if (!Util.validUrl(description.get("path"))) {
            throw new Exception("invalid URL");
          }

          // when uploaded from URL the extension is not automatically added
          if (!Util.hasValidFileExtension(description.get("name"))) {
            throw new Exception("filename does not include a valid file extension");
          }
        } else {
          if (!checkIfFileExist(description.get("path"))) {
            throw new Exception("file not found");
          }
        }

        if (Util.hasPossibleBadFilenamePrefix(description.get("name"))) {
          warnings.add(description.get("name") + ": filename shouldn't have name from camera (DSC, DSCF, etc)");
        }

        if (Util.hasInvalidFilenameCharacters(description.get("name"))) {
          throw new Exception(description.get("name") + ": filename shouldn't contain invalid characters (#, ], {, etc)");
        }

        Set<String> keys = Util.getKeysByValue(description, "");
        if (keys.size() > 0) {
          String values = keys.toString();
          warnings.add(namePath + ": empty values for " + values.substring(1, values.length() - 1));
        }

        StringWriter writer = new StringWriter();
        template.process(description, writer);
        String wikicode = writer.getBuffer().toString();

        if (String.valueOf(wikicode.charAt(0)).equals("'")) {
          wikicode = wikicode.substring(1);
        }

        if (wikicode.isEmpty()) {
          throw new Exception("Error: empty template!");
        }

        Session.FILES_TO_UPLOAD.add(new UploadElement(description, wikicode));

      } catch (Exception ex) {
        errors.add(namePath + " " + ex.getMessage());
      }
    }

    infoContainer.getChildren().add(new WikiLabel("Summary").setAlign("left").setClass("header"));
    addInfo(Session.FILES_TO_UPLOAD.size() + " files loaded successfully");
    addInfo(errors.size() + " errors", "bold");
    errors.stream().forEach((error) -> {
      addInfo(error);
    });
    addInfo(warnings.size() + " warnings", "bold");
    warnings.stream().forEach((warning) -> {
      addInfo(warning);
    });

    if (Session.FILES_TO_UPLOAD.size() > 0) {
      nextButton.setDisable(false);
    }
  }

  private void addInfo(String text) {
    addInfo(text, "");
  }

  private void addInfo(String text, String cssClass) {
    infoContainer.getChildren().add(new WikiLabel(text).setAlign("left").setClass(cssClass));
  }

  /**
   * Checks headers of data sheet (first row).
   *
   * @param sheet sheet with data
   * @throws Exception when essential headers are missing
   */
  private void readHeaders(Sheet sheet) throws Exception {
    int columns = sheet.getColumns();
    ArrayList<String> cols = new ArrayList<>();
    for (int col = 0; col < columns; col++) {
      cols.add(sheet.getCell(col, 0).getContents());
    }

    if (cols.isEmpty()) {
      throw new Exception("Header error: columns not found!");
    }
    if (!cols.contains("path") || !cols.contains("name")) {
      throw new Exception("Header error: found " + cols.size() + " headers but 'path' and/or 'name' headers are missing");
    }
  }

  /**
   * Get value of cell
   *
   * @param sheet sheet with data
   * @param column number of column
   * @param row number of cell
   * @return string with data in cell
   */
  private String getCellValue(Sheet sheet, int column, int row) {
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatDateHour = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    formatDate.setTimeZone(TimeZone.getTimeZone("UTC"));
    formatDateHour.setTimeZone(TimeZone.getTimeZone("UTC"));

    Cell valueCell = sheet.getCell(column, row);
    String value;

    if (valueCell.getType() == CellType.DATE) {
      DateCell dateCell = (DateCell) valueCell;
      //@TODO: more elegant hour detection
      value = dateCell.getContents().contains(":")
              ? formatDateHour.format(dateCell.getDate())
              : formatDate.format(dateCell.getDate());
    } else {
      value = sheet.getCell(column, row).getContents().trim();
    }
    return value;
  }

  /**
   * Sets all session stuff and reads spreadsheet.
   *
   * @param file spreadsheet file
   */
  private void loadSpreadsheet(File file) {
    Session.DIRECTORY = file.getParentFile();
    Session.FILE = file;
    browsePath.setText(file.getAbsolutePath());
    Settings.setSetting("path", Session.DIRECTORY.getAbsolutePath());

    try {
      readSpreadSheet();
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

  /**
   * Reads file descriptions from data sheet
   *
   * @param sheet sheet with file descriptions
   * @return
   * @throws Exception
   */
  private ArrayList<Map<String, String>> readDescriptions(Sheet sheet) {
    ArrayList<Map<String, String>> descriptions = new ArrayList<>();
    int rows = sheet.getRows();
    int columns = sheet.getColumns();

    for (int row = 1; row < rows; row++) {
      Map<String, String> description = new HashMap();
      for (int column = 0; column < columns; column++) {
        String label = sheet.getCell(column, 0).getContents().trim();
        if (label.isEmpty()) {
          continue;
        }
        String value = getCellValue(sheet, column, row);
        description.put(label, value);
      }
      descriptions.add(description);
    }
    return descriptions;
  }

  /**
   * Reads spreadsheet stored in Session.FILE.
   */
  private void readSpreadSheet() throws BiffException, IOException, Exception {
    infoContainer.getChildren().clear();
    Session.SCENES.remove("CheckPane");

    WorkbookSettings ws = new WorkbookSettings();
    ws.setEncoding("Cp1252");

    try {
      Workbook workbook = Workbook.getWorkbook(Session.FILE, ws);
      Sheet dataSheet = workbook.getSheet(0);
      Sheet templateSheet = workbook.getSheet(1);
      readHeaders(dataSheet);
      addFilesToUpload(readDescriptions(dataSheet), readTemplate(templateSheet));
    } catch (IndexOutOfBoundsException ex) {
      throw new Exception("Error: your spreadsheet should have minimum two tabs.");
    }

    reloadButton.setDisable(false);
  }

  /**
   * Reads wikitemplate from template sheet
   *
   * @param sheet sheet with wikitemplate
   * @return
   * @throws IOException
   */
  private Template readTemplate(Sheet sheet) throws Exception {
    try {
      String text = sheet.getCell(0, 0).getContents();
      return new Template("wikitemplate", new StringReader(text), cfg);
    } catch (ArrayIndexOutOfBoundsException ex) {
      throw new Exception("Error: template in spreadsheet looks empty. Check if wikitemplate is present in second tab of your spreadsheet (first row and first column).");
    }
  }

  /**
   * Shows spreadsheet shooser dialog.
   */
  private void selectSpreadSheetFile() {
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(Util.text("validate-file-type"), "*.xls");

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(Util.text("validate-file-select"));
    fileChooser.getExtensionFilters().add(extFilter);

    File initialDir = !Settings.getSetting("path").isEmpty()
            ? new File(Settings.getSetting("path"))
            : null;
    fileChooser.setInitialDirectory(initialDir);

    File spreadsheet;
    try {
      spreadsheet = fileChooser.showOpenDialog(stage);
    } catch (IllegalArgumentException ex) {
      fileChooser.setInitialDirectory(null);
      spreadsheet = fileChooser.showOpenDialog(stage);
    }

    if (spreadsheet != null) {
      loadSpreadsheet(spreadsheet);
    }
  }
}
