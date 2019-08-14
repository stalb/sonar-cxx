/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2010-2019 SonarOpenCommunity
 * http://github.com/SonarOpenCommunity/sonar-cxx
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.cxx.sensors.compiler;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.cxx.CxxLanguage;
import org.sonar.cxx.sensors.utils.CxxIssuesReportSensor;
import org.sonar.cxx.utils.CxxReportIssue;

/**
 * base class for compiler issues
 */
public abstract class CxxCompilerSensor extends CxxIssuesReportSensor {

  private static final Logger LOG = Loggers.get(CxxCompilerSensor.class);

  protected CxxCompilerSensor(CxxLanguage language, String propertiesKeyPathToReports, String ruleRepositoryKey) {
    super(language, propertiesKeyPathToReports, ruleRepositoryKey);
  }

  @Override
  protected void processReport(final SensorContext context, File report) throws javax.xml.stream.XMLStreamException {

    final String reportCharset = getCharset(context);
    final String reportRegEx = getRegex(context);

    if (reportRegEx.isEmpty()) {
      LOG.error("processReport terminated because of empty custom regular expression");
      return;
    }

    LOG.info("Parsing '{}' initialized with report '{}', Charset= '{}'", getCompilerKey(), report, reportCharset);

    try (Scanner scanner = new Scanner(report, reportCharset)) {
      Pattern pattern = Pattern.compile(reportRegEx);
      LOG.info("Using pattern : '{}'", pattern);

      CxxReportIssue issue = null;
      while (scanner.hasNextLine()) {
        Matcher matcher = pattern.matcher(scanner.nextLine());
        if (matcher.find()) {
          String filename = alignFilename(matcher.group("file"));
          String line = alignLine(matcher.group("line"));
          String id = alignId(matcher.group("id"));
          String severity = "warning";
          try {
        	  severity = alignSeverity(matcher.group("severity"));
          } catch(IllegalArgumentException e) {
        	  LOG.error("Scanner-matches Exception: {} - missing \"severity\" group in regex '{}'", e, reportRegEx);
          }
          String msg = alignMessage(matcher.group("message"));
          if (isInputValid(filename, line, id, msg, severity)) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("Scanner-matches file='{}' line='{}' id='{}' msg={}", filename, line, id, msg);
            }
            if (issue != null) {
                saveUniqueViolation(context, issue);
            }
            issue = new CxxReportIssue(id, filename, line, msg);
          } else if ((issue != null) && isInputValidNote(filename, line, id, msg, severity)) {
        	  issue.addFlowElement(filename, line, msg);
          } else {
            LOG.warn("Invalid compiler message: '{}''{}'", id, msg);
          }
        }
      }
      if (issue != null) {
          saveUniqueViolation(context, issue);
      }
    } catch (java.io.FileNotFoundException | java.lang.IllegalArgumentException | java.lang.IllegalStateException e) {
      LOG.error("processReport Exception: {} - not processed '{}'", report, e);
    }
  }

  /**
   * Unique string to identify the compiler
   *
   * @return
   */
  protected abstract String getCompilerKey();

  /**
   * Character set of the report
   *
   * @param context current context
   * @return
   */
  protected abstract String getCharset(final SensorContext context);

  /**
   * Regular expression to parse the report
   *
   * @param context current context
   * @return
   */
  protected abstract String getRegex(final SensorContext context);

  /**
   * Derived classes can overload this method
   *
   * @param filename
   * @param line
   * @param id
   * @param msg
   * @param severity
   * @return true, if valid
   */
  protected boolean isInputValid(String filename, String line, String id, String msg, String severity) {
    return !filename.isEmpty() && !line.isEmpty() && !id.isEmpty() && !msg.isEmpty() && ("warning".equals(severity) || "error".equals(severity));
  }

  /**
   * Derived classes can overload this method
   *
   * @param filename
   * @param line
   * @param id
   * @param msg
   * @param severity
   * @return true, if valid
   */
  protected boolean isInputValidNote(String filename, String line, String id, String msg, String severity) {
    return !filename.isEmpty() && !line.isEmpty() && !msg.isEmpty() && "note".equals(severity);
  }

  /**
   * Derived classes can overload this method to align filename
   *
   * @param filename
   * @return
   */
  protected String alignFilename(String filename) {
    return filename;
  }

  /**
   * Derived classes can overload this method to align line number
   *
   * @param line
   * @return
   */
  protected String alignLine(String line) {
    return line;
  }

  /**
   * Derived classes can overload this method to align message id
   *
   * @param id
   * @return
   */
  protected String alignId(String id) {
    return id;
  }

  /**
   * Derived classes can overload this method to align message
   *
   * @param message
   * @return
   */
  protected String alignMessage(String message) {
    return message;
  }
  
  /**
   * Derived classes can overload this method to align severity
   *
   * @param severity
   * @return
   */
  protected String alignSeverity(String severity) {
    return severity;
  }
}
