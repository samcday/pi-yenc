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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

import eu.pisolutions.io.BaseInputStream;
import eu.pisolutions.lang.Validations;
import eu.pisolutions.lop.InputStreamLineReader;
import eu.pisolutions.lop.LineReader;
import eu.pisolutions.nio.charset.ByteCharset;

/**
 * yEnc input stream.
 * <p>
 * A yEnc input stream can contain multiple entries.
 * </p>
 *
 * @author Laurent Pireyn
 * @see YEncEntry
 */
public final class YEncInputStream
extends BaseInputStream {
    private static final byte BYTE_ESCAPE = 0x3d;
    private static final byte BYTE_KEYWORD_LINE = 0x79;

    private final LineReader lineReader;
    private boolean checkSize = true;
    private boolean checkCrc32 = true;
    private YEncEntry entry;
    private int size;
    private CRC32 crc32;
    private byte[] line;
    private int index;

    public YEncInputStream(InputStream in) {
        this(new InputStreamLineReader(in));
    }

    public YEncInputStream(LineReader lineReader) {
        super();

        Validations.notNull(lineReader, "line reader");

        this.lineReader = lineReader;
    }

    public boolean isCheckSize() {
        return this.checkSize;
    }

    public void setCheckSize(boolean checkSize) {
        this.checkSize = checkSize;
    }

    public boolean isCheckCrc32() {
        return this.checkCrc32;
    }

    public void setCheckCrc32(boolean checkCrc32) {
        this.checkCrc32 = checkCrc32;
    }

    public YEncEntry getNextEntry()
    throws IOException {
        this.closeEntry();

        KeywordLine keywordLine;
        do {
            keywordLine = this.readKeywordLine();
            if (keywordLine == null) {
                // EOF
                return null;
            }
        } while (
               !keywordLine.getName().equals("begin")
            && keywordLine.getParameterAsString("name") != null
            && keywordLine.getParameterAsString("size") != null
            && keywordLine.getParameterAsString("line") != null
        );

        this.entry = new YEncEntry();
        this.entry.setName(keywordLine.getParameterAsString("name"));
        this.entry.setSize(keywordLine.getParameterAsInt("size"));
        this.entry.setLineLength(keywordLine.getParameterAsInt("line"));
        final Integer part = keywordLine.getParameterAsInt("part");
        if (part == null) {
            this.entry.setPartIndex(0);
            this.entry.setPartCount(1);
            this.entry.setPartBegin(0);
            this.entry.setPartEnd(this.entry.getSize() - 1);
        }
        else {
            this.entry.setPartIndex(part - 1);
            final Integer total = keywordLine.getParameterAsInt("total");
            if (total != null) {
                this.entry.setPartCount(total);
            }
            keywordLine = this.readKeywordLine();
            if (keywordLine == null || !keywordLine.getName().equals("part")) {
                throw new YEncException("Missing part header keyword line");
            }
            final Integer begin = keywordLine.getParameterAsInt("begin");
            final Integer end = keywordLine.getParameterAsInt("end");
            if (begin == null || end == null) {
                throw new YEncException("Invalid part header keyword line");
            }
            this.entry.setPartBegin(begin - 1);
            this.entry.setPartEnd(end - 1);
        }

        if (this.checkCrc32) {
            this.crc32 = new CRC32();
        }
        this.line = null;

        return this.entry;
    }

    public void closeEntry()
    throws IOException {
        if (this.entry == null) {
            return;
        }

        KeywordLine keywordLine;
        do {
            this.readExpectedLine();
            keywordLine = this.parseKeywordLine();
        } while (keywordLine == null || !keywordLine.getName().equals("end"));
        this.closeEntry(keywordLine);
    }

    @Override
    public int read()
    throws IOException {
        if (this.entry == null) {
            if (this.getNextEntry() == null) {
                // EOF
                return -1;
            }
        }

        while (this.line == null || this.index == this.line.length) {
            this.readExpectedLine();
            this.index = 0;
        }

        final KeywordLine keywordLine = this.parseKeywordLine();
        if (keywordLine != null && keywordLine.getName().equals("end")) {
            // End of entry
            this.closeEntry(keywordLine);
            return -1;
        }

        int b = this.line[this.index++];
        if (b == YEncInputStream.BYTE_ESCAPE) {
            if (this.index == this.line.length) {
                throw new YEncException("EOL after escape byte");
            }
            b = this.line[this.index++] - 64;
        }
        b = b - 42 & 0xff;

        ++this.size;
        if (this.crc32 != null) {
            this.crc32.update(b);
        }

        return b;
    }

    @Override
    public void close()
    throws IOException {
        this.closeEntry();
        this.lineReader.close();
    }

    private boolean readLine()
    throws IOException {
        this.line = this.lineReader.readLine();
        return this.line != null;
    }

    private void readExpectedLine()
    throws IOException {
        if (!this.readLine()) {
            throw new EOFException("EOF before end of entry");
        }
    }

    private KeywordLine readKeywordLine()
    throws IOException {
        KeywordLine keywordLine;
        do {
            if (!this.readLine()) {
                // EOF
                return null;
            }
            keywordLine = this.parseKeywordLine();
        } while (keywordLine == null);
        return keywordLine;
    }

    private KeywordLine parseKeywordLine() {
        if (this.line == null || this.line.length < 3 || this.line[0] != YEncInputStream.BYTE_ESCAPE || this.line[1] != YEncInputStream.BYTE_KEYWORD_LINE) {
            // Not a keyword line
            return null;
        }

        int first = 2;
        int last;
        for (last = first; last < this.line.length && this.line[last] != ' '; ++last) {}
        final KeywordLine keywordLine = new KeywordLine(new String(ByteCharset.fastDecode(this.line, first, last - first)));

        while (last < this.line.length) {
            first = last + 1;
            for (last = first; last < this.line.length && this.line[last] != '='; ++last) {}
            if (last == this.line.length) {
                break;
            }

            final String name = new String(ByteCharset.fastDecode(this.line, first, last - first));

            first = last + 1;
            if (name.equals("name")) {
                for (last = this.line.length; last > first && this.line[last - 1] == ' '; --last) {}
            }
            else {
                for (last = first; last < this.line.length && this.line[last] != ' '; ++last) {}
            }

            final String value = new String(ByteCharset.fastDecode(this.line, first, last - first));
            keywordLine.addParameter(name, value);
        }

        return keywordLine;
    }

    private void closeEntry(KeywordLine keywordLine)
    throws IOException {
        this.entry = null;

        if (this.checkSize) {
            final Integer size = keywordLine.getParameterAsInt("size");
            if (size != null && this.size != size) {
                throw new IOException("Size mismatch");
            }
        }
        this.size = 0;

        if (this.crc32 != null) {
            final Long crc32 = keywordLine.getParameterAsLongHex("crc32");
            if (crc32 != null && this.crc32.getValue() != crc32) {
                throw new IOException("CRC32 mismatch");
            }
            this.crc32 = null;
        }
    }
}
