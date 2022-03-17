/*
 * EBViewer, a dictionary viewer application.
 * Copyright (C) 2022 Hiroshi Miura.
 * Copyright (C) 2000-2006 Keith Godfrey, Maxym Mykhalchuk, and Henry Pijffers
 *              2007 Zoltan Bartko
 *              2008-2009 Didier Briel
 *              2010 Wildrich Fourie, Antonio Vilei, Didier Briel
 *              2011 John Moran, Didier Briel
 *              2012 Martin Fleurke, Wildrich Fourie, Didier Briel, Thomas Cordonnier,
 *                   Aaron Madlon-Kay
 *              2013 Aaron Madlon-Kay, Zoltan Bartko
 *              2014 Piotr Kulik, Aaron Madlon-Kay
 *              2015 Aaron Madlon-Kay, Yu Tang, Didier Briel, Hiroshi Miura
 *              2016 Aaron Madlon-Kay*
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

import java.io.IOException;

public interface IPreferences {
    String getPreference(String key);

    boolean existsPreference(String key);

    boolean isPreference(String key);

    boolean isPreferenceDefault(String key, boolean defaultValue);

    String getPreferenceDefault(String key, String value);

    <T extends Enum<T>> T getPreferenceEnumDefault(String key, T defaultValue);

    int getPreferenceDefault(String key, int defaultValue);

    /**
     * Return the old value, or null if not set
     */
    Object setPreference(String key, Object value) throws Exception;

    void save() throws IOException;
}
