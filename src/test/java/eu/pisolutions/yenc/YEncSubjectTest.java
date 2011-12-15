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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class YEncSubjectTest
extends Object {
    public YEncSubjectTest() {
        super();
    }

    @Test(dataProvider = "invalidSubjectProvider")
    public void testParseInvalidSubject(String subject) {
        assertNull(YEncSubject.parseSubject(subject));
    }

    @Test(dataProvider = "validSubjectProvider")
    public void testParseValidSubject(String string, String comment1, String fileName, int partIndex, int partCount, int size, String comment2) {
        final YEncSubject subject = YEncSubject.parseSubject(string);
        assertNotNull(subject);
        assertEquals(subject.getComment1(), comment1);
        assertEquals(subject.getFileName(), fileName);
        assertEquals(subject.getPartIndex(), partIndex);
        assertEquals(subject.getPartCount(), partCount);
        assertEquals(subject.getSize(), size);
        assertEquals(subject.getComment2(), comment2);
    }

    @DataProvider(name = "invalidSubjectProvider")
    public Object[][] provideInvalidSubjects() {
        return new Object[][] {
            {null},
            {""},
            {"abc"},
            {"yEnc"},
            {"abc yenc"},
            {"abc yEnc"},
            {"\"abc\""},
            {"\"abc\" yenc"},
        };
    }

    @DataProvider(name = "validSubjectProvider")
    public Object[][] provideValidSubjects() {
        return new Object[][] {
            {"\"abc 123\" yEnc", null, "abc 123", 0, 0, 0, null},
            {"Comment 1 \"abc 123\" yEnc", "Comment 1", "abc 123", 0, 0, 0, null},
            {"\"abc 123\" yEnc Comment 2", null, "abc 123", 0, 0, 0, "Comment 2"},
            {"Comment 1 \"abc 123\" yEnc Comment 2", "Comment 1", "abc 123", 0, 0, 0, "Comment 2"},
            {"\"abc 123\" yEnc 123", null, "abc 123", 0, 0, 123, null},
            {"\"abc 123\" yEnc 123 Comment 2", null, "abc 123", 0, 0, 123, "Comment 2"},
            {"\"abc 123\" yEnc (123/456)", null, "abc 123", 122, 456, 0, null},
            {"\"abc 123\" yEnc (123/456) 789", null, "abc 123", 122, 456, 789, null},
            {"\"abc 123\" yEnc 789 (123/456)", null, "abc 123", 0, 0, 789, "(123/456)"},
        };
    }
}
