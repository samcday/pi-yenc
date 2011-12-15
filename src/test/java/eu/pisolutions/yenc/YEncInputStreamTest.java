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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.testng.annotations.Test;

import eu.pisolutions.io.Streams;
import eu.pisolutions.nio.charset.ByteCharset;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class YEncInputStreamTest
extends Object {
    public YEncInputStreamTest() {
        super();
    }

    @Test
    public void testNotYEnc()
    throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(ByteCharset.fastEncode("This is not yEnc encoded\r\nabc123\r\n"));

        YEncInputStream yEncIn = new YEncInputStream(bais);
        assertNull(yEncIn.getNextEntry());

        bais.reset();
        yEncIn = new YEncInputStream(bais);
        assertEquals(yEncIn.read(), -1);
    }

    @Test
    public void testSinglePart()
    throws IOException {
        final YEncInputStream yEncIn = new YEncInputStream(YEncInputStreamTest.class.getResourceAsStream("single-part.yenc"));
        final YEncEntry entry = yEncIn.getNextEntry();
        assertNotNull(entry);
        assertEquals(entry.getName(), "testfile.txt");
        assertEquals(entry.getSize(), 584);
        assertEquals(entry.getLineLength(), 128);
        assertEquals(entry.getPartIndex(), 0);
        assertEquals(entry.getPartCount(), 1);
        assertEquals(entry.getPartBegin(), 0);
        assertEquals(entry.getPartEnd(), entry.getSize() - 1);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Streams.copy(yEncIn, baos);
        assertEquals(baos.size(), entry.getSize());
        assertEquals(baos.toByteArray(), Streams.readAll(YEncInputStreamTest.class.getResourceAsStream("testfile.txt")));
    }

    @Test
    public void testMultipart()
    throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        YEncInputStream yEncIn = new YEncInputStream(YEncInputStreamTest.class.getResourceAsStream("multipart-1.yenc"));
        YEncEntry entry = yEncIn.getNextEntry();
        assertNotNull(entry);
        assertEquals(entry.getName(), "joystick.jpg");
        assertEquals(entry.getSize(), 19338);
        assertEquals(entry.getLineLength(), 128);
        assertEquals(entry.getPartIndex(), 0);
        assertEquals(entry.getPartCount(), 0);
        assertEquals(entry.getPartBegin(), 0);
        assertEquals(entry.getPartEnd(), 11249);

        Streams.copy(yEncIn, baos);

        yEncIn = new YEncInputStream(YEncInputStreamTest.class.getResourceAsStream("multipart-2.yenc"));
        entry = yEncIn.getNextEntry();
        assertNotNull(entry);
        assertEquals(entry.getName(), "joystick.jpg");
        assertEquals(entry.getSize(), 19338);
        assertEquals(entry.getLineLength(), 128);
        assertEquals(entry.getPartIndex(), 1);
        assertEquals(entry.getPartCount(), 0);
        assertEquals(entry.getPartBegin(), 11250);
        assertEquals(entry.getPartEnd(), 19337);

        Streams.copy(yEncIn, baos);

        assertEquals(baos.toByteArray(), Streams.readAll(YEncInputStreamTest.class.getResourceAsStream("joystick.jpg")));
    }
}
