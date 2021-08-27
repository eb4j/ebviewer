package io.github.eb4j.ebview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class DictionaryPane extends JTextPane implements IThreadPane {

    static final Logger LOG = LoggerFactory.getLogger(DictionaryPane.class.getName());

    protected final List<String> displayedWords = new ArrayList<>();

    public DictionaryPane() {
        super();

        setContentType("text/html");
        ((HTMLDocument) getDocument()).setPreservesUnknownTags(false);
        setFont(getFont());
        setMinimumSize(new Dimension(400, 300));
        setEditable(false);
    }

    @Override
    public void setFont(final Font font) {
        super.setFont(font);
        Document doc = getDocument();
        if (!(doc instanceof HTMLDocument)) {
            return;
        }
        StyleSheet styleSheet = ((HTMLDocument) doc).getStyleSheet();
        styleSheet.addRule("body { font-family: " + font.getName() + "; "
                + " font-size: " + font.getSize() + "; "
                + " font-style: " + (font.getStyle() == Font.BOLD ? "bold"
                : font.getStyle() == Font.ITALIC ? "italic" : "normal") + "; "
                + " color: " + toHex(UIManager.getColor("TextPane.foreground")) + "; "
                + " background: " + toHex(UIManager.getColor("TextPane.background")) + "; "
                + " }");  // noqa
    }

    @Override
    public void setFoundResult(final List<DictionaryEntry> data) {
        clear();

        if (data == null) {
            return;
        }

        StringBuilder txt = new StringBuilder();
        boolean wasPrev = false;
        int i = 0;
        for (DictionaryEntry de : data) {
            if (wasPrev) {
                txt.append("<br><hr>");
            } else {
                wasPrev = true;
            }
            txt.append("<b><span id=\"" + i + "\">");
            txt.append(de.getWord());
            txt.append("</span></b>");
            txt.append(" - ").append(de.getArticle());
            displayedWords.add(de.getWord().toLowerCase());
            i++;
        }
        appendText(txt.toString());
    }

    /** Clears up the pane. */
    private void clear() {
        setText(null);
        scrollRectToVisible(new Rectangle());
        displayedWords.clear();
    }

    private void appendText(final String txt) {
        Document doc = getDocument();
        if (doc.getLength() == 0) {
            // Appending to an empty document results in treating HTML tags as
            // plain text for some reason
            setText(txt);
        } else {
            try {
                doc.insertString(doc.getLength(), txt, null);
            } catch (BadLocationException e) {
                LOG.warn(e.getMessage());
            }
        }
    }

    private String toHex(final Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

}
