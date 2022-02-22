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

import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import pattypan.Session;
import pattypan.Settings;
import pattypan.Util;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;

public class StartPane extends GridPane {

  String css = getClass().getResource("/pattypan/style/style.css").toExternalForm();
  Stage stage;

  Hyperlink bugLink = new Hyperlink(Util.text("start-bug-report"));
  Hyperlink downloadLink = new Hyperlink(Util.text("start-new-version-available-download"));
  Hyperlink logFile = new Hyperlink(Util.text("log-file"));

  public StartPane(Stage stage) {
    this.stage = stage;

    setContent();
    setActions();
    checkVersion();
  }

  /*
   * set content and actions
   *****************************************************************************
   */
  public GridPane getContent() {
    return this;
  }

  private void setActions() {
    bugLink.setOnAction(event -> {
      Util.openUrl("https://github.com/yarl/pattypan/issues/new");
    });

    downloadLink.setOnAction(event -> {
      Util.openUrl("https://github.com/yarl/pattypan/releases");
    });

    logFile.setOnAction(event -> {
      Util.openDirectory(Paths.get(Util.getApplicationDirectory() + "/logs"));
    });
  }

  private GridPane setContent() {
    this.getStylesheets().add(css);
    this.setAlignment(Pos.CENTER);
    this.setHgap(20);
    this.setVgap(5);
    this.getStyleClass().add("background");

    this.getColumnConstraints().add(Util.newColumn(400, "px"));

    this.addRow(0, new WikiLabel("pattypan").setClass("title"));
    this.addRow(1, new WikiLabel("v. " + Settings.VERSION));
    if (!Session.WIKI.getDomain().equals("commons.wikimedia.org")) {
      this.addRow(3, new WikiLabel(Session.WIKI.getDomain()));
    }

    this.addRow(20, new HBox(20,
            new WikiButton("start-generate-button", "primary")
                    .setWidth(300)
                    .linkTo("ChooseDirectoryPane", stage),
            new WikiButton("start-validate-button")
                    .setWidth(300)
                    .linkTo("LoadPane", stage)));

    this.addRow(22, new HBox(20,
            new WikiLabel("start-generate-description").setWidth(300),
            new WikiLabel("start-validate-description").setWidth(300)));

    String year = new SimpleDateFormat("yyyy").format(new Date());
    this.addRow(40, new WikiLabel(year + " // Pawel Marynowski").setClass("muted"));

    TextFlow flow = new TextFlow(
            new Text(Util.text("start-bug-found")), bugLink,
            new Text(" • "), logFile);
    flow.setTextAlignment(TextAlignment.CENTER);

    this.addRow(41, flow);
    return this;
  }

  /*
   * methods
   *****************************************************************************
   */
  /**
   * Checks if this Pattypan version is the lastest one. If no, show alert.
   */
  private void checkVersion() {
    try {
      ArrayList<String> versions = getVersions();
      for (String version : versions) {
        if (Util.versionCompare(version, Settings.VERSION) > 0) {
          this.addRow(10, showUpdateAlert());
          break;
        }
      }
    } catch (UnknownHostException ex) {
      Session.LOGGER.log(Level.INFO, "No internet connection found");
    } catch (Exception ex) {
      Session.LOGGER.log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Gets list of stable versions from Github repo
   *
   * @return list of stable releases from Github
   * @throws Exception
   */
  public ArrayList<String> getVersions() throws Exception {
    ArrayList<String> versions = new ArrayList<>();
    String json = Util.readUrl("https://api.github.com/repos/yarl/pattypan/releases");

    JsonArray releases = JsonParser.parseString(json).getAsJsonArray();
    for (JsonElement element : releases) {
      JsonObject release = element.getAsJsonObject();
      boolean draft = release.get("draft").getAsBoolean();
      boolean pre = release.get("prerelease").getAsBoolean();
      String tag = release.get("tag_name").getAsString();

      if (!draft && !pre) {
        versions.add(tag.contains("v") ? tag.substring(1) : tag);
      }
    }
    return versions;
  }

  private TextFlow showUpdateAlert() {
    TextFlow flow = new TextFlow(new Text(Util.text("start-new-version-available")), downloadLink);
    flow.getStyleClass().add("message");
    flow.setTextAlignment(TextAlignment.CENTER);
    return flow;
  }
}
