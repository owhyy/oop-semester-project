package app;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import java.io.File;

final class Player {
    static final class Controls {
        JButton playButton;
        JButton pauseButton;
        JButton stopButton;
        JSlider seekSlider;
        JLabel timeLabel;
        JSlider volumeSlider;
        JLabel volumePercent;
        JSlider speedSlider;
        JLabel speedPercent;
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
    final JSlider speedSlider;
    final JLabel speedPercent;
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
        this.speedSlider = controls.speedSlider;
        this.speedPercent = controls.speedPercent;
        this.isPaused = false;
    }
}
