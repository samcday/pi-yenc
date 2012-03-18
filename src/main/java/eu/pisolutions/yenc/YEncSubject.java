/*
 * This file is part of Pi yEnc.
 *
 * Copyright (C) 2011 Pi Solutions <info@pisolutions.eu>
 *
 * Pi yEnc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pi yEnc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pi yEnc.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.pisolutions.yenc;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.pisolutions.lang.Strings;
import eu.pisolutions.regex.CharClass;
import eu.pisolutions.regex.Regex;

public final class YEncSubject
extends Object
implements Serializable {
    private static final Pattern PATTERN = YEncSubject.createPattern();
    private static final long serialVersionUID = 1L;

    public static YEncSubject parseSubject(String string) {
        if (string == null) {
            return null;
        }

        final Matcher matcher = YEncSubject.PATTERN.matcher(string);
        if (!matcher.matches()) {
            return null;
        }

        final YEncSubject subject = new YEncSubject();
        subject.comment1 = matcher.group(1);
        subject.fileName = matcher.group(2);
        final String part = matcher.group(3);
        if (part != null) {
            subject.partIndex = Integer.parseInt(part) - 1;
            subject.partCount = Integer.parseInt(matcher.group(4));
        }
        final String size = matcher.group(5);
        if (!Strings.isEmpty(size)) {
            subject.size = Integer.parseInt(size);
        }
        subject.comment2 = matcher.group(6);
        return subject;
    }

    private static Pattern createPattern() {
        final Regex space = Regex.literal(" ");
        final Regex spaces = space.zeroOrMore();
        final Regex quote = Regex.literal("\"");
        final Regex notQuote = CharClass.oneOf("\"", true);
        final Regex digitGroup = CharClass.DIGIT.atLeastOnce().group();
        return Regex.sequence(
            Regex.LINE_BEGIN,
            spaces,
            Regex.sequence(
                notQuote.zeroOrMore().group(), // Comment 1
                space
            ).optional(),
            quote,
            notQuote.atLeastOnce().group(), // File name
            quote,
            Regex.literal(" yEnc"),
            spaces,
            Regex.sequence(
                Regex.literal("("),
                digitGroup, // Part index
                Regex.literal("/"),
                digitGroup, // Part count
                Regex.literal(")"),
                spaces
            ).optional(),
            Regex.sequence(
                digitGroup, // Size
                spaces
            ).optional(),
            Regex.sequence(
                CharClass.ANY.atLeastOnce().group(), // Comment 2
                spaces
            ).optional(),
            Regex.LINE_END
        ).toPattern();
    }

    private String fileName;
    private int size;
    private int partIndex;
    private int partCount;
    private String comment1;
    private String comment2;

    public YEncSubject() {
        super();
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPartIndex() {
        return this.partIndex;
    }

    public void setPartIndex(int partIndex) {
        this.partIndex = partIndex;
    }

    public int getPartCount() {
        return this.partCount;
    }

    public void setPartCount(int partCount) {
        this.partCount = partCount;
    }

    public String getComment1() {
        return this.comment1;
    }

    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    public String getComment2() {
        return this.comment2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }
}
