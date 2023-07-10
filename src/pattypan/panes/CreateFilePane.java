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

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import pattypan.Session;
import pattypan.Settings;
import pattypan.Template;
import pattypan.TemplateField;
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
    addElement(new Region());
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
        showOpenFileButton();
        Settings.saveProperties();
      } catch (IOException  ex) {
        addElement(new WikiLabel("create-file-error"));
        Session.LOGGER.log(Level.WARNING, 
            "Error occurred during creation of spreadsheet file: {0}",
            new String[]{ex.getLocalizedMessage()}
        );
      }
    });

    return this;
  }

  private void showOpenFileButton() {
    Hyperlink link = new Hyperlink(Util.text("create-file-open"));
    TextFlow flow = new TextFlow(new WikiLabel("create-file-success"), link);
    flow.setTextAlignment(TextAlignment.CENTER);
    addElement(flow);
    link.setOnAction(ev -> {
      try {
        Desktop.getDesktop().open(Session.FILE);
      } catch (IOException ex) {
        Session.LOGGER.log(Level.WARNING, 
            "Cannot open file: {0}",
            new String[]{ex.getLocalizedMessage()}
        );
      }
    });

    nextButton.linkTo("StartPane", stage, true).setText(Util.text("create-file-back-to-start"));
    nextButton.setVisible(true);
  }

  private void autoSizeColumn(int column, Sheet sheet) {
    sheet.autoSizeColumn(column);
  }

  private void createSpreadsheet() throws IOException {
    File f = new File(Session.DIRECTORY, fileName.getText() + ".xlsx");
    // Autosize appears to only be supported for OOXML workbooks, so making this specific instead of generic
    //    boolean xml = true;
    //    final Workbook workbook = xml ? new SXSSFWorkbook() : new HSSFWorkbook();
    final SXSSFWorkbook workbook = new SXSSFWorkbook();

    createDataSheet(workbook);
    createTemplateSheet(workbook);

    try (OutputStream os = new FileOutputStream(f)){
      workbook.write(os);
      os.flush();
    }
    workbook.close();
    Session.FILE = f;
  }

  /**
   *
   * @param workbook
   */
  private void createDataSheet(SXSSFWorkbook workbook) {
    SXSSFSheet sheet = workbook.createSheet("Data");

    // first row (header)
    Row header = sheet.createRow(0);
    int column = 0;
    for (String variable : Session.VARIABLES) {
      Cell c = header.createCell(column++);
      c.setCellValue(variable);
    }

    // next rows with path and name
    int row = 1;
    for (File file : Session.FILES) {
      Row cells = sheet.createRow(row++);
      cells.createCell(0, CellType.STRING).setCellValue(file.getAbsolutePath());
      cells.createCell(1, CellType.STRING).setCellValue(Util.getNameFromFilename(file.getName()));
    }

    if (Session.METHOD.equals("template")) {
      Template template = Settings.TEMPLATES.get(Session.TEMPLATE);
      for (TemplateField tf : template.variables) {
        if (tf.isSelected && !tf.value.isEmpty()) {
          column = Session.VARIABLES.indexOf(tf.name);
          row = 1;
          for (File file : Session.FILES) {
            // TODO: We may not have the cells created yet here. Let's see what happens
            sheet.getRow(row++).getCell(column).setCellValue(tf.value);
          }
        }
      }
    }

    column = Session.VARIABLES.indexOf("date");
    if (column >= 0 && !Settings.getSetting("exifDate").isEmpty()) {
      row = 1;
      for (File file : Session.FILES) {
        // TODO: We may not have the cells created yet here. Let's see what happens
        sheet.getRow(row++).getCell(column).setCellValue(getExifDate(file));
      }
    }

    sheet.trackAllColumnsForAutoSizing();
    for (int num = 0; num < sheet.getRow(0).getLastCellNum(); num++) {
      autoSizeColumn(num, sheet);
    }
  }

  /**
   *
   * @param workbook
   */
  private void createTemplateSheet(SXSSFWorkbook workbook)  {
    SXSSFSheet templateSheet = workbook.createSheet("Template");
    Row row = templateSheet.createRow(0);
    Cell cell = row.createCell(0);
    cell.setCellValue("'" + Session.WIKICODE);
    //                 ^^
    // leading apostrophe prevents turning wikitext into formula in Excel

    templateSheet.trackAllColumnsForAutoSizing();
    autoSizeColumn(0, templateSheet);
  }

  /**
   *
   * @param file
   * @return
   */
  private String getExifDate(File file) {

    try {
      Metadata metadata = ImageMetadataReader.readMetadata(file);
      Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
      int dateTag = ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL;

      if (directory != null && directory.containsTag(dateTag)) {
        Date date = directory.getDate(dateTag, TimeZone.getDefault());
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
      } else {
        return "";
      }
    } catch (ImageProcessingException | IOException ex) {
      Session.LOGGER.log(Level.INFO, 
          "Exif error for {0}: {1}",
          new String[]{file.getName(), ex.getLocalizedMessage()}
      );
      return "";
    }
  }
}
