package app;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class App {
    private static final String NATIVE_LIB_ERROR = "Native lib not loaded – build cpp/ first";
    private static final String UPLOAD_LABEL = "Upload Song";
    private static final String PLAY_LABEL = "Play";
    private static final String PAUSE_LABEL = "Pause";
    private static final String RESUME_LABEL = "Resume";
    private static final String STOP_LABEL = "Stop";
    private static final int SEEK_TIMER_MS = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Song Editor");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            final String baseStatus = "Song Editor";
            JTextArea statusArea = createStatusArea(baseStatus);

            JButton playButton = new JButton(PLAY_LABEL);
            playButton.setVisible(false);

            JButton pauseButton = new JButton(PAUSE_LABEL);
            pauseButton.setVisible(false);

            JButton stopButton = new JButton(STOP_LABEL);
            stopButton.setVisible(false);

            JLabel timeLabel = new JLabel("0:00 / 0:00");
            timeLabel.setVisible(false);
            JSlider seekSlider = new JSlider(0, 0, 0);
            seekSlider.setEnabled(false);
            seekSlider.setVisible(false);

            JLabel volumeLabel = new JLabel("Vol");
            JLabel volumePercent = new JLabel("50%");
            JSlider volumeSlider = new JSlider(0, 100, 50);
            volumeSlider.setEnabled(true);
            volumeSlider.setVisible(true);
            volumePercent.setVisible(true);

            Player.Controls controls = new Player.Controls(
                    playButton,
                    pauseButton,
                    stopButton,
                    seekSlider,
                    timeLabel,
                    volumeSlider,
                    volumePercent
            );
            Player playerState = new Player(baseStatus, statusArea, controls);
            playButton.addActionListener(event ->
                    runNativeSafe(playerState, () -> handlePlay(playerState))
            );
            pauseButton.addActionListener(event ->
                    runNativeSafe(playerState, () -> handlePauseToggle(playerState))
            );
            stopButton.addActionListener(event ->
                    runNativeSafe(playerState, () -> handleStop(playerState))
            );
            JButton uploadButton = new JButton(UPLOAD_LABEL);
            uploadButton.addActionListener(event ->
                    runNativeSafe(playerState, () -> handleUpload(frame, playerState))
            );

            seekSlider.addChangeListener(event -> {
                if (playerState.isUpdatingSeek || seekSlider.getValueIsAdjusting() || playerState.durationSeconds <= 0) {
                    return;
                }
                double targetSeconds = seekSlider.getValue();
                runNativeSafe(playerState, () -> {
                    int code = AudioLib.INSTANCE.seek_seconds(targetSeconds);
                    if (code == 0) {
                        updateTimeLabel(timeLabel, targetSeconds, playerState.durationSeconds);
                    }
                });
            });

            volumeSlider.addChangeListener(event -> {
                if (!volumeSlider.isEnabled()) {
                    return;
                }
                float volume = volumeSlider.getValue() / 100.0f;
                volumePercent.setText(volumeSlider.getValue() + "%");
                runNativeSafe(playerState, () -> AudioLib.INSTANCE.set_volume(volume));
            });

            Timer seekTimer = new Timer(SEEK_TIMER_MS, event ->
                    runNativeSafe(playerState, () -> updateSeekBar(playerState, seekSlider, timeLabel))
            );
            seekTimer.start();

            JPanel controlsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            controlsRow.add(uploadButton);
            controlsRow.add(playButton);
            controlsRow.add(pauseButton);
            controlsRow.add(stopButton);

            JPanel seekRow = new JPanel(new BorderLayout());
            seekRow.add(seekSlider, BorderLayout.CENTER);
            seekRow.add(timeLabel, BorderLayout.EAST);

            JPanel volumeRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            volumeRow.add(volumeLabel);
            volumeRow.add(volumeSlider);
            volumeRow.add(volumePercent);

            JPanel top = new JPanel();
            top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
            top.add(controlsRow);
            top.add(seekRow);
            top.add(volumeRow);

            frame.setLayout(new BorderLayout());
            frame.add(top, BorderLayout.NORTH);
            frame.add(new JScrollPane(statusArea), BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }

    private static JTextArea createStatusArea(String baseStatus) {
        JTextArea statusArea = new JTextArea(baseStatus);
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        return statusArea;
    }

    private static void handleUpload(JFrame frame, Player state) {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Audio files (wav, mp3, m4a, flac, ogg)",
                        "wav", "mp3", "m4a", "flac", "ogg"
                )
        );
        int result = chooser.showOpenDialog(frame);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selected = chooser.getSelectedFile();
        AudioLib.INSTANCE.stop_audio();
        state.isPaused = false;
        state.durationSeconds = 0;
        state.pauseButton.setText(PAUSE_LABEL);
        state.playButton.setVisible(false);
        state.pauseButton.setVisible(false);
        state.stopButton.setVisible(false);
        state.seekSlider.setVisible(false);
        state.timeLabel.setVisible(false);
        state.volumeSlider.setVisible(true);
        state.volumePercent.setVisible(true);
        state.seekSlider.setEnabled(false);
        state.volumeSlider.setEnabled(true);
        state.isUpdatingSeek = true;
        state.seekSlider.setValue(0);
        state.isUpdatingSeek = false;
        updateTimeLabel(state.timeLabel, 0, 0);

        state.selectedFile = selected;
        String path = selected.getAbsolutePath();
        int code = AudioLib.INSTANCE.load_audio(path);
        if (code == 0) {
            state.durationSeconds = AudioLib.INSTANCE.get_duration_seconds(path);
            state.statusArea.setText(state.baseStatus + "\nLoaded: " + selected.getName());
            state.playButton.setVisible(true);
            state.pauseButton.setVisible(true);
            state.stopButton.setVisible(true);
            state.seekSlider.setVisible(true);
            state.timeLabel.setVisible(true);
            state.volumeSlider.setVisible(true);
            state.volumePercent.setVisible(true);
            state.volumeSlider.setEnabled(true);
            updateTimeLabel(state.timeLabel, 0, state.durationSeconds);
            state.isPaused = false;
            state.pauseButton.setText(PAUSE_LABEL);
        } else {
            state.statusArea.setText(state.baseStatus + "\nFailed to load: " + path + " (code " + code + ")");
            state.playButton.setVisible(false);
            state.pauseButton.setVisible(false);
            state.stopButton.setVisible(false);
            state.seekSlider.setVisible(false);
            state.timeLabel.setVisible(false);
            state.volumeSlider.setVisible(true);
            state.volumePercent.setVisible(true);
            state.volumeSlider.setEnabled(true);
        }
    }

    private static void handlePlay(Player state) {
        if (state.selectedFile == null) {
            state.statusArea.setText(state.baseStatus + "\nNo file selected.");
            return;
        }
        int code = AudioLib.INSTANCE.play_audio(state.selectedFile.getAbsolutePath());
        if (code == 0) {
            state.statusArea.setText(state.baseStatus + "\nPlaying: " + state.selectedFile.getName());
        } else {
            state.statusArea.setText(state.baseStatus + "\nFailed to play file (code " + code + ").");
        }
    }

    private static void handleStop(Player state) {
        AudioLib.INSTANCE.stop_audio();
        state.statusArea.setText(state.baseStatus + "\nStopped.");
        if (state.seekSlider.isVisible()) {
            state.isUpdatingSeek = true;
            state.seekSlider.setValue(0);
            state.isUpdatingSeek = false;
            updateTimeLabel(state.timeLabel, 0, state.durationSeconds);
        }
    }

    private static void handlePauseToggle(Player state) {
        if (!state.isPaused) {
            int code = AudioLib.INSTANCE.pause_audio();
            if (code == 0) {
                state.isPaused = true;
                state.pauseButton.setText(RESUME_LABEL);
                state.statusArea.setText(state.baseStatus + "\nPaused.");
            } else {
                state.statusArea.setText(state.baseStatus + "\nNothing to pause.");
            }
        } else {
            int code = AudioLib.INSTANCE.resume_audio();
            if (code == 0) {
                state.isPaused = false;
                state.pauseButton.setText(PAUSE_LABEL);
                state.statusArea.setText(state.baseStatus + "\nResumed.");
            } else {
                state.statusArea.setText(state.baseStatus + "\nNothing to resume.");
            }
        }
    }

    private static void runNativeSafe(Player state, Runnable action) {
        try {
            action.run();
        } catch (UnsatisfiedLinkError e) {
            state.statusArea.setText(state.baseStatus + "\n" + NATIVE_LIB_ERROR);
        }
    }

    private static void updateSeekBar(Player state, JSlider slider, JLabel timeLabel) {
        if (state.durationSeconds <= 0 || state.selectedFile == null) {
            return;
        }
        double position = AudioLib.INSTANCE.get_position_seconds();
        if (position < 0) {
            return;
        }
        int durationInt = (int) Math.round(state.durationSeconds);
        if (slider.getMaximum() != durationInt) {
            slider.setMaximum(durationInt);
        }
        if (!slider.getValueIsAdjusting()) {
            state.isUpdatingSeek = true;
            slider.setValue((int) Math.round(position));
            state.isUpdatingSeek = false;
        }
        updateTimeLabel(timeLabel, position, state.durationSeconds);
        slider.setEnabled(true);
    }

    private static void updateTimeLabel(JLabel timeLabel, double position, double duration) {
        timeLabel.setText(formatTime(position) + " / " + formatTime(duration));
    }

    private static String formatTime(double seconds) {
        if (seconds < 0 || Double.isNaN(seconds) || Double.isInfinite(seconds)) {
            return "0:00";
        }
        long totalSeconds = (long) Math.floor(seconds);
        long minutes = totalSeconds / 60;
        long secs = totalSeconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
}
