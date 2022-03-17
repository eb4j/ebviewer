/*
 * EBViewer, a dictionary viewer application.
 * Copyright (C) 2022 Hiroshi Miura.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.eb4j.ebview.utils;

import java.util.ResourceBundle;

public final class LStrings {

    private LStrings() {
    }

    private static final ResourceBundle bundle = ResourceBundle.getBundle("Bundle");

    /**
     * Returns a localized String for a key.
     * @param key query string.
     * @return localized string.
     */
    public static String getString(final String key) {
        return bundle.getString(key);
    }

}
