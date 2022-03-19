/*
 * EBViewer, a dictionary viewer application.
 * Copyright (C) 2022 Hiroshi Miura.
 *               2007 - Zoltan Bartko
 *               2011 Alex Buloichik*
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class Platform {

    static final Logger LOG = LoggerFactory.getLogger(Platform.class.getName());
    /**
     * Configuration directory on Windows platforms
     */
    private static final String WINDOWS_CONFIG_DIR = "\\ebviewer\\";
    private static final String WINDOWS_CACHE_DIR = "\\wbviewer\\";
    /**
     * Configuration directory on UNIX platforms
     */
    private static final String UNIX_CONFIG_DIR = "/.config/ebviewer/";
    private static final String UNIX_CACHE_DIR = "/.cache/ebviewer/";
    /**
     * Configuration directory on Mac OS X
     */
    private static final String OSX_CONFIG_DIR = "/Library/Preferences/ebviewer/";
    private static final String OSX_CACHE_DIR = "/Library/Caches/ebviewer/";
    /**
     * Contains the location of the directory containing the configuration
     * files.
     */
    private static String configDir = null;
    /**
     * Contains the location of the directory containing caches.
     */
    private static String cacheDir = null;

    private final static String NOCACHE = "-no-cache-";

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

        OsType os = getOsType(); // name of operating system
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

        if (isWindows()) {
            String appData = new File(home, "AppData\\Roaming").getAbsolutePath();
            if (!StringUtils.isEmpty(appData)) {
                configDir = appData + WINDOWS_CONFIG_DIR;
            } else {
                configDir = home + WINDOWS_CONFIG_DIR;
            }
        } else if (isLinux() || os == OsType.OTHER) {
            configDir = home + UNIX_CONFIG_DIR;
        } else if (isMacOSX()) {
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

    public static String getCacheDir() {
        // if the configuration directory has already been determined, return it
        if (cacheDir != null) {
            if (cacheDir.equals(NOCACHE)) {
                return null;
            }
            return cacheDir;
        }

        String home;
        try {
            home = System.getProperty("user.home");
        } catch (SecurityException e) {
            // access to the os/user home properties is restricted,
            cacheDir = NOCACHE;
            return cacheDir;
        }
        if (isWindows()) {
            String appData = new File(home, "AppData\\LocalLow").getAbsolutePath();
            if (!StringUtils.isEmpty(appData)) {
                cacheDir = appData + WINDOWS_CACHE_DIR;
            } else {
                cacheDir = home + WINDOWS_CACHE_DIR;
            }
        } else if (isLinux() || getOsType() == OsType.OTHER) {
            cacheDir = home + UNIX_CACHE_DIR;
        } else if (isMacOSX()) {
            cacheDir = home + OSX_CACHE_DIR;
        } else {
            cacheDir = NOCACHE;
        }

        if (!cacheDir.equals(NOCACHE)) {
            try {
                File dir = new File(cacheDir);
                if (!dir.exists()) {
                    boolean created = dir.mkdirs();
                    if (!created) {
                        cacheDir = NOCACHE;
                    }
                }
            } catch (SecurityException e) {
                cacheDir = NOCACHE;
            }
        }
        return cacheDir;
    }

    public enum OsType {
        // os.arch=amd64, os.name=Linux, os.version=3.0.0-12-generic
        LINUX64,
        // os.arch=i386, os.name=Linux, os.version=3.0.0-12-generic
        LINUX32,
        // os.arch=x86_64, os.name=Mac OS X, os.version=10.6.8
        MAC64,
        // os.arch=i386, os.name=Mac OS X, os.version=10.6.8
        MAC32,
        // os.arch=amd64, os.name=Windows 7, os.version=6.1
        WIN64,
        // os.arch=x86, os.name=Windows 7, os.version=6.1
        WIN32,
        // unknown system
        OTHER
    }

    private static OsType osType = OsType.OTHER;

    static {
        String osName = System.getProperty("os.name");
        if (osName != null && System.getProperty("os.arch") != null) {
            boolean is64 = is64Bit();
            if (osName.startsWith("Linux")) {
                osType = is64 ? OsType.LINUX64 : OsType.LINUX32;
            } else if (osName.contains("OS X")) {
                osType = is64 ? OsType.MAC64 : OsType.MAC32;
            } else if (osName.startsWith("Windows")) {
                osType = is64 ? OsType.WIN64 : OsType.WIN32;
            }
        }
    }

    private Platform() {
    }

    public static OsType getOsType() {
        return osType;
    }

    /**
     * Returns true if running on Windoows.
     */
    public static boolean isWindows() {
        OsType os = getOsType();
        return os == OsType.WIN32 || os == OsType.WIN64;
    }

    /**
     * Returns true if running on Mac OS X.
     */
    public static boolean isMacOSX() {
        OsType os = getOsType();
        return os == OsType.MAC32 || os == OsType.MAC64;
    }

    /**
     * Returns true if running on Linux.
     */
    public static boolean isLinux() {
        OsType os = getOsType();
        return os == OsType.LINUX32 || os == OsType.LINUX64;
    }

    /**
     * Returns true if the JVM (NOT the OS) is 64-bit.
     */
    public static boolean is64Bit() {
        String osArch = System.getProperty("os.arch");
        if (osArch != null) {
            return osArch.contains("64");
        }
        return false;
    }
}
