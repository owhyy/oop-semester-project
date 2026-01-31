#ifndef AUDIO_LIB_H
#define AUDIO_LIB_H

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Returns the library version string.
 * Used by JNA to verify the bridge works.
 */
const char* audiolib_version(void);

/**
 * Attempts to load an audio file from the given path.
 * Returns 0 on success, non-zero on failure.
 */
int load_audio(const char* path);

/**
 * Plays the given audio file.
 * Returns 0 on success, non-zero on failure.
 */
int play_audio(const char* path);

/**
 * Stops current playback if any.
 */
void stop_audio(void);

/**
 * Pauses current playback if any.
 * Returns 0 on success, non-zero on failure.
 */
int pause_audio(void);

/**
 * Resumes paused playback if any.
 * Returns 0 on success, non-zero on failure.
 */
int resume_audio(void);

/**
 * Returns the duration of the audio file in seconds.
 * Returns -1 on failure.
 */
double get_duration_seconds(const char* path);

/**
 * Returns the current playback position in seconds.
 * Returns -1 on failure or if nothing is playing.
 */
double get_position_seconds(void);

/**
 * Seeks to the given position in seconds.
 * Returns 0 on success, non-zero on failure.
 */
int seek_seconds(double seconds);

/**
 * Sets playback volume (0.0 - 1.0).
 */
void set_volume(float volume);

/**
 * Sets playback speed (0.5 - 2.0).
 */
void set_speed(float speed);

#ifdef __cplusplus
}
#endif

#endif /* AUDIO_LIB_H */
