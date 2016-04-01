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

import java.util.ArrayList;
import java.util.Arrays;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import pattypan.Session;
import pattypan.Settings;
import pattypan.Template;
import pattypan.TemplateField;
import pattypan.Util;
import pattypan.elements.WikiButton;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiPane;

public class ChooseColumnsPane extends WikiPane {

  Stage stage;

  WikiLabel descLabel;
  WikiButton templateButton;
  WikiButton wikicodeButton;

  VBox templatePane = new VBox(10);
  ComboBox templateBox = new ComboBox();
  VBox templateCheckboxContainer = new VBox(4);

  VBox wikicodePane = new VBox(10);
  TextArea wikicodeText = new TextArea("");

  public ChooseColumnsPane(Stage stage) {
    super(stage, 0.5);
    this.stage = stage;

    setContent();
    setActions();
  }

  public WikiPane getContent() {
    return this;
  }

  /**
   * Adds checkboxes with wikitemplate fields.
   *
   * @param templateName name of wikitemplate
   * @return true, if template exists
   */
  private boolean addCheckboxes(String templateName) {
    templateCheckboxContainer.getChildren().clear();
    Template t = Settings.TEMPLATES.get(templateName);

    if (t == null) {
      return false;
    }

    for (TemplateField tf : t.variables) {
      templateCheckboxContainer.getChildren().add(tf.getCheckbox());
    }
    return true;
  }

  private String getTemplateWikicode(String templateName) {
    Template t = Settings.TEMPLATES.get(templateName);

    if (t == null) {
      return "";
    }

    ArrayList<String> vars = t.getTemplateVariables();
    String wikicode = t.wikicode;
   
    vars.removeAll(Session.VARIABLES);
    for (String var : vars) {
      wikicode = wikicode.replace("${" + var + "}", "");
    }
    return wikicode;
  }

  private ArrayList<String> getTemplateVariables() {
    ArrayList<String> vars = new ArrayList<>(Arrays.asList("path", "name"));

    for (Node n : templateCheckboxContainer.getChildren()) {
      CheckBox cb = (CheckBox) n;
      if (cb.isSelected()) {
        vars.add(cb.getTooltip().getText());
      }
    }

    return vars;
  }
  
  private ArrayList<String> getWikicodeVariables() {
    ArrayList<String> vars = new ArrayList<>(Arrays.asList("path", "name"));
    vars.addAll(Util.getVariablesFromString(wikicodeText.getText()));
    
    return vars;
  }

  private WikiPane setActions() {
    templateBox.setOnAction((Event ev) -> {
      Session.TEMPLATE = templateBox.getSelectionModel().getSelectedItem().toString();
      addCheckboxes(Session.TEMPLATE);
    });

    templateButton.setOnAction(event -> {
      templateButton.getStyleClass().remove("mw-ui-inversed");
      wikicodeButton.getStyleClass().add("mw-ui-inversed");
      if (removeElement(wikicodePane)) {
        addElement(templatePane);
      }
      Session.METHOD = "template";
    });

    wikicodeButton.setOnAction(event -> {
      wikicodeButton.getStyleClass().remove("mw-ui-inversed");
      templateButton.getStyleClass().add("mw-ui-inversed");

      if (removeElement(templatePane)) {
        addElement(wikicodePane);
      }
      Session.METHOD = "wikicode";
    });

    prevButton.linkTo("ChooseDirectoryPane", stage);
    nextButton.setOnAction(event -> {
      if (Session.METHOD.equals("wikicode")) {
        Session.VARIABLES = getWikicodeVariables();
        Session.WIKICODE = wikicodeText.getText().trim();
      }

      if (Session.METHOD.equals("template")) {
        Session.VARIABLES = getTemplateVariables();
        Session.WIKICODE = getTemplateWikicode(Session.TEMPLATE);
      }

      nextButton.goTo("CreateFilePane", stage);
    });
    return this;
  }

  private WikiPane setContent() {
    descLabel = new WikiLabel("choose-columns-intro").setWrapped(true);
    descLabel.setMinHeight(40);
    descLabel.setTextAlignment(TextAlignment.LEFT);
    addElement(descLabel);

    /* buttons */
    templateButton = new WikiButton("choose-columns-template", "group-left").setWidth(150);
    wikicodeButton = new WikiButton("choose-columns-wikicode", "group-right", "inversed").setWidth(150);
    addElementRow(0,
            new Node[]{templateButton, wikicodeButton},
            new Priority[]{Priority.NEVER, Priority.NEVER}
    );

    /* templates pane */
    templateBox.getItems().addAll("Artwork", "Book"); // no l10n
    templateBox.getSelectionModel().select(Session.TEMPLATE);
    addCheckboxes(Session.TEMPLATE);

    templatePane.getChildren().addAll(templateBox, new ScrollPane(templateCheckboxContainer));
    addElement(templatePane);

    /* wiki code pane */
    wikicodeText.getStyleClass().add("mw-ui-input");
    wikicodeText.setText(Session.WIKICODE);
    wikicodePane.getChildren().addAll(wikicodeText);
    return this;
  }
}
