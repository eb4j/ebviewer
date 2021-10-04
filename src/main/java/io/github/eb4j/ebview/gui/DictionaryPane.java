package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.data.DictionaryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * Dictionary article display that accept threading update.
 *
 * @author Hiroshi Miura
 */
public class DictionaryPane extends JTextPane implements IThreadPane {

    static final Logger LOG = LoggerFactory.getLogger(DictionaryPane.class.getName());

    private final HTMLEditorKit htmlEditorKit = new HTMLEditorKit();

    private StyleSheet baseStyleSheet;
    private int zoomLevel = 0;

    public DictionaryPane() {
        super();
        setContentType("text/html");
        ((HTMLDocument) getDocument()).setPreservesUnknownTags(false);
        setFont(getFont());
        setStyle();
        htmlEditorKit.setStyleSheet(baseStyleSheet);
        setEditorKit(htmlEditorKit);
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
        addHyperlinkListener(new LinkActionListener());
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void setFont(final Font font) {
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
        super.setFont(font.deriveFont(attributes));
        Document doc = getDocument();
        if (!(doc instanceof HTMLDocument)) {
            return;
        }
    }

    @SuppressWarnings({"avoidinlineconditionals"})
    private void setStyle() {
        Font font = getFont();
        baseStyleSheet = new StyleSheet();
        baseStyleSheet.addRule("body { font-family: " + font.getName() + "; "
                + " font-size: " + font.getSize() + "; "
                + " font-style: " + (font.getStyle() == Font.BOLD ? "bold"
                : font.getStyle() == Font.ITALIC ? "italic" : "normal") + "; "
                + " color: " + toHex(UIManager.getColor("TextPane.foreground")) + "; "
                + " background: " + toHex(UIManager.getColor("TextPane.background")) + "; } "
                + ".word {font-size: " + (zoomLevel + 2 + font.getSize()) + "; font-style: bold; }"
                + ".article {font-size: " + (zoomLevel + font.getSize()) + "; }"
                + ".reference { font-style: italic; }"
        );
        htmlEditorKit.setStyleSheet(baseStyleSheet);
    }

    private void limitZoom() {
        int size = getFont().getSize();
        if (size + zoomLevel > 64) {
            zoomLevel = 64 - size;
            return;
        }
        if (size + zoomLevel < 6) {
            zoomLevel = size - 6;
        }
    }

    public void increaseZoom() {
        zoomLevel++;
        limitZoom();
        setStyle();
    }

    public void decreaseZoom() {
        zoomLevel--;
        limitZoom();
        setStyle();
    }

    public String getZoomLevel() {
        if (zoomLevel > 0) {
            return "+" + zoomLevel;
        } else {
            return String.valueOf(zoomLevel);
        }
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
            if (wasPrev) {
                txt.append("<br><hr>");
            } else {
                wasPrev = true;
            }
            txt.append("<div class=\"block\" id =\"" + i + "\"><span class=\"word\">");
            txt.append(de.getWord());
            txt.append("</span>");
            txt.append(" - <span class=\"article\">");
            txt.append(de.getArticle());
            txt.append("</span></div>");
            i++;
        }
        txt.append("</html>");
        appendText(txt.toString());
    }

    public void moveTo(final int index) {
        HTMLDocument doc = (HTMLDocument) getDocument();
        Element el = doc.getElement(Integer.toString(index));
        if (el == null) {
            return;
        }
        int pos1 = el.getStartOffset();
        int pos2 = el.getEndOffset();
        try {
            Rectangle2D rect1 = modelToView2D(pos1);
            Rectangle2D rect2 = modelToView2D(pos2);
            // show last of article
            if (rect2 != null) {
                scrollRectToVisible(rect2.getBounds());
            }
            // show first of article
            if (rect1 != null) {
                scrollRectToVisible(rect1.getBounds());
            }
            // highlight selected
            // getHighlighter().removeAllHighlights();
            // getHighlighter().addHighlight(pos1, pos2, DefaultHighlighter.DefaultPainter);

        } catch (BadLocationException ignore) {
        }
    }

    /**
     * Clears up the pane.
     */
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
        } catch (IOException | BadLocationException e) {
            LOG.warn("error");
        }
    }

    private String toHex(final Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

}
