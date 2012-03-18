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

/**
 * Entry in a yEnc stream.
 *
 * @author Laurent Pireyn
 */
public final class YEncEntry
extends Object
implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int size;
    private int lineLength;
    private int partIndex;
    private int partCount;
    private int partBegin;
    private int partEnd;

    public YEncEntry() {
        super();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getLineLength() {
        return this.lineLength;
    }

    public void setLineLength(int lineLength) {
        this.lineLength = lineLength;
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

    public int getPartBegin() {
        return this.partBegin;
    }

    public void setPartBegin(int partBegin) {
        this.partBegin = partBegin;
    }

    public int getPartEnd() {
        return this.partEnd;
    }

    public void setPartEnd(int partEnd) {
        this.partEnd = partEnd;
    }
}
