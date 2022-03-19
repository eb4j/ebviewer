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
 *              2016 Aaron Madlon-Kay
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

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;

public final class Preferences {

    public static final String FILE_PREFERENCES = "ebviewer.prefs";

    /** Private constructor, because this file is singleton */
    private Preferences() {
    }

    public static String getPreference(final String key) {
        return preferences.getPreference(key);
    }

    public static boolean existsPreference(final String key) {
        return preferences.existsPreference(key);
    }

    public static boolean isPreference(final String key) {
        return preferences.isPreference(key);
    }

    public static boolean isPreferenceDefault(final String key, final boolean defaultValue) {
        return preferences.isPreferenceDefault(key, defaultValue);
    }

    public static String getPreferenceDefault(final String key, final String defaultValue) {
        return preferences.getPreferenceDefault(key, defaultValue);
    }

    public <T extends Enum<T>> T getPreferenceEnumDefault(final String key, final T defaultValue) {
        return preferences.getPreferenceEnumDefault(key, defaultValue);
    }

    public int getPreferenceDefault(final String key, final int defaultValue) {
        return preferences.getPreferenceDefault(key, defaultValue);
    }

    public static Object setPreference(final String key, final Object value) {
        try {
            return preferences.setPreference(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void save() {
        try {
            preferences.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void init() throws JsonProcessingException {
        if (didInit) {
            return;
        }
        didInit = true;
        File loadFile = getPreferencesFile();
        File saveFile = new File(Platform.getConfigDir(), Preferences.FILE_PREFERENCES);
        preferences = new PreferencesImpl(loadFile, saveFile);
    }

    private static IPreferences preferences;
    private static volatile boolean didInit = false;

    private static File getPreferencesFile() {
        File prefsFile = new File(Platform.getConfigDir(), FILE_PREFERENCES);
        if (prefsFile.exists()) {
            return prefsFile;
        }
        return null;
    }

}
