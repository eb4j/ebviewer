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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class Preferences {

    static final Logger LOG = LoggerFactory.getLogger(Preferences.class.getName());

    /**
     * Configuration directory on Windows platforms
     */
    private static final String WINDOWS_CONFIG_DIR = "\\ebviewer\\";

    /**
     * Configuration directory on UNIX platforms
     */
    private static final String UNIX_CONFIG_DIR = "/.config/ebviewer/";

    /**
     * Configuration directory on Mac OS X
     */
    private static final String OSX_CONFIG_DIR = "/Library/Preferences/ebviewer/";

    public static final String FILE_PREFERENCES = "ebviewer.prefs";

    public static final String APPEARANCE_CONDENSED_VIEW = "appearance.condensed.view";
    public static final String APPEARANCE_FONT_NAME = "appearance.font.name";
    public static final String APPEARANCE_FONT_DEFAULT = "dialog";
    public static final String APPEARANCE_FONT_SIZE = "appearance.font.size";
    public static final int APPEARANCE_FONT_SIZE_DEFAULT = 12;

    /**
     * Contains the location of the directory containing the configuration
     * files.
     */
    private static String configDir = null;

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

    public static int getPreferenceDefault(final String key, final int defaultValue) {
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
        File saveFile = new File(getConfigDir(), Preferences.FILE_PREFERENCES);
        preferences = new PreferencesImpl(loadFile, saveFile);
    }

    private static IPreferences preferences;
    private static volatile boolean didInit = false;


    /**
     * Returns the location of the configuration directory, depending on the
     * user's platform. Also creates the configuration directory, if necessary.
     * If any problems occur while the location of the configuration directory
     * is being determined, an empty string will be returned, resulting in the
     * current working directory being used.
     *
     * <ul><li>Windows XP: &lt;Documents and Settings>\&lt;User name>\Application Data\OmegaT
     * <li>Windows Vista: User\&lt;User name>\AppData\Roaming
     * <li>Linux: ~/.config/ebviewer
     * <li>Solaris/SunOS: ~/.ebviewer
     * <li>FreeBSD: ~/.ebviewer
     * <li>Mac OS X: ~/Library/Preferences/ebviewer
     * <li>Other: User home directory
     * </ul>
     *
     * @return The full path of the directory containing the configuration files, including trailing path separator.
     */
    public static String getConfigDir() {
        // if the configuration directory has already been determined, return it
        if (configDir != null) {
            return configDir;
        }

        Platform.OsType os = Platform.getOsType(); // name of operating system
        String home; // user home directory

        // get os and user home properties
        try {
            // get the user's home directory
            home = System.getProperty("user.home");
        } catch (SecurityException e) {
            // access to the os/user home properties is restricted,
            // the location of the config dir cannot be determined,
            // set the config dir to the current working dir
            configDir = new File(".").getAbsolutePath() + File.separator;

            // log the exception, only do this after the config dir
            // has been set to the current working dir, otherwise
            // the log method will probably fail
            LOG.error(e.toString());

            return configDir;
        }

        // if os or user home is null or empty, we cannot reliably determine
        // the config dir, so we use the current working dir (= empty string)
        if (os == null || StringUtils.isEmpty(home)) {
            // set the config dir to the current working dir
            configDir = new File(".").getAbsolutePath() + File.separator;
            return configDir;
        }

        // check for Windows versions
        if (Platform.isWindows()) {
            String appData = null;
            File appDataFile = new File(home, "AppData\\Roaming");
            if (appDataFile.exists()) {
                appData = appDataFile.getAbsolutePath();
            } else {
                appDataFile = new File(home, "Application Data");
                if (appDataFile.exists()) {
                    appData = appDataFile.getAbsolutePath();
                }
            }

            if (!StringUtils.isEmpty(appData)) {
                configDir = appData + WINDOWS_CONFIG_DIR;
            } else {
                configDir = home + WINDOWS_CONFIG_DIR;
            }
        } else if (Platform.isLinux() || os == Platform.OsType.OTHER) {
            configDir = home + UNIX_CONFIG_DIR;
        } else if (Platform.isMacOSX()) {
            configDir = home + OSX_CONFIG_DIR;
        } else {
            configDir = home + File.separator;
        }

        // create the path to the configuration dir, if necessary
        if (!configDir.isEmpty()) {
            try {
                // check if the dir exists
                File dir = new File(configDir);
                if (!dir.exists()) {
                    // create the dir
                    boolean created = dir.mkdirs();

                    // if the dir could not be created,
                    // set the config dir to the current working dir
                    if (!created) {
                        configDir = new File(".").getAbsolutePath() + File.separator;
                    }
                }
            } catch (SecurityException e) {
                // the system doesn't want us to write where we want to write
                // reset the config dir to the current working dir
                configDir = new File(".").getAbsolutePath() + File.separator;

                // log the exception, but only after the config dir has been
                // reset
                LOG.error(e.toString());
            }
        }

        // we should have a correct, existing config dir now
        return configDir;
    }

    private static File getPreferencesFile() {
        File prefsFile = new File(getConfigDir(), FILE_PREFERENCES);
        if (prefsFile.exists()) {
            return prefsFile;
        }
        return null;
    }

}
