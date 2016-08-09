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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Template {
  
  public String name;
  public TemplateField[] variables;
  public String wikicode;

  public Template(String name) {
    this.name = name;
  }
  
  public Template(String name, TemplateField[] variables, String wikicode) {
    this.name = name;
    this.variables = variables;
    this.wikicode = wikicode;
  }
  
  /**
   * Returns list of variables
   * 
   * @return
   */
  public ArrayList<TemplateField> getVariables() {
    return new ArrayList<>(Arrays.asList(variables));
  }
  
  
  /**
   * Returns selected template variables with additional "path" and "name" vars
   * 
   * @return
   */
  public ArrayList<String> getComputedVariables() {
    ArrayList<String> vars = new ArrayList<>(Arrays.asList("path", "name"));
    for (TemplateField tf : variables) {
      if (tf.isSelected) {
        vars.add(tf.name);
      }
    }
    vars.add("categories");

    return vars;
  }
  
  
  /**
   * Gets "raw" wikicode, removes variables set as "No" and sets constant 
   * strings for variables set as "Cont"
   * 
   * @return string with computed wikicode
   */
  public String getComputedWikicode() {
    ArrayList<TemplateField> vars = getVariables();
    String text = wikicode;

    for (TemplateField var : vars) {
      if (!var.isSelected && !var.value.isEmpty()) {
        text = text.replace("${" + var.name + "}", var.value);
      } else if (!var.isSelected && var.value.isEmpty()) {
        text = text.replace("${" + var.name + "}", "");
      }
    }

    return text;
  }
  
  /**
   * 
   * 
   * @param text input string
   * @return 
   */
  public static ArrayList<String> getComputedVariablesFromString(String text) {
    final Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
    Matcher m = pattern.matcher(text);
    
    ArrayList<String> results = new ArrayList<>();
    results.add("path");
    results.add("name");

    while (m.find()) {
      results.add(m.group(1));
    }
    return results;
  }
}
