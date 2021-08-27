package io.github.eb4j.ebview;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.util.List;

public class MainWindow extends JFrame {
    private JPanel panel1;
    private JTextField searchWordField;
    private JButton searchButton;
    private JScrollPane articlePane;

    public MainWindow(final EBDict ebDict) {
        super("EBViewer");
        setPreferredSize(new Dimension(415, 405));
        setLayout(new BorderLayout());
        //
        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());
        searchWordField = new JTextField();
        searchWordField.setPreferredSize(new Dimension(300, 30));
        searchButton = new JButton();
        searchButton.setText("Search");
        panel1.add(searchWordField);
        panel1.add(searchButton);
        //
        ThreadPane threadPane = new ThreadPane();
        articlePane = new JScrollPane(threadPane);
        articlePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        configureMargin(threadPane);
        //
        getContentPane().add(panel1, BorderLayout.NORTH);
        getContentPane().add(articlePane, BorderLayout.CENTER);
        //
        searchButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(final ActionEvent e) {
                String word = searchWordField.getText();
                new Thread(() -> {
                    List<DictionaryEntry> result = ebDict.readArticles(word);
                    SwingUtilities.invokeLater(() -> threadPane.setFoundResult(result));
                }).start();
            }
        });

        // Resize
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(final ComponentEvent event) {
                updateMargin(threadPane);
            }
        });
        //
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(true);
        setVisible(true);
    }

    static void updateMargin(final JTextPane textPane) {
        JViewport viewport = (JViewport)
                SwingUtilities.getAncestorOfClass(JViewport.class, textPane);

        if (viewport != null) {
            Insets margin = textPane.getMargin();

            int len = textPane.getDocument().getLength();
            try {
                Rectangle end = textPane.modelToView(len);
                if (end != null) {
                    margin.bottom = viewport.getHeight() - end.height;
                    textPane.setMargin(margin);
                }
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static void configureMargin(final JTextPane textPane) {
        textPane.addPropertyChangeListener("page", event -> updateMargin(textPane));

        textPane.addHierarchyListener(event -> {
            long flags = event.getChangeFlags();
            if ((flags & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                updateMargin(textPane);
            }
        });

        textPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent event) {
                updateMargin(textPane);
            }
        });

        textPane.getDocument().addDocumentListener(new DocumentListener() {
            private void updateTextPane() {
                EventQueue.invokeLater(() -> updateMargin(textPane));
            }

            @Override
            public void changedUpdate(final DocumentEvent event) {
                updateTextPane();
            }

            @Override
            public void insertUpdate(final DocumentEvent event) {
                updateTextPane();
            }

            @Override
            public void removeUpdate(final DocumentEvent event) {
                updateTextPane();
            }
        });
    }
}
