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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

public final class Settings {

  private Settings() {};

  public static final String NAME = "pattypan";
  public static final String VERSION = "17.05";
  public static final String USERAGENT = NAME + "/" + VERSION + " (https://github.com/yarl/pattypan)";

  public static final Map<String, String> SETTINGS = new HashMap<>();
  public static final Map<String, Template> TEMPLATES = new HashMap<>();

  static {
    SETTINGS.put("path", "");
    SETTINGS.put("user", "");
    SETTINGS.put("windowWidth", "800");
    SETTINGS.put("windowHeight", "600");
    SETTINGS.put("exifDate", "");

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
                    + "<#list categories ? split(\";\") as category>\n"
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
                    + "<#list categories ? split(\";\") as category>\n"
                    + "[[Category:${category?trim}]]\n"
                    + "</#list>\n"
                    + "<#else>{{subst:unc}}\n"
                    + "</#if>"
            )
    );
    TEMPLATES.put(
            "Book",
            new Template("Book",
                    new TemplateField[]{
                      new TemplateField("author", "Author"),
                      new TemplateField("translator", "Translator"),
                      new TemplateField("editor", "Editor"),
                      new TemplateField("illustrator", "Illustrator"),
                      new TemplateField("title", "Title"),
                      new TemplateField("subtitle", "Subtitle"),
                      new TemplateField("series_title", "Series title"),
                      new TemplateField("volume", "Volume"),
                      new TemplateField("edition", "Edition"),
                      new TemplateField("publisher", "Publisher"),
                      new TemplateField("printer", "Printer"),
                      new TemplateField("date", "Date"),
                      new TemplateField("city", "City"),
                      new TemplateField("language", "Language"),
                      new TemplateField("description", "Description"),
                      new TemplateField("source", "Source"),
                      new TemplateField("permission", "Permission"),
                      new TemplateField("image", "Image"),
                      new TemplateField("image_page", "Image page"),
                      new TemplateField("pageoverview", "Page overview"),
                      new TemplateField("wikisource", "Wikisource"),
                      new TemplateField("homecat", "Home category"),
                      new TemplateField("other_versions", "Other versions"),
                      new TemplateField("isbn", "ISBN"),
                      new TemplateField("lccn", "LCCN"),
                      new TemplateField("oclc", "OCLC"),
                      new TemplateField("references", "References"),
                      new TemplateField("linkback", "Linkback"),
                      new TemplateField("wikidata", "Wikidata"),
                      new TemplateField("license", "License"),
                      new TemplateField("partnership", "Partnership")
                    }, "=={{int:filedesc}}==\n"
                    + "{{Book\n"
                    + " |Author = ${author}\n"
                    + " |Translator = ${translator}\n"
                    + " |Editor = ${editor}\n"
                    + " |Illustrator = ${illustrator}\n"
                    + " |Title = ${title}\n"
                    + " |Subtitle = ${subtitle}\n"
                    + " |Series title = ${series_title}\n"
                    + " |Volume = ${volume}\n"
                    + " |Edition = ${edition}\n"
                    + " |Publisher = ${publisher}\n"
                    + " |Printer = ${printer}\n"
                    + " |Date = ${date}\n"
                    + " |City = ${city}\n"
                    + " |Language = ${language}\n"
                    + " |Description = ${description}\n"
                    + " |Source = ${source}\n"
                    + " |Permission = ${permission}\n"
                    + " |Image = ${image}\n"
                    + " |Image page = ${image_page}\n"
                    + " |Pageoverview = ${pageoverview}\n"
                    + " |Wikisource = ${wikisource}\n"
                    + " |Homecat = ${homecat}\n"
                    + " |Other_versions = ${other_versions}\n"
                    + " |ISBN = ${isbn}\n"
                    + " |LCCN = ${lccn}\n"
                    + " |OCLC = ${oclc}\n"
                    + " |References = ${references}\n"
                    + " |Linkback = ${linkback}\n"
                    + " |Wikidata = ${wikidata}\n"
                    + "}}\n\n"
                    + "=={{int:license-header}}==\n"
                    + "${license}${partnership}"
                    + "\n\n"
                    + "<#if categories ? has_content>\n"
                    + "<#list categories ? split(\";\") as category>\n"
                    + "[[Category:${category?trim}]]\n"
                    + "</#list>\n"
                    + "<#else>{{subst:unc}}\n"
                    + "</#if>"
            )
    );
    TEMPLATES.put("Musical work",
            new Template("Musical work",
                    new TemplateField[]{
                      new TemplateField("composer", "Composer"),
                      new TemplateField("lyrics_writer", "Lyrics writer"),
                      new TemplateField("performer", "Performer"),
                      new TemplateField("title", "Title"),
                      new TemplateField("description", "Description"),
                      new TemplateField("composition_date", "Composition date"),
                      new TemplateField("performance_date", "Performance date"),
                      new TemplateField("notes", "Notes"),
                      new TemplateField("record_id", "Record ID"),
                      new TemplateField("image", "Image"),
                      new TemplateField("references", "References"),
                      new TemplateField("source", "Source"),
                      new TemplateField("permission", "Permission"),
                      new TemplateField("other_versions", "Other versions"),
                      new TemplateField("license", "License"),
                      new TemplateField("partnership", "Partnership")
                    }, "=={{int:filedesc}}==\n"
                    + "{{Musical work\n"
                    + " |composer = ${composer}\n"
                    + " |lyrics_writer = ${lyrics_writer}\n"
                    + " |performer = ${performer}\n"
                    + " |title = ${title}\n"
                    + " |description = ${description}\n"
                    + " |composition_date = ${composition_date}\n"
                    + " |performance_date = ${performance_date}\n"
                    + " |notes = ${notes}\n"
                    + " |record_ID = ${record_id}\n"
                    + " |image = ${image}\n"
                    + " |references = ${references}\n"
                    + " |source = ${source}\n"
                    + " |permission = ${permission}\n"
                    + " |other_versions = ${other_versions}\n"
                    + "}}\n\n"
                    + "=={{int:license-header}}==\n"
                    + "${license}${partnership}"
                    + "\n\n"
                    + "<#if categories ? has_content>\n"
                    + "<#list categories ? split(\";\") as category>\n"
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
                    + "<#list categories ? split(\";\") as category>\n"
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

  /**
   * Gets directory for local user settings
   *
   * @source http://stackoverflow.com/a/16660314/1418878
   * @return path to local Pattypan directory
   */
  private static String getPropertiesDirectiry() {
    String dir;
    String OS = (System.getProperty("os.name")).toUpperCase();

    if (OS.contains("WIN")) {
      dir = System.getenv("AppData") + "/Pattypan";
    } else if (OS.contains("NUX")) {
      dir = System.getProperty("user.home") + "/.pattypan";
    } else {
      dir = System.getProperty("user.home");
      dir += "/Library/Application Support/Pattypan";
    }
    return dir;
  }

  public static String getSetting(String key) {
    return SETTINGS.get(key) != null ? SETTINGS.get(key) : "";
  }

  public static int getSettingInt(String key) {
    return Integer.parseInt(SETTINGS.get(key));
  }

  /**
   * Reads user properties from config.properties file
   */
  public static void readProperties() {
    Properties prop = new Properties();
    InputStream input = null;

    try {
      File f = new File(getPropertiesDirectiry() + "/config.properties");
      input = new FileInputStream(f);
      prop.load(input);

      prop.forEach((key, value) -> {
        if (!value.toString().isEmpty()) {
          SETTINGS.put(key.toString(), value.toString());
        }
      });

    } catch (FileNotFoundException ex) {
      Session.LOGGER.log(Level.INFO, "Settings file not found, use default");
    } catch (IOException ex) {
      Session.LOGGER.log(Level.SEVERE, null, ex);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException ex) {
          Session.LOGGER.log(Level.SEVERE, null, ex);
        }
      }
    }
  }

  /**
   * Saves user properties to config.properties file
   */
  public static void saveProperties() {
    Properties prop = new Properties();
    OutputStream output = null;

    try {
      new File(getPropertiesDirectiry()).mkdirs();
      File f = new File(getPropertiesDirectiry() + "/config.properties");
      output = new FileOutputStream(f);
      SETTINGS.forEach((key, value) -> {
        prop.setProperty(key, value);
      });
      prop.store(output, null);
    } catch (IOException ex) {
      Session.LOGGER.log(Level.SEVERE, null, ex);
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException ex) {
          Session.LOGGER.log(Level.SEVERE, null, ex);
        }
      }
    }
  }

  public static void setSetting(String key, String value) {
    SETTINGS.put(key, value);
  }
}
