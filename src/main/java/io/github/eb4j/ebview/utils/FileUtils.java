package io.github.eb4j.ebview.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Find files in subdirectories.
     *
     * @param dir
     *            directory to start find
     * @return list of filtered found files
     */
    public static List<File> findFiles(final File dir) {
        final List<File> result = new ArrayList<>();
        Set<String> knownDirs = new HashSet<>();
        findFiles(dir, result, knownDirs);
        return result;
    }

    private static void findFiles(final File dir, final List<File> result, final Set<String> knownDirs) {
        String currDir;
        try {
            // check for recursive
            currDir = dir.getCanonicalPath();
            if (!knownDirs.add(currDir)) {
                return;
            }
        } catch (IOException ex) {
            return;
        }
        File[] list = dir.listFiles();
        if (list != null) {
            for (File f : list) {
                if (f.isDirectory()) {
                    findFiles(f, result, knownDirs);
                } else {
                    result.add(f);
                }
            }
        }
    }
}
