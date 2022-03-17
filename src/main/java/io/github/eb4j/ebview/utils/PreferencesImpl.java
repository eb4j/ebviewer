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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PreferencesImpl implements Preferences.IPreferences {

    private final ObjectNode preferences;
    private final File preferenceFile;

    private final static String EMPTY_PREFERENCE = "{}";

    public PreferencesImpl(final File loadFile, final File saveFile) throws JsonProcessingException {
        JsonNode rootNode1 = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (loadFile != null) {
                rootNode1 = mapper.readTree(loadFile);
            }
        } catch (IOException ex) {
            makeBackup(loadFile);
        }
        if (rootNode1 == null) {
            rootNode1 = mapper.readTree(EMPTY_PREFERENCE);
        }
        preferences = rootNode1.deepCopy();
        preferenceFile = saveFile;
    }

    @Override
    public String getPreference(String key) {
        JsonNode val = preferences.get(key);
        if (val != null) {
            return val.asText();
        }
        return "";
    }

    @Override
    public boolean existsPreference(String key) {
        return preferences.has(key);
    }

    @Override
    public boolean isPreference(String key) {
        try {
            return preferences.get(key).asBoolean();
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean isPreferenceDefault(String key, boolean defaultValue) {
        if (preferences.has(key)) {
            return defaultValue;
        }
        try {
            return preferences.get(key).asBoolean();
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    @Override
    public String getPreferenceDefault(String key, String value) {
        try {
            return preferences.get(key).asText();
        } catch (Exception ex) {
            return value;
        }
    }

    @Override
    public <T extends Enum<T>> T getPreferenceEnumDefault(String key, T defaultValue) {
        return null;
    }

    @Override
    public int getPreferenceDefault(String key, int defaultValue) {
        try {
            return preferences.get(key).asInt();
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    /**
     * Return the old value, or null if not set
     *
     * @param key
     * @param value
     */
    @Override
    public Object setPreference(String key, Object value) throws Exception {
        Object result = null;
        if (preferences.has(key)) {
            result = preferences.get(key);
        }
        if (value instanceof String) {
            preferences.put(key, (String) value);
        } else if (value instanceof Integer) {
            preferences.put(key, (Integer) value);
        } else if (value instanceof Boolean) {
            preferences.put(key, (Boolean) value);
        } else {
            throw new Exception("Unknown type of instance.");
        }
        return result;
    }

    @Override
    public void save() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(preferenceFile, preferences);
    }

    private static void makeBackup(File file) {
        if (file == null || !file.isFile()) {
            return;
        }
        String timestamp = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        File bakFile = new File(file.getAbsolutePath() + "." + timestamp + ".bak");
        try {
            FileUtils.copyFile(file, bakFile);
        } catch (IOException ex) {
        }
    }

}
