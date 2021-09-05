package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.ebview.data.DictionaryData;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.ebview.dictionary.stardict.StarDictEntry;
import io.github.eb4j.ebview.dictionary.stardict.StarDictFileDict;
import io.github.eb4j.ebview.dictionary.stardict.StarDictZipDict;
import org.dict.zip.DictZipInputStream;
import org.dict.zip.RandomAccessInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * Dictionary implementation for StarDict format.
 * <p>
 * StarDict format described on http://code.google.com/p/babiloo/wiki/StarDict_format
 * <p>
 * <h1>Files</h1>
 * Every dictionary consists of these files:
 * <ol><li>somedict.ifo
 * <li>somedict.idx or somedict.idx.gz
 * <li>somedict.dict or somedict.dict.dz
 * <li>somedict.syn (optional)
 * </ol>
 *
 * @author Alex Buloichik
 * @author Hiroshi Miura
 * @author Aaron Madlon-Kay
 * @author Suguru Oho
 */
public class StarDict implements IDictionaryFactory {

    @Override
    public boolean isSupportedFile(File file) {
        return file.getPath().endsWith(".ifo");
    }

    @Override
    public IDictionary loadDict(File ifoFile) throws Exception {
        Map<String, String> header = readIFO(ifoFile);
        String version = header.get("version");
        if (!"2.4.2".equals(version) && !"3.0.0".equals(version)) {
            throw new Exception("Invalid version of dictionary: " + version);
        }
        String sametypesequence = header.get("sametypesequence");
        if (!"g".equals(sametypesequence)
                && !"m".equals(sametypesequence)
                && !"x".equals(sametypesequence)
                && !"h".equals(sametypesequence)) {
            throw new Exception("Invalid type of dictionary: " + sametypesequence);
        }

        /*
         * Field in StarDict .ifo file, added in version 3.0.0. This must be
         * retained in order to support idxoffsetbits=64 dictionaries (not yet
         * implemented).
         *
         * See http://www.stardict.org/StarDictFileFormat
         */
        int idxoffsetbits = 32;
        if ("3.0.0".equals(version)) {
            String bitsString = header.get("idxoffsetbits");
            if (bitsString != null) {
                idxoffsetbits = Integer.parseInt(bitsString);
            }
        }

        if (idxoffsetbits != 32) {
            throw new Exception("StarDict dictionaries with idxoffsetbits=64 are not supported.");
        }

        String f = ifoFile.getPath();
        if (f.endsWith(".ifo")) {
            f = f.substring(0, f.length() - ".ifo".length());
        }
        String dictName = f;

        File idxFile = getFile(dictName, ".idx.gz", ".idx")
                .orElseThrow(() -> new FileNotFoundException("No .idx file could be found"));
        DictionaryData<StarDictEntry> data = loadData(idxFile);

        File dictFile = getFile(dictName, ".dict.dz", ".dict")
                .orElseThrow(() -> new FileNotFoundException("No .dict.dz or .dict files were found for " + dictName));

        try {
            if (dictFile.getName().endsWith(".dz")) {
                return new StarDictZipDict(dictFile, data);
            } else {
                return new StarDictFileDict(dictFile, data);
            }
        } catch (IOException ex) {
            throw new FileNotFoundException("No .dict.dz or .dict files were found for " + dictName);
        }
    }

    /**
     * Read header.
     */
    private Map<String, String> readIFO(File ifoFile) throws Exception {
        Map<String, String> result = new TreeMap<>();
        try (BufferedReader rd = Files.newBufferedReader(ifoFile.toPath(), StandardCharsets.UTF_8)) {
            String line;
            String first = rd.readLine();
            if (!"StarDict's dict ifo file".equals(first)) {
                throw new Exception("Invalid header of .ifo file: " + first);
            }
            while ((line = rd.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                int pos = line.indexOf('=');
                if (pos < 0) {
                    throw new Exception("Invalid format of .ifo file: " + line);
                }
                result.put(line.substring(0, pos), line.substring(pos + 1));
            }
        }
        return result;
    }

    private Optional<File> getFile(String basename, String... suffixes) {
        return Stream.of(suffixes).map(suff -> new File(basename + suff)).filter(f -> f.isFile())
                .findFirst();
    }

    private DictionaryData<StarDictEntry> loadData(File idxFile) throws IOException {
        InputStream is = new FileInputStream(idxFile);
        if (idxFile.getName().endsWith(".gz")) {
            // BufferedInputStream.DEFAULT_BUFFER_SIZE = 8192
            is = new GZIPInputStream(is, 8192);
        }
        DictionaryData<StarDictEntry> newData = new DictionaryData<>();
        try (DataInputStream idx = new DataInputStream(new BufferedInputStream(is));
              ByteArrayOutputStream mem = new ByteArrayOutputStream()) {
            while (true) {
                int b = idx.read();
                if (b == -1) {
                    break;
                }
                if (b == 0) {
                    String key = new String(mem.toByteArray(), 0, mem.size(), StandardCharsets.UTF_8);
                    mem.reset();
                    int bodyOffset = idx.readInt();
                    int bodyLength = idx.readInt();
                    newData.add(key, new StarDictEntry(bodyOffset, bodyLength));
                } else {
                    mem.write(b);
                }
            }
        }
        is.close();
        newData.done();
        return newData;
    }

}
