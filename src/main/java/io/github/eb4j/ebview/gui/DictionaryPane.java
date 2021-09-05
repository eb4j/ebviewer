package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.data.DictionaryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dictionary article display that accept threading update.
 * @author Hiroshi Miura
 */
public class DictionaryPane extends JTextPane implements IThreadPane {

    static final Logger LOG = LoggerFactory.getLogger(DictionaryPane.class.getName());

    protected final List<String> displayedWords = new ArrayList<>();

    public DictionaryPane() {
        super();

        setContentType("text/html");
        ((HTMLDocument) getDocument()).setPreservesUnknownTags(false);
        Font font = getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
        font = font.deriveFont(attributes);
        setFont(font);
        FocusListener listener = new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                Caret caret = getCaret();
                caret.setVisible(true);
                caret.setSelectionVisible(true);
            }
        };
        addFocusListener(listener);
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
            txt.append(" - ");
            txt.append(de.getArticle());
            displayedWords.add(de.getWord().toLowerCase());
            i++;
        }
        appendText(txt.toString());
    }

    public void moveToWord(final String word) {
        int index = displayedWords.indexOf(word.toLowerCase());
        if (index == -1) {
            return;
        }
        HTMLDocument doc = (HTMLDocument) getDocument();
        Element el = doc.getElement(Integer.toString(index));
        if (el == null) {
            return;
        }
        try {
            Rectangle rect = modelToView(el.getStartOffset());
            if (rect != null) {
                // show 5 lines
                rect.height *= 5;
                scrollRectToVisible(rect);
            }
        } catch (BadLocationException ignore) {
        }
    }

    /** Clears up the pane. */
    private void clear() {
        setText(null);
        scrollRectToVisible(new Rectangle());
        displayedWords.clear();
    }

    private void appendText(final String txt) {
        Document doc = getDocument();
        EditorKit kit = getEditorKit();
        try {
            Reader r;
            if (doc.getLength() == 0) {
                r = new StringReader(txt);
            } else {
                StringBuilder sb = new StringBuilder(doc.getText(0, doc.getLength())).append(txt);
                r = new StringReader(sb.toString());
            }
            doc = kit.createDefaultDocument();
            ((HTMLDocument) doc).setPreservesUnknownTags(false);
            kit.read(r, doc, 0);
            setDocument(doc);
        } catch (IOException | BadLocationException  e) {
            LOG.warn("error");
        }
    }

    private String toHex(final Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
