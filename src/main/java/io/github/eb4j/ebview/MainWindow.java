package io.github.eb4j.ebview;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        DictionaryPane dictionaryPane = new DictionaryPane();
        articlePane = new JScrollPane(dictionaryPane);
        articlePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
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
                    SwingUtilities.invokeLater(() -> dictionaryPane.setFoundResult(result));
                }).start();
            }
        });
        //
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(true);
        setVisible(true);
    }

}
