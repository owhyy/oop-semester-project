package app;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Song Editor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            String status = "Song Editor – JNA bridge ready";
            try {
                String version = AudioLib.INSTANCE.audiolib_version();
                status += "\nNative lib: " + version;
            } catch (UnsatisfiedLinkError e) {
                status += "\n(Native lib not loaded – build cpp/ first)";
            }
            JLabel label = new JLabel("<html><center>" + status.replace("\n", "<br>") + "</center></html>", SwingConstants.CENTER);
            frame.add(label);

            frame.setVisible(true);
        });
    }
}
