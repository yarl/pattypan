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

public final class Settings {
  
  private Settings() {};
  
  public static int WINDOW_WIDTH = 600;
  public static int WINDOW_HEIGHT = 400;
  
  public static TemplateField[] TEMPLATE_ARTWORK = {
    new TemplateField("artist", "Artist"),
    new TemplateField("author", "Author"),
    new TemplateField("title", "Title"),
    new TemplateField("description", "Description"),
    new TemplateField("date", "Date"),
    new TemplateField("medium", "Medium"),
    new TemplateField("dimensions", "Dimensions"),
    new TemplateField("institution", "Institution")
  };
  
  public static String WIKICODE_ARTWORK = "{{Artwork\n" +
    " |artist             = ${artist}\n" +
    " |author             = ${author}\n" +
    " |title              = ${title}\n" +
    " |description        = ${description}\n" +
    " |date               = ${date}\n" +
    " |medium             = ${medium}\n" +
    " |dimensions         = ${dimensions}\n" +
    " |institution        = ${institution}\n" +
    " |department         = ${departament}\n" +
    " |place of discovery = \n" +
    " |object history     =\n" +
    " |exhibition history =\n" +
    " |credit line        =\n" +
    " |inscriptions       =\n" +
    " |notes              =\n" +
    " |accession number   =\n" +
    " |place of creation  =\n" +
    " |source             =\n" +
    " |permission         =\n" +
    " |other_versions     =\n" +
    " |references         =\n" +
    " |wikidata           =\n" +
    "}}";
}
