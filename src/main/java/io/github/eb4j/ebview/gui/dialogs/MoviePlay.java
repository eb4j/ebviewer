package io.github.eb4j.ebview.gui.dialogs;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.binding.RuntimeUtil;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

public class MoviePlay extends JDialog {
    private final JPanel contentPane = new JPanel();
    private EmbeddedMediaPlayerComponent videoCanvas;

    public MoviePlay(final int width, final int height) {
        super();
        setTitle("Movie");
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "/usr/lib/x86_64-linux-gnu/vlc/");
        videoCanvas = new EmbeddedMediaPlayerComponent();
        videoCanvas.setVisible(true);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(videoCanvas, BorderLayout.CENTER);
        contentPane.setVisible(true);
        setContentPane(contentPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                onClose();
            }
        });
        contentPane.registerKeyboardAction(e -> onClose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        setSize(new Dimension(width, height));
        setVisible(true);
    }

    public synchronized void play(final String movie) {
        videoCanvas.mediaPlayer().media().play(movie);
    }

    private void onClose() {
        videoCanvas.release();
        dispose();
    }

}
