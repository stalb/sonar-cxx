/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2011 Waleri Enns and CONTACT Software GmbH
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.cxx.checks;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class CheckList {

  public static final String REPOSITORY_KEY = "cxx";

  public static final String DEFAULT_PROFILE = "Sonar way";

  private CheckList() {
  }

  public static List<Class> getChecks() {
    return ImmutableList.<Class> of(
        CommentedCodeCheck.class,
        CommentRegularExpressionCheck.class,
        FileComplexityCheck.class,
        FileHeaderCheck.class,
        FixmeTagPresenceCheck.class,
        FunctionComplexityCheck.class,
        MagicNumberCheck.class,
        MissingNewLineAtEndOfFileCheck.class,
        NoSonarCheck.class,
        ParsingErrorCheck.class,
        StringLiteralDuplicatedCheck.class,
        TabCharacterCheck.class,
        TodoTagPresenceCheck.class,
        TooLongLineCheck.class,
        TooManyLinesOfCodeInFileCheck.class,
        TooManyStatementsPerLineCheck.class,
        XPathCheck.class);
  }

}
