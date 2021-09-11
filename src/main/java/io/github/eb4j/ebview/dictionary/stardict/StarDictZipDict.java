package io.github.eb4j.ebview.dictionary.stardict;

import io.github.eb4j.ebview.data.DictionaryData;
import org.dict.zip.DictZipInputStream;
import org.dict.zip.RandomAccessInputStream;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class StarDictZipDict extends StarDictBaseDict {

    private final DictZipInputStream dataFile;
    private final String dictFilePath;
    private final String bookName;

    public StarDictZipDict(final String bookName, final File dictFile, final DictionaryData<StarDictEntry> data)
            throws IOException {
        super(data);
        dictFilePath = dictFile.getPath();
        dataFile = new DictZipInputStream(new RandomAccessInputStream(new RandomAccessFile(dictFile, "r")));
        this.bookName = bookName;
    }

    @Override
    public String getDictionaryName() {
        return bookName;
    }

    @Override
    protected String readArticle(final int start, final int len) {
        String result = null;
        try {
            dataFile.seek(start);
            byte[] data = new byte[len];
            dataFile.readFully(data);
            result = new String(data, StandardCharsets.UTF_8);
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
