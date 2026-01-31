package app;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import java.io.File;

final class Player {
    final String baseStatus;
    final JTextArea statusArea;
    File selectedFile;
    double durationSeconds;
    final JButton playButton;
    final JButton pauseButton;
    final JButton stopButton;
    final JSlider seekSlider;
    final JLabel timeLabel;
    boolean isPaused;
    boolean isUpdatingSeek;

    Player(
            String baseStatus,
            JTextArea statusArea,
            JButton playButton,
            JButton pauseButton,
            JButton stopButton,
            JSlider seekSlider,
            JLabel timeLabel
    ) {
        this.baseStatus = baseStatus;
        this.statusArea = statusArea;
        this.playButton = playButton;
        this.pauseButton = pauseButton;
        this.stopButton = stopButton;
        this.seekSlider = seekSlider;
        this.timeLabel = timeLabel;
        this.isPaused = false;
    }
}
