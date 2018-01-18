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
package pattypan;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import pattypan.elements.WikiLabel;
import pattypan.elements.WikiRadioButton;
import pattypan.elements.WikiTextField;

public class TemplateField {

  public String name;
  public String label;
  public boolean isSelected;
  public String selection;
  public String value;

  CheckBox cb;
  ToggleGroup group = new ToggleGroup();
  RadioButton buttonYes = new WikiRadioButton("YES", group).setHeight(35);
  RadioButton buttonConst = new WikiRadioButton("CONST", group).setHeight(35);
  RadioButton buttonNo = new WikiRadioButton("NO", group).setHeight(35);

  WikiLabel labelElement = new WikiLabel("");
  WikiTextField valueText = new WikiTextField("").setWidth(50, 500);

  public TemplateField(String name, String label, boolean isSelected, String constant) {
    this.name = name;
    this.label = label;
    this.isSelected = isSelected;
    this.selection = "YES";
    this.value = constant;

    labelElement = new WikiLabel(label).setWidth(200, 500).setHeight(35);
    buttonYes.setSelected(true);

    group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle tOld, Toggle tNew) -> {
      RadioButton btn = (RadioButton) tNew.getToggleGroup().getSelectedToggle();
      setSelection(btn.getId());
    });

    valueText.setOnKeyReleased((KeyEvent event) -> {
      this.value = valueText.getText();
    });
  }

  public TemplateField(String name, String label) {
    this(name, label, true, "");
  }

  public VBox getRow() {
    Region spacer = new Region();
    spacer.setMinWidth(20);

    VBox vb = new VBox(5);
    HBox hb = new HBox(10);
    HBox hbCheckbox = new HBox(10);

    valueText.setText(Settings.getSetting("var-" + name + "-value"));
    value = Settings.getSetting("var-" + name + "-value");
    setSelection(Settings.getSetting("var-" + name + "-selection"));

    hb.getChildren().addAll(labelElement,
            buttonYes, buttonConst, buttonNo,
            spacer, valueText, new Region());
    vb.getChildren().add(hb);

    if (name.equals("date")) {
      Region r = new Region();
      r.setMaxWidth(622);
      r.setPrefWidth(622);
      r.setMinWidth(420);
      r.setMinHeight(30);

      CheckBox checkbox = new CheckBox(Util.text("choose-columns-exif"));
      checkbox.setMaxWidth(500);
      checkbox.setPrefWidth(500);
      checkbox.setMinWidth(305);
      checkbox.setSelected(Settings.getSetting("exifDate").equals("true"));
      checkbox.setOnAction((ActionEvent e) -> {
        Settings.setSetting("exifDate", checkbox.isSelected() ? "true" : "");
      });

      hbCheckbox.getChildren().addAll(r, checkbox);
      vb.getChildren().add(hbCheckbox);
    }

    return vb;
  }

  public void setSelection(String id) {
    this.selection = id;
    switch (id) {
      case "YES":
        valueText.setVisible(true);
        labelElement.setDisable(false);
        buttonYes.setSelected(true);
        this.isSelected = true;
        break;
      case "CONST":
        valueText.setVisible(true);
        labelElement.setDisable(true);
        buttonConst.setSelected(true);
        this.isSelected = false;
        break;
      case "NO":
        valueText.setVisible(false);
        valueText.setText("");
        labelElement.setDisable(true);
        buttonNo.setSelected(true);
        this.isSelected = false;
        break;
      default:
        break;
    }
  }
}
