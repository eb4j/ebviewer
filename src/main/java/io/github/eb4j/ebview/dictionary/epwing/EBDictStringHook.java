package io.github.eb4j.ebview.dictionary.epwing;

import io.github.eb4j.EBException;
import io.github.eb4j.GraphicData;
import io.github.eb4j.SoundData;
import io.github.eb4j.SubBook;
import io.github.eb4j.hook.Hook;
import io.github.eb4j.hook.HookAdapter;
import io.github.eb4j.util.ImageUtil;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * EB/EPWING sequence Hook handler.
 */
public final class EBDictStringHook extends HookAdapter<String> {

    private final int maxlines;
    private final StringBuffer output = new StringBuffer(16384);
    private int lineNum = 0;
    private boolean narrow = false;
    private int decType;
    private final Gaiji gaiji;
    private final SoundData soundData;
    private final GraphicData graphicData;
    private final File[] movieFileList;

    private int lastWidth;
    private int lastHeight;

    public EBDictStringHook(final SubBook subbok) {
        this(subbok, 500);
    }

    public EBDictStringHook(final SubBook subbook, final int lines) {
        super();
        maxlines = lines;
        gaiji = new Gaiji(subbook);
        soundData = subbook.getSoundData();
        graphicData = subbook.getGraphicData();
        movieFileList = subbook.getMovieFileList();
        lastWidth = -1;
        lastHeight = -1;
    }

    /**
     * convert Zenkaku alphabet to Hankaku.
     * <p>
     * convert (\uFF01 - \uFF5E) to (\u0021- \u007E) and \u3000 to \u0020
     *
     * @param text source text with zenkaku.
     * @return String converted
     */
    private static String convertZen2Han(final String text) {
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            int cp = text.codePointAt(i);
            if (0xFF00 < cp && cp < 0xFF5F) {
                result.append((char) (cp - 0xFEE0));
            } else if (cp == 0x3000) {
                result.append("\u0020");
            } else {
                result.appendCodePoint(cp);
            }
        }
        return result.toString();
    }

    /**
     * clear output line.
     */
    @Override
    public void clear() {
        output.delete(0, output.length());
        lineNum = 0;
    }

    /*
     * get result string.
     */
    @Override
    public String getObject() {
        return output.toString();
    }

    /*
     * Can accept more input?
     */
    @Override
    public boolean isMoreInput() {
        return lineNum < maxlines;
    }

    /**
     * append character.
     *
     * @param ch character
     */
    @Override
    public void append(final char ch) {
        append(Character.toString(ch));
    }

    /**
     * Append article text.
     *
     * @param text string to append
     */
    @Override
    public void append(final String text) {
        if (narrow) {
            output.append(convertZen2Han(text));
        } else {
            output.append(text);
        }
    }

    /**
     * Append GAIJI text(Unicode).
     *
     * @param code gaiji code
     */
    @Override
    public void append(final int code) {
        output.append(gaiji.getAltCode(code, narrow));
    }

    /**
     * begin roman alphabet.
     */
    @Override
    public void beginNarrow() {
        narrow = true;
    }

    /**
     * end roman alphabet.
     */
    @Override
    public void endNarrow() {
        narrow = false;
    }

    /**
     * begin subscript.
     */
    @Override
    public void beginSubscript() {
        output.append("<sub>");
    }

    /**
     * end subscript.
     */
    @Override
    public void endSubscript() {
        output.append("</sub>");
    }

    /**
     * begin super script.
     */
    @Override
    public void beginSuperscript() {
        output.append("<sup>");
    }

    /**
     * end super script.
     */
    @Override
    public void endSuperscript() {
        output.append("</sup>");
    }

    /**
     * set indent of line head.
     *
     * @param len size of indent
     */
    @Override
    public void setIndent(final int len) {
        for (int i = 0; i < len; i++) {
            output.append("&nbsp;");
        }
    }

    /**
     * insert new line.
     */
    @Override
    public void newLine() {
        output.append("<br>");
        lineNum++;
    }

    /**
     * set no break.
     */
    @Override
    public void beginNoNewLine() {
    }

    @Override
    public void endNoNewLine() {
    }

    /**
     * insert em tag.
     */
    @Override
    public void beginEmphasis() {
        output.append("<em>");
    }

    @Override
    public void endEmphasis() {
        output.append("</em>");
    }

    /**
     * insert decoration.
     *
     * @param type decoration type #BOLD #ITALIC
     */
    @Override
    public void beginDecoration(final int type) {
        this.decType = type;
        switch (decType) {
            case BOLD:
                output.append("<span style=\"font-style: bold\">");
                break;
            case ITALIC:
                output.append("<span style=\"font-style: italic\">");
                break;
            default:
                output.append("<span style=\"text-decoration:underline\">");
                break;
        }
    }

    @Override
    public void endDecoration() {
        output.append("</span>");
    }

    @Override
    public void beginKeyword() {
        output.append("<span class=\"keyword\">");
    }

    @Override
    public void endKeyword() {
        output.append("</span>");
    }

    @Override
    public void beginUnicode() {
    }

    @Override
    public void endUnicode() {
    }

    @Override
    @SuppressWarnings("emptyblock")
    public void beginSound(final int format, final long start, final long end) {
        if (format == Hook.WAVE) {
            try {
                byte[] bytes = soundData.getWaveSound(start, end);
                File tmpAudioFile = File.createTempFile("ebviewer", ".wav");
                tmpAudioFile.deleteOnExit();
                try (FileOutputStream outputStream = new FileOutputStream(tmpAudioFile)) {
                    outputStream.write(bytes);
                }
                output.append("<a href=\"file:").append(tmpAudioFile.toPath()).append("\">");
            } catch (EBException | IOException e) {
                e.printStackTrace();
            }
        } else if (format == Hook.MIDI) {
            // implement me.
        }
    }

    @Override
    public void endSound() {
        output.append("</a>");
    }

    @Override
    public void beginMonoGraphic(final int width, final int height) {
        lastHeight = height;
        lastWidth = width;
        output.append("<img height=\"").append(height).append("\" width=\"").append(width).append("\" ");
        output.append("alt=\"");
    }

    @Override
    public void endMonoGraphic(final long pos) {
        try {
            byte[] bytes = graphicData.getMonoGraphic(pos, lastWidth, lastHeight);
            output.append("\" src=\"data:image/png;base64,");
            output.append(Utils.convertMonoGraphic2Base64(bytes, lastWidth, lastHeight)).append("\"/>");
        } catch (EBException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beginInlineColorGraphic(final int format, final long pos) {
        try {
            byte[] bytes = graphicData.getColorGraphic(pos);
            if (format == Hook.JPEG) {
                output.append("<img src=\"data:image/jpeg;base64,");
                output.append(Utils.convertImage2Base64("jpeg", bytes));
                output.append("\" alt=\"");
            } else {
                bytes = ImageUtil.dibToPNG(bytes);
                output.append("<img src=\"data:image/png;base64,");
                output.append(Utils.convertImage2Base64("png", bytes));
                output.append("\" alt=\"");
            }
        } catch (EBException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endInlineColorGraphic() {
        output.append("\"/>");
    }

    @Override
    public void beginMovie(final int format, final int width, final int height, final String filename) {
        if (format == Hook.MPEG) {
            File movie = Arrays.stream(movieFileList)
                    .filter(file -> file.getName().equals(filename))
                    .findFirst()
                    .orElseGet(null);
            if (movie == null) {
                // should not come here
                output.append("<a>");
            }
            try {
                File tmpFile = File.createTempFile("ebviewer", ".mpg");
                tmpFile.deleteOnExit();
                try (FileOutputStream outputStream = new FileOutputStream(tmpFile);
                     FileInputStream inputStream = new FileInputStream(movie)) {
                    IOUtils.copy(inputStream, outputStream);
                }
                output.append("<a href=\"file:").append(tmpFile.getAbsolutePath()).append("\">\uD83C\uDFA6");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void endMovie() {
        output.append("\uD83C\uDFA6</a>");
    }

    @Override
    public void beginReference() {
        output.append("<span class=\"reference\">");
    }

    @Override
    public void endReference(final long pos) {
        output.append("</span>");
    }

}
