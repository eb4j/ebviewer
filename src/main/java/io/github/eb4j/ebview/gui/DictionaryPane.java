package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.data.DictionaryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
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
import java.util.List;
import java.util.Map;

/**
 * Dictionary article display that accept threading update.
 * @author Hiroshi Miura
 */
public class DictionaryPane extends JTextPane implements IThreadPane {

    static final Logger LOG = LoggerFactory.getLogger(DictionaryPane.class.getName());

    private final StyleSheet baseStyleSheet = new StyleSheet();
    private final HTMLEditorKit htmlEditorKit = new HTMLEditorKit();

    public DictionaryPane() {
        super();

        setContentType("text/html");
        ((HTMLDocument) getDocument()).setPreservesUnknownTags(false);
        htmlEditorKit.setStyleSheet(baseStyleSheet);
        setEditorKit(htmlEditorKit);
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
    @SuppressWarnings("avoidinlineconditionals")
    public void setFont(final Font font) {
        super.setFont(font);
        Document doc = getDocument();
        if (!(doc instanceof HTMLDocument)) {
            return;
        }
        baseStyleSheet.addRule("body { font-family: " + font.getName() + "; "
                + " font-size: " + font.getSize() + "; "
                + " font-style: " + (font.getStyle() == Font.BOLD ? "bold"
                : font.getStyle() == Font.ITALIC ? "italic" : "normal") + "; "
                + " color: " + toHex(UIManager.getColor("TextPane.foreground")) + "; "
                + " background: " + toHex(UIManager.getColor("TextPane.background")) + "; } "
                + ".word {font-size: " + (2 + font.getSize()) + "; font-style: bold; }"
        );
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
        txt.append("<html>");
        for (DictionaryEntry de : data) {
            String dictNamePrefix = de.getDictName().substring(0, 2).toLowerCase();
            if (wasPrev) {
                txt.append("<br><hr>");
            } else {
                wasPrev = true;
            }
            txt.append("<div id =\"" + i + "\"><b><span class=\"word\">");
            txt.append(de.getWord());
            txt.append("</span></b>");
            txt.append(" - ");
            txt.append(de.getArticle());
            txt.append("</div>");
            i++;
        }
        appendText(txt.toString());
        txt.append("</html>");
    }

    public void moveTo(final int index) {
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
    }

    private void appendText(final String txt) {
        Document doc = getDocument();
        try {
            Reader r;
            if (doc.getLength() == 0) {
                r = new StringReader(txt);
            } else {
                StringBuilder sb = new StringBuilder(doc.getText(0, doc.getLength())).append(txt);
                r = new StringReader(sb.toString());
            }
            doc = htmlEditorKit.createDefaultDocument();
            ((HTMLDocument) doc).setPreservesUnknownTags(false);
            htmlEditorKit.read(r, doc, 0);
            setDocument(doc);
        } catch (IOException | BadLocationException  e) {
            LOG.warn("error");
        }
    }

    private String toHex(final Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
