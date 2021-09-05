package io.github.eb4j.ebview.dictionary.stardict;

import io.github.eb4j.ebview.data.DictionaryData;
import org.dict.zip.DictZipInputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StarDictZipDict extends StarDictBaseDict {
    private final DictZipInputStream dataFile;

    public StarDictZipDict(DictZipInputStream dataFile, DictionaryData<StarDictEntry> data) {
        super(data);
        this.dataFile = dataFile;
    }

    @Override
    protected String readArticle(int start, int len) {
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
