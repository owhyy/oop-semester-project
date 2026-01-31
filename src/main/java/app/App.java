package app;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Song Editor");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            final String baseStatus = "Song Editor";

            JTextArea statusArea = new JTextArea(baseStatus);
            statusArea.setEditable(false);
            statusArea.setLineWrap(true);
            statusArea.setWrapStyleWord(true);

            JButton uploadButton = new JButton("Upload Song");
            uploadButton.addActionListener(event -> {
                JFileChooser chooser = new JFileChooser();
                int result = chooser.showOpenDialog(frame);
                if (result != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File selected = chooser.getSelectedFile();
                String path = selected.getAbsolutePath();
                try {
                    int code = AudioLib.INSTANCE.load_audio(path);
                    if (code == 0) {
                        statusArea.setText(baseStatus + "\nLoaded: " + selected.getName());
                    } else {
                        statusArea.setText(baseStatus + "\nFailed to load: " + path + " (code " + code + ")");
                    }
                } catch (UnsatisfiedLinkError e) {
                    statusArea.setText(baseStatus + "\nNative lib not loaded – build cpp/ first");
                }
            });

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
            top.add(uploadButton);

            frame.setLayout(new BorderLayout());
            frame.add(top, BorderLayout.NORTH);
            frame.add(new JScrollPane(statusArea), BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}
