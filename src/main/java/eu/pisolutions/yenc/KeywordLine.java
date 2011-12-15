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

import java.util.HashMap;
import java.util.Map;

import eu.pisolutions.lang.Strings;

final class KeywordLine
extends Object {
    private static YEncException createException(String name, String value, Throwable cause) {
        return new YEncException("Invalid value '" + value + "' for parameter '" + name + "' in keyword line", cause);
    }

    private final String name;
    private final Map<String, String> parameters = new HashMap<String, String>();

    public KeywordLine(String name) {
        super();

        assert !Strings.isBlank(name);

        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getParameterAsString(String name) {
        return this.parameters.get(name);
    }

    public Integer getParameterAsInt(String name)
    throws YEncException {
        final String value = this.parameters.get(name);
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        }
        catch (NumberFormatException exception) {
            throw KeywordLine.createException(name, value, exception);
        }
    }

    public Long getParameterAsLongHex(String name)
    throws YEncException {
        final String value = this.parameters.get(name);
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(value, 16);
        }
        catch (NumberFormatException exception) {
            throw KeywordLine.createException(name, value, exception);
        }
    }

    public void addParameter(String name, String value) {
        this.parameters.put(name, value);
    }
}
