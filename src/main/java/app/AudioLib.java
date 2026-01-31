package app;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface AudioLib extends Library {
    AudioLib INSTANCE = Native.load("audiolib", AudioLib.class);
    String audiolib_version();
    int load_audio(String path);
    int play_audio(String path);
    void stop_audio();
}
