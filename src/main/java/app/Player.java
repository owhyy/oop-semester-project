package app;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import java.io.File;

final class Player {
    static final class Controls {
        final JButton playButton;
        final JButton pauseButton;
        final JButton stopButton;
        final JSlider seekSlider;
        final JLabel timeLabel;
        final JSlider volumeSlider;
        final JLabel volumePercent;

        Controls(
                JButton playButton,
                JButton pauseButton,
                JButton stopButton,
                JSlider seekSlider,
                JLabel timeLabel,
                JSlider volumeSlider,
                JLabel volumePercent
        ) {
            this.playButton = playButton;
            this.pauseButton = pauseButton;
            this.stopButton = stopButton;
            this.seekSlider = seekSlider;
            this.timeLabel = timeLabel;
            this.volumeSlider = volumeSlider;
            this.volumePercent = volumePercent;
        }
    }

    final String baseStatus;
    final JTextArea statusArea;
    File selectedFile;
    double durationSeconds;
    final JButton playButton;
    final JButton pauseButton;
    final JButton stopButton;
    final JSlider seekSlider;
    final JLabel timeLabel;
    final JSlider volumeSlider;
    final JLabel volumePercent;
    boolean isPaused;
    boolean isUpdatingSeek;

    Player(
            String baseStatus,
            JTextArea statusArea,
            Controls controls
    ) {
        this.baseStatus = baseStatus;
        this.statusArea = statusArea;
        this.playButton = controls.playButton;
        this.pauseButton = controls.pauseButton;
        this.stopButton = controls.stopButton;
        this.seekSlider = controls.seekSlider;
        this.timeLabel = controls.timeLabel;
        this.volumeSlider = controls.volumeSlider;
        this.volumePercent = controls.volumePercent;
        this.isPaused = false;
    }
}
