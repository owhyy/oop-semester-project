#include "audio_lib.h"
#include "miniaudio.h"
#include <mutex>

namespace {
    ma_engine g_engine;
    bool g_engine_init = false;
    ma_sound g_sound;
    bool g_sound_init = false;
    bool g_paused = false;
    std::mutex g_audio_mutex;

    int ensure_engine() {
        if (g_engine_init) {
            return 0;
        }
        ma_result result = ma_engine_init(NULL, &g_engine);
        if (result != MA_SUCCESS) {
            return 10;
        }
        g_engine_init = true;
        return 0;
    }

    void stop_internal() {
        if (g_sound_init) {
            ma_sound_stop(&g_sound);
            ma_sound_uninit(&g_sound);
            g_sound_init = false;
            g_paused = false;
        }
    }
}

extern "C" {

const char* audiolib_version(void) {
    return "audiolib 1.0";
}

int load_audio(const char* path) {
    if (path == nullptr || path[0] == '\0') {
        return 1;
    }

    ma_decoder decoder;
    ma_result result = ma_decoder_init_file(path, NULL, &decoder);
    if (result != MA_SUCCESS) {
        return 2;
    }
    ma_decoder_uninit(&decoder);
    return 0;
}

int play_audio(const char* path) {
    if (path == nullptr || path[0] == '\0') {
        return 1;
    }

    std::lock_guard<std::mutex> lock(g_audio_mutex);
    int engine_code = ensure_engine();
    if (engine_code != 0) {
        return engine_code;
    }

    stop_internal();
    ma_result result = ma_sound_init_from_file(&g_engine, path, MA_SOUND_FLAG_STREAM, NULL, NULL, &g_sound);
    if (result != MA_SUCCESS) {
        return 2;
    }
    g_sound_init = true;
    g_paused = false;

    result = ma_sound_start(&g_sound);
    if (result != MA_SUCCESS) {
        stop_internal();
        return 3;
    }
    return 0;
}

void stop_audio(void) {
    std::lock_guard<std::mutex> lock(g_audio_mutex);
    stop_internal();
}

int pause_audio(void) {
    std::lock_guard<std::mutex> lock(g_audio_mutex);
    if (!g_sound_init) {
        return 1;
    }
    ma_sound_stop(&g_sound);
    g_paused = true;
    return 0;
}

int resume_audio(void) {
    std::lock_guard<std::mutex> lock(g_audio_mutex);
    if (!g_sound_init) {
        return 1;
    }
    if (!g_paused) {
        return 0;
    }
    ma_result result = ma_sound_start(&g_sound);
    if (result != MA_SUCCESS) {
        return 2;
    }
    g_paused = false;
    return 0;
}

double get_duration_seconds(const char* path) {
    if (path == nullptr || path[0] == '\0') {
        return -1.0;
    }

    ma_decoder decoder;
    ma_result result = ma_decoder_init_file(path, NULL, &decoder);
    if (result != MA_SUCCESS) {
        return -1.0;
    }

    ma_uint64 length_frames = 0;
    result = ma_decoder_get_length_in_pcm_frames(&decoder, &length_frames);
    ma_uint32 sample_rate = decoder.outputSampleRate;
    ma_decoder_uninit(&decoder);

    if (result != MA_SUCCESS || sample_rate == 0) {
        return -1.0;
    }

    return static_cast<double>(length_frames) / static_cast<double>(sample_rate);
}

double get_position_seconds(void) {
    std::lock_guard<std::mutex> lock(g_audio_mutex);
    if (!g_sound_init) {
        return -1.0;
    }

    ma_uint64 cursor_frames = 0;
    ma_result result = ma_sound_get_cursor_in_pcm_frames(&g_sound, &cursor_frames);
    if (result != MA_SUCCESS) {
        return -1.0;
    }

    ma_format format;
    ma_uint32 channels;
    ma_uint32 sample_rate;
    result = ma_sound_get_data_format(&g_sound, &format, &channels, &sample_rate, NULL, 0);
    if (result != MA_SUCCESS || sample_rate == 0) {
        return -1.0;
    }

    return static_cast<double>(cursor_frames) / static_cast<double>(sample_rate);
}

int seek_seconds(double seconds) {
    if (seconds < 0) {
        return 1;
    }

    std::lock_guard<std::mutex> lock(g_audio_mutex);
    if (!g_sound_init) {
        return 2;
    }

    ma_format format;
    ma_uint32 channels;
    ma_uint32 sample_rate;
    ma_result result = ma_sound_get_data_format(&g_sound, &format, &channels, &sample_rate, NULL, 0);
    if (result != MA_SUCCESS || sample_rate == 0) {
        return 3;
    }

    ma_uint64 frame = static_cast<ma_uint64>(seconds * static_cast<double>(sample_rate));
    result = ma_sound_seek_to_pcm_frame(&g_sound, frame);
    if (result != MA_SUCCESS) {
        return 4;
    }
    return 0;
}

}
