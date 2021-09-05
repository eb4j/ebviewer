package io.github.eb4j.ebview.dictionary.stardict;

import io.github.eb4j.ebview.data.DictionaryData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class StarDictFileDict extends StarDictBaseDict {
    private final RandomAccessFile dataFile;

    public StarDictFileDict(final File dictFile, final DictionaryData<StarDictEntry> data) throws FileNotFoundException {
        super(data);
        dataFile = new RandomAccessFile(dictFile, "r");
    }

    @Override
    protected String readArticle(int start, int len) {
        String result = null;
        try {
            byte[] data = new byte[len];
            dataFile.seek(start);
            int readLen = dataFile.read(data);
            result = new String(data, 0, readLen, StandardCharsets.UTF_8);
        } catch (IOException e) {
            //Log.log(e);
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        dataFile.close();
    }
}
