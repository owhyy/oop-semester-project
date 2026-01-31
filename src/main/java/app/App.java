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

            Player playerState = new Player(baseStatus, statusArea, playButton, pauseButton, stopButton);
            playButton.addActionListener(event ->
                    handlePlay(playerState)
            );
            pauseButton.addActionListener(event ->
                    handlePauseToggle(playerState)
            );
            stopButton.addActionListener(event ->
                    handleStop(playerState)
            );
            JButton uploadButton = new JButton(UPLOAD_LABEL);
            uploadButton.addActionListener(event ->
                    handleUpload(frame, playerState)
            );

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
            top.add(uploadButton);
            top.add(playButton);
            top.add(pauseButton);
            top.add(stopButton);

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
        state.selectedFile = selected;
        String path = selected.getAbsolutePath();
        try {
            int code = AudioLib.INSTANCE.load_audio(path);
            if (code == 0) {
                state.statusArea.setText(state.baseStatus + "\nLoaded: " + selected.getName());
                state.playButton.setVisible(true);
                state.pauseButton.setVisible(true);
                state.stopButton.setVisible(true);
                state.isPaused = false;
                state.pauseButton.setText(PAUSE_LABEL);
            } else {
                state.statusArea.setText(state.baseStatus + "\nFailed to load: " + path + " (code " + code + ")");
                state.playButton.setVisible(false);
                state.pauseButton.setVisible(false);
                state.stopButton.setVisible(false);
            }
        } catch (UnsatisfiedLinkError e) {
            state.statusArea.setText(state.baseStatus + "\n" + NATIVE_LIB_ERROR);
        }
    }

    private static void handlePlay(Player state) {
        if (state.selectedFile == null) {
            state.statusArea.setText(state.baseStatus + "\nNo file selected.");
            return;
        }
        try {
            int code = AudioLib.INSTANCE.play_audio(state.selectedFile.getAbsolutePath());
            if (code == 0) {
                state.statusArea.setText(state.baseStatus + "\nPlaying: " + state.selectedFile.getName());
            } else {
                state.statusArea.setText(state.baseStatus + "\nFailed to play file (code " + code + ").");
            }
        } catch (UnsatisfiedLinkError e) {
            state.statusArea.setText(state.baseStatus + "\n" + NATIVE_LIB_ERROR);
        }
    }

    private static void handleStop(Player state) {
        try {
            AudioLib.INSTANCE.stop_audio();
            state.statusArea.setText(state.baseStatus + "\nStopped.");
        } catch (UnsatisfiedLinkError e) {
            state.statusArea.setText(state.baseStatus + "\n" + NATIVE_LIB_ERROR);
        }
    }

    private static void handlePauseToggle(Player state) {
        try {
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
        } catch (UnsatisfiedLinkError e) {
            state.statusArea.setText(state.baseStatus + "\n" + NATIVE_LIB_ERROR);
        }
    }
}
