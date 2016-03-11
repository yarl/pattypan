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
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import pattypan.Session;
import pattypan.Settings;
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
  ScrollPane scrollText = new ScrollPane();

  GridPane templatePane = new GridPane();
  ComboBox templateBox = new ComboBox();
  VBox templateCheckboxContainer = new VBox(4);
  
  GridPane wikicodePane = new GridPane();
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
   * @param templateName name of wikitemplate
   * @return true, if template exists
   */
  private boolean addCheckboxes(String templateName) {
    templateCheckboxContainer.getChildren().clear();
    if (templateName.equals("Artwork")) {
      for (TemplateField tf : Settings.TEMPLATE_ARTWORK) {
        templateCheckboxContainer.getChildren().add(tf.getCheckbox());
      }
      return true;
    }
    
    return false;
  }

  private String getTemplateWikicode() {
    if (Session.TEMPLATE.equals("Artwork")) {
      ArrayList<String> vars = Util.getVariablesFromString(Settings.WIKICODE_ARTWORK);
      vars.removeAll(Session.VARIABLES);

      String wikicode = Settings.WIKICODE_ARTWORK;
      for (String var : vars) {
        wikicode = wikicode.replace("${" + var + "}", "");
      }
      return wikicode;
    }
    return "";
  }

  private ArrayList<String> getTemplateVariables() {
    ArrayList<String> vars = new ArrayList<>();

    for (Node n : templateCheckboxContainer.getChildren()) {
      CheckBox cb = (CheckBox) n;
      if (cb.isSelected()) {
        vars.add(cb.getTooltip().getText());
      }
    }

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
        ArrayList<String> vars = Util.getVariablesFromString(wikicodeText.getText());
        Session.VARIABLES.addAll(vars);
        Session.WIKICODE = wikicodeText.getText();
      }

      if (Session.METHOD.equals("template")) {
        Session.VARIABLES = getTemplateVariables();
        Session.WIKICODE = getTemplateWikicode();
      }

      nextButton.goTo("CreateFilePane", stage);
    });
    return this;
  }
  
  private WikiPane setContent() {
    descLabel = new WikiLabel("In cursus nunc enim, ac ullamcorper lectus consequat accumsan. Mauris erat sapien, iaculis a quam in, molestie dapibus libero. Morbi mollis mattis porta. Pellentesque at suscipit est, id vestibulum risus.").setWrapped(true);
    descLabel.setTextAlignment(TextAlignment.LEFT);
    addElement(descLabel);

    /* buttons */
    templateButton = new WikiButton("Use template", "group-left").setWidth(150);
    wikicodeButton = new WikiButton("Write wikicode", "group-right", "inversed").setWidth(150);
    addElementRow(0,
            new Node[]{templateButton, wikicodeButton},
            new Priority[]{Priority.NEVER, Priority.NEVER}
    );

    /* templates pane */
    templateBox.getItems().addAll("Artwork", "Book");
    templateBox.getSelectionModel().select(Session.TEMPLATE);
    addCheckboxes();

    templatePane.add(templateBox, 0, 0);
    templatePane.add(templateCheckboxContainer, 0, 1);
    addElement(templatePane);

    /* wiki code pane */
    wikicodeText.getStyleClass().add("mw-ui-input");
    wikicodeText.setText(Session.WIKICODE);
    wikicodePane.add(wikicodeText, 0, 0);
    return this;
  }
}
