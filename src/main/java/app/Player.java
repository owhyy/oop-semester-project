package app;

import javax.swing.JButton;
import javax.swing.JTextArea;
import java.io.File;

final class Player {
    final String baseStatus;
    final JTextArea statusArea;
    File selectedFile;
    final JButton playButton;
    final JButton pauseButton;
    final JButton stopButton;
    boolean isPaused;

    Player(
            String baseStatus,
            JTextArea statusArea,
            JButton playButton,
            JButton pauseButton,
            JButton stopButton
    ) {
        this.baseStatus = baseStatus;
        this.statusArea = statusArea;
        this.playButton = playButton;
        this.pauseButton = pauseButton;
        this.stopButton = stopButton;
        this.isPaused = false;
    }
}
