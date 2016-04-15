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

import java.util.HashMap;
import java.util.Map;

public final class Settings {

  private Settings() {};
  
  public static final String NAME = "pattypan";
  public static final String VERSION = "0.1.0";
  public static final String USERAGENT = NAME + "/" + VERSION + " (https://github.com/yarl/pattypan)";

  public static int WINDOW_WIDTH = 600;
  public static int WINDOW_HEIGHT = 400;

  public static final Map<String, Template> TEMPLATES = new HashMap<>();

  static {
    TEMPLATES.put(
            "Information",
            new Template("Information",
                    new TemplateField[]{
                      new TemplateField("description", "Description"),
                      new TemplateField("date", "Date"),
                      new TemplateField("source", "Source"),
                      new TemplateField("author", "Author"),
                      new TemplateField("permission", "Permission"),
                      new TemplateField("other_versions", "Other versions"),
                      new TemplateField("license", "License"),
                    }, "=={{int:filedesc}}==\n"
                    + "{{Information\n"
                    + " |description = ${description}\n"
                    + " |date = ${date}\n"
                    + " |source = ${source}\n"
                    + " |author = ${author}\n"
                    + " |permission = ${permission}\n"
                    + " |other versions = ${other_versions}\n"
                    + "}}\n\n"
                    + "=={{int:license-header}}==\n${license}\n\n"
                    + "<#if categories ? has_content>\n"
                    + "<#list categories ? split(\",\") as category>\n"
                    + "[[Category:${category?trim}]]\n"
                    + "</#list>\n"
                    + "<#else>{{subst:unc}}\n"
                    + "</#if>"
            )
    );
    TEMPLATES.put(
            "Artwork",
            new Template("Artwork",
                    new TemplateField[]{
                      new TemplateField("artist", "Artist"),
                      new TemplateField("author", "Author"),
                      new TemplateField("title", "Title"),
                      new TemplateField("description", "Description"),
                      new TemplateField("date", "Date"),
                      new TemplateField("medium", "Medium"),
                      new TemplateField("dimensions", "Dimensions"),
                      new TemplateField("institution", "Institution"),
                      new TemplateField("departament", "Departament"),
                      new TemplateField("place_of_discovery", "Place of discovery"),
                      new TemplateField("object_history", "Object history"),
                      new TemplateField("exhibition_history", "Exhibition history"),
                      new TemplateField("credit_line", "Credit line"),
                      new TemplateField("inscriptions", "Inscriptions"),
                      new TemplateField("notes", "Notes"),
                      new TemplateField("accession_number", "Accession number"),
                      new TemplateField("place_of_creation", "Place of creation"),
                      new TemplateField("source", "Source"),
                      new TemplateField("permission", "Permission"),
                      new TemplateField("other_versions", "Other versions"),
                      new TemplateField("references", "References"),
                      new TemplateField("wikidata", "Wikidata"),
                      new TemplateField("license", "License"),
                      new TemplateField("partnership", "Partnership")
                    }, "=={{int:filedesc}}==\n"
                    + "{{Artwork\n"
                    + " |artist = ${artist}\n"
                    + " |author = ${author}\n"
                    + " |title = ${title}\n"
                    + " |description = ${description}\n"
                    + " |date = ${date}\n"
                    + " |medium = ${medium}\n"
                    + " |dimensions = ${dimensions}\n"
                    + " |institution = ${institution}\n"
                    + " |department = ${departament}\n"
                    + " |place of discovery = ${place_of_discovery}\n"
                    + " |object history = ${object_history}\n"
                    + " |exhibition history = ${exhibition_history}\n"
                    + " |credit line = ${credit_line}\n"
                    + " |inscriptions = ${inscriptions}\n"
                    + " |notes = ${notes}\n"
                    + " |accession number = ${accession_number}\n"
                    + " |place of creation = ${place_of_creation}\n"
                    + " |source = ${source}\n"
                    + " |permission = ${permission}\n"
                    + " |other_versions = ${other_versions}\n"
                    + " |references = ${references}\n"
                    + " |wikidata = ${wikidata}\n"
                    + "}}\n\n"
                    + "=={{int:license-header}}==\n"
                    + "${license}${partnership}"
                    + "\n\n"
                    + "<#if categories ? has_content>\n"
                    + "<#list categories ? split(\",\") as category>\n"
                    + "[[Category:${category?trim}]]\n"
                    + "</#list>\n"
                    + "<#else>{{subst:unc}}\n"
                    + "</#if>"
            )
    );
    TEMPLATES.put("Photograph",
            new Template("Photograph",
                    new TemplateField[]{
                      new TemplateField("photographer", "Photographer"),
                      new TemplateField("title", "Title"),
                      new TemplateField("description", "Description"),
                      new TemplateField("depicted_people", "Depicted people"),
                      new TemplateField("depicted_place", "Depicted place"),
                      new TemplateField("date", "Date"),
                      new TemplateField("medium", "Medium"),
                      new TemplateField("dimensions", "Dimensions"),
                      new TemplateField("institution", "Institution"),
                      new TemplateField("department", "Department"),
                      new TemplateField("references", "References"),
                      new TemplateField("object_history", "Object history"),
                      new TemplateField("exhibition_history", "Exhibition history"),
                      new TemplateField("credit_line", "Credit line"),
                      new TemplateField("inscriptions", "Inscriptions"),
                      new TemplateField("notes", "Notes"),
                      new TemplateField("accession_number", "Accession number"),
                      new TemplateField("source", "Source"),
                      new TemplateField("permission", "Permission"),
                      new TemplateField("other_versions", "Other versions"),
                      new TemplateField("license", "License"),
                      new TemplateField("partnership", "Partnership")
                    }, "=={{int:filedesc}}==\n"
                    + "{{Photograph\n"
                    + " |photographer = ${photographer}\n"
                    + " |title = ${title}\n"
                    + " |description = ${description}\n"
                    + " |depicted people = ${depicted_people}\n"
                    + " |depicted place = ${depicted_place}\n"
                    + " |date = ${date}\n"
                    + " |medium = ${medium}\n"
                    + " |dimensions = ${dimensions}\n"
                    + " |institution = ${institution}\n"
                    + " |department = ${department}\n"
                    + " |references = ${references}\n"
                    + " |object history = ${object_history}\n"
                    + " |exhibition history = ${exhibition_history}\n"
                    + " |credit line = ${credit_line}\n"
                    + " |inscriptions = ${inscriptions}\n"
                    + " |notes = ${notes}\n"
                    + " |accession number = ${accession_number}\n"
                    + " |source = ${source}\n"
                    + " |permission = ${permission}\n"
                    + " |other_versions = ${other_versions}\n"
                    + "}}\n\n"
                    + "=={{int:license-header}}==\n"
                    + "${license}${partnership}"
                    + "\n\n"
                    + "<#if categories ? has_content>\n"
                    + "<#list categories ? split(\",\") as category>\n"
                    + "[[Category:${category?trim}]]\n"
                    + "</#list>\n"
                    + "<#else>{{subst:unc}}\n"
                    + "</#if>"
            )
    );
    
    /*
    TEMPLATES.put("Name",
            new Template("Name",
                    new TemplateField[]{
                      new TemplateField("", ""),
                      new TemplateField("", ""),
                    }, ""
            )
    );
    */
  }
}
