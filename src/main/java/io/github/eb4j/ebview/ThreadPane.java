package io.github.eb4j.ebview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

public class ThreadPane extends JTextPane {

    static final Logger LOG = LoggerFactory.getLogger(ThreadPane.class.getName());

    private static final String EXPLANATION = "Dictionary search result";

    protected final List<String> displayedWords = new ArrayList<>();

    public enum EditorColor {
        COLOR_BACKGROUND(UIManager.getColor("TextPane.background")), // Also used for EditorPane.background
        COLOR_FOREGROUND(UIManager.getColor("TextPane.foreground"));

        private Color color;

        EditorColor(Color defaultColor) {
            this.color = defaultColor;
        }

        public String toHex() {
            return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        }

    }


    public ThreadPane() {
        super();

        setContentType("text/html");
        ((HTMLDocument) getDocument()).setPreservesUnknownTags(false);
        setFont(getFont());
        setMinimumSize(new Dimension(400, 300));
        setEditable(false);
        setText(EXPLANATION);
        makeCaretAlwaysVisible(this);
    }

    @Override
    public void setFont(Font font) {
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
                + " color: " + EditorColor.COLOR_FOREGROUND.toHex() + "; "
                + " background: " + EditorColor.COLOR_BACKGROUND.toHex() + "; "
                + " }");
    }

    /** Clears up the pane. */
    public void clear() {
        setText(null);
        scrollRectToVisible(new Rectangle());
        displayedWords.clear();
    }

    /**
     * Make caret visible even when the {@link JTextComponent} is not editable.
     */
    private static FocusListener makeCaretAlwaysVisible(final JTextComponent comp) {
        FocusListener listener = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                Caret caret = comp.getCaret();
                caret.setVisible(true);
                caret.setSelectionVisible(true);
            }
        };
        comp.addFocusListener(listener);
        return listener;
    }

    public void setFoundResult(List<DictionaryEntry> data) {
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

}
