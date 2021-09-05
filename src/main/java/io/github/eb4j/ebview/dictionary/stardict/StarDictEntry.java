package io.github.eb4j.ebview.dictionary.stardict;

import java.util.Objects;

/**
 * Simple container for offsets+lengths of entries in StarDict dictionary.
 * Subclasses of StarDictDict know how to read this from the underlying data
 * file.
 */
public class StarDictEntry {
    private final int start;
    private final int len;

    public StarDictEntry(int start, int len) {
        this.start = start;
        this.len = len;
    }

    public int getStart() {
        return start;
    }

    public int getLen() {
        return len;
    }

    @Override
    public int hashCode() {
        return Objects.hash(len, start);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StarDictEntry other = (StarDictEntry) obj;
        return len == other.len && start == other.start;
    }
}
