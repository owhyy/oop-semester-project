package app;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class App {
    private static final String NATIVE_LIB_ERROR = "Native lib not loaded – build cpp/ first";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Song Editor");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            final String baseStatus = "Song Editor";

            JTextArea statusArea = createStatusArea(baseStatus);

            final File[] selectedFile = {null};

            JButton uploadButton = new JButton("Upload Song");
            uploadButton.addActionListener(event ->
                    handleUpload(frame, statusArea, baseStatus, selectedFile)
            );

            JButton playButton = new JButton("Play");
            playButton.addActionListener(event ->
                    handlePlay(statusArea, baseStatus, selectedFile)
            );

            AtomicBoolean isPaused = new AtomicBoolean(false);
            JButton pauseButton = new JButton("Pause");
            pauseButton.addActionListener(event ->
                    handlePauseToggle(statusArea, baseStatus, isPaused, pauseButton)
            );

            JButton stopButton = new JButton("Stop");
            stopButton.addActionListener(event ->
                    handleStop(statusArea, baseStatus)
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

    private static void handleUpload(
            JFrame frame,
            JTextArea statusArea,
            String baseStatus,
            File[] selectedFile
    ) {
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
        selectedFile[0] = selected;
        String path = selected.getAbsolutePath();
        try {
            int code = AudioLib.INSTANCE.load_audio(path);
            if (code == 0) {
                statusArea.setText(baseStatus + "\nLoaded: " + selected.getName());
            } else {
                statusArea.setText(baseStatus + "\nFailed to load: " + path + " (code " + code + ")");
            }
        } catch (UnsatisfiedLinkError e) {
            statusArea.setText(baseStatus + "\n" + NATIVE_LIB_ERROR);
        }
    }

    private static void handlePlay(JTextArea statusArea, String baseStatus, File[] selectedFile) {
        if (selectedFile[0] == null) {
            statusArea.setText(baseStatus + "\nNo file selected.");
            return;
        }
        try {
            int code = AudioLib.INSTANCE.play_audio(selectedFile[0].getAbsolutePath());
            if (code == 0) {
                statusArea.setText(baseStatus + "\nPlaying: " + selectedFile[0].getName());
            } else {
                statusArea.setText(baseStatus + "\nFailed to play file (code " + code + ").");
            }
        } catch (UnsatisfiedLinkError e) {
            statusArea.setText(baseStatus + "\n" + NATIVE_LIB_ERROR);
        }
    }

    private static void handleStop(JTextArea statusArea, String baseStatus) {
        try {
            AudioLib.INSTANCE.stop_audio();
            statusArea.setText(baseStatus + "\nStopped.");
        } catch (UnsatisfiedLinkError e) {
            statusArea.setText(baseStatus + "\n" + NATIVE_LIB_ERROR);
        }
    }

    private static void handlePauseToggle(
            JTextArea statusArea,
            String baseStatus,
            AtomicBoolean isPaused,
            JButton pauseButton
    ) {
        try {
            if (!isPaused.get()) {
                int code = AudioLib.INSTANCE.pause_audio();
                if (code == 0) {
                    isPaused.set(true);
                    pauseButton.setText("Resume");
                    statusArea.setText(baseStatus + "\nPaused.");
                } else {
                    statusArea.setText(baseStatus + "\nNothing to pause.");
                }
            } else {
                int code = AudioLib.INSTANCE.resume_audio();
                if (code == 0) {
                    isPaused.set(false);
                    pauseButton.setText("Pause");
                    statusArea.setText(baseStatus + "\nResumed.");
                } else {
                    statusArea.setText(baseStatus + "\nNothing to resume.");
                }
            }
        } catch (UnsatisfiedLinkError e) {
            statusArea.setText(baseStatus + "\n" + NATIVE_LIB_ERROR);
        }
    }
}
