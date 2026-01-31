package app;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface AudioLib extends Library {
    AudioLib INSTANCE = Native.load("audiolib", AudioLib.class);
    String audiolib_version();
    int load_audio(String path);
    int play_audio(String path);
    void stop_audio();
    int pause_audio();
    int resume_audio();
    double get_duration_seconds(String path);
    double get_position_seconds();
    int seek_seconds(double seconds);
    void set_volume(float volume);
}
