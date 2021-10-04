package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.gui.dialogs.MoviePlay;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class LinkActionListener implements HyperlinkListener {

    private static final String[] movieExts = {".mpg", ".MPG", ".ogv", ".mp4", ".mov"};

    private static boolean hasExt(final String path, final String[] extrn) {
        return Arrays.stream(extrn).anyMatch(entry -> path.endsWith(entry));
    }

    public static synchronized void playSound(final File file) {
        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
                clip.open(inputStream);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }).start();
    }

    @Override
    public void hyperlinkUpdate(final HyperlinkEvent hyperlinkEvent) {
        if (hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
            URL url = hyperlinkEvent.getURL();
            if (url.getProtocol().equals("file")) {
                try {
                    String path = url.toURI().getPath();
                    if (path.endsWith(".wav") || path.endsWith(".WAV")) {
                        playSound(new File(path));
                    } else if (hasExt(path, movieExts)) {
                        MoviePlay player = new MoviePlay(354, 280);
                        player.play(path);
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
