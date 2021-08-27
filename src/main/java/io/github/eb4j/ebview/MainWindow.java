package io.github.eb4j.ebview;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainWindow extends JFrame {
    JPanel panel1;
    JTextField searchWordField;
    JButton searchButton;
    JScrollPane articlePane;

    public MainWindow(EBDict ebDict) {
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
            public void actionPerformed(ActionEvent e) {
                String word = searchWordField.getText();
                DictionarySearcher searcher = new DictionarySearcher(threadPane, ebDict);
                searcher.setWord(word);
                searcher.start();
            }
        });

        // Resize
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                updateMargin(threadPane);
            }
        });
        //
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(true);
        setVisible(true);
    }

    static void updateMargin(JTextPane textPane) {
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
            public void componentResized(ComponentEvent event) {
                updateMargin(textPane);
            }
        });

        textPane.getDocument().addDocumentListener(new DocumentListener() {
            private void updateTextPane() {
                EventQueue.invokeLater(() -> updateMargin(textPane));
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                updateTextPane();
            }

            @Override
            public void insertUpdate(DocumentEvent event) {
                updateTextPane();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                updateTextPane();
            }
        });
    }
}
