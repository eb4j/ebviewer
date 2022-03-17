/*
 * EBViewer, a dictionary viewer application.
 * Copyright (C) 2022 Hiroshi Miura.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.gui.dialogs.MoviePlay;
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class LinkActionListener implements HyperlinkListener {

    private static final String[] MOVIE_EXTS = {".mpg", ".MPG", ".ogv", ".mp4", ".mov"};
    private static final String[] SOUND_EXTS = {".wav", ".WAV"};
    private static final String[] MUSIC_EXTS = {".mp3", ".MP3"};

    private static boolean hasExt(final String path, final String[] extrn) {
        return Arrays.stream(extrn).anyMatch(entry -> path.endsWith(entry));
    }

    public static synchronized void playSound(final File file) {
        new SwingWorker<Void, Void>() {
            private Clip clip;
            private AudioInputStream inputStream;

            @Override
            protected Void doInBackground() throws Exception {
                clip = AudioSystem.getClip();
                inputStream = AudioSystem.getAudioInputStream(file);
                clip.open(inputStream);
                clip.start();
                clip.drain();
                return null;
            }

            @Override
            protected void done() {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
                clip.close();
            }
        }.execute();
    }

    @Override
    public void hyperlinkUpdate(final HyperlinkEvent hyperlinkEvent) {
        if (hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
            URL url = hyperlinkEvent.getURL();
            if (url.getProtocol().equals("file")) {
                try {
                    String path = url.getPath();
                    if (hasExt(path, SOUND_EXTS)) {
                        playSound(new File(url.toURI()));
                    } else if (hasExt(path, MOVIE_EXTS)) {
                        MoviePlay player = new MoviePlay(354, 280);
                        player.play(path);
                    } else if (hasExt(path, MUSIC_EXTS)) {
                        AudioPlayerComponent audioPlayerComponent = new AudioPlayerComponent();
                        audioPlayerComponent.mediaPlayer().media().play(path);
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
